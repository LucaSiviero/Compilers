package AST.Stat;

import AST.Expr.ExprNode;
import Visitor.VisitorInterface;

import java.util.LinkedList;

public class IfStatOp extends StatNode{
    ExprNode e;
    BodyOp b1;
    LinkedList<ElifOp> elif_list;
    ElseNode el2;

    public IfStatOp(ExprNode e, BodyOp b1, LinkedList<ElifOp> elif_list, ElseNode el2){
        this.e = e;
        this.b1 = b1;
        this.elif_list = elif_list;
        this.el2 = el2;
    }

    public IfStatOp(ExprNode e, BodyOp b1, ElseNode el2){
        this.e = e;
        this.b1 = b1;
        this.el2 = el2;
    }


    @Override
    public <T> T accept(VisitorInterface<T> v) {
        return v.Visit(this);
    }
    public ExprNode getExpr() {
        return e;
    }

    public BodyOp getBody() {
        return b1;
    }

    public LinkedList<ElifOp> getElif_list() {
        return elif_list;
    }

    public ElseNode getElse() {
        return el2;
    }
}
