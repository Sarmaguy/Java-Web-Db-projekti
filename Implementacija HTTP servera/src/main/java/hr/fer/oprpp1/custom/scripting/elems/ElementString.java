package hr.fer.oprpp1.custom.scripting.elems;
/**
 * String element
 * @author Jura
 *
 */
public class ElementString extends Element {
	private String value;
	
	public  ElementString(String value) {
		super();
		this.value=value;
	}
	@Override
	public String asText() {
		return value;
	}

}
