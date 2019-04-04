    
package game;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class Util {

    public static void writeString(DataOutputStream os, String msg) {
        // start a thread
        new Thread(() -> {
            try {
                os.writeUTF(msg); // write message
                os.flush(); // flush os
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public static void writeString(ObjectOutputStream oos, String msg) {
        new Thread(() -> {
            try {
                oos.writeUTF(msg);
                oos.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public static void writeObject(ObjectOutputStream oos, Object o) {
        new Thread(() -> {
            try {
                oos.writeObject(o);
                System.out.println("obj written");
                oos.flush();
                oos.reset();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    /**
     * A Runnable that serves as a reader that reads and prints
     * continuously from the given input stream. This class is meant
     * to be used for a thread.
     */
    public static class Reader implements Runnable {

        // the input stream to be read from
        private DataInputStream is;

        // most recent message received
        private String message;

        /**
         * Create a new Reader.
         *
         * @param is the data input stream
         */
        public Reader(DataInputStream is) {
            this.is = is;
        }

        @Override
        public synchronized void run() {
            try {
                //noinspection InfiniteLoopStatement
                while (true) {
                    if (is.available() > 0) { // if the stream has more bytes
                        String s = is.readUTF(); // read one token
                        System.out.println(s); // print the token
                    }
                }
            } catch (IOException ioe) {
                System.err.println("Reading Error");
                ioe.printStackTrace();
            }
        }

        public String msg() {
            return message;
        }
    }
}