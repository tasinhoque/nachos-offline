Part 2

- [x] Task 2
  - [x] `UserKernel`
  - [x] `UserProcess`
    - [x] `lookUpPageTable()`
    - [x] `readVirtualMemory()`
    - [x] `writeVirtualMemory()`
    - [x] `loadSections()`
    - [x] `unloadSections()`

### Template

- Steps:

- Changes:

- Data Structures Used: 

- Additional Comment:

- Testing:

## Part 1

### Task 1 (Join)

- Steps:
  1. If `this` thread is already finished, return immediately.
  2. Disable machine interrupt.
  3. If `joinQueue` (of `this` thread) is null, initialize it and give `this` thread
  access to the CPU.
  4. Give access to the current thread.
  5. Relinquish the CPU for the current thread.
  6. Restore machine interrupt.
- Changes:
  1. finish() method of KThread
  2. join() method of KThread
- Data Structures Used: 
  1. `joinQueue`: Instance of FifoQueue (a subclass of ThreadQueue) which maintains
    a linked list of joined threads.
- Additional Comment: After a thread finishes execution, we'll now let the other
threads waiting for joining to finish execution before killing the thread.
- Testing: We created two threads and forked them. Then, we called join on the first
  thread. This enabled the first thread to finish its execution before getting killed.
  
### Task 2 (Condition Variable)

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