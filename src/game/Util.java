package game;

import java.io.DataOutputStream;
import java.io.IOException;

public class Util {

    public static void writeWithThread(DataOutputStream os, String msg) {
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
}
