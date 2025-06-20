package com.game_states;


import java.util.HashMap;
import java.util.Random;

import com.GameObjects.Bomb;
import com.GameObjects.BombContext;
import com.GameObjects.BombFactory;
import com.GameObjects.BombWorld;
import com.GameObjects.BombWorldFactory;
import com.GameObjects.Clip;
import com.GameObjects.Ground;
import com.GameObjects.GroundWorld;
import com.GameObjects.Player;
import com.GameObjects.PlayerWorld;
import com.GameObjects.myContactListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2D;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Queue;
import com.badlogic.gdx.utils.ScreenUtils;
import com.ray3k.stripe.scenecomposer.SceneComposerStageBuilder;
import com.utils.WorldUtils;
import com.utils.polygonGen;

public class GameStateOffline extends State {

	private World world;
	private float accumulator;
	private Random random;

	private TextureAtlas atlas;
	private AtlasRegion backregion;

	private Array<Player> players;
	private HashMap<Player, PlayerWorld> playersmap;
	private Player player;
	private Sprite turnmark;
	private int launchcount = 0;
	private int inputindex = 0;

	private BombFactory bombfactory;
	private BombWorldFactory bombworldfactory;
	private Array<Bomb> bombs;
	private HashMap<Bomb, BombWorld> bombsmap;
	private Queue<Bomb> bombsaddqueue;
	private Queue<Integer> bombsremovequeue;

	private Queue<Clip> clipsqueue;
	private Clip currentclip;

	private Ground ground;
	private GroundWorld groundworld;

	private float timer = 0;
	private float turntimer = 0;
	private boolean gameended = false;
	private boolean shouldexit = false;
	private final float matchtime = 300;
	private final float turntime = 20;

	@Override
	public void create() {
		// TODO Auto-generated method stub
		Box2D.init();

		world = new World(new Vector2(0,-9.8f), true);
		world.setContactListener(new myContactListener());
		atlas = new TextureAtlas("packimgs//atlaspack.atlas");
		random = new Random();

		initStage();
		createGameObjects();
		getRegions();
	}



	@Override
	public void create(State prevst) {
		// TODO Auto-generated method stub
		super.create(prevst);
	}



	@Override
	public void render() {
		// TODO Auto-generated method stub
		float delta = Gdx.graphics.getDeltaTime();

		preRender();

	    batchRender();

	    stageRender();

	    doPhysicsStep(delta);

	    gameUpdate(delta);
	    if(shouldexit) {
	    	changeState(new FirstState());
	    	return;
	    }

	    inputUpdate();

	    updatePlayers(delta);

	    updateTurnMark();

	    updateBombs(delta);

	    updateClips(delta);

	    groundworld.createFixtures();
	    groundworld.destroyFixtures();
	    ground.updateDepthBuffer();
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		super.dispose();

		world.dispose();
		atlas.dispose();
		ground.disposeShapeRenderer();
	}

	@Override
	public void resize(int width, int height) {
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

		clipsqueue = new Queue<>();

		bombfactory = new BombFactory(atlas);
		bombworldfactory = new BombWorldFactory(clipsqueue);
		bombs = new Array<Bomb>();
		bombsmap = new HashMap<>();
		bombsaddqueue = new Queue<Bomb>();
		bombsremovequeue = new Queue<Integer>();

		createGround();
		createPlayers();
	}

	private void createGround() {
//		polygonGen.imageToFixtures("images//atlasimages//grnd.png", "fonts//grnddata.xml");
		ground = new Ground(atlas, camera);

		BodyDef bdef = new BodyDef();
		bdef.position.set(-2, 3);
		bdef.fixedRotation = true;
		bdef.type = BodyType.StaticBody;
		Body body = world.createBody(bdef);
		groundworld = new GroundWorld(ground, body);
		body.setUserData(groundworld);
		polygonGen.createCustomBody(world, "fonts//grnddata.xml", body, WorldUtils.createFixDef(1, 0.5f, 0), 1/(32f*3f));

		for(Fixture f : body.getFixtureList()) {
			PolygonShape shape = (PolygonShape)f.getShape();
			float[] arr = new float[2*shape.getVertexCount()];
			Vector2 vec = new Vector2();

			for(int i = 0; i < shape.getVertexCount(); i++) {
				shape.getVertex(i, vec);

				arr[2*i] = vec.x;
				arr[2*i+1] = vec.y;

			}

			ground.addFixture(arr);
			groundworld.addToBroadcastCreate(arr);

		}
	}

	private void createPlayers() {
		players = new Array<>();
		playersmap = new HashMap<>();

		for(int i = 0; i < 5; i++) {
			Player aplayer = createPlayer("Player " + (i + 1), 5 + 3*i, 15);
			players.add(aplayer);
		}
		player = players.get(0);
		Label currentplayer = (Label)stage.getRoot().findActor("currentplayer");
		currentplayer.setText(player.getName());
	}

	private Player createPlayer(String name, float x, float y) {
		Player player = new Player(stage, skin, name, mainvp, atlas);

		BodyDef bdef = new BodyDef();
		bdef.position.set(x, y);
		bdef.fixedRotation = true;
		bdef.type = BodyType.DynamicBody;
		FixtureDef fdef = WorldUtils.createFixDef(1f, 0.3f, 0);
		PolygonShape shp = new PolygonShape();
		shp.setAsBox(0.1f, 0.2f);
		fdef.shape = shp;
		Body playerbody = world.createBody(bdef);
		PlayerWorld playerworld = new PlayerWorld(playerbody, player);
		playersmap.put(player, playerworld);

		playerbody.setUserData(playerworld);
		playerbody.createFixture(fdef).setUserData(playerworld);
		fdef = WorldUtils.createFixDef(0.1f, 0.7f, 0.2f);
		shp.setAsBox(0.1f, 0.01f, new Vector2(0, -0.21f), 0);
		fdef.shape = shp;
		fdef.isSensor = true;
		playerbody.createFixture(fdef).setUserData(playerworld);
		shp.dispose();

		return player;
	}

	private void updatePlayers(float delta) {
		for(Player aplayer : players) {
			PlayerWorld playerworld = playersmap.get(aplayer);
			Body body = playerworld.getBody();
			aplayer.centerSpriteToHere(body.getPosition().x, body.getPosition().y );

			if(aplayer.getHealth() <= 0) {
				if(aplayer.getRespawnTime() > 3) {
					aplayer.respawn(skin, stage);
					body.setTransform((5 + 3 * random.nextInt(5)), 15, 0);
					body.setLinearVelocity(0, -0.01f);
					aplayer.resetRespawnTime();
				}
				else {
					aplayer.passrespawntime(delta);
				}
				continue;
			}

			if(body.getPosition().y < 3) {
				body.applyForceToCenter(0, 1f, false);
				aplayer.addDamage(10);
			}

			int damage = aplayer.pollDamage();

			if(damage != -1) {
				aplayer.damageBy(damage, skin, stage);
			}

			int scorepoint = aplayer.pollScorePoint();

			if(scorepoint != -1) {
				aplayer.scoreBy(scorepoint, skin, stage);
			}

			if(aplayer.getPowerLevel() != -1) aplayer.updatePowerIndicator();
		}
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

	private void gameUpdate(float delta) {

		timer += delta;

		Label matchtimerlabel = (Label)stage.getRoot().findActor("matchtimer");
		int remtime = (int)(matchtime - timer);
		matchtimerlabel.setText(remtime);

		turntimer+=delta;

		Label turntimerlabel = (Label)stage.getRoot().findActor("turntimer");
		remtime = (int)(turntime - turntimer);
		turntimerlabel.setText(remtime);

		if(turntimer > turntime) {
			inputindex++;
			inputindex %= players.size;
			inputChanged();
		}

		if(timer > matchtime) {
			shouldexit = true;
			return;
		}

	}

	private void inputUpdate() {
		PlayerWorld playerworld = playersmap.get(player);
		Body body = playerworld.getBody();

		if(Gdx.input.isKeyJustPressed(Input.Keys.W)) {
			if(playerworld.inContact())
			body.setLinearVelocity(0, 5);
		}

		if(Gdx.input.isKeyPressed(Input.Keys.A)) {
			body.applyForceToCenter(-0.1f, 0, false);
		}

		if(Gdx.input.isKeyPressed(Input.Keys.D)) {
			body.applyForceToCenter(0.1f, 0, false);
		}

		if(launchcount < 3 && Gdx.input.isKeyJustPressed(Input.Keys.SPACE)){
			player.setPowerLevel(player.getPowerLevel() == -1? 0 : -1);
		}

		if(launchcount < 3 && player.getPowerLevel() != -1) {
			adjustAngle(getPowerIndicatorAngle(player));

			touchInput(Gdx.input.isTouched(), playerworld);
		}

	}

	private void updateBombs(float delta) {
		for(BombWorld bombworld : bombsmap.values()) {
			bombworld.update(delta);
		}

		for(int i = 0; i < bombs.size; i++) {
			Bomb bomb = bombs.get(i);
			BombWorld bombworld = bombsmap.get(bomb);

			if(!bomb.isAlive()) {
				bombs.removeIndex(i);
				bombsmap.remove(bomb);
				bombsremovequeue.addLast(i);
				continue;
			}

			bomb.setCenter(bombworld.getBody().getPosition().x, bombworld.getBody().getPosition().y);
			bomb.setRotation((float) Math.toDegrees(bombworld.getBody().getAngle()));
		}

	}

	private void updateClips(float delta) {
		if(currentclip == null) {
			currentclip = clipsqueue.isEmpty() ? null : clipsqueue.removeFirst();
			return;
		}

		if(currentclip.updateClip(delta)) currentclip = null;
	}

	@Override
	public void batchRender() {
		// TODO Auto-generated method stub
		batch.begin();

		batch.draw(backregion, 0, 0, 960/32f, 540/32f);

		batch.end();

		ground.draw(batch);

	    batch.begin();
	    for(Player aplayer : players) {
	    	aplayer.drawSprites(batch);
	    }

	    turnmark.draw(batch);

	    for(Bomb bomb : bombs) {
	    	bomb.draw(batch);
	    }

	    batch.end();
	}

	@Override
	public void preRender() {
		// TODO Auto-generated method stub
		ScreenUtils.clear(1, 1, 1, 1);
		camera.update();
		batch.setProjectionMatrix(camera.combined.scl(32));
	}

	private void doPhysicsStep(float deltaTime) {
	    // fixed time step
	    // max frame time to avoid spiral of death (on slow devices)
	    float frameTime = Math.min(deltaTime, 0.25f);
	    accumulator += frameTime;
	    while (accumulator >= 1/60f) {
	        world.step(1/60f, 6, 2);
	        accumulator -= 1/60f;
	    }
	}

	private void adjustAngle(float angle) {
		player.getPowerSprite().setRotation(angle);
	}

	private void touchInput(boolean touched, PlayerWorld playerworld) {
		if(touched) {
			player.incrementPowerLevel();
			return;
		}

		if(player.getPowerLevel() > 0) {
			BombContext context = playersmap.get(player).getBombContext();
			BombWorld bombworld = bombworldfactory.LaunchBomb(context);
			Bomb bomb = bombfactory.generateBomb();

			bombworld.setBomb(bomb);
			bombs.add(bomb);
			bombsmap.put(bomb, bombworld);
			bombsaddqueue.addLast(bomb);

			launchcount++;
			player.setPowerLevel(launchcount<3 ? 0 : -1);
		}
	}

	private void inputChanged() {
		player.setPowerLevel(-1);
		player = players.get(inputindex);
		Label currentplayer = (Label)stage.getRoot().findActor("currentplayer");
		currentplayer.setText(player.getName());
		turntimer = 0;
		launchcount = 0;
	}

	private void updateTurnMark() {
		Sprite currentplayersprite = player.getSprite();
	    turnmark.setCenter(currentplayersprite.getX() + (currentplayersprite.getWidth()/2), currentplayersprite.getY() + currentplayersprite.getHeight() + (turnmark.getWidth()/2));
	}

	private float getPowerIndicatorAngle(Player player) {
		float inputx = (Gdx.input.getX() - mainvp.getScreenX())*(960f/mainvp.getScreenWidth())/32f;
		float inputy = ((Gdx.graphics.getHeight() - Gdx.input.getY()) - mainvp.getScreenY())*(960f/mainvp.getScreenWidth())/32f;
		Vector2 vector = new Vector2();
		vector.set(inputx - player.getSprite().getX(), inputy - player.getSprite().getY());
		return vector.angleDeg();
	}

	private void createEndCard() {
		Table endcard = new Table();
		stage.addActor(endcard);

		Player winnerplayer = players.first();

		for(Player aplayer : players) {
			if(winnerplayer.getScore() < aplayer.getScore()) {
				winnerplayer = aplayer;
			}
		}

		Label winnertitle = new Label("Winner is " + winnerplayer.getName(), skin);
		endcard.add(winnertitle);

	}


}
