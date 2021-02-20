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

public interface VisitorInterface <T>{

    public T Visit(ForOp node);

    public  T Visit(ASTNode node );

    public  T Visit(ProgramOp node );

    public  T Visit(VarDeclOp node );

    public T Visit(IntegerConst node);

    public  T Visit(ProcOp node );

    public  T Visit(GenericType node );

    public  T Visit(IdListInitOp node );

    public  T Visit(ResultTypeOp node );

    public  T Visit(ReturnExprsOp node );

    public  T Visit(ParDeclOp node );

    public  T Visit(IdNode node );

    public  T Visit(StatNode node );

    public  T Visit(IfStatOp node );

    public  T Visit(ElifOp node );

    public  T Visit(ElseNode node );

    public  T Visit(WhileOp node );

    public  T Visit(ReadOp node );

    public  T Visit(WriteOp node );

    public  T Visit(AssignOp node );

    public  T Visit(CallProcOp node );

    public  T Visit(AddOp node );

    public  T Visit(AndOp node );

    public  T Visit(OrOp node );

    public  T Visit(NotOp node );

    public  T Visit(DivOp node );

    public  T Visit(EqOp node );

    public  T Visit(GeOp node );

    public  T Visit(SubOp node );

    public  T Visit(GtOp node ) ;

    public  T Visit(LeOp node );

    public  T Visit(LtOp node );

    public  T Visit(MultOp node );

    public  T Visit(NeOp node );

    public  T Visit(BinaryExprNode node );

    public  T Visit(ArithmeticExpr node);

    public  T Visit(BooleanConst node );

    public  T Visit(ConstNode node );

    public  T Visit(FloatConst node );

    public  T Visit(NullConst node );

    public  T Visit(StringConst node );

    public  T Visit(ProcBodyOp node );

    public  T Visit(BodyOp node );

    public  T Visit(BodyOpElif node );

    public  T Visit(BoolType node );

    public  T Visit(FloatType node );

    public  T Visit(IntegerType node );

    public  T Visit(StringType node );

    public  T Visit(MinusOp node );

    public  T Visit(UnaryExprNode node );

    public T Visit(ExprCallProc node);

}
