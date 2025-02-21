package com.GameObjects;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.game_states.GameState;

public class PlayerWorld {
	private Body body;
	private Player player;
	private int contacts = 0;
	
	public PlayerWorld(Body body, Player player) {
		this.body = body;
		this.player = player;
	}
	
	public void destroy() {
		body.getWorld().destroyBody(body);
	}
	
	public Body getBody() {
		return body;
	}
	
	public BombContext getBombContext() {
		BombContext context = new BombContext();
		
		context.setAngle(body.getAngle());
		
		Vector2 position = new Vector2(0.5f, 0);
		position.setAngleRad(context.getAngle());
		position.add(body.getPosition());
		context.setPosition(position);
		
		context.setPlayerworld(this);
		
		context.setWorld(body.getWorld());
		
		Vector2 velocity = new Vector2(player.getPowerLevel()/6f, 0);
		velocity.setAngleRad(context.getAngle());
		context.setVelocity(velocity);
		
		return context;
	}
	
	public void updateContacts(boolean begin) {
		contacts += begin ? 1 : -1;
	}
	
	public boolean inContact() {
		return contacts != 0;
	}
	
}
