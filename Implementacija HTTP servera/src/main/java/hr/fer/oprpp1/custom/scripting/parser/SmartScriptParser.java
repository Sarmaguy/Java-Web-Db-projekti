package hr.fer.oprpp1.custom.scripting.parser;

import hr.fer.oprpp1.custom.scripting.lexer.Lexer;
import hr.fer.oprpp1.custom.scripting.lexer.Token;
import hr.fer.oprpp1.custom.scripting.lexer.TokenType;
import hr.fer.oprpp1.custom.scripting.nodes.DocumentNode;
import hr.fer.oprpp1.custom.scripting.nodes.Node;

import java.util.Stack;

/**
 * Parser of our codes
 * @author Jura
 *
 */
public class SmartScriptParser {
	DocumentNode doc;
	Stack<Object> stack;
	private Lexer lexy;
	/**
	 * Basic constructor
	 * @param text - code that we want to parse
	 */
	public SmartScriptParser(String text) {
		doc = new DocumentNode();
		stack = new Stack<>();
		stack.push(doc);
		parse(new Lexer(text));
	}
	/**
	 * 
	 * @return document node
	 */
	public DocumentNode getDocumentNode() {
		return (DocumentNode) stack.peek();
	}
	/**
	 * Our main method for parsing
	 * @param lexer
	 */
	private void parse(Lexer lexer) {
		Token token = lexer.getToken();
		while (token.getType()!=TokenType.EOF) {
			Node vrh = (Node) stack.peek();
			TokenType type = token.getType();
			if (type==TokenType.END) {
				stack.pop();
			}
			if (type==TokenType.TXT) {
				vrh.addChildNode((Node) token.getNode());
			}
			if (type==TokenType.ECHO) {
				vrh.addChildNode((Node) token.getNode());
			}
			if (type==TokenType.FOR) {
				vrh.addChildNode((Node) token.getNode());
				stack.push(token.getNode());
			}
			try {
				token = lexer.getToken();
			} catch (Exception e) {
				e.printStackTrace();
				throw new SmartScriptParserException();
			}
				
		}
		
		
	}

}
