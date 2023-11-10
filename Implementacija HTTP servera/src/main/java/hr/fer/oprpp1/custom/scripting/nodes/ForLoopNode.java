package hr.fer.oprpp1.custom.scripting.nodes;

import hr.fer.oprpp1.custom.scripting.elems.Element;
import hr.fer.oprpp1.custom.scripting.elems.ElementVariable;
/**
 * For loop node
 * @author Jura
 *
 */
public class ForLoopNode extends Node {
	private static int index=1;
	private ElementVariable variable;
	private Element startExpression;
	private Element endExpression;
	private Element stepExpression;
	@Override
	public String toString() {
		index++;
		String s="ForLoopNode [variable=" + variable.asText() + ", startExpression=" + startExpression.asText() + ", endExpression="
				+ endExpression.asText() + ", stepExpression=" + stepExpression.asText() + "]";
		for (int i = 0; i < numberOfChildren(); i++) {
			s+="\n";
			for (int j = 0; j < index; j++) s+="\t";
			s+=getChild(i).toString();
		}
		index--;
		return s;
	}


	
	public ForLoopNode(ElementVariable variable, Element startExpression, Element endExpression,
			Element stepExpression) {
		super();
		this.variable = variable;
		this.startExpression = startExpression;
		this.endExpression = endExpression;
		this.stepExpression = stepExpression;
	}

	public static int getIndex() {
		return index;
	}

	public ElementVariable getVariable() {
		return variable;
	}

	public Element getStartExpression() {
		return startExpression;
	}

	public Element getEndExpression() {
		return endExpression;
	}

	public Element getStepExpression() {
		return stepExpression;
	}

	@Override
	public void accept(INodeVisitor visitor) {
		visitor.visitForLoopNode(this);
	}
}
