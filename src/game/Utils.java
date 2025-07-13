package game;


import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class Utils {

    //
    // CurrentThread
    //
    public static class CurrentThread {
        public static void sleep(long ms) {
            try {
                Thread.sleep(ms);
            }catch(Exception e) {
                e.printStackTrace();
            };
        }
    }

    //
    // Log
    //
    public static class Log {
        public static void print(String str) {
            System.out.print(str);
        }
    }


    //
    // FileIO
    //
    public static class FileIO {
        public static BufferedImage loadImage(String path) {
            BufferedImage img = null;

            try {
                img = ImageIO.read(new File(path));
            }catch(Exception e) {
                e.printStackTrace();
            }

            return img;
        }

        public static Font loadFont(String path, float size) {
            Font font = null;
            try {
                font = Font.createFont(Font.TRUETYPE_FONT, new File(path));
                font = font.deriveFont(size);
            }catch (Exception e) {
                e.printStackTrace();
            }

            return font;
        }
    }

    //
    // ByteBuff
    //
    public static class ByteBuff {
        private final int SIZE_OF_INT = 4;

        private byte[] buff;
        private int buff_size;
        private int buff_at;

        public byte[] getBytes() { return buff; }
        public int getSize()     { return buff_size; }
        public void reset()      { buff_at = 0; }

        public ByteBuff(int size) {
            buff = new byte[size];
            buff_at = 0;
            buff_size = size;
        }

        public ByteBuff(byte[] readBuff, int size) {
            buff = readBuff;
            buff_at = 0;
            buff_size = size;
        }

        public void writeInt(int val) {
            assert(buff_at + SIZE_OF_INT <= buff_size);

            long bigVal = val;
            buff[buff_at]     = (byte)((bigVal >> 0)  & 0xFF);
            buff[buff_at + 1] = (byte)((bigVal >> 8)  & 0xFF);
            buff[buff_at + 2] = (byte)((bigVal >> 16) & 0xFF);
            buff[buff_at + 3] = (byte)((bigVal >> 24) & 0xFF);

            buff_at += SIZE_OF_INT;
        }

        public int readInt() {
            assert(buff_at + SIZE_OF_INT <= buff_size);

            int v0 = buff[buff_at];
            if(v0 < 0) v0 += 256;

            int v1 = buff[buff_at + 1];
            if(v1 < 0) v1 += 256;

            int v2 = buff[buff_at + 2];
            if(v2 < 0) v2 += 256;

            int v3 = buff[buff_at + 3];
            if(v3 < 0) v3 += 256;

            buff_at += SIZE_OF_INT;

            int v = (v0 | (v1 << 8) | (v2 << 16) | (v3 << 24));
            return v;
        }

        public void writeString(String str) {
            char[] cstr = str.toCharArray();
            writeInt(cstr.length); // @note: This write can also fail
            assert(buff_at + cstr.length <= buff_size);

            for(char c : cstr) {
                buff[buff_at] = (byte)c;
                buff_at += 1;
            }
        }

        public String readString() {
            int len = readInt(); //@note: This read can also fail
            assert(buff_at + len <= buff_size);

            String str = "";
            for(int i = 0; i < len; i += 1) {
                str += (char)buff[buff_at];
                buff_at += 1;
            }

            return str;
        }
    }

    //
    // FSM
    //
    public static class FSM {
        public String  currState;
        public boolean stateEntered;

        public FSM()                     { currState = "none"; }
        public FSM(String startingState) { currState = startingState; }

        public boolean enter() {
            boolean result = !stateEntered;
            stateEntered = true;

            return result;
        }

        public void set(String newState) {
            currState = newState;
            stateEntered = false;
        }

        public boolean check(String state)  { return(currState.equals(state)); }

        public void invalidState() { assert(false); }
    }

    //
    // Timer
    //
    public static class Timer {
        long startTimeNano;
        double targetTime;

        public void start(double target) {
            startTimeNano = System.nanoTime();
            targetTime = target;
        }

        public void start() {
            startTimeNano = System.nanoTime();
            targetTime = Double.POSITIVE_INFINITY;
        }

        public void restart() {
            startTimeNano = System.nanoTime();
        }

        public double getTime() {
            long timeElapsedNano = System.nanoTime() - startTimeNano;
            double time = (double)timeElapsedNano/1_000_000_000.0; // nanoseconds to just seconds
            return time;
        }

        public boolean hasEnded() {
            return(getTime() > targetTime);
        }



    }
}
