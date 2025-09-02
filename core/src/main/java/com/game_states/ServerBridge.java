package com.game_states;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.ConcurrentLinkedQueue;


public class ServerBridge implements Runnable{

	private Socket server;
	private BufferedReader in;
	private PrintWriter out;

	private ConcurrentLinkedQueue<String> messages;
	private ConcurrentLinkedQueue<String> return_messages;
	private volatile boolean just_cleared = false;

	public ServerBridge(Socket server, String name) throws IOException {
		this.server = server;
		in = new BufferedReader(new InputStreamReader(server.getInputStream()));
		out = new PrintWriter(server.getOutputStream(), true);

		messages = new ConcurrentLinkedQueue<>();
	    return_messages = new ConcurrentLinkedQueue<>();

		addMessage(name);

	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		while(!server.isClosed()) {
			if(!messages.isEmpty()) {
				String message = messages.poll();
				String return_message = sendMessage(message);

				if(just_cleared) continue;

				return_messages.offer(return_message);
			}

			just_cleared = false;

			try {
				Thread.sleep(5);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	public void addMessage(String message) {

		messages.offer(message);
	}

	public String pollReturnMessage() {
		if(return_messages.isEmpty()) return "";

		return return_messages.poll();
	}

	private synchronized String sendMessage(String message) {
		try {
			out.println(message);
			String response = in.readLine();
			return response;
		}
		catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return null;
		}
	}

	public void clearQueues() {
		messages.clear();
		return_messages.clear();

		just_cleared = true;
	}

	public boolean isConnected() {
		return !server.isClosed();
	}

	public synchronized void closeSocket() {
		try {
			in.close();
			out.close();
			server.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public Socket getServerSocket() {
		return server;
	}

}
