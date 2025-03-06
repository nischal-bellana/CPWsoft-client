package com.game_states;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.SocketException;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent;
import com.badlogic.gdx.utils.viewport.Viewport;

public class HomeState extends State{
	
	private Label online;
	private String name;
	
	HomeState(State prevst, String name){
		super(prevst);
		
		this.name = name;
		
	}

	@Override
	public void create() {
		// TODO Auto-generated method stub
		createStage();
		
		message = new StringBuilder();
		
	}

	@Override
	protected void createStage() {
		// TODO Auto-generated method stub
		Table table = new Table();
		table.setBackground(skin.getDrawable("back"));
		initStage(table);
		table.top().left();
		table.setDebug(false);
		
//		table.row().expandX();
		
		Button back = new Button(skin, "backbutton");
		back.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				// TODO Auto-generated method stub
				appendRequest("hb");
			}
		});
		table.add(back);
		
		Label nop = new Label("No of players online: ", skin);
		table.add(nop).padLeft(300);
		
		Label oc =  online = new Label("1", skin);
		oc.setColor(0, 1, 0, 1);
		table.add(oc);
		
		Label username = new Label("Username: " + name, skin);
		table.add(username).padLeft(10);
		
		TextButton settings = new TextButton("Settings",skin);
		table.add(settings).expandX().right();
		table.row();
		
		TextButton playm = new TextButton("Play Online", skin);
		table.add(playm).colspan(5).width(200).padTop(150);
		playm.addListener(new ChangeListener() {

			@Override
			public void changed(ChangeEvent event, Actor actor) {
				// TODO Auto-generated method stub
				appendRequest("ho");
			}
			
		});
	}

	@Override
	protected void polling() {
		// TODO Auto-generated method stub
		appendRequest("co");
	}

	@Override
	protected void handleResponse(String response) {
		// TODO Auto-generated method stub
		String request = response.substring(0, 2);
		
		if(request.equals("co") && response.charAt(2) == 'p') {
			online.setText(response.substring(3));
			return;
		}
		
		else if(request.equals("hb") && response.charAt(2) == 'p') {
			changeState(new FirstState(this));
		}
		
		else if(request.equals("ho") && response.charAt(2) == 'p') {
			changeState(new LobbyState(this, name));
		}
	}
	
	
	
}
