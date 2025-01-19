package IO;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
public class MyCompressorOutputStream extends OutputStream{
    private OutputStream out;


    public MyCompressorOutputStream(OutputStream out){
        this.out = out;

    }

    public void write(int b){

    }

    public void write(byte[] b){
        ArrayList<Byte> byteArrayList = new ArrayList<Byte>();
        int index = 0;

        while (index < 12) {
            byteArrayList.add(b[index]);
            index++;
        }

        while (index < b.length) {
            int counter = 0;
            // Temporarily stores a group of 8 bits (1 byte worth of data)
            ArrayList<Byte> byteTemp = new ArrayList<Byte>();
            while (counter < 8 && index < b.length) {// Collect up to 8 bits (or less if we reach the end of the data)
                byteTemp.add(b[index]);
                index++;
                counter++;
            }
            byteArrayList.add(binaryToDecimal(byteTemp));
        }

        try {
            for (int i = 0; i < byteArrayList.size(); i++)
                out.write(byteArrayList.get(i));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    private byte binaryToDecimal(ArrayList<Byte> B){
        int value = 0;
        int index = 0;
        for(int i = B.size() - 1; i >-1 ; i--){
            value = value + (int)Math.pow(2,index) * B.get(i);
            index ++;
        }
        return (byte)value;
    }


    public void compress(){

    }
}
