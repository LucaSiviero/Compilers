package AST.Functions;

import AST.ASTNode;
import AST.Expr.IdNode;
import AST.ParDeclOp;
import AST.Type.ResultTypeOp;
import Visitor.VisitorInterface;

import java.util.LinkedList;

public class ProcOp extends ASTNode {
    IdNode fun_name;
    LinkedList<ParDeclOp> pars;
    LinkedList<ResultTypeOp> returns;
    ProcBodyOp procBody;
    boolean isMain;

    public ProcOp(IdNode fun_name, LinkedList<ParDeclOp> pars, LinkedList<ResultTypeOp> returns, ProcBodyOp procBody) {
        this.fun_name = fun_name;
        this.pars = pars;
        this.returns = returns;
        this.procBody = procBody;
    }

    @Override
    public <T> T accept(VisitorInterface<T> v) {
        return v.Visit(this);
    }

    public IdNode getFun_name() {
        return fun_name;
    }

    public LinkedList<ParDeclOp> getPars() {
        return pars;
    }

    public LinkedList<ResultTypeOp> getReturns() {
        return returns;
    }

    public ProcBodyOp getProcBody() {
        return procBody;
    }

    public Boolean getMain(){
        return this.isMain;
    }

    public void setMain(boolean main){
        this.isMain = main;
    }
}
