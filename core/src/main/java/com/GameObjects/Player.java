package com.GameObjects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar.ProgressBarStyle;
import com.badlogic.gdx.utils.Queue;
import com.badlogic.gdx.utils.viewport.Viewport;

public class Player {
	private Sprite sprite;
	private Viewport viewport;
	private AtlasRegion[] regions;
	private int regionindex = 0;
	private String name;
	private Label namelabel;
	private Table stats;
	private int health = 100;
	private int score = 0;
	private Queue<Integer> damages;
	private Queue<Integer> scorepoints;
	
	
	private AtlasRegion[] powerindicators;
	private Sprite powersprite;
	private int powerindicatorLevel = -1;
	
	public Player(Stage stage, Skin skin, String name, Viewport viewport, TextureAtlas atlas) {
		this.viewport = viewport;
		this.name = name;
		namelabel = new Label(name, skin, "playernamestyle");
		stage.getRoot().addActor(namelabel);
		
		stats = new Table();
		HorizontalGroup bottompane = stage.getRoot().findActor("bottom pane");
		bottompane.addActor(stats);
		
		Label label = new Label(name, skin, "head");
		stats.add(label).width(150).height(20);
		
		ProgressBar healthbar = new ProgressBar(0,101,1, false, skin);
		healthbar.setAnimateDuration(1);
		healthbar.setAnimateInterpolation(Interpolation.linear);
		healthbar.setValue(100);
		healthbar.setName("healthbar");
		stats.row();
		stats.add(healthbar).height(30);
		
		Label healthlabel = new Label(health+"", skin);
		healthlabel.setName("healthlabel");
		healthlabel.setColor(Color.GREEN);
		stats.add(healthlabel);
		
		Label score = new Label("0", skin, "head");
		score.setName("score");
		stats.row();
		stats.add(score).height(20);
		
		damages = new Queue<>();
		scorepoints = new Queue<>();
		
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
		sprite.setBounds(0, 0, 0.4f, 0.4f);
		
		powersprite = new Sprite(powerindicators[0]);
		powersprite.setBounds(0, 0, 1, 1);
		powersprite.setOrigin(-0.25f, powersprite.getHeight()/2);
		
	}
	
	public void drawSprites(SpriteBatch batch) {
		sprite.draw(batch);
		if(powerindicatorLevel != -1) {
			powersprite.draw(batch);
		}
	}
	
	public void addDamage(int amount) {
		int min = Math.min(amount, health);
		damages.addLast(min);
	}
	
	public int pollDamage() {
		if(damages.isEmpty()) return -1;
		
		return damages.removeFirst();
		
	}
	
	public void addScorePoints(int amount) {
		scorepoints.addLast(amount);
	}
	
	public int pollScorePoint() {
		if(scorepoints.isEmpty()) return -1;
		
		return scorepoints.removeFirst();
		
	}
	
	public void damageBy(int amount) {
		health -= amount;
		ProgressBar healthbar = stats.findActor("healthbar");
		healthbar.setValue(health);
		Label healthlabel = stats.findActor("healthlabel");
		healthlabel.setText(health);
	}
	
	public void scoreBy(int amount) {
		score += amount;
		Label scorelabel = stats.findActor("score");
		scorelabel.setText(score);
	}
	
	public int getScore() {
		return score;
	}
	
	public String getName() {
		return name;
	}
	
	public Sprite getSprite() {
		return sprite;
	}
	
	public Sprite getPowerSprite() {
		return powersprite;
	}
	
	public Label getNameLabel() {
		return namelabel;
	}
	
	public void centerSpriteToHere(float x, float y) {
		sprite.setCenter(x, y);
		namelabel.setPosition(sprite.getX()*32f, (sprite.getY() - 0.3f)*32f);
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
	
}
