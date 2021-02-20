package Visitor;

import AST.*;
import AST.ArithmeticExprs.ArithmeticExpr;
import AST.BinaryExpr.*;
import AST.Const.*;
import AST.Expr.ExprCallProc;
import AST.Expr.ExprNode;
import AST.Expr.IdNode;
import AST.Functions.CallProcOp;
import AST.Functions.ProcBodyOp;
import AST.Functions.ProcOp;
import AST.Stat.*;
import AST.Type.*;
import AST.UnaryExpr.MinusOp;
import AST.UnaryExpr.NotOp;
import AST.UnaryExpr.UnaryExprNode;
import SymbolTable.*;
import SymbolTable.SymbolTable;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.logging.Logger;

public class CodeGenerationVisitor implements VisitorInterface <StringBuilder> {
    Boolean multipleDifferentReturns = false;
    Boolean multipleReturns = false;
    SymbolTable temp = new SymbolTable(null);
    SymbolTable structTable = new SymbolTable(null);
    Logger log = Logger.getGlobal();
    StringBuilder mainProgram;
    boolean flag = false;
    int count = 0;
    int count2 = 0;


    public CodeGenerationVisitor() {
        this.mainProgram = new StringBuilder();
    }

    public StringBuilder translateC(ProgramOp node, File filename) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append(Visit(node));
        FileWriter fileWriter = new FileWriter(filename);
        fileWriter.write(sb.toString());
        fileWriter.close();
        return sb;
    }

    @Override
    public StringBuilder Visit(ForOp node) {
        mainProgram.append("for(");
        IdListInitOp ids = node.getVarDecl().getIds();
        for(IdNode i : ids.getIds()) {
            mainProgram.append(i.getId());
            for(ExprNode e : ids.getExprs()) {
                if(e.toConst() != null) {
                    mainProgram.append(" = " + e.toConst().getArgument() + ";");
                }
                if(e.toId(e) != null) {
                    mainProgram.append(" = " + e.toId(e).getId() + ";");
                }
            }
        }
        mainProgram.append(node.getEval().accept(this) + ";");
        if(node.getAssign().accept(this) != null) {
            mainProgram.append(node.getAssign().accept(this));
        }
        mainProgram.append("){");

        for(VarDeclOp var : node.getBody().getVarDecls()){
            mainProgram.append(var.getType().toCtype() +" ");
           for(IdNode i : var.getIds().getIds()) {
               mainProgram.append(i.getId());
               for(ExprNode e : var.getIds().getExprs()) {
                if(e.toConst() != null) {
                    mainProgram.append( " = " + e.toConst().getArgument() + ";\n");
                }
               }
           }
        }
        for(StatNode s : node.getBody().getStats()){
            s.accept(this);
        }

        mainProgram.append("}");


        return null;
    }

    @Override
    public StringBuilder Visit(ASTNode node) {
        return new StringBuilder();
    }

    @Override
    public StringBuilder Visit(ProgramOp node) {
        StringBuilder sb = new StringBuilder();


        mainProgram.append("#include<stdio.h>\n");
        mainProgram.append("#include<string.h>\n\n");
        if(node.getProcs() != null) {
            LinkedList<IdNode> idlist;
            GenericType t;
            for(ProcOp p : node.getProcs()) {
                if(!p.getFun_name().getId().equals("main")) {
                    if(p.getProcBody().getEs().size() > 1){
                        for(int i = 1; i < p.getProcBody().getEs().size(); i++) {
                            if(p.getProcBody().getEs().get(i) != p.getProcBody().getEs().getFirst()){
                                flag = true;
                            }
                        }

                        if(flag) {
                            multipleDifferentReturns = true;
                            count++;
                            mainProgram.append("typedef struct ret" + count+ " {\n");
                            for(ExprNode e : p.getProcBody().getEs()){
                                count2++;
                                if(e.getType().toCtype().equals("char")){
                                    mainProgram.append("\t"+e.getType().toCtype() + "* x"+count2+";\n");
                                }
                                else {
                                    mainProgram.append("\t" + e.getType().toCtype() + " x" + count2 + ";\n");
                                }
                            }
                            count2 = 0;
                            mainProgram.append("};\n");
                            mainProgram.append("struct ret"+count + " " + p.getFun_name().getId() + "(");
                        }
                        //count = 0;
                        if(!flag) {
                            mainProgram.append(p.getReturns().getFirst().getType().Ctype(p.getReturns().getFirst().getType()) + "* " + p.getFun_name().getId() + "(");
                        }
                    }
                    else {
                        mainProgram.append(p.getReturns().getFirst().getType().toCtype() + " " + p.getFun_name().getId() + "(");
                    }

                    if(p.getPars() == null) {
                        mainProgram.append(");\n");
                    }
                    if(p.getPars() != null) {

                        LinkedList<IdNode> ids = new LinkedList<>();

                        for(ParDeclOp par : p.getPars()){
                            for(int i = par.getIds().size() - 1; i >= 0; i--){
                                ids.add(par.getIds().get(i));
                            }
                        }

                        for (IdNode id : ids) {
                            t = id.getTable().lookUp(id.getId()).getType();
                            if (id != ids.getLast()) {
                                mainProgram.append(t.toCtype() + " " + id.getId() + ",");
                            } else if (id == ids.getLast()) {
                                mainProgram.append(t.toCtype() + " " + id.getId() + ");\n\n");
                            }
                        }

                    }
                    else if(!flag){
                        //mainProgram.append(");\n");
                    }
                }
            }count = 0;
        }


        if(node.getVarDecls() != null) {
            for(VarDeclOp v : node.getVarDecls()) {
                v.accept(this);
            }
        }


        if(node.getProcs() != null) {
            for(ProcOp p : node.getProcs()) {
                sb.append(p.accept(this));
            }
        }
        return mainProgram;
    }

    @Override
    public StringBuilder Visit(VarDeclOp node) {
        if(node.getIds() != null) {
            node.getIds().accept(this);
            }
        return null;
    }

    @Override
    public StringBuilder Visit(IntegerConst node) {
        //mainProgram.append(node.getValue() + ";\n");
        return null;
    }

    @Override
    public StringBuilder Visit(ProcOp node) {

        if(node.getFun_name().getId().equals("main")) {
            mainProgram.append("int " + node.getFun_name().getId() + "() ");
            mainProgram.append("{\n");
            senzaParentesi(node.getProcBody());
            mainProgram.append("return 0;\n}\n");
        }

        else if(node.getReturns().size() > 1 && flag) {
            count++;
            mainProgram.append("struct ret" + count + " " + node.getFun_name().getId() + "(");
            if(node.getPars() == null) {
                mainProgram.append("){\n");
            }
            if(node.getPars() != null) {
                GenericType t;
                LinkedList<IdNode> ids = new LinkedList<>();
                LinkedList<ParDeclOp> pars = new LinkedList<>();
                for(int i = node.getPars().size()-1; i >= 0; i--) {
                    pars.add(node.getPars().get(i));
                }
                for(ParDeclOp p : pars){
                    for(IdNode i : p.getIds()){
                        ids.add(i);
                    }
                }

                for (IdNode id : ids) {

                    t = id.getTable().lookUp(id.getId()).getType();
                    if (id != ids.getLast()) {
                        mainProgram.append(t.toCtype() + " " + id.getId() + ",");
                    } else if (id == ids.getLast()) {
                        mainProgram.append(t.toCtype() + " " + id.getId() + "){\n");
                    }
                }

            }

            if(node.getProcBody().accept(this) == null) {
            }

            else {
                mainProgram.append(node.getProcBody().accept(this));
            }

        }

        else if(node.getReturns().size() > 1 && !flag) {
            GenericType tipo = node.getReturns().getFirst().getType();
            String tipoC = tipo.Ctype(tipo) + "* ";
            mainProgram.append(tipoC + node.getFun_name().getId() +"(");
            if(node.getPars() == null) {
                mainProgram.append("){\n");

            }
            if(node.getPars() != null) {
                LinkedList<IdNode> idlist;
                GenericType t;

                for (ParDeclOp p : node.getPars()) {
                    idlist = p.getIds();
                    for (IdNode id : idlist) {
                        t = id.getTable().lookUp(id.getId()).getType();
                        if (id != idlist.getLast()) {
                            mainProgram.append(t.Ctype(t) + " " + id.getId() + ",");
                        } else if (id == idlist.getLast()) {
                            mainProgram.append(t.Ctype(t) + " " + id.getId() + "){\n");
                        }
                    }
                }
            }
            if(node.getProcBody().accept(this) == null) {
            }

            else {
                mainProgram.append(node.getProcBody().accept(this));
            }
        }


        else{
            String type = node.getReturns().getFirst().getType().toCtype();
            mainProgram.append(type + " " + node.getFun_name().getId() + " " + "(");
            GenericType t;

            if(node.getPars() == null) {
                mainProgram.append("){\n");
            }

            if(node.getPars() != null) {

                LinkedList<IdNode> ids = new LinkedList<>();
                for(ParDeclOp param : node.getPars()){
                    for(int i = param.getIds().size()-1; i >= 0; i--) {
                        ids.add(param.getIds().get(i));
                    }
                }

                for (IdNode id : ids) {
                    t = id.getTable().lookUp(id.getId()).getType();
                    if (id != ids.getLast()) {
                        mainProgram.append(t.toCtype() + " " + id.getId() + ",");
                    } else if (id == ids.getLast()) {
                        mainProgram.append(t.toCtype() + " " + id.getId() + "){\n");
                    }
                }

            }

            if(node.getProcBody().accept(this) == null) {
            }

            else {
                mainProgram.append(node.getProcBody().accept(this));
            }
        }
        return null;
    }

    @Override
    public StringBuilder Visit(GenericType node) {
        return null;
    }

    @Override
    public StringBuilder Visit(IdListInitOp node) {
        GenericType t =  node.getIds().getFirst().getType();
        ExprNode e;
        LinkedList<IdNode> ids = new LinkedList<IdNode>();


        for(int i = node.getIds().size()-1; i >= 0; i--) {
            ids.add(node.getIds().get(i));
        }

        int i = node.getExprs().size() - 1;
        while (i >= 0) {
            try {
                if (node.getExprs().get(i) == null) {

                    if(ids.get(i).getType().getClass().getSimpleName().equals(StringType.class.getSimpleName())){
                        mainProgram.append("\t" + ids.get(i).getType().toCtype() + "* " + ids.get(i).getId() + "" + ";\n");
                        i--;
                    }

                    else {
                        mainProgram.append("\t" + ids.get(i).getType().Ctype(t) + " " + ids.get(i).getId() + ";\n");
                        i--;
                    }
                }

                if(node.getExprs().get(i).getClass().equals(BooleanConst.class)){
                    e = node.getExprs().get(i);
                    if(e.toConst().getArgument().equals("FALSE")){
                        mainProgram.append("\t" + ids.get(i).getType().toCtype() + " " + ids.get(i).getId() + " = 0" + ";\n");
                    }
                    if(e.toConst().getArgument().equals("TRUE")) {
                        mainProgram.append("\t" + ids.get(i).getType().toCtype() + " " + ids.get(i).getId() + " = 1" + ";\n");
                    }
                    i--;
                }

                else if (node.getExprs().get(i).toId(node.getExprs().get(i)) != null) {
                    e = node.getExprs().get(i);
                    mainProgram.append("\t" + ids.get(i).getType().Ctype(t) + " " + ids.get(i).getId() + " = " + e.toId(e).getId() + ";\n");
                    i--;
                } else if (node.getExprs().get(i).toConst().getClass().equals(StringConst.class)) {
                    e = node.getExprs().get(i);
                    e.setType(new StringType());
                    mainProgram.append("\t" + ids.get(i).getType().toCtype() + " " + ids.get(i).getId() + "[] = " + e.toConst().getArgument() + ";\n");
                    i--;
                } else {
                    e = node.getExprs().get(i);
                    mainProgram.append("\t" + ids.get(i).getType().Ctype(t) + " " + ids.get(i).getId() + " = " + e.toConst().getArgument() + ";\n");
                    i--;
                }

            }catch(Exception ex) {}
        }
        return null;
    }

    @Override
    public StringBuilder Visit(ResultTypeOp node) {
        return null;
    }

    @Override
    public StringBuilder Visit(ReturnExprsOp node) {
        return null;
    }

    @Override
    public StringBuilder Visit(ParDeclOp node) {
        return null;
    }

    @Override
    public StringBuilder Visit(IdNode node) {
        return null;
    }

    @Override
    public StringBuilder Visit(StatNode node) {
        return null;
    }

    @Override
    public StringBuilder Visit(IfStatOp node) {
        mainProgram.append("if(" + node.getExpr().accept(this) +"){\n");

        node.getBody().accept(this);


        mainProgram.append("}\n");

        if(node.getElif_list() != null) {
            for(ElifOp e : node.getElif_list()) {
                e.accept(this);
            }
        }

        if(node.getElse() != null) {
            mainProgram.append("else { \n");
            node.getElse().getBody().accept(this);
            mainProgram.append("}\n");
        }

        return null;
    }

    @Override
    public StringBuilder Visit(ElifOp node) {

        mainProgram.append("else if(" + node.getExpr().accept(this) + "){\n");

        for(StatNode s : node.getBody().getStats()) {
            s.accept(this);
        }

        mainProgram.append("}\n");
        return null;
    }

    @Override
    public StringBuilder Visit(ElseNode node) {
        node.getBody().accept(this);
        return null;
    }

    @Override
    public StringBuilder Visit(WhileOp node) {
        BinaryExprNode expr = (BinaryExprNode) node.getExpr();

        if(node.getExpr() != null) {
            if(node.getBody() != null) {
                node.getBody().accept(this);
            }
        }

        mainProgram.append("\nwhile (" + expr.accept(this) + ") {\n");




        if(node.getBody2() != null) {

            node.getBody2().accept(this);
        }

        if(node.getExpr() != null) {
            if(node.getBody() != null) {
                node.getBody().accept(this);
            }
        }
        mainProgram.append("\n}\n");
        return null;
    }

    @Override
    public StringBuilder Visit(ReadOp node) {

        String format = "";

        for(IdNode i : node.getIds()) {

            i.setType(i.getTable().lookUp(i.getId()).getType());
            try {
                if (i.getType().getClass().getSimpleName().equals(StringType.class.getSimpleName())) {
                    format = "%s";
                    mainProgram.append("\tscanf(\"" + format + "\", &" + i.getId() + ");\n");
                }
                if (i.getType().getClass().getSimpleName().equals(IntegerType.class.getSimpleName())) {
                    format = "%d";
                    mainProgram.append("\tscanf(\"" + format + "\", " + "&" + i.getId() + ");\n");
                }

                if (i.getType().getClass().getSimpleName().equals(FloatType.class.getSimpleName())) {
                    format = "%f";
                    mainProgram.append("\tscanf(\"" + format + "\", " + "&" + i.getId() + ");\n");
                }

                if (i.getType().getClass().getSimpleName().equals(BoolType.class.getSimpleName())) {
                    format = "%d";
                    mainProgram.append("\tscanf(\"" + format + "\", " + "&" + i.getId() + ");\n");
                }
            }catch(Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public StringBuilder Visit(WriteOp node) {

        String format = "";
        for(ExprNode e : node.getExprs()) {
            try {
                if (e.toConst() != null) {
                    if(e.toConst().getArgument().contains("\n")) {
                        String str = e.toConst().getArgument().replace("\n", "\\n");
                        mainProgram.append("\tprintf(" + str + ");\n");
                    }
                    else {
                        mainProgram.append("\tprintf(" + e.toConst().getArgument() + ");\n");
                    }
                }

                if (e.toId(e) != null) {
                    if(structTable.lookUp(e.toId(e).getId()) != null) {
                        String tipo2 = e.toId(e).getType().toCtype();;
                        String tipo = structTable.lookUp(e.toId(e).getId()).getType().toCtype();
                        String index = structTable.lookUp(e.toId(e).getId()).getKind();

                        if(tipo.equals("int") || tipo2.equals("int")){
                            format = "%d";
                            mainProgram.append("\tprintf(\"" + format + "\"," + "toReturn.x"+index+");\n");
                        }
                        if(tipo.equals("float") || tipo2.equals("float")){
                            format = "%f";
                            mainProgram.append("\tprintf(\"" + format + "\"," + "toReturn.x"+index+");\n");
                        }
                        if(tipo.equals("string") || tipo.equals("char") || tipo2.equals("string") || tipo2.equals("char")){
                            format = "%s";
                            mainProgram.append("\tprintf(\"" + format + "\"," + "&toReturn.x"+index+");\n");
                        }

                        //mainProgram.append("\tprintf(\"" + format + "\"," + "toReturn.x"+index+");\n");
                    }


                    if(temp.lookUp(e.toId(e).getId()) != null) {

                        String tipo = temp.lookUp(e.toId(e).getId()).getType().toCtype();
                        if(tipo.equals("int")){
                            format = "%d";
                        }
                        if(tipo.equals("float")){
                            format = "%f";
                        }
                        if(tipo.equals("string")){
                            format = "%s";
                        }
                        String index = temp.lookUp(e.toId(e).getId()).getKind();
                        mainProgram.append("\tprintf(\"" + format + "\"," + "array[" + index +"]);\n");
                    }

                    if(temp.lookUp(e.toId(e).getId()) == null && structTable.lookUp(e.toId(e).getId()) == null) {
                        if (e.getType().getClass().getSimpleName().equals(StringType.class.getSimpleName())) {
                            format = "%s";
                            mainProgram.append("\tprintf(\"" + format + "\", &" + e.toId(e).getId() + ");\n");
                        }

                        if (e.getType().getClass().getSimpleName().equals(IntegerType.class.getSimpleName())) {
                            format = "%d";
                            mainProgram.append("\tprintf(\"" + format + "\", " + e.toId(e).getId() + ");\n");
                        }

                        if (e.getType().getClass().getSimpleName().equals(FloatType.class.getSimpleName())) {
                            format = "%f";
                            mainProgram.append("\tprintf(\"" + format + "\", " + e.toId(e).getId() + ");\n");
                        }

                        if (e.getType().getClass().getSimpleName().equals(BoolType.class.getSimpleName())) {
                            format = "%d";
                            mainProgram.append("\tprintf(\"" + format + "\", " + e.toId(e).getId() + ");\n");
                        }
                    }
                }

                if(e.getClass().getSimpleName().equals(ExprCallProc.class.getSimpleName())) {
                    ExprCallProc expr = (ExprCallProc) e;
                    LinkedList<ExprNode> exprs = expr.getExprs();
                    GenericType t = expr.getTable().lookUp(expr.getId().getId()).getType();
                    if(t.getClass().getSimpleName().equals(IntegerType.class.getSimpleName())) {
                        format = "%d";
                    }
                    if(t.getClass().getSimpleName().equals(FloatType.class.getSimpleName())) {
                        format = "%f";
                    }
                    mainProgram.append("\tprintf(\"" + format + "\", " + expr.getId().getId() + "(");

                    if(exprs!= null) {
                        for(ExprNode ex : exprs) {
                            if(ex != exprs.getLast()) {
                                if (ex.toId(ex) != null) {
                                    mainProgram.append(ex.toId(ex).getId() + ", ");
                                }
                                if (ex.toConst() != null) {
                                    mainProgram.append(ex.toConst().getArgument() + ", ");
                                }
                                else {
                                    try{
                                        ArithmeticExpr arit = (ArithmeticExpr) ex;
                                        mainProgram.append(printOperandi(arit) + ", ");
                                    }
                                    catch(Exception exception) {    }

                                }
                            }
                            else {
                                if (ex.toId(ex) != null) {
                                    mainProgram.append(ex.toId(ex).getId());
                                }
                                if (ex.toConst() != null) {
                                    mainProgram.append(ex.toConst().getArgument());
                                }
                                else {
                                    try{
                                        ArithmeticExpr arit = (ArithmeticExpr) ex;
                                        mainProgram.append(printOperandi(arit));
                                    }
                                    catch(Exception exception) {    }
                                }
                            }
                        }
                    }
                    mainProgram.append("));\n");
                }
            }catch(Exception exce) {   }
        }

        return null;
    }



    public void assignCall(AssignOp node) {

        ExprCallProc call = (ExprCallProc) node.getExprs().getFirst();

        String tipo = node.getIds().getFirst().getType().Ctype(node.getIds().getFirst().getType());
        LinkedList<IdNode> listaId = new LinkedList<IdNode>();
        mainProgram.append("\t" + tipo + "* array = " + call.getId().getId() + "(");
        if(call.getExprs().size() != 0) {
            for(ExprNode e : call.getExprs()){
                if(e != call.getExprs().getLast()) {
                    if (e.toId(e) != null) {
                        mainProgram.append(e.toId(e).getId() + ", ");
                    }
                    if(e.toConst() != null) {
                        mainProgram.append(e.toConst().getArgument() + ", ");
                    }
                }
                else {
                    if (e.toId(e) != null) {
                        mainProgram.append(e.toId(e).getId());
                    }
                    if(e.toConst() != null) {
                        mainProgram.append(e.toConst().getArgument());
                    }
                }
            }
        }
        mainProgram.append(")");
        mainProgram.append(";\n");

        for(int i = node.getIds().size() - 1; i >= 0; i--) {
            listaId.add(node.getIds().get(i));
        }
        for(int x = 0; x < listaId.size(); x++) {
            String num = String.valueOf(x);
            Item tempItem = new Item(listaId.get(x).getType(), num, listaId.get(x).getId());
            temp.addIdentifier(tempItem);
        }
    }


    public void assignDifferentCall(AssignOp node) {


        ExprCallProc call = (ExprCallProc) node.getExprs().getFirst();
        LinkedList<IdNode> listaId = new LinkedList<IdNode>();
        mainProgram.append("\tstruct ret"+count + " toReturn = " + call.getId().getId() + "(");
        if(call.getExprs().size() != 0) {
            for(ExprNode e : call.getExprs()){
                if(e != call.getExprs().getLast()) {
                    if (e.toId(e) != null) {
                        mainProgram.append(e.toId(e).getId() + ", ");
                    }
                    if(e.toConst() != null) {
                        mainProgram.append(e.toConst().getArgument() + ", ");
                    }
                }
                else {
                    if (e.toId(e) != null) {
                        mainProgram.append(e.toId(e).getId());
                    }
                    if(e.toConst() != null) {
                        mainProgram.append(e.toConst().getArgument());
                    }
                }
            }
        }
        mainProgram.append(")");
        mainProgram.append(";\n");


        for(int i = node.getIds().size() - 1; i >= 0; i--) {
            listaId.add(node.getIds().get(i));
        }

        for(int x = 0; x < listaId.size(); x++) {
            String num = String.valueOf(x+1);
            Item tempItem = new Item(listaId.get(x).getType(), num, listaId.get(x).getId());
            structTable.addIdentifier(tempItem);
        }


    }


    @Override
    public StringBuilder Visit(AssignOp node) {

        if(node.getIds().size() == 1) {
            for (IdNode i : node.getIds()) {
                if (i != null) {
                    mainProgram.append(i.getId() + " = ");
                }
            }
        }

        if(node.getExprs().size() == 1 && flag && multipleDifferentReturns){
            try{
                assignDifferentCall(node);
                return null;
            }catch(Exception ex){   }
        }

        if(node.getExprs().size() == 1 && multipleReturns) {
            if(node.getExprs().getFirst().getClass().equals(ExprCallProc.class)){
                assignCall(node);
                return null;
            }
        }

        if(node.getIds().size() > 1) {
            LinkedList<ExprNode> exprs = node.getExprs();
            LinkedList<IdNode> ids = new LinkedList<IdNode>();

            for(int x = node.getIds().size() - 1; x >= 0; x--) {
                ids.add(node.getIds().get(x));
            }


            for (int i = 0; i < ids.size(); i++) {

                mainProgram.append("\t" + ids.get(i).getId() + " = ");
                if(exprs.size() > 1) {
                    if (exprs.get(i).accept(this) == null) {
                    } else {
                        mainProgram.append(exprs.get(i).accept(this)+";\n");
                    }
                    mainProgram.append(";\n");
                }


                if(exprs.size() == 1) {
                    if(!exprs.getFirst().getClass().equals(ExprCallProc.class)) {
                        exprs.getFirst().accept(this);
                    }
                    mainProgram.append(";\n");
                }
            }
            return null;
        }


        for(ExprNode e : node.getExprs()) {

            if(e != null) {

                if(e.toId(e) != null){
                    mainProgram.append(e.toId(e).getId());

                }
                if(e.toConst() != null) {
                    mainProgram.append(e.toConst().getArgument());
                }

                if(e.accept(this) == null) {

                }
                else {
                    mainProgram.append(e.accept(this));
                }
            }
        }
        mainProgram.append(";\n");


        return null;
    }

    @Override
    public StringBuilder Visit(CallProcOp node) {
        mainProgram.append(node.getId().getId() + "(");

        if(node.getExprs().isEmpty()){
            mainProgram.append(");\n");
        }

        else if(node.getExprs() != null) {
            for (ExprNode e : node.getExprs()) {

                if (node.getExprs().getLast() != e) {
                    if (e.toConst() != null) {
                        mainProgram.append(e.toConst().getArgument() + ", ");
                    }

                    if (e.toId(e) != null) {
                        mainProgram.append(e.toId(e).getId() + ", ");
                    } else if (e.toId(e) == null && e.toConst() == null) {
                        mainProgram.append(/*e.accept(this)*/ printOperandi((ArithmeticExpr) e)+ ", ");
                    }
                } else {
                    if (e.toConst() != null) {
                        mainProgram.append(e.toConst().getArgument() + ");\n");
                    }
                    if (e.toId(e) != null) {
                        mainProgram.append(e.toId(e).getId() + ");\n");
                    } else if (e.toId(e) == null && e.toConst() == null) {
                        mainProgram.append(e.accept(this) + ");\n");
                    }
                }
            }
        }


        return null;
    }

    @Override
    public StringBuilder Visit(AddOp node) {

        if(node.getLeft().getClass().getSimpleName().equals(ExprCallProc.class.getSimpleName())) {
            ExprCallProc e = (ExprCallProc) node.getLeft();
            if(e.accept(this) == null) {

            }
            else mainProgram.append(e.accept(this));
        }
        if (node.getLeft().toConst() != null) {
            mainProgram.append(node.getLeft().toConst().getArgument());
        }
        if (node.getLeft().toId(node.getLeft()) != null) {
            mainProgram.append(node.getLeft().toId(node.getLeft()).getId());
        }
        mainProgram.append(" + ");


        if(node.getRight().getClass().getSimpleName().equals(ExprCallProc.class.getSimpleName())) {
            ExprCallProc e = (ExprCallProc) node.getRight();

            if(e.accept(this) == null) {

            }
            else {
                mainProgram.append(e.accept(this));
            }
        }
        if (node.getRight().toConst() != null) {
            mainProgram.append(node.getRight().toConst().getArgument());
        }

        if (node.getRight().toId(node.getRight()) != null) {
            mainProgram.append(node.getRight().toId(node.getRight()).getId());
        }

        return null;
    }

    @Override
    public StringBuilder Visit(AndOp node) {
        return null;
    }

    @Override
    public StringBuilder Visit(OrOp node) {
        return null;
    }

    @Override
    public StringBuilder Visit(NotOp node) {
        //mainProgram.append(node.getLink());
        return null;
    }

    @Override
    public StringBuilder Visit(DivOp node) {

        if(node.getLeft().getClass().getSimpleName().equals(ExprCallProc.class.getSimpleName())) {
            node.accept(this);
        }
        if (node.getLeft().toConst() != null) {
            mainProgram.append(node.getLeft().toConst().getArgument());
        }
        if (node.getLeft().toId(node.getLeft()) != null) {
            mainProgram.append(node.getLeft().toId(node.getLeft()).getId());
        }
        mainProgram.append(" / ");


        if(node.getRight().getClass().getSimpleName().equals(ExprCallProc.class.getSimpleName())) {
            ExprCallProc e = (ExprCallProc) node.getRight();

            if(e.accept(this) == null) {

            }
            else {
                mainProgram.append(e.accept(this));
            }
        }
        if (node.getRight().toConst() != null) {
            mainProgram.append(node.getRight().toConst().getArgument());
        }

        if (node.getRight().toId(node.getRight()) != null) {
            mainProgram.append(node.getRight().toId(node.getRight()).getId());
        }

        return null;
    }


    public void aritCheck(ExprNode node1, ExprNode node2) {

        if(node1.toId(node1) != null) {
            mainProgram.append(node1.toId(node1).getId());
        }

        if(node1.toConst() != null) {
            mainProgram.append(node1.toConst().getArgument());
        }

        mainProgram.append(" op ");

        if(node2.toId(node2) != null) {
            mainProgram.append(node2.toId(node2).getId());
        }

        if(node2.toConst() != null) {
            mainProgram.append(node2.toConst().getArgument());
        }

        mainProgram.append(");");

    }


    public StringBuilder binaryCheck(BinaryExprNode node, String operation) {
        StringBuilder sb = new StringBuilder();

        if(node.getLeft().toId(node.getLeft()) != null) {
            sb.append(node.getLeft().toId(node.getLeft()).getId() + " " + operation + " ");
        }

        if(node.getRight().toId(node.getLeft()) == null){
            ConstNode x = node.getLeft().toConst();
            sb.append(x.getArgument() +  " " + operation + " ");
        }


        if(node.getRight().toId(node.getRight()) != null) {
            sb.append(node.getRight().toId(node.getRight()).getId());
        }

        if(node.getRight().toId(node.getRight()) == null){
            ConstNode x = node.getRight().toConst();
            sb.append(x.getArgument());
        }
        return sb;
    }

    @Override
    public StringBuilder Visit(EqOp node) {
        return binaryCheck(node, "==");
    }

    @Override
    public StringBuilder Visit(GeOp node) {

        return binaryCheck(node, ">=");
    }

    @Override
    public StringBuilder Visit(SubOp node) {

        if(node.getLeft().getClass().getSimpleName().equals(ExprCallProc.class.getSimpleName())) {
            node.accept(this);
        }
        if (node.getLeft().toConst() != null) {
            mainProgram.append(node.getLeft().toConst().getArgument());
        }
        if (node.getLeft().toId(node.getLeft()) != null) {
            mainProgram.append(node.getLeft().toId(node.getLeft()).getId());
        }
        mainProgram.append(" - ");


        if(node.getRight().getClass().getSimpleName().equals(ExprCallProc.class.getSimpleName())) {
            ExprCallProc e = (ExprCallProc) node.getRight();
            if(e.accept(this) == null) {

            }
            else {
                mainProgram.append(e.accept(this));
            }
        }

        if (node.getRight().toConst() != null) {
            mainProgram.append(node.getRight().toConst().getArgument());
        }

        if (node.getRight().toId(node.getRight()) != null) {
            mainProgram.append(node.getRight().toId(node.getRight()).getId());
        }

        return null;
    }

    @Override
    public StringBuilder Visit(GtOp node) {
       return binaryCheck(node, ">");
    }

    @Override
    public StringBuilder Visit(LeOp node) {
        return binaryCheck(node, "<=");
    }

    @Override
    public StringBuilder Visit(LtOp node) {
        return binaryCheck(node, "<");
    }

    @Override
    public StringBuilder Visit(MultOp node) {
            if(node.getLeft().getClass().getSimpleName().equals(ExprCallProc.class.getSimpleName())) {
                node.accept(this);
            }
            if (node.getLeft().toConst() != null) {
                mainProgram.append(node.getLeft().toConst().getArgument());
            }
            if (node.getLeft().toId(node.getLeft()) != null) {
                mainProgram.append(node.getLeft().toId(node.getLeft()).getId());
            }
            mainProgram.append(" * ");


            if(node.getRight().getClass().getSimpleName().equals(ExprCallProc.class.getSimpleName())) {
                ExprCallProc e = (ExprCallProc) node.getRight();

                if(e.accept(this) == null) {

                }
                else {
                    mainProgram.append(e.accept(this));
                }
            }
            if (node.getRight().toConst() != null) {
                mainProgram.append(node.getRight().toConst().getArgument());
            }

            if (node.getRight().toId(node.getRight()) != null) {
                mainProgram.append(node.getRight().toId(node.getRight()).getId());
            }

        return null;
    }

    @Override
    public StringBuilder Visit(NeOp node) {
        return binaryCheck(node, "!=");
    }

    @Override
    public StringBuilder Visit(BinaryExprNode node) {
        return null;
    }

    @Override
    public StringBuilder Visit(ArithmeticExpr node) {
        return null;
    }

    @Override
    public StringBuilder Visit(BooleanConst node) {
        return null;
    }

    @Override
    public StringBuilder Visit(ConstNode node) {
        return null;
    }

    @Override
    public StringBuilder Visit(FloatConst node) {
        return null;
    }

    @Override
    public StringBuilder Visit(NullConst node) {
        return null;
    }

    @Override
    public StringBuilder Visit(StringConst node) {
        return null;
    }

    @Override
    public StringBuilder Visit(ProcBodyOp node) {


        if(node.getVarDecls() != null) {
            for(VarDeclOp v : node.getVarDecls()) {
                v.accept(this);
            }
        }



        if(node.getStats() != null) {
            for(StatNode s : node.getStats()) {
                s.accept(this);
            }
        }

        if(node.getEs().size() > 1 && flag) {
            mainProgram.append("\tstruct ret"+count+" toReturn;\n");
            for(int x = 1; x <= node.getEs().size(); x++) {
                mainProgram.append("\ttoReturn."+"x"+ x + " = " + node.getEs().get(x-1).toId(node.getEs().get(x-1)).getId() + ";\n");
            }
            mainProgram.append("\treturn toReturn;\n");

        }


        if(node.getEs().size() > 1 && !flag) {
            multipleReturns = true;
            String t = "";
            t = node.getEs().getFirst().getType().Ctype(node.getEs().getFirst().getType());
            mainProgram.append("\t"+ t + " array[" + node.getEs().size() + "] = {");
            for(ExprNode e : node.getEs()) {
                if(e != node.getEs().getLast()) {
                    if(e.toId(e) != null){
                        mainProgram.append(e.toId(e).getId() + ", ");
                    }
                    if(e.toConst() != null){
                        mainProgram.append(e.toConst().getArgument() + ", ");
                    }
                }
                else{
                    if(e.toId(e) != null){
                        mainProgram.append(e.toId(e).getId() + "};\n");
                    }
                    if(e.toConst() != null){
                        mainProgram.append(e.toConst().getArgument() + "};\n");
                    }
                }
            }
            mainProgram.append("\treturn array;\n");
        }


        if(node.getEs() != null) {
            if(node.getEs().size() == 1) {
                for(ExprNode e : node.getEs()) {
                    if(e.getClass().getSimpleName().equals(NullConst.class.getSimpleName())) {
                        continue;
                    }

                    if(e.toId(e) != null) {
                        mainProgram.append("\treturn " + e.toId(e).getId() + ";\n");
                    }

                    if(e != null) {

                        try{
                            if(e.getClass().equals(NotOp.class)){
                                if(((NotOp) e).getLink().toId(((NotOp) e).getLink()) != null) {
                                    mainProgram.append("\treturn !" + ((NotOp) e).getLink().toId(((NotOp) e).getLink()).getId() + ";\n");
                                }
                            }
                            if(e.getClass().equals(MinusOp.class)){
                                if(((MinusOp) e).getLink().toId(((MinusOp) e).getLink()) != null) {
                                    mainProgram.append("\treturn -" + ((MinusOp) e).getLink().toId(((MinusOp) e).getLink()).getId() + ";\n");
                                }
                            }
                        }
                        catch(Exception ex) {}

                        try {
                            BinaryExprNode bin = (BinaryExprNode) e;
                            if(e.getClass().equals(AndOp.class)){
                                mainProgram.append("\treturn " + binaryCheck(bin, "&&") + ";\n");
                            }

                            if(e.getClass().equals(OrOp.class)){
                                mainProgram.append("\treturn " + binaryCheck(bin, "||") + ";\n");
                            }

                        }catch(Exception excpet) {}
                    }




                    else {
                        try {
                            mainProgram.append("\treturn " + printOperandi((ArithmeticExpr) e) + ";\n");

                        } catch(Exception ex){ mainProgram.append("\treturn " + e.accept(this) + ";\n");  }
                    }
                }
            }




        }


        mainProgram.append("}\n");
        return null;
    }

    public StringBuilder printOperandi(ArithmeticExpr e) {
        StringBuilder s = new StringBuilder();
        ExprNode e1 = e.getLeft();
        ExprNode e2 = e.getRight();
        String op = "";

        if(e.getClass().equals(AddOp.class)) {
            op = "+";
        }
        if(e.getClass().equals(SubOp.class)) {
            op = "-";
        }
        if(e.getClass().equals(MultOp.class)){
            op = "*";
        }
        if(e.getClass().equals(DivOp.class)) {
            op = "/";
        }

        if(e1.toId(e1) != null) {
            s.append(e1.toId(e1).getId() + " " + op + " ");
        }
        else{
            if(e1.toConst() != null) {
                s.append(e1.toConst().getArgument() + " " + op + " ");
            }
        }

        if(e2.toId(e2) != null) {
            s.append(e2.toId(e2).getId());
        }
        else{
            if(e2.toConst() != null) {
                s.append(e2.toConst().getArgument());
            }
        }
        return s;
    }


    public StringBuilder senzaParentesi(ProcBodyOp node) {      //Usato solo per il main
        if(node.getVarDecls() != null) {
            for(VarDeclOp v : node.getVarDecls()) {
                v.accept(this);
            }
        }

        if(node.getStats() != null) {
            for(StatNode s : node.getStats()) {
                s.accept(this);
            }
        }

        if(node.getEs() != null) {
            for(ExprNode e : node.getEs()) {
                if(e.getClass().getSimpleName().equals(NullConst.class.getSimpleName())) {
                    continue;
                }
                else {
                    mainProgram.append("\treturn " + e.accept(this) + ";\n}\n");
                }
            }
        }
        return null;
    }

    @Override
    public StringBuilder Visit(BodyOp node) {
        if(node.getStats() != null) {
            for(StatNode s : node.getStats()) {
                if(s.getClass() != null) {
                    s.accept(this);
                }

            }
        }
        return null;
    }

    @Override
    public StringBuilder Visit(BodyOpElif node) {
        return null;
    }

    @Override
    public StringBuilder Visit(BoolType node) {
        return null;
    }

    @Override
    public StringBuilder Visit(FloatType node) {
        return null;
    }

    @Override
    public StringBuilder Visit(IntegerType node) {
        return null;
    }

    @Override
    public StringBuilder Visit(StringType node) {
        return null;
    }

    @Override
    public StringBuilder Visit(MinusOp node) {
        return null;
    }

    @Override
    public StringBuilder Visit(UnaryExprNode node) {
        return null;
    }

    @Override
    public StringBuilder Visit(ExprCallProc node) {
        mainProgram.append(node.getId().getId() + "(");
        if(node.getExprs() != null) {
            for(ExprNode e : node.getExprs()){

                if(node.getExprs().getLast() != e) {

                    if(e.toId(e) != null) {
                        mainProgram.append(e.toId(e).getId() + ",");
                    }

                    else{
                        mainProgram.append(e.toConst().getArgument() + ",");
                    }

                }
                else {

                    if(e.toId(e) != null) {
                        mainProgram.append(e.toId(e).getId());
                    }
                    else if(e.toConst() != null){
                        mainProgram.append(e.toConst().getArgument());
                    }
                    else {
                        if(e.accept(this) == null) {

                        }
                        else mainProgram.append(e.accept(this));
                    }
                }
            }
        }
        mainProgram.append(")");
        return null;
    }
}
