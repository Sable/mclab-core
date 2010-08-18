package natlab.toolkits.DependenceAnalysis;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.StringTokenizer;
import java.util.regex.PatternSyntaxException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import matlab.FunctionEndScanner;
import matlab.PositionMap;
import matlab.TranslationProblem;
import matlab.FunctionEndScanner.NoChangeResult;
import matlab.FunctionEndScanner.ProblemResult;
import matlab.FunctionEndScanner.TranslationResult;
import natlab.CommentBuffer;
import natlab.CompilationProblem;
import natlab.IntNumericLiteralValue;
import natlab.NatlabParser;
import natlab.NatlabScanner;
import natlab.toolkits.analysis.ForVisitor;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import beaver.Parser;

import ast.ASTNode;
import ast.AssignStmt;
import ast.FPLiteralExpr;
import ast.ForStmt;
import ast.IfStmt;
import ast.IntLiteralExpr;
import ast.NameExpr;
import ast.Program;
import ast.RangeExpr;
import ast.Stmt;
import ast.SwitchStmt;
import natlab.Main;

public class McFlatDriver extends ForVisitor{
   
private String fileName;
private Document doc;
private String dirName;

public String getFileName() {
	return fileName;
}
public String getDirName() {
	return dirName;
}
public void setDirName(String dirName) {
	this.dirName = dirName;
}
public void setFileName(String fName) {
	StringTokenizer st = new StringTokenizer(fName,".");
	fileName=st.nextToken();	
}
public McFlatDriver(){
	
}
private boolean checkForDependenceFile(){
	File file = new File(dirName + "/" + "dependenceProfiled" + fileName + ".xml");
	boolean exists = file.exists();
	if(exists)return true;
	else return false;
}

private boolean checkForDirectoryStructure(){
	File file = new File(dirName);	
	boolean exists = file.exists();
	if(exists)return true;
	else return false;
}

private boolean checkForProfiledFile(){
	File file = new File(dirName + "/" + "Profiled" + fileName + ".m");	
	boolean exists = file.exists();
	if(exists)return true;
	else return false;
}

private boolean checkForDataFile(){
	File file = new File(dirName + "/" + "RangeData" + fileName + ".xml");	
	boolean exists = file.exists();
	if(exists)return true;
	else return false;
}

private Program parseFile(String fName, Reader file, ArrayList errList ){
    NatlabParser parser = new NatlabParser();   
    NatlabScanner scanner = null;
    CommentBuffer cb = new CommentBuffer();
    
    parser.setCommentBuffer(cb);
    
    try{
        scanner = new NatlabScanner( file );
        scanner.setCommentBuffer( cb );
        try{
            
            Program prog = (Program)parser.parse(scanner);
	
            if( parser.hasError() ){
                String delim = "],[";
                for( String error : parser.getErrors()){
                    //return an array of string with {line, column, msg}
                    CompilationProblem parserError;
                    try{
                        String[] message = error.split(delim);
                        parserError = new CompilationProblem( Integer.valueOf(message[0]).intValue(),
                                                              Integer.valueOf(message[1]).intValue(),
                                                              message[3]);
                    }
                    catch( PatternSyntaxException e ){
                        parserError = new CompilationProblem( error );
                    }
                    errList.add(parserError);}
                prog = null;
            }
            return prog;
            
        }catch(Parser.Exception e){
            String ErrorString= e.getMessage()+"\n";
            for(String error : parser.getErrors()) {
                ErrorString+= error + "\n";
            }
            CompilationProblem Parsercerror = new CompilationProblem(ErrorString);
            errList.add(Parsercerror);
            return null;
        } 
    }catch(FileNotFoundException e){
        CompilationProblem FileNotFoundcerror = new CompilationProblem("File "+fName+" not found!\nAborting\n");
        errList.add(FileNotFoundcerror);
        return null;
    }
    catch(IOException e){
        CompilationProblem IOcerror = new CompilationProblem("Error translating "+fName+"\n"+e.getMessage());
        errList.add( IOcerror);
        return null;
    }
}


public void traverseFile(Program prog){
    prog.apply(this);    
}



	


public void caseLoopStmt(ASTNode node){
	
	if (node instanceof ForStmt){
		ForStmt fNode=(ForStmt)node;
		parseXmlFile(fNode);
	}
	else{
		//ForVisitor fVisitor=new ForVisitor(); 
		//node.applyAllChild(fVisitor);
		node.applyAllChild(this);
	}
}

public void caseBranchingStmt(ASTNode node){}

public void caseASTNode(ASTNode node) {}
public void caseIfStmt(IfStmt node){}
public void caseSwitchStmt(SwitchStmt node){}


private void writeToFile(Program prog){
  File f = new File(dirName);//this checks for the presence of directory	    
  if(!f.exists()){
     f.mkdir();
  }
  Writer output;        
  File file = new File(dirName+"/"+ fileName + ".m");
  try {
	  output = new BufferedWriter(new FileWriter(file));
	  output.write(prog.getPrettyPrinted());
	 output.close();
    } catch (IOException e) {			  
	 e.printStackTrace();
  }
	
}
/*
 * Assumption:Basic assumption is that input .m file would be annotated with loop numbers.
 * 
 */
public void checkFiles(Program prog,boolean flag){
	//if (checkForDependenceFile() && !flag){//This is the case where all information is present and you just have to present it and analy flag is set false by user.
		//1.Load xml file.
		//doc=loadXmlFile();
		//prog.apply(this);
		
		
	//}//end of if
	//else if(flag){// THis is the case when user ask to do DA at runtime.          	  
    	  if(!checkForProfiledFile()){     		  
    		  ProfilerDriver pDriver=new ProfilerDriver();
    		  pDriver.setDirName(dirName);
    	      pDriver.setFileName(fileName+".m");
    	      pDriver.traverseFile(prog);
    	      pDriver.generateInstrumentedFile(prog);
    	      //System.out.println("Generate the Data file and then place it in the same directory as ProfiledFile.m");
    	  }
    	  //else{
	      //if(checkForDataFile()){
    		  HeuristicEngineDriver hDriver=new HeuristicEngineDriver(fileName+".m");
    		  hDriver.setDirName(dirName);
    		  hDriver.parseXmlFile();
    		  DependenceAnalysisDriver dDriver=new DependenceAnalysisDriver();
    		  dDriver.setFileName(fileName); //this step is when dataFile is present
    		  //String fName=dDriver.getFileName();
    		  dDriver.setPredictedLoopValues(hDriver.getTable());//same is the case when data file is present.
    		  ArrayList errors = new ArrayList();
    		  String fName=dirName+"/"+"Profiled"+fileName+".m";
    		  try{    			  
                  Reader fileReader = new FileReader( fName);
                  Program program = parseFile(fName,  fileReader, errors );    
                  dDriver.setDirName(dirName);
                  dDriver.setATrans(flag);
          		  dDriver.traverseFile(program);
          		  
          		  //System.out.println(program.getPrettyPrinted());
          		  writeToFile(program);
              }catch(FileNotFoundException e){
                  System.err.println("File "+fName+" not found!\nAborting");
                  System.exit(1);
                }	    		  
	    	// }//end of if
	    	// else{ 
	    		 //System.out.println("Data file doesnot exist,doing the data dependence computation at runtime"); 
	    		// DependenceAnalysisDriver dDriver=new DependenceAnalysisDriver();
	    		 // ArrayList errors = new ArrayList();
	    		 // String fName=dirName+"/"+"Profiled"+fileName+".m";
	    		 // try{    			  
	              //    Reader fileReader = new FileReader( fName);
	               //   Program program = parseFile(fName,  fileReader, errors );
	               //   dDriver.setDirName(dirName);
	               //   dDriver.setFileName(fileName);	                  
	          		//  dDriver.traverseFile(program);	
	          		  //System.out.println(program.getPrettyPrinted());
	             // }catch(FileNotFoundException e){
	             //     System.err.println("File "+fName+" not found!\nAborting");
	              //    System.exit(1);
	             // }
	           // }//end of else
         //}//end of elseif.
	  //}//end of else.
	
 }//end of function.

private Document loadXmlFile(){	
	try{
		DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
        doc = docBuilder.parse (new File(dirName+"/"+"dependenceProfiled"+fileName+ ".xml"));  
        // normalize text representation
        doc.getDocumentElement ().normalize ();
        
	 }catch (SAXParseException err) {
		    System.out.println ("** Parsing error" + ", line " 
		         + err.getLineNumber () + ", uri " + err.getSystemId ());
		    System.out.println(" " + err.getMessage ());

		    }catch (SAXException e) {
		    Exception x = e.getException ();
		    ((x == null) ? e : x).printStackTrace ();

		    }catch (Throwable t) {
		    t.printStackTrace ();
		   } 
		    return doc;	    
}

private void parseXmlFile(ForStmt fStmt){
	
   int start,end;
	AssignStmt assStmt= fStmt.getAssignStmt();//This gives the assignment statement of the loop                       	 		
    if(assStmt.getRHS() instanceof RangeExpr){		
	    RangeExpr expr=(RangeExpr) assStmt.getRHS();
	    start=((BigInteger)((IntNumericLiteralValue)((IntLiteralExpr)expr.getLower()).getValue()).getValue()).intValue();	    
	    end=((BigInteger)((IntNumericLiteralValue)((IntLiteralExpr)expr.getUpper()).getValue()).getValue()).intValue();
	    NodeList loopList = doc.getElementsByTagName("LoopNo");     
	    int nestingLevel;
	    float loopNo;
	    for(int s=0; s<loopList.getLength() ; s++){
	       Element loopElement=(Element)loopList.item(s);
	       loopNo=Float.parseFloat((loopElement.getAttribute("LoopNumber")).trim());
	       for(int j=0;j<fStmt.getNumStmt();j++){  
	   		Stmt stmt=fStmt.getStmt(j);   		
	   	    if(stmt instanceof AssignStmt){
	   	    	AssignStmt aStmt=(AssignStmt)stmt;
	   	    	if((aStmt.getRHS()) instanceof FPLiteralExpr && (aStmt.getLHS()) instanceof NameExpr){
	   	    		NameExpr str=(NameExpr)aStmt.getLHS();
	   	    		if(str.getVarName().equals("lNum")){
	   	    		  float lNo=((FPLiteralExpr)aStmt.getRHS()).getValue().getValue().floatValue();
	   	    		  if(loopNo==lNo){
	   	    			nestingLevel=Integer.parseInt((loopElement.getAttribute("NestingLevel")).trim());
	   	                if(nestingLevel>=0){                       	                	
	                       NodeList rangeNodeList=loopElement.getChildNodes();
	                       for(int k=0;k<rangeNodeList.getLength();k++){
	                    	   Element range=(Element)rangeNodeList.item(k);
	                    	   int stoStart=Integer.parseInt((range.getAttribute("Start")).trim());
	                    	   int stoEnd=Integer.parseInt((range.getAttribute("End")).trim());
	                    	   if((start>=stoStart && start<stoEnd) && (end>=stoStart && end<=stoEnd)){//this is the range check	                    		   
	                    		 String forString=range.getAttribute("ForLoop");
	                    		 ArrayList errList=new ArrayList();
	                    		 Reader source=Main.translateFile("DA", forString, errList);
	                    		 Program prog=Main.parseFile("DA", source, errList);	                    		 
	                    		 ForStmt forStmt=(ForStmt)prog.getChild(1).getChild(0);
	                    		 ASTNode node=fStmt.getParent();
	                    		 int a=node.getIndexOfChild(fStmt);
	                    		 node.setChild(forStmt, a);	                    		  
	                    		 //System.out.println(forStmt.getPrettyPrinted());
	                    		 return;
	                        }//end of 7th if                    	   
	                    }//end of for
	   	             }//end of 5th if 
	   	    	  }//end of 4th if
	   	       }//end of 3rd if
	   	     }//end of 2nd if    	
    	   }//end of 1st if       
   	    }//end of 2nd for
      }//end of 1st if
    }//end of main if
 }//end of function.

}