package hr.fer.oprpp1.custom.scripting.nodes;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Basic Node class
 * @author Jura
 *
 */
public abstract class Node {
	private ArrayList<Object> array;
	/**
	 * Basic constructor
	 */
	public Node() {
		
	};
	/**
	 * Adds child node to an array. If array does not exists it allocates one.
	 * @param child
	 */
	public void addChildNode(Node child) {
		if (array==null) array= new ArrayList<>();
		
		array.add(child);
	}
	/**
	 * 
	 * @return number of node's children
	 */
	public int numberOfChildren() {
		if (array==null) return 0;
		return array.size();
	}
	/**
	 * 
	 * @param index
	 * @return child located at specified index
	 */
	public Node getChild(int index) {
		return (Node) array.get(index);
	}
	@Override
	public int hashCode() {
		return Objects.hash(array);
	}
	@Override
	public boolean equals(Object obj) {
		return this.toString().equals(obj.toString());
	}

	public abstract void accept(INodeVisitor visitor);
	
	
	
	
	

}
