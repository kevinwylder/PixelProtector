package com.kevin.pixelprotector;

import android.app.*;
import android.content.*;
import android.graphics.*;
import android.os.*;
import android.view.*;
import com.kevin.pixelprotector.*;
import java.util.*;

public class HighScoresView extends SurfaceView implements SurfaceHolder.Callback
{ 
    
	int width;
	int height;
	Activity ctx;
	boolean running=true;
	SurfaceHolder holder;
	Handler handler=new Handler();
	Runnable runnable=new Runnable(){
		@Override
		public void run(){
			if(running){
				stars.add(new Star(width,height));
				Canvas canvas=holder.lockCanvas();
				canvas.drawColor(Color.BLACK);
				int tmp=stars.size();
				for(int i=0;i<tmp;i++){
					stars.get(i).draw(canvas);
					if(stars.get(i).update()){
						stars.remove(i);
						tmp--;
						i--;
					}
				}
				for(int i=0;i<buttons.size();i++){
					buttons.get(i).Draw(canvas);
				}
				updateView(canvas);
				handler.postDelayed(runnable,30);
			}
		}
	};
	List<Star> stars=new ArrayList<Star>();
	List<ButtonHolder> buttons=new ArrayList<ButtonHolder>();
	OnTouchListener listener=new View.OnTouchListener(){
		public boolean onTouch(View p1, MotionEvent p2){
			if(buttons.get(9).isTouched(p2.getX(),p2.getY())){
				Intent i=new Intent(ctx,MainScreenActivity.class);
				ctx.startActivity(i);
				ctx.finish();
			}
			return false;
		}
	};
	
	public HighScoresView(Context context) {
		super(context);
		holder=getHolder();
		holder.addCallback(this);
		this.setOnTouchListener(listener);
		ctx=(Activity)context;
	}
	
	public void surfaceCreated(SurfaceHolder p1){
		handler.post(runnable);
	}

	public void surfaceChanged(SurfaceHolder p1, int p2, int p3, int p4){
		width=p3;
		height=p4;
		SharedPreferences prefs=ctx.getSharedPreferences("prefs",Context.MODE_WORLD_READABLE);
		buttons.clear();
		buttons.add(new ButtonHolder("High Scores",ButtonHolder.ALIGN_CENTER,.1f,width,height,true));
		buttons.add(new ButtonHolder("Classic",ButtonHolder.ALIGN_LEFT,.24f,width,height,false));
		buttons.add(new ButtonHolder(""+prefs.getInt("ClassicHighScore",0),ButtonHolder.ALIGN_LEFT,.33f,width,height,false));
		buttons.add(new ButtonHolder("Survival",ButtonHolder.ALIGN_RIGHT,.24f,width,height,false));
		buttons.add(new ButtonHolder(""+prefs.getInt("SurvivalHighScore",0),ButtonHolder.ALIGN_RIGHT,.33f,width,height,false));
		buttons.add(new ButtonHolder(""+prefs.getInt("GamesPlayed",0),ButtonHolder.ALIGN_LEFT,.7f,width,height,false));
		buttons.add(new ButtonHolder(convert(prefs.getInt("TimePlayed",0)/1000),ButtonHolder.ALIGN_RIGHT,.7f,width,height,false));
		buttons.add(new ButtonHolder("Games Played",ButtonHolder.ALIGN_LEFT,.6f,width,height,false));
		buttons.add(new ButtonHolder("Time Played",ButtonHolder.ALIGN_RIGHT,.6f,width,height,false));
		buttons.add(new ButtonHolder("Return to Main Menu",ButtonHolder.ALIGN_CENTER,.9f,width,height,false));
		buttons.add(new ButtonHolder(prefs.getString("SurvivalName","Nobody"),ButtonHolder.ALIGN_RIGHT,.4f,width,height,false));
		buttons.add(new ButtonHolder(prefs.getString("ClassicName","Nobody"),ButtonHolder.ALIGN_LEFT,.4f,width,height,false));
	}

	public void surfaceDestroyed(SurfaceHolder p1){
		running=false;
	}
	
	public void updateView(Canvas canvas){
		if(running){
			synchronized(holder){
				holder.unlockCanvasAndPost(canvas);
			}
		}
	}
	
	public String convert(int convert){
		int days=(int)(Math.floor(convert/86400));
		int hours=(int)(Math.floor(convert/3600))%24;
		int minutes=(int)(Math.floor(convert/60))%60;
		int seconds=convert%60;
		String builder="";
		if(days>0)builder+=days+"d ";
		if(hours>0)builder+=hours+"h ";
		if(minutes>0)builder+=minutes+"m ";
		if(seconds>0)builder+=seconds+"s";
		return builder;
	}
	
	class Star{

		float x;
		float y;
		float vx;
		float vy;
		float ax;
		float ay;
		float size;
		float vs;
		Paint paint=new Paint();
		float xb;
		float yb;
		
		
		public Star(int width,int height){
			size=0;
			vs=(float)(Math.random()*.2)+.05f;
			x=(width-size)/2;
			y=(height-size)/2;
			double angle=Math.random()*Math.PI*2;
			ax=(float)((.25+Math.random()*.08)*Math.cos(angle));
			ay=(float)((.25+Math.random()*.08)*Math.sin(angle));
			vx=0;
			vy=0;
			xb=width-size;
			yb=height-size;
			paint.setColor(Color.rgb((int)(Math.random()*10)+215,(int)(Math.random()*10)+215,(int)(Math.random()*10)+215));
		}
		
		public boolean update(){
			vx+=ax;
			vy+=ay;
			x+=vx;
			y+=vy;
			size+=vs;
			if(x>xb|y>yb|x<-size|y<-size)return true;
			return false;
		}
		
		public void draw(Canvas canvas){
			canvas.drawRect(x,y,x+size,y+size,paint);
		}
		
	}
	
}
