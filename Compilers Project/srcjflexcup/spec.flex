import java_cup.runtime.*; 		//This is how we pass tokens to the parser
import java.io.*;
%%

%{
    StringBuffer string = new StringBuffer();

    private Symbol symbol(int type) {
        return new Symbol(type, yyline, yycolumn);
    }

    private Symbol symbol(int type, Object value) {
        return new Symbol(type, yyline, yycolumn, value);
    }
%}

%class Lexer
%unicode 								//Si usa per leggere i file di testo con codifica unicode
%cup                                    //usiamo il parser generator (CUP)
%line                                   //Per ogni simbolo vogliamo salvare in yyline e yycolumn riga e colonna nel file
%column
%state STRING

//Definizioni regolari

NoZeroDigit = [1-9]
ID = {LETTER} ({LETTER} | {DIGIT})*
DIGIT = [0-9]
LINETERMINATOR = [\r \n \t \f]*
WHITESPACE     = {LINETERMINATOR} | [ \t\f]
INT_CONST = 0 | {NoZeroDigit} {DIGIT}*
FLOAT_CONST =  {INT_CONST} "." {DIGIT}* {NoZeroDigit}
LETTER = [a-zA-z]
COMMENT   = "/*" [^*] ~"*/" | "/*" "*"+ "/"

%line

%%

<YYINITIAL> {

    "for"   {return symbol(Sym.FOR);}

    ";"     {return symbol(Sym.SEMI);}

    ","     {return symbol(Sym.COMMA);}

    "string"    {return symbol(Sym.STRING);}

    "bool"       {return symbol(Sym.BOOL);}

    "("     {return symbol(Sym.LPAR);}

    ")"     {return symbol(Sym.RPAR);}

    ":"     {return symbol(Sym.COLON);}

    "void"  {return symbol(Sym.VOID);}

    "readln"  {return symbol(Sym.READ);}

    "write" {return symbol(Sym.WRITE);}

    ":="    {return symbol(Sym.ASSIGN);}

    "+"     {return symbol(Sym.PLUS);}

    "-"     {return symbol(Sym.MINUS);}

    "*"     {return symbol(Sym.TIMES);}

    "/"     {return symbol(Sym.DIV);}

    "="     {return symbol(Sym.EQ);}

    "<>"    {return symbol(Sym.NE);}

    "<"     {return symbol(Sym.LT);}

    "<="    {return symbol(Sym.LE);}

    ">"     {return symbol(Sym.GT);}

    ">="    {return symbol(Sym.GE);}

    "&&"    {return symbol(Sym.AND);}

    "||"    {return symbol(Sym.OR);}

    "!"     {return symbol(Sym.NOT);}

    "null"  {return symbol(Sym.NULL);}

    "true"  {return symbol(Sym.TRUE);}

    "false" {return symbol(Sym.FALSE);}

	"if" { return symbol(Sym.IF, yytext()); }

    "fi"     {return symbol(Sym.FI);}

	"then" { return symbol(Sym.THEN); }

	"else" { return symbol(Sym.ELSE); }

    "elif"     {return symbol(Sym.ELIF);}

	"while" { return symbol(Sym.WHILE); }

    "do"     {return symbol(Sym.DO);}

    "od"     {return symbol(Sym.OD);}

	"proc"  {return symbol(Sym.PROC);}

	"corp"     {return symbol(Sym.CORP);}

    "int"   {return symbol(Sym.INT);}

    "float"     {return symbol(Sym.FLOAT);}

    ";"     {return symbol(Sym.SEMI);}

    "->"    {return symbol(Sym.RETURN);}

    \"      {string.setLength(0);  yybegin(STRING);}

	{INT_CONST} { return symbol(Sym.INT_CONST, yytext()); }

	{FLOAT_CONST} { return symbol(Sym.FLOAT_CONST, yytext()); }

	{ID} { return symbol(Sym.ID, yytext()); }

	{COMMENT}   {}


}

<STRING>{

    \"                             { yybegin(YYINITIAL); return symbol(sym.STRING_CONST, string.toString()); }
    [^\n\r\"\\]+                   { string.append( yytext() ); }
    \\t                            { string.append('\t'); }
    \\n                            { string.append('\n'); }
    \\r                            { string.append('\r'); }
    \\\"                           { string.append('\"'); }
    \\                             { string.append('\\'); }

}

{WHITESPACE}    {/*niente da fare*/}
[^]		{ return symbol(Sym.ERROR, yytext()); }
<<EOF>>	{ return symbol(Sym.EOF); }
