/************************************************************************
**  AUTHOR:         Brent Dudley (btd27)
**  CLASS:          CS1501, M/W 3:00-4:15PM
**  PROFESSOR:      Dr. Khattab
**  ASSIGNMENT 4:   Primitive SecureChatServer/Client with RSA Encryption
************************************************************************/

import java.util.*;
import java.io.*;
import java.net.*;
import java.math.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;

/*
**  SecureChatClient utilizes SecureChatServer to create a primitive chat client/server setup with RSA Encryption
**
**  Contents (adapted and revised for encryption from ImprovedChatClient.java):
**  1)  ImrovedChatClient() - Constructor that handles all of the encryption/handshake
**  2)  run() - method to keep open the chat client and handle receiving messages
**  3)  actionPerformed() - method for handling actions, with encryption
**  4)  main() - Same as ImprovedChatClient.java, updated for SecureChatClient
*/

/*
**  Handshake Procedure:
**      1)  Open connection to server via socket at server IP & Port
**              Use Port: 8765; Server Name: localhost (d.t. running on own machine)
**      2)  Create ObjectOutputStream (for writing), immediately call flush() to prevent deadlock
**      3)  Create ObjectInputStream (ensure this is done after O/P stream)
**      4)  Receive server's public key, E, as a BigInt object
**      5)  Receive server's public mod value, N, as a BigInt object
**      6)  Receive server's preferred symmetric cipher ("Sub" or "Add") as a String object
**      7)  Create Substitute or Add128 object, based on Step 6, stored in SymCypher variable
**      8)  Get key from cipher object using getKey(), convert key to BigInt 
**              (use correct constructor from API to ensure created BigInt is positive) (See API for correct constructor)
**      9)  RSA-Encrypt BigInt key using E and N, send resulting BigInt to server
**              So server can determine the key - Server will already know which cipher will be used
**      10) Prompt the user for name, encrypt using cipher, send name to server
**              Done using encode() from SymCipher class, resulting array of bytes sent to server as a single object
**              using the ObjectOutputStream
**
**      HANDSHAKE COMPLETE
*/

public class SecureChatClient extends JFrame implements Runnable, ActionListener {
    
    public static final int PORT = 8765;
    
    // Declared from ImprovedChatClient
    ObjectInputStream myReader;
    ObjectOutputStream myWriter;
    JTextArea outputArea;
    JLabel prompt;
    JTextField inputField;
    String myName, serverName;
	Socket connection;
    
    // New Needs
    BigInteger E;           // Server's Public Key
    BigInteger N;           // Server's public mod value
    String cipherPref;      // Server's preferred symmetric cypher
    SymCipher symCi;        // SymCipher Var, based on cipherName
    
    public SecureChatClient() {
        try {
            
            // Step 1 - Create socket connection to server
            myName = JOptionPane.showInputDialog(this, "Enter your user name: ");
            serverName = JOptionPane.showInputDialog(this, "Enter the server name: ");
            InetAddress addr = InetAddress.getByName(serverName);
            connection = new Socket(addr, PORT);
            
            // Step 2 - Create writer/flush()
            myWriter = new ObjectOutputStream(connection.getOutputStream());
            myWriter.flush();
            
            // Step 3 - Create reader
            myReader = new ObjectInputStream(connection.getInputStream());
            
            // Step 4 - Receive public key from server
            E = (BigInteger) myReader.readObject();
            System.out.println("E Received: " + E);
            
            // Step 5 - Receive public mod value from server
            N = (BigInteger) myReader.readObject();
            System.out.println("N Received: " + N);
            
            // Step 6 - Receive server's preferred cipher
            cipherPref = (String) myReader.readObject();
            System.out.println("Preferred Cipher Type: " + cipherPref);
            
            // Step 7 - Create cipher type based on Step 6, store in symCi
            switch (cipherPref) {
                case "Sub":
                    symCi = new Substitute();
                    System.out.println("Using Substitute cipher");
                    break;
                case "Add":
                    symCi = new Add128();
                    System.out.println("Using Add128 cipher");
                    break;
                default:
                    System.out.println("Unknown cipher option");
                    System.exit(1);
            }
            
            // Step 8/9 - Get/Encrypt key; SEEEEEENNNNNNDDD IIIIIIIIIITTTTTT
            myWriter.writeObject(new BigInteger(1, symCi.getKey()).modPow(E, N));
            myWriter.flush();
            
            // Step 10 - Send name
            // Cache key, then encrypt name
            // Cache:
            byte[] cachedKey = symCi.getKey();
            
            // Test Line to see key
            for (int i = 0; i < cachedKey.length; i++) {
                System.out.println(cachedKey[i] + " ");
            }
            System.out.println();
            
            // Set/encrypt/send
            this.setTitle(myName);
            myWriter.writeObject(symCi.encode(myName));
            myWriter.flush();
            
            // Chat client interface below adapted from 
            // ***********************************************************************************
            Box b = Box.createHorizontalBox();  // Set up graphical environment for user
            outputArea = new JTextArea(8, 30);
            outputArea.setEditable(false);
            b.add(new JScrollPane(outputArea));

            outputArea.append("Welcome to the Chat Group, " + myName + "\n");

            inputField = new JTextField("");  // This is where user will type input
            inputField.addActionListener(this);

            prompt = new JLabel("Type your messages below:");
            Container c = getContentPane();

            c.add(b, BorderLayout.NORTH);
            c.add(prompt, BorderLayout.CENTER);
            c.add(inputField, BorderLayout.SOUTH);

            Thread outputThread = new Thread(this);  // Thread is to receive strings from server
            outputThread.start();

            // Had to adjust these events for the encryption as well, but framework taken from ImprovedChatClient.java
            addWindowListener(
                new WindowAdapter() {
                    public void windowClosing(WindowEvent e) {
                        try {
                            myWriter.writeObject(symCi.encode("CLIENT CLOSING"));
                            myWriter.flush();
                        } catch (IOException ex) {
                            System.out.println("Problem closing client!");
                        } finally {
                            System.exit(0);
                        }
                }});

            setSize(500, 200);
            setVisible(true);
            // ***********************************************************************************
            
        } catch (Exception e) {
            System.out.println("Problem starting client!");
        }
    }
    
    // Method framework adapted from ImprovedChatClient, updated for encryption
    public void run() {
        while (true) {
            try {
                byte[] coded = (byte[]) myReader.readObject();
                String currMsg = symCi.decode(coded);
                outputArea.append(currMsg + "\n");
                
                // Test Lines:
                System.out.println("Cipher Received: ");
                for (int i = 0; i < coded.length; i++) {
                    System.out.print(coded[i] + " ");
                }
                System.out.println("");
                
                System.out.println("Decoded Message: " + currMsg);
                System.out.println("");
                // End Test Lines
            } catch (ClassNotFoundException ce) {
                System.out.println("Error receiving message");
            } catch (IOException ex) {
                System.out.println(ex + ", closing client!");
                break;
            }
        }
        System.exit(0);
    }
    
    public void actionPerformed(ActionEvent e) {
        String currMsg = e.getActionCommand();      // Get input value
        inputField.setText("");
        
        try {
            String message = myName + ":" + currMsg;    // Add name
            byte[] cipher = symCi.encode(message);
            myWriter.writeObject(cipher);
            myWriter.flush();
            // Test Lines:
            System.out.println("Message Sent: " + message);
            System.out.print("Cipher Sent: ");
            for (int i = 0; i < cipher.length; i++) {
                System.out.print(cipher[i] + " ");
            }
            System.out.println("");
        } catch (IOException ex) {
            outputArea.append("Error sending message\n");
        }
    }
    
    public static void main(String [] args) {
         SecureChatClient JR = new SecureChatClient();
         JR.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    }
    
}





















