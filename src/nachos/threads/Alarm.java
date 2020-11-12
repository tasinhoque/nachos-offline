package nachos.threads;

import nachos.machine.*;

import java.util.PriorityQueue;

/**
 * Uses the hardware timer to provide preemption, and to allow threads to sleep
 * until a certain time.
 */
public class Alarm {
    /**
     * Allocate a new Alarm. Set the machine's timer interrupt handler to this
     * alarm's callback.
     *
     * <p><b>Note</b>: Nachos will not function correctly with more than one
     * alarm.
     */
    public Alarm() {
        Machine.timer().setInterruptHandler(new Runnable() {
            public void run() {
                timerInterrupt();
            }
        });
    }

    /**
     * The timer interrupt handler. This is called by the machine's timer
     * periodically (approximately every 500 clock ticks). Causes the current
     * thread to yield, forcing a context switch if there is another thread
     * that should be run.
     */
    public void timerInterrupt() {
        boolean inStatus=Machine.interrupt().disable();
        while(!waitQueue.isEmpty()
                && waitQueue.peek().wakeTime <= Machine.timer().getTime()) {
            System.out.println("Waking up thread " +  waitQueue.peek().thread.toString()
                    + " at " + Machine.timer().getTime() + " cycles");
            waitQueue.poll().thread.ready();
        }
        KThread.yield();
        Machine.interrupt().restore(inStatus);
    }

    /**
     * Put the current thread to sleep for at least <i>x</i> ticks,
     * waking it up in the timer interrupt handler. The thread must be
     * woken up (placed in the scheduler ready set) during the first timer
     * interrupt where
     *
     * <p><blockquote>
     * (current time) >= (WaitUntil called time)+(x)
     * </blockquote>
     *
     * @param    x    the minimum number of clock ticks to wait.
     * @see    nachos.machine.Timer#getTime()
     */
    // Todo: Part 1, Task 3
    public void waitUntil(long x) {
        // for now, cheat just to get something working (busy waiting is bad)
        long wakeTime = Machine.timer().getTime() + x;

        boolean inStatus = Machine.interrupt().disable();
        System.out.println("At " + (wakeTime - x) + " cycles, sleeping thread "
                + KThread.currentThread().toString() + " until " + wakeTime + " cycles");
        waitQueue.add(new WaitThread(KThread.currentThread(), wakeTime));
        KThread.sleep();
        Machine.interrupt().restore(inStatus);
    }

    private class WaitThread implements Comparable<WaitThread> {
        KThread thread;
        long wakeTime;

        public WaitThread(KThread thread, long wakeTime){
            this.thread = thread;
            this.wakeTime = wakeTime;
        }

        public int compareTo(WaitThread thread){
            return Long.compare(this.wakeTime, thread.wakeTime);

        }
    }

    private PriorityQueue<WaitThread> waitQueue = new PriorityQueue<>();
    private static final char dbgAlarm = 'w';

    public static void selfTest() {
        class RunAlarm implements Runnable {
            @Override
            public void run() {
                long tick = (int)(Math.random() * 1000) + 500;
                ThreadedKernel.alarm.waitUntil(tick);
            }
        }

        Lib.debug(dbgAlarm, "Enter Alarm.selfTest");

        RunAlarm runAlarm = new RunAlarm();

        for (int i = 0; i < 10; i++) {
            KThread thread = new KThread(runAlarm).setName("Alarm-" + i);
            thread.fork();
        }

        Lib.debug(dbgAlarm, "Exit Alarm.selfTest");

    }
}
