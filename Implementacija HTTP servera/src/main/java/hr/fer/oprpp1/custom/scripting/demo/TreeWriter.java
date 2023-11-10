package hr.fer.oprpp1.custom.scripting.demo;

import hr.fer.oprpp1.custom.scripting.elems.Element;
import hr.fer.oprpp1.custom.scripting.nodes.*;
import hr.fer.oprpp1.custom.scripting.parser.SmartScriptParser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class TreeWriter {

    public static class WriterVisitor implements INodeVisitor {

        @Override
        public void visitTextNode(TextNode node) {
            System.out.print(node.getText());
        }

        @Override
        public void visitForLoopNode(ForLoopNode node) {
            String s = "{$ FOR " + node.getVariable().asText() + " " + node.getStartExpression().asText() + " " + node.getEndExpression().asText();

            if(node.getStepExpression() != null) {
                s += " " + node.getStepExpression().asText();
            }

            s += " $}";
            System.out.print(s);
            for (int i = 0; i < node.numberOfChildren(); i++) {
                node.getChild(i).accept(this);
            }
            s = "{$END$}";
            System.out.print(s);

        }

        @Override
        public void visitEchoNode(EchoNode node) {
            String s = "{$= ";

            for(Element e : node.getElements()) {
                s += e.asText() + " ";
            }

            s += "$}";
            System.out.print(s);
        }

        @Override
        public void visitDocumentNode(DocumentNode node) {
            for (int i = 0; i < node.numberOfChildren(); i++) {
                node.getChild(i).accept(this);
            }
        }
    }

    public static void main(String[] args) throws IOException {
        /**
        if (args.length != 1) {
            System.out.println("Expected 1 argument, got " + args.length);
            System.exit(1);
        }**/

        String path = "ulaz.txt";

        String data = new String(Files.readAllBytes(Paths.get(path)));

        SmartScriptParser parser = new SmartScriptParser(data);
        WriterVisitor visitor = new WriterVisitor();
        parser.getDocumentNode().accept(visitor);

    }
}
