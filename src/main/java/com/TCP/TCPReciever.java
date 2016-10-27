package com.TCP;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.OutputStream;
import java.net.InetAddress;

public class TCPReciever extends Thread {
    private String adrIp = null;
    protected int port;
    protected ServerSocket server =null;
    
    public TCPReciever(String AdrIp, int Port) {
        this.adrIp = AdrIp;
        port = port;
        System.out.println(this.port);
        try {
            this.server = new ServerSocket(this.port, 50, InetAddress.getByName(this.adrIp)); 
        } catch (Exception e) {
            System.out.println("wait");
        }
    }
};
