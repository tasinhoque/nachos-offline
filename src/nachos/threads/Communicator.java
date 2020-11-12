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
        this.waiting = new Lock();
        this.speak = new Condition2(waiting);
        this.listen = new Condition2(waiting);
        this.spoke = false;
    }
    /**
     * Wait for a thread to listen through this communicator, and then transfer
     * <i>word</i> to the listener.
     *
     * <p>
     * Does not return until this thread is paired up with a listening thread.
     * Exactly one listener should receive <i>word</i>.
     *
     * @param	word	the integer to transfer.
     */
    public void speak(int word) {
        this.waiting.acquire();
        while (spoke)
        {
            this.listen.wakeAll();
            this.speak.sleep();
        }

        this.toTransfer = word;
        this.spoke = true;
        this.listen.wakeAll();
        this.speak.sleep();
        this.waiting.release();
    }
    /**
     * Wait for a thread to speak through this communicator, and then return
     * the <i>word</i> that thread passed to <tt>speak()</tt>.
     *
     * @return	the integer transferred.
     */
    public int listen() {
        this.waiting.acquire();
        while (!spoke) {
            this.listen.sleep();
        }
        int transferring = this.toTransfer;
        this.speak.wakeAll();
        spoke = false;
        this.waiting.release();
        return transferring;
    }

    private Lock waiting;
    private Condition2 speak;
    private Condition2 listen;
    private int toTransfer;
    private boolean spoke;

    public static void selfTest() {
        KThread t1 = new KThread(new ComTest(1));
        KThread t2 = new KThread(new ComTest(2));
        KThread t3 = new KThread(new ComTest(3));
        KThread t4 = new KThread(new ComTest(4));
        KThread t5 = new KThread(new ComTest(5));
        t1.fork();
        t2.fork();
        t3.fork();
        t4.fork();
        t5.fork();

        System.out.println("-----Communicator Test---------");
        new ComTest(0).run();
    }
    protected static class ComTest implements Runnable {
        private int comID;
        private static Communicator comm = new Communicator();


        ComTest(int comID) {
            this.comID = comID;
        }
        public void run() {

            if (comID == 0) {
                for (int i = 0; i < 5; i++) {
                    System.out.println("ComTest " + comID + " Speak(" + i + ")");
                    comm.speak(i);
                }
            } else {
                for (int i = 0; i < 5; i++) {
                    System.out.println("ComTest " + comID + " listening to... " + i);
                    int transfered = comm.listen();
                    System.out.println("ComTest " + comID + " heard word " + transfered);
                }
            }
            if (comID == 0)
                System.out.println("-----Communicator Test Complete-------");
            ThreadedKernel.alarm.waitUntil(2000);
        }
    }
}
