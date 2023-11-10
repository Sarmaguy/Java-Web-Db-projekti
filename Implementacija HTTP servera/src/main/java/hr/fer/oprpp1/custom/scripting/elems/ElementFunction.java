package hr.fer.oprpp1.custom.scripting.elems;
/**
 * Function element
 * @author Jura
 *
 */
public class ElementFunction extends Element {
	private String name;
	
	public  ElementFunction(String name) {
		super();
		this.name=name;
	}
	@Override
	public String asText() {
		return name;
	}
}
