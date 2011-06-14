// =========================================================================== //
//                                                                             //
// Copyright 2008-2011 Andrew Casey, Jun Li, Jesse Doherty,                    //
//   Maxime Chevalier-Boisvert, Toheed Aslam, Anton Dubrau, Nurudeen Lameed,   //
//   Amina Aslam, Rahul Garg, Soroush Radpour, Olivier Savary Belanger,        //
//   Laurie Hendren, Clark Verbrugge and McGill University.                    //
//                                                                             //
//   Licensed under the Apache License, Version 2.0 (the "License");           //
//   you may not use this file except in compliance with the License.          //
//   You may obtain a copy of the License at                                   //
//                                                                             //
//       http://www.apache.org/licenses/LICENSE-2.0                            //
//                                                                             //
//   Unless required by applicable law or agreed to in writing, software       //
//   distributed under the License is distributed on an "AS IS" BASIS,         //
//   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  //
//   See the License for the specific language governing permissions and       //
//   limitations under the License.                                            //
//                                                                             //
// =========================================================================== //

package natlab;

import java.io.*;
import java.util.*;
import java.util.regex.*;
import org.antlr.runtime.ANTLRReaderStream;
import matlab.*;
import matlab.FunctionEndScanner.*;
import ast.*;
import beaver.Parser;
import java.util.List;

/**
 * Set of static methods to parse files.
 * This needs some serious overhaul
 * 
 * - there should be one parsing method taking a Reader
 * - there should be one translating method taking a Reader
 * 
 * - all other methods should be very very small
 * 
 * - method to open files and report errors?
 * 
 * 
 * 
 * 
 */




public class Parse {
    
    /**
     * code that should get executed before any parsing
     */
    private static void parsePreamble(){
        Stmt.setDefaultOutputSuppression(false);
    }
    
    /**
     * code that should get executed after any parsing
     */
    private static void parsePostscript(){
        Stmt.setDefaultOutputSuppression(true);
    }

    /**
     * Parse a given file and return the Program ast node. This
     * expects the program to already be in natlab syntax.
     *
     * @param fName  The name of the file being parsed.
     * @param file   The reader object containing the source being
     * parsed.
     * @param errList   A list of errors for error collection.
     *
     * @return The Program node for the given file being parsed if no
     * errors. If an error occurs then null is returned. 
     */
    public static Program parseFile(String fName, Reader file, ArrayList<CompilationProblem> errList )
    {
        parsePreamble();
        
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
                parsePostscript();
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
            CompilationProblem IOcerror = new CompilationProblem("Error parsing "+fName+"\n"+e.getMessage());
            errList.add( IOcerror);
            return null;
        }
    }

    /**
     * Parse a given file and return the Program ast node. This
     * expects the program to already be in natlab syntax.
     *
     * @param fName  The name of the file being parsed.
     * @param errList   A list of errors for error collection.
     *
     * @return The Program node for the given file being parsed if no
     * errors. If an error occurs then null is returned. 
     */
    public static Program parseFile(String fName, ArrayList<CompilationProblem> errList ){
    	try {
    		FileReader reader = new FileReader(fName);
    		Program program = parseFile(fName,reader,errList);    	
    		if (program == null){
    		    System.err.println(errList);    		    
    		}
    		
    		return program;
    	} catch (FileNotFoundException e){
    	    System.err.println("File "+fName+" not found!");
    	    return null;
    	}    
    }

    /**
     * Parse a given file as a Matlab file and return the Program ast node.
     *
     * @param fName  The name of the file being parsed.
     * @param errList   A list of errors for error collection.
     *
     * @return The Program node for the given file being parsed if no
     * errors. If an error occurs then null is returned. 
     */
    public static Program parseMatlabFile(String fName, ArrayList<CompilationProblem> errList ){
    	Reader source = Parse.translateFile( fName, errList );
        if( source == null ) return null;
        Program program = Parse.parseFile( fName, source, errList );
        return program;
    }

    public static CompilationUnits parseFiles(List<String> fileList,   ArrayList<CompilationProblem> errList ){
    CompilationUnits cu = new CompilationUnits();
    for (String fName: fileList){
        Reader natlabFile = Parse.translateFile(fName, errList);
        //parse natlab
        Program p=Parse.parseFile( fName,  natlabFile,  errList );
        cu.addProgram(p);
    }
    return cu;
    
    }

    /**
     * Parse a given file as a Matlab file and return the Program ast node.
     *
     * @param fName  The name of the file being parsed.
     * @param file   The reader object containing the source being
     * parsed.
     * @param errList   A list of errors for error collection.
     *
     * @return The Program node for the given file being parsed if no
     * errors. If an error occurs then null is returned. 
     */
    public static Program parseMatlabFile(String fName, Reader file, ArrayList<CompilationProblem> errList ){
    	//TODO - something should be done about the mapping file
    	//translate into natlab
    	Reader natlabFile = Parse.translateFile(fName, file, errList);
    	//parse natlab
    	return parseFile( fName,  natlabFile,  errList );
    }

    /**
     * Perform the reading and translation of a given file.
     *
     * @param fName    The name of the file to be translated.
     * @param errList  A list of errors for error collection.
     * 
     * @return A reader object giving access to the translated
     * source.
     */
    public static Reader translateFile(String fName, ArrayList<CompilationProblem> errList)
    {
        BufferedReader in = null;
        PositionMap prePosMap = null;
        try{
            in = new BufferedReader( new FileReader( fName ) );
            FunctionEndScanner prescanner = new FunctionEndScanner(in);
            FunctionEndScanner.Result result = prescanner.translate();
            in.close();
            
            if(result instanceof NoChangeResult){
                in = new BufferedReader( new FileReader( fName ) );
            }else {
                in.close();
                if(result instanceof ProblemResult){
                    for(TranslationProblem prob : ((ProblemResult) result).getProblems()){
                        CompilationProblem translationcproblem = new CompilationProblem(prob.getLine(),prob.getColumn(),prob+"\n");
                        errList.add(translationcproblem);
                    }
                    return null; //terminate early since extraction parser can't work without balanced 'end's
                } else if(result instanceof TranslationResult){
                    TranslationResult transResult = (TranslationResult) result;
                    in = new BufferedReader(new StringReader(transResult.getText()));
                    prePosMap = transResult.getPositionMap();
                }
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
        
        return Parse.finishTranslateFile(fName, in, prePosMap, errList);
        
    }

    /**
     * Perform the translation of a given string containing source
     * code.
     *
     * @param fName    The name of the file to which the source
     * belongs.
     * @param source   The string containing the source code.
     * @param errList  A list of errors for error collection.
     * 
     * @return A reader object giving access to the translated
     * source.
     */
    public static Reader translateFile(String fName, String source, ArrayList<CompilationProblem> errList)
    {
        BufferedReader in = null;
        PositionMap prePosMap = null;
        try{
            in = new BufferedReader( new StringReader( source ) );
            FunctionEndScanner prescanner = new FunctionEndScanner(in);
            FunctionEndScanner.Result result = prescanner.translate();
            in.close();
            
            if(result instanceof NoChangeResult){
                in = new BufferedReader( new StringReader( source ) );
            }else {
                in.close();
                if(result instanceof ProblemResult){
                    for(TranslationProblem prob : ((ProblemResult) result).getProblems()){
                        CompilationProblem translationcproblem = new CompilationProblem(prob.getLine(),prob.getColumn(),prob+"\n");
                        errList.add(translationcproblem);
                    }
                    return null; //terminate early since extraction parser can't work without balanced 'end's
                } else if(result instanceof TranslationResult){
                    TranslationResult transResult = (TranslationResult) result;
                    in = new BufferedReader(new StringReader(transResult.getText()));
                    prePosMap = transResult.getPositionMap();
                }
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
        
        return Parse.finishTranslateFile(fName, in, prePosMap, errList);
    }

    /**
     * Perform the translation from a given Reader containing source code.
     *
     * @param fName    The name of the file to which the source belongs.
     * @param source   The string containing the source code.
     * @param errList  A list of errors for error collection.
     * 
     * @return A reader object giving access to the translated
     * source.
     */
    public static Reader translateFile(String fName, Reader source, ArrayList<CompilationProblem> errList)
    {
    	//we'll just build a String out of the source and call the method that takes a String
    	//TODO - this should be done directly from the reader
    	BufferedReader buffer = new BufferedReader(source);
    	StringBuilder builder = new StringBuilder();
    	try{
    		while(true){
    			String line = buffer.readLine();
    			if (line == null) break;
    			builder.append(line);
    		}
    	}catch(IOException e){
    		CompilationProblem IOcerror = new CompilationProblem("Error translating "+fName+"\n"+e.getMessage());
    		errList.add(IOcerror);
    		return null;    
    	}
    	return translateFile(fName,builder.toString(),errList);
    }

    /**
     * Translate a given file and return a reader to access the
     * translated version. This method is used by the translateFile
     * methods and performs the final part of the translation.
     *
     * @param fName     The name of the file to which the source
     * belongs.
     * @param in        The source.
     * @param prPosMap  The position map to map from the original
     * translated file positions and original file positions.
     * @param errList   A list of errors for error collection.
     * 
     * @return A reader object giving access to the translated
     * source.
     */
    static Reader finishTranslateFile(String fName, BufferedReader in, 
                                              PositionMap prePosMap, ArrayList errList)
    {
        try{
            OffsetTracker offsetTracker = new OffsetTracker(new TextPosition(1, 1));
            List<TranslationProblem> problems = new ArrayList<TranslationProblem>();
            String destText = MatlabParser.translate(new ANTLRReaderStream(in), 1, 1, offsetTracker, problems);
            
            if( problems.isEmpty() ){
                PositionMap posMap = offsetTracker.buildPositionMap();
                
                if( prePosMap != null ){
                    posMap = new CompositePositionMap(posMap, prePosMap);
                }
                //TODO-JD: do something with the posMap
                return new StringReader(destText);
            }
            else{
                for(TranslationProblem prob : problems){
                    CompilationProblem Translationcproblem = new CompilationProblem(prob.getLine(),prob.getColumn(),prob.getMessage()+"\n");
                    errList.add(Translationcproblem);
                    
                }
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

}
