package AST.Stat;

import Visitor.VisitorInterface;

import java.util.LinkedList;

public class BodyOp extends StatNode{
    LinkedList<StatNode> stat_list;

    public BodyOp(LinkedList<StatNode> stat_list){
        this.stat_list = stat_list;
    }
    public LinkedList<StatNode> getStats(){
        return this.stat_list;
    }

    @Override
    public <T> T accept(VisitorInterface<T> v) {
        return v.Visit(this);
    }

}
