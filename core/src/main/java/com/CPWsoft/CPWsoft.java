package com.CPWsoft;

import com.badlogic.gdx.ApplicationAdapter;
import com.game_states.GameStateManager;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class CPWsoft extends ApplicationAdapter {
	public static GameStateManager gsm;
	@Override
	public void create () {
		gsm = new GameStateManager();
	}
	@Override
	public void render () {
		gsm.render();
	}
	@Override
	public void dispose () {
		gsm.dispose();
	}
	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
		gsm.resize(width, height);
	}
}
