package natlab.tame.builtin.isComplexInfoProp;

import java.io.IOException;
import java.io.StringReader;
import java.util.*;
import java.lang.*;

import beaver.Parser;

import natlab.tame.builtin.isComplexInfoProp.ast.ICCaselist;
import natlab.tame.builtin.isComplexInfoProp.*;
import natlab.tame.valueanalysis.value.*;

public class isComplexInfoPropTool {

	
	 public static ICCaselist parse(String source){
	    	//System.err.println("parsing: "+source);
	    	isComplexInfoPropParser parser = new isComplexInfoPropParser();
	    	isComplexInfoPropScanner input = new isComplexInfoPropScanner(new StringReader(source));
	    	try{
	    		ICCaselist iclist = (ICCaselist) parser.parse(input);
	        	return iclist;
	    	}catch(Exception e){
	    		e.printStackTrace();
	    		return null;
	    	}
	    }
	 
	 
	 public static void main(String[] args) throws IOException, Parser.Exception
		{
		  
		    String s1, s2;
		    
		    //s1 = parse("R* -> R").toString();
		    System.out.println("parse result : "+parse("A+ -> NUMXARGS>0 ? X : R")+"\n");
		    
		 
		 
		}
}
