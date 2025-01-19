package IO;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

public class SimpleCompressorOutputStream extends OutputStream {
    private final OutputStream out;
    private static final int METADATA_SIZE = 12; //The first 12 bytes of input contain metadata
    private static final int MAX_BYTE_VALUE = 255;

    public SimpleCompressorOutputStream(OutputStream out){
        this.out = out;

    }

    public void write(int b){

    }

    public void write(byte[] bytes) throws IOException {
        // ArrayList to store the compressed data temporarily
        ArrayList<Byte> byteArrayList = new ArrayList<Byte>();
        int index = 0;
        if (bytes.length == 0) {
            return;
        }

        // Preserve metadata (first 12 bytes)
        while (index < 12) {
            byteArrayList.add(bytes[index]);
            index++;
        }
        // Compress maze data using run-length encoding
        byte currentByte = bytes[12];
        int count = 1; // Counter for consecutive identical values
        for (int i = 13; i < bytes.length; i++) {
            if (bytes[i] == currentByte) {
                count++;
            } else {
                // When value changes:
                // 1. Update to new value
                currentByte = bytes[i];
                // 2. Store count of previous value run
                byteArrayList.add((byte) count);
                // 3. Reset counter for new value
                count = 1;
            }
        }
        // Write compressed data to output stream
        try {
            for (int i = 0; i < byteArrayList.size(); i++) {
                int b = byteArrayList.get(i);
                // Handle counts larger than 255 by breaking them into chunks
                while (b > 255) {
                    out.write(255);// Write maximum byte value
                    out.write(0); // Write continuation marker
                    b = b - 255; // Calculate remaining count
                }
                out.write(b);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void close() throws IOException {
        out.close();
    }

}
