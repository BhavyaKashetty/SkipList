"""
Skip lists
@author
Name: Bhavya Kashetty
"""
 
import java.util.Iterator;
import java.util.Random;
//import rbk.Timer;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class SkipList<T extends Comparable<? super T>> {
	static final int PossibleLevels = 33;

	static class Entry<E> {
		E element;
		Entry<E>[] next;
		Entry<E> prev;
		int[] span;

		public Entry(E x, int lev) {
			element = x;
			next = new Entry[lev];
			span = new int[lev];
		}

		public E getElement() {
			return element;
		}
	}
	Entry<T> head,tail;
	int size,maxLevel;
	Entry<T>[] last;
	Random random;
	// Constructor
	public SkipList() {
		head = new Entry<T>(null,PossibleLevels);
		tail = new Entry<T>(null,PossibleLevels);
		size =0;
		maxLevel=PossibleLevels;
		last = new Entry[PossibleLevels];
		random = new Random();
		for(int i=0;i<PossibleLevels;i++) {
			head.next[i] = tail;
		}
	}
	void find(T x) {
		Entry<T> cuurentNode = head;
		for(int i=maxLevel-1;i>=0;i--) {
			while(cuurentNode.next[i] != null && cuurentNode.next[i].element != null && cuurentNode.next[i].element.compareTo(x) < 0) {
				if(cuurentNode.next[i].element != null) {
					cuurentNode = cuurentNode.next[i];
				}
			}
			last[i] = cuurentNode;
		}
	}
	int chooseLevel() {
		int lev = 0;
		lev = 1;
		while(random.nextBoolean())
		{
			lev++;
		}
		if(lev>maxLevel) {
			maxLevel = lev;
		}
		return lev;
	}

	// Add x to list. If x already exists, reject it. Returns true if new node is added to list
	public boolean add(T x) {
		if(contains(x))
			return false;
		int level = chooseLevel();
		Entry<T> ent = new Entry<T>(x,level);
		for(int i=0;i<level;i++) {
			ent.next[i] = last[i].next[i];
			last[i].next[i] = ent;   	   
		}
		if(ent.next[0] != null)
			ent.next[0].prev = ent; 
		ent.prev = last[0];
		size = size + 1;
		return true;
	}

	// Find smallest element that is greater or equal to x
	public T ceiling(T x) {
		Entry<T> currentNode = head;   	
		for(int i=maxLevel-1;i>=0;i--) {
			Entry<T> val = currentNode.next[i];
			if(val != null && val.element != null){
				while(currentNode.next[i] != null && currentNode.next[i].element != null && currentNode.next[i].element.compareTo(x) < 0) {
					if(currentNode.next[i].element != null) {
						currentNode = currentNode.next[i];
					} else {
						break;
					}
				}
			} 
		}
		if (currentNode.next[0].element != null) {
			return currentNode.next[0].element;
		}
		return null;
	}
	// Find largest element that is less than or equal to x
		public T floor(T x) {
			Entry<T> currentNode = head;   	
			for(int i=maxLevel-1;i>=0;i--) {
				Entry<T> val = currentNode.next[i];
				if(val != null && val.element != null){
					while(currentNode.next[i].element != null && currentNode.next[i].element.compareTo(x) < 0) {
						if(currentNode.next[i].element != null) {
							currentNode = currentNode.next[i];
						} else {
							break;
						}
					}
				} 
			}
			if(currentNode.next[0].element != null && currentNode.next[0].element.compareTo(x) == 0) {
				return currentNode.next[0].element;
			} else {
				return currentNode.element;
			}
		}


	// Does list contain x?
	public boolean contains(T x) {
		find(x);
		if (last[0].next[0].element != null && last[0].next[0].element.compareTo(x) == 0) {
			return true;
		} else {
			return false;
		}
	}

	// Return first element of list
	public T first() {
		Entry<T> cuurentNode = head;
		if(cuurentNode.next[0] != null) {
			return cuurentNode.next[0].element;
		}
		return null;
	}

	
	// Return element at index n of list.  First element is at index 0.
	public T get(int n) {
		return getLinear(n);
	}

	// O(n) algorithm for get(n)
	public T getLinear(int n) {
		if(n<0 || n>size-1)
			return null;
		Entry<T> node = head;
		for(int i=0;i<=n;i++) 
			node = node.next[0];
		return node.element;
	}

	// Optional operation: Eligible for EC.
	// O(log n) expected time for get(n). Requires maintenance of spans, as discussed in class.
	public T getLog(int n) {
		return null;
	}

	// Is the list empty?
	public boolean isEmpty() {
		if(size==0){
			return true;
		} 
		return false;
	}

	// Iterate through the elements of list in sorted order
	public Iterator<T> iterator() {
		return new SkipListIterator();
	}

	//SkipListIterator to iterate elements in sorted order
	public class SkipListIterator implements Iterator<T>{
		Entry<T> cursor;
		SkipListIterator(){
			cursor = head;
		}
		public boolean hasNext() {
			if(cursor.next[0] != null && cursor.next[0].element != null) {
				return true;
			} else {
				return false;
			}
		}

		//Return the next element of list
		public T next() {
			if(cursor.next[0] != null && cursor.next[0].element != null) {
				cursor = cursor.next[0];
				return cursor.element;
			}
			else {
				return null;
			}
		}
	}

	// Return last element of list
	public T last() {
		Entry<T> currentNode = head;   	
		for(int i=maxLevel-1;i>=0;i--) {
			Entry<T> val = currentNode.next[i];
			if(val != null && val.element != null){
				while(currentNode.next[i].element != null) {
					if(currentNode.next[i].element != null) {
						currentNode = currentNode.next[i];
					} else {
						break;
					}
				}
			} 
		}
		return currentNode.element;
	}

	// Optional operation: Reorganize the elements of the list into a perfect skip list
	// Not a standard operation in skip lists. Eligible for EC.
	public void rebuild() {

	}

	// Remove x from list.  Removed element is returned. Return null if x not in list
	public T remove(T x) {
		if (contains(x)) {
			Entry<T> nodeToDelete = last[0].next[0];
			for(int i=0;i<nodeToDelete.next.length;i++) {
				last[i].next[i] = nodeToDelete.next[i];
			}
			size = size-1;
			return nodeToDelete.element;
		} else {
			return null;
		}
	}

	// Return the number of elements in the list
	public int size() {
		return size;
	}

	public static void main(String[] args) throws FileNotFoundException {
		Scanner sc;
		if (args.length > 0) {
			File file = new File(args[0]);
			sc = new Scanner(file);
		} else {
			sc = new Scanner(System.in);
		}
		String operation = "";
		long operand = 0;
		int modValue = 999983;
		long result = 0;
		Long returnValue = null;
		SkipList<Long> skipList = new SkipList<>();
		// Initialize the timer
		Timer timer = new Timer();

		while (!((operation = sc.next()).equals("End"))) {
			switch (operation) {
			case "Add": {	//Add element to SkipList
				operand = sc.nextLong();
				boolean value2 = skipList.add(operand);
				if(value2 == true) {
					result = (result + 1) % modValue;
				}
				break;
			}
			case "Ceiling": {	//returns smallest element that is greater or equal to x
				operand = sc.nextLong();
				returnValue = skipList.ceiling(operand);
				if (returnValue != null) {
					result = (result + returnValue) % modValue;
				}
				break;
			}
			case "First": {	//First element of the list
				returnValue = skipList.first();
				if (returnValue != null) {
					result = (result + returnValue) % modValue;
				}
				break;
			}
			case "Get": {	//Get index of element
				int intOperand = sc.nextInt();
				returnValue = skipList.get(intOperand);
				if (returnValue != null) {
					result = (result + returnValue) % modValue;
				}
				break;
			}
			case "Last": {	//Last element of the list
				returnValue = skipList.last();
				if (returnValue != null) {
					result = (result + returnValue) % modValue;
				}
				break;
			}
			case "Floor": {	//Floor value
				operand = sc.nextLong();
				returnValue = skipList.floor(operand);
				if (returnValue != null) {
					result = (result + returnValue) % modValue;
				}
				break;
			}
			case "Remove": {	//Removes element
				operand = sc.nextLong();
				returnValue = skipList.remove(operand);
				if (returnValue != null) {
					result = (result + 1) % modValue;
				}
				break;
			}
			case "Contains":{	//Checks the element
				operand = sc.nextLong();
				boolean vlaue = skipList.contains(operand);
				if (vlaue == true) {
					result = (result + 1) % modValue;
				}
				break;
			}
			case "Iterator":{	//Iterates through the SkipList
				Iterator it = skipList.iterator();
				while(it.hasNext()) {
					System.out.println(it.next());
				}
				break;
			}
			}
		}
		sc.close();
		// End Time
		timer.end();

		System.out.println(result);
		System.out.println(timer);
	}
	static public class Timer {
		long startTime, endTime, elapsedTime, memAvailable, memUsed;

		public Timer() {
			startTime = System.currentTimeMillis();
		}

		public void start() {
			startTime = System.currentTimeMillis();
		}

		public Timer end() {
			endTime = System.currentTimeMillis();
			elapsedTime = endTime-startTime;
			memAvailable = Runtime.getRuntime().totalMemory();
			memUsed = memAvailable - Runtime.getRuntime().freeMemory();
			return this;
		}

		public String toString() {
			return "Time: " + elapsedTime + " msec.\n" + "Memory: " + (memUsed/1048576) + " MB / " + (memAvailable/1048576) + " MB.";
		}
	}
}
