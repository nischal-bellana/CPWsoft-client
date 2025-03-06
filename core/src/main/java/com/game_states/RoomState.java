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

public class RoomState extends State{
	
	Label roomname;
	Label online;
	String name;
	String rname;
	Table chatarea;
	Table userslist;
	Label allreadytime;
	int chatIndex = 0;
	String chatrequest = "rh0";
	boolean isready = false;
	
	public RoomState(State prevst, String name, String rname) {
		super(prevst);
		this.name = name;
		this.rname = rname;
		
		// TODO Auto-generated constructor stub
	}

	@Override
	public void create() {
		// TODO Auto-generated method stub
		createStage();
		
		message = new StringBuilder();
		
		appendRequest("ri");
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
				appendRequest("rb");
			}
			
		});
		topbar.add(back).left();
		
		Label nop = new Label("No of players in the Room: ", skin);
		topbar.add(nop).padLeft(300);
		
		Label oc =  online = new Label("1", skin);
		oc.setColor(0, 1, 0, 1);
		topbar.add(oc);
		
		roomname = new Label("Room Name: " + rname, skin);
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
				if(content.equals("") || content.contains("{") || content.contains("}")) return;
				
				appendRequest("rm" + content);
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
				appendRequest("rr");
			}
			
		});
		readybutton.setName("readybutton");
		table.add(readybutton).height(50).width(100).right();
		
		allreadytime = new Label("30", skin);
		allreadytime.setVisible(false);
		table.row();
		table.add(allreadytime).right().colspan(3).height(50).width(50);
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
	
	private void refreshChat(String newchat) {
		int i = 0;
		
		System.out.println("newchat: " + newchat);
		
		while(i < newchat.length()) {
			int beginindex = getBeginIndex(newchat, i, '&');
			int endindex = beginindex + getEndIndex(newchat, i, '&');
			String clientname = newchat.substring(beginindex, endindex);
			i = endindex;
			
			System.out.println("1." + beginindex + " " + endindex);
			
			boolean myMessage = clientname.equals(name);
			
			if(!myMessage) {
				Label clientlabel = new Label(clientname, skin);
				clientlabel.setColor(Color.BLUE);
				chatarea.row();
				chatarea.add(clientlabel).expandX().left();
			}
			
			beginindex = getBeginIndex(newchat, i, '&');
			endindex = beginindex + getEndIndex(newchat, i, '&');
			String message = newchat.substring(beginindex, endindex);
			i = endindex;
			
			System.out.println("2." + beginindex + " " + endindex);
			
			Label messagelabel = new Label(message, skin);
			chatarea.row();
			Cell<Label> cell = chatarea.add(messagelabel).expandX();
			if(myMessage) {
				messagelabel.setColor(Color.GOLD);
				cell.right();
			}
			else {
				cell.left();
			}
			
			chatIndex++;
		}
		
		chatrequest = "rh" + chatIndex;
		
	}

	@Override
	protected void polling() {
		// TODO Auto-generated method stub
		appendRequest("rn");
		
		appendRequest(chatrequest);
		
		appendRequest("ru");
		
		synchronized(this) {
			if(isready) {
				appendRequest("ra");
			}
		}
		
	}

	@Override
	protected void handleResponse(String response) {
		// TODO Auto-generated method stub
		String request = response.substring(0, 2);
		
		if(request.equals("rn") && response.charAt(2) == 'p') {
			online.setText(response.substring(3));
		}
		else if(request.equals("rh") && response.charAt(2) == 'p') {
			refreshChat(response.substring(3));
		}
		else if(request.equals("ru") && response.charAt(2) == 'p') {
			refreshUsersList(response.substring(3));
		}
		else if(request.equals("ra")) {
			if(response.charAt(2) == 'f') {
				if(response.charAt(3) == '1') {
					isready = false;
					TextButton readybutton = stage.getRoot().findActor("readybutton");
					readybutton.setText("Ready");
				}
				allreadytime.setVisible(false);
			}
			else {
				allreadytime.setVisible(true);
				
				int timevalue = Integer.parseInt(response.substring(3));
				allreadytime.setText(Math.max(timevalue, 0));
				
				if(timevalue == -10) {
					changeState(new GameState(this, rname, name));
				}
			}
		}
		else if(request.equals("rr")) {
			isready = !isready;
			TextButton readybutton = stage.getRoot().findActor("readybutton");
			readybutton.setText(isready ? "X Cancel" :"Ready");
		}
		else if (request.equals("rb") && response.charAt(2) == 'p'){
			changeState(new LobbyState(this, name));
		}
	}
	
	private int getEndIndex(String s, int i, char stopchar) {
		int num = 0; 
		
		
		while(true) {
			char c = s.charAt(i);
			
			if(c == stopchar) {
				return num;
			}
			
			int value = c - '0';
			num *= 10;
			num += value;
			
			i++;
		}
	}
	
	private int getBeginIndex(String s, int i, char stopchar) {
		while(true) {
			char c = s.charAt(i);
			
			if(c == stopchar) {
				return i + 1;
			}
			
			i++;
		}
	}
	
}
