package app;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import java.io.IOException;
import java.util.HashMap;

import antlr.*;
import Classes.*;
import visitor.*;

import org.antlr.v4.runtime.tree.ParseTree;
import visitor.dartVisitor;

public class Dart {
    public static void main(String[] args) {
        String fileName = args[0];
        dartParser parser = getParser(fileName);

        ParseTree AST = parser.prog();
        dartVisitor visitor =  new dartVisitor();

        Program program = (Program) visitor.visit(AST);
        System.out.println(program);

        if(visitor.semanticErrors.isEmpty()) {
            /*
            * Methods Symbol Table
            * */
            System.out.println("\n\n\n\n\n----------- Methods Symbol Table -------------------------");
            MethodProcessor mp = new MethodProcessor(visitor.methods , visitor.assignedVars);
            mp.getReturnValues().forEach((key,value)->{
                if(key.length() < 15){
                    for(int i=key.length();i<15;i++){
                        key = key + " ";
                    }
                }
                System.out.println("| Method " + key + "| Return value is : " +value + " ");
            });
            System.out.println("-----------------------------------------------------------");

            /*
            * Variables Symbol Table
             */

            System.out.println("\n----------- Variables Symbol Table -------------------------");
            VariableProcessor vp = new VariableProcessor(visitor.assignedVars);
            vp.getVars().forEach((key,value)->{
                if(key.length() < 15){
                    for(int i=key.length();i<15;i++){
                        key = key + " ";
                    }
                }
                System.out.println("| Variable " + key + "| Value is : " +value + " ");
            });
            System.out.println("-----------------------------------------------------------");
        }
        else {
            for(String err : visitor.semanticErrors) {
                System.out.println(err);
            }
        }

    }
    private static dartParser getParser(String fileName) {
        dartParser parser = null;

        CharStream input;
        try {
            input = CharStreams.fromFileName(fileName);
            dartLexer lexer = new dartLexer(input);
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            parser = new dartParser(tokens);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return parser;
    }
}
