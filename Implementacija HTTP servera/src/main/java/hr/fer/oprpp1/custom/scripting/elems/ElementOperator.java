	package hr.fer.oprpp1.custom.scripting.elems;
/**
 * Operator element
 * @author Jura
 *
 */
public class ElementOperator extends Element {
	private String symbol;
	
	public ElementOperator(String symbol) {
		super();
		this.symbol=symbol;
	}
	@Override
	public String asText() {
		return symbol;
	}

}
