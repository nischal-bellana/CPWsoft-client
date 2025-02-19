package com.GameObjects;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.utils.Array;
import com.utils.Clipping;
import com.utils.Decomp;
import com.utils.PointNode;

public class myContactListener implements ContactListener {

	@Override
	public void beginContact(Contact contact) {
		// TODO Auto-generated method stub
		Fixture fixtureA = contact.getFixtureA();
		Fixture fixtureB = contact.getFixtureB();
		
		resolveFixtures(fixtureA, fixtureB);
	}

	@Override
	public void endContact(Contact contact) {
		// TODO Auto-generated method stub

	}

	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {
		// TODO Auto-generated method stub

	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {
		// TODO Auto-generated method stub

	}
	
	private void resolveFixtures(Fixture fixtureA, Fixture fixtureB) {
		tryBomb(fixtureA, fixtureB);
		
		tryClip(fixtureA, fixtureB);
		
		if(tryPlayerAndGroundWorld(fixtureA, fixtureB)) return;
		
		if(tryClipAndGroundWorld(fixtureA, fixtureB)) return;
	}
	
	private boolean tryPlayerAndGroundWorld(Fixture fixtureA, Fixture fixtureB) {
		if(!isPlayer(fixtureA) && !isPlayer(fixtureB)) return false;
		if(!isGroundWorld(fixtureA) && !isGroundWorld(fixtureB)) return false;
		
		return true;
	}
	
	private boolean tryClipAndGroundWorld(Fixture fixtureA, Fixture fixtureB) {
		if(!isClip(fixtureA) && !isClip(fixtureB)) return false;
		if(!isGroundWorld(fixtureA) && !isGroundWorld(fixtureB)) return false;
		
		if(isClip(fixtureA)) resolveClipAndGroundWorld((Clip) fixtureA.getUserData(), (GroundWorld) fixtureB.getUserData(), fixtureB);
		else resolveClipAndGroundWorld((Clip) fixtureB.getUserData(), (GroundWorld) fixtureA.getUserData(), fixtureA);
		
		return true;
	}
	
	private boolean tryBomb(Fixture fixtureA, Fixture fixtureB) {
		if(!isBomb(fixtureA) && !isBomb(fixtureB)) return false;
		
		if(isBomb(fixtureA)) resolveBomb((Bomb) fixtureA.getUserData());
		else resolveBomb((Bomb) fixtureB.getUserData());
		
		return true;
	}
	
	private boolean tryClip(Fixture fixtureA, Fixture fixtureB) {
		if(!isClip(fixtureA) && !isClip(fixtureB)) return false;
		
		if(isClip(fixtureA)) resolveClip((Clip) fixtureA.getUserData());
		else resolveClip((Clip) fixtureB.getUserData());
		
		return true;
	}
	
	private void resolveClipAndGroundWorld(Clip clip, GroundWorld groundworld, Fixture groundworldFixture) {
		if(clip.processedFixture(groundworldFixture)) return;
		clip.addToProcessed(groundworldFixture);
		
		Vector2 vec = new Vector2(clip.getPosition());
		vec.sub(groundworld.getPosition());
		
		PointNode clipnode = Clipping.getClipPointNode(vec);
		
		PointNode groundworldnode = Clipping.getGroundPointNode(groundworldFixture);
		
		PointNode res = Clipping.clip(groundworldnode, clipnode);
		Clipping.resetClip(clipnode);
		
		if(res == groundworldnode) return;
		
		groundworld.queueDestroy(groundworldFixture);
		
		if(res == null) return;
		
		Array<PointNode> finalFixtures = new Array<>();
		PointNode curPoly = res;
		System.out.println("decomposing ...");
		while(curPoly != null) {
			PointNode nextPoly = curPoly.nextPoly;
			curPoly.nextPoly = null;
			curPoly.prevPoly = null;
			Decomp.decomp(curPoly, finalFixtures);
			curPoly = nextPoly;
		}
		
		groundworld.queueCreate(finalFixtures);
		
		System.out.println("Complete");
		
	}
	
	private void resolveBomb(Bomb bomb) {
		PlayerWorld playerworld = bomb.getPlayerWorld();
		playerworld.requestDestroyBomb();
	}
	
	private void resolveClip(Clip clip) {
		PlayerWorld playerworld = clip.getPlayerWorld();
		playerworld.requestDestroyClip();
	}
	
	private boolean isClip(Fixture fixture) {
		return (fixture.getUserData() instanceof Clip);
	}
	
	private boolean isPlayer(Fixture fixture) {
		return (fixture.getUserData() instanceof PlayerWorld);
	}
	
	private boolean isGroundWorld(Fixture fixture) {
		return (fixture.getUserData() instanceof GroundWorld);
	}
	
	private boolean isBomb(Fixture fixture) {
		return (fixture.getUserData() instanceof Bomb);
	}
	
}
