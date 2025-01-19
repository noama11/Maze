package IO;

import java.io.*;
import java.util.*;

public class AdvancedDecompressorInputStream extends InputStream {
    private final InputStream in;
    private static final int METADATA_SIZE = 12;
    private static final int BLOCK_SIZE = 16;
    private byte[] buffer;  // Holds decompressed data
    private int bufferPos;  // Current position in buffer
    private int bufferSize; // Amount of valid data in buffer

    public AdvancedDecompressorInputStream(InputStream in) {
        this.in = in;
        this.buffer = new byte[1024];  // Initial buffer size
        this.bufferPos = 0;
        this.bufferSize = 0;
    }

    @Override
    public int read() throws IOException {
        // If buffer is empty or we've read everything in it, try to fill it
        if (bufferPos >= bufferSize) {
            fillBuffer();
            if (bufferSize == 0) {
                return -1;  // End of stream
            }
        }
        return buffer[bufferPos++] & 0xFF;  // Return next byte from buffer
    }

    @Override
    public int read(byte[] result) throws IOException {
        if (result == null) {
            throw new NullPointerException();
        }

        // First, read and copy metadata directly
        for (int i = 0; i < METADATA_SIZE; i++) {
            int value = in.read();
            if (value == -1) {
                return -1;  // End of stream reached prematurely
            }
            result[i] = (byte) value;
        }

        // Read pattern dictionary
        Map<Byte, byte[]> patterns = readPatternDictionary();

        // Decompress the maze data
        int bytesRead = decompressMazeData(result, METADATA_SIZE, patterns);

        // Return total number of bytes read
        return bytesRead + METADATA_SIZE;
    }

    private int decompressMazeData(byte[] result, int startPos,
                                   Map<Byte, byte[]> patterns) throws IOException {
        int currentPos = startPos;
        int totalBytesRead = 0;

        while (currentPos < result.length) {
            int nextByte = in.read();
            if (nextByte == -1) break;  // End of stream
            totalBytesRead++;

            if (nextByte == 0xFF) {
                // Pattern reference found
                byte patternId = (byte) in.read();
                totalBytesRead++;

                byte[] pattern = patterns.get(patternId);
                if (pattern != null) {
                    int copyLength = Math.min(pattern.length, result.length - currentPos);
                    System.arraycopy(pattern, 0, result, currentPos, copyLength);
                    currentPos += BLOCK_SIZE;
                }
            } else {
                // Run-length encoded sequence
                int count = nextByte;
                if ((count & 0x80) != 0) {
                    // Handle variable-length number
                    count = readVariableLengthNumber(count);
                    totalBytesRead++;
                }

                // Read the value to repeat
                int value = in.read();
                if (value == -1) break;
                totalBytesRead++;

                int fillLength = Math.min(count, result.length - currentPos);
                Arrays.fill(result, currentPos, currentPos + fillLength, (byte)value);
                currentPos += count;
            }
        }

        return totalBytesRead;
    }
    private Map<Byte, byte[]> readPatternDictionary() throws IOException {
        // Read number of patterns using variable-length encoding
        int numPatterns = readVariableLengthNumber();

        // Create dictionary storing pattern ID -> pattern bytes
        Map<Byte, byte[]> patterns = new HashMap<>();

        // Read each pattern
        for (int i = 0; i < numPatterns; i++) {
            byte patternId = (byte) in.read();
            byte[] patternData = new byte[BLOCK_SIZE];
            in.read(patternData);  // Read pattern bytes
            patterns.put(patternId, patternData);
        }

        return patterns;
    }


    private int readVariableLengthNumber() throws IOException {
        int result = 0;
        int shift = 0;
        int b;

        do {
            b = in.read();
            if (b == -1) {
                throw new EOFException("Unexpected end of stream");
            }

            result |= (b & 0x7F) << shift;
            shift += 7;
        } while ((b & 0x80) != 0);

        return result;
    }

    private int readVariableLengthNumber(int firstByte) throws IOException {
        int result = firstByte & 0x7F;
        int shift = 7;
        int b;

        while ((firstByte & 0x80) != 0) {
            b = in.read();
            if (b == -1) {
                throw new EOFException("Unexpected end of stream");
            }

            result |= (b & 0x7F) << shift;
            shift += 7;
            firstByte = b;
        }

        return result;
    }

    private void fillBuffer() throws IOException {
        bufferPos = 0;
        bufferSize = in.read(buffer);
    }

    @Override
    public void close() throws IOException {
        in.close();
    }
}