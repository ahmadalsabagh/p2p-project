
import java.io.*;
import java.net.*;

public class SimpleFileClient {

  public final static int SOCKET_PORT = 20540;      // you may change this
  public final static String SERVER = "192.168.0.19";  // localhost
  public final static String FILE_TO_RECEIVED = "./source-downloaded.jpeg";  // you may change this, I give a
                                                            // different name because i don't want to
                                                            // overwrite the one used by server...

  public final static int FILE_SIZE = 20000; // file size temporary hard coded
                                               // should bigger than the file to be downloaded

  public static void main (String [] args ) throws Exception {
    Socket sock = null;
    sock = new Socket(SERVER, SOCKET_PORT);

    saveFile();
  }


  public static void saveFile() {
    try
    {
    String fileRequest = "http://192.168.0.19:20540/bob.jpeg";
    URL obj = new URL(fileRequest);
    HttpURLConnection con = (HttpURLConnection) obj.openConnection();

    con.setRequestMethod("GET");

    int responseCode = con.getResponseCode();
    System.out.println("\nSending 'GET' request to URL : " + fileRequest);
    System.out.println("Response Code : " + responseCode);
    DataInputStream dis = new DataInputStream(con.getInputStream());
    FileOutputStream fos = new FileOutputStream("testfile.jpeg");
    byte[] buffer = new byte[32768];
    
    int filesize = 15875; // Send file size in separate msg
    int read = 0;
    int totalRead = 0;
    int remaining = filesize;
    while((read = dis.read(buffer, 0, Math.min(buffer.length, remaining))) > 0) {
      totalRead += read;
      remaining -= read;
      System.out.println("read " + totalRead + " bytes.");
      fos.write(buffer, 0, read);
    }
    
    fos.close();
    dis.close();
    //clientSock.close();
  }
  catch(Exception e){
   e.printStackTrace();
  }
}

}