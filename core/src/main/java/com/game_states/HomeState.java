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
	public void create(State prevst) {
		// TODO Auto-generated method stub
		super.create(prevst);
		
		this.name = prevst.next_state_inf[0];
		
		createStage();
	}

	@Override
	public void createStage() {
		// TODO Auto-generated method stub
		Table table = new Table();
		table.setBackground(skin.getDrawable("back"));
		initStage(table);
		table.top().left();
		table.setDebug(false);
		
		Button back_b = new Button(skin, "backbutton");
		back_b.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				// TODO Auto-generated method stub
				appendRequest("hb");
			}
		});
		table.add(back_b);
		
		Label l_nopo = new Label("No of players online: ", skin);
		table.add(l_nopo).padLeft(300);
		
		Label online_count_l =  new Label("1", skin);
		online_count_l.setName("onlinecount");
		online_count_l.setColor(0, 1, 0, 1);
		table.add(online_count_l);
		
		Label l_username = new Label("Username: " + name, skin);
		table.add(l_username).padLeft(10);
		
		TextButton settings_tb = new TextButton("Settings",skin);
		table.add(settings_tb).expandX().right(); 
		table.row();
		
		TextButton play_online_tb = new TextButton("Play Online", skin);
		table.add(play_online_tb).colspan(5).width(200).padTop(150);
		play_online_tb.addListener(new ChangeListener() {

			@Override
			public void changed(ChangeEvent event, Actor actor) {
				// TODO Auto-generated method stub
				appendRequest("ho");
			}
			
		});
	}

	@Override
	public void poll() {
		// TODO Auto-generated method stub
		appendRequest("co");
	}

	@Override
	public void handleResponse(int start, int end, String return_message) {
		// TODO Auto-generated method stub
		
		if(ParsingUtils.requestCheck(start, return_message, "co") && return_message.charAt(start + 2) == 'p') {
			Label online_count_l = (Label) stage.getRoot().findActor("onlinecount");
			online_count_l.setText(ParsingUtils.parseInt(start + 3, end, return_message));
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
