package com.GameObjects;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;

public class Bomb {
	private Sprite sprite;
	private boolean alive = true;
	
	public Bomb(AtlasRegion region) {
		sprite = new Sprite(region);
		sprite.setBounds(0, 0, 0.4f, 0.4f);
		sprite.setOrigin(0.2f , 0.2f);
	}
	
	public void draw(SpriteBatch batch) {
		if(alive) {
			sprite.draw(batch);
		}
	}
	
	public Sprite getSprite() {
		return sprite;
	}
	
	public void setCenter(float x, float y) {
		sprite.setCenter(x, y);
	}
	
	public void setRotation(float angle) {
		sprite.setRotation(angle);
	}
	
	public void setAlive(boolean value) {
		alive = value;
	}
	
	public boolean isAlive() {
		return alive;
	}
	
}
