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
import SymbolTable.Item;
import SymbolTable.SymbolTable;

import java.util.LinkedList;
import java.util.logging.Logger;

public class TypeCheckVisitor implements VisitorInterface<GenericType>{

    LinkedList<String> errors;
    Logger log = Logger.getGlobal();

    public TypeCheckVisitor() {
        this.errors = new LinkedList<String>();
    }

    public LinkedList<String> doTypeCheck(ProgramOp node) {
        Visit(node);
        return errors;
    }

    @Override
    public GenericType Visit(ForOp node) {
        return null;
    }

    @Override
    public GenericType Visit(ASTNode node) {
        return null;
    }

    @Override
    public GenericType Visit(ProgramOp node) {
        if(node.getVarDecls() != null) {
            for(VarDeclOp v : node.getVarDecls()) {
                if(v.accept(this) == null) return null;
            }
        }
        if(node.getProcs() != null) {
            for(ProcOp p : node.getProcs()) {
                if(p.accept(this) == null) return null;
            }
        }

        return new GenericType();
    }

    @Override
    public GenericType Visit(VarDeclOp node) {
        IdListInitOp idlist = node.getIds();
        for(IdNode i : idlist.getIds()) {
            Item temp = node.getTable().lookUp(i.getId());
            i.setType(temp.getType());
            for(ExprNode e : idlist.getExprs()) {
                try{
                    IdNode id = e.toId(e);
                    id.setType(node.getTable().lookUp(id.getId()).getType());

                    if(!temp.getType().getClass().getSimpleName().equals(e.getType().getClass().getSimpleName())) {
                        errors.add("Assegnazione di tipi non compatibili");
                        System.err.println(errors);
                    }
                }catch(Exception exc) {     }
            }
        }
        node.getIds().accept(this);
        return node.getType();
    }

    @Override
    public GenericType Visit(IntegerConst node) {
        return new IntegerType();
    }

    @Override
    public GenericType Visit(ProcOp node) {

        node.getProcBody().accept(this);
        ProcBodyOp procBody = node.getProcBody();
        for(ExprNode e : procBody.getEs()) {
            try {
                if (!(node.getReturns().getFirst().getType().getClass().getSimpleName().equals(e.accept(this).getClass().getSimpleName()))) {
                    errors.add("return statement non compatibile con il tipo di ritorno della funzione");
                    System.err.println(errors);
                    return null;
                }
            }catch(Exception ex ){}

        }

        return node.getReturns().getFirst().getType();
    }

    @Override
    public GenericType Visit(GenericType node) {
        if(node.getClass().getSimpleName().equals(IntegerType.class.getSimpleName())) {
            return new IntegerType();
        }
        if(node.getClass().getSimpleName().equals(FloatType.class.getSimpleName())) {
            return new FloatType();
        }
        if(node.getClass().getSimpleName().equals(StringType.class.getSimpleName())) {
            return new StringType();
        }
        if(node.getClass().getSimpleName().equals(BoolType.class.getSimpleName())) {
            return new BoolType();
        }
        return null;
    }

    @Override
    public GenericType Visit(IdListInitOp node) {
        GenericType t = node.getIds().getFirst().getType();
        if(node.getExprs() != null) {
            try{
                int i = 0;
                while(i < node.getExprs().size()) {
                    if (node.getExprs().get(i) == null) {
                        i++;
                    }
                    else if(node.getExprs().get(i) != null) {
                        if(!(node.getExprs().get(i).getType().getClass().getSimpleName().equals(t.getClass().getSimpleName()))){
                            errors.add("Assegnazione non compatibile");
                            System.err.println(errors);
                            return null;
                        }
                        else i++;
                    }
                }
            }
            catch (Exception e){}

        }

        return t;
    }

    @Override
    public GenericType Visit(ResultTypeOp node) {
        return null;
    }

    @Override
    public GenericType Visit(ReturnExprsOp node) {
        return null;
    }

    @Override
    public GenericType Visit(ParDeclOp node) {
        return node.getType();
    }

    @Override
    public GenericType Visit(IdNode node) {
        return null;
    }

    @Override
    public GenericType Visit(StatNode node) {
        return null;
    }

    @Override
    public GenericType Visit(IfStatOp node) {

        node.getExpr().accept(this);

        if(node.getBody()!= null){

            node.getBody().accept(this);
        }
        if(node.getElse() != null) {

            node.getElse().accept(this);
        }
        if(node.getElif_list() != null) {

            for(ElifOp elif : node.getElif_list()) {
                elif.accept(this);
            }
        }
        return new GenericType();
    }

    @Override
    public GenericType Visit(ElifOp node) {
        node.getExpr().accept(this);
        if(node.getBody() != null) {
            node.getBody().accept(this);
        }
        return new GenericType();
    }

    @Override
    public GenericType Visit(ElseNode node) {
        if(node.getBody().accept(this) != null){
            return new GenericType();
        }
        return null;
    }

    @Override
    public GenericType Visit(WhileOp node) {

        node.getExpr().accept(this);
        if(node.getBody() != null) {
            node.getBody().accept(this);
        }
        if(node.getBody2() != null) {
            node.getBody2().accept(this);
        }
        return new GenericType();
    }

    @Override
    public GenericType Visit(ReadOp node) {
        return null;
    }

    @Override
    public GenericType Visit(WriteOp node) {
        return new GenericType();
    }

    @Override
    public GenericType Visit(AssignOp node) {
        if(node.getIds() != null) {
            try {
                for(IdNode i : node.getIds()) {
                    i.setType(i.getTable().lookUp(i.getId()).getType());
                    if(node.getExprs() != null) {
                        for (ExprNode e : node.getExprs()) {
                            if(e.toId(e) != null) {
                                IdNode exprId = e.toId(e);
                                exprId.setType(e.getTable().lookUp(exprId.getId()).getType());
                                if(!exprId.getType().getClass().getSimpleName().equals(i.getType().getClass().getSimpleName())){
                                    errors.add("Assegnazione di un tipo non compatibile");
                                    return null;
                                }
                            }
                            e.accept(this);
                        }
                    }
                }
            }catch(Exception e){}
        }
        return new GenericType();
    }

    @Override
    public GenericType Visit(CallProcOp node) {
        return null;
    }

    public boolean arithmeticCheck(ArithmeticExpr node) {
        try {
            if ((node.getLeft().toId(node.getLeft()) != null) && (node.getRight().toId(node.getRight()) != null)) {
                IdNode id1 = node.getLeft().toId(node.getLeft());
                IdNode id2 = node.getRight().toId(node.getRight());
                GenericType t1 = id1.getTable().lookUp(id1.getId()).getType();
                GenericType t2 = id2.getTable().lookUp(id2.getId()).getType();
                try {
                    if (t1.getClass().getSimpleName().equals(t2.getClass().getSimpleName())) {

                        return true;
                    } else{
                        errors.add("Operazione tra tipi non compatibili!");
                        return false;
                    }
                } catch (Exception e) {
                }
            }

            if (node.getLeft().toId(node.getLeft()) != null) {
                IdNode id1 = node.getLeft().toId(node.getLeft());
                GenericType t1 = id1.getTable().lookUp(id1.getId()).getType();
                GenericType t2 = node.getRight().getType();
                try {
                    if (t1.getClass().getSimpleName().equals(t2.getClass().getSimpleName())) {

                        return true;
                    }else{
                        errors.add("Operazione tra tipi non compatibili!");
                        return false;
                    }
                } catch (Exception e) {
                }
            }

            if (node.getRight().toId(node.getRight()) != null) {
                IdNode id2 = node.getRight().toId(node.getRight());
                GenericType t2 = id2.getTable().lookUp(id2.getId()).getType();
                GenericType t1 = node.getLeft().getType();
                try {
                    if (t1.getClass().getSimpleName().equals(t2.getClass().getSimpleName())) {

                        return true;
                    } else{
                        errors.add("Operazione tra tipi non compatibili!");
                        return false;
                    }
                } catch (Exception e) {
                }
            } else {
                GenericType t1 = node.getLeft().getType();
                GenericType t2 = node.getRight().getType();
                try {
                    if (t1.getClass().getSimpleName().equals(t2.getClass().getSimpleName())) {

                        return true;
                    } else{
                        errors.add("Operazione tra tipi non compatibili!");
                        return false;
                    }
                } catch (Exception e) {
                }
            }

        } catch(Exception e) {return false;  }
        return false;
    }

    public boolean booleanCheck(BinaryExprNode node) {
        if((node.getLeft().toId(node.getLeft()) != null) && (node.getRight().toId(node.getRight()) != null)){
            IdNode id1 = node.getLeft().toId(node.getLeft());
            IdNode id2 = node.getRight().toId(node.getRight());
            GenericType t1 = id1.getTable().lookUp(id1.getId()).getType();
            GenericType t2 = id2.getTable().lookUp(id2.getId()).getType();
            try{
                if(t1.getClass().getSimpleName().equals(t2.getClass().getSimpleName())) {

                    return true;
                }
                else return false;
            }
            catch(Exception e) {    }
        }

        if(node.getLeft().toId(node.getLeft()) != null) {
            IdNode id1 = node.getLeft().toId(node.getLeft());
            GenericType t1 = id1.getTable().lookUp(id1.getId()).getType();
            GenericType t2 = node.getRight().getType();
            try{
                if(t1.getClass().getSimpleName().equals(t2.getClass().getSimpleName())) {

                    return true;
                }
                else return false;
            }
            catch(Exception e) {    }
        }

        if(node.getRight().toId(node.getRight())!= null) {
            IdNode id2 = node.getRight().toId(node.getRight());
            GenericType t2 = id2.getTable().lookUp(id2.getId()).getType();
            GenericType t1 = node.getLeft().getType();
            try{
                if(t1.getClass().getSimpleName().equals(t2.getClass().getSimpleName())) {

                    return true;
                }
                else return false;
            }
            catch(Exception e) {    }
        }

        else{
            GenericType t1 = node.getLeft().getType();
            GenericType t2 = node.getRight().getType();
            try{
                if(t1.getClass().getSimpleName().equals(t2.getClass().getSimpleName())) {

                    return true;
                }
                else return false;
            }
            catch(Exception e) {    }
        }
        return false;
    }



    @Override
    public GenericType Visit(AddOp node) {
        GenericType t = null;
        IdNode i1 = null;
        IdNode i2 = null;
        if(arithmeticCheck(node)){
            if(node.getLeft().toId(node.getLeft()).getId() != null) {
                i1 = node.getLeft().toId(node.getLeft());
                i1.setType(i1.getTable().lookUp(i1.getId()).getType());
                if(node.getRight().toId(node.getRight()).getId() != null) {
                    i2 = node.getRight().toId(node.getRight());
                    i2.setType(i2.getTable().lookUp(i2.getId()).getType());
                }
            }
            if(i1.getType().getClass().getSimpleName().equals(IntegerType.class.getSimpleName())) {
                t =  new IntegerType();
            }
            if(i2.getType().getClass().getSimpleName().equals(FloatType.class.getSimpleName())){
                t = new FloatType();
            }
        }
        else {
            //errors.add("Addizione tra tipi non compatibili!");
            //System.err.println(errors);
            return null;
        }
        return t;
    }

    @Override
    public GenericType Visit(AndOp node) {
        if(booleanCheck(node)){
            return new BoolType();
        }
        else {
            errors.add("And tra tipi non compatibili!");
            System.err.println(errors);
            return null;
        }
    }

    @Override
    public GenericType Visit(OrOp node) {
        if(booleanCheck(node)){
            return new BoolType();
        }
        else {
            errors.add("Or tra tipi non compatibili!");
            System.err.println(errors);
            return null;
        }
    }

    @Override
    public GenericType Visit(NotOp node) {
        if(node.getLink().getTable() != null) {
            SymbolTable table = node.getLink().getTable();
            if(node.getLink().toId(node.getLink()) != null) {
                if (table.lookUp(node.getLink().toId(node.getLink()).getId()) != null) {
                    node.getLink().setType(table.lookUp(node.getLink().toId(node.getLink()).getId()).getType());
                }
            }
        }

        if(node.getLink().toConst() != null) {
            try{
                BooleanConst b = (BooleanConst) node.getLink();
                b.setType(new BoolType());
            }catch (Exception ex){
                System.err.println("Not a boolean const");
            }
        }

        if(!(node.getLink().getType().getClass().getSimpleName().equals(BoolType.class.getSimpleName()))){
            return null;
        }

        else{
            return new BoolType();
        }

    }

    @Override
    public GenericType Visit(DivOp node) {
        GenericType t = null;
        IdNode i1 = null;
        IdNode i2 = null;
        if(arithmeticCheck(node)){
            if(node.getLeft().toId(node.getLeft()).getId() != null) {
                i1 = node.getLeft().toId(node.getLeft());
                i1.setType(i1.getTable().lookUp(i1.getId()).getType());
                if(node.getRight().toId(node.getRight()).getId() != null) {
                    i2 = node.getRight().toId(node.getRight());
                    i2.setType(i2.getTable().lookUp(i2.getId()).getType());
                }
            }
            if(i1.getType().getClass().getSimpleName().equals(IntegerType.class.getSimpleName())) {
                t =  new IntegerType();
            }
            if(i2.getType().getClass().getSimpleName().equals(FloatType.class.getSimpleName())){
                t = new FloatType();
            }
        }
        else {
            //errors.add("Divisione tra tipi non compatibili!");
            //System.err.println(errors);
            return null;
        }
        return t;
    }

    @Override
    public GenericType Visit(EqOp node) {
        if(!checkConfronti(node)){
            return null;
        }
        return new GenericType();
    }

    public boolean checkConfronti(BinaryExprNode e) {
        boolean isCompatible = false;
        String classe = e.getClass().getSimpleName();
        if (!(classe.equals(NotOp.class.getSimpleName()) || classe.equals(MinusOp.class.getSimpleName()))) {
            GenericType t2 = e.getRight().getType();
            try {
                IdNode i = e.getLeft().toId(e.getLeft());
                GenericType t1 = i.getTable().lookUp(i.getId()).getType();
                if(!(t1.getClass().getSimpleName().equals(t2.getClass().getSimpleName()))) {
                    errors.add("Confronto tra tipi non compatibili");
                    System.err.println(errors);
                    return false;
                }
                else isCompatible = true;
            }
            catch(Exception exception) {        }
        }
        return isCompatible;
    }

    @Override
    public GenericType Visit(GeOp node) {
        if(!checkConfronti(node)){
            return null;
        }
        return new GenericType();
    }

    @Override
    public GenericType Visit(SubOp node) {
        GenericType t = null;
        IdNode i1 = null;
        IdNode i2 = null;
        if(arithmeticCheck(node)){
            if(node.getLeft().toId(node.getLeft()).getId() != null) {
                i1 = node.getLeft().toId(node.getLeft());
                i1.setType(i1.getTable().lookUp(i1.getId()).getType());
                if(node.getRight().toId(node.getRight()).getId() != null) {
                    i2 = node.getRight().toId(node.getRight());
                    i2.setType(i2.getTable().lookUp(i2.getId()).getType());
                }
            }
            if(i1.getType().getClass().getSimpleName().equals(IntegerType.class.getSimpleName())) {
                t =  new IntegerType();
            }
            if(i2.getType().getClass().getSimpleName().equals(FloatType.class.getSimpleName())){
                t = new FloatType();
            }
        }
        else {
            //errors.add("Sottrazione tra tipi non compatibili!");
            //System.err.println(errors);
            return null;
        }
        return t;
    }

    @Override
    public GenericType Visit(GtOp node) {
        if(!checkConfronti(node)){
            return null;
        }
        return new GenericType();
    }

    @Override
    public GenericType Visit(LeOp node) {
        if(!checkConfronti(node)){
            return null;
        }
        return new GenericType();
    }

    @Override
    public GenericType Visit(LtOp node) {
        if(!checkConfronti(node)){
            return null;
        }
        return new GenericType();
    }

    @Override
    public GenericType Visit(MultOp node) {
        GenericType t = null;
        IdNode i1 = null;
        IdNode i2 = null;
        if(arithmeticCheck(node)){
            if(node.getLeft().toId(node.getLeft()).getId() != null) {
                i1 = node.getLeft().toId(node.getLeft());
                i1.setType(i1.getTable().lookUp(i1.getId()).getType());
                if(node.getRight().toId(node.getRight()).getId() != null) {
                    i2 = node.getRight().toId(node.getRight());
                    i2.setType(i2.getTable().lookUp(i2.getId()).getType());
                }
            }
            if(i1.getType().getClass().getSimpleName().equals(IntegerType.class.getSimpleName())) {
                t =  new IntegerType();
            }
            if(i2.getType().getClass().getSimpleName().equals(FloatType.class.getSimpleName())){
                t = new FloatType();
            }
        }
        else {
            //errors.add("Moltiplicazione tra tipi non compatibili!");
            //System.err.println(errors);
            return null;
        }
        return t;
    }

    @Override
    public GenericType Visit(NeOp node) {
        if(!checkConfronti(node)){
            return null;
        }
        return new GenericType();
    }

    @Override
    public GenericType Visit(BinaryExprNode node) {
        return null;
    }

    @Override
    public GenericType Visit(ArithmeticExpr node) {
        return null;
    }

    @Override
    public GenericType Visit(BooleanConst node) {
        return new BoolType();
    }

    @Override
    public GenericType Visit(ConstNode node) {
        return null;
    }

    @Override
    public GenericType Visit(FloatConst node) {
        return new FloatType();
    }

    @Override
    public GenericType Visit(NullConst node) {
        return new VoidType();
    }

    @Override
    public GenericType Visit(StringConst node) {
        return new StringType();
    }

    @Override
    public GenericType Visit(ProcBodyOp node) {
        if(node.getStats() != null) {
            for(StatNode s : node.getStats()){
                s.accept(this);
            }
        }

        if(node.getVarDecls() != null){
            SymbolTable table = node.getTable();
            IdListInitOp idlist = null;
            for(VarDeclOp v : node.getVarDecls()){
                GenericType t = v.getType();
                idlist = v.getIds();
                for(IdNode i : idlist.getIds()) {
                    i.setType(t);

                    if(idlist.getExprs() != null) {
                        LinkedList<ExprNode> exprs = idlist.getExprs();
                        try {
                            int iter = 0;
                            while (iter < exprs.size()) {
                                if (exprs.get(iter) == null) {
                                    iter++;
                                }
                                if (exprs.get(iter) != null) {
                                    if(exprs.get(iter).toId(exprs.get(iter)) != null) {
                                        IdNode id = exprs.get(iter).toId(exprs.get(iter));
                                        //.println(node.getTable().lookUp(id.getId()).getType());
                                        GenericType idType = node.getTable().lookUp(id.getId()).getType();
                                        if(!idType.getClass().getSimpleName().equals(i.getType().getClass().getSimpleName())) {
                                            errors.add("Assegnazione di un valore non compatibile");
                                            return null;
                                        }
                                    }
                                    if (!(t.getClass().getSimpleName().equals(exprs.get(iter).getType().getClass().getSimpleName()))) {
                                        errors.add("Assegnazione di un valore non compatibile");
                                        return null;
                                    } else iter++;
                                }
                            }
                        }
                        catch(Exception e) {}
                    }
                }
            }
        }

        if(node.getEs() != null){
            for(ExprNode e : node.getEs()) {
                //System.out.println(e.accept(this));
                e.accept(this);
            }
        }
        return new GenericType();
    }

    @Override
    public GenericType Visit(BodyOp node) {

        if(node.getStats() != null) {
            for(StatNode s : node.getStats()) {
                s.accept(this);
            }
        }
        return new GenericType();
    }

    @Override
    public GenericType Visit(BodyOpElif node) {
        if(node.getElif_list() != null) {
            for(ElifOp elif : node.getElif_list()) {
                elif.getExpr().accept(this);
            }
        }
        return null;
    }

    @Override
    public GenericType Visit(BoolType node) {
        return null;
    }

    @Override
    public GenericType Visit(FloatType node) {
        return null;
    }

    @Override
    public GenericType Visit(IntegerType node) {
        return null;
    }

    @Override
    public GenericType Visit(StringType node) {
        return null;
    }

    @Override
    public GenericType Visit(MinusOp node) {
        try {
            String simple = node.getLink().getType().getClass().getSimpleName();
            if (!((simple.equals(IntegerType.class.getSimpleName())) || simple.equals(FloatType.class.getSimpleName()))) {
                return null;
            }
            if (simple.equals(IntegerType.class.getSimpleName())) {
                return new IntegerType();
            }

            if (simple.equals(FloatType.class.getSimpleName())) {
                return new FloatType();
            }
            return null;
        }
        catch(Exception e){return null;}

    }

    @Override
    public GenericType Visit(UnaryExprNode node) {
        return null;
    }

    @Override
    public GenericType Visit(ExprCallProc node) {
        try {
            GenericType t = node.getTable().lookUp(node.getId().getId()).getType();
            return t;
        }catch(Exception e) {
            return null;
        }
    }
}
