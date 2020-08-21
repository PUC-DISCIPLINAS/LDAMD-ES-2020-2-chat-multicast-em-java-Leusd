import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.*;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) {
        Socket s = null;
        Scanner scan = new Scanner(System.in);
        String groupAddress;
        Server server = new Server();
        boolean isInAGroup = false;
        try {
            int serverPort = 7896;
            s = new Socket("localhost", serverPort);

            DataInputStream in = new DataInputStream(s.getInputStream());
            DataOutputStream out = new DataOutputStream(s.getOutputStream());
            System.out.println("Digite seu nome: ");
            String name = scan.nextLine();

            while (true) {
                String msg = scan.nextLine();
                if (msg.equals("join")) {
                    out.writeUTF("join/" + name);
                    groupAddress = in.readUTF();
                    server.joinGroup("------" + name + " join in group -------", groupAddress);
                    isInAGroup = true;
                    server.showMessages();
                } else if (msg.equals("exit")) {
                    out.writeUTF("exit/" + name);
                    server.exitGroup("------" + name + " say goodbye -------");
                    isInAGroup = false;
                    server.showMessages();
                } else if (isInAGroup){
                    server.sendMessage(name + ":\t" + msg);
                    server.showMessages();
                }else {
                    System.out.println("You need to join in the group");
                }

            }

        } catch (UnknownHostException e) {
            System.out.println("Socket:" + e.getMessage());
        } catch (EOFException e) {
            System.out.println("EOF:" + e.getMessage());
        } catch (IOException e) {
            System.out.println("readline:" + e.getMessage());
        } finally {
            if (s != null)
                try {
                    s.close();
                } catch (IOException e) {
                    System.out.println("close:" + e.getMessage());
                }
        }
    }
}
