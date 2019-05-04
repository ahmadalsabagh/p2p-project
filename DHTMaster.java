import java.io.*;
import java.net.*;
import java.util.*;

import java.util.ArrayList; // import the ArrayList class
public class DHTMaster
{
	private static  int port;
	private static int server;
	private static String nextIP;
	private static String ip2;
	private static String ip3;
	private static String ip4;
	private static ArrayList<String[]> dataLocation;


	public static void udpListener(){


		try{
		String clientSentence;
		String clientArray[];
		DatagramSocket serverSocket = new DatagramSocket(port);
		byte[] receiveData = new byte[1024];
		byte[] sendData = new byte[1024];
		while (true) 
		{
			DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);

			serverSocket.receive(receivePacket);
			System.out.println("Recieved data.");

			clientSentence = new String(receivePacket.getData(), receivePacket.getOffset(), receivePacket.getLength());

			InetAddress ipAddress = receivePacket.getAddress();	

			int returnPort = receivePacket.getPort();

			clientArray = clientSentence.split(" ");
			String response = clientArray[0];

			if (response.equalsIgnoreCase("IP"))
			{
				if(clientArray[1].equalsIgnoreCase("2")){
				    System.out.println("it works");
				    ip2 = sendTCPMessage("IP 2");
					sendData = ip2.getBytes();
					DatagramPacket sendPacket = new DatagramPacket(sendData,sendData.length,ipAddress,returnPort);
					serverSocket.send(sendPacket);
				}
				if(clientArray[1].equalsIgnoreCase("3")){
					System.out.println("it works");
				   ip3 = sendTCPMessage("IP 3");
					sendData = ip3.getBytes();
					DatagramPacket sendPacket = new DatagramPacket(sendData,sendData.length,ipAddress,returnPort);
					serverSocket.send(sendPacket);

				}
				if(clientArray[1].equalsIgnoreCase("4")){
				 System.out.println("it works");
			     ip4 = sendTCPMessage("IP 4");
				sendData = ip4.getBytes();
				DatagramPacket sendPacket = new DatagramPacket(sendData,sendData.length,ipAddress,returnPort);
				serverSocket.send(sendPacket);

				}

			}
			if (response.equalsIgnoreCase("store")){
				String[] content = {clientArray[1],clientArray[2]};
				dataLocation.add(content);
				System.out.println("stored " + clientArray[1] + " " + clientArray[2]);

			}

			if (response.equalsIgnoreCase("query")){
				for(String[] content: dataLocation){
					if (content[0].equals(clientArray[1])){
						sendData = content[1].getBytes();
						DatagramPacket sendPacket = new DatagramPacket(sendData,sendData.length,ipAddress,returnPort);
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

	
	public static void main(String[] args) throws IOException
	{
		port = 20540;
		server = 1;
		System.out.println("Please enter the IP of DHT Server 2:");
		Scanner userIn = new Scanner(System.in);
		nextIP = userIn.nextLine();
		System.out.println("Listening on UDP port " + port);
		dataLocation = new ArrayList<String[]>();
		udpListener();
	




	}
	public static String sendTCPMessage(String message)
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


