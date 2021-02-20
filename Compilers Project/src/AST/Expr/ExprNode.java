package AST.Expr;

import AST.ASTNode;
import AST.Const.*;
import AST.Type.*;

public abstract class ExprNode extends ASTNode {
    GenericType type;

    public GenericType getType(){
        if(this.getClass().equals(IntegerConst.class)){
            this.type = new IntegerType();
        }
        if(this.getClass().equals(FloatConst.class)){
            this.type = new FloatType();
        }
        if(this.getClass().equals(BooleanConst.class)){
            this.type = new BoolType();
        }
        if(this.getClass().equals(StringConst.class)){
            this.type = new StringType();
        }
        return type;
    }

    public IdNode toId(ExprNode e) {
        if(e.getClass().getSimpleName().equals(IdNode.class.getSimpleName())){
            return (IdNode) e;
        }
        return null;
    }


    public ConstNode toConst() {
        if(this.getClass().getSimpleName().equals(IntegerConst.class.getSimpleName())){
            return (IntegerConst) this;
        }

        if(this.getClass().getSimpleName().equals(FloatConst.class.getSimpleName())){
            return (FloatConst) this;
        }

        if(this.getClass().getSimpleName().equals(StringConst.class.getSimpleName())){
            return (StringConst) this;
        }

        if(this.getClass().getSimpleName().equals(BooleanConst.class.getSimpleName())){
            return (BooleanConst) this;
        }

        return null;
    }


}


