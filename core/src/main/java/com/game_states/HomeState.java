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
import com.utils.ParsingUtils;

public class HomeState extends State{
	
	private String name;
	
	@Override
	public void create() {
		// TODO Auto-generated method stub
		createStage();
		
		tcp_frame_message = new StringBuilder();
		
	}
	
	

	@Override
	public void create(State prevst) {
		// TODO Auto-generated method stub
		super.create(prevst);
		
		this.name = next_state_inf[0];
	}

	@Override
	public void createStage() {
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
		
		Label oc =  new Label("1", skin);
		oc.setName("onlinecount");
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
	protected void poll() {
		// TODO Auto-generated method stub
		appendRequest("co");
	}

	@Override
	public void handleResponse(int start, int end, String return_message) {
		// TODO Auto-generated method stub
		
		if(ParsingUtils.requestCheck(start, return_message, "co") && return_message.charAt(start + 2) == 'p') {
			Label onlinecountlabel = (Label) stage.getRoot().findActor("onlinecount");
			onlinecountlabel.setText(ParsingUtils.parseInt(start + 3, end, return_message));
			return;
		}
		
		if(ParsingUtils.requestCheck(start, return_message, "hb") && return_message.charAt(start + 2) == 'p') {
			changeState(new FirstState());
			return;
		}
		
		if(ParsingUtils.requestCheck(start, return_message, "ho") && return_message.charAt(start + 2) == 'p') {
			next_state_inf = new String[1];
			next_state_inf[0] = name;
			changeState(new LobbyState());
		}
	}
	
	
	
}
