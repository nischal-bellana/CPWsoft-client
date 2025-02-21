package com.game_states;


import com.GameObjects.Bomb;
import com.GameObjects.BombFactory;
import com.GameObjects.Ground;
import com.GameObjects.Player;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2D;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;

import com.ray3k.stripe.scenecomposer.SceneComposerStageBuilder;

public class GameState extends HomeState{
	
	private TextureAtlas atlas;
	private AtlasRegion backregion;
	
	String rname;
			
	private Array<Player> players;
	private Player player;
	
	private boolean isinput = false;
	private StringBuilder inputs;
	
	private BombFactory bombfactory;
	private Array<Bomb> bombs;
	
	private Ground ground;
	
	GameState(State prevst, String name, String rname){
		super(prevst, name);
		
		this.rname = rname;
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
	    if(gsm.next_st != null) return;
	    
	    if(isinput) {
	    	inputUpdate();
	    }
	    
	    ground.updateDepthBuffer();
	}

	@Override
	protected void dispose() {
		// TODO Auto-generated method stub
		super.dispose();
		
		if(gsm.next_st == null) {
			atlas.dispose();
		}
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
	}
	
	private void createGameObjects(){
		
		bombfactory = new BombFactory(atlas);
		bombs = new Array<Bomb>();
		
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
		backregion = atlas.findRegion("back");
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
			
			inputs.append(';');
			inputs.append(Gdx.input.isTouched() ? 't' : 'f');
			
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
		batch.begin();
		
		batch.draw(backregion, 0, 0, 960/32f, 540/32f);
		
		batch.end();
		
		ground.draw(batch);
	    
	    batch.begin();
	    for(int i = 0; i < players.size; i++) {
	    	players.get(i).drawSprites(batch);
	    }
	    
	    for(int i = 0; i < bombs.size; i++) {
	    	bombs.get(i).draw(batch);
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
		
		String response = sendMsg("gg");
		
		if(response.charAt(0) == 'f') {
			gsm.next_st = new RoomState(this, name, rname);
			return;
		}
		
		getBroadcast();
		
		if(isinput && inputs.length() > 2) {
			response = sendMsg(inputs.toString());
		}
		
		response = sendMsg("gt");
		isinput = response.charAt(0) == 'p';
		
	}
	
	private void getBroadcast() {
		String response = sendMsg("gb");
		
		if(response.charAt(0) != 'p') return;
		
		String[] broadcast = response.substring(1).split(";", -1);
		
		updatePlayers(broadcast);
		
		updateBombs(broadcast);
		
		updateGroundFixtures(broadcast);
		
		updateTime(broadcast);
		
		updateCurrentPlayer(broadcast);
		
	}
	
	private void updatePlayers(String[] broadcast) {
		
		if(!broadcast[0].equals("")) {
			
			String[] removedindicessplitted = broadcast[0].split(",");
			
			for(String removedindexstr : removedindicessplitted) {
				
				int removedindex = Integer.parseInt(removedindexstr);
				
				players.removeIndex(removedindex);
				
			}
			
		}
		
		String[] playersdata = broadcast[1].split("&");
		
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
			
			if(aplayer.getPowerLevel() != -1) aplayer.updatePowerIndicator();
			
		}
	}
	
	private void updateBombs(String[] broadcast) {
		String[] bombsdata = broadcast[2].split("&", -1);
		
		int addcount = Integer.parseInt(bombsdata[0]);
		
		for(int i = 0; i < addcount; i++) {
			bombs.add(bombfactory.generateBomb());
		}
		
		if(!bombsdata[1].equals("")) {
			String[] bombsremovedstr = bombsdata[1].split(",");
			
			for(String bombremovedstr : bombsremovedstr) {
				int index = Integer.parseInt(bombremovedstr);
				bombs.removeIndex(index);
			}
		}
		
		if(bombs.size != 0) {
			String[] bombsdatastr = bombsdata[2].split("#");
			for(int i = 0; i < bombs.size; i++) {
				Bomb bomb = bombs.get(i);
				String[] bombdatastr = bombsdatastr[i].split(",");
				
				float x = Float.parseFloat(bombdatastr[0]);
				float y = Float.parseFloat(bombdatastr[1]);
				float angle = Float.parseFloat(bombdatastr[2]);
				
				bomb.setCenter(x, y);
				bomb.setRotation(angle);
				
			}
		}
	}
	
	private void updateGroundFixtures(String[] broadcast) {
		if(!broadcast[3].equals("")) {
			String[] fixtures = broadcast[3].split("c");
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
		
		if(!broadcast[4].equals("")) {
			String[] fixtureIndices = broadcast[4].split("d");
			
			for(int i = 0; i < fixtureIndices.length; i++) {
				ground.removeFixture(Integer.parseInt(fixtureIndices[i]));
			}
			
		}
	}
	
	private void updateTime(String[] broadcast) {
		String[] timers =  broadcast[5].split(",");
		
		Label matchtimer = (Label)stage.getRoot().findActor("matchtimer");
		matchtimer.setText(timers[0]);
		
		Label turntimer = (Label)stage.getRoot().findActor("turntimer");
		turntimer.setText(timers[1]);
	}
	
	private void updateCurrentPlayer(String[] broadcast) {
		Label currentplayer = (Label)stage.getRoot().findActor("currentplayer");
		currentplayer.setText(broadcast[6]);
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
