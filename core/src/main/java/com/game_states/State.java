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

public class State {
	//fields
	protected GameStateManager gsm;
	
	protected OrthographicCamera camera;
    protected SpriteBatch batch;
	protected Viewport mainvp;
	
	protected Skin skin;
	
	protected Stage stage;
	
	protected Socket server;
	protected BufferedReader in;
	protected PrintWriter out;
	
	protected void create() {
		initCamera();
    	
		skin = new Skin(Gdx.files.internal("packimgs//skin.json"));
        
        createStage();
	}
	
	protected void render() {
		float delta = Gdx.graphics.getDeltaTime();
		
		preRender();
        
        batchRender();
        
        stageRender(delta);
	}
	
	protected void dispose() {
		if(gsm.next_st==null) {
			batch.dispose();
	        stage.dispose();
	        skin.dispose();
	        if(server != null && !server.isClosed()) {
				try {
					out.println("cc");
					server.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        }
	        return;
		}
		stage.dispose();
	}
	
	protected void resize(int width,int height) {
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
    
    public int toNum(String s) {
		int n = 0;
		
		for(int i=0; i<s.length(); i++) {
			char c = s.charAt(i);
			if(Character.isDigit(c)) {
				n *= 10;
				n += c - '0';
			}
		}
		
		return n;
		
	}
    
}
