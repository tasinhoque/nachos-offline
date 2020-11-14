package nachos.threads;

import nachos.machine.*;

import java.util.Queue;
import java.util.LinkedList;

/**
 * A <i>communicator</i> allows threads to synchronously exchange 32-bit
 * messages. Multiple threads can be waiting to <i>speak</i>,
 * and multiple threads can be waiting to <i>listen</i>. But there should never
 * be a time when both a speaker and a listener are waiting, because the two
 * threads can be paired off at this point.
 */
public class Communicator {
    /**
     * Allocate a new communicator.
     */
    public Communicator() {
        lock = new Lock();
        speaker = new Condition2(lock);
        listener = new Condition2(lock);
        spoke = false;
    }

    /**
     * Wait for a thread to listen through this communicator, and then transfer
     * <i>word</i> to the listener.
     *
     * <p>
     * Does not return until this thread is paired up with a listening thread.
     * Exactly one listener should receive <i>word</i>.
     *
     * @param    word    the integer to transfer.
     */
    public void speak(int word) {
        lock.acquire();

        while (spoke) {
            listener.wakeAll();
            speaker.sleep();
        }

        wordToTransfer = word;
        spoke = true;
        listener.wakeAll();
        speaker.sleep();
        lock.release();
    }

    /**
     * Wait for a thread to speak through this communicator, and then return
     * the <i>word</i> that thread passed to <tt>speak()</tt>.
     *
     * @return the integer transferred.
     */
    public int listen() {
        lock.acquire();

        while (!spoke) {
            listener.sleep();
        }

        int word = wordToTransfer;

        speaker.wakeAll();
        spoke = false;
        lock.release();

        return word;
    }

    private Lock lock;
    private Condition2 speaker;
    private Condition2 listener;
    private int wordToTransfer;
    private boolean spoke;

    public static void selfTest() {
        KThread t1 = new KThread(new CommunicatorTest(1));
        KThread t2 = new KThread(new CommunicatorTest(2));
        KThread t3 = new KThread(new CommunicatorTest(3));
        KThread t4 = new KThread(new CommunicatorTest(4));
        KThread t5 = new KThread(new CommunicatorTest(5));
        t1.fork();
        t2.fork();
        t3.fork();
        t4.fork();
        t5.fork();

        System.out.println("-----Communicator Test Begin---------");
        new CommunicatorTest(0).run();
    }

    protected static class CommunicatorTest implements Runnable {
        private int communicatorId;
        private static Communicator communicator = new Communicator();

        CommunicatorTest(int comID) {
            communicatorId = comID;
        }

        public void run() {
            if (communicatorId == 0) {
                for (int i = 0; i < 5; i++) {
                    System.out.println("Communicator " + communicatorId + " spoke word " + i);
                    communicator.speak(i);
                }
            } else {
                for (int i = 0; i < 5; i++) {
                    System.out.println("Communicator " + communicatorId + " listening...");
                    int transferred = communicator.listen();
                    System.out.println("Communicator " + communicatorId + " heard word " + transferred);
                }
            }
            if (communicatorId == 0)
                System.out.println("-----Communicator Test End-------");
            ThreadedKernel.alarm.waitUntil(2000);
        }
    }
}
