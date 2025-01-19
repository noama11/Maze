package IO;

import java.io.IOException;
import java.io.InputStream;

public class SimpleDecompressorInputStream extends InputStream {
    private final InputStream in;

    public SimpleDecompressorInputStream(InputStream in) {
        this.in = in;
    }

    @Override
    public int read() throws IOException {
        try {
            return in.read();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public int read(byte[] buffer) throws IOException {
        int index = 0;
        byte b = 0;
        while (index < 12){
            buffer[index] = (byte) in.read();
            index++;
        }
        while (index < buffer.length) {
            int count = in.read();
            if (count == -1) {
                break;
            }
            for (int i = 0; i < count; i++) {
                buffer[index] = b;
                index++;
            }
            if(b == 1)
                b = 0;
            else
                b = 1;
        }
        if(index == 0)
            return -1;
        return index;
    }

    @Override
    public void close() throws IOException {
        in.close();
    }
}
