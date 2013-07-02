package com.kevin.pixelprotector;

import java.util.ArrayList;
import java.util.List;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.InputFilter;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.EditText;

public class GameView extends SurfaceView implements SurfaceHolder.Callback{
	
	SurfaceHolder holder;
	int width;
	int height;
	int counter=0;
	int highscore;
	float depth;
	Boolean cancelTouchFlag=false;
	Boolean highScore=false;
	Activity ctx;
	List<Asteroid> asteroids;
	PointF finger=new PointF();
	PointF player=new PointF();
	float sensitivity;
	int playerWidth;
	Paint playerPaint=new Paint();
	Paint asteroidPaint=new Paint();
	Paint textPaint=new Paint();
	long time;
	Handler handler=new Handler();
	Runnable runnable=new Runnable(){
		public void run(){
			if(running){
				Canvas canvas=holder.lockCanvas();
				drawGame(canvas);
				updateView(canvas);
				counter++;
			}	
			if(!gameOver&!paused)handler.postDelayed(runnable,7);
			else if(gameOver){
				SharedPreferences prefs=ctx.getSharedPreferences("prefs",Context.MODE_WORLD_READABLE);
				if(highscore<score){
					Canvas canvas=holder.lockCanvas();
					canvas.drawColor(Color.BLACK);
					buttons.add(new ButtonHolder("New High Score!",ButtonHolder.ALIGN_CENTER,.35f,width,height,false));
					buttons.get(0).buttonText="Game Over";
					for(int i=0;i<buttons.size();i++){
						buttons.get(i).Draw(canvas);
					}
					updateView(canvas);
					AlertDialog.Builder ad=new AlertDialog.Builder(ctx,AlertDialog.THEME_DEVICE_DEFAULT_DARK);
					final EditText name=new EditText(ctx);
					name.setFilters(new InputFilter[]{new InputFilter.LengthFilter(12)});
					name.setSelectAllOnFocus(true);
					name.setHint("Name");
					ad.setCancelable(false);
					ad.setTitle("Enter Your Name");
					ad.setView(name);
					ad.setPositiveButton("Use This Name", new DialogInterface.OnClickListener(){
						@Override
						public void onClick(DialogInterface dialog, int which) {
							String nmString="";
							if(name.length()>0)nmString=name.getText().toString();
							setHighScoreName(nmString);
						}
					});
					ad.setNegativeButton("Use Old Name",null);
					ad.create().show();
					setHighScore();
				}
				gamesIncrement();
				prefs.edit().putInt("TimePlayed", (int) (prefs.getInt("TimePlayed",0)+(System.currentTimeMillis()-time))).commit();
				time=System.currentTimeMillis();
			}
		}
	};
	boolean paused=false;
	boolean running=true;
	boolean gameOver=false;
	boolean fancyGraphics=false;
	int score=0;
	List<ButtonHolder> buttons=new ArrayList<ButtonHolder>();
	OnTouchListener listener=new View.OnTouchListener() {		
		public Boolean cancelled=false;
		
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if(cancelTouchFlag){
				cancelled=true;
				cancelTouchFlag=false;
			}
			if(event.getPointerCount()==1&event.getAction()==MotionEvent.ACTION_DOWN)cancelled=false;
			if(!gameOver&!paused){
				if(event.getPointerCount()==2&!cancelled){
					setPaused();
					cancelled=true;
				}
				if(event.getAction()==MotionEvent.ACTION_MOVE&!cancelled){
					player.x-=sensitivity*(finger.x-event.getX());
					player.y-=sensitivity*(finger.y-event.getY());
					if(player.x<playerWidth)player.x=playerWidth;
					if(player.y<playerWidth)player.y=playerWidth;
					if(player.x>width-playerWidth)player.x=width-playerWidth;
					if(player.y>height-playerWidth)player.y=height-playerWidth;
					finger.set(event.getX(), event.getY());
				}else if(event.getAction()==MotionEvent.ACTION_DOWN&!cancelled){
					finger.set(event.getX(0),event.getY(0));
				}
			}else if(gameOver&!cancelled){
				if(buttons.get(2).isTouched(event.getX(),event.getY())){
					Intent i=new Intent(ctx,MainScreenActivity.class);
					ctx.startActivity(i);
					ctx.finish();
				}else if(buttons.get(3).isTouched(event.getX(),event.getY())){
					restart();
					cancelled=true;
				}
			}else if(paused&!cancelled){
				if(event.getPointerCount()==2|buttons.get(1).isTouched(event.getX(), event.getY())){
					running=true;
					paused=false;
					cancelled=true;
					handler.post(runnable);
					buttons.clear();
				}else if(buttons.get(2).isTouched(event.getX(), event.getY())){
					restart();
					cancelled=true;
					gamesIncrement();
				}else if(buttons.get(3).isTouched(event.getX(), event.getY())){
					Intent i=new Intent(ctx,MainScreenActivity.class);
					ctx.startActivity(i);
					ctx.finish();
				}
			}
			return true;
		}
	};
	
	public GameView(Context context) {
		super(context);
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int _width, int _height) {
		width=_width;
		height=_height;
		player.set(width/2, height/2);
		postSetUp();
		initialAsteroids();
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		time=System.currentTimeMillis();
		handler.post(runnable);
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		running=false;
	}
	
	public void updateView(Canvas canvas){
		synchronized(holder){
			holder.unlockCanvasAndPost(canvas);
		}
	}
	
	public void drawGame(Canvas canvas){
		
	}
	
	public void setGameOver(){
		gameOver=true;
		buttons.clear();
		buttons.add(new ButtonHolder("Game Over :(",ButtonHolder.ALIGN_CENTER,.3f,width,height,true));
		buttons.add(new ButtonHolder("Score: "+score,ButtonHolder.ALIGN_CENTER,.5f,width,height,false));
		buttons.add(new ButtonHolder("Main Menu",ButtonHolder.ALIGN_LEFT,.9f,width,height,false));
		buttons.add(new ButtonHolder("Restart",ButtonHolder.ALIGN_RIGHT,.9f,width,height,false));
		cancelTouchFlag=true;
	}
	
	public void setPaused(){
		paused=true;
		buttons.clear();
		buttons.add(new ButtonHolder("Paused",ButtonHolder.ALIGN_CENTER,.3f,width,height,true));
		buttons.add(new ButtonHolder("Continue",ButtonHolder.ALIGN_LEFT,.5f,width,height,false));
		buttons.add(new ButtonHolder("Restart",ButtonHolder.ALIGN_RIGHT,.7f,width,height,false));
		buttons.add(new ButtonHolder("Quit",ButtonHolder.ALIGN_LEFT,.9f,width,height,false));
	}

	public void restart(){
		
	}
	
	public void postSetUp(){
		playerWidth=(int)((25f/1900f)*(float)(height));
	}
	
	public void initialAsteroids(){
		
	}
	
	public void gamesIncrement(){
		SharedPreferences prefs=ctx.getSharedPreferences("prefs",Context.MODE_WORLD_READABLE);
		prefs.edit().putInt("GamesPlayed",prefs.getInt("GamesPlayed", 0)+1).commit();
	}
	
	public void setHighScore(){
		
	}
	
	public void setHighScoreName(String nmString) {
		
	}
	
	public void draw3D(Canvas canvas,float wid,float x,float y,Paint paint){
		float[] verts1;
		float[] verts2;
		float sizeModulous=wid/50f;

		if(x+wid<(width)/2){
			float a=(width)/2-(x+wid);
			float b1=(height)/2-(y+wid);
			float b2=(height)/2-(y);
			double angle1=Math.atan2(b1, a);
			double angle2=Math.atan2(b2, a);
			verts1=new float[]{
					x+wid,y+wid,
					(float)(a*depth*sizeModulous)+(x+wid),(float)(a*Math.tan(angle1)*depth*sizeModulous)+(y+wid),
					x+wid,y,
					(float)(a*depth*sizeModulous)+(x+wid),(float)(a*Math.tan(angle2)*depth*sizeModulous)+(y),
					(float)(a*depth*sizeModulous)+(x+wid),(float)(a*Math.tan(angle1)*depth*sizeModulous)+(y+wid),
					(float)(a*depth*sizeModulous)+(x+wid),(float)(a*Math.tan(angle2)*depth*sizeModulous)+(y),
			};
		}else if(x>(width)/2){
			float a=(width)/2-(x);
			float b1=(height)/2-(y+wid);
			float b2=(height)/2-(y);
			double angle1=Math.atan2(b1, a);
			double angle2=Math.atan2(b2, a);
			verts1=new float[]{
					x,y+wid,
					(float)(a*depth*sizeModulous)+(x),(float)(a*Math.tan(angle1)*depth*sizeModulous)+(y+wid),
					x,y,
					(float)(a*depth*sizeModulous)+(x),(float)(a*Math.tan(angle2)*depth*sizeModulous)+(y),
					(float)(a*depth*sizeModulous)+(x),(float)(a*Math.tan(angle1)*depth*sizeModulous)+(y+wid),
					(float)(a*depth*sizeModulous)+(x),(float)(a*Math.tan(angle2)*depth*sizeModulous)+(y),
			};
		}else{
			verts1=new float[]{
					0,0,
					0,0
			};
		}
		
		if(y+wid<(height)/2){
			float a=(height)/2-(y+wid);
			float b1=(width)/2-(x);
			float b2=(width)/2-(x+wid);
			double angle2=Math.atan2(b1, a);
			double angle1=Math.atan2(b2, a);
			verts2=new float[]{
					x+wid,y+wid,
					(float)(a*Math.tan(angle1)*depth*sizeModulous)+(x+wid),(float)(a*depth*sizeModulous)+(y+wid),
					x,y+wid,
					(float)(a*Math.tan(angle2)*depth*sizeModulous)+(x),(float)(a*depth*sizeModulous)+(y+wid),
					(float)(a*Math.tan(angle1)*depth*sizeModulous)+(x+wid),(float)(a*depth*sizeModulous)+(y+wid),
					(float)(a*Math.tan(angle2)*depth*sizeModulous)+(x),(float)(a*depth*sizeModulous)+(y+wid),
			};
		}else if(y>(height)/2){
			float a=(height)/2-(y);
			float b1=(width)/2-(x);
			float b2=(width)/2-(x+wid);
			double angle2=Math.atan2(b1, a);
			double angle1=Math.atan2(b2, a);
			verts2=new float[]{
					x+wid,y,
					(float)(a*Math.tan(angle1)*depth*sizeModulous)+(x+wid),(float)(a*depth*sizeModulous)+(y),
					x,y,
					(float)(a*Math.tan(angle2)*depth*sizeModulous)+(x),(float)(a*depth*sizeModulous)+(y),
					(float)(a*Math.tan(angle1)*depth*sizeModulous)+(x+wid),(float)(a*depth*sizeModulous)+(y),
					(float)(a*Math.tan(angle2)*depth*sizeModulous)+(x),(float)(a*depth*sizeModulous)+(y),
			};
		}else{
			verts2=new float[]{
					0,0,
					0,0
			};
		}
		canvas.drawRect(x,y,x+wid,y+wid, paint);
		canvas.drawLines(verts1, paint);
		canvas.drawLines(verts2, paint);
	}
	
}
