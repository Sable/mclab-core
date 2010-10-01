package natlab.example;

import ast.*;

public class JesseRewrite
{

    public static ASTNode addInstruments(ASTNode node)
    {

        if( node instanceof Function )
            return instrumentFunction( (Function)node );
        else if( node instanceof Script )
            return instrumentScript( (Script)node );
        else if( node instanceof List )
            return instrumentList( (List)node );
        else if( node instanceof AssignStmt )
            return instrumentAssignStmt( (AssignStmt)node );
        else
            return instrumentASTNode( node );
    }

    public static Stmt genGlobal()
    {
        return new GlobalStmt(new List().add( new Name("JESSECOUNT")));
    }
    public static Stmt genIf()
    {
        Name jessecount1 = new Name("JESSECOUNT");
        Name jessecount2 = new Name("JESSECOUNT");
        Name jessecount3 = new Name("JESSECOUNT");
        NameExpr jcexp1 = new NameExpr(jessecount1);
        NameExpr jcexp2 = new NameExpr(jessecount2);
        NameExpr jcexp3 = new NameExpr(jessecount3);
        Expr size = new NameExpr( new Name("size") );
        size = new ParameterizedExpr( size, new List().add(jcexp1) );
        Expr z1 = new IntLiteralExpr( new natlab.DecIntNumericLiteralValue("0") );
        Expr z2 = new IntLiteralExpr( new natlab.DecIntNumericLiteralValue("0") );
        Expr mat = new MatrixExpr( new List().add(new Row( new List().add(z1).add(z2) )) );
        Expr eq = new EQExpr( jcexp2, mat );
        Expr all = new NameExpr( new Name("all") );
        all = new ParameterizedExpr( all, new List().add(eq) );
        Expr z3 = new IntLiteralExpr( new natlab.DecIntNumericLiteralValue("0") );
        IfBlock block = new IfBlock( all, new List().add( new AssignStmt( jcexp3, z3 ) ) );
        IfStmt ifstmt = new IfStmt( new List().add( block ), new Opt() );

        return ifstmt;
    }
    public static Stmt genIncrement()
    {
        Name jessecount1 = new Name("JESSECOUNT");
        Name jessecount2 = new Name("JESSECOUNT");
        NameExpr jcexp1 = new NameExpr(jessecount1);
        NameExpr jcexp2 = new NameExpr(jessecount1);
        Expr incExpr = new IntLiteralExpr( new natlab.DecIntNumericLiteralValue( "1" ) );
        incExpr = new PlusExpr( jcexp1, incExpr );
        return new AssignStmt( jcexp2, incExpr );
    }
    public static Function instrumentFunction( Function node )
    {
        for( int i = 0 ; i< node.getNumNestedFunction(); i++ )
            node.setNestedFunction( (Function)addInstruments(node.getNestedFunction(i)), i);
        List body = node.getStmts();
        body = (List)addInstruments( body );

        body.insertChild( genGlobal(), 0 );

        body.insertChild( genIf(), 1 );

        return node;
    }

    public static Script instrumentScript( Script node )
    {
        List body = node.getStmts();
        body = (List) addInstruments( body );

        body.insertChild( genGlobal(), 0 );
        body.insertChild( genIf(), 1 );

        return node;
    }

    public static List instrumentList( List node )
    {
        for( int i=0; i< node.getNumChild(); i++ ){
            ASTNode child = node.getChild(i);
            ASTNode newChild = addInstruments( child );
            if( !( child instanceof List ) && ( newChild instanceof List ) ){
                node.removeChild(i);
                for( int j=0; j<newChild.getNumChild(); j++ ){
                    node.insertChild( newChild.getChild( j ), i );
                    i++;
                }
                i--;
            }
            else
                node.setChild( newChild, i );
        }
        return node;
    }

    public static ASTNode instrumentAssignStmt( AssignStmt node )
    {
        boolean hasJesse = false;
        for( String s: node.getLValues() ){
            if( s.equals("jesse") ){
                hasJesse = true;
                break;
            }
        }
        if( hasJesse ){
            List newNodes = new List();
            newNodes.add( genIncrement() );
            newNodes.add( node );
            return newNodes;
        }
        else
            return node;
    }

    public static ASTNode instrumentASTNode( ASTNode node )
    {
        for( int i=0; i< node.getNumChild(); i++ ){
            node.setChild( addInstruments( node.getChild(i) ), i );
        }
        return node;
    }
}