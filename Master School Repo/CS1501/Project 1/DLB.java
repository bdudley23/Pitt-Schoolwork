import java.util.*;
public class DLB implements DictInterface {
	// String Terminator
	private final char TERMINATE_CHAR = '$';
	
	// Root is the first node on the first level, only has one child, everything else should be a sibling
	Node rootNode;
	
	// Constructor
	public DLB() {
		rootNode = new Node();
	}
	
	public boolean add(String str) {
		// Add a terminator at the end of string
		str = str + TERMINATE_CHAR;
		Node currentNode = rootNode;
		boolean added = false;
		for (int i = 0; i < str.length(); i++) {
			char ch = str.charAt(i);
			NodeAddResult result = addChild(currentNode, ch);
			currentNode = result.node;
			added = result.added; // the node is added if it did not exist before
		}
		
		return added;
	}
	
	public int searchPrefix(StringBuilder key) {
		Node currentNode = rootNode;
		for (int i = 0; i < key.length(); i++) {
			char ch = key.charAt(i);
			currentNode = getChild(currentNode, ch);
			
			if (currentNode == null) {
				// Not a word, not a prefix
				return 0;
			}
		}
		
		// Method has not returned yet
		// currentNode is at the last char of the string, without terminator char
		// Find the node with terminator in currentNode's child nodes
		Node terminatorNode = getChild(currentNode, TERMINATE_CHAR);
		if (terminatorNode == null) {
			// The last char in the string does not have a terminator
			// Not a word, but a prefix
			return 1;
		} else if (terminatorNode.sibling == null) {
			// There is a terminator node, but it does not have peers
			// A word, but not a prefix
			return 2;
		} else {
			// There is a terminator node and it has peers
			// A word, and a prefix
			return 3;
		}
	}
	
    public int searchPrefix(StringBuilder key, int start, int end) {
        Node currentNode = rootNode;
		for (int i = start; i < end; i++) {
			char ch = key.charAt(i);
			currentNode = getChild(currentNode, ch);
			
			if (currentNode == null) {
				// Not a word, not a prefix
				return 0;
			}
		}
		
		// Method has not returned yet
		// currentNode is at the last char of the string, without terminator char
		// Find the node with terminator in currentNode's child nodes
		Node terminatorNode = getChild(currentNode, TERMINATE_CHAR);
		if (terminatorNode == null) {
			// The last char in the string does not have a terminator
			// Not a word, but a prefix
			return 1;
		} else if (terminatorNode.sibling == null) {
			// There is a terminator node, but it does not have peers
			// A word, but not a prefix
			return 2;
		} else {
			// There is a terminator node and it has peers
			// A word, and a prefix
			return 3;
		}
    }
    
    /*
    ** Searches for char c in the sibling nodes of this node.
    ** @param siblingStart The first sibling node on the level of search.
    ** @param ch The containing char of the node to search for.
    ** @return The sibling node that contains the char. Returns null if not found.
    */
	private Node getSibling(Node siblingStart, char ch) {
		Node nextSibling = siblingStart;
		while (nextSibling != null) {
			// break if data matches, so the node will be retained
			if (nextSibling.value == ch) 
                break;
			
			// go to next node if it ain't the char you are looking for
			nextSibling = nextSibling.sibling;
		}
		return nextSibling;
	}
	
	/**
	 * Searches for char ch in the child nodes of this node
	 * @param parentNode The parent node of the child nodes
	 * @param ch The containing char of the node to search for.
	 * @return The child node that contains the char. Returns null if not found.
	 */
	private Node getChild(Node parentNode, char ch) {
		return getSibling(parentNode.child, ch);
	}
	
	/**
	 * Adds a sibling node on this level.
	 * @param siblingStart The first sibling node on the level.
	 * @param ch The char to be added
	 * @return The node added. If a node containing the char already exists, that node will be returned.
	 */
	private NodeAddResult addSibling(Node siblingStart, char ch) {
		if (siblingStart == null) {
			siblingStart = new Node(ch);
			return new NodeAddResult(siblingStart, true);
		} else {
			Node nextSibling = siblingStart;
			while (nextSibling.sibling != null) {
				if (nextSibling.value == ch) 
                    break;
                
				    nextSibling = nextSibling.sibling;
			}
			// right now nextSibling is either a node that contains ch,
			// or the last node on the level and ch is not found
			if (nextSibling.value == ch) {
				// This node has the right data, no need to create a new one
				return new NodeAddResult(nextSibling, false);
			} else {
				// Did not find a node with specified value, create one in the end of the chain
				nextSibling.sibling = new Node(ch);
				return new NodeAddResult(nextSibling.sibling, true);
			}
		}
	}
	
    /*
    ** Adds a child node to this parent node.
    ** @param parentNode The parent node where the child node should be added to
    ** @param c The char to be added
    ** @return The node added. If a node containing the char already exists, that node will be returned.
    */
	private NodeAddResult addChild(Node parentNode, char ch) {
		// Check if parentNode has a child node or not
		if (parentNode.child == null) {
			// Parent node does not have any children
			// Simply create a node as its child node and return it
			parentNode.child = new Node(ch);
			return new NodeAddResult(parentNode.child, true);
		} else {
			// Parent node has a child node
			// Call addSibling() using the child node
			return addSibling(parentNode.child, ch);
		}
	}
}

//  Node class for DLB trie
class Node {
	Node sibling;
	Node child;
	char value;
	
	public Node() { }
	
	public Node(char value) {
		this(value, null, null);
	}
	
	public Node(char value, Node sibling, Node child) {
		this.value = value;
		this.sibling = sibling;
		this.child = child;
	}
}

 /*
 ** The result of an add node operation.
 ** node is the node added by the method, or an existing node that contains the value we are looking for
 ** added is true if the method added this node, false if the node already exists and method did not add the
 ** node.
 */
class NodeAddResult {
	Node node;
	boolean added;
	
	public NodeAddResult(Node node, boolean added) {
		this.node = node;
		this.added = added;
	}
}