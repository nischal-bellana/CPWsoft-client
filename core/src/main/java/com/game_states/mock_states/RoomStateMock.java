package com.game_states.mock_states;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.game_states.LobbyState;
import com.game_states.RoomState;

public class RoomStateMock extends RoomState {

    @Override
    public void goBack() {
        changeToLobbyState();
    }

    @Override
    public void changeToLobbyState() {
        next_state_inf = new String[1];
        next_state_inf[0] = name;
        changeState(new LobbyStateMock());
    }

    @Override
    public void readyForBattle() {
        toggleReady();
    }

    @Override
    public boolean sendMessage() {
        StringBuilder message = new StringBuilder();

        VerticalGroup chatinput_vg = stage.getRoot().findActor("chatinput_vg");

        for(Actor a: chatinput_vg.getChildren()){
            Container<TextField> chatinput_tf_c = (Container<TextField>) a;
            String line = chatinput_tf_c.getActor().getText();

            if(line.length() > 26){
                int i = 0;
                while(line.length() - i > 26){
                    message.append(line.substring(i, i + 26));
                    message.append("-\n-");
                    i += 26;
                }
                message.append(line.substring(i, line.length()));
                message.append('\n');
                continue;
            }

            message.append(line);
            message.append('\n');
        }
        message.deleteCharAt(message.length()-1);

        String message_str = message.toString().strip();

        if(message_str.equals("")) return false;

        addMyMessage(message_str);

        while(chatinput_vg.getChildren().size > 1){
            chatinput_vg.removeActorAt(1, false);
        }
        ((Container<TextField>) chatinput_vg.getChild(0)).getActor().setText("");

        return true;
    }

    @Override
    public void postRenderUpdate() {

    }

    @Override
    public void appendRequest(String request) {

    }
}
