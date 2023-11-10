	package hr.fer.oprpp1.custom.scripting.elems;
/**
 * Variable element
 * @author Jura
 *
 */
public class ElementVariable extends Element {
	private String name;
	
	public ElementVariable(String name) {
		super();
		this.name=name;
	}
	@Override
	public String asText() {
		return name;
	}

}
