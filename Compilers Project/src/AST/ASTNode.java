package AST;

import AST.Type.GenericType;
import SymbolTable.SymbolTable;
import Visitor.Visitable;

public abstract class ASTNode implements Visitable {
    SymbolTable table;
    GenericType genericType;

    public GenericType getType() {
        return this.genericType;
    }

    public void setType(GenericType genericType) {
        this.genericType = genericType;
    }

    public void setTable(SymbolTable table){
       this.table = table;
    }

    public SymbolTable getTable(){
        return this.table;
    }


}
