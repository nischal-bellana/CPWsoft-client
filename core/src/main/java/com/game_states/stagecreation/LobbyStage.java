package com.game_states.stagecreation;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.game_states.LobbyState;

public class LobbyStage {
    public static void createTopBar(Table table, LobbyState state){
        Table topbar_t = new Table();

        Button back_b = new Button(state.skin, "backbutton");
        back_b.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                // TODO Auto-generated method stub
                state.goBack();
            }
        });
        topbar_t.add(back_b).width(50).height(50);

        Label nop = new Label("No of players online: ", state.skin);
        topbar_t.add(nop).padLeft(300);

        Label online_count_l = new Label("1", state.skin);
        online_count_l.setName("onlinecount");
        online_count_l.setColor(0, 1, 0, 1);
        topbar_t.add(online_count_l).left();

        Label l_username = new Label("Username: " + state.name, state.skin);
        topbar_t.add(l_username).padLeft(10);

        table.add(topbar_t).expandX().left();
        table.row();
    }

    public static void createRoomsDataTable(Table table, LobbyState state){
        Table scrollable_t = new Table();
        scrollable_t.top().left();
        scrollable_t.setName("scrollabletable");
        state.bgrp = new ButtonGroup<>();
        state.bgrp.setMinCheckCount(0);
        ScrollPane scrollpane_sp = new ScrollPane(scrollable_t, state.skin);
        table.add(scrollpane_sp).padTop(50).height(200).width(600).padBottom(50);
        table.row();
        Table headrow_t = new Table();
        scrollable_t.add(headrow_t).expandX().height(50).width(550);
        Label l_room_name = new Label("Room Name", state.skin);
        headrow_t.add(l_room_name).expandX().left();
        Label l_occupied = new Label("Occupied", state.skin);
        headrow_t.add(l_occupied).expandX().right();
        scrollable_t.row();
    }

    public static void createBottomBar(Table table, LobbyState state){
        Table bottombar_t = new Table();

        TextButton room_search_tb = new TextButton("Search By RoomID:", state.skin);
        bottombar_t.add(room_search_tb).height(40).width(200);

        TextField room_filter_tf = new TextField("", state.skin);
        bottombar_t.add(room_filter_tf).width(200).height(20).colspan(2);
        bottombar_t.row();

        room_search_tb.addListener(new ChangeListener() {

            @Override
            public void changed(ChangeEvent event, Actor actor) {
                // TODO Auto-generated method stub
                state.searchRoom();
            }

        });

        TextButton room_join_tb = new TextButton("Join", state.skin);
        room_join_tb.addListener(new ChangeListener() {

            @Override
            public void changed(ChangeEvent event, Actor actor) {
                // TODO Auto-generated method stub
                state.joinRoom();
            }

        });
        bottombar_t.add(room_join_tb).height(40);

        TextButton room_create_tb = new TextButton("Create", state.skin);
        room_create_tb.addListener(new ChangeListener() {

            @Override
            public void changed(ChangeEvent event, Actor actor) {
                // TODO Auto-generated method stub
                state.createRoom();
            }

        });
        bottombar_t.add(room_create_tb).height(40);

        table.add(bottombar_t);
    }

    public static void resetScrollTable(LobbyState state){
        Table scrollable_t = state.stage.getRoot().findActor("scrollabletable");
        scrollable_t.clearChildren();

        Table headrow_t = new Table();
        scrollable_t.add(headrow_t).expandX().height(50).width(550);
        Label l_room_name = new Label("Room Name", state.skin);
        headrow_t.add(l_room_name).expandX().left();
        Label l_occupied = new Label("Occupied", state.skin);
        headrow_t.add(l_occupied).expandX().right();
        scrollable_t.row();

        state.bgrp.clear();
        state.bgrp.setMinCheckCount(0);
    }

}
