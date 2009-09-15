package natlab;

import java.io.*;
import java.util.ArrayList;

import junit.framework.TestCase;
import ast.*;

public class StaticFortranTest extends TestCase
{
	private static final String basePath = "../Benchmarks/matlabBenchmarks/McFor/mcfor_test/";
    private static final String diffFileName = "diff.log";
    private static final String checkExtention = ".R3";//".ok";
    private static final String inFileName[] = {
    	"test/ComplexNum.m", "test/CellArray.m",  "test/ExprStmt.m",  
    	"test/FuncForm.m", 
    	"test/FuncArg1.m", "test/FuncArg2.m", "test/FuncArg3.m" , "test/FuncArg4.m",    	
    	"test/RangeExpr.m", "test/MatrixConstruction.m",
    	"test/ParamExpr1.m","test/ParamExpr2.m", "test/ParamExpr3.m","test/ParamExpr4.m",    	
    	"test/Transform1.m", "test/Transform2.m", "test/Transform3.m",    	
    	"test/TransformFunc.m",    	
    	"test/TypeConflict.m",  "test/TypeConflictFunc.m",
    	"test/TypeInferrenceProcess.m",
    	"test/benchmark2_2.m",     
    };

    private static final String inFolderName[] = {
    	"capr",
        "clos",
    	"diff","dich",
    	"edit",	 
    	"fiff", "fft", 
    	"fdtd",
    	"mbrt","nb1d", "nb3d",
    	
    	/*  
		"adpt", // need option -fbounds-check
		"crni",
    	 */
   	};

    public void test_ErrorCheckTest_function() throws Exception
    {
    	boolean bIdentical = true;
    	boolean bIdenticalAll = true;
    	String[] argList = new String[2];
    	// Initial the log file
    	initLogFile();
    	
    	for(int i=0; i<inFileName.length; i++) {
	    	argList[0] = basePath + inFileName[i];
	    	StaticFortranTool.DEBUG = false;
	        String filenameTag = McFor.M2F(argList);
	        bIdentical = gatherDiff(filenameTag, filenameTag+checkExtention);
	    	if(!bIdentical)	System.err.println("[Error] in "+filenameTag);
	    	bIdenticalAll = bIdenticalAll && bIdentical;
    	}
    	for(int i=0; i<inFolderName.length; i++) {
	    	argList[0] = "-d";
	    	argList[1] = basePath + inFolderName[i];
	        String filenameTag = McFor.M2F(argList);
	        bIdentical = gatherDiff(filenameTag, filenameTag+checkExtention);
	    	if(!bIdentical)	System.err.println("[Error] in "+filenameTag);
	    	bIdenticalAll = bIdenticalAll && bIdentical;
    	}
    	junit.framework.Assert.assertTrue(bIdenticalAll);
    }
 
    //-------------------------------------------------------------------------
    // Utility functions for gathering difference between new generated file 
    // and the expecting result
    public static void initLogFile() {
		String filename = getAbslouteName(basePath + diffFileName);
    	try {
            BufferedWriter outFile = new BufferedWriter(new FileWriter(filename,false));
			outFile.write(""); 
			outFile.close();
    	} catch (IOException e) {
			e.printStackTrace();
			System.err.println(e.getMessage());
    	}
    }
    public static boolean gatherDiff(String fileSource, String fileTraget) {
		String filename = getAbslouteName(basePath + diffFileName);
    	boolean bIdentical = false;
    	try {
            BufferedWriter outFile = new BufferedWriter(new FileWriter(filename,true));
	    	// Append the difference to the log file
	    	ArrayList<String> fileDiff = compareFile(fileSource, fileTraget);
	    	bIdentical = (fileDiff.size()==0);
			for(String str:fileDiff) {
				outFile.write(str+"\n"); 
			}
			outFile.close();
    	} catch (IOException e) {
			e.printStackTrace();
			System.err.println(e.getMessage());
    	}
    	return bIdentical;
    }
    
    public static ArrayList<String> compareFile(String fileSource, String fileTraget) {
    	System.out.println("CompareFile: "+fileSource + " vs " + fileTraget);
    	ArrayList<String> fileDiff = new ArrayList<String>();
    	ArrayList<String> contentSrc = getFileContents(fileSource);
    	ArrayList<String> contentTag = getFileContents(fileTraget);
    	int i,j;
    	for(i=0; i<contentSrc.size() && i<contentTag.size() ; i++) {
    		if(!(contentSrc.get(i).equals(contentTag.get(i)))) {
    			fileDiff.add(">>>>["+i+"]");
    			fileDiff.add(contentSrc.get(i));
    			fileDiff.add("<<<<");
    			fileDiff.add(contentTag.get(i));
    		}
    	}
    	for(j=i; j<contentSrc.size(); j++) {
			fileDiff.add(">>>>["+j+"]");
			fileDiff.add(contentSrc.get(i));
    	}
    	for(j=i; j<contentTag.size(); j++) {
			fileDiff.add("<<<<["+j+"]");
			fileDiff.add(contentTag.get(i));
    	}
    	if(fileDiff.size()==0) {
    		System.out.println("CompareFile: idential");
    	} else {
    		System.out.println("CompareFile: different="+fileDiff.size()/4);
    		fileDiff.add(0, "-------- [CompareFile: "+fileSource +"] --------");
    	}
    	return fileDiff;
    }
    
	public static String PATH_STRING = "/";			// different in DOS and Linux     
	public static String getAbslouteName(String name) {
		/*
        int startIndex = System.getProperty("user.dir").lastIndexOf("/");
        if(startIndex<=0) {
        	PATH_STRING = "\\";
        } else {
        	PATH_STRING = "/";
        } */
		String absDirName = name;
		if(!name.subSequence(1, 2).equals(":")) {
			if(name.subSequence(0, 1).equals(PATH_STRING)) {
				absDirName = name;
			} else {
				absDirName = System.getProperty("user.dir")+ PATH_STRING + name;
			}
		}
		return absDirName;
	}
	public static ArrayList<String> getFileContents(String filename) {
		filename = getAbslouteName(filename);

    	ArrayList<String> contents = new ArrayList<String>();
    	try {
			BufferedReader inFile = new BufferedReader(new FileReader(filename));
			String str="";
	        while ((str = inFile.readLine()) != null) {
	        	contents.add(str);
	        }
			inFile.close();
    	} catch (IOException e) {
			// e.printStackTrace();
			System.err.println(e.getMessage());
    	}
    	return contents;
	}
}
