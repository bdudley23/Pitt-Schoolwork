My implementation of semaphores includes a global list to keep track of all semaphores that have been created.  The global list is implemented using the existing 
linux structures for a doubly linked list and is operated as a FIFO Queue.  Similarly, the list of tasks that is maintained by each semaphore is implemented in the
same manner.  The use of a FIFO Queue, as opposed to a hash table, has a few benefits.  Linux contains the structures and operations necessary for both circular linked
lists and hash tables, but in this case, the linked list has an advantage over the hash table in that it occupies less space in memory because nodes are only created
when needed and deleted after either a task or semaphore is no longer in service.  A hash table would require, at a minimum, the memory alloted to it to create what is
traditionally viewed as a 2D array that may end up being sparsely populated, and so it would be very inefficient in regards to memory consumption and usage.  Second, 
and somewhat compounding on the first point, the use of a hash table would also require a separate structure that would maintain the keys needed to look up each hash,
consuming more memory and resources.  A hash table would have that advantage in lookup time (typically what would be constant time) whereas the FIFO Queue lacks the 
lookup speed in this case - this implementation of a FIFO Queue requires us to look through the global list of semaphores until we find the one we need by matching
the semaphore's ID to the given sem_id argument.  
