package com.GameObjects;

import java.util.HashSet;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.utils.PointNode;
import com.utils.WorldUtils;

public class Clip {
	private Vector2 position;
	private PlayerWorld playerworld;
	private HashSet<Fixture> processed;
	
	public Clip(PlayerWorld playerworld, float x, float y) {
		position = new Vector2(x, y);
		this.playerworld = playerworld;
	}
	
	public PlayerWorld getPlayerWorld() {
		return playerworld;
	}
	
	public void setPosition(float x, float y) {
		position.set(x, y);
	}
	
	public Vector2 getPosition() {
		return position;
	}
	
	public Body create() {
		BodyDef bdef = new BodyDef();
		bdef.position.set(position);
		bdef.fixedRotation = true;
		bdef.type = BodyType.DynamicBody;
		bdef.linearDamping = 10;
		
		Body body = playerworld.getBody().getWorld().createBody(bdef);
		body.setUserData(this);
		
		FixtureDef fdef = WorldUtils.createFixDef(0.001f, 0.1f, 0.1f);
		CircleShape shape = new CircleShape();
		shape.setRadius(2f);
		fdef.shape = shape;
		fdef.isSensor = true;
		
		body.createFixture(fdef).setUserData(this);
		shape.dispose();
		
		processed = new HashSet<>();
		
		return body;
	}
	
	public boolean processedFixture(Fixture f) {
		return processed.contains(f);
	}
	
	public void addToProcessed(Fixture f) {
		processed.add(f);
	}
	
}
