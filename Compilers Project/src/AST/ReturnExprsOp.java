package AST;

import AST.Expr.ExprNode;
import Visitor.VisitorInterface;

import java.util.LinkedList;

public class ReturnExprsOp extends ExprNode {
    LinkedList<ExprNode> exprs;

    public ReturnExprsOp(LinkedList<ExprNode> exprs){
        this.exprs = exprs;
    }


    public LinkedList<ExprNode> getExprs(){
        return this.exprs;
    }


    @Override
    public <T> T accept(VisitorInterface<T> v) {
        return v.Visit(this);
    }
}
