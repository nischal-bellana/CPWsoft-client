package com.game_states.mock_states;

import com.game_states.LobbyState;
import com.game_states.RoomState;
import com.game_states.State;

public class LobbyStateMock extends LobbyState {

    @Override
    public void create(State prevst) {
        super.create(prevst);

        addRoomData("fusdjn", 3);
        addRoomData("fusdjn", 3);
        addRoomData("fusdjn", 3);
        addRoomData("fusdjn", 3);
        addRoomData("fusdjn", 3);
    }

    @Override
    public void joinRoom() {
        if(bgrp.getChecked() == null) return;
        System.out.println("Joining...");
        next_state_inf = new String[2];
        next_state_inf[0] = name;
        next_state_inf[1] = "fusdjn";
        changeState(new RoomStateMock());
    }

    @Override
    public void searchRoom() {
        System.out.println("Searching...");
    }

    @Override
    public void createRoom() {
        System.out.println("Creating...");
    }

    @Override
    public void postRenderUpdate() {

    }
}
