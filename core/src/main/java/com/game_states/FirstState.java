package com.game_states;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class FirstState extends State{
	
    FirstState(){
    	create();
    }
    
    FirstState(State prevst){
    	this.gsm = prevst.gsm;
		
		this.camera = prevst.camera;
	    this.batch = prevst.batch;
		this.mainvp = prevst.mainvp;
		
		this.skin = prevst.skin;
		
		createStage();
    }
    
    @Override
    public void create() {
    	super.create();
    }

	@Override
    public void render() {
		super.render();
    }

    @Override
    public void dispose() {
    	super.dispose();
    }

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
		mainvp.update(width, height);
	}
    
	@Override
    protected void createStage(){
    	Table table = new Table();
    	table.setBackground(skin.getDrawable("back"));
    	initStage(table);
    	
    	Label username = new Label("Username", skin);
    	table.row();
    	table.add(username).padTop(18);
    	TextField name = new TextField("",skin);
    	table.row();
    	table.add(name).height(18).padTop(18);
    	
    	Button button = new Button(skin);
    	button.addListener(new ChangeListener() {

			@Override
			public void changed(ChangeEvent event, Actor actor) {
				// TODO Auto-generated method stub
				System.out.println("Connecting...");
				String usernametext = name.getText();
				usernametext = usernametext.trim();
				if(validateUsername(usernametext) && connectServer(usernametext))
				gsm.next_st = new HomeState(FirstState.this, usernametext);
			}
    		
    	});
    	table.row();
    	table.add(button).center();
    	
    	Label label = new Label("Connect to Server",skin);
    	table.row();
    	table.add(label).center().padTop(18);
    	
    	TextField tf = new TextField("108.123.34.5",skin);
    	table.row();
    	table.add(tf).height(18).padTop(18);
    	
    	table.setTouchable(Touchable.enabled);
    	table.addListener(new ClickListener() {

			@Override
			public void clicked(InputEvent event, float x, float y) {
				// TODO Auto-generated method stub
				if(stage.getKeyboardFocus() == null) return;
				
				if(event.getTarget()!= stage.getKeyboardFocus()) {
					stage.unfocus(stage.getKeyboardFocus());
				}
			}
    	});
    	
    } 
    
    @Override
	protected void stageRender(float delta) {
		// TODO Auto-generated method stub
    	super.stageRender(delta);
	}
	
	@Override
	protected void preRender() {
		// TODO Auto-generated method stub
		super.preRender();
	}
	
	public boolean validateUsername(String username) {
		if(username.length() <= 5 || username.length() > 14 || Character.isDigit(username.charAt(0))) return false;
		for(int i = 0; i < username.length(); i++) {
			char c = username.charAt(i);
			if(c != '-' && c != '_' && !Character.isAlphabetic(c) && !Character.isDigit(c)) 
				return false;
		}
		return true;
	}

	private boolean connectServer(String name) {
    	try {  
    		if(server!=null && !server.isClosed()) server.close();
    		
			server = new Socket("localhost",1433);
			
			in = new BufferedReader(new InputStreamReader(server.getInputStream())); 
			out = new PrintWriter(server.getOutputStream(), true);
			out.println(name);
			String res = in.readLine();
			
			if(res==null || res.equals("f")) return false;
			
			return true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
    }
    
}
