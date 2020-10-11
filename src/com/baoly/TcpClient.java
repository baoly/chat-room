package com.baoly;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * @author baoly
 * @version 1.0
 * @date 2020/10/11 13:05
 * @description
 */
public class TcpClient extends Frame {
    private TextField textField = new TextField();
    private TextArea textArea = new TextArea();
    private Socket socket = null;
    private DataOutputStream dos = null;
    private DataInputStream dis = null;
    private boolean isConnected = false;


    public static void main(String[] args) throws IOException {
        TcpClient tcpClient = new TcpClient();
        tcpClient.lanchFrame();
    }

    public void lanchFrame() {
        setLocation(300, 400);
        setSize(400, 400);
        add(textField, BorderLayout.SOUTH);
        add(textArea, BorderLayout.NORTH);
        pack();

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                disconnect();
                System.exit(0);
            }
        });

        textField.addActionListener(new TextFiledListener());
        connect();
        setVisible(true);
    }

    public void connect() {
        try {
            socket = new Socket("127.0.0.1", 6666);
            System.out.println("已经连接到服务器");
            isConnected = true;
            dos = new DataOutputStream(socket.getOutputStream());
            dis = new DataInputStream(socket.getInputStream());
            new Thread(new ReceiveThread()).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void disconnect() {
        try {
            socket.close();
            dos.close();
            dis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private class TextFiledListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            String msg = textField.getText();
            textField.setText("");
            try {
                dos.writeUTF(msg);
                dos.flush();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    private class ReceiveThread implements Runnable {
        @Override
        public void run() {
            while (isConnected) {
                String msg = null;
                try {
                    msg = dis.readUTF();
                    textArea.setText(textArea.getText() + msg + "\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }
}
