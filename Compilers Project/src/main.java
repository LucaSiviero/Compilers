import AST.ProgramOp;
import Visitor.*;
import java.io.File;
import java.io.FileReader;
import java.util.LinkedList;

public class main {

    public static void main(String[] args) throws Exception {
        String inFile = args[0];
        File Cfile = new File("C:\\Users\\lucas\\Desktop\\ESAME\\Translated\\file.c");
        String file = "C:\\Users\\lucas\\Desktop\\ESAME\\XML\\visit.xml";
        FileReader in = new FileReader(inFile);
        Lexer lexer = new Lexer(in);
        parser p = new parser(lexer);

        ProgramOp node = null;
        try {
            node = (ProgramOp) p.parse().value;
        }
        catch(Exception e){
            System.err.println("Parsing error");
            return;
        }

        if(node == null) return;

        new SyntaxTreeVisitor(file, node);
        RootSymbolTableVisitor v1 = new RootSymbolTableVisitor();
        v1.generateRootTable(node);

        SymbolTableVisitor v2 = new SymbolTableVisitor();
        v2.generateSymbolTables(node);

        TypeCheckVisitor v3 = new TypeCheckVisitor();
        LinkedList<String> typeCheckErrors = v3.doTypeCheck(node);
        if(typeCheckErrors.size() != 0) {
            System.err.println(typeCheckErrors);
        }

        CodeGenerationVisitor v4 = new CodeGenerationVisitor();
        v4.translateC(node, Cfile);

    }
}