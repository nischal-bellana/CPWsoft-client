package com.game_states;


import com.GameObjects.Ground;
import com.GameObjects.Player;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2D;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;

import com.ray3k.stripe.scenecomposer.SceneComposerStageBuilder;

public class GameState extends HomeState{
	
	private TextureAtlas atlas;
	
	private Array<Player> players;
	private Player player;
	private boolean isinput = false;
	private Ground ground;
	private StringBuilder inputs;
	
	GameState(State prevst, String name){
		super(prevst, name);
	}
	
	@Override
	protected void create() {
		// TODO Auto-generated method stub
		Box2D.init();
		
		atlas = new TextureAtlas("packimgs//atlaspack.atlas");
		
		initStage();
		createGameObjects();
		getRegions();
		System.out.println("In gamestate ");
	}

	@Override
	protected void render() {
		// TODO Auto-generated method stub
		float delta = Gdx.graphics.getDeltaTime();
		
	    preRender(delta);
		
	    batchRender();
	    
	    stageRender(delta);
	    
	    polling();
	    
	    if(isinput) {
	    	inputUpdate();
	    }
	    
	    ground.updateDepthBuffer();
	}

	@Override
	protected void dispose() {
		// TODO Auto-generated method stub
		super.dispose();
		atlas.dispose();
		ground.disposeShapeRenderer();
	}

	@Override
	protected void resize(int width, int height) {
		// TODO Auto-generated method stub
		mainvp.update(width, height);
		ground.setUpdateDepthBuffer(true);
	}
	
	private void initStage() {
		stage = new Stage(mainvp);
		Gdx.input.setInputProcessor(stage);
		SceneComposerStageBuilder builder = new SceneComposerStageBuilder();
        builder.build(stage, skin, Gdx.files.internal("packimgs//gamestate.json"));
        stage.getRoot().setVisible(false);
	}
	
	private void createGameObjects(){
		createGround();
		createPlayers();
	}
	
	private void createGround() {
//		polygonGen.imageToFixtures("images//atlasimages//grnd.png", "fonts//grnddata.xml");
		ground = new Ground(atlas, camera);
	}
	
	private void createPlayers() {
		players = new Array<>();
		
		String response = sendMsg("gn");
		int size = Integer.parseInt(response.substring(1));
		
		response = sendMsg("gx");
		int index = Integer.parseInt(response.substring(1));
		
		for(int i = 0; i < size; i++) {
			Player player = new Player(mainvp, atlas);
			
			players.add(player);
		}
		
		player = players.get(index);
		
	}
	
	private void getRegions() {
		
	}
	
	private void inputUpdate() {
		inputs = new StringBuilder("gi");
		
		if(Gdx.input.isKeyJustPressed(Input.Keys.W)) {
//			playerbody.setLinearVelocity(0, 10);
			inputs.append('t');
		}
		else {
			inputs.append('f');
		}
		if(Gdx.input.isKeyPressed(Input.Keys.A)) {
//			playerbody.applyForceToCenter(-3, 0, false);
			inputs.append('t');
		}
		else {
			inputs.append('f');
		}
		if(Gdx.input.isKeyPressed(Input.Keys.D)) {
//			playerbody.applyForceToCenter(3, 0, false);
			inputs.append('t');
		}
		else {
			inputs.append('f');
		}
		if(Gdx.input.isKeyJustPressed(Input.Keys.SPACE)){
//			player.toggleWeapon();
			inputs.append('t');
		}
		else {
			inputs.append('f');
		}
		
//		if(player.weaponReady() && !player.bombAlive()) {
//			if(Gdx.input.isTouched()) {
//				player.incrementPowerLevel();
//			}
//			else if(player.powerLevelNonZero()) {
//				player.LaunchBomb();
//				player.resetPowerLevel();
//			}
//		}
		if(player.getPowerLevel() != -1) {
			inputs.append(';');
			inputs.append(getPowerIndicatorAngle(player));
			
			if(!player.isBombAlive()) {
				inputs.append(';');
				inputs.append(Gdx.input.isTouched() ? 't' : 'f');
			}
			
		}
		
	}

	@Override
	protected void stageRender(float delta) {
		// TODO Auto-generated method stub
		stage.act(delta);
	    stage.draw();
	}

	@Override
	protected void batchRender() {
		// TODO Auto-generated method stub
		ground.draw(batch);
	    
	    batch.begin();
	    for(int i = 0; i < players.size; i++) {
	    	players.get(i).drawSprites(batch);
	    }
	    batch.end();
	}

	@Override
	protected void preRender(float delta) {
		// TODO Auto-generated method stub
		ScreenUtils.clear(1, 1, 1, 1f);
		camera.update();
        batch.setProjectionMatrix(camera.combined.scl(32));
		
	}

	@Override
	protected void polling() {
		// TODO Auto-generated method stub
		String response = sendMsg("gb");
		
		if(response.charAt(0) == 'p') {
			
			String[] broadcast = response.substring(1).split(";", -1);
			
			String[] playersdata = broadcast[0].split("&");
			
			for(int i = 0; i < players.size; i++) {
				Player aplayer = players.get(i);
				
				String[] playerdata = playersdata[i].split("#");
				String[] position = playerdata[0].split(",");
				
				aplayer.centerSpriteToHere(Float.parseFloat(position[0]), Float.parseFloat(position[1]));
				
				if(playerdata[1].charAt(0) == 't') {
					String[] powerdata = playerdata[1].substring(1).split(",");
					
					aplayer.setPowerLevel(Integer.parseInt(powerdata[0]));
					aplayer.getPowerSprite().setRotation(Float.parseFloat(powerdata[1]));
				}
				else {
					aplayer.setPowerLevel(-1);
				}
				
				if(playerdata[2].charAt(0) == 't') {
					String[] bombdata = playerdata[2].substring(1).split(",");
					
					Sprite bombsprite = aplayer.getBombSprite();
					bombsprite.setCenter(Float.parseFloat(bombdata[0]), Float.parseFloat(bombdata[1]));
					bombsprite.setRotation(Float.parseFloat(bombdata[2]));
					aplayer.setBombAlive(true);
				}
				else {
					aplayer.setBombAlive(false);
				}
				
				if(aplayer.getPowerLevel() != -1) aplayer.updatePowerIndicator();
				
			}
			
			if(!broadcast[1].equals("")) {
				String[] fixtures = broadcast[1].split("c");
				System.out.println("No of fixtures: " + fixtures.length);
				for(int i = 0; i < fixtures.length; i++) {
					String[] coordinates = fixtures[i].split(",");
					float[] arr = new float[coordinates.length];
					
					for(int j = 0; j < arr.length; j++) {
						arr[j] = Float.parseFloat(coordinates[j]);
					}
					ground.addFixture(arr);
					
				}
			}
			
			if(!broadcast[2].equals("")) {
				String[] fixtureIndices = broadcast[2].split("d");
				
				for(int i = 0; i < fixtureIndices.length; i++) {
					ground.removeFixture(Integer.parseInt(fixtureIndices[i]));
				}
				
			}
			
		}
		
		if(isinput && inputs.length() > 2) {
			
			response = sendMsg(inputs.toString());
			
		}
		
		response = sendMsg("gt");
		isinput = response.charAt(0) == 'p';
		
	}
	
	private float getPowerIndicatorAngle(Player player) {
		float inputx = (Gdx.input.getX() - mainvp.getScreenX())*(960f/mainvp.getScreenWidth())/32f;
		float inputy = ((Gdx.graphics.getHeight() - Gdx.input.getY()) - mainvp.getScreenY())*(960f/mainvp.getScreenWidth())/32f;
		Vector2 vector = new Vector2();
		vector.set(inputx - player.getSprite().getX(), inputy - player.getSprite().getY());
		return vector.angleDeg();
	}
	
	private void printsa(String[] arr) {
		System.out.print("->");
		for(int i = 0; i < arr.length; i++) {
			System.out.print(arr[i]);
			if(i < arr.length-1) {
				System.out.print("^");
			}
		}
		System.out.println("<-");
	}
	
}
