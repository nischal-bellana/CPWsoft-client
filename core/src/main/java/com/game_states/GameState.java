package com.game_states;


import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;

import com.GameObjects.Bomb;
import com.GameObjects.BombFactory;
import com.GameObjects.Ground;
import com.GameObjects.Player;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2D;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;

import com.ray3k.stripe.scenecomposer.SceneComposerStageBuilder;

public class GameState extends State{
	
	private TextureAtlas atlas;
	private AtlasRegion backregion;
	
	String name;
	String rname;
			
	private Array<Player> players;
	private Player player;
	private Sprite turnmark;
	
	private int inputindex = 0;
	private int index = 0;
	private StringBuilder inputs;
	
	private BombFactory bombfactory;
	private Array<Bomb> bombs;
	
	private Ground ground;
	
	private UDPServerBridgeReceiver udpbridgereceiver;
	private UDPServerBridgeSender udpbridgesender;
	
	GameState(State prevst, String name, String rname){
		super(prevst);
		
		this.name = name;
		this.rname = rname;
	}
	
	@Override
	public void create() {
		// TODO Auto-generated method stub
		
		atlas = new TextureAtlas("packimgs//atlaspack.atlas");
		
		inputs = new StringBuilder();
		
		message = new StringBuilder();
		
		initUDPServerBridge();
		
		createStage();
		createGameObjects();
		getRegions();
		System.out.println("1.My index is : " + index + " inputindex is : " + inputindex);
		System.out.println("In gamestate ");
	}

	@Override
	public void render() {
		// TODO Auto-generated method stub
		super.render();
		
		 appendRequest("gb");
		
	    if(gsm.next_st != null) return;
	    
	    updateTurnMark();
	    
	    if(index == inputindex) {
	    	inputUpdate();
	    }
	    
	    if(index == inputindex && inputs.length() > 0) {  
			udpbridgesender.addMessage(inputs.toString());
			inputs = new StringBuilder();
		}
	    
	    applyUDPBroadcast(udpbridgereceiver.getBroadcast());
	    
	    ground.updateDepthBuffer();
	    
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		super.dispose();
		
		atlas.dispose();
		ground.disposeShapeRenderer();
		udpbridgesender.closeSocket();
		System.out.println("Closed Sender");
		udpbridgereceiver.closeSocket();
		System.out.println("Closed Receiver");
	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
		super.resize(width, height);
		ground.setUpdateDepthBuffer(true);
	}
	
	@Override
	protected void createStage() {
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
		
		serverbridge.addMessage("gn");
		
		String response = forceResponse("gn");
		
		String[] responsesplitted = response.substring(1).split("&");
		
		index = Integer.parseInt(responsesplitted[0]);
		
		String[] names = responsesplitted[1].split(",");
		
		for(int i = 0; i < names.length; i++) {
			Player player = new Player(stage, skin, names[i], mainvp, atlas);
			
			players.add(player);
		}
		
		player = players.get(index);
		
	}
	
	private void getRegions() {
		backregion = atlas.findRegion("back");
		turnmark = new Sprite(atlas.findRegion("currentplayermarker"));
		turnmark.setBounds(0, 0, 0.3f, 0.3f);
		
		Drawable scrollpaneback = skin.getDrawable("scrollpaneback");
		scrollpaneback.setMinHeight(20);
		Drawable back = skin.getDrawable("back");
		back.setMinHeight(15);
		back.setMinWidth(0);
	}
	
	private void inputUpdate() {
		
		if(inputs.length() != 0) inputs = new StringBuilder();
		
		if(Gdx.input.isKeyJustPressed(Input.Keys.W)) {
//			playerbody.setLinearVelocity(0, 10);
			inputs.append('w');
		}
		if(Gdx.input.isKeyPressed(Input.Keys.A)) {
//			playerbody.applyForceToCenter(-3, 0, false);
			inputs.append('a');
		}
		if(Gdx.input.isKeyPressed(Input.Keys.D)) {
//			playerbody.applyForceToCenter(3, 0, false);
			inputs.append('d');
		}
		if(Gdx.input.isKeyJustPressed(Input.Keys.SPACE)){
//			player.toggleWeapon();
			inputs.append('c');
			player.setPowerLevel(player.getPowerLevel() == -1? 0 : -1);
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
			if(Gdx.input.isTouched()) inputs.append('t');
			
			inputs.append(getPowerIndicatorAngle(player));
			
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
	    
	    turnmark.draw(batch);
	    
	    for(int i = 0; i < bombs.size; i++) {
	    	bombs.get(i).draw(batch);
	    }
	    
	    batch.end();
	}

	@Override
	protected void preRender() {
		// TODO Auto-generated method stub
		ScreenUtils.clear(1, 1, 1, 1f);
		camera.update();
        batch.setProjectionMatrix(camera.combined.scl(32));
		
	}

	@Override
	protected void handleResponse(String response) {
		// TODO Auto-generated method stub
		String request = response.substring(0, 2);
		
		if(request.equals("gg") && response.charAt(2) == 'f') {
			changeState(new RoomState(this, rname, name));
		}
		else if(request.equals("gb") && response.charAt(2) == 'p') {
			applyBroadcast(response.substring(3));
		}
		
		
	}

	@Override
	protected void polling() {
		// TODO Auto-generated method stub
		
		appendRequest("gg");
		
	}
	
	private void applyBroadcast(String broadcast) {
		
		String[] broadcastsplitted = broadcast.split(":", -1);
		
		for(String segment: broadcastsplitted) {
			handleBroadcastSegment(segment);
		}
		
	}
	
	private void handleBroadcastSegment(String segment) {
		switch(segment.charAt(0)) {
		case 'p':
			playersRemoval(segment);
			break;
		case 'i':
			inputChange(segment);
			break;
		case 'd':
			playersDamage(segment);
			break;
		case 's':
			playersScore(segment);
			break;
		case 'b':
			bombsRemoveOrAdd(segment);
			break;
		case 'g':
			updateGroundFixtures(segment);
			break;
		}
	}
	
	private void playersRemoval(String segment) {
		String data = segment.substring(1);
		String[] removedindicessplitted = data.split(",");
		
		for(String removedindexstr : removedindicessplitted) {
			
			int removedindex = Integer.parseInt(removedindexstr);
			
			Player aplayer = players.removeIndex(removedindex);
			
			stage.getRoot().removeActor(aplayer.getNameLabel());
			
		}
		
		index = players.indexOf(player, true);
	}
	
	private void inputChange(String segment) {
		inputindex = Integer.parseInt(segment.substring(1));
		Label currentplayer = (Label)stage.getRoot().findActor("currentplayer");
		currentplayer.setText(players.get(inputindex).getName());
	}
	
	private void playersDamage(String segment) {
		String[] data = segment.substring(1).split(",");
		
		for(String playerdata : data) {
			String[] playerdatasplitted =  playerdata.split("&");
			int i = Integer.parseInt(playerdatasplitted[0]);
			Player aplayer = players.get(i);
			
			int damage = Integer.parseInt(playerdatasplitted[1]);
			aplayer.damageBy(damage, skin, stage);
		}
	}
	
	private void playersScore(String segment) {
		String[] data = segment.substring(1).split(",");
		
		for(String playerdata : data) {
			String[] playerdatasplitted =  playerdata.split("&");
			int i = Integer.parseInt(playerdatasplitted[0]);
			Player aplayer = players.get(i);
			
			int score = Integer.parseInt(playerdatasplitted[1]);
			aplayer.scoreBy(score, skin, stage);
		}
	}
	
	private void bombsRemoveOrAdd(String segment) {
		int addcount = Integer.parseInt(segment.substring(1));
		
		if(addcount > 0) {
			for(int i = 0; i < addcount; i++) {
				bombs.add(bombfactory.generateBomb());
			}
		}
		else {
			addcount = Math.abs(addcount);
			for(int i = 0; i < addcount; i++) {
				bombs.removeIndex(0);
			}
		}
	}
	
	private void updateGroundFixtures(String segment) {
		
		String[] data = segment.substring(1).split("&", -1);
		
		for(String datum: data) {
			if(datum.charAt(0) == 'c') {
				groundAddFixtures(datum.substring(1));
			}
			else {
				groundRemoveFixtures(datum.substring(1));
			}
		}
		
	}
	
	private void groundAddFixtures(String fixturesstr) {
		String[] fixtures = fixturesstr.split("c");
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
	
	private void groundRemoveFixtures(String indicesstr) {
		String[] fixtureIndices = indicesstr.split("d");
		
		for(int i = 0; i < fixtureIndices.length; i++) {
			ground.removeFixture(Integer.parseInt(fixtureIndices[i]));
		}
	}
	
	private void applyUDPBroadcast(String broadcast) {
		if(broadcast.length() == 0) return;
		
		String[] broadcastsplitted = broadcast.split(":", -1);
		
		if(broadcastsplitted.length != 3) return;
		
		updatePlayersSprites(broadcastsplitted);
		
		updateBombsSprites(broadcastsplitted);
		
		updateTime(broadcastsplitted);
		
	}
	
	private void updatePlayersSprites(String[] broadcastsplitted) {
		String[] playersdata = broadcastsplitted[0].split("&");
		
		if(playersdata.length != players.size) return;
		
		for(int i = 0; i < players.size; i++) {
			Player aplayer = players.get(i);
			
			String[] playerdata = playersdata[i].split("#", -1);
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
	
	private void updateBombsSprites(String[] broadcastsplitted) {
		if(broadcastsplitted[1].length() == 0) return;
		
		String[] bombsdatastr = broadcastsplitted[1].split("#");
		
		if(bombsdatastr.length != bombs.size) return;
		
		for(int i = 0; i < bombs.size; i++) {
			Bomb bomb = bombs.get(i);
			String[] bombdatastr = bombsdatastr[i].split(",");
			
			float x = 0;
			try {
				x = Float.parseFloat(bombdatastr[0]);
			}
			catch(Exception e) {
				printsa(bombsdatastr);
			}
			float y = Float.parseFloat(bombdatastr[1]);
			float angle = Float.parseFloat(bombdatastr[2]);
			
			bomb.setCenter(x, y);
			bomb.setRotation(angle);
		}
		
	}
	
	private void updateTime(String[] broadcastsplitted) {
		String[] timers =  broadcastsplitted[2].split(",");
		
		Label matchtimer = (Label)stage.getRoot().findActor("matchtimer");
		matchtimer.setText(timers[0]);
		
		Label turntimer = (Label)stage.getRoot().findActor("turntimer");
		turntimer.setText(timers[1]);
	}
	
	private float getPowerIndicatorAngle(Player player) {
		float inputx = (Gdx.input.getX() - mainvp.getScreenX())*(960f/mainvp.getScreenWidth())/32f;
		float inputy = ((Gdx.graphics.getHeight() - Gdx.input.getY()) - mainvp.getScreenY())*(960f/mainvp.getScreenWidth())/32f;
		Vector2 vector = new Vector2();
		vector.set(inputx - player.getSprite().getX(), inputy - player.getSprite().getY());
		return vector.angleDeg();
	}
	
	private void updateTurnMark() {
		Sprite currentplayersprite = players.get(inputindex).getSprite();
	    turnmark.setCenter(currentplayersprite.getX() + (currentplayersprite.getWidth()/2), currentplayersprite.getY() + currentplayersprite.getHeight() + (turnmark.getWidth()/2));
	}
	
	private void printsa(String[] arr) {
		System.out.print("||");
		for(int i = 0; i < arr.length; i++) {
			System.out.print(arr[i]);
			if(i < arr.length-1) {
				System.out.print("^");
			}
		}
		System.out.println("||");
	}
	
	private String forceResponse(String request) {
		String return_message = serverbridge.pollReturnMessage();
		
		while(true) {
			if(return_message.equals("")) {
				try {
					Thread.sleep(20);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return_message = serverbridge.pollReturnMessage();
				continue;
			}
			
			String[] responses = return_message.split(";");
			
			for(String response : responses) {
				if(response.substring(0, 2).equals(request)) {
					return response.substring(2);
				}
			}
			
			return_message = serverbridge.pollReturnMessage();
			
		}
		
	}
	
	private void initUDPServerBridge() {
		try {
			udpbridgereceiver = new UDPServerBridgeReceiver(serverbridge);
			udpbridgesender = new UDPServerBridgeSender(serverbridge);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Thread udpthread1 = new Thread(udpbridgereceiver);
		udpthread1.start();
		
		Thread udpthread2 = new Thread(udpbridgesender);
		udpthread2.start();
	}
	
}
