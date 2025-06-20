package com.game_states;

import com.game_states.mock_states.FirstStateMock;

public class GameStateManager {
	public State st;
	public State next_st;
	public GameStateManager() {
		st = new FirstStateMock();
		st.gsm = this;
		st.create();
	}

	public void render() {
		if(next_st==null) {
			st.render();
		}
		else {
			st.disposeHalf();
			next_st.create(st);
			st = next_st;
			next_st = null;
		}
	}
	public void dispose() {
		st.dispose();
	}

	public void resize(int width,int height) {
		st.resize(width, height);
	}
}
