package hr.fer.oprpp1.custom.scripting.exec;

import hr.fer.oprpp1.custom.scripting.elems.*;
import hr.fer.oprpp1.custom.scripting.nodes.*;
import hr.fer.oprpp1.custom.scripting.parser.SmartScriptParser;
import oprpp2.jmbag.webserver.RequestContext;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.*;

public class SmartScriptEngine {
    private DocumentNode documentNode;
    private RequestContext requestContext;
    private ObjectMultistack multistack = new ObjectMultistack();
    private INodeVisitor visitor = new INodeVisitor() {
        @Override
        public void visitTextNode(TextNode node) {
            try {
                requestContext.write(node.getText());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void visitForLoopNode(ForLoopNode node) {
            String variable = node.getVariable().asText();
            ValueWrapper startExpression = new ValueWrapper(node.getStartExpression().asText());
            String endExpression = node.getEndExpression().asText();
            String stepExpression = node.getStepExpression().asText();

            multistack.push(variable, startExpression);
            while (multistack.peek(variable).numCompare(endExpression) <= 0) {
                for (int i = 0; i < node.numberOfChildren(); i++) {
                    node.getChild(i).accept(this);
                }
                multistack.peek(variable).add(stepExpression);
            }

            multistack.pop(variable);

        }

        @Override
        public void visitEchoNode(EchoNode node) {
            Stack<ValueWrapper> tempStack = new Stack<>();

            for(Element e : node.getElements()){
                if (e instanceof ElementConstantDouble){
                    tempStack.push(new ValueWrapper(
                            ((ElementConstantDouble)e).getValue()));
                    
                } else if (e instanceof ElementConstantInteger) {
                    tempStack.push(new ValueWrapper(
                            ((ElementConstantInteger)e).getValue()));


                } else if (e instanceof ElementVariable) {
                    tempStack.push(new ValueWrapper(
                            multistack.peek((e).asText()).getValue()));

                } else if (e instanceof ElementString) {
                    tempStack.push(new ValueWrapper(
                            e.asText()));
                    
                } else if (e instanceof ElementOperator) {
                    ValueWrapper first = tempStack.pop();
                    ValueWrapper second = tempStack.pop();
                    String operator = e.asText();
                    switch (operator) {
                        case "+" -> {
                            second.add(first.getValue());
                        }
                        case "-" -> {
                            second.subtract(first.getValue());
                        }
                        case "*" -> {
                            second.multiply(first.getValue());
                        }
                        case "/" -> {
                            second.divide(first.getValue());
                        }
                    }
                    tempStack.push(second);

                } else if (e instanceof ElementFunction){
                    String name = e.asText();

                    switch (name){
                        case "sin" -> {
                            ValueWrapper value = tempStack.pop();
                            value.add(0.0);
                            Double x = (Double) value.getValue();
                            value.setValue(Math.sin((Math.toRadians(x)))); //rad ili ne rad ???
                            tempStack.push(value);
                        }
                        case "decfmt" -> {
                            ValueWrapper f = tempStack.pop();
                            ValueWrapper x = tempStack.pop();
                            double doubleX = (double) x.getValue();
                            DecimalFormat r = new DecimalFormat(f.getValue().toString());
                            tempStack.push(new ValueWrapper(r.format((doubleX))));
                        }
                        case "dup" -> {
                            ValueWrapper value = tempStack.pop();
                            tempStack.push(value);
                            tempStack.push(value);
                        }
                        case "swap" -> {
                            ValueWrapper first = tempStack.pop();
                            ValueWrapper second = tempStack.pop();
                            tempStack.push(first);
                            tempStack.push(second);
                        }
                        case "setMimeType" -> {
                            ValueWrapper value = tempStack.pop();
                            requestContext.setMimeType(String.valueOf(value.getValue()));
                        }
                        case "paramGet" -> {
                            ValueWrapper dv = tempStack.pop();
                            ValueWrapper nm = tempStack.pop();
                            String value = requestContext.getParameter(String.valueOf(nm.getValue()));
                            tempStack.push(value==null ? dv : new ValueWrapper(value));
                        }
                        case "pparamGet" -> {
                            ValueWrapper dv = tempStack.pop();
                            ValueWrapper nm = tempStack.pop();
                            String value = requestContext.getPersistentParameter(String.valueOf(nm.getValue()));
                            tempStack.push(value==null ? dv : new ValueWrapper(value));
                        }
                        case "pparamSet" -> {
                            ValueWrapper nm = tempStack.pop();
                            ValueWrapper value = tempStack.pop();
                            requestContext.setPersistentParameter(String.valueOf(nm.getValue()), String.valueOf(value.getValue()));
                        }
                        case "pparamDel" -> {
                            ValueWrapper nm = tempStack.pop();
                            requestContext.removePersistentParameter(String.valueOf(nm.getValue()));
                        }
                        case "tparamGet" -> {
                            ValueWrapper dv = tempStack.pop();
                            ValueWrapper nm = tempStack.pop();
                            String value = requestContext.getTemporaryParameter(String.valueOf(nm.getValue()));
                            tempStack.push(value==null ? dv : new ValueWrapper(value));
                        }
                        case "tparamSet" -> {
                            ValueWrapper nm = tempStack.pop();
                            ValueWrapper value = tempStack.pop();
                            requestContext.setTemporaryParameter(String.valueOf(nm.getValue()), String.valueOf(value.getValue()));
                        }
                        case "tparamDel" -> {
                            ValueWrapper nm = tempStack.pop();
                            requestContext.removeTemporaryParameter(String.valueOf(nm.getValue()));
                        }
                    }

                }
            }
            List<ValueWrapper> list = new LinkedList<>();
            while(!tempStack.isEmpty())
                list.add(0, tempStack.pop());

            for(ValueWrapper v : list) {
                try {
                    requestContext.write(String.valueOf(v.getValue()));
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }

        @Override
        public void visitDocumentNode(DocumentNode node) {
            for (int i = 0; i < node.numberOfChildren(); i++) {
                node.getChild(i).accept(this);
            }
        }
    };

    public SmartScriptEngine(DocumentNode documentNode, RequestContext requestContext) {
        this.documentNode = documentNode;
        this.requestContext = requestContext;
    }

public void execute(){
        documentNode.accept(visitor);
    }

    public static void main(String[] args) throws IOException {
        String documentBody = new String(Files.readAllBytes(Paths.get("ulaz.txt")));
        Map<String,String> parameters = new HashMap<String, String>();
        Map<String,String> persistentParameters = new HashMap<String, String>();
        List<RequestContext.RCCookie> cookies = new ArrayList<RequestContext.RCCookie>();
// create engine and execute it
        new SmartScriptEngine(
                new SmartScriptParser(documentBody).getDocumentNode(),
                new RequestContext(System.out, parameters, persistentParameters, cookies)
        ).execute();


    }
}
