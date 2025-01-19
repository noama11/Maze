package IO;

import java.io.*;
import java.util.*;

public class AdvancedCompressorOutputStream extends OutputStream {
    // Constants that define how our compression works
    private final OutputStream out;
    private static final int METADATA_SIZE = 12;  // First 12 bytes for maze properties
    private static final int BLOCK_SIZE = 16;     // Size of pattern blocks we'll look for

    // Constructor - initializes our compression stream
    public AdvancedCompressorOutputStream(OutputStream out) {
        this.out = out;
    }

    // Required override for OutputStream, handles single byte writes
    @Override
    public void write(int b) throws IOException {
        out.write(b);
    }

    // Main compression method - this is where the magic happens!
    @Override
    public void write(byte[] input) throws IOException {
        if (input.length == 0) return;

        /* Step 1: Handle Metadata
           The first 12 bytes contain crucial maze information:
           - Bytes 0-1: Width
           - Bytes 2-3: Height
           - Bytes 4-7: Start position
           - Bytes 8-11: End position */
        writeMetadata(input);

        // Get just the maze data, ignoring metadata
        byte[] mazeData = Arrays.copyOfRange(input, METADATA_SIZE, input.length);

        /* Step 2: Pattern Analysis
           Look for repeating patterns in the maze structure.
           Common patterns might be:
           - Long walls (sequences of 1's)
           - Corridors (alternating 1's and 0's)
           - Open spaces (sequences of 0's) */
        Map<String, Integer> patterns = findPatterns(mazeData);

        // Write our pattern dictionary so we can reference it during decompression
        writePatternDictionary(patterns);

        // Finally, compress the maze using our patterns
        compressMazeData(mazeData, patterns);
    }

    // Handles saving the maze's basic properties
    private void writeMetadata(byte[] input) throws IOException {
        for (int i = 0; i < METADATA_SIZE; i++) {
            out.write(input[i]);
        }
    }

    /* Pattern detection method
       Example: If we find a sequence like:
       1,1,1,1,0,0,0,0 appearing multiple times,
       we'll add it to our pattern dictionary */
    private Map<String, Integer> findPatterns(byte[] data) {
        Map<String, Integer> patternFreq = new HashMap<>();

        // Look at each possible block of BLOCK_SIZE bytes
        for (int i = 0; i <= data.length - BLOCK_SIZE; i += BLOCK_SIZE) {
            byte[] block = Arrays.copyOfRange(data, i, i + BLOCK_SIZE);
            String pattern = Arrays.toString(block);
            // Count how many times we see each pattern
            patternFreq.merge(pattern, 1, Integer::sum);
        }

        // Only keep patterns that appear more than twice (space efficient)
        return patternFreq.entrySet().stream()
                .filter(e -> e.getValue() > 2)
                .collect(HashMap::new,
                        (m, e) -> m.put(e.getKey(), e.getValue()),
                        HashMap::putAll);
    }

    /* Dictionary writing method
       Format: [number of patterns][pattern1][pattern2]...
       Each pattern: [ID][pattern bytes] */
    private void writePatternDictionary(Map<String, Integer> patterns) throws IOException {
        writeVariableLengthNumber(patterns.size());

        byte patternId = 0;
        for (String pattern : patterns.keySet()) {
            byte[] patternBytes = stringToByteArray(pattern);
            out.write(patternId++);
            out.write(patternBytes);
        }
    }

    /* Main compression method
       Uses two techniques:
       1. Pattern matching: References common patterns we found
       2. Run-Length Encoding: For unique sequences */
    private void compressMazeData(byte[] data, Map<String, Integer> patterns) throws IOException {
        int i = 0;
        while (i < data.length) {
            // First, try to match a known pattern
            String matchedPattern = findMatchingPattern(data, i, patterns);

            if (matchedPattern != null) {
                // If we found a pattern, write a reference to it
                // Format: [0xFF][pattern ID]
                out.write(0xFF);  // Special marker indicating pattern reference
                out.write(getPatternId(matchedPattern, patterns));
                i += BLOCK_SIZE;
            } else {
                // If no pattern, use run-length encoding
                int count = countConsecutive(data, i);
                writeVariableLengthNumber(count);
                out.write(data[i]);
                i += count;
            }
        }
    }

    /* Efficient number encoding
       Example: Number 130 becomes two bytes: [130|10000010][1|00000001]
       This saves space for small numbers while handling large ones */
    private void writeVariableLengthNumber(int number) throws IOException {
        while (number >= 128) {
            out.write((number & 0x7F) | 0x80);  // Write 7 bits + continuation bit
            number >>>= 7;  // Shift to next 7 bits
        }
        out.write(number);  // Write final byte
    }


    // Helper methods for handling patterns and pattern matching

    /* Converts a string representation of a byte array back to actual bytes
       Example: "[1,1,1,1,0,0,0,0]" -> byte[] {1,1,1,1,0,0,0,0} */
    private byte[] stringToByteArray(String pattern) {
        // First, remove the square brackets from the string
        String content = pattern.substring(1, pattern.length() - 1);

        // Create array to hold our bytes
        byte[] result = new byte[BLOCK_SIZE];

        // Split the string by commas and process each number
        String[] numbers = content.split(",");

        // Convert each string number to a byte and store in our array
        for (int i = 0; i < numbers.length && i < BLOCK_SIZE; i++) {
            result[i] = Byte.parseByte(numbers[i].trim());
        }

        return result;
    }

    /* Looks for a matching pattern at the current position in the data
       Returns the pattern string if found, null if no match */
    private String findMatchingPattern(byte[] data, int startIndex,
                                       Map<String, Integer> patterns) {
        // Make sure we have enough data left to check for a pattern
        if (startIndex + BLOCK_SIZE > data.length) {
            return null;
        }

        // Extract the block of data we want to check
        byte[] currentBlock = Arrays.copyOfRange(data, startIndex,
                startIndex + BLOCK_SIZE);

        // Convert the block to a string format that matches our pattern keys
        String blockString = Arrays.toString(currentBlock);

        // Check if this block matches any of our patterns
        return patterns.containsKey(blockString) ? blockString : null;
    }

    /* Gets the ID assigned to a pattern in our dictionary
       This is used when writing pattern references */
    private byte getPatternId(String pattern, Map<String, Integer> patterns) {
        // Convert patterns keyset to a list so we can get index
        List<String> patternList = new ArrayList<>(patterns.keySet());
        return (byte) patternList.indexOf(pattern);
    }

    /* Extends the countConsecutive method with more robust checking */
    private int countConsecutive(byte[] data, int start) {
        if (start >= data.length) {
            return 0;
        }

        int count = 1;
        byte value = data[start];

        // Count consecutive occurrences of the same value
        while (start + count < data.length &&
                data[start + count] == value &&
                count < 255) {  // Cap at 255 for byte storage
            count++;
        }

        return count;
    }

    /* Checks if the next block of data is worth storing as a pattern
       Used to optimize pattern detection */
    private boolean isWorthyPattern(byte[] block) {
        // A pattern might be worthy if:
        // 1. It has some repetition within itself
        // 2. It's not all zeros or all ones

        boolean hasZero = false;
        boolean hasOne = false;

        for (byte b : block) {
            if (b == 0) hasZero = true;
            if (b == 1) hasOne = true;

            // If we found both values, this might be an interesting pattern
            if (hasZero && hasOne) return true;
        }

        // If we only found one value, it's better handled by RLE
        return false;
    }
}