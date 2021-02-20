package AST.Stat;

import AST.Expr.IdNode;
import Visitor.VisitorInterface;

import java.util.LinkedList;

public class ReadOp extends StatNode {
    LinkedList<IdNode> id = new LinkedList<IdNode>();

    public ReadOp(LinkedList<IdNode> id){
        this.id=id;
    }

    public LinkedList<IdNode> getIds() {
        return id;
    }


    @Override
    public <T> T accept(VisitorInterface<T> v) {
        return v.Visit(this);
    }}
