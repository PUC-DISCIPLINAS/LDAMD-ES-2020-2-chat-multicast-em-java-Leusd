import java.net.*;
import java.io.*;
import java.util.ArrayList;

public class Main {
	public static void main(String[] args) {

		ServerSocket listenSocket = null;

		try {
			int serverPort = 7896;
			listenSocket = new ServerSocket(serverPort);
			System.out.println("Server open in port TCP/7896.");

			while (true) {
				Socket clientSocket = listenSocket.accept();
				new Connection(clientSocket);
			}
		} catch (IOException e) {
			System.out.println("Listen socket:" + e.getMessage());
		} finally {
			if (listenSocket != null)
				try {
					listenSocket.close();
					System.out.println("Server closed");
				} catch (IOException e) {
					// close fail //
				}
		}
	}

}

class Connection extends Thread {
	DataInputStream in;
	DataOutputStream out;
	Socket clientSocket;
	static ArrayList<String> menbersList = new ArrayList<>();


	public Connection(Socket aClientSocket) {
		try {
			clientSocket = aClientSocket;
			in = new DataInputStream(clientSocket.getInputStream());
			out = new DataOutputStream(clientSocket.getOutputStream());
			this.start();
		} catch (IOException e) {
			System.out.println("Conection:" + e.getMessage());
		}
	}

	public void run() {
		try {

			while (true) {
				String data = in.readUTF();

				String[] params = data.split("/");

				if (params[0].equals("join")) {
					menbersList.add(params[1]);
					String groupAddress = "228.5.6.7";
					out.writeUTF(groupAddress);
				} else if (params[0].equals("exit")) {
					menbersList.remove(params[1]);
				}
				System.out.println("---Members List---");
				if(menbersList.isEmpty()){
					System.out.println("Group is empty");
				}
				else {
					for (String name : menbersList) {
						System.out.println(name);
					}
				}
			}

		} catch (EOFException e) {
			System.out.println("EOF:" + e.getMessage());
		} catch (IOException e) {
			System.out.println("readline:" + e.getMessage());
		} finally {
			try {
				clientSocket.close();
				System.out.println("Server closed");
			} catch (IOException e) {
				/* close fail */
			}
		}

	}
}
