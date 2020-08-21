import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;

public class Server {
	static final int port = 7896;
	MulticastSocket mSocket = null;
	InetAddress groupIp;
	boolean isFinished = false;

	public void joinGroup(String msg, String address) {
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

	public void showMessages() {
		//  Starts receiving messages thread
		Thread thread = new Thread(() -> {
			try {
				while (!isFinished) {
					byte[] buffer = new byte[1000];
					DatagramPacket messageIn = new DatagramPacket(buffer, buffer.length, groupIp, port);
					mSocket.receive(messageIn);
					System.out.println(new String(messageIn.getData()).trim());
				}
			} catch (IOException e) {
				System.out.println("IO: " + e.getMessage());
			}
		});
		thread.start();
	}

	public void exitGroup(String msg) {
		try {
			byte[] message = msg.getBytes();
			DatagramPacket messageOut = new DatagramPacket(message, message.length, groupIp, port);
			mSocket.send(messageOut);
			mSocket.leaveGroup(groupIp);

		} catch (SocketException e) {
			System.out.println("Socket: " + e.getMessage());
		} catch (IOException e) {
			System.out.println("IO: " + e.getMessage());
		}
	}
}