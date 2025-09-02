package com.game_states.stagecreation;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
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
                state.goBack();
            }

        });
        topbar_t.add(back_b).left().width(50).height(50);

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
        table.add(scrollpane_right_sp).height(300).width(200).padRight(20).expandX().right().colspan(2);

        table.row();
    }

    public static void createLowerComps(Table table, RoomState state){
        createChatInput(table, state);

        Button send_b = new Button(state.skin, "rightbutton");
        send_b.addListener(new ChangeListener() {

            @Override
            public void changed(ChangeEvent event, Actor actor) {
                // TODO Auto-generated method stub
                state.sendMessage();
            }

        });
        table.add(send_b).left().top().height(40).width(40).expandX().padTop(10);

        TextButton ready_tb = new TextButton("Ready", state.skin);
        ready_tb.addListener(new ChangeListener() {

            @Override
            public void changed(ChangeEvent event, Actor actor) {
                // TODO Auto-generated method stub
                state.readyForBattle();
            }

        });
        ready_tb.setName("readybutton");
        table.add(ready_tb).height(50).width(100).right().top();

        Label allready_time_l = new Label("30", state.skin);
        allready_time_l.setVisible(false);
        allready_time_l.setName("allreadytime");
        table.add(allready_time_l).height(50).width(50).padRight(20).top().left();
    }

    public static void createChatInput(Table table, RoomState state){
        VerticalGroup chatinput_vg = new VerticalGroup();
        TextField chatinput_b_tf = new TextField("", state.skin);
        chatinput_b_tf.setName("chatinput_b_tf");

        Container<TextField> chatinput_b_tf_c = new Container<>(chatinput_b_tf);
        chatinput_vg.setName("chatinput_vg");
        chatinput_b_tf_c.height(20).width(400);

        chatinput_b_tf.addListener(new InputListener(){
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                event.cancel();
                if(keycode == Input.Keys.ENTER){
                    if(Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT)){
                        createNewLineChatInput(1, chatinput_vg, state);
                        return true;
                    }

                    if(chatinput_b_tf.getText().equals("")){
                        state.stage.setKeyboardFocus(table);
                        return true;
                    }
                    state.sendMessage();

                    return true;
                }

                if(keycode == Input.Keys.DOWN){
                    if(chatinput_vg.getChildren().size <= 1) return true;

                    Container<TextField> newfocused_c = (Container<TextField>)chatinput_vg.getChild(1);
                    TextField newfocused_tf = newfocused_c.getActor();
                    state.stage.setKeyboardFocus(newfocused_tf);
                    newfocused_tf.setCursorPosition(chatinput_b_tf.getCursorPosition());

                    return true;
                }

                return false;
            }
        });

        chatinput_vg.addActor(chatinput_b_tf_c);
        table.add(chatinput_vg).padLeft(20).left().padTop(20).top();

        table.addListener(new InputListener(){
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                if(keycode == Input.Keys.ENTER){
                    state.stage.setKeyboardFocus(chatinput_b_tf);
                    return true;
                }
                return false;
            }
        });
    }

    public static void createNewLineChatInput(int index, VerticalGroup chatinput_vg, RoomState state){
        TextField chatinput_x_tf = new TextField("", state.skin);
        state.stage.setKeyboardFocus(chatinput_x_tf);
        Container<TextField> chatinput_x_tf_c = new Container<>(chatinput_x_tf);
        chatinput_x_tf_c.height(20);
        chatinput_x_tf_c.width(400);

        chatinput_x_tf.addListener(new InputListener(){

            String prevstr = "";

            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                event.cancel();
                if(keycode == Input.Keys.ENTER){
                    if(Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT)){
                        int insert_index = chatinput_vg.getChildren().indexOf(chatinput_x_tf_c, true) + 1;
                        createNewLineChatInput(insert_index, chatinput_vg, state);
                        return true;
                    }

                    state.sendMessage();
                    state.stage.setKeyboardFocus(chatinput_vg.getChild(0));

                    return true;
                }

                if(keycode == Input.Keys.DOWN){
                    int newfocus_index = chatinput_vg.getChildren().indexOf(chatinput_x_tf_c, true) + 1;
                    if(newfocus_index >= chatinput_vg.getChildren().size) return true;

                    Container<TextField> newfocused_c = (Container<TextField>)chatinput_vg.getChild(newfocus_index);
                    TextField newfocused_tf = newfocused_c.getActor();
                    state.stage.setKeyboardFocus(newfocused_tf);
                    newfocused_tf.setCursorPosition(chatinput_x_tf.getCursorPosition());

                    return true;
                }

                if(keycode == Input.Keys.UP){
                    int newfocus_index = chatinput_vg.getChildren().indexOf(chatinput_x_tf_c, true) - 1;
                    if(newfocus_index < 0) return true;

                    Container<TextField> newfocused_c = (Container<TextField>)chatinput_vg.getChild(newfocus_index);
                    TextField newfocused_tf = newfocused_c.getActor();
                    state.stage.setKeyboardFocus(newfocused_tf);
                    newfocused_tf.setCursorPosition(chatinput_x_tf.getCursorPosition());
                }

                return false;
            }

            @Override
            public boolean keyTyped(InputEvent event, char character) {
                if (character == '\b' && prevstr.equals("")) {
                    int remove_index = chatinput_vg.getChildren().indexOf(chatinput_x_tf_c, true);
                    chatinput_vg.removeActorAt(remove_index, true);

                    Container<TextField> newfocused_c = (Container<TextField>)chatinput_vg.getChild(remove_index - 1);
                    TextField newfocused_tf = newfocused_c.getActor();
                    state.stage.setKeyboardFocus(newfocused_tf);
                    newfocused_tf.setCursorPosition(newfocused_tf.getText().length());

                    return true;
                }

                prevstr = chatinput_x_tf.getText();

                return false;
            }
        });

        chatinput_vg.addActorAt(index, chatinput_x_tf_c);
    }
}
