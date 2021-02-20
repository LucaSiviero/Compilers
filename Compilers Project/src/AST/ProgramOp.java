package AST;

import AST.Functions.ProcOp;
import Visitor.VisitorInterface;

import java.util.LinkedList;

public class ProgramOp extends ASTNode{
    LinkedList<VarDeclOp> varDecls;
    LinkedList<ProcOp> procs;

    public ProgramOp(LinkedList<VarDeclOp> vars, LinkedList<ProcOp> procs){
        this.varDecls = vars;
        this.procs = procs;
    }
    @Override
    public <T> T accept(VisitorInterface<T> v) {
        return (v.Visit(this));
    }

    public LinkedList<VarDeclOp> getVarDecls() {
        return varDecls;
    }

    public LinkedList<ProcOp> getProcs() {
        return procs;
    }
}
