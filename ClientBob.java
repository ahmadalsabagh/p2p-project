import java.io.*;
import java.net.*;
import java.util.*;

public class ClientBob
{
	private static String dhtMaster;
	private static String dht2;
	private static String dht3;
	private static List<List<String>> localRecords;
	private static String dht4;

	public static void main(String[] args )
	{	
		int port = 20540;
		Scanner input = new Scanner(System.in);
		System.out.println("What is the IP of the Master DHT Server?");
		dhtMaster = input.nextLine();
		while(true)
		{	
			System.out.println("Welcome! What action would you like to take?");
			System.out.println("Init - initiates DHT Server IPs");
			System.out.println("Inform and update - Hashes file and sends it to appropriate DHT Server along with IP");
			System.out.println("Query - Find out where your file is located and request transfer");
			System.out.println("Exit - Exit program");
			String response = input.nextLine();
			if(response.equalsIgnoreCase("init"))
			{
				System.out.println("Initiating DHT Server IPs");
				dht2 = sendMessage("IP 2",dhtMaster);
				dht3 = sendMessage("IP 3",dhtMaster);
				System.out.println(dht2);
				System.out.println(dht3);
				dht4 = sendMessage("IP 4",dhtMaster);
				System.out.println("Initiated DHT Server IPs");
			}
			if(response.equalsIgnoreCase("exit"))
			{

				System.out.println("GoodBye");
				break;
			}
			if(response.equalsIgnoreCase("inform and update"))
			{
				informAndUpdate();

			}

			if(response.equals("query"))
			{
				String fileIp = "";
				System.out.println("Which file would you like to query?");
				String contentName = input.nextLine();
				int hashed = hashFunction(contentName);
				
				switch(hashed)
				{
					case 1:
					fileIp = sendMessage(("Query "+contentName),dhtMaster);
					break;
					case 2:
					fileIp = sendMessage(("Query "+contentName),dht2);
					break;
					case 3:
					fileIp = sendMessage(("Query "+contentName),dht3);
					break;
					case 4:
					fileIp = sendMessage(("Query "+contentName),dht4);
					break;
				}
				System.out.println(fileIp);
				System.out.println("Would you like to request transfer of this file? (Y/N)");
				String answer = input.nextLine();

				if(answer.equalsIgnoreCase("y")){
					getFile(fileIp,contentName);
					System.out.println("Saved file!");
				}
				else{
					System.out.println("Goodbye.");
				}
			}
		}
	}

	// HTTP GET request
  public static void getFile(String ip,String file) {
    try
    {
    //String fileRequest = "http://127.0.1.1:20109/ab.jpeg";
    String fileRequest = "http://"+ ip + ":20109/" + file;
    System.out.println(fileRequest);
    URL obj = new URL(fileRequest);
    HttpURLConnection con = (HttpURLConnection) obj.openConnection();

    con.setRequestMethod("GET");

    int responseCode = con.getResponseCode();
    System.out.println("\nSending 'GET' request to URL : " + fileRequest);
    System.out.println("Response Code : " + responseCode);
    DataInputStream dis = new DataInputStream(con.getInputStream());
    FileOutputStream fos = new FileOutputStream("file.jpeg");
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
  }
  catch(Exception e){
   e.printStackTrace();
  }
}
	public static void informAndUpdate()
	{	
		try
		{
		localRecords = new ArrayList<List<String>>();
		Scanner input = new Scanner(System.in);
		InetAddress localhost = InetAddress.getLocalHost();
		String myIP = localhost.getHostAddress().trim();
		 ArrayList<String> contentArray = new ArrayList<String>();

		while(true)
		{	
			contentArray = new ArrayList<String>();
			System.out.println("What is the name of the content you would like to store (type exit to stop adding files)");
			String content = input.nextLine();
			if(content.equalsIgnoreCase("exit")){
				System.out.println("Goodbye.");
				break;
			}
			int hashed = hashFunction(content);
			switch(hashed)
				{
					case 1:
					
					 contentArray.add(content);
					 contentArray.add("1");
					 contentArray.add(dhtMaster);
					 localRecords.add(contentArray);
					 sendMessage(("Store "+content+" "+myIP),dhtMaster);
					 System.out.println("Stored in Server 1");
					 break;
					case 2:
					 
					 contentArray.add(content);
					 contentArray.add("2");
					 contentArray.add(dht2);
					 localRecords.add(contentArray);
					 sendMessage(("Store "+content+" "+myIP),dht2);
					 System.out.println("Stored in Server 2");
					 break;
					case 3:
					 contentArray.add(content);
					 contentArray.add("3");
					 contentArray.add(dht3);
					 localRecords.add(contentArray);
					 sendMessage(("Store "+content+" "+myIP),dht3);
					 System.out.println("Stored in Server 3");
					 break;
					case 4:
 					 contentArray.add(content);
					 contentArray.add("4");
					 contentArray.add(dht4);
					 localRecords.add(contentArray);
					 sendMessage(("Store "+content+" "+myIP),dht4);
					 System.out.println("Stored in Server 4");
					 break;
				}		  
		}
	}catch(Exception e){
		e.printStackTrace();
	}
}
	public static String sendMessage(String message,String ip)
	{
		try
		{
		 int sendPort = getRandPort();
		 System.out.println(sendPort);
		DatagramSocket clientSocket = new DatagramSocket(sendPort);
		InetAddress ipAddress = InetAddress.getByName(ip);
		byte[] sendData = new byte[1024];
		byte[] receiveData = new byte[1024];
		sendData = message.getBytes();
		DatagramPacket sendPacket = new DatagramPacket(sendData,sendData.length, ipAddress,20540/*ENTER PORT HERE*/);
		clientSocket.send(sendPacket);
		DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
		clientSocket.receive(receivePacket);
		String modifiedSentence = new String(receivePacket.getData(), receivePacket.getOffset(), receivePacket.getLength());
		clientSocket.close();
		return modifiedSentence;
	}
	catch(Exception e)
	{
		e.printStackTrace();
	}

	return "";
	}
	public static int getRandPort()
	{	
	int max = 20109;
	int min = 20100;
	Random rand = new Random(); 
	int value = rand.nextInt((max - min) + 1) + min;
	return value;

	}


	public static int hashFunction(String filename){
		char character;
		int asciiValue;
		int result=0;
		int dhtServer;
		for (int x = 0; x < filename.length(); x++){
			character = filename.charAt(x);
			//convert it to the ascii value
			asciiValue = (int) character;
			result += asciiValue;
		}
		dhtServer = (result % 4) + 1;
		return dhtServer;

	}
}