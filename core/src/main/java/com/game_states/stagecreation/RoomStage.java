package com.game_states.stagecreation;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.game_states.RoomState;

public class RoomStage {
    public static void createTopBar(Table table, RoomState state){
        Table topbar_t = new Table();
        table.add(topbar_t).colspan(2).left();

        Button back_b = new Button(state.skin, "backbutton");
        back_b.addListener(new ChangeListener() {

            @Override
            public void changed(ChangeEvent event, Actor actor) {
                // TODO Auto-generated method stub
                state.appendRequest("rb");
            }

        });
        topbar_t.add(back_b).left();

        Label l_nopr = new Label("No of players in the Room: ", state.skin);
        topbar_t.add(l_nopr).padLeft(300);

        Label online_count_l = new Label("1", state.skin);
        online_count_l.setName("onlinecount");
        online_count_l.setColor(0, 1, 0, 1);
        topbar_t.add(online_count_l);

        Label room_name_l = new Label("Room Name: " + state.room_name, state.skin);
        topbar_t.add(room_name_l).padLeft(10);
    }

    public static void createChatArea(Table table, RoomState state){
        Table chatarea_t = new Table();
        chatarea_t.top().left();
        chatarea_t.setName("chatarea");

        ScrollPane scrollpane_left_sp = new ScrollPane(chatarea_t, state.skin);
        table.row();
        table.add(scrollpane_left_sp).width(400).left().height(300).padLeft(20).expandX().colspan(2);
    }

    public static void createUsersList(Table table, RoomState state){
        Table userslist_t = new Table();
        userslist_t.top();
        userslist_t.setDebug(false);
        userslist_t.setName("userslist");

        ScrollPane scrollpane_right_sp = new ScrollPane(userslist_t, state.skin);
        table.add(scrollpane_right_sp).height(300).width(200).padRight(20).expandX().right();

        table.row();
    }

    public static void createLowerComps(Table table, RoomState state){
        TextField chatfield_tf = new TextField("", state.skin);
        chatfield_tf.setName("chatfield_tf");
        table.add(chatfield_tf).fillX().padLeft(20).height(30);

        TextButton send_tb = new TextButton("Send", state.skin);
        send_tb.addListener(new ChangeListener() {

            @Override
            public void changed(ChangeEvent event, Actor actor) {
                // TODO Auto-generated method stub
                state.sendMessage();
            }

        });
        table.add(send_tb).left().height(50).width(100);

        table.row();
        table.add();
        table.add();
        TextButton ready_tb = new TextButton("Ready", state.skin);
        ready_tb.addListener(new ChangeListener() {

            @Override
            public void changed(ChangeEvent event, Actor actor) {
                // TODO Auto-generated method stub
                state.appendRequest("rr");
            }

        });
        ready_tb.setName("readybutton");
        table.add(ready_tb).height(50).width(100).right();

        Label allready_time_l = new Label("30", state.skin);
        allready_time_l.setVisible(false);
        allready_time_l.setName("allreadytime");
        table.row();
        table.add(allready_time_l).right().colspan(3).height(50).width(50);
    }

}
