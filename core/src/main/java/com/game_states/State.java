package com.game_states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.utils.ParsingUtils;

public class State {
	
	GameStateManager gsm;
	
	OrthographicCamera camera;
    SpriteBatch batch;
	Viewport mainvp;
	
	Skin skin;
	
	Stage stage;
	
	ServerBridge serverbridge;
	float poll_elapsed_time = 0;
	final float poll_period = 0.3f;
	StringBuilder tcp_frame_message;
	
	String[] next_state_inf;
	float frame_delta_time = 0;
	 
	public void create() {
		initCVB();
    	
		skin = new Skin(Gdx.files.internal("packimgs//skin.json"));
        
        createStage();
	}
	
	public void create(State prevst) {
		this.gsm = prevst.gsm;
		
		this.camera = prevst.camera;
		this.mainvp = prevst.mainvp;
	    this.batch = prevst.batch;
		
		this.skin = prevst.skin;
		
		this.serverbridge = prevst.serverbridge;
	}
	
	public void render() {
		frame_delta_time = Gdx.graphics.getDeltaTime();
		
		preRender();
		
		batchRender();
		
		stageRender();
		
		postRenderUpdate();
		
	}
	
	/** Disposes all disposable of the state. Use when closing app. */
	public void dispose() {
		batch.dispose();
	    stage.dispose();
	    skin.dispose();
	    if(serverbridge != null && serverbridge.isConnected()) {
	        serverbridge.closeSocket();
	    }
	}
	
	/**Disposes only those that are not carried on by the next state. Use when changing states. */
	public void disposeHalf() {
		stage.dispose();
	}
	
	/** Triggered when window is resized. {@link Viewport}s are updated. */
	public void resize(int width,int height) {
		mainvp.update(width, height, true);
	}
	
	/** Scene2D stage layout and behavior are configured here. */
	public void createStage(){
    	
    }
    
	/** Stage elements rendering and updates*/
	public void stageRender() {
		stage.act(frame_delta_time);
        stage.draw();
	}
    
    public void batchRender() {
    	
    }
    
    public void preRender() {
    	ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);
    	
        batch.setProjectionMatrix(camera.combined);
        
        /*Because we have set the projection matrix of the batch. */
        camera.update();
    }
    
    public void initStage(Table table) {
    	stage = new Stage(mainvp);
    	Gdx.input.setInputProcessor(stage);
    	table.setFillParent(true);
    	stage.addActor(table);
    }
	
    /**Initializes camera, viewports and batch. */
    public void initCVB() {
    	camera = new OrthographicCamera();
    	camera.setToOrtho(false, 960, 540);
    	mainvp = new ExtendViewport(960, 540, camera);
        batch = new SpriteBatch();
    }
    
    public void postRenderUpdate() {
    	/* Return if there is no connection to server made. */
    	if(serverbridge == null) return;
    	
		tryPolling();
		
		tryTCPMessageDispatch();
		
		tryHandleReturnMessage();
		
	}
	
	public void appendRequest(String request) {
		if(tcp_frame_message == null) tcp_frame_message = new StringBuilder();
		
		ParsingUtils.appendData(request, tcp_frame_message);
	}
	
	public void handleResponse(int start, int end, String response) {
		
	}
	
	/** Poll requests if poll time period elapsed. */
	public void tryPolling() {
		poll_elapsed_time += frame_delta_time;
		if(poll_elapsed_time <= poll_period) return;
		
		poll();
		poll_elapsed_time = 0; 
	}
	
	public void poll() {
		
	}
	
	/**Send tcp message if there is atleast one request to be sent. */
	public void tryTCPMessageDispatch() {
		if(tcp_frame_message != null) {
			addMessage(tcp_frame_message.toString());
			tcp_frame_message = null;
		}
	}
	
	 private void addMessage(String message) {
			if(serverbridge == null || !serverbridge.isConnected()) return;
			
			serverbridge.addMessage(message);
			
		}
	
	/**Handle return message if there is any. */
	public void tryHandleReturnMessage() {
		String return_message = serverbridge.pollReturnMessage();
		if(return_message.length() == 0) return;
		
		handleReturnMessage(return_message);
	}
	
	public void handleReturnMessage(String return_message) {
		for(int i = 0; i < return_message.length();) {
			int start = ParsingUtils.getBeginIndex(i, return_message, '&');
			int end = start + ParsingUtils.parseInt(i, start - 1, return_message);
			
			handleResponse(start, end, return_message);
			
			i = end;
		}
	}
    
    public void changeState(State newstate) {
    	gsm.next_st = newstate;
    	if(serverbridge != null) serverbridge.clearQueues();
    }
    
}
