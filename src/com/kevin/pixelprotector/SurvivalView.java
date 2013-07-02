package com.kevin.pixelprotector;

import java.util.ArrayList;
import java.util.List;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

public class SurvivalView extends GameView{
	
	int counter;
	int marker=750;
	int vmarker=725;
	int bgcolor;
	int kill=0;
	int killCount=0;
	Boolean inverse=false;
	int inverseColor;

	public SurvivalView(Context context) {
		super(context);
		ctx=(Activity)context;
		holder=getHolder();
		this.setOnTouchListener(listener);
		score=0;
		asteroids=new ArrayList<Asteroid>();
		SharedPreferences prefs=ctx.getSharedPreferences("prefs",Context.MODE_WORLD_READABLE);
		if(prefs.getBoolean("SurvivalShowHelp", true)){
			prefs.edit().putBoolean("SurvivalShowHelp", false).commit();
			Intent intent=new Intent(ctx,HelpView.class);
			intent.putExtra("classic", false);
			ctx.startActivity(intent);
			ctx.finish();
		}
		fancyGraphics=prefs.getBoolean("3DRendering", false);
		sensitivity=prefs.getFloat("movementSensitivity", 1);
		highscore=prefs.getInt("SurvivalHighScore", 0);
		if(fancyGraphics)asteroidPaint.setStyle(Paint.Style.STROKE);
		else asteroidPaint.setStyle(Paint.Style.FILL);
		asteroidPaint.setStrokeWidth(4);
		asteroidPaint.setColor(prefs.getInt("AsteroidColor", Color.LTGRAY));
		if(fancyGraphics)playerPaint.setStyle(Paint.Style.STROKE);
		else playerPaint.setStyle(Paint.Style.FILL);
		playerPaint.setStrokeWidth(4);
		playerPaint.setColor(prefs.getInt("PlayerColor", Color.RED));
		playerWidth=(int)((25f/1900f)*(float)(height));
		textPaint.setTextSize(59f);
		textPaint.setColor(Color.WHITE);
		bgcolor=prefs.getInt("BackgroundColor", Color.BLACK);
		inverseColor=prefs.getInt("FlashColor", Color.DKGRAY);
		if(fancyGraphics)depth=ctx.getSharedPreferences("prefs",Context.MODE_WORLD_READABLE).getFloat("3Depth", 0.025f);
		holder.addCallback(this);
	}
	
	public void drawGame(Canvas canvas){
		handleGame(true);
		for(int i=0;i<asteroids.size();i++){
			if(!asteroids.get(i).update()){
				asteroids.remove(i);
				if(kill==0)asteroids.add(i,new Asteroid(width,height));
				if(kill>0){
					kill--;
					i--;
				}
			}else{
				if(asteroids.get(i).x+asteroids.get(i).width>player.x-playerWidth&asteroids.get(i).x<player.x+playerWidth&asteroids.get(i).y+asteroids.get(i).width>player.y-playerWidth&asteroids.get(i).y<player.y+playerWidth){
					setGameOver();
				}
			}
		}
		counter++;
		if(counter%30==29){
			score++;
		}
		if(counter%700==699){
			asteroids.add(new Asteroid(width,height));
		}
		if(!paused&!gameOver){
			if(!inverse)canvas.drawColor(bgcolor);
			else canvas.drawColor(inverseColor);
			for(int i=0;i<asteroids.size();i++){
				if(fancyGraphics){
					draw3D(canvas,asteroids.get(i).width,asteroids.get(i).x,asteroids.get(i).y,asteroidPaint);
				}else{
					canvas.drawRect(asteroids.get(i).x,asteroids.get(i).y,asteroids.get(i).x+asteroids.get(i).width,asteroids.get(i).y+asteroids.get(i).width,asteroidPaint);
				}
			}
			if(fancyGraphics){
				draw3D(canvas, playerWidth*2, player.x-playerWidth, player.y-playerWidth, playerPaint);
			}else{
				canvas.drawRect(player.x-playerWidth,player.y-playerWidth,player.x+playerWidth,player.y+playerWidth,playerPaint);
			}
			if(score>highscore)textPaint.setColor(Color.rgb(255, 170, 0));
			canvas.drawText(" "+score,20,height-79,textPaint);
		}else{
			canvas.drawColor(Color.BLACK);
			for(int i=0;i<buttons.size();i++){
				buttons.get(i).Draw(canvas);
			}
		}
	}
	
	public void restart(){
		running=false;
		asteroids.clear();
		for(int i=0;i<15;i++){
			asteroids.add(new Asteroid(width,height));
		}
		score=0;
		counter=0;
		marker=750;
		player.set(width/2,height/2);
		running=true;
		gameOver=false;
		inverse=false;
		killCount=0;
		textPaint.setColor(Color.WHITE);
		buttons.clear();
		paused=false;
		handler.post(runnable);
	}
	
	public void initialAsteroids(){
		if(asteroids.size()==0){
			for(int i=0;i<15;i++){
				asteroids.add(new Asteroid(width,height));
			}
		}
	}
	
	public void handleGame(boolean first){
		if(counter==marker){
			marker+=vmarker;
			if(vmarker>299)vmarker-=25;
			inverse=false;
			int type=(int)(Math.random()*6);
			Log.i("type", "type="+type);
			if(type==0){
				for(int i=0;i<5;i++){
					asteroids.add(new Asteroid(width,height));
				}
			}else if(type==1){
				int side=(int)(Math.random()*4);
				for(int i=0;i<30;i++){
					asteroids.add(new Asteroid(width,height,side));
					kill++;
				}
			}else if(type==2){
				for(int i=0;i<asteroids.size();i++){
					Asteroid asteroid=asteroids.get(i);
					asteroids.set(i, new Asteroid(asteroid.x,asteroid.y,asteroid.vx*2f,asteroid.vy*2f,asteroid.width,width,height));
				}
			}else if(type==3){
				for(int i=0;i<asteroids.size();i++){
					Asteroid asteroid=asteroids.get(i);
					asteroids.set(i, new Asteroid(asteroid.x,asteroid.y,asteroid.vy,asteroid.vx,asteroid.width,width,height));
				}
			}
			else if(type==4){
				for(int i=0;i<asteroids.size();i++){
					Asteroid asteroid=asteroids.get(i);
					asteroids.set(i, new Asteroid(asteroid.x,asteroid.y,asteroid.vx*-1,asteroid.vy*-1,asteroid.width,width,height));
				}
			}else if(type==5){
				List<Asteroid> list=new ArrayList<Asteroid>();
				for(int i=0;i<asteroids.size();i++){
					if(Math.random()>.75){
						Asteroid asteroid=asteroids.get(i);
						int pieces=2+(int)(Math.random()*3);
						int size=(int)(asteroid.width/pieces);
						for(int a=pieces;a>0;a--){
							double angle=Math.random()*Math.PI*2;
							double mag=Math.random()*2;
							float vx=(float)(Math.cos(angle)*mag);
							float vy=(float)(Math.sin(angle)*mag);
							list.add(new Asteroid(asteroid.x,asteroid.y,vx+asteroid.vx,vy+asteroid.vy,size,width,height));
							killCount++;
						}
						asteroids.remove(asteroid);
						i--;
					}
				}
				asteroids.addAll(list);
			}
			if(score>550&first)handleGame(false);
		}else if(counter==marker-25)inverse=true;
	}
	
	public void setHighScore(){
		SharedPreferences prefs=ctx.getSharedPreferences("prefs",Context.MODE_WORLD_READABLE);
		prefs.edit().putInt("SurvivalHighScore", score).commit();
	}
	
	public void setHighScoreName(String nmString) {
		SharedPreferences prefs=ctx.getSharedPreferences("prefs",Context.MODE_WORLD_READABLE);
		prefs.edit().putString("SurvivalName", nmString).commit();
	}
	
}
