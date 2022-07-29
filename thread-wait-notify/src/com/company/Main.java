package com.company;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public class Main {

    public static void main(String[] args) throws InterruptedException {

        PacketManager manager = new PacketManager();

        Sender sender = new Sender(manager);
        new Thread(sender).start();

        Receiver receiver = new Receiver(manager);
        new Thread(receiver).start();

        sender.send("First");
        sender.send("Second");
        sender.send("Third");
        sender.send("Four");
        sender.send("Five");
        sender.send("Six");
        sender.send("Seven");
        sender.send("Eight");

        // Wait on Five for five minutes and read others
        Thread.sleep(5000);

        manager.tellThemToReadOthers();

    }

    public static class PacketManager {

        private volatile Queue<String> packets = new LinkedBlockingQueue<>();

        public synchronized void send(String packet) {
            try {
                if (packet.equals("Five"))
                    wait();
                packets.add(packet);
            } catch (Exception e) {
            }
        }

        public synchronized String receive() {
            return packets.poll();
        }

        public synchronized void tellThemToReadOthers() {
            notifyAll();
        }
    }

    public static class Sender implements Runnable {

        private PacketManager packetManager;

        private LinkedBlockingQueue<String> queue = new LinkedBlockingQueue<>();

        public Sender(PacketManager packetManager) {
            this.packetManager = packetManager;
        }

        public void send(String data) {
            queue.add(data);
        }

        @Override
        public void run() {
            while (true) {
                String current = queue.poll();
                if (current != null)
                    packetManager.send(current);
            }
        }
    }

    public static class Receiver implements Runnable {

        private PacketManager packetManager;

        public Receiver(PacketManager packetManager) {
            this.packetManager = packetManager;
        }

        @Override
        public void run() {
            while (true) {
                String current = packetManager.receive();
                if (current != null)
                    System.out.println(current);
            }
        }
    }
}
