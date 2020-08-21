import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.*;
import java.util.ArrayList;

public class Server {
	static final int port = 7896;
	static final String address = "228.5.6.7";
	MulticastSocket mSocket = null;
	InetAddress groupIp;
	boolean isFinished = false;
	static ArrayList<String> groupList = new ArrayList<>();

	public void enterGroup(String msg) {
		try {
			groupIp = InetAddress.getByName(address);

			mSocket = new MulticastSocket(port);
			mSocket.joinGroup(groupIp);
			byte[] message = msg.getBytes();
			DatagramPacket messageOut = new DatagramPacket(message, message.length, groupIp, port);
			mSocket.send(messageOut);
		} catch (SocketException e) {
			System.out.println("Socket: " + e.getMessage());
		} catch (IOException e) {
			System.out.println("IO: " + e.getMessage());
		}
	}

	public void sendMessage(String msg) {
		try {
			byte[] message = msg.getBytes();
			DatagramPacket messageOut = new DatagramPacket(message, message.length, groupIp, port);
			mSocket.send(messageOut);
		} catch (SocketException e) {
			System.out.println("Socket: " + e.getMessage());
		} catch (IOException e) {
			System.out.println("IO: " + e.getMessage());
		}
	}

	public void listGroup() {
			System.out.println("Group List");
			groupList.forEach(System.out::println);
	}

	public void showMessages() {
		//  Starts receiving messages thread
		Thread thread = new Thread(() -> {
			try {
				while (!isFinished) {
					byte[] buffer = new byte[1000];
					DatagramPacket messageIn = new DatagramPacket(buffer, buffer.length, groupIp, port);
					mSocket.receive(messageIn);
					String msg = new String(messageIn.getData()).trim();
					System.out.println( new String(messageIn.getData()).trim());
				}
			} catch (IOException e) {
				System.out.println("IO: " + e.getMessage());
			}
		});
		thread.start();
	}

	public void exitGroup() {
		try {
			mSocket.leaveGroup(groupIp);
		} catch (SocketException e) {
			System.out.println("Socket: " + e.getMessage());
		} catch (IOException e) {
			System.out.println("IO: " + e.getMessage());
		}
	}
	public static void main(String[] args) {

		ServerSocket listenSocket = null;

		try {
			InetAddress groupIp = InetAddress.getByName(MULTICAST_ADDRESS);


			mSocket = new MulticastSocket(MULTICAST_PORT);
			mSocket.joinGroup(groupIp);
			listenSocket = new ServerSocket(port);
			System.out.println("Server open in TCP/7896.");

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


	public Connection(Socket aClientSocket) {
		try {
			clientSocket = aClientSocket;
			in = new DataInputStream(clientSocket.getInputStream());
			out = new DataOutputStream(clientSocket.getOutputStream());
			this.start();
		} catch (IOException e) {
			System.out.println("Conex�o:" + e.getMessage());
		}
	}

	public void run() {
		try {

			while (true) {
				String data = in.readUTF();

				String[] params = data.split(",");

				if (params[0].equals("join")) {
					Server.groupList.add(params[1]);
					String groupAddress = "228.5.6.7";
					out.writeUTF(groupAddress);
				} else if (params[0].equals("exit")) {
					Server.groupList.remove(params[1]);
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
				/* close falhou */
			}
		}

	}
}
