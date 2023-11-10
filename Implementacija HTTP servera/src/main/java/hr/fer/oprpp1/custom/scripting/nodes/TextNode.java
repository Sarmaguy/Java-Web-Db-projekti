package hr.fer.oprpp1.custom.scripting.nodes;

public class TextNode extends Node {
	private String text;
	
	public TextNode(String text)
	{
		super();
		this.text=text;
	}
	/**
	 * 
	 * @return text of node
	 */
	public String getText() {
		return text;
	}

	@Override
	public String toString() {
		return "TextNode [text=" + text + "]";
	}

	@Override
	public void accept(INodeVisitor visitor) {
		visitor.visitTextNode(this);
	}
}
