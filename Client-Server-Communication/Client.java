import java.net.*;
import javax.swing.*;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.*;

public class Client extends JFrame {
  Socket socket;
  BufferedReader br;
  PrintWriter out;

  // Declare Components
  private JLabel heading = new JLabel("Client Area");
  private JTextArea messageArea = new JTextArea();
  private JTextField messageInput = new JTextField();
  private Font font = new Font("Roboto", Font.PLAIN, 20);

  // Constructor

  public Client() {
    try {
      System.out.println("Sending request to the server");
      socket = new Socket("172.20.10.7", 7778);
      System.out.println("Connection established.");
      br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      out = new PrintWriter(socket.getOutputStream());
      createGUI();
      handleEvents();
      startReading();
      startWriting();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void handleEvents() {
    messageInput.addKeyListener(new KeyAdapter() {
      @Override
      public void keyReleased(KeyEvent e) {
        System.out.println("key released " + e.getKeyCode());
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
          String contentToSend = messageInput.getText();
          messageArea.append("Me: " + contentToSend + "\n");
          out.println(contentToSend);
          out.flush();
          messageInput.setText("");
          messageInput.requestFocus();
        }
      }
    });
  }

  private void createGUI() {
    // GUI code...
    this.setTitle("Client Messager[END]");
    this.setSize(600, 700);
    this.setLocationRelativeTo(null);
    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    // Coding for components
    heading.setFont(font);
    messageArea.setFont(font);
    messageInput.setFont(font);

    heading.setIcon(new ImageIcon("clogo.png")); // You should provide a valid image path here
    heading.setHorizontalTextPosition(JLabel.CENTER);
    heading.setVerticalTextPosition(JLabel.BOTTOM);
    heading.setHorizontalAlignment(JLabel.CENTER);
    heading.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    messageArea.setEditable(false);
    messageInput.setHorizontalAlignment(JTextField.CENTER);

    // Frame ka layout set karenge
    this.setLayout(new BorderLayout());

    this.add(heading, BorderLayout.NORTH);
    JScrollPane jScrollPane = new JScrollPane(messageArea);
    this.add(jScrollPane, BorderLayout.CENTER);
    this.add(messageInput, BorderLayout.SOUTH);

    this.setVisible(true);
  }

  // Start reading [method]

  public void startReading() {
    Runnable r1 = () -> {
      System.out.println("Reader started..");
      try {
        while (true) {
          String msg = br.readLine();
          if (msg == null || msg.equals("exit")) {
            System.out.println("Server terminated the chat");
            JOptionPane.showMessageDialog(this, "Server Terminated the chat");
            messageInput.setEnabled(false);
            socket.close();
            break;
          }
          messageArea.append("Server: " + msg + "\n");
        }
      } catch (Exception e) {
        System.out.println("Connection closed");
      }
    };
    new Thread(r1).start();
  }

  // Start writing send [method]

  public void startWriting() {
    Runnable r2 = () -> {
      System.out.println("Writer started..");
      try {
        while (!socket.isClosed()) {
          BufferedReader br1 = new BufferedReader(new InputStreamReader(System.in));
          String content = br1.readLine();
          out.println(content);
          out.flush();
          if (content.equals("exit")) {
            socket.close();
            break;
          }
        }
        System.out.println("Connection is closed");
      } catch (Exception e) {
        e.printStackTrace();
      }
    };
    new Thread(r2).start();
  }

  public static void main(String[] args) {
    System.out.println("This is the client...");
    new Client();
  }
}
