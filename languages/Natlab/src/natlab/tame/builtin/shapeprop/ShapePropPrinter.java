package natlab.tame.builtin.shapeprop;

import java.io.IOException;
import java.io.StringReader;

import beaver.Symbol;
import beaver.Parser;

import natlab.tame.builtin.shapeprop.ast.*;

public class ShapePropPrinter {
	
    public static SPCaselist parse(String source){
    	System.err.println("parsing: "+source);
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
		System.out.println("pretty-printed SP equ. for  true():\n"+parse("$,m=prescalar()->[m,m]||M=[],($,m=prescalar(n),M=[M,m])+->M||[1,n],M=prevector(n)->M"));
		System.out.println("pretty-printed SP equ. for  min():\n"+parse("[1,n]|[n,1]->$||[m,n]->[1,n]||M,M(1)=1->M,M||$|M,$|M->M||M,[],$,d=prescalar(),M(d)=1->M,M"));
		System.out.println("pretty-printed SP equ. for  complex():\n"+parse("($|M)*->M"));
		System.out.println("pretty-printed SP equ. for  real():\n"+parse("M->M"));
		System.out.println("pretty-printed SP equ. for  svd():\n"+parse("numargout(1),[j,k],n=min(j,k)->[n,1]||numargout(2),[j,k]->[j,j],[j,k],[k,k]||numargout(3),[j,k]->[j,j],[j,k],[k,k]||[j,k],value(0),n=min(j,k)->[j,n],[n,k],[k,k]||[j,k],stringValue('econ'),n=min(j,k)->[j,n],[n,n],[k,n]"));
		System.out.println("pretty-printed SP equ. for  plus():\n"+parse("M->M"));
		System.out.println("pretty-printed SP equ. for  mtimes():\n"+parse("$,$|[m,n]->[m,n]||$|[m,n],$->[m,n]||[m,k]|[k,n]->[m,n]"));
		System.out.println("pretty-printed SP equ. for  sqrtm():\n"+parse("[m,m]->[m,m],$,$"));
		System.out.println("pretty-printed SP equ. for  ones():\n"+parse("$,m=prescalar()->[m,m]||M=[],($,m=prescalar(),M=[M,m])+->M||[1,n],M=prevector()->M"));
		System.out.println("pretty-printed SP equ. for  vertcat():\n"+parse("$->[]||M,n=presize(1),M(1)=0,(N,n=n+presize(1),N(1)=0,isequal(N,M))*,M(1)=n->M"));
		System.out.println("pretty-printed SP equ. for  horzcat():\n"+parse("$->[]||M,n=presize(2),M(2)=0,(N,n=n+presize(2),N(2)=0,isequal(N,M))*,M(2)=n->M"));
		System.out.println("pretty-printed SP equ. for  cat():\n"+parse("$->[]||$,d=prescalar(),M,n=presize(d),M(d)=0,(N,n=n+presize(d),N(d)=0,isequal(N,M))*,M(d)=0->M"));
		System.out.println("pretty-printed SP equ. for  fft():\n"+parse("$|M->M||$,$,n=prescalar()->[1,n]||M,$,n=prescalar(),M(1)=n->M||M,[],$->M"));
		System.out.println("pretty-printed SP equ. for  diag():\n"+parse("[1,j]|[j,1],($,k=presaclar())?,n=j+abs(k)->[n,n]||[m,n],($,k=prescalar())?->[k,1]"));
	}
}