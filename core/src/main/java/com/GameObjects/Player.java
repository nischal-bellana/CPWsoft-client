package com.GameObjects;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.utils.viewport.Viewport;

public class Player {
	private Sprite sprite;
	private Viewport viewport;
	private AtlasRegion[] regions;
	private int regionindex = 0;
	
	private AtlasRegion[] powerindicators;
	private Sprite powersprite;
	private int powerindicatorLevel = -1;
	
	private Sprite bombsprite;
	private boolean bombalive = false;
	
	public Player(Viewport viewport, TextureAtlas atlas) {
		this.viewport = viewport;
		
		regions = new AtlasRegion[7];
		powerindicators = new AtlasRegion[10];
		
		for(int i = 1; i < 11; i++) {
			powerindicators[i-1] = atlas.findRegion("powerindicator" + i);
		}
		
		regions[0] = atlas.findRegion("playerbase");
		for(int i = 1; i < 4; i++) {
			regions[i] = atlas.findRegion("playerleft" + i);
		}
		
		for(int i = 1; i < 4; i++) {
			regions[i + 3] = atlas.findRegion("playerright" + i);
		}
		
		sprite = new Sprite(regions[0]);
		sprite.setBounds(0, 0, 0.8f, 0.8f);
		
		powersprite = new Sprite(powerindicators[0]);
		powersprite.setBounds(0, 0, 2, 2);
		powersprite.setOrigin(-0.5f, powersprite.getHeight()/2);
		
		bombsprite = new Sprite(atlas.findRegion("bomb"));
		bombsprite.setBounds(0, 0, 0.8f, 0.8f);
		bombsprite.setOrigin(0.4f , 0.4f);
		
	}
	
	public void drawSprites(SpriteBatch batch) {
		sprite.draw(batch);
		if(powerindicatorLevel != -1) {
			powersprite.draw(batch);
		}
		if(bombalive) {
			bombsprite.draw(batch);
		}
	}
	
	public Sprite getSprite() {
		return sprite;
	}
	
	public Sprite getBombSprite() {
		return bombsprite;
	}
	
	public Sprite getPowerSprite() {
		return powersprite;
	}
	
	public void centerSpriteToHere(float x, float y) {
		sprite.setCenter(x, y);
	}
	
	public void updatePowerIndicator() {
		powersprite.setPosition(sprite.getX() + (sprite.getWidth()/2) - powersprite.getOriginX(), sprite.getY() + (sprite.getWidth()/2) - powersprite.getOriginY());
		powersprite.setRegion(powerindicators[powerindicatorLevel/10]);
	}
	
	public void setPowerLevel(int x) {
		powerindicatorLevel = x % 100;
	}
	
	public void incrementPowerLevel() {
		if(powerindicatorLevel < 99) {
			powerindicatorLevel++;
		}
	}
	
	public int getPowerLevel() {
		return powerindicatorLevel;
	}
	
	public void setBombAlive(boolean value) {
		bombalive = value;
	}
	
	public boolean isBombAlive() {
		return bombalive;
	}
	
}
