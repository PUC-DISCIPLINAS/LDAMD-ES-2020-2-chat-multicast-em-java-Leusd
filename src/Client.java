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
        try {
            int serverPort = 7896;
            s = new Socket("localhost", serverPort);

            DataInputStream in = new DataInputStream(s.getInputStream());
            DataOutputStream out = new DataOutputStream(s.getOutputStream());
            System.out.println("Digite seu nome: ");
            String name = scan.nextLine();
            out.writeUTF("join/" + name);
            groupAddress = in.readUTF();
            server.joinGroup("------" + name + " join in group -------", groupAddress);

            server.showMessages();
            while (true) {
                String msg = scan.nextLine();
                if (msg.equals("exit")) {
                    out.writeUTF("exit/" + name);
                    server.exitGroup("------" + name + " say goodbye -------");
                }else {
                    server.sendMessage(name + ":\t" + msg);
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
