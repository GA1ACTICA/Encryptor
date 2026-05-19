/**
 * Project: Encryptor
 *
 * Author: Galactica
 *
 * Licensed under the GPL 3.0 License.
 * See LICENSE file in the project root for full license information.
 *
 * Copyright © 2026 Galactica
 */
package Networking.Threads;

import java.io.PrintWriter;
import java.util.concurrent.BlockingQueue;

public class Sender implements Runnable {
    private PrintWriter writer;
    private BlockingQueue<String> queue;

    public Sender(PrintWriter writer, BlockingQueue<String> queue) {
        this.writer = writer;
        this.queue = queue;
    }

    @Override
    public void run() {
        try {
            while (true) {
                String msg = queue.take();
                writer.println(msg);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
