package SymbolTable;

import java.util.Stack;

public class StackSymbolTable {
    Stack<SymbolTable> stack = null;

    public StackSymbolTable(){
        this.stack = new Stack<SymbolTable>();
    }

    public StackSymbolTable(SymbolTable table) {
        this.stack = new Stack<SymbolTable>();
        if(table.getPrev() == null) {
            this.stack.push(table);
        }
    }

    public SymbolTable enterScope(){
        SymbolTable t;
        if(stack.empty()) {
            t = new SymbolTable(null);
        }
        else {
            t = new SymbolTable(stack.peek());
        }
        this.stack.push(t);
        return t;
    }

    public void exitScope(){
        this.stack.pop();
    }

    public Item probe(String id) {
        return this.peek().probe(id);
    }

    public Item lookUp(String id) {
        return this.peek().lookUp(id);
    }

    public boolean addIdentifier(Item item) {
        return stack.peek().addIdentifier(item);
    }

    public SymbolTable peek(){
        return stack.peek();
    }

}
