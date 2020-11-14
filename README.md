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

- Data Structures Used:

  1. `waitQueue`: A LinkedList instance of KThread (used as a FIFO queue)
  2. `conditionLock`: An instance of Lock class.

- Steps:

  - threads.Condition2#Condition2():

    1. Initialize `waitQueue`.

  - threads.Condition2#sleep():

    1. Disable machine interrupt.
    2. Release `conditionLock`.
    3. Add the current KThread to the `waitQueue`
    4. Relinquish the CPU for the current thread.
    5. Acquire `conditionLock`.
    6. Restore machine interrupt.

  - threads.Condition2#wake():

    1. Disable machine interrupt.
    2. If the `waitQueue` is not empty, remove the first thread from the front and
       change its state to ready, along with putting it in the `readyQueue` (a static
       `ThreadQueue` in `KThread`).
    3. Restore machine interrupt.

  - threads.Condition2#wakeAll():

    1. Disable machine interrupt.
    2. While the `waitQueue` is not empty, remove the first thread from the front and
       change its state to ready, along with putting it in the `readyQueue` (a static
       `ThreadQueue` in `KThread`).
    3. Restore machine interrupt.

- Testing: We created three threads. Each of them printed a line and went to sleep.
  We then woke a single thread and created another three threads. After that, we
  woke up all the remaining threads.

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

### Task 4: Implement synchronous send and receive of one-word messages

- Data Structures Used:

  1. `lock`: An instance of Lock.
  2. `speaker`: An instance of Condition2.
  3. `listener`: An instance of Condition2.
  4. `wordToTransfer`: An integer denoting the word we are communicating.
  5. `spoke`: A boolean to keep track of whether any speaker has spoke (without
      begin listened to).
  6. `CommunicatorTest`: A java class used for testing. It contains a static
     instance of `Communicator` and an integer `communicatorId`.


- Steps:

  - threads.Communicator#Communicator():

    1. Initialize `lock`.
    2. Initialize `listener` & `speaker` with `lock`.
    3. Initialize `spoke` with false.

  - threads.Communicator#speak(`word`):
    1. Acquire `lock`.
    2. While someone has spoke some word which is still not heard by any listener,
       wake up all the listeners and put the speaker to sleep.
    3. Set `wordToTransfer` to the parameter `word`.
    4. Set `spoke` to true.
    5. Wake up all listeners.
    6. Put this speaker to sleep.
    7. Release `lock`.

  - threads.Communicator#listen():
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

- Data Structures Used:

  1. `machine.OpenFile` for handling files and streams.

- Steps:

  - userprog.UserProcess#handleSyscall():

    1. For syscallRead, call userprog.UserProcess#handleRead() with
       the first three arguments.
    2. For syscallWrite, call userprog.UserProcess#handleWrite() with
       the first three arguments.

  - userprog.UserProcess#handleRead():

    1. Check for validity of arguments passed. If invalid, return
       failure.
    2. Open the file/stream indicated by the file descriptor passed in
       the args.
    3. Read from the opened file/stream into a byte array.
    4. Write the byte array into virtual memory.
    5. Return the size of data read.

  - userprog.UserProcess#handleWrite():

    1. Check for validity of arguments passed. If invalid, return
       failure.
    2. Open the file/stream indicated by the file descriptor passed in
       the args.
    3. Read from virtual memory into a byte array.
    4. Write the byte array into the file/stream.
    5. Return the size of data written.

### Task 2: Implement support for multiprogramming

- Data Structures Used:

  1. `allocated`: Instance of LinkedList of TranslationEntry.
  2. `pageTable`: Array of TranslationEntry.

- Steps: 

  - userprog.UserProcess#readVirtualMemory():

    1. Check if the virtual address parameter `vaddr` is valid.
    2. Loop through each virtual page within the range computed from the parameters
       `vaddr` and `length`.  
    3. If the current virtual page is not valid, break.
    4. Take the required block of memory from the current page and write it to
       the array in the parameter called `data`.
    5. Update the total bytes of memory transferred.
    6. Outside the loop, finally return the transferred memory count.

  - userprog.UserProcess#writeVirtualMemory():

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
    
### Task 3:  Implement the system calls (exec, join, and exit, also documented in syscall.h).

- Data structures Used:
	1.	`ChildrenExitStatus`: A HashMap where every child's exit status is saved.
	2.	`StatusLock`: A lock variable.
	3.	`children`: A linkList of user processes.
	4.  `childProcessStatus`: A hashmap for ever process for its children status.
	

- userprog.UserProcess#handleExec():

	We passed three parameters here. 
    - the Memory address of the file
    - number of argument
    - the memory address for fetching the arguments

	1. For handling the execution, first it gets the filename from virtualMemory
		that is to be executed.
	2. Next, fetches all the arguments from the virtualMemory.
	3. Create a new UserProcess thread .
	4. Execute the newly created UserProcess thread by passing the filename and arguments.
	5. Make the current thread as the parent thread of this newly created thread
 		and add this new thread to be the children list.
	6.	Return the id of the newly created thread.

- userprog.UserProcess#handleJoin():
	
	handleJoin() receives two parameters. 
  - the process for  joining 
  - the memory address where it's exitStatus will be saved

	1. Current process  searches through it's children table for the process passed as argument
	2. If the child is found, the join  function is called upon from thread
	3. After that, we remove the child from its parent's children list
	4. We fetch Child's ExitStatus from  the ChildrenExitStatus 
	5. We write the child's ExitStatus to the  virtual memory address that was passed as function
		argument
		
	
- userprog.UserProcess#handleExit():
	
	handleExit() function receives a parameter of exitStatus of the  process that is to be 
	passed to its parent process.

	1. If the parent process is not null, we put the status of this thread in the 
		childProcessStatus hashmap of its parent.
	2. Release any resource allotted by loadSection.
	3. Remove it's child processes and set their parent to be null
	4. If the process is root, we terminate it by terminating kernel
		else  we finish the thread and return. 
