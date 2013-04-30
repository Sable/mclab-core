package natlab;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import natlab.options.Options;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

import ast.CompilationUnits;
import ast.Program;

import com.google.common.collect.Lists;

public class NatlabServer {
  private static final int SERVER_PORT = 47146; //default server port
  private static final long HEART_RATE = 4000; //in milliseconds
  private static final long HEART_DELAY = 5000; //delay till first heart beat check is made

  private int port;
  private boolean quiet;
  private boolean natlab;
  private boolean heartbeat;

  private PrintWriter out;
  private Scanner in;
  private AtomicBoolean heartbeatFlag = new AtomicBoolean(true);
  private Timer heartbeatTimer = new Timer();

  public static NatlabServer create(Options options) {
    int port = SERVER_PORT;
    if (options.sp().length() > 0) {
      port = Integer.parseInt(options.sp());
    }
    return new NatlabServer(port, options.quiet(), options.natlab(), !options.noheart());
  }

  private NatlabServer(int port, boolean quiet, boolean natlab, boolean heartbeat) {
    this.port = port;
    this.quiet = quiet;
    this.natlab = natlab;
    this.heartbeat = heartbeat;
  }

  private void log(String message) {
    if (!quiet) {
      System.err.println(message);
    }
  }

  private void connect() {
    log("Server mode");
    log("Opening server on port " + port);
    ServerSocket serverSocket = null;
    try{
      serverSocket = new ServerSocket( port );
    } catch (IOException e) {
      System.err.println("Server could not be opened on port " + port);
      System.exit(1);
    }
    log("Server started");

    Socket clientSocket = null;
    try {
      clientSocket = serverSocket.accept();
    } catch (IOException e) {
      System.err.println("Accept client failed");
      System.exit(1);
    }
    log("Client connected");

    try {
      out = new PrintWriter(clientSocket.getOutputStream(), true);
      in = new Scanner(clientSocket.getInputStream()).useDelimiter("\0");
    } catch (IOException e) {
      System.err.println("Server input stream creation failed");
      System.exit(1);
    }

    if (heartbeat) {
      heartbeatTimer.schedule(new TimerTask() {
        @Override public void run() {
          if (!heartbeatFlag.getAndSet(false)) {
            log("Server timed out, aborting");
            out.print("<errorlist><error>server timed out, aborting</error></errorlist>\0");
            out.flush();
            System.exit(1);
          }
        }
      }, HEART_DELAY, HEART_RATE);
    }
  }

  private void shutdown() {
    log("shutdown cmd");
    out.print("<shutdown />\0");
    out.flush();
    heartbeatTimer.cancel();
    out.close();
    in.close();
  }

  private static class Command {
    public String command;
    public String argument;
  }

  private Command parseCommand(String cmd) {
    try {
      DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
      dbf.setIgnoringElementContentWhitespace(true);
      dbf.setCoalescing(true);
      DocumentBuilder db = dbf.newDocumentBuilder();
      Reader xmlData = new StringReader(cmd);
      Document d = db.parse(new InputSource(xmlData));
      Element root = d.getDocumentElement();
      root.normalize();

      Command command = new Command();
      command.command = root.getNodeName().trim().toLowerCase();
      if (root.hasChildNodes()) {
        command.argument = root.getFirstChild().getNodeValue().trim();
      }
      return command;
    } catch (Exception e) {
      return null;
    }
  }

  public void start() {
    connect();

    while (in.hasNext()) {
      Command command = parseCommand(in.next());

      if (command.command.equals("heartbeat")) {
        heartbeatFlag.set(true);
        continue;
      } else if (command.command.equals("shutdown")) {
        shutdown();
        break;
      }

      String filename = null;
      Reader input = null;
      if (command.command.equals("parsefile")) {
        filename = command.argument;
      } else if (command.command.equals("parsetext")) {
        filename = "source/text";
        input = new StringReader(command.argument);
      }

      log("Parsing");
      List<CompilationProblem> errors = Lists.newArrayList();
      Program program;
      if (!natlab) {
        if (input != null) {
          program = Parse.parseMatlabFile(filename, input, errors);
        } else {
          program = Parse.parseMatlabFile(filename, errors);
        }
      } else {
        if (input != null) {
          program = Parse.parseNatlabFile(filename, input, errors);
        } else {
          program = Parse.parseNatlabFile(filename, errors);
        }
      }

      if (!errors.isEmpty()) {
        out.print("<errorlist>");
        for (CompilationProblem problem : errors) {
          out.print("<error>" + problem + "</error>");
        }
        out.print("</errorlist>\0");
        out.flush();
        continue;
      }

      CompilationUnits cu = new CompilationUnits();
      cu.addProgram(program);
      String ast = cu.XMLtoString(cu.ASTtoXML(false));
      log("Sending response: \n" + ast);
      out.print(ast + "\0");
      out.flush();
    }
    log("Server shutdown");
  }
}
