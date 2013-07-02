package com.kevin.pixelprotector;

import java.util.ArrayList;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.preference.PreferenceManager;
import android.util.Log;


public class ClassicView extends GameView{
	
	PointF goal=new PointF();
	float goalWidth;
	Paint goalPaint=new Paint();
	int bgcolor;
	
	public ClassicView(Context context) {
		super(context);
		ctx=(Activity)context;
		holder=getHolder();
		this.setOnTouchListener(listener);
		asteroids=new ArrayList<Asteroid>();
		SharedPreferences prefs=ctx.getSharedPreferences("prefs",Context.MODE_WORLD_READABLE);
		if(prefs.getBoolean("ClassicShowHelp", true)){
			prefs.edit().putBoolean("ClassicShowHelp", false).commit();
			Intent intent=new Intent(ctx,HelpView.class);
			intent.putExtra("classic", true);
			ctx.startActivity(intent);
			ctx.finish();
		}
		sensitivity=prefs.getFloat("movementSensitivity", 1);
		fancyGraphics=prefs.getBoolean("3DRendering", false);
		highscore=prefs.getInt("ClassicHighScore", 0);
		if(fancyGraphics)asteroidPaint.setStyle(Paint.Style.STROKE);
		else asteroidPaint.setStyle(Paint.Style.FILL);
		asteroidPaint.setColor(prefs.getInt("AsteroidColor", Color.LTGRAY));
		asteroidPaint.setStrokeWidth(4);
		if(fancyGraphics)playerPaint.setStyle(Paint.Style.STROKE);
		else playerPaint.setStyle(Paint.Style.FILL);
		playerPaint.setColor(prefs.getInt("PlayerColor", Color.RED));
		playerPaint.setStrokeWidth(4);
		if(fancyGraphics)goalPaint.setStyle(Paint.Style.STROKE);
		else goalPaint.setStyle(Paint.Style.FILL);
		goalPaint.setColor(prefs.getInt("GoalColor", Color.GREEN));
		goalPaint.setStrokeWidth(4);
		if(fancyGraphics)depth=ctx.getSharedPreferences("prefs",Context.MODE_WORLD_READABLE).getFloat("3Depth", 0.025f);
		textPaint.setTextSize(59f);
		textPaint.setColor(Color.WHITE);
		bgcolor=prefs.getInt("BackgroundColor", Color.BLACK);
		holder.addCallback(this);
		
	}

	public void goalRecieved() {
		goal.set((float)(Math.random()*(width-(goalWidth*7f)))+(goalWidth*3f),(float)(Math.random()*(height-(goalWidth*7f)))+(goalWidth*3f));
		score++;
		if(Math.random()>.7){
			asteroids.add(new Asteroid(width,height));
			Log.d("asteroid",""+asteroids.size());
		}
	}
	
	public void drawGame(Canvas canvas){
		for(int i=0;i<asteroids.size();i++){
			if(!asteroids.get(i).update()){
				asteroids.remove(i);
				asteroids.add(i,new Asteroid(width,height));
			}
			if(asteroids.get(i).x+asteroids.get(i).width>player.x-playerWidth&asteroids.get(i).x<player.x+playerWidth&asteroids.get(i).y+asteroids.get(i).width>player.y-playerWidth&asteroids.get(i).y<player.y+playerWidth){
				setGameOver();
			}
		}
		if(goal.x+goalWidth>player.x-playerWidth&goal.x<player.x+playerWidth&goal.y+goalWidth>player.y-playerWidth&goal.y<player.y+playerWidth)goalRecieved();
		if(!paused&!gameOver){
			canvas.drawColor(bgcolor);
			for(int i=0;i<asteroids.size();i++){	
				if(fancyGraphics){
					draw3D(canvas,asteroids.get(i).width,asteroids.get(i).x,asteroids.get(i).y,asteroidPaint);
				}else{
					canvas.drawRect(asteroids.get(i).x,asteroids.get(i).y,asteroids.get(i).x+asteroids.get(i).width,asteroids.get(i).y+asteroids.get(i).width,asteroidPaint);
				}
			}
			if(fancyGraphics){
				draw3D(canvas, playerWidth*2, player.x-playerWidth, player.y-playerWidth, playerPaint);
				draw3D(canvas, goalWidth, goal.x, goal.y, goalPaint);
			}else{
				canvas.drawRect(player.x-playerWidth,player.y-playerWidth,player.x+playerWidth,player.y+playerWidth,playerPaint);
				canvas.drawRect(goal.x,goal.y,goal.x+goalWidth,goal.y+goalWidth,goalPaint);	
			}
			
			if(score>highscore)textPaint.setColor(Color.rgb(255, 170, 0));
			canvas.drawText(""+score,20,height-79,textPaint);
		}else{
			canvas.drawColor(Color.BLACK);
		}
		for(int i=0;i<buttons.size();i++){
			buttons.get(i).Draw(canvas);
		}
	}
	
	public void restart(){
		running=false;
		asteroids.clear();
		for(int i=0;i<30;i++){
			asteroids.add(new Asteroid(width,height));
		}
		goalRecieved();
		score=0;
		player.set(width/2,height/2);
		running=true;
		gameOver=false;
		textPaint.setColor(Color.WHITE);
		paused=false;
		buttons.clear();
		handler.post(runnable);
	}
	
	public void postSetUp(){
		playerWidth=(int)((25f/1900f)*(float)(height));
		goalWidth=2.6f*playerWidth;
		goal.set((float)(Math.random()*(width-(goalWidth*7f)))+(goalWidth*3f),(float)(Math.random()*(height-(goalWidth*7f)))+(goalWidth*3f));
	}
	
	public void initialAsteroids(){
		if(asteroids.size()==0){
			for(int i=0;i<30;i++){
				asteroids.add(new Asteroid(width,height));
			}
		}
	}
	
	public void setHighScore(){
		SharedPreferences prefs=ctx.getSharedPreferences("prefs",Context.MODE_WORLD_READABLE);
		prefs.edit().putInt("ClassicHighScore", score).commit();
	}
	
	public void setHighScoreName(String nmString) {
		SharedPreferences prefs=ctx.getSharedPreferences("prefs",Context.MODE_WORLD_READABLE);
		prefs.edit().putString("ClassicName", nmString).commit();
	}
	
}
