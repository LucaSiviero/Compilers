import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.logging.Logger;

public class Lexer {
    public static Logger log=Logger.getLogger("global");
    private RandomAccessFile input;             //File da leggere
    private static HashMap<String, Token> stringTable;  // la struttura dati potrebbe essere una hash map
    private int state;
    private String lessema;
    private int carattere;
    private char c;
    private Token toReturn;

    public Lexer() {
        // la symbol table in questo caso la chiamiamo stringTable
        stringTable = new HashMap<String, Token>();
        state = 0;
        stringTable.put("if", new Token("IF"));// inserimento delle parole chiavi nella stringTable per evitare di scrivere un diagramma di transizione per ciascuna di esse (le parole chiavi verranno "catturate" dal diagramma di transizione e gestite e di conseguenza). IF poteva anche essere associato ad una costante numerica
        stringTable.put("then", new Token("THEN"));
        stringTable.put("else", new Token("ELSE"));
        stringTable.put("while", new Token("WHILE"));
        stringTable.put("do", new Token("DO"));
        stringTable.put("int", new Token("INT"));
        stringTable.put("float", new Token("FLOAT"));
    }

    public Boolean initialize(String filePath) throws IOException {
        try {
            input = new RandomAccessFile(filePath, "rw");
            log.info("File aperto con successo!");
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void retract(RandomAccessFile in) {
        try {
            in.seek(in.getFilePointer() - 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Token installID(String lessema) {
        Token token = null;
        try {
            Integer.parseInt(lessema);
            token = new Token("NUMBER", lessema);
            return token;
        } catch (NumberFormatException e) {
        }
        try {
            Float.parseFloat(lessema);
            token = new Token("NUMBER", lessema);
            return token;
        } catch (NumberFormatException e) {
            for (String key : stringTable.keySet()) {
                if (lessema.equals(key)) {
                    token = new Token(lessema.toUpperCase());
                    return token;
                }
            }
            token = new Token("ID", lessema);
            return token;
        }
    }

    public Token nextToken() throws Exception, EOFException {  //Ad ogni chiamata del lexer (nextToken())
        //si resettano tutte le variabili utilizzate
        state = 9;      //Lo stato 9 è il nostro stato iniziale
        String lessema = ""; //è il lessema riconosciuto


        while (true) {
            switch (state) {
                case 9:
                    carattere = input.read();
                    if (carattere != -1) {
                        c = (char) carattere;
                        if (Character.isLetter(c)) {      //Se il carattere letto è una lettera, passo allo stato 10, e voglio sapere se sto per leggere un ID o una keyword
                            lessema += c;
                            state = 10;
                        }
                        if(Character.isDigit(c)){           //Se è un numero, probabilmente sto leggendo una costante numerica.
                            state = 13;
                            lessema += c;
                        }
                        if(c == '0'){       //Se ho letto uno 0 come primo carattere gestisco l'errore come scritto nel README.md
                            //lessema += c;    Non faccio l'append, perché il caso in cui il char letto è un digit viene incontrato prima :)
                            int chartemp = input.read();
                            char temp = (char) chartemp;
                            if(temp == 46) {
                                state = 13;

                            }
                            else {
                                retract(input);
                                state = 20;
                            }
                        }
                        if (c == '(' || c == ')' || c == '{' || c == '}' || c == ',' || c == ';') { //Separatori
                            toReturn = new Token("SEP", Character.toString(c));
                            //System.out.println(toReturn.toString());
                            state = 9;
                            return toReturn;
                        }
                        if (c == '<'){      //Operatori relazionali
                            state = 1;
                        }
                        if (c == '>'){
                            state = 2;
                        }
                        if(c == '='){
                            toReturn = new Token("RELOP", "EQ");
                            System.out.println(toReturn.toString());
                            state = 9;
                        }
                        if (c == '/') {     //Se ho letto uno slash, voglio assicurarmi che sto leggendo un commento. Se il commento non viene chiuso, il programma NON termina la sua esecuzione.
                            carattere = input.read();
                            if(carattere != -1){
                                c = (char) carattere;
                                if (c == '*') {
                                    carattere = input.read();
                                    c = (char) carattere;
                                    while (c != '*') {
                                        carattere = input.read();
                                        c = (char) carattere;
                                    }
                                    if(c == '*'){
                                        carattere = input.read();
                                        c = (char) carattere;
                                        if (c == '/') {
                                            log.info("Ho letto un commento e lo ignoro!");
                                        }
                                    }
                                }
                            }
                        }

                    }

                    else if(carattere == -1){       //Se è EOF chiudo tutto
                        state = -1;
                    }

                    break;


                case 1:
                    carattere = input.read();
                    if(carattere != -1){
                        c = (char) carattere;
                        if(c == '='){
                            toReturn = new Token("RELOP", "LE");
                            System.out.println(toReturn.toString());
                            state = 9;
                        }
                        else if(c == '-'){
                            carattere = input.read();
                            char temp = (char) carattere;
                            if(temp == '-'){
                                toReturn = new Token("ASSIGN");
                                //System.out.println(toReturn.toString());
                                state = 9;
                                return toReturn;
                            }
                        }
                        else{
                            toReturn = new Token("RELOP", "LT");
                            //System.out.println(toReturn.toString());
                            state = 9;
                            retract(input);
                            return toReturn;
                        }
                    }
                    else{
                        toReturn = new Token("RELOP", "LT");
                        state = -1;
                        return toReturn;
                    }

                    break;      //Break case 1

                case 2:
                    carattere = input.read();
                    if(carattere != -1) {
                        c = (char) carattere;
                        if (c == '=') {
                            toReturn = new Token("RELOP", "GE");
                            state = 9;
                            return toReturn;

                        } else {
                            toReturn = new Token("RELOP", "GT");
                            retract(input);
                            state = 9;
                            return toReturn;
                        }
                    }
                    break;      //Break case 2


                case 10:
                    carattere = input.read();
                    if (carattere != -1) {
                        c = (char) carattere;
                        if (Character.isLetterOrDigit(c)) {     //Continuo a leggere dopo una lettera, sto leggendo un id o una keyword
                            lessema += c;
                            state = 10;
                            break;
                        }
                        if (!(Character.isLetterOrDigit(c))) {      //Se leggo altro, vado nello stato finale 11 (Come sul libro)
                            retract(input);
                            state = 11;
                        }
                    }
                    if(carattere == -1){        //Gestione EOF
                        state = -1;
                    }

                case 11:
                    return installID(lessema);

                case 13:            //Nel caso in cui ho letto una cifra, che non sia lo 0
                    carattere = input.read();
                    if(carattere != -1){
                        c = (char) carattere;
                        if(Character.isDigit(c)){       //Cerco altre cifre e ciclo sullo stato
                            lessema += c;
                            state = 13;
                        }
                        if(c == '.'){               //Cerco un punto per capire se sto leggendo un float
                            lessema += c;
                            state = 14;
                        }
                        if(c == 'E'){               //Cerco una E per capire se sto leggendo un esponente.
                            lessema += c;
                            state = 16;
                            break;
                        }
                        if(!(Character.isDigit(c)) && c != '.'){        //Se non è nessuno di questi due casi restituisco il token
                            retract(input);
                            state = 20;
                        }
                    }
                    else{
                        state = 20;
                    }
                    break;

                case 14:            //Subito dopo che ho letto un punto
                    carattere = input.read();
                    if(carattere != -1){
                        c = (char) carattere;
                        if(Character.isDigit(c)){       //Se leggo una cifra continuo su 15
                            lessema += c;
                            state = 15;
                        }
                        else{               //Altrimenti ritorno il token e gestisco l'input come specificato in README.md
                            retract(input);
                            state = 20;
                        }
                    }
                    else{
                        state = 20;
                    }
                    break;

                case 15:        //Dopo che ho letto la prima cifra decimale continuo a cercare altri caratteri
                    carattere = input.read();
                    if(carattere != -1){
                        c = (char) carattere;
                        if(Character.isDigit(c)){       //Se è un'altra cifra, sto continuando a leggere il numero
                            lessema += c;
                        }
                        if(!Character.isDigit(c)){      //Se non è una cifra ho un errore e lo gestisco come in README.md
                            state = 21;
                        }
                        if(c == 'E'){           //Se leggo una E sto leggendo un esponente
                            lessema += c;
                            state = 16;
                        }
                    }
                    else{
                        state = 20;
                    }
                    break;

                case 16:        //Dopo l'esponente
                    carattere = input.read();
                    if(carattere != -1){
                        c = (char)carattere;
                        if(c == '+' || c == '-'){       //Voglio leggere il segno
                            lessema+=c;
                            state = 17;
                            break;
                        }
                        if(Character.isDigit(c)){           //Se non c'è l'esponente è positivo
                            lessema += c;
                            state = 18;
                        }
                        else{                   //Altrimenti gestisco l'errore
                            retract(input);
                            state = 20;
                        }
                    }
                    else{
                        state = 20;
                    }
                    break;

                case 17:        //Qui ho letto un + o - dopo la E, quindi ora cerco un numero o gestisco l'errore
                    carattere = input.read();
                    if(carattere != -1){
                        c = (char)carattere;
                        if(Character.isDigit(c)){
                            lessema += c;
                            state = 18;
                        }
                        else{
                            retract(input);
                            state = 20;
                        }
                    }
                    else{
                        state = 20;
                    }
                    break;

                case 18:  //Qui sto leggendo le ultime cifre del mio esponente.
                    carattere = input.read();
                    if(carattere != -1){
                        c = (char)carattere;
                        if(Character.isDigit(c)){
                            lessema += c;
                        }
                        else{
                            retract(input);
                            state = 20;
                        }
                    }
                    else{
                        state = 20;
                    }
                    break;

                case 20:
                    state = 9;
                    return installID(lessema);

                case 21:
                    retract(input);
                    return installID(lessema);

                case -1:
                    toReturn = new Token("EOF");
                    return toReturn;

                default:
                    break;
            }//end switch
        }//end while
    }
}

