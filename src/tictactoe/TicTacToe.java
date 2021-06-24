package tictactoe;

import java.awt.*;
import java.awt.event.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import javax.swing.*;


public class TicTacToe implements ActionListener {
   
   // Client Server variables
   public String ip = "localhost";
   public int port = 2222;
   public Scanner scanner = new Scanner(System.in);
   public Socket socket;
   public DataOutputStream dos;
   public DataInputStream dis;
   public ServerSocket serverSocket;
   public int errors = 0;
   public String waitingString = "Waiting for another player";
   public String unableToConnectString = "Unable to connect with players";
   
   // Netplay variables
   public boolean accepted = false;
   public boolean unableToConnect = false;
   
   
   // Game Variables
   Random random = new Random();
   JFrame frame = new JFrame();
   JPanel title = new JPanel();
   JPanel board = new JPanel();
   JLabel textfield = new JLabel();
   JButton[] buttons = new JButton[9];
   boolean player1Turn;
   public int firstSpot = -1;
   public int secondSpot = -1;
   public int thirdSpot = -1;
   ArrayList<String> occupied = new ArrayList<String>();
   public int[][] wins = new int[][] 
   { 
       {0, 1, 2}, {3, 4, 5}, {6, 7, 8}, // Horizontal Wins
       {0, 3, 6}, {1, 4, 7}, {2, 5, 8}, // Vertical Wins
       {0, 4, 8}, {2, 4, 6}             // Diagonal Wins
   };
   
   // Constructor
   public TicTacToe(){
       // Establish connection
       System.out.println("Please enter the IP: ");
       ip = scanner.nextLine();
       System.out.println("Please enter the port: ");
       port = scanner.nextInt();
       while (port < 1024) {
           System.out.println("The port you entered was invalid, please enter another port: ");
           port = scanner.nextInt();
       }
       
       if (!connect()) initializeServer();
       
       // Build board GUI
       frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
       frame.setSize(400, 400);
       frame.getContentPane().setBackground(new Color(50,50,50));
       frame.setLayout(new BorderLayout());
       frame.setVisible(true);
       
       textfield.setBackground(new Color(25,25,25));
       textfield.setForeground(new Color(25,255,0));
       textfield.setFont(new Font("Times New Roman", Font.BOLD, 75));
       textfield.setHorizontalAlignment(JLabel.CENTER);
       textfield.setText("Tic-Tac-Toe");
       textfield.setOpaque(true);
       
       title.setLayout(new BorderLayout());
       title.setBounds(0,0,800,100);
       
       board.setLayout(new GridLayout(3,3));
       board.setBackground(new Color(150,150,150));
       
       for (int i = 0; i < 9; i++) {
           buttons[i] = new JButton();
           board.add(buttons[i]);
           buttons[i].setFont(new Font("Verdana", Font.BOLD, 120));
           buttons[i].setFocusable(false);
           buttons[i].addActionListener(this);
       }
       
       title.add(textfield);
       frame.add(title, BorderLayout.NORTH);
       frame.add(board);
       
       // Start game
       firstTurn();
   }
   
   // Socket runner
   public void run() {
       while (true) {
           if (!player1Turn && !accepted) {
               listenForServer();
               try {
               int space = dis.read();
               } catch (IOException e) {
                   e.printStackTrace();
               }
           }
       }
   }
   
   // Creating server
   // Creates input output to transmit moves to server and client
   public void listenForServer() {
       Socket socket = null;
               try {
                   socket = serverSocket.accept();
                   dos = new DataOutputStream(socket.getOutputStream());
                   dis = new DataInputStream(socket.getInputStream());
                   accepted = true;
                   System.out.println("Client has requested to join and we have accepted.");
               } catch (IOException e) {
                   e.printStackTrace();
               }
   }
   
   // Establishing connection to server
   // Creates input output to transmit moves to server and client
   public boolean connect() {
       try {
           socket = new Socket(ip, port);
           dos = new DataOutputStream(socket.getOutputStream());
           dis = new DataInputStream(socket.getInputStream());
           accepted = true;
       } catch (IOException e) {
           System.out.println("Unable to connect to the addres: " + ip + ":" + port + " | Starting a server");
           return false;
       }
       System.out.println("Successfully connected to server.");
       return true;
   }
   
   
   public void initializeServer() {
       try {
           serverSocket = new ServerSocket(port, 8, InetAddress.getByName(ip));
       } catch (Exception e) {
           e.printStackTrace();
       }
       player1Turn = true;
   }
   
   // Method to send outputs or moves to the server
   public void sendMove(int a) {
       if (dos != null) {
           try {
               dos.writeInt(a);
               dos.flush();
           } catch (IOException e) {
               e.printStackTrace();
               errors++;
           }
       }
   }
   
   @Override
   public void actionPerformed(ActionEvent e) {
       if (errors > 9) unableToConnect = true;
       for(int i = 0; i < 9; i++) {
           if(e.getSource() == buttons[i]) {
               if(player1Turn && !unableToConnect) {
                   if (buttons[i].getText() == "") {
                       if (buttons[i] != null) {
                           sendMove(i);
                           buttons[i].setForeground(new Color(255, 0, 0));
                           buttons[i].setText("X");
                           player1Turn = !player1Turn;
                           textfield.setText("O Turn");
                           check();
                       }
                   }
               }
               else {
                   if (buttons[i].getText() == "") {
                       if (buttons[i] != null) {
                           sendMove(i);
                           buttons[i].setForeground(new Color(0, 0, 255));
                           buttons[i].setText("O");
                           player1Turn = !player1Turn;
                           textfield.setText("X Turn");
                           check();
                       }
                   }
               }
           }
       }
   }
   
   public void firstTurn() {
       
       try {
           // Delay before game starts
           Thread.sleep(1500);
       } catch (InterruptedException e) {
           e.printStackTrace();
       }
       
       if(random.nextInt(2) == 0 ){
           player1Turn = true;
           textfield.setText("X Turn");
       }
       else {
           player1Turn = false;
           textfield.setText("O Turn");
       }
       
   }
   // wins = new int[][] { {0,1,2}, {3,4,5}, {6,7,8}, {0,3,6}, {1,4,7}, {2,5,8}, {0,4,8}, {2,4,6} };
   // wins.lenght = 8
   // buttons[wins[i][0]].getText() == "X" && buttons[wins[i][1]] == "X" && buttons[wins[i][2]] == "X"
   public void check() {
       for (int i = 0; i < wins.length; i++) {
           // X win conditions
           if (buttons[wins[i][0]].getText() == "X" && buttons[wins[i][1]].getText() == "X" && buttons[wins[i][2]].getText() == "X") {
               firstSpot = wins[i][0];
               secondSpot = wins[i][1];
               thirdSpot = wins[i][2];
               xWins(firstSpot, secondSpot, thirdSpot);
               return;
           } 
           // O win conditions
           else if (buttons[wins[i][0]].getText() == "O" && buttons[wins[i][1]].getText() == "O" && buttons[wins[i][2]].getText() == "O") {
               firstSpot = wins[i][0];
               secondSpot = wins[i][1];
               thirdSpot = wins[i][2];
               oWins(firstSpot, secondSpot, thirdSpot);
               return;
           }
           // Draw conditions
           else {
               for (int j = 0; j < 9; j++) {
                   if (buttons[j].getText().length() == 1) {
                       occupied.add(buttons[j].getText());
                   }
                   else {
                       return;
                   }
               }
               if (occupied.size() == 9) {
                   drawGame();
               }
           }
       }     
   }
   
   public void xWins(int a, int b, int c) {       
       for( int i = 0; i < 9; i++) {
           buttons[i].setEnabled(false);
       }
       textfield.setText("X WINS");
   }
   
   public void oWins(int a, int b, int c) {
       for( int i = 0; i < 9; i++) {
           buttons[i].setEnabled(false);
       }
       textfield.setText("O WINS");
   }
   
   public void drawGame() {
        for( int i = 0; i < 9; i++) {
           buttons[i].setEnabled(false);
       }
       textfield.setText("DRAW");
   }
   
   public void rematch() {
       
   }
}
