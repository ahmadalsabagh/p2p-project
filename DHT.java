import java.io.*;
import java.net.*;
import java.util.*;

import java.util.ArrayList; // import the ArrayList class
public class DHT
{
	private static int port;
	private static String server;
	private ServerSocket mainSocket;
	private ArrayList<String[]> dataLocation;



	public  static  void main(String[] args) throws IOException
	{
		port = 20102;
		server = args[0];
		Scanner userInput = new Scanner(System.in);
		System.out.println("Enter the IP of the next DHT Server");
		String nextIP = userInput.nextLine();
		Thread udpThread = new UDPlistener(port);
		Thread tcpThread = new TCPlistener(port,nextIP,Integer.parseInt(server));
		udpThread.start();
		tcpThread.start();

	}
}

class UDPlistener extends Thread
{
	int  port;
	private ArrayList<String[]> dataLocation;
	public UDPlistener(int portNum)
	{
	port = portNum;
	dataLocation = new ArrayList<String[]>();
	}

	public void run() {
		try{
		String clientSentence;
		String clientArray[];
		DatagramSocket serverSocket = new DatagramSocket(port);
		
		System.out.println("Waiting on  UDPPort "+port);
		byte[] receiveData = new byte[1024];	
		byte[] sendData = new byte[1024];
		while (true) 
		{
			DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);

			serverSocket.receive(receivePacket);
			System.out.println("Recieved.");

			clientSentence = new String(receivePacket.getData(), receivePacket.getOffset(), receivePacket.getLength());

			InetAddress IPAddress = receivePacket.getAddress();	

			int returnPort = receivePacket.getPort();

			clientArray = clientSentence.split(" ");
			System.out.println(clientArray[0]);
			System.out.println(clientArray[0].equalsIgnoreCase("Init"));
			String response = clientArray[0];
			if(response.equals("Store"))
			{
				
				String[] content = {clientArray[1],clientArray[2]};
				dataLocation.add(content);
				System.out.println("stored" + clientArray[1]);

			}
						if (response.equalsIgnoreCase("query")){
				for(String[] content: dataLocation){
					if (content[0].equals(clientArray[1])){
						sendData = content[1].getBytes();
						DatagramPacket sendPacket = new DatagramPacket(sendData,sendData.length,IPAddress,returnPort);
						serverSocket.send(sendPacket);
					}
				}
			}

			}
		}
		catch(Exception e)
		{	
			e.printStackTrace();
		}
}

}
class TCPlistener extends Thread
{
	int port;
	String nextIP;
	int serverId;
	public TCPlistener(int portNum,String nIP,int id)
	{
		port = portNum;
		nextIP = nIP;
		serverId = id;
	}
	public void run()
	{

	try{


		String clientSentence;
		String[] clientArray;
		String capitalizedSentence;

		ServerSocket welcomeSocket = new ServerSocket(port);
		System.out.println("Waiting on TCP Port "+ port);

		while(true)
		{
			Socket connectionSocket = welcomeSocket.accept();

			BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));

			DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());

			clientSentence = inFromClient.readLine();

			clientArray = clientSentence.split(" ");
			if(clientArray[0].equals("IP"))
			{
				if(clientArray[1].equals(Integer.toString(serverId)))
				{	
					String myIp =  Inet4Address.getLocalHost().getHostAddress();
					System.out.println(" IP sent");
					outToClient.writeBytes(myIp);

				}
				else
				{
					System.out.println("Passing Message on");
					String message = sendTCPMessage(clientSentence);
					outToClient.writeBytes(message);


				}
			}
			connectionSocket.close();
		}

	} catch(Exception e ){
			e.printStackTrace();
		}
	}

	public String sendTCPMessage(String message)
	{
		try 
		{
		String sentence;
		String modifiedSentence;

		Socket clientSocket = new Socket(nextIP,20102);

		DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
		BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

		outToServer.writeBytes(message+"\n");

		String response = inFromServer.readLine();
		
		clientSocket.close();

		return response;
	}
	catch(Exception e)
	{
		e.printStackTrace();

	}
	return "";
	}
}


