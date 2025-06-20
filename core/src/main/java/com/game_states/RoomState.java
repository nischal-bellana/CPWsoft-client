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
import com.utils.ParsingUtils;

public class RoomState extends State {
	String name;
	String room_name;
	int chatIndex = 0;
	String chatrequest = "rh0";
	boolean isready = false;

	@Override
	public void create(State prevst) {
		// TODO Auto-generated method stub
		super.create(prevst);

		name = prevst.next_state_inf[0];
		room_name = prevst.next_state_inf[1];

		createStage();

		appendRequest("ri");
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

		Table topbar_t = new Table();
		table.add(topbar_t).colspan(2).left();

		Button back_b = new Button(skin, "backbutton");
		back_b.addListener(new ChangeListener() {

			@Override
			public void changed(ChangeEvent event, Actor actor) {
				// TODO Auto-generated method stub
				appendRequest("rb");
			}

		});
		topbar_t.add(back_b).left();

		Label l_nopr = new Label("No of players in the Room: ", skin);
		topbar_t.add(l_nopr).padLeft(300);

		Label online_count_l = new Label("1", skin);
		online_count_l.setName("onlinecount");
		online_count_l.setColor(0, 1, 0, 1);
		topbar_t.add(online_count_l);

		Label room_name_l = new Label("Room Name: " + room_name, skin);
		topbar_t.add(room_name_l).padLeft(10);

		Table chatarea_t = new Table();
		chatarea_t.top().left();
		chatarea_t.setName("chatarea");

		ScrollPane scrollpane_left_sp = new ScrollPane(chatarea_t, skin);
		table.row();
		table.add(scrollpane_left_sp).width(400).left().height(300).padLeft(20).expandX().colspan(2);

		Table userslist_t = new Table();
		userslist_t.top();
		userslist_t.setDebug(false);
		userslist_t.setName("userslist");

		ScrollPane scrollpane_right_sp = new ScrollPane(userslist_t, skin);
		table.add(scrollpane_right_sp).height(300).width(200).padRight(20).expandX().right();

		table.row();
		TextField chatfield_tf = new TextField("", skin);
		table.add(chatfield_tf).fillX().padLeft(20).height(30);

		TextButton send_tb = new TextButton("Send", skin);
		send_tb.addListener(new ChangeListener() {

			@Override
			public void changed(ChangeEvent event, Actor actor) {
				// TODO Auto-generated method stub
				String content = chatfield_tf.getText();
				if(content.equals("") || content.contains("{") || content.contains("}")) return;

				appendRequest("rm" + content);
			}

		});
		table.add(send_tb).left().height(50).width(100);

		table.row();
		table.add();
		table.add();
		TextButton ready_tb = new TextButton("Ready", skin);
		ready_tb.addListener(new ChangeListener() {

			@Override
			public void changed(ChangeEvent event, Actor actor) {
				// TODO Auto-generated method stub
				appendRequest("rr");
			}

		});
		ready_tb.setName("readybutton");
		table.add(ready_tb).height(50).width(100).right();

		Label allready_time_l = new Label("30", skin);
		allready_time_l.setVisible(false);
		allready_time_l.setName("allreadytime");
		table.row();
		table.add(allready_time_l).right().colspan(3).height(50).width(50);
	}

	protected void refreshUsersList(int start, int end, String return_message) {
		Table userslist_t = stage.getRoot().findActor("userslist");
		userslist_t.clearChildren();
		Label users = new Label("Users", skin, "head");
		userslist_t.add(users).padBottom(20).height(50).width(80);
		userslist_t.row();

		if(end - start == 0) return;

		for(int i = start; i < end;) {

			int start1 = ParsingUtils.getBeginIndex(i, return_message, '&');
			int end1 = start1 + ParsingUtils.parseInt(i, start1 - 1, return_message);
			int start2 = ParsingUtils.getBeginIndex(start1, return_message, '&');

			Label label = new Label(return_message.substring(start1, start2 - 1), skin);
			userslist_t.add(label).padBottom(20);
			Image statusimg = new Image();
			statusimg.setDrawable(skin.getDrawable(return_message.charAt(start2) == 't' ? "statuson" : "statusoff"));
			userslist_t.add(statusimg).padBottom(20);
			userslist_t.row();

			i = end1;
		}
	}

	private void refreshChat(int start, int end, String return_message) {
		Table chatarea_t = stage.getRoot().findActor("chatarea");

		for(int i = start; i < end;) {
			int beginindex = ParsingUtils.getBeginIndex(i, return_message, '&');
			int endindex = beginindex + ParsingUtils.parseInt(i, beginindex - 1, return_message);
			String clientname = return_message.substring(beginindex, endindex);
			i = endindex;

			boolean myMessage = clientname.equals(name);

			if(!myMessage) {
				Label clientlabel = new Label(clientname, skin);
				clientlabel.setColor(Color.BLUE);
				chatarea_t.row();
				chatarea_t.add(clientlabel).expandX().left();
			}

			beginindex = ParsingUtils.getBeginIndex(i, return_message, '&');
			endindex = beginindex + ParsingUtils.parseInt(i, beginindex - 1, return_message);
			String message = return_message.substring(beginindex, endindex);
			i = endindex;

			Label messagelabel = new Label(message, skin);
			chatarea_t.row();
			Cell<Label> cell = chatarea_t.add(messagelabel).expandX();
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
	public void poll() {
		// TODO Auto-generated method stub
		appendRequest("rn");

		appendRequest(chatrequest);

		appendRequest("ru");

		if(isready) {
			appendRequest("rg");
		}

	}

	@Override
	public void handleResponse(int start, int end, String return_message) {
		// TODO Auto-generated method stub

		if(ParsingUtils.requestCheck(start, return_message, "rn") && return_message.charAt(start + 2) == 'p') {
			Label online_count_l = stage.getRoot().findActor("onlinecount");
			online_count_l.setText(ParsingUtils.parseInt(start + 3, end, return_message));
		}
		else if(ParsingUtils.requestCheck(start, return_message, "rh") && return_message.charAt(start + 2) == 'p') {
			refreshChat(start + 3, end, return_message);
		}
		else if(ParsingUtils.requestCheck(start, return_message, "ru") && return_message.charAt(start + 2) == 'p') {
			refreshUsersList(start + 3, end, return_message);
		}
		else if(ParsingUtils.requestCheck(start, return_message, "rg")) {
			Label allready_time_l = stage.getRoot().findActor("allreadytime");

			if(return_message.charAt(start + 2) == 'f') {
				Label allready_time = (Label)stage.getRoot().findActor("allreadytime");
				if(return_message.charAt(start + 3) == '2') {
					allready_time.setVisible(true);
					allready_time.setText(return_message.substring(start + 4, end));
				}
				else {
					allready_time.setVisible(false);
				}
			}
			else {
				next_state_inf = new String[2];
				next_state_inf[0] = name;
				next_state_inf[1] = room_name;
				changeState(new GameState());
			}
		}
		else if(ParsingUtils.requestCheck(start, return_message, "rr")) {
			isready = !isready;
			if(!isready) {
				Label allready_time = (Label)stage.getRoot().findActor("allreadytime");
				allready_time.setVisible(false);
			}
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
