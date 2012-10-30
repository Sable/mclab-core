package natlab.server;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;

import natlab.CompilationProblem;
import natlab.Parse;
import natlab.options.Options;
import ast.CompilationUnits;
import ast.Program;

import com.google.common.collect.Lists;

public class NatlabServer {
  private static final int SERVER_PORT = 47146; //default server port
  private static final long HEART_RATE = 4000; //in milliseconds
  private static final long HEART_DELAY = 5000; //delay till first heart beat check is made

  private Options options;
  private List<CompilationProblem> errors;
  public static NatlabServer create(Options options) {
    return new NatlabServer(options);
  }

  private NatlabServer(Options options) {
    this.options = options;
    this.errors = Lists.newArrayList();
  }

  private void log(String message) {
    if (!options.quiet()) {
      System.err.println(message);
    }
  }

  public void start() {
    log("server mode");
    int portNumber = SERVER_PORT;
    if (options.sp().length() > 0) {
      portNumber = Integer.parseInt(options.sp());
    }

    log("opening server on port " + portNumber);
    ServerSocket serverSocket = null;
    try{
      serverSocket = new ServerSocket( portNumber );
    } catch(IOException e) {
      System.err.println("Server could not be opened on port " + portNumber);
      System.exit(1);
    }
    log("Server started");

    Socket clientSocket = null;
    try {
      clientSocket = serverSocket.accept();
    } catch(IOException e) {
      System.err.println("accept client failed");
      System.exit(1);
    }
    log("Client connected");

    BufferedReader in = null;
    final PrintWriter out;
    try {
      out = new PrintWriter(clientSocket.getOutputStream(), true);
      in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    } catch (IOException e) {
      System.err.println("Server input stream creation failed");
      System.exit(1);
      return;
    }
    int inCharInt = 0;
    StringBuffer commandBuf;
    XMLCommandHandler xmlCH = new XMLCommandHandler();
    boolean shutdown = false;
    final AtomicBoolean hbFlag = new AtomicBoolean(true);
    Timer heartBeat = new Timer();
    if (!options.noheart()) {
      heartBeat.schedule(new TimerTask() {
        @Override public void run() {
          if (!hbFlag.getAndSet(false)) {
            log("server timed out, aborting");
            //TODO: is this thread safe? probably not
            out.print("<errorlist><error>server timed out, aborting</error></errorlist>\0");
            out.flush();
            System.exit(1);
          }
        }
      }, HEART_DELAY, HEART_RATE);
    }

    while(!shutdown) {
      commandBuf = new StringBuffer();
      try {
        while(true) {
          inCharInt = in.read();
          if (inCharInt == 0) {
            break;
          }
          commandBuf.append((char)inCharInt);
        }
      } catch (IOException e) {
        System.err.println("Error reading from socket");
        System.exit(1);
      }
      if (xmlCH.parse(commandBuf.toString())) {
        boolean parsing = false;
        Reader source = new StringReader("");
        String fileName = "";
        String cmd = xmlCH.getCommand();
        List<CompilationProblem> serverErrors = Lists.newArrayList();
        if (cmd.equalsIgnoreCase("parsefile")) {
          fileName = xmlCH.getBody();
          log("parsefile cmd");
          log("parse file " + fileName);
          if(fileName == null) {
            String FNstr="";
            if(!options.quiet()) {
              FNstr+="  file name was null"+"\n";
            }
            CompilationProblem FileNullcerror = new CompilationProblem(FNstr+="<errorlist><error>parsefile error: filename was null</error></errorlist>\0");
            errors.add(FileNullcerror);
            continue;
          }
          parsing = true;
          if (options.matlab()) {
            log("translating");
            source = Parse.translateFile( fileName, serverErrors );
          }
          else {
            try {
              source = new FileReader( fileName );
            } catch( FileNotFoundException e){
              String FNFEstr="";
              if (options.quiet())
                FNFEstr+="file: "+fileName+" not found"+"/n";
              CompilationProblem FileNotFoundcerror = new CompilationProblem(FNFEstr+="<errorlist><error>file: "+fileName+" not found</error></errorlist>\0");
              errors.add(FileNotFoundcerror);
            }
          }
        }
        else if( cmd.equalsIgnoreCase( "parsetext" ) ){
          String programText = xmlCH.getBody();
          if(!options.quiet()){
            System.err.println(" parsetext cmd ");
            System.err.println("  text to parse:");
            System.err.println( programText );
          }
          if( programText == null ){
            log("program text was null");
            out.print("<errorlist><error>parsetext error: program text was null</error></errorlist>\0");
            out.flush();
            continue;
          }
          fileName = "source/text";
          parsing = true;
          if (options.matlab()) {
            log("translating");
            source = Parse.translateFile( fileName, programText, serverErrors );
          }
          else {
            source = new StringReader( programText );
          }
        }
        else if (cmd.equalsIgnoreCase("shutdown")) {
          log("shutdown cmd");
          out.print("<shutdown />\0");
          out.flush();
          shutdown = true;
          heartBeat.cancel();
          try{
            clientSocket.close();
            out.close();
            in.close();
            serverSocket.close();
          }catch(IOException e){}

          continue;
        }
        else if (cmd.equalsIgnoreCase("heartbeat")) {
          hbFlag.set(true);
        } else{
          log("ignored cmd");
        }

        if( parsing ) {
          if( options.matlab() ){
            /*if( !quiet )
                                  System.err.println(" translating ");
                                  source = translateFile( fileName, source, serverErrors );
             */
            if( serverErrors.size() > 0 ){
              String serverEstr="";
              if (!options.quiet())
                serverEstr+="errors\n" + serverErrors+"/n";
              CompilationProblem Servercerror = new CompilationProblem(serverEstr+="<errorlist><error>Error has occured in translating, more information to come later</error></errorlist>\0");
              errors.add(Servercerror);
              continue;

            }
            if( source == null ){
              String fooEstr="";
              if (!options.quiet())
                fooEstr+="skipping file"+"/n";
              CompilationProblem Foocerror = new CompilationProblem(fooEstr+="<errorlist><error>Error has occured in translating, more information to come later</error></errorlist>\0");
              errors.add(Foocerror);
              continue;
            }
          }
          log("parsing");
          Program prog = Parse.parseFile( fileName, source, serverErrors );
          if( serverErrors.size() > 0){
            String serverEstr="";
            if(!options.quiet())
              serverEstr+="errors\n" + serverErrors+"/n";
            CompilationProblem Servercerror = new CompilationProblem(serverEstr+="<errorlist><error>Error has occured in translating, more information to come later</error></errorlist>\0");
            errors.add(Servercerror);
            continue;
          }
          if( prog == null ){
            String serverEstr="";
            if(!options.quiet())
              serverEstr+="errors\n" + serverErrors+"/n";
            CompilationProblem Servercerror = new CompilationProblem(serverEstr+="<errorlist><error>Error has occured, more information to come later</error></errorlist>\0");
            errors.add(Servercerror);
            continue;
          }
          CompilationUnits cu = new CompilationUnits();
          cu.addProgram( prog );
          log("Sending response: \n" + cu.XMLtoString(cu.ASTtoXML()));

          out.print(cu.XMLtoString(cu.ASTtoXML())+ "\0");
          out.flush();
          log("Response sent");
        }
      }
    }
    log("Server shutdown");
  }
}
