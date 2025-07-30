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
        String message_str = getMessageFromChatInput();

        if(message_str.equals("")) return false;

        addOthersMessage("other guy", message_str);

        resetChatInput();

        return true;
    }

    @Override
    public void postRenderUpdate() {

    }

    @Override
    public void appendRequest(String request) {

    }
}
