package com.GameObjects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.physics.box2d.Body;

public class Bomb {
	private PlayerWorld playerworld;
	
	public void setPlayer(PlayerWorld playerworld) {
		this.playerworld = playerworld;
	}
	
	public PlayerWorld getPlayerWorld() {
		return playerworld;
	}
}
