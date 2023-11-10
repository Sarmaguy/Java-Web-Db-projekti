package hr.fer.oprpp1.custom.scripting.lexer;

import hr.fer.oprpp1.custom.scripting.elems.Element;
import hr.fer.oprpp1.custom.scripting.elems.ElementConstantDouble;
import hr.fer.oprpp1.custom.scripting.elems.ElementConstantInteger;
import hr.fer.oprpp1.custom.scripting.elems.ElementFunction;
import hr.fer.oprpp1.custom.scripting.elems.ElementOperator;
import hr.fer.oprpp1.custom.scripting.elems.ElementString;
import hr.fer.oprpp1.custom.scripting.elems.ElementVariable;
import hr.fer.oprpp1.custom.scripting.nodes.EchoNode;
import hr.fer.oprpp1.custom.scripting.nodes.ForLoopNode;
import hr.fer.oprpp1.custom.scripting.nodes.TextNode;

import java.util.ArrayList;

/**
 * Class lexer for parsing code
 * @author Jura
 *
 */
public class Lexer {
	private char[] data; // ulazni tekst
	private Token token; // trenutni token
	private int currentIndex; // indeks prvog neobrađenog znaka
	private LexerMode state=LexerMode.NORMAL;
	/**
	 * Basic constructor
	 * @param text
	 */
	public Lexer(String text) { 
		data = text.toCharArray();
		currentIndex=0;
	}
	/**
	 * Method that parses text
	 * @return Token with value and type
	 */
	public Token getToken() {
		if (currentIndex==data.length) {
			token = new Token(TokenType.EOF, null);
			currentIndex++;
			return token;
		}
		String s="";
		boolean flag=false;
		if (state==LexerMode.NORMAL) {
			while(true){
				if((data[currentIndex]=='{' && data.length>currentIndex+1 ) && !flag)
					if(data[currentIndex+1]=='$') break;
				flag=false;
				s+=data[currentIndex++];
				if (data.length==currentIndex) {
					break;
				}
				if(data[currentIndex-1]=='\\' ) flag = true;
			}
			state=LexerMode.SPECIAL;
			token = new Token(TokenType.TXT, new TextNode(s));
			return token;
		}
		
		else {
			TokenType type=null;
			boolean bracket=false;
			while(data[currentIndex]!='}') {
				if (data[currentIndex]=='$')  bracket=true;
				
				//echo
				if (bracket && data[currentIndex]!=' ' && data[currentIndex]!='}') {
					if(data[currentIndex]=='=') {
						currentIndex++;
						bracket = false;
						type=TokenType.ECHO;
						while(data[currentIndex]==' ') {
							s+=data[currentIndex++];
						}
						String tmp="";
						ArrayList<Object> array= new ArrayList<>();
						boolean doubly=false;
						while(data[currentIndex]!='}') {
							if (data[currentIndex]=='$' && data[currentIndex+1]=='}' ) {
								currentIndex+=2;
								break;
							}
							if (data[currentIndex]==' ') currentIndex++;
							
							if(data[currentIndex]=='\"') {
								currentIndex++;
								while(data[currentIndex]!='\"') tmp+=data[currentIndex++];
								array.add(new ElementString(tmp));
								currentIndex++;
								tmp="";
							}
							
							if(data[currentIndex]=='@') {
								currentIndex++;
								while(Character.isLetter(data[currentIndex])) tmp+=data[currentIndex++];
								array.add(new ElementFunction(tmp));
								tmp="";
							}
							
							if (data[currentIndex]=='*' || data[currentIndex]=='+' ||data[currentIndex]=='-'||data[currentIndex]=='/'||data[currentIndex]=='^') {
								array.add(new ElementOperator(tmp+data[currentIndex++]));
							}
							
							if(Character.isLetter(data[currentIndex])){
								while(Character.isLetter(data[currentIndex])) tmp+=data[currentIndex++];
								array.add(new ElementVariable(tmp));
								tmp="";
							}
							
							if (Character.isDigit(data[currentIndex])) {
								String n="";
								doubly=false;
								while(Character.isDigit(data[currentIndex]) || !(doubly || data[currentIndex]=='.' )) {
									if (data[currentIndex]=='.') doubly=true;
									n+=data[currentIndex++];
								}
								if (doubly) array.add(new ElementConstantDouble(Double.valueOf(n)));
								else array.add(new ElementConstantInteger(Integer.valueOf(n)));
							}
							}
						Element[] e= new Element[array.size()];
						for (int i = 0; i < e.length; i++) {
							e[i]=(Element) array.get(i);
						}
						state=LexerMode.NORMAL;
						return new Token(TokenType.ECHO, new EchoNode(e));
					}
					
					//end
					if(data[currentIndex]=='E' && data[currentIndex+1]=='N' && data[currentIndex+2]=='D') {
						currentIndex+=3;
						while(data[currentIndex]==' ') currentIndex++;
						if(data[currentIndex]=='$' && data[currentIndex+1]=='}') {
							bracket = false;
							currentIndex+=2;
							type=TokenType.END;
							state=LexerMode.NORMAL;
							return new Token(type, null);
						}
						throw new LexerException();
						
					}
					
					//for
					if(data[currentIndex]=='F') {
						bracket = false;
						type=TokenType.FOR;
						if(data[currentIndex]=='F'&&data[currentIndex+1]=='O'&&data[currentIndex+2]=='R') {
							currentIndex+=3;
							
							//variable
							while(data[currentIndex]==' ') currentIndex++;
							String variable="";
							if(Character.isLetter(data[currentIndex])){
								while(Character.isLetter(data[currentIndex])) variable+=data[currentIndex++];
								
							
							while(data[currentIndex]==' ') currentIndex++;
							//start
							Element startexp = null;
							if(data[currentIndex]=='\"') {
								String tmp="";
								currentIndex++;
								while(data[currentIndex]!='\"') tmp+=data[currentIndex++];
								currentIndex++;
								startexp=new ElementString(tmp);
								
							}
							
							
							if(Character.isLetter(data[currentIndex])){
								String tmp="";
								while(Character.isLetter(data[currentIndex])) tmp+=data[currentIndex++];
								startexp= new ElementVariable(tmp);
							}
							
							if (Character.isDigit(data[currentIndex]) || (data[currentIndex]=='-' && Character.isDigit(data[currentIndex+1]))) {
								String n="";
								if (data[currentIndex]=='-') n+=data[currentIndex++];
								boolean doubly=false;
								while(Character.isDigit(data[currentIndex]) || (!doubly && data[currentIndex]=='.' ) || (doubly && data[currentIndex]!='.' )) {
									if (data[currentIndex]=='.') doubly=true;
									n+=data[currentIndex++];
								}
								if (doubly) startexp= new ElementConstantDouble(Double.valueOf(n));
								else startexp= new ElementConstantInteger(Integer.valueOf(n));
							}
							
							
							while(data[currentIndex]==' ') currentIndex++;
							//end
							Element endExpression = null;
							if(data[currentIndex]=='\"') {
								String tmp="";
								currentIndex++;
								while(data[currentIndex]!='\"') tmp+=data[currentIndex++];
								currentIndex++;
								endExpression=new ElementString(tmp);
								
							}
							
							
							if(Character.isLetter(data[currentIndex])){
								String tmp="";
								while(Character.isLetter(data[currentIndex])) tmp+=data[currentIndex++];
								endExpression= new ElementVariable(tmp);
							}
							
							if (Character.isDigit(data[currentIndex]) || (data[currentIndex]=='-' && Character.isDigit(data[currentIndex+1]))) {
								String n="";
								if (data[currentIndex]=='-') n+=data[currentIndex++];
								boolean doubly=false;
								while(Character.isDigit(data[currentIndex]) || (!doubly && data[currentIndex]=='.' ) || (doubly && data[currentIndex]!='.' )) {
									if (data[currentIndex]=='.') doubly=true;
									n+=data[currentIndex++];
								}
								if (doubly) endExpression= new ElementConstantDouble(Double.valueOf(n));
								else endExpression= new ElementConstantInteger(Integer.valueOf(n));
							}
							
							while(data[currentIndex]==' ') currentIndex++;
							if (data[currentIndex]=='$' && data[currentIndex+1]=='}') {
								currentIndex+=2;
								state=LexerMode.NORMAL;
								return new Token(TokenType.FOR, new ForLoopNode(new ElementVariable(variable), startexp, endExpression, null));
							}
							//step
							Element stepExpression=null;
							
							if(data[currentIndex]=='\"') {
								String tmp="";
								currentIndex++;
								while(data[currentIndex]!='\"') tmp+=data[currentIndex++];
								currentIndex++;
								stepExpression=new ElementString(tmp);
								
							}
							
							
							if(Character.isLetter(data[currentIndex])){
								String tmp="";
								while(Character.isLetter(data[currentIndex])) tmp+=data[currentIndex++];
								stepExpression= new ElementVariable(tmp);
							}
							
							if (Character.isDigit(data[currentIndex]) || (data[currentIndex]=='-' && Character.isDigit(data[currentIndex+1]))) {
								String n="";
								if (data[currentIndex]=='-') n+=data[currentIndex++];
								boolean doubly=false;
								while(Character.isDigit(data[currentIndex]) || (!doubly && data[currentIndex]=='.' ) || (doubly && data[currentIndex]!='.' )) {
									if (data[currentIndex]=='.') doubly=true;
									n+=data[currentIndex++];
								}
								if (doubly) stepExpression= new ElementConstantDouble(Double.valueOf(n));
								else stepExpression= new ElementConstantInteger(Integer.valueOf(n));
							}
							
							while(data[currentIndex]==' ') currentIndex++;
							if (data[currentIndex]=='$' && data[currentIndex+1]=='}') {
								currentIndex+=2;
								state=LexerMode.NORMAL;
								return new Token(TokenType.FOR, new ForLoopNode(new ElementVariable(variable), startexp, endExpression, stepExpression));
							}
							
							
							}
							
							
							
							
							
						}
					}
				}
				
				s+=data[currentIndex++];
			}
			s+=data[currentIndex++];
			state=LexerMode.NORMAL;
			throw new LexerException();
			
		}
		
	}

	private class LexerException extends RuntimeException {
		private static final long serialVersionUID = 1L;

		public LexerException() {
			super();
		}
	}
		
	}
