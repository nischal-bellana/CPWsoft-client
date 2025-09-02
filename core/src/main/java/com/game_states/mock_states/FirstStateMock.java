package com.game_states.mock_states;

import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.game_states.FirstState;

public class FirstStateMock extends FirstState{
    @Override
    public void connectServer() {
        System.out.println("Connecting...");
        TextField name_tf = stage.getRoot().findActor("name_tf");
        String name = name_tf.getText().trim();
        if(!validateUsername(name)) return;

        next_state_inf = new String[1];
        next_state_inf[0] = name;
        changeState(new LobbyStateMock());
    }

}
