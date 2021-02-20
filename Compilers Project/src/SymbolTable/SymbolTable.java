package SymbolTable;

import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

public class SymbolTable {
    private SymbolTable prev;
    private Hashtable<String, Item> table;

    public SymbolTable getPrev() {
        return this.prev;
    }

    public Hashtable<String, Item> getTable(){
        return this.table;
    }

    public SymbolTable(SymbolTable prev) {
        this.prev = prev;
        this.table = new Hashtable<String, Item>();
    }

    public Item probe(String name) {
        return table.get(name);
    }

    public boolean addIdentifier(Item item) {
        if(this.probe(item.getName()) == null){
            table.put(item.getName(), item);
            return true;
        }
        return false;
    }

    public Item lookUp(String id) {
        for (SymbolTable t = this; t != null; t = t.getPrev()){
            Item item = t.probe(id);
            if(item != null) {
                return item;
            }

        }
        return null;
    }

    @Override
    public String toString() {
        SymbolTable t = this;

        String s = "";
        while(t!= null) {

            s += "=================\n";
            Set<Map.Entry<String, Item>> entries = t.getTable().entrySet();
            for( Map.Entry<String, Item> entry : entries ){
                s+= entry.getKey() + "  =>  " + entry.getValue().getKind() + "\t" + entry.getValue().getType().getClass().getSimpleName() + "\n";
            }
            t = t.getPrev();
        }
        return s;

    }
}
