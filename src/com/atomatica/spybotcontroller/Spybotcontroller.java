package com.atomatica.spybotcontroller;

import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Spybotcontroller extends JApplet {
    ControlPanel controlPanel;

    public void init() {
        controlPanel = new ControlPanel("atomatica.com");

        Container container=getContentPane();
        container.setLayout(new GridLayout());
        container.add(controlPanel);
    }

    public void start() {
        controlPanel.start();
    }

    public void stop() {
        controlPanel.stop();
    }

    public void destroy() {

    }
}

class ControlPanel extends JPanel implements Runnable {
    private Thread thread;
    
    private JTextField enterField;
    private JTextArea displayArea;

    private String chatServer;
    
    private Socket client;
    protected BufferedReader input;
    protected PrintWriter output;
    private String message = "";

    public ControlPanel(String host) {
        chatServer = host;

        setLayout(new BorderLayout(5, 5));
        enterField = new JTextField();
        enterField.setEditable(false);
        enterField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                sendData(event.getActionCommand());
                enterField.setText("");
            }
        });
        add(enterField, BorderLayout.NORTH);
        displayArea = new JTextArea();
        this.add(new JScrollPane(displayArea), BorderLayout.CENTER);

        setSize(300, 150);
        setVisible(true);
    }
    
    public void start() {
        if (thread == null) {
            thread = new Thread(this);
            thread.start();
        }
    }

    public void run() {
        try {
            connectToServer();
            getStreams();
            processConnection();
        }

        catch (EOFException e) {
            displayMessage("Server terminated connection\n");
        }

        catch (IOException e) {
            e.printStackTrace();
        }

        finally {
            closeConnection();
        }
    }
    
    public void stop() {
        if ((thread != null) && thread.isAlive()) {
            thread = null;
        }
    }

    private void connectToServer() throws IOException {
        displayMessage("Attempting connection\n");
        client = new Socket(InetAddress.getByName(chatServer), 9103);
        displayMessage("Connected to: " + client.getInetAddress().getHostName() + "\n");
    }

    private void getStreams() throws IOException {
        output = new PrintWriter(client.getOutputStream(), true);
        input = new BufferedReader(new InputStreamReader(client.getInputStream()));
    }

    private void processConnection() throws IOException {
        setTextFieldEditable(true);

        do {
            message = input.readLine();
            displayMessage("Server> " + message + "\n");
        } while (!message.equals("TERMINATE"));
    }

    private void closeConnection() {
        displayMessage("Closing connection\n");
        setTextFieldEditable(false);

        try {
            output.close();
            input.close();
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendData(String message) {
        output.println(message);
        output.flush();
        displayMessage("Client> " + message + "\n");
    }

    private void displayMessage(final String message) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run(){
                displayArea.append(message);
                displayArea.setCaretPosition(displayArea.getText().length());
            }
        });
    }

    private void setTextFieldEditable(final boolean editable) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                enterField.setEditable(editable);
            }
        });
    }
}
