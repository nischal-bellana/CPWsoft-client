package com.GameObjects;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.utils.Queue;
import com.utils.WorldUtils;

public class PlayerWorld {
	private Body body;
	private Player player;
	
	private Body bombbody;
	private boolean requestedDestroyBomb = false;
	private float bombtime = 0;
	
	private Queue<Clip> clips;
	private Body currentClip;
	private boolean requestedDestroyClip = false;
	private float cliptime = 0;
	
	public PlayerWorld(Body body, Player player) {
		this.body = body;
		this.player = player;
		
		clips = new Queue<>();
		
	}
	
	public Body getBody() {
		return body;
	}
	
	public Body getBombBody() {
		return bombbody;
	}
	
	public void LaunchBomb() {
		Bomb bomb = new Bomb();
		bomb.setPlayer(this);
		BodyDef bdef = new BodyDef();
		float angle = player.getPowerSprite().getRotation();
		Vector2 bombposition = new Vector2(1f, 0);
		bombposition.rotateDeg(angle);
		bombposition.add(body.getPosition());
		bdef.position.set(bombposition);
		bdef.angle = (float)Math.toRadians(angle);
		bdef.type = BodyType.DynamicBody;
		bdef.fixedRotation = false;
		bdef.angularDamping = 10;
		Vector2 velocity = new Vector2(player.getPowerLevel()/6f,0);
		velocity.setAngleDeg(player.getPowerSprite().getRotation());
		bdef.linearVelocity.set(velocity);
		
		bombbody = body.getWorld().createBody(bdef);
		bombbody.setUserData(bomb);
		
		FixtureDef fdef = WorldUtils.createFixDef(0.3f, 0.1f, 0.2f);
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(0.4f, 0.2f);
		fdef.shape = shape;
		bombbody.createFixture(fdef).setUserData(bombbody.getUserData());
		shape.dispose();
		
		player.setBombAlive(true);
	}
	
	public void updateBombAndClip(float delta) {
		if(bombbody != null) {
			updateBomb(delta);
		}
		
		if(currentClip != null) updateClip(delta);
	}
	
	private void updateBomb(float delta) {
		if(bombbody == null) return;
		
		if(bombtime > 7 || requestedDestroyBomb) {
			destroyBomb();
			return;
		}
		
		Sprite bombsprite = player.getBombSprite();
		bombsprite.setCenter(bombbody.getPosition().x, bombbody.getPosition().y);
		bombsprite.setRotation((float) Math.toDegrees(bombbody.getAngle()));
		
		angleCorrection();
		
		bombtime += delta;
	}
	
	private void angleCorrection() {
		Vector2 vec = bombbody.getLinearVelocity().cpy();
		vec.scl(1/vec.len());
		Vector2 vec2 = new Vector2(1,0);
		vec2.setAngleRad(bombbody.getAngle());
		bombbody.applyTorque(vec2.crs(vec), true);
	}
	
	private void destroyBomb() {
		int time = 0;
		System.out.println();
		while(bombbody.getWorld().isLocked()) {
			System.out.print("\r" + "World is locked: " + time);
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			time += 0.01;
		}
		
		System.out.println("Bomb is destroyed in " + (bombtime+time) + "secs");
		clips.addLast(new Clip(this, bombbody.getPosition().x, bombbody.getPosition().y));
		bombbody.getWorld().destroyBody(bombbody);
		bombbody = null;
		bombtime = 0;
		requestedDestroyBomb = false;
		player.setBombAlive(false);
	}
	
	private void updateClip(float delta) {
		if(cliptime > 1 || requestedDestroyClip) {
			destroyClip();
		}
		
		cliptime += delta;
	}
	
	public void destroyClip() {
		int time = 0;
		System.out.println();
		while(currentClip.getWorld().isLocked()) {
			System.out.print("\r" + "World is locked: " + time);
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			time += 0.01;
		}
		
		System.out.println("Clip is destroyed in " + (cliptime+time) + "secs");
		currentClip.getWorld().destroyBody(currentClip);
		currentClip = null;
		cliptime = 0;
		requestedDestroyClip = false;
	}
	
	public void pollClip() {
		if(!clips.isEmpty() && currentClip == null) {
			currentClip = clips.removeFirst().create();
		}
	}
	
	public void requestDestroyClip() {
		requestedDestroyClip = true;
	}
	
	public void requestDestroyBomb() {
		requestedDestroyBomb = true;
	}
	
}
