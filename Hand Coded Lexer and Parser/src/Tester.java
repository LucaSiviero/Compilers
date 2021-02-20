import java.io.IOException;
public class Tester {

/*QUANDO SI INSERISCE IL PATH DEL FILE DA ANALIZZARE BISOGNA AGGIUNGERE LE VIRGOLETTE SE CI SONO SPAZI NEL PERCORSO AL FILE*/

    public static void main(String[] args) throws Exception {
        String filePath =args[0];
        Lexer lexicalAnalyzer = new Lexer();

        if (lexicalAnalyzer.initialize(filePath)) {
            Token token;
            try {
                while (!(token = lexicalAnalyzer.nextToken()).getName().equals("EOF")) {
                    System.out.println(token);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else
            System.out.println("File not found!!");
        boolean valid = Parser.isValid(args);
        if (valid)
            System.out.println("Valido");
        else
            System.out.println("Non valido");
    }


}
