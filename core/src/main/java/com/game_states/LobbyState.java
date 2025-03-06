package com.game_states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent;

public class LobbyState extends State{
	private String name;
	private Label online;
	private Table scrtable;
	private ButtonGroup<TextButton> bgrp;

	public LobbyState(State prevst, String name) {
		super(prevst);
		
		this.name = name;
		// TODO Auto-generated constructor stub
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
		Table topbar = new Table();
		
		Button back = new Button(skin, "backbutton");
		back.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				// TODO Auto-generated method stub
				changeState(new HomeState(LobbyState.this, name));
			}
		});
		topbar.add(back);
		
		Label nop = new Label("No of players online: ", skin);
		topbar.add(nop).padLeft(300);
		
		Label oc =  online = new Label("1", skin);
		oc.setColor(0, 1, 0, 1);
		topbar.add(oc).left();
		
		Label username = new Label("Username: " + name, skin);
		topbar.add(username).padLeft(10);
		
		table.add(topbar).expandX().left();
		table.row();
		
		scrtable = new Table();
		scrtable.top().left();
		bgrp = new ButtonGroup<>();
		
		ScrollPane scp = new ScrollPane(scrtable, skin);
		table.add(scp).padTop(50).height(200).width(600).padBottom(50);
		table.row();
		
		Table bottombar = new Table();
		
		TextButton rnamesearch = new TextButton("Search By RoomID:", skin);
		bottombar.add(rnamesearch).height(30).width(250);
		
		TextField rname = new TextField("", skin);
		bottombar.add(rname).width(200).height(20).colspan(2);
		bottombar.row();
		
		rnamesearch.addListener(new ChangeListener() {

			@Override
			public void changed(ChangeEvent event, Actor actor) {
				// TODO Auto-generated method stub
				appendRequest("lf" + rname.getText());
			}
			
		});
		
		TextButton join = new TextButton("Join", skin);
		join.addListener(new ChangeListener() {

			@Override
			public void changed(ChangeEvent event, Actor actor) {
				// TODO Auto-generated method stub
				if(bgrp.getChecked() == null) return;
				
				String roomdata = bgrp.getChecked().getName();
				String[] dataarray = roomdata.split("&");
				int no = Integer.parseInt(dataarray[1]);
				if(no>=5) return;
					
				appendRequest("lj" + dataarray[0]);
			}
			
		});
		bottombar.add(join);
		TextButton createRoom = new TextButton("Create", skin);
		createRoom.addListener(new ChangeListener() {

			@Override
			public void changed(ChangeEvent event, Actor actor) {
				// TODO Auto-generated method stub
				appendRequest("lc");
			}
			
		});
		bottombar.add(createRoom);
		
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
		
		table.add(bottombar);
	}
	
	protected void refreshRooms(String data) {
		scrtable.clearChildren();
		scrtable.add();
		Label roomshead = new Label("Room Name", skin, "head");
		scrtable.add(roomshead).padLeft(10).padBottom(20).width(120).height(50);
		Label nopl = new Label("No of Players", skin, "head");
		scrtable.add(nopl).padLeft(10).padBottom(20).width(150).height(50);
		scrtable.row();
		bgrp.clear();
		bgrp.setMinCheckCount(0);
		
		System.out.println("refreshing rooms ...");
		if(data.equals("")) return;
		String[] roomnames = data.split(",");
		
		for(String roomdata: roomnames) {
			String[] room = roomdata.split("&");
			TextButton button = new TextButton("", skin, "checkenable");
			button.setName(roomdata);
			bgrp.add(button);
			scrtable.add(button).size(50).padBottom(20);
			Label label = new Label(room[0], skin);
			scrtable.add(label).padBottom(20);
			Label no = new Label(room[1]+"/5", skin);
			scrtable.add(no).padBottom(20);
			scrtable.row();
		}
		
	}
	
	@Override
	protected void handleResponse(String response) {
		// TODO Auto-generated method stub
		String request = response.substring(0, 2);
		
		if(request.equals("co") && response.charAt(2) == 'p') {
			online.setText(response.substring(3));
		}
		else if(request.equals("lr") && response.charAt(2) == 'p') {
			refreshRooms(response.substring(3));
		}
		else if(request.equals("lf") && response.charAt(2) == 'p') {
			refreshRooms(request.substring(3));
		}
		else if(request.equals("lj") && response.charAt(2) == 'p') {
			String roomname = response.substring(3);
			changeState(new RoomState(this, name, roomname));
		}
		else if(request.equals("lc") && response.charAt(2) == 'p') {
			String roomname = response.substring(3);
			changeState(new RoomState(this, name, roomname));
		}
	}

	@Override
	protected void polling() {
		// TODO Auto-generated method stub
		appendRequest("co");

		appendRequest("lr");
	}

	
	
}
