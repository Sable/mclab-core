package natlab;

import java.io.*;
import java.net.*;
import java.lang.*;

public class TestClient
{
    public static void main(String[] args)
    {
        try{
            Socket socket = new Socket( "127.0.0.1", 47146 );
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader( socket.getInputStream() ));

            String parseFile = "<parsefile>../Natlab/bar.m</parsefile>\0";
            String parseText = "<parsetext>\nx = zeros(10,1)\nfor i=1:10\n  x(i) = i+1;\nend\n</parsetext>\0";
            //String parseText = "<parsetext>\ncd tests\n</parsetext>\0";
            String shutdown = "<shutdown />\0";

            System.out.println("trying parsetext");
            try{Thread.currentThread().sleep(1000);}catch(Exception e){}
            System.out.println(" sending text:\n" + parseText);
            out.print(parseText);
            out.flush();
            System.out.println("msg sent, waiting for respones");
            StringBuffer response = new StringBuffer();
            int inCharInt = 0;
            while( true ){
                inCharInt = in.read();
                if( inCharInt == 0 )
                    break;
                response.append((char)inCharInt);
            }
            System.out.println("response: \n" + response);
            try{Thread.currentThread().sleep(1000);}catch(Exception e){}




            System.out.println("trying parsefile");
            try{Thread.currentThread().sleep(1000);}catch(Exception e){}
            System.out.println(" sending text:\n" + parseFile);
            out.print(parseFile);
            out.flush();
            System.out.println("msg sent, waiting for respones");
            response = new StringBuffer();
            inCharInt = 0;
            while( true ){
                inCharInt = in.read();
                if( inCharInt == 0 )
                    break;
                response.append((char)inCharInt);
            }
            System.out.println("response: \n" + response);
            try{Thread.currentThread().sleep(1000);}catch(Exception e){}


            /*parseFile = "<parsefile>cmdTest.m</parsefile>\0";
            System.out.println("trying parsefile again");
            try{Thread.currentThread().sleep(1000);}catch(Exception e){}
            System.out.println(" sending text:\n" + parseFile);
            out.print(parseFile);
            out.flush();
            System.out.println("msg sent, waiting for respones");
            response = new StringBuffer();
            inCharInt = 0;
            while( true ){
                inCharInt = in.read();
                if( inCharInt == 0 )
                    break;
                response.append((char)inCharInt);
            }
            System.out.println("response: \n" + response);*/



            try{Thread.currentThread().sleep(1000);}catch(Exception e){}

            
            System.out.println("waiting for 5 secs");
            try{Thread.currentThread().sleep(5000);}catch(Exception e){}

            System.out.println("sending shutdown");
            out.print(shutdown);
            out.flush();

            response = new StringBuffer();
            inCharInt = 0;
            while( true ){
                inCharInt = in.read();
                if( inCharInt == 0 )
                    break;
                response.append((char)inCharInt);
            }
            System.out.println("response: \n" + response);


            System.out.println("done");
                
        }catch( IOException e ){}
    }
}


