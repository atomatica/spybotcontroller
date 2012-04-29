package com.atomatica.spybotcontroller;

import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Spybotcontroller extends JApplet {
    SpybotcontrollerUI ui;

    public void init() {
        ui = new SpybotcontrollerUI("atomatica.com");

        Container container=getContentPane();
        container.setLayout(new GridLayout());
        container.add(ui);
    }

    public void start() {
        ui.start();
    }

    public void stop() {
        ui.stop();
    }

    public void destroy() {

    }
}

class SpybotcontrollerUI extends JPanel implements Runnable {
    private Thread ui;
    
    private JTextField enterField;
    private JTextArea displayArea;
    private ObjectOutputStream output;
    private ObjectInputStream input;
    private String message = "";
    private String chatServer;
    private Socket client;

    public SpybotcontrollerUI(String host) {
        chatServer = host;

        // create enterField and register listener
        enterField = new JTextField();
        enterField.setEditable(false);
        enterField.addActionListener(new ActionListener() {

            // send message to server
            public void actionPerformed(ActionEvent event) {
                sendData(event.getActionCommand());
                enterField.setText("");
            }
        });

        this.add(enterField, BorderLayout.NORTH);

        // create displayArea
        displayArea = new JTextArea();
        this.add(new JScrollPane(displayArea), BorderLayout.CENTER);

        setSize(300, 150);
        setVisible(true);
    }
    
    public void start() {
        // create thread
        if (ui == null) {
            ui = new Thread(this);
            ui.start();
        }
    }

    // connect to server and process messages from server
    public void run() {
        // connect to server, get streams, process connection
        try {
            connectToServer(); // Step 1: Create a Socket to make connection
            getStreams(); // Step 2: Get the input and output streams
            processConnection(); // Step 3: Process connection
        }

        // server closed connection
        catch (EOFException eofException) {
            System.err.println("Client terminated connection");
        }

        // process problems communicating with server
        catch (IOException e) {
            e.printStackTrace();
        }

        finally {
            closeConnection(); // Step 4: Close connection
        }
    }
    
    // clear thread
    public void stop() {
        if ((ui != null) && ui.isAlive()) {
            ui = null;
        }
    }

    // connect to server
    private void connectToServer() throws IOException {
        displayMessage("Attempting connection");

        // create Socket to make connection to server
        client = new Socket(InetAddress.getByName(chatServer), 9103);

        // display connection information
        displayMessage("Connected to: " + client.getInetAddress().getHostName());
    }

    // get streams to send and receive data
    private void getStreams() throws IOException {
        // set up output stream for objects
        output = new ObjectOutputStream(client.getOutputStream());
        output.flush(); // flush output buffer to send header information

        // set up input stream for objects
        input = new ObjectInputStream(client.getInputStream());

        displayMessage("Got I/O streams");
    }

    // process connection with server
    private void processConnection() throws IOException {
        // enable enterField so client user can send messages
        setTextFieldEditable(true);

        do {
            // read message and display it
            try {
                message = (String) input.readObject();
                displayMessage("\n" + message);
            }

            // catch problems reading from server
            catch (ClassNotFoundException classNotFoundException) {
                displayMessage("\nUnknown object type received");
            }

        } while (!message.equals("TERMINATE"));

    } // end method processConnection

    // close streams and socket
    private void closeConnection() {
        displayMessage("Closing connection");
        setTextFieldEditable(false);

        try {
            output.close();
            input.close();
            client.close();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    // send message to server
    private void sendData(String message) {
        // send object to server
        try {
            output.writeObject(message);
            output.flush();
            displayMessage("Client> " + message);
        }

        // process problems sending object
        catch (IOException ioException) {
            displayArea.append("Error writing object");
        }
    }

    // utility method called from other threads to manipulate
    // displayArea in the event-dispatch thread
    private void displayMessage(final String messageToDisplay) {
        // display message from GUI thread of execution
        SwingUtilities.invokeLater(new Runnable() {
                    public void run() // updates displayArea
                    {
                        displayArea.append(messageToDisplay);
                        displayArea.setCaretPosition(displayArea.getText()
                                .length());
                    }
                } // end inner class
                ); // end call to SwingUtilities.invokeLater
    }

    // utility method called from other threads to manipulate
    // enterField in the event-dispatch thread
    private void setTextFieldEditable(final boolean editable) {
        // display message from GUI thread of execution
        SwingUtilities.invokeLater(new Runnable() {
                    public void run() // sets enterField's editability
                    {
                        enterField.setEditable(editable);
                    }
                } // end inner class
                ); // end call to SwingUtilities.invokeLater
    }
}
