package AST;

import AST.Expr.ExprNode;
import AST.Expr.IdNode;
import Visitor.VisitorInterface;

import java.util.LinkedList;

public class IdListInitOp /*extends AssignOp*/ {
    LinkedList<ExprNode> exprs = new LinkedList<ExprNode>();
    LinkedList<IdNode> ids = new LinkedList<IdNode>();

    public IdListInitOp(){

    }

    public void addInitOp(AssignOp x){
        this.ids.add(x.getId());
        this.exprs.addFirst(x.getExpr());
    }

    public LinkedList<ExprNode> getExprs(){
        return this.exprs;
    }

    public LinkedList<IdNode> getIds(){
        return this.ids;
    }

    //@Override
    public <T> T accept(VisitorInterface<T> v) {
        return (v.Visit(this));
    }


}
