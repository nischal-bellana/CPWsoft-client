package com.game_states;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.util.concurrent.ConcurrentLinkedQueue;

public class UDPServerBridgeSender implements Runnable{

	private DatagramSocket socket;
	private DatagramPacket packet;
	private byte[] content;
	private InetAddress serverip;
	private int port = 1344;

	private ConcurrentLinkedQueue<String> messages;
	private volatile boolean running = true;

	public UDPServerBridgeSender(ServerBridge serverbridge) throws IOException {
		Socket serversockettcp = serverbridge.getServerSocket();
		serverip = serversockettcp.getInetAddress();

		socket = new DatagramSocket();

		messages = new ConcurrentLinkedQueue<>();
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub

		while(running) {
			if(!messages.isEmpty()) {
				try {
					sendMessage(messages.poll());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}

	}

	public void addMessage(String message) {

		messages.offer(message);
	}

	public synchronized void closeSocket() {
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

	private synchronized String receiveMessage() throws IOException {
		content = new byte[65535];

		packet = new DatagramPacket(content, content.length);

		socket.receive(packet);

		return data(content);

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
