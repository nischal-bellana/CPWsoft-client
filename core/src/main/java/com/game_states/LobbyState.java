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
import com.utils.ParsingUtils;

public class LobbyState extends State{
	private String name;
	private Label online;
	private Table scrtable;
	private ButtonGroup<TextButton> bgrp;

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
		
		name = prevst.next_state_inf[0];
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
		Table topbar = new Table();
		
		Button back = new Button(skin, "backbutton");
		back.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				// TODO Auto-generated method stub
				changeState(new HomeState());
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
	
	protected void refreshRooms(int start, int end, String return_message) {
		scrtable.clearChildren();
		scrtable.add();
		Label roomshead = new Label("Room Name", skin, "head");
		scrtable.add(roomshead).padLeft(10).padBottom(20).width(120).height(50);
		Label nopl = new Label("No of Players", skin, "head");
		scrtable.add(nopl).padLeft(10).padBottom(20).width(150).height(50);
		scrtable.row();
		bgrp.clear();
		bgrp.setMinCheckCount(0);
		
		if(end - start == 0) return;

		for(int i = start; i < end;) {
			
			int start1 = ParsingUtils.getBeginIndex(i, return_message, '&');
			int end1 = start1 + ParsingUtils.parseInt(i, start1 - 1 , return_message);
			
			TextButton button = new TextButton("", skin, "checkenable");
			button.setName(return_message.substring(start1, end1));
			bgrp.add(button);
			scrtable.add(button).size(50).padBottom(20);
			
			int start2 = ParsingUtils.getBeginIndex(start1, return_message, '&');
			
			Label label = new Label(return_message.substring(start1, start2 - 1), skin);
			scrtable.add(label).padBottom(20);
			Label no = new Label(ParsingUtils.parseInt(start2, end1, return_message) +"/5", skin);
			scrtable.add(no).padBottom(20);
			scrtable.row();
			
			i = end1;
		}
		
	}
	
	@Override
	public void handleResponse(int start, int end, String return_message) {
		// TODO Auto-generated method stub
		
		if(ParsingUtils.requestCheck(start, return_message, "co") && return_message.charAt(start + 2) == 'p') {
			online.setText(ParsingUtils.parseInt(start + 3, end, return_message));
		}
		else if(ParsingUtils.requestCheck(start, return_message, "lr") && return_message.charAt(start + 2) == 'p') {
			refreshRooms(start + 3, end, return_message);
		}
		else if(ParsingUtils.requestCheck(start, return_message, "lf") && return_message.charAt(start + 2) == 'p') {
			refreshRooms(start + 3, end, return_message);
		}
		else if(ParsingUtils.requestCheck(start, return_message, "lj") && return_message.charAt(start + 2) == 'p') {
			String roomname = return_message.substring(start + 3, end);
			next_state_inf = new String[2];
			next_state_inf[0] = name;
			next_state_inf[1] = roomname;
			changeState(new RoomState());
		}
		else if(ParsingUtils.requestCheck(start, return_message, "lc") && return_message.charAt(start + 2) == 'p') {
			String roomname = return_message.substring(start + 3, end);
			next_state_inf = new String[2];
			next_state_inf[0] = name;
			next_state_inf[1] = roomname;
			changeState(new RoomState());
		}
	}

	@Override
	protected void poll() {
		// TODO Auto-generated method stub
		appendRequest("co");

		appendRequest("lr");
	}

	
	
}
