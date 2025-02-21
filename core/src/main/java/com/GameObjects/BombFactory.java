package com.GameObjects;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;

public class BombFactory {
private AtlasRegion bombregion;
	
	public BombFactory(TextureAtlas atlas) {
		bombregion = atlas.findRegion("bomb");
	}
	
	public Bomb generateBomb() {
		
		Bomb bomb = new Bomb(bombregion);
		
		return bomb;
	}
	
}
