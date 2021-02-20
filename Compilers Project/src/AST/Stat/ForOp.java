package AST.Stat;

import AST.AssignOp;
import AST.Expr.ExprNode;
import AST.Functions.ProcBodyOp;
import AST.VarDeclOp;
import Visitor.VisitorInterface;

public class ForOp extends StatNode {
    VarDeclOp v;
    ExprNode e;
    AssignOp a;
    ProcBodyOp body;

    public VarDeclOp getVarDecl() {
        return v;
    }

    public ExprNode getEval() {
        return e;
    }

    public AssignOp getAssign() {
        return a;
    }

    public ProcBodyOp getBody() {
        return body;
    }

    public ForOp(VarDeclOp v, ExprNode e, AssignOp a, ProcBodyOp body) {
        this.v = v;
        this.e = e;
        this.a = a;
        this.body = body;
    }

    @Override
    public <T> T accept(VisitorInterface<T> v) {
        return v.Visit(this);
    }
}
