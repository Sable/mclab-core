package natlab.server;

import java.util.*;
import java.io.*;

public class HeartBeatTask extends TimerTask
{
    private HeartBeatFlag flag;
    private PrintWriter out;
    private boolean quiet;

    public HeartBeatTask( HeartBeatFlag flag, PrintWriter out, boolean quiet )
    {
        this.flag = flag;
        this.out = out;
        this.quiet = quiet;
    }
    public void run()
    {
        if( !flag.readFlagAndUnset() ){
            if( !quiet )
                System.err.println("server timed out, aborting");
            //TODO: is this thread safe? probably not
            out.print("<errorlist><error>server timed out, aborting</error></errorlist>\0");
            out.flush();
            //TODO-JD: make the exiting nicer
            System.exit(1);
        }
    }
}