package com.GameObjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Queue;
import com.utils.PointNode;
import com.utils.WorldUtils;

public class Ground{
	private Sprite sprite;
	private boolean updatebuffermask = false;
	private ShapeRenderer shapeRenderer;
	private Camera camera;
	private Array<float[]> fixtures;

	public Ground(TextureAtlas atlas, Camera camera) {
		this.camera = camera;
		fixtures = new Array<>();
		AtlasRegion region = atlas.findRegion("grnd");
		sprite = new Sprite(region);
		sprite.setBounds(-2, 3, region.getRegionWidth()/(32f*3f), region.getRegionHeight()/(32f*3f));
		shapeRenderer = new ShapeRenderer();
	}
	
	public void draw(SpriteBatch batch) {
		// TODO Auto-generated method stub
		Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
		
	    Gdx.gl.glColorMask(true, true, true, true);
	    Gdx.gl.glDepthFunc(GL20.GL_EQUAL);
		
	    batch.begin();
		sprite.draw(batch);
		batch.end();
		
		Gdx.gl.glDisable(GL20.GL_DEPTH_TEST);
	}
	
	private void createMask() {
		/* Clear our depth buffer info from previous frame. */
		System.out.println("creating Mask...");
		Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
	    Gdx.gl.glClear(GL20.GL_DEPTH_BUFFER_BIT);

	    /* Set the depth function to LESS. */
	    Gdx.gl.glDepthFunc(GL20.GL_LESS);

	    /* Disable RGBA color writing. */
	    Gdx.gl.glColorMask(false, false, false,false);
	    
	    /* Render mask elements. */
	    shapeRenderer.setProjectionMatrix(camera.combined.scl(32));
	    shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
	    
	    float x = sprite.getX();
	    float y = sprite.getY();
	    // Draw the mask (e.g., a circular mask or polygon mask)
	    for(float[] vert : fixtures) {
	        int i = 4;
	        while (i < vert.length) {
	            shapeRenderer.triangle(vert[0] + x, vert[1] + y, vert[i-2] + x, vert[i-1] + y, vert[i] + x, vert[i+1] + y);
	            i += 2;
	        }
	    }
	    
	    shapeRenderer.end();
	    Gdx.gl.glDisable(GL20.GL_DEPTH_TEST);
	    updatebuffermask = false;
	}

	public void addFixture(float[] arr) {
		fixtures.add(arr);
		updatebuffermask = true;
	}
	
	public void removeFixture(int index) {
		fixtures.removeIndex(index);
		updatebuffermask = true;
	}
	
	public void setUpdateDepthBuffer(boolean value) {
		updatebuffermask = value;
	}
	
	public void updateDepthBuffer() {
		if(updatebuffermask) {
			createMask();
		}
	}
	
	public void disposeShapeRenderer() {
		shapeRenderer.dispose();
	}
	
}
