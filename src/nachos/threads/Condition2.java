package nachos.threads;

import nachos.machine.*;

/**
 * An implementation of condition variables that disables interrupt()s for
 * synchronization.
 *
 * <p>
 * You must implement this.
 *
 * @see    nachos.threads.Condition
 */
public class Condition2 {
    /**
     * Allocate a new condition variable.
     *
     * @param    conditionLock    the lock associated with this condition
     * variable. The current thread must hold this
     * lock whenever it uses <tt>sleep()</tt>,
     * <tt>wake()</tt>, or <tt>wakeAll()</tt>.
     */
    public Condition2(Lock conditionLock) {
        this.conditionLock = conditionLock;
        waitQueue = ThreadedKernel.scheduler.newThreadQueue(false);
    }

    /**
     * Atomically release the associated lock and go to sleep on this condition
     * variable until another thread wakes it using <tt>wake()</tt>. The
     * current thread must hold the associated lock. The thread will
     * automatically reacquire the lock before <tt>sleep()</tt> returns.
     */
    public void sleep() {
        Lib.assertTrue(conditionLock.isHeldByCurrentThread());

        conditionLock.release();

        boolean inStatus = Machine.interrupt().disable();
        waitQueue.waitForAccess(KThread.currentThread());
        KThread.sleep();
        Machine.interrupt().restore(inStatus);

        conditionLock.acquire();
    }

    /**
     * Wake up at most one thread sleeping on this condition variable. The
     * current thread must hold the associated lock.
     */
    public void wake() {
        Lib.assertTrue(conditionLock.isHeldByCurrentThread());

        boolean inStatus = Machine.interrupt().disable();
        KThread thread = waitQueue.nextThread();

        if (thread != null)
            thread.ready();

        Machine.interrupt().restore(inStatus);
    }

    /**
     * Wake up all threads sleeping on this condition variable. The current
     * thread must hold the associated lock.
     */
    public void wakeAll() {
        Lib.assertTrue(conditionLock.isHeldByCurrentThread());

        boolean inStatus = Machine.interrupt().disable();
        while (true) {
            KThread thread = waitQueue.nextThread();
            if (thread == null)
                break;
            thread.ready();
        }
        Machine.interrupt().restore(inStatus);
    }

    public static void selfTest() {
        final Lock lock = new Lock();
        final Condition2 condition = new Condition2(lock);
        int count = 0;

        class RunCondition2 implements Runnable {
            int id = -1;

            RunCondition2(int count) {
                id = count++;
            }

            @Override
            public void run() {
                lock.acquire();
                System.out.println("Thread " + id + " is going to sleep");
                condition.sleep();
                System.out.println("Thread " + id + " has been woken up");
                lock.release();
            }
        }

        System.out.println("Condition TEST #1: Start");

        KThread thread1 = new KThread(new RunCondition2(++count));
        KThread thread2 = new KThread(new RunCondition2(++count));
        KThread thread3 = new KThread(new RunCondition2(++count));

        thread1.fork();
        thread2.fork();
        thread3.fork();

        System.out.println("Main: yielding to run the other thread");
        KThread.yield();
        System.out.println("Main: sending the wake signal (to a single thread) then yeilding");
        lock.acquire();
        condition.wake();
        lock.release();
        KThread.yield();

        System.out.println("Condition TEST #1: End\n");

        System.out.println("Condition TEST #2: Start");

        thread1 = new KThread(new RunCondition2(++count));
        thread2 = new KThread(new RunCondition2(++count));
        thread3 = new KThread(new RunCondition2(++count));

        thread1.fork();
        thread2.fork();
        thread3.fork();

        System.out.println("Main: yielding to run the other thread");
        KThread.yield();
        System.out.println("Main: sending the wake signal (to all threads) then yeilding");
        lock.acquire();
        condition.wakeAll();
        lock.release();
        KThread.yield();

        System.out.println("Condition TEST #2: End");
    }

    private Lock conditionLock;
    private ThreadQueue waitQueue;
}
