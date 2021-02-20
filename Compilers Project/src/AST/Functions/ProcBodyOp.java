package AST.Functions;

import AST.ASTNode;
import AST.Expr.ExprNode;
import AST.ReturnExprsOp;
import AST.Stat.StatNode;
import AST.VarDeclOp;
import Visitor.VisitorInterface;

import java.util.LinkedList;

public class ProcBodyOp extends ASTNode {
    LinkedList<VarDeclOp> varDecls;
    LinkedList<StatNode> stats;
    LinkedList<ExprNode> es;

    public ProcBodyOp(LinkedList<VarDeclOp> varDecls, LinkedList<StatNode> stats, LinkedList<ExprNode> exprs) {
        this.varDecls = varDecls;
        this.stats = stats;
        this.es = exprs;
    }

    public LinkedList<VarDeclOp> getVarDecls() {
        return varDecls;
    }

    public LinkedList<StatNode> getStats() {
        return stats;
    }


    public LinkedList<ExprNode> getEs(){
        return es;
    }

    @Override
    public <T> T accept(VisitorInterface<T> v) {
        return v.Visit(this);
    }
}
