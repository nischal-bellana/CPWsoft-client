package com.game_states;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.game_states.stagecreation.LobbyStage;
import com.utils.ParsingUtils;

public class LobbyState extends State {
	public String name;
	public ButtonGroup<Button> bgrp;

	@Override
	public void create(State prevst) {
		// TODO Auto-generated method stub
		super.create(prevst);

		name = prevst.next_state_inf[0];

		createStage();
	}

	@Override
	public void createStage() {
		// TODO Auto-generated method stub
		Table table = new Table();
		table.setBackground(skin.getDrawable("back"));
		initStage(table);
		table.top().left();
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

        LobbyStage.createTopBar(table, this);

		LobbyStage.createRoomsDataTable(table, this);

        LobbyStage.createBottomBar(table, this);
	}

    public void goBack(){
        if(serverbridge != null) serverbridge.closeSocket();
        changeState(new FirstState());
    }

    public void searchRoom(){
        appendRequest("lr");
    }

    public void joinRoom(){
        if(bgrp.getChecked() == null) return;

        String roomdata = bgrp.getChecked().getName();
        String[] dataarray = roomdata.split("&");
        int no = Integer.parseInt(dataarray[1]);
        if(no>=5) return;

        appendRequest("lj" + dataarray[0]);
    }

    public void createRoom(){
        appendRequest("lc");
    }

	public void refreshRooms(int start, int end, String return_message) {
        LobbyStage.resetScrollTable(this);

		if(end - start == 0) return;

		for(int i = start; i < end;) {

			int start1 = ParsingUtils.getBeginIndex(i, return_message, '&');
			int end1 = start1 + ParsingUtils.parseInt(i, start1 - 1 , return_message);

            int start2 = ParsingUtils.getBeginIndex(start1, return_message, '&');
            String room_name = return_message.substring(start1, start2 - 1);
            int fill_count = ParsingUtils.parseInt(start2, end1, return_message);

            addRoomData(room_name, fill_count);

			i = end1;
		}

	}

	@Override
	public void handleResponse(int start, int end, String return_message) {
		// TODO Auto-generated method stub

		if(ParsingUtils.requestCheck(start, return_message, "co") && return_message.charAt(start + 2) == 'p') {
			Label online_count_l = stage.getRoot().findActor("onlinecount");
			online_count_l.setText(ParsingUtils.parseInt(start + 3, end, return_message));
		}
		else if(ParsingUtils.requestCheck(start, return_message, "lr") && return_message.charAt(start + 2) == 'p') {
			refreshRooms(start + 3, end, return_message);
		}
		else if(ParsingUtils.requestCheck(start, return_message, "lj") && return_message.charAt(start + 2) == 'p') {
			String room_name = return_message.substring(start + 3, end);
            changeToRoomState(room_name);
		}
		else if(ParsingUtils.requestCheck(start, return_message, "lc") && return_message.charAt(start + 2) == 'p') {
			String room_name = return_message.substring(start + 3, end);
			changeToRoomState(room_name);
		}
	}

    public void changeToRoomState(String room_name){
        next_state_inf = new String[2];
        next_state_inf[0] = name;
        next_state_inf[1] = room_name;
        changeState(new RoomState());
    }

	@Override
	public void poll() {
		// TODO Auto-generated method stub
		appendRequest("co");

		appendRequest("lr");
	}

    public void addRoomData(String room_name, int fill_count) {
        Table scrollable_t = stage.getRoot().findActor("scrollabletable");
        Button newbutton = new Button(skin, "Empty");
        newbutton.setName(room_name + "&" + fill_count);

        Label room_name_l = new Label(room_name, skin);
        Label fill_count_l = new Label(fill_count + "/5", skin);

        newbutton.add(room_name_l).expandX().left();
        newbutton.add(fill_count_l).expandX().right();

        scrollable_t.add(newbutton).height(40).expandX().width(550);
        scrollable_t.row();

        bgrp.add(newbutton);
    }

}
