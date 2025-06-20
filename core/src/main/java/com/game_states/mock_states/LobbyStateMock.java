package com.game_states.mock_states;

import com.game_states.LobbyState;

public class LobbyStateMock extends LobbyState {
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
        System.out.println("Creating Room...");
    }

    @Override
    public void postRenderUpdate() {

    }
}
