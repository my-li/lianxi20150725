import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

//@SuppressWarnings("serial")
public class ChatServer {
   boolean started = false;
   ServerSocket ss = null;
   List<Client> clients = new ArrayList<Client>();
 
   public static void main(String[] args) {
	   ChatServer f = new ChatServer();
       f.start();
   }

   public void start() {
       try {
           ss = new ServerSocket(8888);
           started = true;

       } catch (IOException e) {
           System.out.println("ERROR_端口占用中");
           System.exit(0);
       }

       try {
           while (started) {
               Socket s = ss.accept();
               Client c = new Client(s);
               System.out.println("一个用户连接了");
               new Thread(c).start();
               clients.add(c);
               // dis.close();
           }
       } catch (IOException e) {
           e.printStackTrace();
       } finally {
           try {
               ss.close();
           } catch (IOException e) {
               e.printStackTrace();
           }
       }
   }

   class Client implements Runnable {

       private Socket s;
       private DataInputStream dis = null;
       private boolean bConnected = false;
       private DataOutputStream dos = null;

       public Client(Socket s) {
           this.s = s;
           try {
               dis = new DataInputStream(s.getInputStream());
               dos = new DataOutputStream(s.getOutputStream());
               bConnected = true;
           } catch (IOException e) {
               e.printStackTrace();
           }
       }

       public void send(String str) {

           try {
               dos.writeUTF(str);
           } catch (IOException e) {
               clients.remove(this);
               // e.printStackTrace();
           }

       }

       @Override
       public void run() {

           try {
               while (bConnected) {
                   String str = dis.readUTF();
                   System.out.println(str);
                   for (int i = 0; i < clients.size(); i++) {
                       Client c = clients.get(i);
                       c.send(str);
                   }
               }
           } catch (EOFException e) {
               // System.out.println("用户关闭");
           } catch (IOException e) {
               e.printStackTrace();
               // System.out.println("用户关闭");
           } finally {
               try {
                   if (dis != null)
                       dis.close();
                   if (dos != null)
                       dos.close();
                   if (s != null)
                       s.close();
               } catch (IOException e1) {
                   e1.printStackTrace();
               }
           }

       }
   }

}
