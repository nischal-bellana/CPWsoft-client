package com.game_states;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class FirstState extends State{
    
	@Override
	public void createStage(){
    	Table table = new Table();
    	table.setBackground(skin.getDrawable("back"));
    	initStage(table);
    	
    	Label username = new Label("Username", skin);
    	table.row();
    	table.add(username).padTop(18);
    	TextField name = new TextField("",skin);
    	table.row();
    	table.add(name).height(18).padTop(18);
    	
    	Button button = new Button(skin);
    	button.addListener(new ChangeListener() {

			@Override
			public void changed(ChangeEvent event, Actor actor) {
				// TODO Auto-generated method stub
				System.out.println("Connecting...");
				String usernametext = name.getText();
				usernametext = usernametext.trim();
				if(validateUsername(usernametext)) connectServer(usernametext);
			}
    		
    	});
    	table.row();
    	table.add(button).center();
    	
    	Label label = new Label("Connect to Server",skin);
    	table.row();
    	table.add(label).center().padTop(18);
    	
    	TextField tf = new TextField("localhost",skin);
    	tf.setName("serverip");
    	table.row();
    	table.add(tf).height(18).padTop(18);
    	
    	TextButton playo = new TextButton("Play Offline", skin);
    	table.row();
		table.add(playo).padTop(50);
		playo.addListener(new ChangeListener() {

			@Override
			public void changed(ChangeEvent event, Actor actor) {
				// TODO Auto-generated method stub
				changeState(new GameStateOffline());
			}
			
		});
    	
    	table.setTouchable(Touchable.enabled);
    	table.addListener(new ClickListener() {

			@Override
			public void clicked(InputEvent event, float x, float y) {
				// TODO Auto-generated method stub
				if(stage.getKeyboardFocus() == null) return;
				
				if(event.getTarget()!= stage.getKeyboardFocus()) {
					stage.unfocus(stage.getKeyboardFocus());
				}
			}
    	});
    	
    }

	public boolean validateUsername(String username) {
		if(username.length() <= 5 || username.length() > 14 || Character.isDigit(username.charAt(0))) return false;
		for(int i = 0; i < username.length(); i++) {
			char c = username.charAt(i);
			if(c != '-' && c != '_' && !Character.isAlphabetic(c) && !Character.isDigit(c)) 
				return false;
		}
		return true;
	}

	private void connectServer(String name) {
    	try {  
    		if(serverbridge != null && serverbridge.isConnected()) serverbridge.closeSocket();
    		
    		TextField tf = stage.getRoot().findActor("serverip");
    		
    		SocketAddress address = new InetSocketAddress(tf.getText(), 1323);
    		Socket server = new Socket();

    		int timeout = 3000;
    		server.connect(address, timeout);
			
    		serverbridge = new ServerBridge(server, name);
    		
    		if(!serverbridge.isConnected()) {
    			serverbridge.closeSocket();
    			serverbridge = null;
    			System.out.println("Connection failed server socket is closed");
    			return;
    		}
			
    		Thread serverbridgethread = new Thread(serverbridge);
    		serverbridgethread.start();
    		
    		next_state_inf = new String[1];
			next_state_inf[0] = name;
			changeState(new HomeState());
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Connection failed");
		}
    }
    
}
