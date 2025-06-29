package com.game_states.mock_states;

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
    public void postRenderUpdate() {

    }

    @Override
    public void appendRequest(String request) {

    }
}
