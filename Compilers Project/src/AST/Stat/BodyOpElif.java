package AST.Stat;

import Visitor.VisitorInterface;

import java.util.LinkedList;

public class BodyOpElif extends StatNode{
    LinkedList<ElifOp> elif_list;

    public BodyOpElif(LinkedList<ElifOp> elif_list){
        this.elif_list = elif_list;
    }

    public LinkedList<ElifOp> getStats(){
        return this.elif_list;
    }


    @Override
    public <T> T accept(VisitorInterface<T> v) {
        return v.Visit(this);
    }
    public LinkedList<ElifOp> getElif_list() {
        return elif_list;
    }
}
