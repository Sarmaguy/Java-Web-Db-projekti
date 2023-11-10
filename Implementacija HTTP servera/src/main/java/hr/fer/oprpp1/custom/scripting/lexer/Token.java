package hr.fer.oprpp1.custom.scripting.lexer;

import hr.fer.oprpp1.custom.scripting.nodes.Node;
/**
 * Token class used by lexer
 * @author Jura
 *
 */
public class Token {
	TokenType type;
	Node node;
	/**
	 * Basic constructor
	 * @param type
	 * @param value
	 */
	public Token(TokenType type, Node value) {
		super();
		this.type = type;
		this.node = value;
	}
	/**
	 * 
	 * @return type of Token
	 */
	public TokenType getType() {
		return type;
	}
	/**
	 * 
	 * @return Token's node
	 */
	public Node getNode() {
		return node;
	}
	
	

}
