package Visitor;

import AST.*;
import AST.ArithmeticExprs.ArithmeticExpr;
import AST.BinaryExpr.*;
import AST.Const.*;
import AST.Expr.ExprCallProc;
import AST.Expr.IdNode;
import AST.Functions.CallProcOp;
import AST.Functions.ProcBodyOp;
import AST.Functions.ProcOp;
import AST.Stat.*;
import AST.Type.*;
import AST.UnaryExpr.MinusOp;
import AST.UnaryExpr.NotOp;
import AST.UnaryExpr.UnaryExprNode;
import SymbolTable.Item;
import SymbolTable.SymbolTable;

public class RootSymbolTableVisitor implements VisitorInterface<Item> {
    SymbolTable rootTable;

    public RootSymbolTableVisitor() {
        rootTable = new SymbolTable(null);
    }

    public SymbolTable generateRootTable(ProgramOp node) {
        Visit(node);
        return rootTable;
    }


    @Override
    public Item Visit(ForOp node) {
        return null;
    }

    @Override
    public Item Visit(ASTNode node) {
        return null;
    }

    @Override
    public Item Visit(ProgramOp node) {
        //Check di dichiarazione del main.
        boolean isMainDefined = false;
        for(ProcOp p : node.getProcs()) {
            if(p.getFun_name().getId().equals("main")) {
                isMainDefined = true;
            }
            p.accept(this);
        }

        if(!isMainDefined) {
            System.err.println("Il metodo main non è presente");
        }
        return null;
    }

    @Override
    public Item Visit(VarDeclOp node) {
        return null;
    }

    @Override
    public Item Visit(IntegerConst node) {
        return null;
    }

    @Override
    public Item Visit(ProcOp node) {
        return null;
    }

    @Override
    public Item Visit(GenericType node) {
        return null;
    }

    @Override
    public Item Visit(IdListInitOp node) {
        return null;
    }

    @Override
    public Item Visit(ResultTypeOp node) {
        return null;
    }

    @Override
    public Item Visit(ReturnExprsOp node) {
        return null;
    }

    @Override
    public Item Visit(ParDeclOp node) {
        return null;
    }

    @Override
    public Item Visit(IdNode node) {
        return null;
    }

    @Override
    public Item Visit(StatNode node) {
        return null;
    }

    @Override
    public Item Visit(IfStatOp node) {
        return null;
    }

    @Override
    public Item Visit(ElifOp node) {
        return null;
    }

    @Override
    public Item Visit(ElseNode node) {
        return null;
    }

    @Override
    public Item Visit(WhileOp node) {
        return null;
    }

    @Override
    public Item Visit(ReadOp node) {
        return null;
    }

    @Override
    public Item Visit(WriteOp node) {
        return null;
    }

    @Override
    public Item Visit(AssignOp node) {
        return null;
    }

    @Override
    public Item Visit(CallProcOp node) {
        return null;
    }

    @Override
    public Item Visit(AddOp node) {
        return null;
    }

    @Override
    public Item Visit(AndOp node) {
        return null;
    }

    @Override
    public Item Visit(OrOp node) {
        return null;
    }

    @Override
    public Item Visit(NotOp node) {
        return null;
    }

    @Override
    public Item Visit(DivOp node) {
        return null;
    }

    @Override
    public Item Visit(EqOp node) {
        return null;
    }

    @Override
    public Item Visit(GeOp node) {
        return null;
    }

    @Override
    public Item Visit(SubOp node) {
        return null;
    }

    @Override
    public Item Visit(GtOp node) {
        return null;
    }

    @Override
    public Item Visit(LeOp node) {
        return null;
    }

    @Override
    public Item Visit(LtOp node) {
        return null;
    }

    @Override
    public Item Visit(MultOp node) {
        return null;
    }

    @Override
    public Item Visit(NeOp node) {
        return null;
    }

    @Override
    public Item Visit(BinaryExprNode node) {
        return null;
    }

    @Override
    public Item Visit(ArithmeticExpr node) {
        return null;
    }

    @Override
    public Item Visit(BooleanConst node) {
        return null;
    }

    @Override
    public Item Visit(ConstNode node) {
        return null;
    }

    @Override
    public Item Visit(FloatConst node) {
        return null;
    }

    @Override
    public Item Visit(NullConst node) {
        return null;
    }

    @Override
    public Item Visit(StringConst node) {
        return null;
    }

    @Override
    public Item Visit(ProcBodyOp node) {
        return null;
    }

    @Override
    public Item Visit(BodyOp node) {
        return null;
    }

    @Override
    public Item Visit(BodyOpElif node) {
        return null;
    }

    @Override
    public Item Visit(BoolType node) {
        return null;
    }

    @Override
    public Item Visit(FloatType node) {
        return null;
    }

    @Override
    public Item Visit(IntegerType node) {
        return null;
    }

    @Override
    public Item Visit(StringType node) {
        return null;
    }

    @Override
    public Item Visit(MinusOp node) {
        return null;
    }

    @Override
    public Item Visit(UnaryExprNode node) {
        return null;
    }

    @Override
    public Item Visit(ExprCallProc node) {
        return null;
    }
}
