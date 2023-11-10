package hr.fer.oprpp1.custom.scripting.nodes;

import java.util.Arrays;

import hr.fer.oprpp1.custom.scripting.elems.Element;
/**
 * Node for "="
 * @author Jura
 *
 */
public class EchoNode extends Node {
	private Element[] elements;

	@Override
	public String toString() {
		String s= "EchoNode [elements=";
		for (int i = 0; i < elements.length; i++) {
			s+=" " + elements[i].asText();
		}
		return s+"]";
	}


	
	public EchoNode(Element[] elements) {
		this.elements=elements;
	}


	@Override
	public void accept(INodeVisitor visitor) {
		visitor.visitEchoNode(this);
	}

	public Element[] getElements() {
		return elements;
	}
}
