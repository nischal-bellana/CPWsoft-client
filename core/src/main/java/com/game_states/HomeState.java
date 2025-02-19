package com.game_states;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

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
	
	Label online;
	String name; 
	float time = 0;
	
	HomeState(State prevst, String name){
		this.gsm = prevst.gsm;
		
		this.camera = prevst.camera;
	    this.batch = prevst.batch;
		this.mainvp = prevst.mainvp;
		
		this.skin = prevst.skin;
		
		this.server = prevst.server;
		this.in = prevst.in;
		this.out = prevst.out;
		
		this.name = name;
		
		create();
	}

	@Override
	protected void create() {
		// TODO Auto-generated method stub
		createStage();
	}

	@Override
	protected void render() {
		// TODO Auto-generated method stub
		float delta = Gdx.graphics.getDeltaTime();
		
		preRender(delta);
		
		batchRender();
		
		stageRender(delta);
	}

	@Override
	protected void dispose() {
		// TODO Auto-generated method stub
		super.dispose();
		if(gsm.next_st != null && gsm.next_st instanceof FirstState) {
			if(server != null && !server.isClosed()) {
				try {
					sendMsg("hb");
					server.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        }
		}
	}

	@Override
	protected void resize(int width, int height) {
		// TODO Auto-generated method stub
		mainvp.update(width, height);
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
				gsm.next_st = new FirstState(HomeState.this);
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
				System.out.println("Into Lobby");
				gsm.next_st = new LobbyState(HomeState.this, name);
			}
			
		});
		
		table.row();
		
		TextButton playo = new TextButton("Play Offline", skin);
		table.add(playo).colspan(5).padTop(50);
		
	}

	@Override
	protected void stageRender(float delta) {
		// TODO Auto-generated method stub
		super.stageRender(delta);
	}

	@Override
	protected void batchRender() {
		// TODO Auto-generated method stub
		
	}

	protected void preRender(float delta) {
		// TODO Auto-generated method stub
		super.preRender();
		
		time += delta;
		
		if(time > 0.3f) {
			polling();
			time = 0;
		}
	}
	
	protected synchronized String sendMsg(String sending) {
		String received = "";
		try {
			out.println(sending);
			
			received = in.readLine();
		}
		catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return received;
	}
	
	protected void polling() {
		String received = sendMsg("co");
		if(received.equals("f")) {
			online.setText(0);
			return;
		}
		online.setText(received.substring(1));
	}
	
}
