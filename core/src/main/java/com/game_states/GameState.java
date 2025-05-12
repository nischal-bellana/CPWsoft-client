package com.game_states;


import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Arrays;

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
import com.utils.ParsingUtils;

public class GameState extends State{
	
	private TextureAtlas atlas;
	private AtlasRegion backregion;
	
	private String name;
	private String room_name;
			
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
	
	@Override
	public void create(State prevst) {
		// TODO Auto-generated method stub
		super.create(prevst);
		
		name = prevst.next_state_inf[0];
		room_name = prevst.next_state_inf[1];
		
		atlas = new TextureAtlas("packimgs//atlaspack.atlas");
		
		initUDPServerBridge();
		
		createStage();
		createGameObjects();
		getRegions();
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		super.dispose();
		
		atlas.dispose();
		ground.disposeShapeRenderer();
		udpbridgesender.closeSocket();
		udpbridgereceiver.closeSocket();
	}
	
	

	@Override
	public void disposeHalf() {
		// TODO Auto-generated method stub
		super.disposeHalf();
		
		atlas.dispose();
		ground.disposeShapeRenderer();
		udpbridgesender.closeSocket();
		udpbridgereceiver.closeSocket();
	}
	

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
		super.resize(width, height);
		ground.setUpdateDepthBuffer(true);
	}
	
	@Override
	public void createStage() {
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
		
		serverbridge.addMessage("2&gn");
		
		String return_message = forceResponse();
		int start = ParsingUtils.getBeginIndex(0, return_message, '&');
		start += 3;
		int end = ParsingUtils.getBeginIndex(start, return_message, '&');
		
		index = ParsingUtils.parseInt(start, end - 1, return_message);
		start = end;
		
		for(int i = start; i < return_message.length();) {
			start = ParsingUtils.getBeginIndex(i, return_message, '&');
			end = start + ParsingUtils.parseInt(i, start - 1, return_message);
			
			Player player = new Player(stage, skin, return_message.substring(start, end), mainvp, atlas);
			
			players.add(player);
			
			i = end;
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
		
		if(Gdx.input.isKeyJustPressed(Input.Keys.W)) {
//			playerbody.setLinearVelocity(0, 10);
			if(inputs == null) inputs = new StringBuilder();
			
			inputs.append('w');
		}
		if(Gdx.input.isKeyPressed(Input.Keys.A)) {
//			playerbody.applyForceToCenter(-3, 0, false);
			if(inputs == null) inputs = new StringBuilder();
			
			inputs.append('a');
		}
		if(Gdx.input.isKeyPressed(Input.Keys.D)) {
//			playerbody.applyForceToCenter(3, 0, false);
			if(inputs == null) inputs = new StringBuilder();
			
			inputs.append('d');
		}
		if(Gdx.input.isKeyJustPressed(Input.Keys.SPACE)){
//			player.toggleWeapon();
			if(inputs == null) inputs = new StringBuilder();
			
			inputs.append('c');
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
			if(inputs == null) inputs = new StringBuilder();
			
			if(Gdx.input.isTouched()) inputs.append('t');
			
			inputs.append(getPowerIndicatorAngle(player));
			
		}
		
	}

	@Override
	public void batchRender() {
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
	public void preRender() {
		// TODO Auto-generated method stub
		ScreenUtils.clear(1, 1, 1, 1f);
		
		camera.update();
        batch.setProjectionMatrix(camera.combined.scl(32));
		
	}

	@Override
	public void postRenderUpdate() {
		// TODO Auto-generated method stub
		super.postRenderUpdate();
		
		appendRequest("gb");
		
	    if(gsm.next_st != null) return;
	    
	    updateTurnMark();
	    
	    if(index == inputindex) {
	    	inputUpdate();
	    }
	    
	    if(index == inputindex && inputs != null) {  
			udpbridgesender.addMessage(inputs.toString());
			inputs = null;
		}
	    
	    applyUDPBroadcast(udpbridgereceiver.getBroadcast());
	    
	    ground.updateDepthBuffer();
	}

	@Override
	public void handleResponse(int start, int end, String return_message) {
		// TODO Auto-generated method stub
		
		if(ParsingUtils.requestCheck(start, return_message, "gg") && return_message.charAt(start + 2) == 'f') {
			next_state_inf = new String[2];
			next_state_inf[0] = name;
			next_state_inf[1] = room_name;
			changeState(new RoomState());
		}
		else if(ParsingUtils.requestCheck(start, return_message, "gb") && return_message.charAt(start + 2) == 'p') {
			applyBroadcast(start + 3, end, return_message);
		}
		
		
	}

	@Override
	public void poll() {
		// TODO Auto-generated method stub
		
		appendRequest("gg");
		
	}
	
	private void applyBroadcast(int start, int end, String return_message) {
		for(int i = start; i < end; ) {
			int start1 = ParsingUtils.getBeginIndex(i, return_message, '&');
			int end1 = start1 + ParsingUtils.parseInt(i, start1 - 1, return_message);
			
			handleBroadcastSegment(start1, end1, return_message);
			
			i = end1;
		}
		
	}
	
	private void handleBroadcastSegment(int start, int end, String return_message) {
		switch(return_message.charAt(start)) {
		case 'p':
			playersRemoval(start + 1, end, return_message);
			break;
		case 'i':
			inputChange(start + 1, end, return_message);
			break;
		case 'd':
			playersDamage(start + 1, end, return_message);
			break;
		case 's':
			playersScore(start + 1, end, return_message);
			break;
		case 'b':
			bombsRemoveOrAdd(start + 1, end, return_message);
			break;
		case 'g':
			updateGroundFixtures(start + 1, end, return_message);
			break;
		}
	}
	
	private void playersRemoval(int start, int end, String return_message) {
		for(int i = start; i < end;) {
			int start1 = ParsingUtils.getBeginIndex(i, end, return_message, '&');
			
			int removedindex = ParsingUtils.parseInt(i, start1 - 1, return_message);
			
			Player aplayer = players.removeIndex(removedindex);
			
			stage.getRoot().removeActor(aplayer.getNameLabel());
			
			i = start1;
		}
		
		index = players.indexOf(player, true);
	}
	
	private void inputChange(int start, int end, String return_message) {
		inputindex = ParsingUtils.parseInt(start, end, return_message);
		Label currentplayer = (Label)stage.getRoot().findActor("currentplayer");
		currentplayer.setText(players.get(inputindex).getName());
	}
	
	private void playersDamage(int start, int end, String return_message) {
		for(int i = start; i < end;) {
			int start1 = ParsingUtils.getBeginIndex(i, return_message, '&');
			int end1 = start1 + ParsingUtils.parseInt(i, start1 - 1, return_message);
			
			int start2 = ParsingUtils.getBeginIndex(start1, return_message, '&');
			int ind = ParsingUtils.parseInt(start1, start2 - 1, return_message);
			Player aplayer = players.get(ind);
			
			int damage = ParsingUtils.parseInt(start2, end1, return_message);
			aplayer.damageBy(damage, skin, stage);
			
			i = end1;
		}
	}
	
	private void playersScore(int start, int end, String return_message) {
		for(int i = start; i < end;) {
			int start1 = ParsingUtils.getBeginIndex(i, return_message, '&');
			int end1 = start1 + ParsingUtils.parseInt(i, start1 - 1, return_message);
			
			int start2 = ParsingUtils.getBeginIndex(start1, return_message, '&');
			int ind = ParsingUtils.parseInt(start1, start2 - 1, return_message);
			Player aplayer = players.get(ind);
			
			int scorepoint = ParsingUtils.parseInt(start2, end1, return_message);
			aplayer.scoreBy(scorepoint, skin, stage);
			
			i = end1;
		}
	}
	
	private void bombsRemoveOrAdd(int start, int end, String return_message) {
		int addcount = ParsingUtils.parseInt(start, end, return_message);
		
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
	
	private void updateGroundFixtures(int start, int end, String return_message) {
		
		for(int i = start; i < end;) {
			int start1 = ParsingUtils.getBeginIndex(i, return_message, '&');
			int end1 = start1 + ParsingUtils.parseInt(i, start1 - 1, return_message);
			
			if(return_message.charAt(start1) == 'c') {
				groundAddFixtures(start1 + 1, end1, return_message);
			}
			else {
				groundRemoveFixtures(start1 + 1, end1, return_message);
			}
			
			
			i = end1;
		}
		
	}
	
	private void groundAddFixtures(int start, int end, String return_message) {
		for(int i = start; i < end;) {
			int start1 = ParsingUtils.getBeginIndex(i, return_message, '&');
			int end1 = start1 + ParsingUtils.parseInt(i, start1 - 1, return_message);
			
			int start2 = ParsingUtils.getBeginIndex(start1, return_message, '&');
			int len = ParsingUtils.parseInt(start1, start2 - 1, return_message);
			
			float[] arr = new float[len];
			
			for(int i2 = start2, j = 0; i2 < end1 && j < arr.length; j++) {
				int start3 = ParsingUtils.getBeginIndex(i2, end1, return_message, '&');
				
				arr[j] = ParsingUtils.parseFloat(i2, start3 - 1, return_message);
				
				i2 = start3;
			}
			
			ground.addFixture(arr);
			
			i = end1;
		}
	}
	
	private void groundRemoveFixtures(int start, int end, String return_message) {
		for(int i = start; i < end;) {
			int start1 = ParsingUtils.getBeginIndex(i, end, return_message, '&');
			
			ground.removeFixture(ParsingUtils.parseInt(i, start1 - 1, return_message));
			
			i = start1;
		}
		
	}
	
	private void applyUDPBroadcast(String broadcast) {
		for(int i = 0; i < broadcast.length();) {
			int start = ParsingUtils.getBeginIndex(i, broadcast.length(), broadcast, '&');
			int end = start + ParsingUtils.parseInt(i, start - 1, broadcast);
			
			if(broadcast.charAt(start) == 'p') {
				updatePlayersDyn(start + 1, end, broadcast);
			}
			else if(broadcast.charAt(start) == 'b') {
				updateBombsDyn(start + 1, end, broadcast);
			}
			else {
				updateTimes(start + 1, end, broadcast);
			}
			
			i = end;
		}
		
	}
	
	private void updatePlayersDyn(int start, int end, String broadcast) {
		for(int i = start; i < end;) {
			int start2 = ParsingUtils.getBeginIndex(i, broadcast, '&');
			int end2 = start2 + ParsingUtils.parseInt(i, start2 - 1, broadcast);
			
			int start3 = start2;
			int end3 = ParsingUtils.getBeginIndex(start3, end2, broadcast, '&');
			int playerindex = ParsingUtils.parseInt(start3, end3 - 1, broadcast);
			if(playerindex >= players.size) return;
			
			start3 = end3;
			end3 = ParsingUtils.getBeginIndex(start3, end2, broadcast, '&');
			float x = ParsingUtils.parseFloat(start3, end3 - 1, broadcast);
			
			start3 = end3;
			end3 = ParsingUtils.getBeginIndex(start3, end2, broadcast, '&');
			float y = ParsingUtils.parseFloat(start3, end3 - 1, broadcast);
			
			start3 = end3;
			end3 = ParsingUtils.getBeginIndex(start3, end2, broadcast, '&');
			float angle = ParsingUtils.parseFloat(start3, end3 - 1, broadcast);
			
			start3 = end3;
			end3 = ParsingUtils.getBeginIndex(start3, end2, broadcast, '&');
			int level = ParsingUtils.parseInt(start3, end3 - 1, broadcast);
			
			Player aplayer = players.get(playerindex);
			aplayer.getSprite().setPosition(x, y);
			
			aplayer.getPowerSprite().setRotation(angle);
			
			aplayer.setPowerLevel(level);
			
			aplayer.updatePowerIndicator();
			
			i = end2;
		}
	}
	
	private void updateBombsDyn(int start, int end, String broadcast) {
		for(int i = start; i < end;) {
			int start2 = ParsingUtils.getBeginIndex(i, broadcast, '&');
			int end2 = start2 + ParsingUtils.parseInt(i, start2 - 1, broadcast);
			
			int start3 = start2;
			int end3 = ParsingUtils.getBeginIndex(start3, end2, broadcast, '&');
			int bombindex = ParsingUtils.parseInt(start3, end3 - 1, broadcast);
			if(bombindex >= bombs.size) return;
			
			start3 = end3;
			end3 = ParsingUtils.getBeginIndex(start3, end2, broadcast, '&');
			float x = ParsingUtils.parseFloat(start3, end3 - 1, broadcast);
			
			start3 = end3;
			end3 = ParsingUtils.getBeginIndex(start3, end2, broadcast, '&');
			float y = ParsingUtils.parseFloat(start3, end3 - 1, broadcast);
			
			start3 = end3;
			end3 = ParsingUtils.getBeginIndex(start3, end2, broadcast, '&');
			float angle = ParsingUtils.parseFloat(start3, end3 - 1, broadcast);
			
			Bomb bomb = bombs.get(bombindex);
			bomb.getSprite().setPosition(x, y);
			bomb.getSprite().setRotation(angle);
			
			i = end2;
		}
	}
	
	private void updateTimes(int start, int end, String broadcast) {
		int start2 = ParsingUtils.getBeginIndex(start, end, broadcast, '&');
		int mtchtmer = ParsingUtils.parseInt(start, start2 - 1, broadcast);
		int trntmer = ParsingUtils.parseInt(start2, end, broadcast);
		
		Label matchtimer = (Label)stage.getRoot().findActor("matchtimer");
		matchtimer.setText(mtchtmer);
		Label turntimer = (Label)stage.getRoot().findActor("turntimer");
		turntimer.setText(trntmer);
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
	
	private String forceResponse() {
		String return_message = serverbridge.pollReturnMessage();
		while(return_message.length() == 0) {
			return_message = serverbridge.pollReturnMessage();
		}
		return return_message;
		
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
