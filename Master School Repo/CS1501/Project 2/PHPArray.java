/* ASSIGNMENT 2
** DR. KHATTAB, CS1501
** Author:          Brent Dudley
** Pitt ID:         btd27
** Submitted:       Oct 18, 2019
*/


import java.util.ArrayList;
import java.util.Iterator;

public class PHPArray<V> implements Iterable<V> {

	private Node<V> head;
    private Node<V> tail;
	private PHPArray.Node<V>[] hashTable;
	private int length;
	private int hashNum;
	private Node<V> eachNode;

	@SuppressWarnings("unchecked")
    
	public PHPArray(int capacity) {
		hashTable = (Node<V>[]) new Node<?>[capacity];
		length = 0;
		hashNum = capacity;
		eachNode = head;
	}
    
	/*
    ** Put the key and data into the hash table and linkedlist. If the key exists the
    ** value associated with it is changed to the data argument 
    ** @param s - the key of the pair
    ** @param data - data of the pair
    */
	public void put(String s, V data) {
        
		if (s.equals("") || data == null) {
            // Return if input is void
			return;
        }
        
        // Create a node and put it into the linked list
		Node<V> n = putNode(s, data);
		
		if (length >= hashTable.length/2) {
            // Resize the hash table if over half full
			resizeHashTable(2);      
        }
        
		int index = getLocation(n, hashTable);
        
        // Update value associated with key in the linked list
		if (hashTable[index] != null) { 
			hashTable[index].value = data;
            
            // delete the last entry in the linkedlist since it is no longer needed
			tail = tail.previous; 
			tail.next = null;
			length--;
		} else {
            // Put into the hash table
			hashTable[index] = n; 
        }
		length++;
	}

    /*
    ** Same as the other put method but key is input as a int and converted to string
    ** @param s
    ** @param data
    */
	public void put(int s, V data) {
		put(Integer.toString(s), data);
	}

    /*
    ** Resizes the hash table by doubling its size
    ** Rehashes the nodes into the new table
    */
	private void resizeHashTable(int n) {
		System.out.println("Size: " + length + 
                           " -- resizing array from " + hashNum + " to " + hashNum * n);
        // Doubles the size of table
		hashNum *= n; 
        
		@SuppressWarnings("unchecked")
        
        // Temporary node array
		PHPArray.Node<V>[] temp = (Node<V>[]) new Node<?>[hashNum]; 
		for (int i = 0; i < hashTable.length; i++) {
			if (hashTable[i] != null)
                // Rehashes the nodes
				temp[getLocation(hashTable[i], temp)] = hashTable[i];
		}
		hashTable = temp;
	}

    /*
    ** Finds out where the node is supposed to go in the hash table 
    ** Utilizes linear probing
    ** @param n - node that goes into the table
    ** @param table - table the node will be put in
    ** @return -  index in the table to be placed
    */
	private int getLocation(Node<V> n, Node<V>[] table) {
        
		int index = (n.key.hashCode() & 0x7fffffff)% table.length;
        
		while (table[index] != null) {
			if (table[index].key == n.key) {
				return index;
            }
			index = (index + 1) % table.length;
		}
		return index;
	}

    /*
    ** Create a node and put it into the linkedlist
    ** @param s
    ** @param data
    ** @return new node in the list
    */
	private Node<V> putNode(String s, V data) {
		Node<V> n = new Node<V>(s, data);
        
        // Special case of list being empty
		if (head == null) { 
			head = n;
			tail = n;
			eachNode = head;
        // Normal case of tail insertion
		} else { 
			tail.next = n;
			n.previous = tail;
			tail = n;
		}
		return n;
	}

    /*
    ** Get value associated with argument key by linear probing
    ** @param s - key to search for
    ** @return - value of key, null if key does not exist
    */
	public V get(String s) {
        
		int index = (s.hashCode()& 0x7fffffff) % hashNum;
        
		while (hashTable[index] != null) {
            // The node at index has the same key as the argument
			if (hashTable[index].key.equals(s)) {
				return hashTable[index].value;
            } else {
				index++;
            }
            
			if (index >= hashTable.length) {
                // Wrap around to the front
				index = 0;
            }
		}
        
		return null;
	}

    /*
    ** Same as above get method with key as an int converted to a string
    ** @param s - (int) key
    ** @return - value of key, null if key does not exists
    */
	public V get(int s) {
		return get(Integer.toString(s));
	}

    /*
    ** Deletes the node with the argument key. If key isn't found it does nothing.
    ** Rehashes any keys clustered after the deleted key.
    ** @param s - the key of key, value pair to delete
    */
	public void unset(String s) {
        
        // If key isn't in the table do nothing
		if (get(s) == null)	{
			return;		
        }
        
        // Start looking for the key at this index
		int index = (s.hashCode()& 0x7fffffff) % hashNum;
        
        // Iterate through the table looking for the matching key
		while (!hashTable[index].key.equals(s))	{
			index = (index + 1) % hashNum;
        }
        
        // Delete the node holding the key,value pair
		deleteNode(index);
        
        // Remove the reference to the node in the hash table
		hashTable[index]=null;
		length--;											

		index = (index + 1) % hashNum;
        
        // Goes through the cluster after the found key and rehash
		while (hashTable[index] != null) {
			Node<V> n = hashTable[index];
			hashTable[index] = null;
			System.out.println("Key: " + n.key + " rehashed...");
			hashTable[getLocation(n,hashTable)] = n;
			index = (index + 1) % hashNum;
		}
	}

    /*
    ** Same as above but the key is inputed as an int and must be converted to a string. 
    ** @param a key of the key, value pair to delete
    */
	public void unset(int a) {
		unset(Integer.toString(a));
	}
	
    /*
    ** Deletes a node from the linked list stored in the hash table. 
    ** Changes the previous and next pointers of the nodes before and after it.
    ** @param index - the index of the node in the hash table
    */
	private void deleteNode(int index) {
        
        //Node is front of the list
		if (hashTable[index].previous == null) {
			hashTable[index].next.previous = null;
			head = hashTable[index].next;
			return;
		} else {
			hashTable[index].next.previous=hashTable[index].previous;
        }
        
        //Node is end of the list
		if (hashTable[index].next == null) {
			hashTable[index].previous.next = null;
			tail = hashTable[index].previous;
		} else {
			hashTable[index].previous.next = hashTable[index].next;
        }
	}

    /*
    ** Returns the size of the hash table
    */
	public int length() {
		return length;
	}

    /*
    ** Returns a Pair object created from the current node and advances the node down the list.
    ** Very similar to an iterator
    ** @return - Pair object created from the node or null if the current node is null
    */
	public Pair<V> each() {
        
		if (eachNode == null) {
			return null;
        }
        
		Pair<V> p = new Pair<V>(eachNode.key, eachNode.value);
		eachNode = eachNode.next;
		return p;
	}

    /*
    ** Resets the iteration of each back to the head of the list
    */
	public void reset() {
		eachNode = head;
	}

    /*
    ** Prints out the contents of the entire hash table
    */
	public void showTable() {
		System.out.println("Raw Hash Table Contents:");
        
		for (int i = 0; i < hashTable.length; i++) {
			if (hashTable[i] != null) {
				System.out.println(i + ": Key: " + hashTable[i].key + " Value: " + hashTable[i].value);
            } else {
				System.out.println(i + ": null");
            }
		}
	}

    /*
    ** Sorts the linked list using quick sort. 
    ** Values of the keys are changed to 0 - (length-1) and rehashed
    */
	@SuppressWarnings("unchecked")
	public void sort() {
        
        // Sorting of the list
		qSort(head, tail);	
        
		hashTable = (Node<V>[]) new Node<?>[hashNum];
		Node<V> current = head;
        
        // Reassignment of keys and rehashing
		for (int i = 0; i < length; i++) {
			current.key = Integer.toString(i);
			hashTable[getLocation(current,hashTable)] = current;
			current = current.next;
		}
	}

    /*
    ** Sort the linked list but does not change the keys
    */
	public void asort() {
		qSort(head, tail);
	}

    /*
    ** Partioning needed for quicksort 
    ** @param l - first node in the partition
    ** @param h - pivot node being the last value in the partition
    ** @return - pivot node after partitioning
    */
	private Node<V> partition(Node<V> l, Node<V> h) {
		// set pivot as h element
		V x = h.value;

		// similar to i = l-1 for array implementation
		Node<V> i = l.previous;

		// Similar to "for (int j = l; j <= h- 1; j++)"
		for (Node<V> j = l; j != h; j = j.next) {
			if (j.compareTo(h) >= 0) {
				// Similar to i++ for array
				i = (i == null) ? l : i.next;
				V temp = i.value;
				String temp2 = i.key;
				i.value = j.value;
				i.key=j.key;
				j.value = temp;
				j.key=temp2;
			}
		}
        
		i = (i == null) ? l : i.next; // Similar to i++
		V temp = i.value;
		String temp2 = i.key;
		i.value = h.value;
		i.key=h.key;
		h.value = temp;
		h.key=temp2;
		return i;
	}

    /*
    ** A recursive implementation of quicksort for linked list
    ** @param l - head node
    ** @param h - tail node
    */
	private void qSort(Node<V> l, Node<V> h) {
		if (h != null && l != h && l != h.next) {
			Node<V> temp = partition(l, h);
			qSort(l, temp.previous);
			qSort(temp.next, h);
		}
	}

    /*
    ** Returns an arraylist of all the keys
    ** @return arraylist of keys
    */
	public ArrayList<String> keys() {
		ArrayList<String> list = new ArrayList<String>(length);
		Node<V> current = head;
		for (int i = 0; i < length; i++) {
			list.add(current.key);
			current = current.next;
		}
		return list;
	}

    /*
    ** Returns an arraylist of the values 
    ** @return arraylist of values
    */
	public ArrayList<V> values() {
		ArrayList<V> list = new ArrayList<V>(length);
		Node<V> current = head;
		for (int i = 0; i < length; i++) {
			list.add(current.value);
			current = current.next;
		}
		return list;
	}

    /*
    ** Flips the keys and values inside each node
    ** @return - new PHPArray of the flipped array
    */
	public PHPArray<String> array_flip() {
		if (!(head.value instanceof String)) {
			throw new ClassCastException();
        } else {
			PHPArray<String> B = new PHPArray<String>(hashNum);
			Node<V> current=head;
			while(current!=null) {
				B.put((String)current.value,current.key);
				current=current.next;
			}
			return B;
		}
	}
	
    /*
    ** Creates and returns an iterator
    */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public Iterator<V> iterator() {
		return new ListIterator(head);
	}

    /*
    ** Node classes to make a linkedlist and hold the key, value pairs
    */
	private static class Node<V> implements Comparable<Node<V>> {
		String key;
		V value;
		Node<V> next;
		Node<V> previous;

		public Node(String k, V data) {
			key = k;
			value = data;
		}

		@Override
		public int compareTo(Node<V> n) {
			return ((Comparable) n.value).compareTo((Comparable) value);
		}
	}

    /*
    ** Pair class that goes along with each method
    */
	public static class Pair<V> {
		String key;
		V value;

		public Pair(String s, V value) {
			key = s;
			this.value = value;
		}
	}

    /*
    ** Iterator for the linkedlist
    */
	public class ListIterator<V> implements Iterator<V> {
		private Node<V> n;

		public ListIterator(Node<V> n) {
			this.n = n;
		}

		@Override
		public boolean hasNext() {
			return (n != null);
		}

		@Override
		public V next() {
			V temp = n.value;
			n = n.next;
			return temp;
		}
	}
}