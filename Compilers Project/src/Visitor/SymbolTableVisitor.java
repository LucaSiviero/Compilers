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
import SymbolTable.StackSymbolTable;
import SymbolTable.SymbolTable;
import SymbolTable.Item;
import sun.awt.Symbol;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.logging.Logger;
    /*Per stampare la tabella rimuovere i commenti da VarDeclOp e ProcOp*/
public class SymbolTableVisitor implements VisitorInterface<Void>{
    Logger log = Logger.getGlobal();
    String errors = "";
    StackSymbolTable stackTable;
    SymbolTable parsTable = new SymbolTable(null);
    int count = 0;

    public String generateSymbolTables(ProgramOp node) {
        SymbolTable initialTable = new SymbolTable(null);
        node.setTable(initialTable);
        Visit(node);
        return errors;
    }

    @Override
    public Void Visit(ForOp node) {
        stackTable.enterScope();
        SymbolTable tmp = stackTable.peek();
        Item i1 = new Item(node.getVarDecl().getType(), "index of for", node.getVarDecl().getIds().getIds().getFirst().getId());
        tmp.addIdentifier(i1);
        node.getBody().accept(this);
        stackTable.exitScope();
        return null;
    }

    @Override
    public Void Visit(ASTNode node) {
        return null;
    }

    @Override
    public Void Visit(ProgramOp node) {
        stackTable = new StackSymbolTable(node.getTable());
        if(node.getProcs() != null) {
            for (ProcOp p : node.getProcs()) {
                if(stackTable.lookUp(p.getFun_name().getId()) != null) {
                    System.err.println("Dichiarazione già presente");
                }
                Item i = new Item(p.getReturns().getFirst().getType(), "method", p.getFun_name().getId());
                stackTable.addIdentifier(i);
                if(p.getPars() != null) {

                    for(ParDeclOp par : p.getPars()) {
                        for(IdNode id: par.getIds()) {
                            count += 1;
                        }
                        parsTable.addIdentifier(new Item(new GenericType(), "" + count, p.getFun_name().getId()));
                        count = 0;
                    }
                }
            }
        }

            if(node.getVarDecls() != null) {
                for (VarDeclOp v : node.getVarDecls()) {
                    v.accept(this);
                    for(IdNode i : v.getIds().getIds()){
                        Item item = new Item(v.getType(), "var", i.getId());
                        stackTable.addIdentifier(item);
                    }
                }
            }
            if(node.getProcs() != null) {
                for (ProcOp p : node.getProcs()) {
                    p.accept(this);
                }
            }

        return null;
    }

    @Override
    public Void Visit(VarDeclOp node) {
        node.setTable(stackTable.peek());

        IdListInitOp ids = node.getIds();
        ids.accept(this);
        for (IdNode i : ids.getIds()) {
            i.setTable(stackTable.peek());
            i.setType(node.getType());
            Item item = new Item(node.getType(), "var", i.getId());
            if(node.getTable().lookUp(item.getName()) != null) {
                errors += "\nDichiarazione multipla per l'identificatore " + i.getId();
                System.err.println(errors);
                return null;
            }

            else {
                node.getTable().addIdentifier(item);
            }
        }
        return null;
    }

    @Override
    public Void Visit(IntegerConst node) {
        return null;
    }

    @Override
    public Void Visit(ProcOp node) {
        node.setTable(stackTable.peek());
        stackTable.enterScope();
        SymbolTable t = stackTable.peek();
        Item procItem = new Item(node.getReturns().getFirst().getType(), "method", node.getFun_name().getId());
        t.addIdentifier(procItem);
        addFunctionPars(node, t);
        node.getProcBody().accept(this);
        stackTable.exitScope();

        return null;
    }


    public void addFunctionPars(ProcOp node, SymbolTable t){
        if(node.getPars() != null) {
            for (ParDeclOp p : node.getPars()) {
                for (IdNode i : p.getIds()) {
                    i.setTable(t);
                    Item item = new Item(p.getType(), "param in " + node.getFun_name().getId(), i.getId());
                    t.addIdentifier(item);
                }
            }
        }
    }


    @Override
    public Void Visit(GenericType node) {
        return null;
    }

    @Override
    public Void Visit(IdListInitOp node) {
        return null;
    }

    @Override
    public Void Visit(ResultTypeOp node) {
        return null;
    }

    @Override
    public Void Visit(ReturnExprsOp node) {
        return null;
    }

    @Override
    public Void Visit(ParDeclOp node) {
        return null;
    }

    @Override
    public Void Visit(IdNode node) {
        if(node.getTable() != null) {
            node.setTable(stackTable.peek());
        }
        else{
            errors+= "\nL'identificatore " + node.getId() + " non è stato dichiarato prima del suo utilizzo\n";
            System.err.println(errors);
        }
        return null;
    }

    @Override
    public Void Visit(StatNode node) {
        node.accept(this);
        return null;
    }

    @Override
    public Void Visit(IfStatOp node) {
        node.getExpr().accept(this);
        node.getBody().accept(this);
        if(node.getBody().getStats() != null) {
            for(StatNode s : node.getBody().getStats()) {
                s.accept(this);
            }
        }
        if(node.getElse() != null) {
            if(node.getElse().getBody() != null) {
                node.getElse().getBody().accept(this);
            }
        }

        if(node.getElif_list() != null) {
            for(ElifOp elif : node.getElif_list()){
                elif.getExpr().accept(this);
                elif.getBody().accept(this);
            }
        }
        return null;
    }

    @Override
    public Void Visit(ElifOp node) {
        node.getExpr().accept(this);
        if(node.getBody().getStats() != null) {
            for(StatNode s : node.getBody().getStats()) {
                s.accept(this);
            }
        }
        return null;
    }

    @Override
    public Void Visit(ElseNode node) {
        node.getBody().accept(this);
        return null;
    }

    @Override
    public Void Visit(WhileOp node) {
        node.getExpr().accept(this);
        if(node.getBody() != null) {
            node.getBody().accept(this);
        }
        if(node.getBody2() != null) {
            node.getBody2().accept(this);
        }
        //System.out.println(node.getExpr().accept(this));
        return null;
    }


    public Void accept(ExprNode node) {
        if(node.toId(node) != null) {

        }
        return null;
    }

    @Override
    public Void Visit(ReadOp node) {
        try {
            for (IdNode i : node.getIds()) {
                i.setTable(stackTable.peek());
                i.setType(stackTable.lookUp(i.getId()).getType());
            }
        }
        catch(Exception e) {}
        return null;
    }



    @Override
    public Void Visit(WriteOp node) {

        for(ExprNode e : node.getExprs()) {
            if(e.toId(e) != null){
                try {
                    String x = e.toId(e).getId();
                    e.setType(stackTable.lookUp(x).getType());
                }catch(Exception exc) {   }
            }
            try {
                ExprCallProc expr = (ExprCallProc) e;
                expr.setTable(stackTable.peek());

            }
            catch(Exception ex) {    }
        }

        return null;
    }

    @Override
    public Void Visit(AssignOp node) {
        if(node.getIds() != null) {
            for(IdNode i : node.getIds()){
                try {
                    if (stackTable.lookUp(i.getId()) == null) {
                        errors += "\nDichiarazione mancante per l'id " + i.getId() + "\n";
                        System.err.println(errors);
                    }
                    else {
                        i.setTable(stackTable.peek());
                    }
                }
                catch(Exception ex) {}
            }
        }
        if(node.getExprs() != null) {
            int i = 0;
            while(i < node.getExprs().size()) {
                if(node.getExprs().get(i).toId(node.getExprs().get(i)) != null) {
                    IdNode id = node.getExprs().get(i).toId(node.getExprs().get(i));
                    if(stackTable.lookUp(id.getId()) == null) {
                        errors += "\nDichiarazione mancante per l'id " + id.getId() + "\n";
                        System.err.println(errors);
                        return null;
                    }
                }
                if(node.getExprs().get(i) != null && node.getExprs().get(i).getClass().getSimpleName().equals(IdNode.class.getSimpleName())) {
                    node.getExprs().get(i).setTable(stackTable.peek());
                    i++;
                }
                else if(node.getExprs().get(i) != null && node.getExprs().get(i).getClass().getSimpleName().equals(ExprCallProc.class.getSimpleName())){

                    ExprCallProc call = (ExprCallProc) node.getExprs().get(i);
                    call.accept(this);
                    i++;
                }
                else i++;
            }
        }

        try{
            node.getId().setTable(stackTable.peek());
        } catch(Exception e) {  }

        try{
            if(node.getExpr().getClass().getSimpleName().equals(IdNode.class.getSimpleName())){
                node.getExpr().setTable(stackTable.peek());
            }
        }
        catch(Exception e) {    }
        return null;
    }

    @Override
    public Void Visit(CallProcOp node) {
        node.setTable(stackTable.peek());
        if(stackTable.lookUp(node.getId().getId()) == null){
            errors += "\nDichiarazione mancante per il metodo chiamato\n";
            System.err.println(errors);
            return null;
        }

        if(!node.getExprs().isEmpty()) {
            String numPar = "" + node.getExprs().size();
            try {
                if (!numPar.equals(parsTable.lookUp(node.getId().getId()).getKind())) {
                    System.err.println("Parametri non conformi alla dichiarazione del metodo");
                    return null;
                }
            }catch(Exception e){}
        }
        for(ExprNode e : node.getExprs()) {
            if(e.toId(e) != null) {
                if(stackTable.lookUp(e.toId(e).getId()) == null) {
                    errors += "\nDichiarazione mancante per i parametri "+ e.toId(e).getId() + "\n";
                    System.err.println(errors);
                    return null;
                }
            }
        }
        return null;
    }

    @Override
    public Void Visit(AddOp node) {
        if(node.getLeft().getClass().getSimpleName().equals(IdNode.class.getSimpleName())){
            node.getLeft().setTable(stackTable.peek());
        }

        if(node.getRight().getClass().getSimpleName().equals(IdNode.class.getSimpleName())){
            node.getRight().setTable(stackTable.peek());
        }
        return null;
    }

    @Override
    public Void Visit(AndOp node) {
        if(node.getLeft().getClass().getSimpleName().equals(IdNode.class.getSimpleName())){
            node.getLeft().setTable(stackTable.peek());
        }

        if(node.getRight().getClass().getSimpleName().equals(IdNode.class.getSimpleName())){
            node.getRight().setTable(stackTable.peek());
        }
        return null;
    }

    @Override
    public Void Visit(OrOp node) {
        if(node.getLeft().getClass().getSimpleName().equals(IdNode.class.getSimpleName())){
            node.getLeft().setTable(stackTable.peek());
        }

        if(node.getRight().getClass().getSimpleName().equals(IdNode.class.getSimpleName())){
            node.getRight().setTable(stackTable.peek());
        }
        return null;
    }

    @Override
    public Void Visit(NotOp node) {
        if(node.getLink().getClass().getSimpleName().equals(IdNode.class.getSimpleName())){
            node.getLink().setTable(stackTable.peek());
        }
        return null;
    }

    @Override
    public Void Visit(DivOp node) {
        if(node.getLeft().getClass().getSimpleName().equals(IdNode.class.getSimpleName())){
            node.getLeft().setTable(stackTable.peek());
        }

        if(node.getRight().getClass().getSimpleName().equals(IdNode.class.getSimpleName())){
            node.getRight().setTable(stackTable.peek());
        }
        return null;
    }

    @Override
    public Void Visit(EqOp node) {
        if(node.getLeft().getClass().getSimpleName().equals(IdNode.class.getSimpleName())){
            if(stackTable.lookUp(node.getLeft().toId(node.getLeft()).getId()) == null){
                errors += "Dichiarazione mancante per l'id " + node.getLeft().toId(node.getLeft()).getId() + "\n";
                System.err.println(errors);
                return null;
            }

            node.getLeft().setTable(stackTable.peek());
        }

        if(node.getRight().getClass().getSimpleName().equals(IdNode.class.getSimpleName())){
            if(stackTable.lookUp(node.getRight().toId(node.getRight()).getId()) == null){
                errors += "Dichiarazione mancante per l'id " + node.getRight().toId(node.getRight()).getId() + "\n";
                System.err.println(errors);
                return null;
            }

            node.getRight().setTable(stackTable.peek());
        }
        return null;
    }

    @Override
    public Void Visit(GeOp node) {
        if(node.getLeft().getClass().getSimpleName().equals(IdNode.class.getSimpleName())){
            if(stackTable.lookUp(node.getLeft().toId(node.getLeft()).getId()) == null){
                errors += "Dichiarazione mancante per l'id " + node.getLeft().toId(node.getLeft()).getId() + "\n";
                System.err.println(errors);
                return null;
            }

            node.getLeft().setTable(stackTable.peek());
        }

        if(node.getRight().getClass().getSimpleName().equals(IdNode.class.getSimpleName())){
            if(stackTable.lookUp(node.getRight().toId(node.getRight()).getId()) == null){
                errors += "Dichiarazione mancante per l'id " + node.getRight().toId(node.getRight()).getId() + "\n";
                System.err.println(errors);
                return null;
            }

            node.getRight().setTable(stackTable.peek());
        }
        return null;
    }

    @Override
    public Void Visit(SubOp node) {
        if(node.getLeft().getClass().getSimpleName().equals(IdNode.class.getSimpleName())){
            node.getLeft().setTable(stackTable.peek());
        }

        if(node.getRight().getClass().getSimpleName().equals(IdNode.class.getSimpleName())){
            node.getRight().setTable(stackTable.peek());
        }
        return null;
    }

    @Override
    public Void Visit(GtOp node) {
        if(node.getLeft().getClass().getSimpleName().equals(IdNode.class.getSimpleName())){
            if(stackTable.lookUp(node.getLeft().toId(node.getLeft()).getId()) == null){
                errors += "Dichiarazione mancante per l'id " + node.getLeft().toId(node.getLeft()).getId() + "\n";
                System.err.println(errors);
                return null;
            }

            node.getLeft().setTable(stackTable.peek());
        }

        if(node.getRight().getClass().getSimpleName().equals(IdNode.class.getSimpleName())){
            if(stackTable.lookUp(node.getRight().toId(node.getRight()).getId()) == null){
                errors += "Dichiarazione mancante per l'id " + node.getRight().toId(node.getRight()).getId() + "\n";
                System.err.println(errors);
                return null;
            }

            node.getRight().setTable(stackTable.peek());
        }
        return null;
    }

    @Override
    public Void Visit(LeOp node) {
        if(node.getLeft().getClass().getSimpleName().equals(IdNode.class.getSimpleName())){
            if(stackTable.lookUp(node.getLeft().toId(node.getLeft()).getId()) == null){
                errors += "Dichiarazione mancante per l'id " + node.getLeft().toId(node.getLeft()).getId() + "\n";
                System.err.println(errors);
                return null;
            }

            node.getLeft().setTable(stackTable.peek());
        }

        if(node.getRight().getClass().getSimpleName().equals(IdNode.class.getSimpleName())){
            if(stackTable.lookUp(node.getRight().toId(node.getRight()).getId()) == null){
                errors += "Dichiarazione mancante per l'id " + node.getRight().toId(node.getRight()).getId() + "\n";
                System.err.println(errors);
                return null;
            }

            node.getRight().setTable(stackTable.peek());
        }
        return null;
    }

    @Override
    public Void Visit(LtOp node) {
        if(node.getLeft().getClass().getSimpleName().equals(IdNode.class.getSimpleName())){
            if(stackTable.lookUp(node.getLeft().toId(node.getLeft()).getId()) == null){
                errors += "Dichiarazione mancante per l'id " + node.getLeft().toId(node.getLeft()).getId() + "\n";
                System.err.println(errors);
                return null;
            }

            node.getLeft().setTable(stackTable.peek());
        }

        if(node.getRight().getClass().getSimpleName().equals(IdNode.class.getSimpleName())){
            if(stackTable.lookUp(node.getRight().toId(node.getRight()).getId()) == null){
                errors += "Dichiarazione mancante per l'id " + node.getRight().toId(node.getRight()).getId() + "\n";
                System.err.println(errors);
                return null;
            }

            node.getRight().setTable(stackTable.peek());
        }
        return null;
    }

    @Override
    public Void Visit(MultOp node) {
        if(node.getLeft().getClass().getSimpleName().equals(IdNode.class.getSimpleName())){
            node.getLeft().setTable(stackTable.peek());
        }

        if(node.getRight().getClass().getSimpleName().equals(IdNode.class.getSimpleName())){
            node.getRight().setTable(stackTable.peek());
        }
        return null;
    }

    @Override
    public Void Visit(NeOp node) {
        if(node.getLeft().getClass().getSimpleName().equals(IdNode.class.getSimpleName())){
            if(stackTable.lookUp(node.getLeft().toId(node.getLeft()).getId()) == null){
                errors += "Dichiarazione mancante per l'id " + node.getLeft().toId(node.getLeft()).getId() + "\n";
                System.err.println(errors);
                return null;
            }

            node.getLeft().setTable(stackTable.peek());
        }

        if(node.getRight().getClass().getSimpleName().equals(IdNode.class.getSimpleName())){
            if(stackTable.lookUp(node.getRight().toId(node.getRight()).getId()) == null){
                errors += "Dichiarazione mancante per l'id " + node.getRight().toId(node.getRight()).getId() + "\n";
                System.err.println(errors);
                return null;
            }

            node.getRight().setTable(stackTable.peek());
        }
        return null;
    }

    @Override
    public Void Visit(BinaryExprNode node) {
        node.getLeft().accept(this);
        node.getRight().accept(this);
        return null;
    }

    @Override
    public Void Visit(ArithmeticExpr node) {
        if(stackTable.lookUp(node.getLeft().toId(node.getLeft()).getId()) == null) {
            errors += "Dichiarazione mancante per l'id " + node.getLeft().toId(node.getLeft()).getId();
            System.err.println(errors);
            return null;
        }
        if(stackTable.lookUp(node.getRight().toId(node.getRight()).getId()) == null) {
            errors += "Dichiarazione mancante per l'id " + node.getRight().toId(node.getRight()).getId();
            System.err.println(errors);
            return null;
        }
        else {
            node.getLeft().accept(this);
            node.getRight().accept(this);
        }
        return null;
    }

    @Override
    public Void Visit(BooleanConst node) {
        return null;
    }

    @Override
    public Void Visit(ConstNode node) {
        return null;
    }

    @Override
    public Void Visit(FloatConst node) {
        return null;
    }

    @Override
    public Void Visit(NullConst node) {
        return null;
    }

    @Override
    public Void Visit(StringConst node) {
        return null;
    }

    @Override
    public Void Visit(ProcBodyOp node) {
        node.setTable(stackTable.peek());
        SymbolTable t = node.getTable();
        if (node.getVarDecls() != null) {
            IdListInitOp ids;
            for(VarDeclOp vars : node.getVarDecls()) {
                ids = vars.getIds();
                for(IdNode i : ids.getIds()){
                    Item item = new Item(vars.getType(), "var", i.getId());
                    if(t.lookUp(item.getName()) != null) {
                        errors += "\nDichiarazione multipla per l'identificatore " + i.getId();
                        System.err.println(errors);
                        return null;
                    }
                    else t.addIdentifier(item);
                }
                try {
                    for (ExprNode e : ids.getExprs()) {
                        if (e.toId(e) != null) {
                            IdNode x = e.toId(e);
                            if(t.lookUp(x.getId()) == null) {
                                errors += "\nAssegnazione di un tipo non dichiarato precedentemente";
                                System.err.println(errors);
                                return null;
                            }
                            //System.out.println(node.getTable().lookUp(x.getId()));
                        }
                    }
                }
                catch(Exception exception) {    }
            }
        }
        if(node.getStats() != null) {
            for (StatNode s : node.getStats()) {
                s.accept(this);
            }
        }
        if(node.getEs() != null) {
            for(ExprNode e : node.getEs()) {
                if(e.toId(e) != null) {
                    IdNode i = e.toId(e);
                    i.setTable(stackTable.peek());
                    i.setType(i.getTable().lookUp(i.getId()).getType());
                    e.accept(this);
                }

                e.accept(this);
            }
        }
        return null;
    }




    @Override
    public Void Visit(BodyOp node) {
        if(node.getStats() != null) {
            for (StatNode s : node.getStats()) {
                s.accept(this);
            }
        }
        return null;
    }

    @Override
    public Void Visit(BodyOpElif node) {
        if(node.getStats() != null) {
            for (StatNode s : node.getStats()) {
                s.accept(this);
            }
        }
        if(node.getElif_list() != null) {
            for(ElifOp e: node.getElif_list()) {
                e.getExpr().accept(this);
                e.getBody().accept(this);
            }
        }
        return null;
    }

    @Override
    public Void Visit(BoolType node) {
        return null;
    }

    @Override
    public Void Visit(FloatType node) {
        return null;
    }

    @Override
    public Void Visit(IntegerType node) {
        return null;
    }

    @Override
    public Void Visit(StringType node) {
        return null;
    }

    @Override
    public Void Visit(MinusOp node) {
        return null;
    }

    @Override
    public Void Visit(UnaryExprNode node) {
        return null;
    }



    @Override
    public Void Visit(ExprCallProc node) {
        node.setTable(stackTable.peek());
        return null;
    }
}
