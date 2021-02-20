package Visitor;

import AST.*;
import AST.ArithmeticExprs.ArithmeticExpr;
import AST.BinaryExpr.*;
import AST.Const.*;
import AST.Expr.ExprCallProc;
import AST.Expr.ExprNode;
import AST.Expr.IdNode;
import AST.Functions.CallProcOp;
import AST.Functions.ProcBodyOp;
import AST.Functions.ProcOp;
import AST.Stat.*;
import AST.Type.*;
import AST.UnaryExpr.MinusOp;
import AST.UnaryExpr.NotOp;
import AST.UnaryExpr.UnaryExprNode;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.LinkedList;
import java.util.logging.Logger;

public class SyntaxTreeVisitor implements VisitorInterface<Node> {

    DocumentBuilderFactory docFactory;
    DocumentBuilder docBuilder;
    Logger log = Logger.getGlobal();
    Document doc;

    public SyntaxTreeVisitor(String pathname, ProgramOp node ) throws ParserConfigurationException, TransformerException {
        docFactory = DocumentBuilderFactory.newInstance();
        docBuilder = docFactory.newDocumentBuilder();
        doc = docBuilder.newDocument();
        doc.appendChild(Visit(node));

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(new File(pathname));
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

        transformer.transform(source, result);

    }


    @Override
    public Node Visit(ForOp node) {
        Element op = doc.createElement("ForOp");
        op.appendChild(node.getVarDecl().accept(this));
        op.appendChild(node.getEval().accept(this));
        op.appendChild(node.getAssign().accept(this));
        for(VarDeclOp v: node.getBody().getVarDecls()){
            op.appendChild(v.accept(this));
        }
        for(StatNode s : node.getBody().getStats()){
            op.appendChild(s.accept(this));
        }
        return op;
    }

    @Override
    public Node Visit(ASTNode node) {
        return null;
    }


    @Override
    public Node Visit(ReturnExprsOp node) {
        Element op = doc.createElement("ReturnExprsOp");
        for (ExprNode e : node.getExprs()){
            op.appendChild(e.accept(this));
        }
        return op;
    }


    @Override
    public Node Visit(WriteOp node) {
        Element op = doc.createElement("WriteOp");
        LinkedList<ExprNode> list = new LinkedList<ExprNode>();
        for (ExprNode e : list) {
            op.appendChild(e.accept(this));
        }
        return op;
    }


    @Override
    public Node Visit(MinusOp node) {
        Element op = doc.createElement("MinusOp");

        op.appendChild(node.getLink().accept(this));
        return op;
    }

    @Override
    public Node Visit(UnaryExprNode node) {
        return null;
    }

    @Override
    public Node Visit(ExprCallProc node) {
        Element op = doc.createElement("ExprCallProcOp");
        IdNode id = node.getId();
        LinkedList<ExprNode> exprs = node.getExprs();

        op.appendChild(id.accept(this));
        for (ExprNode e : exprs){
            op.appendChild(e.accept(this));
        }
        return op;
    }


    @Override
    public Node Visit(ReadOp node) {
        Element op = doc.createElement("ReadlnOp");

        for(IdNode i: node.getIds()){
            op.appendChild(i.accept(this));
        }
        return op;
    }


    @Override
    public Node Visit(NotOp node) {
        Element op = doc.createElement("NotOp");
        op.appendChild(node.getLink().accept(this));
        return op;
    }

    @Override
    public Node Visit(CallProcOp node) {
        Element op = doc.createElement(("CallProcOp"));

        op.appendChild(node.getId().accept(this));
        Element op2 = doc.createElement("Exprs");

        for (ExprNode e : node.getExprs())
            op2.appendChild(e.accept(this));

        op.appendChild(op2);
        return op;
    }


    public Node Visit(ProgramOp node) {

        Element op = doc.createElement("ProgramOp");
        if(node.getVarDecls() != null) {
            for (VarDeclOp s : node.getVarDecls()) {
                op.appendChild(s.accept(this));
            }
        }

        for (ProcOp s : node.getProcs()) {
            op.appendChild(s.accept(this));
        }
        return op;
    }


    public Node Visit(ProcOp node){

        Element op = doc.createElement("ProcOp");

        op.appendChild(node.getFun_name().accept(this));
        if(node.getPars() != null) {
            for (ParDeclOp s : node.getPars()) {
                if (s == null) break;
                op.appendChild(s.accept(this));
            }
        }
        if(node.getReturns() != null) {
            for (ResultTypeOp s : node.getReturns()) {
                op.appendChild(s.getType().accept(this));
            }
        }
        op.appendChild(node.getProcBody().accept(this));
        return op;
    }


    public Node Visit(VarDeclOp node){
        Element op = doc.createElement("VarDeclOp");
        //System.out.println(node.getIds());
        op.appendChild(node.getIds().accept(this));
        op.appendChild(node.getType().accept(this));


        //System.out.println(ids);


        return op;

    }

    public Node Visit(ParDeclOp node){

        Element op = doc.createElement("ParDeclOp");
        op.appendChild( node.getType().accept(this));
        if(node.getIds() != null) {
            for (IdNode s : node.getIds()) {
                if (s == null) break;
                op.appendChild(s.accept(this));
            }
        }
        return op;
    }

    public Node Visit(AddOp node) {
        Element op = doc.createElement(node.getClass().getSimpleName());
        op.appendChild(node.getLeft().accept(this));
        op.appendChild(node.getRight().accept(this));
        return op;
    }

    @Override
    public Node Visit(DivOp node) {
        Element op = doc.createElement(node.getClass().getSimpleName());

        op.appendChild(node.getLeft().accept(this));
        op.appendChild(node.getRight().accept(this));
        return op;
    }

    @Override
    public Node Visit(SubOp node) {
        Element op = doc.createElement(node.getClass().getSimpleName());

        op.appendChild(node.getLeft().accept(this));
        op.appendChild(node.getRight().accept(this));
        return op;
    }

    @Override
    public Node Visit(AndOp node) {
        Element op = doc.createElement(node.getClass().getSimpleName());

        op.appendChild(node.getLeft().accept(this));
        op.appendChild(node.getRight().accept(this));
        return op;
    }

    @Override
    public Node Visit(OrOp node) {
        Element op = doc.createElement(node.getClass().getSimpleName());

        op.appendChild(node.getLeft().accept(this));
        op.appendChild(node.getRight().accept(this));
        return op;
    }

    @Override
    public Node Visit(EqOp node) {
        Element op = doc.createElement(node.getClass().getSimpleName());
        op.appendChild(node.getLeft().accept(this));
        //System.out.println(node.getRight().accept(this));
        op.appendChild(node.getRight().accept(this));
        return op;
    }

    @Override
    public Node Visit(GeOp node) {
        Element op = doc.createElement(node.getClass().getSimpleName());

        op.appendChild(node.getLeft().accept(this));
        op.appendChild(node.getRight().accept(this));
        return op;
    }

    @Override
    public Node Visit(GtOp node) {
        Element op = doc.createElement(node.getClass().getSimpleName());
        ExprNode left = node.getLeft();
        ExprNode right = node.getRight();

        op.appendChild(left.accept(this));
        op.appendChild(right.accept(this));


        return op;
    }

    @Override
    public Node Visit(LeOp node) {
        Element op = doc.createElement(node.getClass().getSimpleName());

        op.appendChild(node.getLeft().accept(this));
        op.appendChild(node.getRight().accept(this));
        return op;    }

    @Override
    public Node Visit(LtOp node) {
        Element op = doc.createElement(node.getClass().getSimpleName());

        op.appendChild(node.getLeft().accept(this));
        op.appendChild(node.getRight().accept(this));
        return op;    }

    @Override
    public Node Visit(NeOp node) {
        Element op = doc.createElement(node.getClass().getSimpleName());

        op.appendChild(node.getLeft().accept(this));
        op.appendChild(node.getRight().accept(this));
        return op;    }

    @Override
    public Node Visit(BinaryExprNode node) {
        return null;
    }

    @Override
    public Node Visit(ArithmeticExpr node) {
        return null;
    }

    @Override
    public Node Visit(BooleanConst node) {
        return null;
    }



    @Override
    public Node Visit(MultOp node) {
        Element op = doc.createElement(node.getClass().getSimpleName());
        ExprNode left = node.getLeft();
        ExprNode right = node.getRight();

        op.appendChild(left.accept(this));

        //System.out.println(right.getClass());

        if(right.accept(this) != null){
            op.appendChild(right.accept(this));
        }


        return op;
    }


    public Node Visit(IfStatOp node) {
        Element op = doc.createElement("IfStatOp");
        op.appendChild(node.getExpr().accept(this));
        op.appendChild(node.getBody().accept(this));
        if(node.getElif_list() != null) {
            for (ElifOp s : node.getElif_list()) {
                if (s == null) break;
                op.appendChild(s.accept(this));
            }
        }
        if(node.getElse() == null){
            return op;
        }
        op.appendChild(node.getElse().accept(this));

        return op;
    }


    public Node Visit(ElseNode node) {

        Element op = doc.createElement("ElseNode");
        op.appendChild(node.getBody().accept(this));
        return op;

    }

    public Node Visit(ElifOp node) {

        Element op = doc.createElement("ElifOp");
        op.appendChild(node.getExpr().accept(this));
        op.appendChild(node.getBody().accept(this));
        return op;
    }

    public Node Visit(BodyOp node) {
        Element op = doc.createElement("BodyOp");
        if(node.getStats() != null) {
            for (StatNode s : node.getStats()) {
                if (s == null) break;
                op.appendChild(s.accept(this));
            }
        }

        return op;
    }

    public Node Visit(BodyOpElif node) {
        Element op = doc.createElement("BodyOpElif");
        if(node.getElif_list() != null) {
            for (StatNode s : node.getElif_list()) {
                if (s == null) break;
                op.appendChild(s.accept(this));
            }
        }
        return op;
    }

    @Override
    public Node Visit(BoolType node) {
        return null;
    }

    @Override
    public Node Visit(FloatType node) {return null;
    }

    @Override
    public Node Visit(IntegerType node) {
        return null;
    }

    @Override
    public Node Visit(StringType node) {
        return null;
    }

    public Node Visit(ProcBodyOp node) {
        Element op = doc.createElement("ProcBodyOp");
        if(node.getVarDecls() != null) {
            for (VarDeclOp s : node.getVarDecls()) {
                if (s == null) break;
                op.appendChild(s.accept(this));
            }
        }
        if(node.getStats() != null) {
            for (StatNode s : node.getStats()) {
                if (s == null) break;
                op.appendChild(s.accept(this));
            }

        }
        /*if (node.getExprs() == null){
            return op;
        }*/
        else if(node.getEs() != null) {
            for(ExprNode e : node.getEs()) {
                op.appendChild(e.accept(this));
            }
        }

        return op;
    }



    public Node Visit(AssignOp node) {

        Element op = doc.createElement("AssignOp");
        LinkedList<IdNode> list = node.getIds();
        if(list != null) {
            for (IdNode s : list) {
                if (s == null) break;

                op.appendChild(s.accept(this));
            }
        }
        LinkedList<ExprNode> exprs = node.getExprs();
        if(exprs != null) {
            for (ExprNode s : exprs) {
                if (s.getClass().equals(FloatConst.class)){
                    //System.out.println(((FloatConst) s).getValue());
                    doc.createTextNode("[FloatConst," +((FloatConst) s).getValue() + "]" );
                }
                op.appendChild(s.accept(this));
                //op.appendChild(s.accept(this));
            }
        }
        else {
            op.appendChild(node.getId().accept(this));
            op.appendChild(node.getExpr().accept(this));
        }
        return op;
    }

    public Node Visit(IdListInitOp node) {
        Element op = doc.createElement("IdListInitOp");
        LinkedList<ExprNode> es = new LinkedList<ExprNode>();
        if(node.getIds() != null) {
            for (IdNode s : node.getIds()) {
                if (s == null) break;

                op.appendChild(s.accept(this));
            }
        }
        if(node.getExprs() != null) {
            try {
                int i = 0;
                while(i < node.getExprs().size()) {
                    ExprNode s = node.getExprs().get(i);
                    if(s!= null) {
                        if (s.getClass().getSimpleName().equals(BooleanConst.class.getSimpleName())|| s.getClass().getSimpleName().equals(FloatConst.class.getSimpleName()) || s.getClass().getSimpleName().equals(IntegerConst.class.getSimpleName()) || s.getClass().getSimpleName().equals(StringConst.class.getSimpleName()) || s.toId(s) != null) {
                            es.add(s);
                            i++;
                        }
                    }
                    else i++;
                }
            }
            catch (Exception excep) {     }

            try {
                for (ExprNode e : es) {

                    if(e.getClass().equals(BooleanConst.class)){
                        op.appendChild(e.accept(this));
                    }
                    if (e.getClass().equals(FloatConst.class)) {

                        op.appendChild(e.accept(this));
                    }

                    if (e.getClass().equals(IntegerConst.class)) {
                        op.appendChild(e.accept(this));
                    }
                    if (e.getClass().equals(StringConst.class)) {
                        op.appendChild(e.accept(this));
                    }

                    if(e.toId(e) != null) {
                        op.appendChild(e.accept(this));
                    }
                }
            }
            catch (Exception e) {   }
        }
        return op;
    }

    @Override
    public Node Visit(ResultTypeOp node) {

        Element op = doc.createElement("ResultTypeOp");

        op.appendChild(node.getType().accept(this));
        return op;
    }

    public Node Visit(WhileOp node) {
        Element op = doc.createElement("WhileOp");
        op.appendChild(node.getExpr().accept(this));
        if (node.getBody() == null) {
            op.appendChild(node.getBody2().accept(this));
            return op;
        }
        else {
            op.appendChild(node.getBody().accept(this));
            op.appendChild(node.getBody2().accept(this));
        }
        return op;
    }


    public Node Visit(IdNode node) {
        return doc.createTextNode("[ID," + node.getId() + "]");
    }

    @Override
    public Node Visit(StatNode node) {
        return null;
    }

    public Node Visit(GenericType node) {
        return  doc.createTextNode("[" + node.getClass().getSimpleName()+ "]");
    }

    public Node Visit(ConstNode node) {
        String kind = node.getClass().getSimpleName();
        //System.out.println(kind);
        String argument = node.getArgument();
        /*if(argument == null) return doc.createTextNode("[" + kind + "]");
        else return doc.createTextNode("[" + kind + "," + argument + "]");*/
        return null;
    }

    @Override
    public Node Visit(IntegerConst node) {
        return doc.createTextNode("[IntegerConst," + node.getValue() + "]");
    }

    @Override
    public Node Visit(FloatConst node) {
        return doc.createTextNode("[FloatConst," + node.getArgument() + "]");
    }

    @Override
    public Node Visit(NullConst node) {
        return doc.createTextNode("");
    }

    @Override
    public Node Visit(StringConst node) {
        return doc.createTextNode("[StringConst," + node.getArgument() + "]");
        //return null;
    }
}

