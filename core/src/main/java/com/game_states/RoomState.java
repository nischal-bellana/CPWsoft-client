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
import com.utils.ParsingUtils;

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

	@Override
	public void create() {
		// TODO Auto-generated method stub
		createStage();
		
		tcp_frame_message = new StringBuilder();
		
		appendRequest("ri");
	}
	
	
	
	@Override
	public void create(State prevst) {
		// TODO Auto-generated method stub
		super.create(prevst);
		
		name = prevst.next_state_inf[0];
		rname = prevst.next_state_inf[1];
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
	
	protected void refreshUsersList(int start, int end, String return_message) {
		userslist.clearChildren();
		Label users = new Label("Users", skin, "head");
		userslist.add(users).padBottom(20).height(50).width(80);
		userslist.row();
		
		if(end - start == 0) return;
		
		for(int i = start; i < end;) {
			
			int start1 = ParsingUtils.getBeginIndex(i, return_message, '&');
			int end1 = start1 + ParsingUtils.parseInt(i, start1 - 1, return_message);
			int start2 = ParsingUtils.getBeginIndex(start1, return_message, '&');
			
			Label label = new Label(return_message.substring(start1, start2 - 1), skin);
			userslist.add(label).padBottom(20);
			Image statusimg = new Image();
			statusimg.setDrawable(skin.getDrawable(return_message.charAt(start2) == 'p' ? "statuson" : "statusoff"));
			userslist.add(statusimg).padBottom(20);
			userslist.row();
			
			i = end1;
		}
	}
	
	private void refreshChat(int start, int end, String return_message) {
		
		for(int i = start; i < end;) {
			int beginindex = ParsingUtils.getBeginIndex(i, return_message, '&');
			int endindex = beginindex + ParsingUtils.parseInt(i, beginindex - 1, return_message);
			String clientname = return_message.substring(beginindex, endindex);
			i = endindex;
			
			boolean myMessage = clientname.equals(name);
			
			if(!myMessage) {
				Label clientlabel = new Label(clientname, skin);
				clientlabel.setColor(Color.BLUE);
				chatarea.row();
				chatarea.add(clientlabel).expandX().left();
			}
			
			beginindex = ParsingUtils.getBeginIndex(i, return_message, '&');
			endindex = beginindex + ParsingUtils.parseInt(i, beginindex - 1, return_message);
			String message = return_message.substring(beginindex, endindex);
			i = endindex;
			
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
	protected void poll() {
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
	public void handleResponse(int start, int end, String return_message) {
		// TODO Auto-generated method stub
		
		if(ParsingUtils.requestCheck(start, return_message, "rn") && return_message.charAt(start + 2) == 'p') {
			online.setText(ParsingUtils.parseInt(start + 3, end, return_message));
		}
		else if(ParsingUtils.requestCheck(start, return_message, "rh") && return_message.charAt(start + 2) == 'p') {
			refreshChat(start + 3, end, return_message);
		}
		else if(ParsingUtils.requestCheck(start, return_message, "ru") && return_message.charAt(start + 2) == 'p') {
			refreshUsersList(start + 3, end, return_message);
		}
		else if(ParsingUtils.requestCheck(start, return_message, "ra")) {
			if(return_message.charAt(start + 2) == 'f') {
				if(return_message.charAt(start + 3) == '1') {
					isready = false;
					TextButton readybutton = stage.getRoot().findActor("readybutton");
					readybutton.setText("Ready");
				}
				allreadytime.setVisible(false);
			} 
			else {
				allreadytime.setVisible(true);
				
				int timevalue = ParsingUtils.parseInt(start + 3, end, return_message);
				allreadytime.setText(Math.max(timevalue, 0));
				
				if(timevalue == -10) {
					next_state_inf = new String[2];
					next_state_inf[0] = name;
					next_state_inf[1] = rname;
					changeState(new GameState());
				}
			}
		}
		else if(ParsingUtils.requestCheck(start, return_message, "rr")) {
			isready = !isready;
			TextButton readybutton = stage.getRoot().findActor("readybutton");
			readybutton.setText(isready ? "X Cancel" :"Ready");
		}
		else if (ParsingUtils.requestCheck(start, return_message, "rb") && return_message.charAt(start + 2) == 'p') {
			next_state_inf = new String[1];
			next_state_inf[0] = name;
			changeState(new LobbyState());
		}
	}
	
}
