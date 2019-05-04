import java.io.*;
import java.net.*;
import java.util.*;


public class ServerEmily 
{ 
    public static void main(String[] args) throws IOException
    { 
    int port = 20109;
    ServerSocket welcomeSocket = new ServerSocket(port);
    System.out.println("Server started. Listening for connections on port " + port + "...");

    while(true) {
    Socket s = null;
    s = welcomeSocket.accept();
    Thread thread = new ParallelConnections(s);
    thread.start();
        } 
    }
} 

class ParallelConnections extends Thread
{
    Socket sock;

     public ParallelConnections(Socket sock){
          this.sock = sock;
     }

	public void run(){
		try{ 
            InputStream sis = sock.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(sis));

            String request = br.readLine(); // Now you get GET object.jpeg HTTP/1.1
            String[] requestParam = request.split(" "); //split up request
            String path = requestParam[1]; //object name
            PrintWriter out = new PrintWriter(sock.getOutputStream(), true);
            File file = new File("." + path);


            if (!requestParam[0].equals("GET")){
                out.write("HTTP 400 Bad Request\n");
                System.out.println("HTTP 400 Bad Request");
            }

            else if(!requestParam[2].equals("HTTP/1.1")){
                out.write("HTTP 505 HTTP Version Not Supported\n");
                System.out.println("HTTP 505 HTTP Version Not Supported");
            }


            else if (!file.exists()) {
                 out.write("HTTP 404 File Not Found\n"); // the file does not exists
                 System.out.println("HTTP 404 File Not Found");
            }
            else if(file.exists()){
                FileInputStream fis = null;
                BufferedInputStream bis = null;
                OutputStream os = null;
                    
                BufferedReader bfr = new BufferedReader(new FileReader(file));
                long len = file.length();
                String line;
                out.println("HTTP/1.1 200 OK");
                out.println("Content-Type: image/jpeg");
                out.println("Connection: Keep-Alive");
                out.println("Content-Length: " + len);
                out.println("");

                byte[] mybytearray  = new byte [(int)file.length()];
                fis = new FileInputStream(file);
                bis = new BufferedInputStream(fis);
                bis.read(mybytearray,0,mybytearray.length);
                os = sock.getOutputStream();
                System.out.println("Sending " + path + "(" + mybytearray.length + " bytes)");
                os.write(mybytearray,0,mybytearray.length);
                System.out.println("Done.");




                os.flush();
                out.flush();
                bfr.close();
                sock.close();

            }


            out.close();
            br.close();
        }
        catch (Exception e) { 
            System.out.println ("Exception is caught"); 
        } 
	}
}
