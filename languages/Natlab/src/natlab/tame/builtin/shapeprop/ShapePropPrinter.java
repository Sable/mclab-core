package natlab.tame.builtin.shapeprop;

import java.io.IOException;
import java.io.StringReader;

import beaver.Symbol;
import beaver.Parser;

import natlab.tame.builtin.shapeprop.ast.*;

public class ShapePropPrinter {
	
    public static SPCaselist parse(String source){
    	//System.err.println("parsing: "+source);
    	ShapePropParser parser = new ShapePropParser();
    	ShapePropScanner input = new ShapePropScanner(new StringReader(source));
    	try{
    		SPCaselist splist = (SPCaselist) parser.parse(input);
        	return splist;
    	}catch(Exception e){
    		e.printStackTrace();
    		return null;
    	}
    }
	
	static public void main(String[] args) throws IOException, Parser.Exception
	{
		System.out.println("print:   "+parse("$,m=prescalar()->[m,m]||M=[],($,m=prescalar(n),M=[M,m])+->M||[1,n],M=prevector(n)->M"));
		String s1 = parse("$,m=prescalar()->[m,m]||M=[],($,m=prescalar(n),M=[M,m])+->M||[1,n],M=prevector(n)->M").toString();
		System.out.println("reparsed "+parse(s1));
		
		System.out.println("print:   "+parse("[1,n]|[n,1]->$||[m,n]->[1,n]||M,M(1)=1->M,M||$|M,$|M->M||M,[],$,d=prescalar(),M(d)=1->M,M"));
		String s2 = parse("[1,n]|[n,1]->$||[m,n]->[1,n]||M,M(1)=1->M,M||$|M,$|M->M||M,[],$,d=prescalar(),M(d)=1->M,M").toString();
		System.out.println("reparsed "+parse(s2));
		
		System.out.println("print:   "+parse("($|M)*->M"));
		String s3 = parse("($|M)*->M").toString();
		System.out.println("reparsed "+parse(s3));
		
		System.out.println("print:   "+parse("M->M"));
		String s4 = parse("M->M").toString();
		System.out.println("reparsed "+parse(s4));
		
		System.out.println("print:   "+parse("numargout(1),[j,k],n=min(j,k)->[n,1]||numargout(2),[j,k]->[j,j],[j,k],[k,k]||numargout(3),[j,k]->[j,j],[j,k],[k,k]||[j,k],value(0),n=min(j,k)->[j,n],[n,k],[k,k]||[j,k],stringValue('econ'),n=min(j,k)->[j,n],[n,n],[k,n]"));
		String s5 = parse("numargout(1),[j,k],n=min(j,k)->[n,1]||numargout(2),[j,k]->[j,j],[j,k],[k,k]||numargout(3),[j,k]->[j,j],[j,k],[k,k]||[j,k],value(0),n=min(j,k)->[j,n],[n,k],[k,k]||[j,k],stringValue('econ'),n=min(j,k)->[j,n],[n,n],[k,n]").toString();
		System.out.println("reparsed "+parse(s5));
		
		System.out.println("print:   "+parse("$,$|[m,n]->[m,n]||$|[m,n],$->[m,n]||[m,k]|[k,n]->[m,n]"));
		String s6 = parse("$,$|[m,n]->[m,n]||$|[m,n],$->[m,n]||[m,k]|[k,n]->[m,n]").toString();
		System.out.println("reparsed "+parse(s6));
		
		System.out.println("print:   "+parse("[m,m]->[m,m],$,$"));
		String s7 = parse("[m,m]->[m,m],$,$").toString();
		System.out.println("reparsed "+parse(s7));
		
		System.out.println("print:   "+parse("$,m=prescalar()->[m,m]||M=[],($,m=prescalar(),M=[M,m])+->M||[1,n],M=prevector()->M"));
		String s8 = parse("$,m=prescalar()->[m,m]||M=[],($,m=prescalar(),M=[M,m])+->M||[1,n],M=prevector()->M").toString();
		System.out.println("reparsed "+parse(s8));
		
		System.out.println("print:   "+parse("$->[]||M,n=presize(1),M(1)=0,(N,n=n+presize(1),N(1)=0,isequal(N,M))*,M(1)=n->M"));
		String s9 = parse("$->[]||M,n=presize(1),M(1)=0,(N,n=n+presize(1),N(1)=0,isequal(N,M))*,M(1)=n->M").toString();
		System.out.println("reparsed "+parse(s9));
		
		System.out.println("print:   "+parse("$->[]||M,n=presize(2),M(2)=0,(N,n=n+presize(2),N(2)=0,isequal(N,M))*,M(2)=n->M"));
		String s10 = parse("$->[]||M,n=presize(2),M(2)=0,(N,n=n+presize(2),N(2)=0,isequal(N,M))*,M(2)=n->M").toString();
		System.out.println("reparsed "+parse(s10));
		
		System.out.println("print:   "+parse("$->[]||$,d=prescalar(),M,n=presize(d),M(d)=0,(N,n=n+presize(d),N(d)=0,isequal(N,M))*,M(d)=0->M"));
		String s11 = parse("$->[]||$,d=prescalar(),M,n=presize(d),M(d)=0,(N,n=n+presize(d),N(d)=0,isequal(N,M))*,M(d)=0->M").toString();
		System.out.println("reparsed "+parse(s11));
		
		System.out.println("print:   "+parse("$|M->M||$,$,n=prescalar()->[1,n]||M,$,n=prescalar(),M(1)=n->M||M,[],$->M"));
		String s12 = parse("$|M->M||$,$,n=prescalar()->[1,n]||M,$,n=prescalar(),M(1)=n->M||M,[],$->M").toString();
		System.out.println("reparsed "+parse(s12));
		
		System.out.println("parsed:  "+parse("[1,j]|[j,1],($,k=presaclar())?,n=j+abs(k)->[n,n]||[m,n],($,k=prescalar())?->[k,1]"));
		String s13 = parse("[1,j]|[j,1],($,k=presaclar())?,n=j+abs(k)->[n,n]||[m,n],($,k=prescalar())?->[k,1]").toString();
		System.out.println("reparsed "+parse(s13));
	}
}