# Report Template

## Part #num

### Task #num: Heading (copy from the assignment pdf)

- Data Structures Used

  1. `variableRef`: What it is. What it does.

- Steps

  - package.class#method()

    1. Pseudocode step 1.
    2. Pseudocode step 2.

- Testing

- Additional Comment: (if any)

# Report

## Part 1

### Task 1: Implement KThread.join()

- Data Structures Used

  1. `joinQueue`: Private instance of FifoQueue (a subclass of
  ThreadQueue) which maintains a linked list of the threads that have
  called join().

- Steps

  - threads.KThread#join()

    1. If `this` thread is already finished, return immediately.
    2. Record the state of the machine interrupt and disable it.
    3. If `joinQueue` (of `this` thread) is null, initialize it.
    4. Add current thread to the `joinQueue` of `this` thread.
    5. Current thread relinquishes CPU for the next thread in
       readyQueue.
    6. Restore machine interrupt to the previous state.

  - threads.KThread#finish()

    1. Record the state of the machine interrupt and disable it.
    2. Move all the threads that were waiting on current thread to
       ready state.
    3. Schedule the current thread to be destroyed.
    4. Restore machine interrupt to the previous state.

- Testing: We created two threads and forked them. Then, we called
  join on the first thread. This enabled the first thread to finish
  its execution before getting killed.

### Task 2: Implement condition variables directly

- Data Structures Used

  1. `waitQueue`: A LinkedList instance of KThread (used as a FIFO queue)
  2. `conditionLock`: An instance of Lock class.

- Steps

  - threads.Condition2#Condition2()

    1. Initialize `waitQueue`.

  - threads.Condition2#sleep()

    1. Disable machine interrupt.
    2. Release `conditionLock`.
    3. Add the current KThread to the `waitQueue`
    4. Relinquish the CPU for the current thread.
    5. Acquire `conditionLock`.
    6. Restore machine interrupt.

  - threads.Condition2#wake()

    1. Disable machine interrupt.
    2. If the `waitQueue` is not empty, remove the first thread from the front and
       change its state to ready, along with putting it in the `readyQueue` (a static
       `ThreadQueue` in `KThread`).
    3. Restore machine interrupt.

  - threads.Condition2#wakeAll()

    1. Disable machine interrupt.
    2. While the `waitQueue` is not empty, remove the first thread from the front and
       change its state to ready, along with putting it in the `readyQueue` (a static
       `ThreadQueue` in `KThread`).
    3. Restore machine interrupt.

- Testing: We created three threads. Each of them printed a line and went to sleep.
  We then woke a single thread and created another three threads. After that, we
  woke up all the remaining threads.

### Task 3: Complete the implementation of the Alarm class

- Data Structures Used

  1. `WaitThread`: A java inner class. Acts as a member of a priority
     queue which sorts the threads according to their waketime.
  2. `waitQueue`: A priority queue. Holds `WaitThread` objects as
     members.

- Steps

  - threads.Alarm#Alarm()

    1. Set the interrupt handler of `Machine.timer`. The handler, a
       runnable object, calls Alarm#timerInterrupt().

  - threads.Alarm#timerInterrupt()

    1. Record the state of the machine interrupt and disable it.
    2. Wake all the threads in the waitQueue whose waiting period is
       over.
    3. Yield the current thread.
    4. Restore machine interrupt to the previous state.

  - threads.Alarm#WaitUntil()

    1. Calculate the waketime, i.e., the time period for which the
       current thread is to wait.
    2. Record the state of the machine interrupt and disable it.
    3. Add the current thread to the `waitQueue`.
    4. Send the current thread to sleep.
    5. Restore machine interrupt to the previous state.

- Testing: We created 10 threads and called Alarm#waitUntil() on them.

### Task 4: Implement synchronous send and receive of one-word messages

- Data Structures Used

  1. `lock`: An instance of Lock.
  2. `speaker`: An instance of Condition2.
  3. `listener`: An instance of Condition2.
  4. `wordToTransfer`: An integer denoting the word we are communicating.
  5. `spoke`: A boolean to keep track of whether any speaker has spoke (without
      begin listened to).
  6. `CommunicatorTest`: A java class used for testing. It contains a static
     instance of `Communicator` and an integer `communicatorId`.

- Steps

  - threads.Communicator#Communicator()

    1. Initialize `lock`.
    2. Initialize `listener` & `speaker` with `lock`.
    3. Initialize `spoke` with false.

  - threads.Communicator#speak(`word`)

    1. Acquire `lock`.
    2. While someone has spoke some word which is still not heard by any listener,
       wake up all the listeners and put the speaker to sleep.
    3. Set `wordToTransfer` to the parameter `word`.
    4. Set `spoke` to true.
    5. Wake up all listeners.
    6. Put this speaker to sleep.
    7. Release `lock`.

  - threads.Communicator#listen()

    1. Acquire `lock`.
    2. While there is no incoming word, put this listener to sleep.
    3. Store the incoming word `wordToTransfer`.
    4. Wake up all the speakers.
    5. Set `spoke` to false.
    6. Release `lock`.
    7. Return the word.

- Testing: We created five instances of `CommunicatorTest` for the purpose of
  listening. Then, we created another instance of the same class as a speaker and
  executed its speak method for five times. Only one listener could listen to any
  specific word transferred from the speaker.

## Part 2

### Task 1: Implement the system calls read and write documented in syscall.h

- Data Structures Used

  1. `machine.OpenFile` for handling files and streams.

- Steps

  - userprog.UserProcess#handleSyscall()

    1. For syscallRead, call userprog.UserProcess#handleRead() with
       the first three arguments.
    2. For syscallWrite, call userprog.UserProcess#handleWrite() with
       the first three arguments.

  - userprog.UserProcess#handleRead()

    1. Check for validity of arguments passed. If invalid, return
       failure.
    2. Open the file/stream indicated by the file descriptor passed in
       the args.
    3. Read from the opened file/stream into a byte array.
    4. Write the byte array into virtual memory.
    5. Return the size of data read.

  - userprog.UserProcess#handleWrite()

    1. Check for validity of arguments passed. If invalid, return
       failure.
    2. Open the file/stream indicated by the file descriptor passed in
       the args.
    3. Read from virtual memory into a byte array.
    4. Write the byte array into the file/stream.
    5. Return the size of data written.

### Task 2: Implement support for multiprogramming

- Data Structures Used

  1. `allocated`: Instance of LinkedList of TranslationEntry.
  2. `pageTable`: Array of TranslationEntry.

- Steps

  - userprog.UserProcess#readVirtualMemory()

    1. Check if the virtual address parameter `vaddr` is valid.
    2. Loop through each virtual page within the range computed from the parameters
       `vaddr` and `length`.
    3. If the current virtual page is not valid, break.
    4. Take the required block of memory from the current page and write it to
       the array in the parameter called `data`.
    5. Update the total bytes of memory transferred.
    6. Outside the loop, finally return the transferred memory count.

  - userprog.UserProcess#writeVirtualMemory()

    1. Check if the virtual address parameter `vaddr` is valid.
    2. Loop through each virtual page within the range computed from the parameters
       `vaddr` and `length`.
    3. If the current virtual page is not valid, break.
    4. Mark the required block of memory from the current page where we'll write.
       Write `data` to that block.
    5. Update the total bytes of memory transferred.
    6. Outside the loop, finally return the transferred memory count.

  - userprog.UserProcess#loadSections()

    1. If there is insufficient physical memory, close the coff file and return false.
    2. Loop through all the sections in the coff file. For each section, loop
       through all the virtual page numbers. Look up page table using the virtual
       page number. Using the returned physical page number, load a page into
       physical memory.

  - userprog.UserProcess#lookUpPageTable()

    1. If `pageTable` is null, return null.
    2. If the virtual page number parameter is a valid index of `pageTable` then
       return the corresponding entry.
    3. Else, return null.

    - userProg.UserProcess#allocate()

    1. For `desiredPages` (the parameter denoting the number of
       physical pages to allocate) times, loop and allocate a physical
       page.
    2. If the allocation is failed, loop and deallocate all physical
       pages allocated in this loop and return failure.
    3. If the allocation is successful, increment numPages variable.
    4. Return success.

### Task 3:  Implement the system calls (exec, join, and exit)

- Data structures Used

  1. `childrenexitstatus`: A hashmap. Saves the exit status of the
     children.
  2. `StatusLock`: A Lock variable. Handles possible memory
     corruption through threading.
  3. `children`: A LinkedList of user processes spawned by this
     process.

- userprog.UserProcess#handleExec()

  1. Check if the parameters are valid.
  2. Read filename from the virtual memory.
  3. Check if the file is a coff binary.
  4. Read the arguments from virtual memory.
  5. Create a new user process.
  6. Execute the process.
  7. Make this process the parent of the new process.
  8. Make the new process the child of this process.
  9. Attach an unique process ID to the new process.

- userprog.UserProcess#handleJoin()

  1. Check if the parameters are valid.
  2. Check if the process to join is a child of this process.
  3. If not a child, return failure.
  4. If is a child, call join() on the child's UserThread.
  5. When the child process returns, this process continues execution.
  6. Delete the child process from this process's list of children.
  7. Get the exit status of the child process.
  8. If exit status cannot be found, return 0 for unhandled exception.
  9. If exit status found, write the status at the virtual address
     provided in the parameter.

- userprog.UserProcess#handleExit()

  1. If this process has a parent process, update the parent's list of
     exit of its children.
  2. Release any resource allocated for this process.
  3. Remove this process from being its children's parent.
  4. If this process is the root process, call
     Kernel.kernel.terminate()
  5. If not, finish the thread used by this process.

### Task 4: Testing Part 2

We have used mypgr.c file to test the added functionalities in part 2
of our project. We used MIPS to convert mypgr.c into mypgr.coff,
changed the value of `Kernel.shellProgram` in nachos.conf, and ran the
user program.

The program calls two other user programs: echo.coff and halt.coff.

The main process calls exec() and join() on the first process. The
output of the first process is thus properly shown on the console.

The second time, however, the main process calls exec() on two
processes, both halt.coff. The main process calls join() on the first
process, and the output on the console is proper, but, on the second
process, it doesn't call join(), so the output is scrambled, since two
process are simultaneously using the console.
