package natlab.tame.builtin.shapeprop;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import natlab.tame.builtin.shapeprop.ast.SPNode;
import natlab.tame.valueanalysis.components.shape.HasShape;
import natlab.tame.valueanalysis.components.shape.Shape;
import natlab.tame.valueanalysis.value.Args;
import natlab.tame.valueanalysis.value.Value;
import beaver.Parser;

public class ShapePropTool<V extends Value<V>> {
	
	static boolean Debug = false;
	
	/**
	 * this static method is called by Builtin class.
	 * @param equation
	 * @return
	 */
    public static SPNode parse(String equation) {
    	if (Debug) System.out.println("parsing: "+equation);
    	ShapePropParser parser = new ShapePropParser();
    	ShapePropScanner scanner = new ShapePropScanner(new StringReader(equation));
    	try {
    		SPNode sp = (SPNode) parser.parse(scanner);
        	return sp;
    	} catch(Exception e) {
    		e.printStackTrace();
    		return null;
    	}
    }
    
    /**
     * this method is called by ShapePropagator class, the input arguments are the AST tree which is 
     * the result of parse method, and a list of V extends Value<V> which is input arguments of the built-in.
     * @param tree
     * @param argValues
     * @return
     */
    public List<Shape<V>> matchByValues(SPNode<V> tree, Args<V> argValues) {
		if (Debug) System.out.println("inside ShapePropTool matchByValues method.");
		/*
		 * first, test whether the Args is empty, if not, every element value should has a shape; 
		 * if the Args is empty, we can still do shape info propagation, i.e. a=ones().
		 */
    	if (argValues.size()!=0) {
        	if (Debug) System.out.println(tree+" with arguments "+argValues);
    		for (Value<V> arg : argValues) {
    			if (((HasShape<V>)arg).getShape()==null) {
    				System.err.println(arg+"'s shape info is unavailable.");
    				//FIXME what if arg's shape info is null, if continue like this, the program will throw null pointer exception.
    				return null;
    			}
    		}
    	}
    	// do shape info matching.
    	ShapePropMatch<V> spmatch = tree.match(true, new ShapePropMatch<V>(), argValues, argValues.getNargout());
    	/*
    	 * if shape info propagation fails, return null.
    	 */
        if (spmatch == null || spmatch.isError || spmatch.howManyMatched != argValues.size()) {
        	System.err.println("shape info propagation fails, since argument(s) "+argValues+" cannot match "+tree);
        	return null;
        }
        else {
            List<Shape<V>> results = spmatch.getAllResults();
            if (Debug) System.out.println("all the results are "+results);
            return results;        	
        }
    }
    
    /**
     * This main entry point is used for testing the scanner and parser of shape equation language.
     * @param args
     * @throws IOException
     * @throws Parser.Exception
     */
	public static void main(String[] args) throws IOException, Parser.Exception {
				
		System.out.println("print:   "+parse("$,'a2'->M||#->[]"));
		String s0 = parse("$,'a2'->M||#->[]").toString();
		System.out.println("reparsed "+parse(s0));
		
		System.out.println("print:   "+parse("$,m=previousScalar()->[m,m]||M=[],($,m=previousScalar(n),M=[M,m])+->M||[1,n],M=prevector(n)->M"));
		String s1 = parse("$,m=previousScalar()->[m,m]||M=[],($,m=previousScalar(n),M=[M,m])+->M||[1,n],M=prevector(n)->M").toString();
		System.out.println("reparsed "+parse(s1));
		
		System.out.println("print:   "+parse("[1,n]|[n,1]->$||[m,n]->[1,n]||M,M(1)=1->M,M||$|M,$|M->M||M,[],$,d=previousScalar(),M(d)=1->M,M"));
		String s2 = parse("[1,n]|[n,1]->$||[m,n]->[1,n]||M,M(1)=1->M,M||$|M,$|M->M||M,[],$,d=previousScalar(),M(d)=1->M,M").toString();
		System.out.println("reparsed "+parse(s2));
		
		System.out.println("print:   "+parse("($|M)*->M"));
		String s3 = parse("($|M)*->M").toString();
		System.out.println("reparsed "+parse(s3));
		
		System.out.println("print:   "+parse("M->M"));
		String s4 = parse("M->M").toString();
		System.out.println("reparsed "+parse(s4));
		
		System.out.println("print:   "+parse("numOutput(1),$|M,k=previousShapeDim()->[1,k]||numOutput(2),[m,n]->$,$"));
		String s5 = parse("numOutput(1),$|M,k=previousShapeDim()->[1,k]||numOutput(2),[m,n]->$,$").toString();
		System.out.println("reparsed "+parse(s5));
		
		System.out.println("print:   "+parse("$,$|[m,n]->[m,n]||$|[m,n],$->[m,n]||[m,k]|[k,n]->[m,n]"));
		String s6 = parse("$,$|[m,n]->[m,n]||$|[m,n],$->[m,n]||[m,k]|[k,n]->[m,n]").toString();
		System.out.println("reparsed "+parse(s6));
		
		System.out.println("print:   "+parse("[m,m]->[m,m],$,$"));
		String s7 = parse("[m,m]->[m,m],$,$").toString();
		System.out.println("reparsed "+parse(s7));
		
		System.out.println("print:   "+parse("$,m=previousScalar()->[m,m]||M=[],($,m=previousScalar(),M=[M,m])+->M||[1,n],M=prevector()->M"));
		String s8 = parse("$,m=previousScalar()->[m,m]||M=[],($,m=previousScalar(),M=[M,m])+->M||[1,n],M=prevector()->M").toString();
		System.out.println("reparsed "+parse(s8));
		
		System.out.println("print:   "+parse(
				"M,n=previousShapeDim(1),K=copy(M),K(1)=0,(#,k=previousShapeDim(1),N=copy(#),N(1)=0,isequal(K,N),n=increment(k))*,K(1)=n->K" +
				"||$,n=previousShapeDim(1),K=copy($),K(1)=0,(#,k=previousShapeDim(1),N=copy(#),N(1)=0,isequal(K,N),n=increment(k))*,K(1)=n->K"));
		String s9 = parse("" +
				"M,n=previousShapeDim(1),K=copy(M),K(1)=0,(#,k=previousShapeDim(1),N=copy(#),N(1)=0,isequal(K,N),n=increment(k))*,K(1)=n->K" +
				"||$,n=previousShapeDim(1),K=copy($),K(1)=0,(#,k=previousShapeDim(1),N=copy(#),N(1)=0,isequal(K,N),n=increment(k))*,K(1)=n->K").toString();
		System.out.println("reparsed "+parse(s9));
		
		System.out.println("print:   "+parse(
				"M,n=previousShapeDim(2),K=copy(M),K(2)=0,(#,k=previousShapeDim(2),N=copy(#),N(2)=0,isequal(K,N),n=increment(k))*,K(2)=n->K" +
				"||$,n=previousShapeDim(2),K=copy($),K(2)=0,(#,k=previousShapeDim(2),N=copy(#),N(2)=0,isequal(K,N),n=increment(k))*,K(2)=n->K"));
		String s10 = parse(
				"M,n=previousShapeDim(2),K=copy(M),K(2)=0,(#,k=previousShapeDim(2),N=copy(#),N(2)=0,isequal(K,N),n=increment(k))*,K(2)=n->K" +
				"||$,n=previousShapeDim(2),K=copy($),K(2)=0,(#,k=previousShapeDim(2),N=copy(#),N(2)=0,isequal(K,N),n=increment(k))*,K(2)=n->K").toString();
		System.out.println("reparsed "+parse(s10));
		
		System.out.println("print:   "+parse("$|M->M||$,$,n=previousScalar()->[1,n]||M,$,n=previousScalar(),M(1)=n->M||M,[],$->M"));
		String s11 = parse("$|M->M||$,$,n=previousScalar()->[1,n]||M,$,n=previousScalar(),M(1)=n->M||M,[],$->M").toString();
		System.out.println("reparsed "+parse(s11));
	}
}