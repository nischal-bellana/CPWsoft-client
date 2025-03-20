package com.game_states;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.utils.ParsingUtils;

public class State {
	//fields
	protected GameStateManager gsm;
	
	protected OrthographicCamera camera;
    protected SpriteBatch batch;
	protected Viewport mainvp;
	
	protected Skin skin;
	
	protected Stage stage;
	
	protected ServerBridge serverbridge;
	protected float poll_breaktime = 0;
	protected StringBuilder message;
	
	public State() {
		
	}
	
	public State(State prevst) {
		this.gsm = prevst.gsm;
		
		this.camera = prevst.camera;
	    this.batch = prevst.batch;
		this.mainvp = prevst.mainvp;
		
		this.skin = prevst.skin;
		
		this.serverbridge = prevst.serverbridge;
	}
	
	public void create() {
		initCamera();
    	
		skin = new Skin(Gdx.files.internal("packimgs//skin.json"));
        
        createStage();
        
        message = new StringBuilder();
	}
	
	public void render() {
		float delta = Gdx.graphics.getDeltaTime();
		poll_breaktime += delta;
		
		preRender();
		
		batchRender();
		
		stageRender(delta);
		
		postRenderUpdate();
		
	}
	
	public void dispose() {
		if(gsm.next_st==null) {
			batch.dispose();
	        stage.dispose();
	        skin.dispose();
	        if(serverbridge != null && serverbridge.isConnected()) {
	        	serverbridge.closeSocket();
	        }
	        return;
		}
		stage.dispose();
	}
	
	public void resize(int width,int height) {
		mainvp.update(width, height);
	}
	
	protected void createStage(){
    	
    	
    }
    
	protected void stageRender(float delta) {
		stage.act(delta);
        stage.draw();
	}
    
    protected void batchRender() {
    	
    }
    
    protected void preRender() {
    	ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);
        batch.setProjectionMatrix(camera.combined);
        camera.update();
    }
    
    protected void initStage(Table table) {
    	stage = new Stage(mainvp);
    	Gdx.input.setInputProcessor(stage);
    	table.setFillParent(true);
    	stage.addActor(table);
    }
	
    protected void initCamera() {
    	camera = new OrthographicCamera();
    	camera.setToOrtho(false, 960, 540);
    	mainvp = new FitViewport(960, 540, camera);
        batch = new SpriteBatch();
    }
    
    protected void postRenderUpdate() {
		// TODO Auto-generated method stub
    	
    	if(serverbridge == null) return;
    	
		if(poll_breaktime > 0.3f) {
			polling();
			poll_breaktime = 0;
		}
		
		if(message.length() > 0) {
			addMessage(message.toString());
			
			message = new StringBuilder();
		}
		
		String return_message = serverbridge.pollReturnMessage();
		
		if(return_message.length() > 0) {
			for(int i = 0; i < return_message.length();) {
				int start = ParsingUtils.getBeginIndex(i, return_message, '&');
				int end = start + ParsingUtils.parseInt(i, start - 1, return_message);
				
				handleResponse(start, end, return_message);
				
				i = end;
			}
			
		}
		
	}
	
	protected void appendRequest(String request) {
		ParsingUtils.appendData(request, message);
	}
	
	protected void handleResponse(int start, int end, String response) {
		
	}
	
	protected void polling() {
		
	}
    
    private void addMessage(String message) {
		if(serverbridge == null || !serverbridge.isConnected()) return;
		
		serverbridge.addMessage(message);
		
	}
    
    protected void changeState(State newstate) {
    	gsm.next_st = newstate;
    	if(serverbridge != null) serverbridge.clearQueues();
    }
    
}
