import java.io.RandomAccessFile;
import java.util.logging.Logger;

public class Parser {

    public static Lexer lexer;
    public static Token token;
    static Logger log = Logger.getLogger("global");

    public static boolean isValid (String[] args) throws Exception{
        boolean isValid;
        lexer = new Lexer();
        lexer.initialize(args[0]);
        isValid = Program();
        return isValid;
    }

    private static boolean Program() throws Exception {
        if(!Stmt()){
            return false;
        }
        if(!Program1()){
            return false;
        }
        return true;
    }

    static boolean Stmt() throws Exception {
        token = read();
        if((token.getName().equals("IF"))) {
            if(!Expr()) {
                return false;
            }
            token = read();
            if(!(token.getName().equals("THEN"))) {
                return false;
            }

            if(!Stmt()) {
                return false;
            }
            if(Program1()) {
                return true;
            }

            if(!(token.getName().equals("ELSE"))) {
                return false;
            }
            if(!Stmt()) {
                return false;
            }
            return true;
        }else if((token.getName().equals("ID"))) {
            token = read();
            if(!(token.getName().equals("ASSIGN"))) {
                return false;
            }
            if(!Expr()) {
                return false;
            }
            else {
                return true;
            }
        }else if((token.getName().equals("WHILE"))) {
            if(!Expr()) {
                return false;
            }
            token = read();
            if(!(token.getName().equals("DO"))) {
                return false;
            }
            if(!Stmt()) {
                return false;
            }
            return true;
        }else{
            return false;
        }
    }

    private static boolean Program1() throws Exception {
        if(token.getName().equals("SEP")) {
            if(!Stmt()) {
                return false;
            }
            if(!Program1()) {
                return false;
            }
        }if(token.getName().equals("EOF"))
            return true;
        return false;
    }

    static boolean Expr() throws Exception {
        if(!Term()) {
            return false;
        }
        if(!Option()) {
            return false;
        }
        return true;
    }

    static boolean Term() throws Exception {
        token = read();
        if((token.getName().equals("ID"))) {
            return true;
        }else if((token.getName().equals("NUMBER"))) {
            return true;
        }else{
            return false;
        }
    }

    static boolean Option() throws Exception {
        token = read();
        if((token.getName().equals("RELOP"))) {
            if(!Term()) {
                return false;
            }
            return true;
        }
        return true;
    }

    private static Token read() throws Exception {
        token = lexer.nextToken();
        return token;
    }

}

