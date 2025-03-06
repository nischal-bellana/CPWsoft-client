package com.game_states;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.ConcurrentLinkedQueue;

public class UDPServerBridgeReceiver implements Runnable{
	
	private DatagramSocket socket;
	private DatagramPacket packet;
	private byte[] content;
	private InetAddress serverip;
	private int port = 1344;
	private Object broadcastlock;
	
	private String broadcast = "";
	private volatile boolean running = true;
	
	public UDPServerBridgeReceiver(ServerBridge serverbridge) throws IOException {
		Socket serversockettcp = serverbridge.getServerSocket();
		serverip = serversockettcp.getInetAddress();
		
		socket = new DatagramSocket();
		socket.setSoTimeout(300);
		
		broadcastlock = new Object();
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
		while(!broadcast.equals("pass")) {
			try {
				sendMessage("hello");
				receiveMessage();
			} catch (IOException e) {
				// TODO Auto-generated catch block
			}
		}
		
		System.out.println("registered!!");
		
		getBroadcast();
		
		while(running) {
			
			try {
				receiveMessage();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				
			}
		}
		
	}
	
	public String getBroadcast() {
		String tobereturned = "";
		synchronized(broadcastlock) {
			tobereturned = broadcast;
			broadcast = "";
		}
		return tobereturned;
	}
	
	public void closeSocket() {
		running = false;
		socket.close();
	}
	
	public boolean isClosed() {
		return socket.isClosed();
	}
	
	private synchronized void sendMessage(String message) throws IOException {
		content = message.getBytes();
		
		packet = new DatagramPacket(content, content.length, serverip, port);
		
		socket.send(packet);
		
	}
	
	private synchronized void receiveMessage() throws IOException {
		content = new byte[65535];
		
		packet = new DatagramPacket(content, content.length);
		
		System.out.println("Receiving...");
		
		socket.receive(packet);
		
		synchronized(broadcastlock) {
			broadcast = data(content);
			System.out.println("Received: " + broadcast);
		}
		
	}
	
	private String data(byte[] a) 
    { 
        if (a == null) 
            return null; 
        StringBuilder ret = new StringBuilder(); 
        int i = 0; 
        while (a[i] != 0) 
        { 
            ret.append((char) a[i]); 
            i++; 
        } 
        return ret.toString(); 
    }
	
}
