package com.game_states.mock_states;

import com.game_states.LobbyState;
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
        System.out.println("Joining...");
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
