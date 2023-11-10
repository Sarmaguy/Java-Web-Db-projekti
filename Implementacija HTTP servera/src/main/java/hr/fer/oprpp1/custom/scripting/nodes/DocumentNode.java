package hr.fer.oprpp1.custom.scripting.nodes;
/**
 * Starting node
 * @author Jura
 *
 */
public class DocumentNode extends Node {

	@Override
	public String toString() {
		String s="DocumentNode ";
		for (int i = 0; i < numberOfChildren(); i++) {
			s+="\n\t"+getChild(i).toString();
		}
		return s;
	}


	@Override
	public void accept(INodeVisitor visitor) {
		visitor.visitDocumentNode(this);
	}
}
