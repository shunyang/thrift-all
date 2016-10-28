package com.yangyang.thrift;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


public class ServerSocketTest {

    public static void main(String[] args) {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(8000);
            Socket socket = serverSocket.accept();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
