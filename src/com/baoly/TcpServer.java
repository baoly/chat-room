package com.baoly;

import java.io.*;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * @author baoly
 * @version 1.0
 * @date 2020/10/11 13:06
 * @description
 */
public class TcpServer {

    boolean isStarted = false;
    ServerSocket serverSocket = null;
    List<Client> clients = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        new TcpServer().start();
    }

    public void start() {
        try {
            serverSocket = new ServerSocket(6666);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            if (serverSocket != null) {
                isStarted = true;
            }
            while (isStarted) {
                Socket socket = serverSocket.accept();
                Client client = new Client(socket);
                clients.add(client);
                new Thread(client).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (serverSocket != null) {
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private class Client implements Runnable {
        private Socket socket;
        private DataInputStream dis;
        private DataOutputStream dos;
        private boolean isConnected;

        public Client(Socket socket) {
            try {
                this.socket = socket;
                this.dis = new DataInputStream(socket.getInputStream());
                this.dos = new DataOutputStream(socket.getOutputStream());
                this.isConnected = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void sendMsg(String msg) {
            try {
                dos.writeUTF(msg);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            System.out.println("客户端连接上了");
            try {
                while (isConnected) {
                    String msg = dis.readUTF();
                    for (Client client : clients) {
                        client.sendMsg(msg);
                    }
                }
            } catch (BindException e) {
                System.out.println("端口正在使用");
                System.out.println("请关掉相关程序并重启服务器");
                System.exit(0);
            } catch (EOFException e) {
                System.out.println("客户端已关闭");
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (socket != null) {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (dis != null) {
                    try {
                        dis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (dos != null) {
                    try {
                        dos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
