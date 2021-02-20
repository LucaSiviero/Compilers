package SymbolTable;

import AST.Type.GenericType;

public class Item {
    GenericType t;
    String kind;
    String name;

    public Item(GenericType t, String kind, String name) {
        this.t = t;
        this.kind = kind;
        this.name = name;
    }



    public void setType(GenericType t) {
        this.t = t;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public void setName(String name) {
        this.name = name;
    }

    public GenericType getType() {
        return t;
    }

    public String getKind() {
        return kind;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return this.getName() + " " + this.getKind() + " " + t.getClass().getSimpleName();
    }
}
