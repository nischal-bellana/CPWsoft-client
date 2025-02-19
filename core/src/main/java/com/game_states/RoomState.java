package com.game_states;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.ScreenUtils;

public class RoomState extends HomeState{
	
	Label roomname;
	Table chatarea;
	Table userslist;
	Label allreadytime;
	int chatIndex = 0;
	String chatrequest = "rh0";
	boolean myMessage = false;
	boolean isready = false;
	
	public RoomState(State prevst, String name, String rname) {
		super(prevst, name);
		roomname.setText("Room Name: " + rname);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void create() {
		// TODO Auto-generated method stub
		super.create();
	}

	@Override
	protected void render() {
		// TODO Auto-generated method stub
		super.render();
	}

	@Override
	protected void dispose() {
		// TODO Auto-generated method stub
		super.dispose();
	}

	@Override
	protected void resize(int width, int height) {
		// TODO Auto-generated method stub
		super.resize(width, height);
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
		table.add(topbar).colspan(2).left();
		
		Button back = new Button(skin, "backbutton");
		back.addListener(new ChangeListener() {

			@Override
			public void changed(ChangeEvent event, Actor actor) {
				// TODO Auto-generated method stub
				if(!sendMsg("rb").equals("f")) {
					gsm.next_st = new LobbyState(RoomState.this, name);
				}
			}
			
		});
		topbar.add(back).left();
		
		Label nop = new Label("No of players in the Room: ", skin);
		topbar.add(nop).padLeft(300);
		
		Label oc =  online = new Label("1", skin);
		oc.setColor(0, 1, 0, 1);
		topbar.add(oc);
		
		roomname = new Label("", skin);
		topbar.add(roomname).padLeft(10);
		
		chatarea = new Table();
		chatarea.top().left();
		
		ScrollPane scpleft = new ScrollPane(chatarea, skin);
		table.row();
		table.add(scpleft).width(400).left().height(300).padLeft(20).expandX().colspan(2);
		
		userslist = new Table();
		userslist.top();
		userslist.setDebug(false);
		
		ScrollPane scpright = new ScrollPane(userslist, skin);
		table.add(scpright).height(300).width(200).padRight(20).expandX().right();
		
		table.row();
		TextField chatfield = new TextField("", skin);
		table.add(chatfield).fillX().padLeft(20).height(30);
		
		TextButton send = new TextButton("Send", skin);
		send.addListener(new ChangeListener() {

			@Override
			public void changed(ChangeEvent event, Actor actor) {
				// TODO Auto-generated method stub
				String content = chatfield.getText();
				if(content == null || content.equals("")) return;
				
				sendMsg("rm" + content);
			}
			
		});
		table.add(send).left().height(50).width(100);
		
		table.row();
		table.add();
		table.add();
		TextButton readybutton = new TextButton("Ready", skin);
		readybutton.addListener(new ChangeListener() {

			@Override
			public void changed(ChangeEvent event, Actor actor) {
				// TODO Auto-generated method stub
				String response = sendMsg("rr");
				if(response.charAt(0) == 'p') {
					synchronized(RoomState.this) {
						isready = !isready;
						readybutton.setText(isready? "X Cancel" : "Ready");
					}
				}
			}
			
		});
		table.add(readybutton).height(50).width(100).right();
		
		allreadytime = new Label("30", skin);
		allreadytime.setVisible(false);
		table.row();
		table.add(allreadytime).right().colspan(3).height(50).width(50);
	}

	@Override
	protected void stageRender(float delta) {
		// TODO Auto-generated method stub
		super.stageRender(delta);
	}

	@Override
	protected void batchRender() {
		// TODO Auto-generated method stub
		super.batchRender(); 
	}

	@Override
	protected void preRender(float delta) {
		// TODO Auto-generated method stub
		super.preRender(delta);
	}
	
	protected void refreshUsersList(String data) {
		userslist.clearChildren();
		Label users = new Label("Users", skin, "head");
		userslist.add(users).padBottom(20).height(50).width(80);
		userslist.row();
		
		if(data.equals("")) return;
		String[] usernames = data.split(",");
		
		for(String user: usernames) {
			String[] usersplitted = user.split("&");
			Label label = new Label(usersplitted[0], skin);
			userslist.add(label).padBottom(20);
			Image statusimg = new Image();
			statusimg.setDrawable(skin.getDrawable(usersplitted[1].equals("p")?"statuson" : "statusoff"));
			userslist.add(statusimg).padBottom(20);
			userslist.row();
		}
	}
	
	protected void refreshChat() {
		String response = sendMsg(chatrequest);
		
		while(response.charAt(0) == 'p') {
			String content = response.substring(1);
			if(chatIndex % 2 == 0) {
				myMessage = content.equals(name);
				if(!myMessage) {
					Label clientname = new Label(content, skin);
					clientname.setColor(Color.BLUE);
					chatarea.row();
					chatarea.add(clientname).expandX().left();
				}
			}
			else {
				Label message = new Label(content, skin);
				chatarea.row();
				Cell<Label> cell = chatarea.add(message).expandX();
				if(myMessage) {
					message.setColor(Color.GOLD);
					cell.right();
				}
				else {
					cell.left();
				}
			}
			chatIndex++;
			chatrequest = "rh" + chatIndex;
			response = sendMsg(chatrequest);
		}
		
	}

	@Override
	protected void polling() {
		// TODO Auto-generated method stub
		String response = sendMsg("rn");
		if(response.charAt(0) == 'p' && response.length() > 1) {
			online.setText(response.substring(1));
		}
		
		refreshChat();
		
		response = sendMsg("ru");
		if(response.charAt(0) == 'p') {
			refreshUsersList(response.length() > 1? response.substring(1) : "");
		}
		
		synchronized(this) {
			if(isready) {
				response = sendMsg("ra");
				if(response.charAt(0) == 'p') {
					allreadytime.setVisible(true);
					String time = response.substring(1);
					int value = (int)Float.parseFloat(time);
					allreadytime.setText(value<0?0:value);
					if(value <= 0 && sendMsg("rs").charAt(0) == 'p') {
						gsm.next_st = new GameState(this, name);
					}
				}
				else {
					allreadytime.setVisible(false);
					if(response.charAt(1) == '1') {
						isready = false;
					}
				}
			}
			else {
				allreadytime.setVisible(false);
			}
		}
		
	}
	
	
	
}
