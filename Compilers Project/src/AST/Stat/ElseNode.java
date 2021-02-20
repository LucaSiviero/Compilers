package AST.Stat;

import Visitor.VisitorInterface;

public class ElseNode extends StatNode {
    BodyOp body;

    public ElseNode(BodyOp body){
        this.body = body;
    }


    @Override
    public <T> T accept(VisitorInterface<T> v) {
        return v.Visit(this);
    }
    public BodyOp getBody() {
        return body;
    }
}
