package org.example;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

public class BlockingQueue<T> {
    private final Queue<T> queue = new LinkedList<>();
    private final int capacity;

    public BlockingQueue(int capacity) {
        this.capacity = capacity;
    }

    public synchronized void put(T element) throws InterruptedException {
        // wait until there’s space
        while (queue.size() == capacity) {
            wait();
        }
        queue.add(element);
        // notify both producers and consumers
        notifyAll();
    }

    public synchronized T take() throws InterruptedException {
        // wait until there’s something to take
        while (queue.isEmpty()) {
            wait();
        }
        T item = queue.remove();
        // notify both producers and consumers
        notifyAll();
        return item;
    }

    public static void main(String[] args) {
        BlockingQueue<Integer> queue = new BlockingQueue<>(2);

        // Producer: generates a random number every 3s and puts it into the queue
        Runnable producer = () -> {
            Random rnd = new Random();
            try {
                while (true) {
                    int value = rnd.nextInt(1000);
                    queue.put(value);
                    System.out.println(Thread.currentThread().getName() + " produced: " + value);
                    Thread.sleep(3000);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("Producer interrupted");
            }
        };

        // Consumer: takes from the queue as soon as an item is available
        Runnable consumer = () -> {
            try {
                while (true) {
                    Integer value = queue.take();
                    System.out.println(Thread.currentThread().getName() + " consumed: " + value);
                    // simulate processing time
                    Thread.sleep(1000);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("Consumer interrupted");
            }
        };

        Thread prodThread = new Thread(producer, "Producer");
        Thread consThread = new Thread(consumer, "Consumer");

        prodThread.start();
        consThread.start();
    }
}
