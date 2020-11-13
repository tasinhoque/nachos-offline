# Report Template

## Part #num

### Task #num: Heading (copy from the assignment pdf)

- Data Structures Used:

  1. `variableRef`: What it is. What it does.

- Steps:

  - package.class#method():

    1. Pseudocode step 1.
    2. Pseudocode step 2.

- Testing:

- Additional Comment: (if any)

# Report

## Part 1

### Task 1: Implement KThread.join()

- Data Structures Used:

  1. `joinQueue`: Private instance of FifoQueue (a subclass of
  ThreadQueue) which maintains a linked list of the threads that have
  called join().

- Steps:

  - threads.KThread#join():
    1. If `this` thread is already finished, return immediately.
    2. Record the state of the machine interrupt and disable it.
    3. If `joinQueue` (of `this` thread) is null, initialize it.
    4. Add current thread to the `joinQueue` of `this` thread.
    5. Current thread relinquishes CPU for the next thread in
       readyQueue.
    6. Restore machine interrupt to the previous state.

  - threads.KThread#finish():
    1. Record the state of the machine interrupt and disable it.
    2. Move all the threads that were waiting on current thread to
       ready state.
    3. Schedule the current thread to be destroyed.
    4. Restore machine interrupt to the previous state.

- Testing: We created two threads and forked them. Then, we called
  join on the first thread. This enabled the first thread to finish
  its execution before getting killed.

### Task 2: Implement condition variables directly

- Steps:
  - Sleep
    1. Disable machine interrupt.
    2. Release `conditionLock`.
    3. Add the current KThread to the `waitQueue`
    4. Relinquish the CPU for the current thread.
    5. Acquire `conditionLock`.
    6. Restore machine interrupt.
  - Wake
    1. Disable machine interrupt.
    2. If the `waitQueue` is not empty, remove the first thread from the front and
    change its state to ready, along with putting it in the `readyQueue` (a static
    `ThreadQueue` in `KThread`).
    3. Restore machine interrupt.
  - Wake All
    1. Disable machine interrupt.
    2. While the `waitQueue` is not empty, remove the first thread from the front and
    change its state to ready, along with putting it in the `readyQueue` (a static
    `ThreadQueue` in `KThread`).
    3. Restore machine interrupt.
- Changes:
  1. Condition2() constructor
  2. sleep()
  3. wake()
  4. wakeAll()
- Data Structures Used:
  1. `waitQueue`: Linked list of KThread (used as a FIFO queue)
  2. `conditionLock`: An instance of Lock class.
- Testing: We forked three threads after creating them.


### Task 3: Complete the implementation of the Alarm class

- Data Structures Used:

  1. `WaitThread`: A java inner class. Acts as a member of a priority
     queue which sorts the threads according to their waketime.
  2. `waitQueue`: A priority queue. Holds `WaitThread` objects as
     members.

- Steps:

  - threads.Alarm#Alarm():

    1. Set the interrupt handler of `Machine.timer`. The handler, a
       runnable object, calls Alarm#timerInterrupt().

  - threads.Alarm#timerInterrupt():

    1. Record the state of the machine interrupt and disable it.
    2. Wake all the threads in the waitQueue whose waiting period is
       over.
    3. Yield the current thread.
    4. Restore machine interrupt to the previous state.

  - threads.Alarm#WaitUntil():

    1. Calculate the waketime, i.e., the time period for which the
       current thread is to wait.
    2. Record the state of the machine interrupt and disable it.
    3. Add the current thread to the `waitQueue`.
    4. Send the current thread to sleep.
    5. Restore machine interrupt to the previous state.

- Testing: We created 10 threads and called Alarm#waitUntil() on them.
