import java.io.File;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;

/**
 * Created by uncle on 06.01.2016.
 */
public class CheckGeo {
    public static void main(String[] args) throws Throwable {
        int original, real = 0;
        int flat = 0, complex = 0;
        String fileName = "20_10_conv.dat";
        try(RandomAccessFile raf = new RandomAccessFile(new File("./"+fileName), "r"); FileChannel fc= raf.getChannel()) {
            ByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
            bb.order(ByteOrder.LITTLE_ENDIAN);
            bb.position(6);
            original = bb.getInt();
            bb.position(18);
//real = (bb.limit() - bb.position());
            for(int i = 0; i < 65536; i++) {
                short blocktype = bb.getShort();
//real += 2;
                if(blocktype == 0) {
                    bb.position(bb.position() + 4);
                    flat++;
//real += 2;
                } else if(blocktype == 0x0040) {
                    bb.position(bb.position() + 128);
                    real += 64;
                    complex++;
                } else /*if(blocktype == 0x0048)*/ {
                    for(int j = 0; j < 64; j++) {
                        short layers = bb.getShort();
//real += 2;
                        real += layers;
                        bb.position(bb.position() + (layers << 1));
                    }
                }
            }
            System.out.println(bb.position());
            System.out.println(bb.limit());
        }
        String b1 = String.format("%02X", Integer.parseInt(fileName.substring(0,2)));
        String b2 = String.format("%02X", Integer.parseInt(fileName.substring(3,5)));
        String b6 = String.format("%02X", real & 0xFF);
        String b7 = String.format("%02X", real>>8 & 0xFF);
        String b8 = String.format("%02X", real>>16 & 0xFF);
        String b9 = String.format("%02X", real>>24 & 0xFF);
        String b10 = String.format("%02X", (flat+complex) & 0xFF);
        String b11 = String.format("%02X", (flat+complex)>>8 & 0xFF);
        String b12 = String.format("%02X", (flat+complex)>>16 & 0xFF);
        String b13 = String.format("%02X", (flat+complex)>>24 & 0xFF);
        String b14 = String.format("%02X", (flat) & 0xFF);
        String b15 = String.format("%02X", (flat)>>8 & 0xFF);
        String b16 = String.format("%02X", (flat)>>16 & 0xFF);
        String b17 = String.format("%02X", (flat)>>24 & 0xFF);
        System.out.println(b6+b7+b8);
        System.out.printf("Original \t%X\r\nReal \t\t%X\r\n",original,real);
        System.out.println("\r\nHeader");
        System.out.println("00 01 02 03 04 05 06 07 08 09 0a 0b 0c 0d 0e 0f 10 11");
        System.out.printf("%s %s 80 00 10 00 %s %s %s %s %s %s %s %s %s %s %s %s",b1,b2,b6,b7,b8,b9,b10,b11,b12,b13,b14,b15,b16,b17);

        //System.out.println("");
        //System.out.printf("Flat+Complex \t%X\r\n",flat+complex);
        //System.out.printf("Flat \t%X\r\n",flat);
    }
}
