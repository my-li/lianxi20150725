//package test;

import java.awt.BorderLayout;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketException;
import javax.swing.JFrame;

public class ChatClient extends JFrame {

   /**
    * 
    */
   private static final long serialVersionUID = 1L;
   TextField tfTxt = new TextField();
   TextArea taContent = new TextArea();
   Socket s = null;
   DataOutputStream dos = null;
   DataInputStream dis = null;
   private boolean bConnected = false;

   /**
    * @param args
    */
   public static void main(String[] args) {
	   ChatClient c = new ChatClient();
       c.launchFrame();
   }

   public ChatClient() {
       super("Client");
   }

   private void launchFrame() {
       setLocation(400, 300);
       this.setSize(300, 300);
       add(tfTxt, BorderLayout.SOUTH);
       add(taContent, BorderLayout.NORTH);
       // É¾³ý¸÷ÖÖµÄ¾àÀë
       pack();
       this.addWindowListener(new WindowAdapter()

       {

           @Override
           public void windowClosing(WindowEvent e) {
               disconnect();
               System.exit(0);
           }

       });
       tfTxt.addActionListener(new TFLinsener());
       setVisible(true);
       connect();
       new Thread(new RecvThread()).start();
   }

   public void connect() {
       try {
           s = new Socket("127.0.0.1", 8888);
           dos = new DataOutputStream(s.getOutputStream());
           dis = new DataInputStream(s.getInputStream());
           System.out.println("Á¬½ÓÁË¹þ");
           bConnected = true;
       } catch (ConnectException e) {
           taContent.setText("Á¬½ÓÊ§°Ü");
           try {
               new Thread();
               Thread.sleep(2000);
           } catch (InterruptedException e1) {
               // TODO Auto-generated catch block
               e1.printStackTrace();
           }
           System.exit(0);
       } catch (IOException e) {
           e.printStackTrace();
       }
   }

   public void disconnect() {
       try {
           dos.close();
           s.close();
       } catch (IOException e) {

           e.printStackTrace();
       }

   }

   private class TFLinsener implements ActionListener {

       @Override
       public void actionPerformed(ActionEvent e) {
           String str = tfTxt.getText().trim();
           // taContent.setText(str);
           tfTxt.setText("");
           try {
               dos.writeUTF(str);
               dos.flush();
               // dos.close();
           } catch (IOException e1) {
               e1.printStackTrace();
           }
       }

   }

   private class RecvThread implements Runnable {

       @Override
       public void run() {

           try {
               while (bConnected) {
                   String str = dis.readUTF();
                   // System.out.println(str);
                   taContent.setText(taContent.getText() + str + '\n');
               }
           } catch (SocketException e) {
               System.out.println("ÍË³öÁË bye");
           } catch (EOFException e) {
               System.out.println("ÍË³öÁË-- bye");
           } catch (IOException e) {
               e.printStackTrace();
           }

       }
   }

}
