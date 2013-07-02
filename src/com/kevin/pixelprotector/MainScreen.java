package com.kevin.pixelprotector;

import java.util.ArrayList;
import java.util.List;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

public class MainScreen extends SurfaceView implements SurfaceHolder.Callback{
		
		Handler handler=new Handler();
		Runnable runnable=new Runnable(){
			public void run(){
				Paint paint=new Paint();
				paint.setColor(Color.DKGRAY);
				paint.setStyle(Paint.Style.FILL_AND_STROKE);
				for(int i=0;i<30;i++){
					if(!asteroids.get(i).update()){
						asteroids.remove(i);
						asteroids.add(i,new Asteroid(width,height));
					}
				}
				Rect rect1=new Rect();
				Rect rect2=new Rect();
				rect1.left=(int)(-200+(xyz[0]*-7))*2;
				rect1.top=(int)(-200+(xyz[1]*5))*2;
				rect1.bottom=rect1.top+1200*2;
				rect1.right=rect1.left+1200*2;
				rect2.left=(int)(-200+(xyz[0]*-25))*2;
				rect2.top=(int)(-200+(xyz[1]*23))*2;
				rect2.bottom=rect1.top+1400*2;
				rect2.right=rect1.left+1200*2;
				if(running){
					Canvas canvas=holder.lockCanvas();
					canvas.drawBitmap(background,0,0,paint);
					canvas.drawBitmap(layer1,null,rect1,paint);
					canvas.drawBitmap(layer2,null,rect2,paint);
					for(int i=0;i<30;i++){
						canvas.drawRect(asteroids.get(i).x+(xyz[0]*-8),asteroids.get(i).y+(xyz[1]*8),asteroids.get(i).x+asteroids.get(i).width+(xyz[0]*-8),asteroids.get(i).y+asteroids.get(i).width+(xyz[1]*8),paint);
					}
					for(int i=0;i<5;i++){
						buttons.get(i).Draw(canvas);
					}
					if(running)updateView(canvas);
					if(!send)handler.post(runnable);
					else{
						send=false;
						Intent intent=new Intent();
						if(location==1)intent=new Intent(ctxx,Classic.class);
						else if(location==2)intent=new Intent(ctxx,Survival.class);
						else if(location==3)intent=new Intent(ctxx,HighScores.class);
						else if(location==4)intent=new Intent(ctxx,Settings.class);
						ctxx.startActivity(intent);
						((Activity) ctxx).finish();
					}
				}
			}
		};

		SurfaceHolder holder;
		Boolean running=false;
		List<Asteroid> asteroids;
		Bitmap background;
		Bitmap layer1;
		Bitmap layer2;
		SensorManager sm;
		Sensor accelerometer;
		float[] xyz=new float[]{0f,0f,0f};
		int width;
		int height;
		Boolean send=false;
		int location=0;
		Context ctxx;
		List<ButtonHolder> buttons=new ArrayList<ButtonHolder>();
		// 1=Classic 2=Survival 3=High Scores 4=Settings 0=Exit App
		
		public MainScreen(Context ctx){
			super(ctx);
			ctxx=ctx;
			holder=getHolder();
			holder.addCallback(this);
			asteroids=new ArrayList<Asteroid>();
			setRunning(true);
			sm=(SensorManager)ctx.getSystemService(Context.SENSOR_SERVICE);
			accelerometer=sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
			sm.registerListener(new SensorEventListener(){
				@Override
				public void onAccuracyChanged(Sensor sensor, int accuracy) {
					
				}
				@Override
				public void onSensorChanged(SensorEvent event) {
					for(int i=0;i<3;i++){
							if(xyz[i]-event.values[i]>.5){
								xyz[i]-=0.25f;
							}else if(xyz[i]-event.values[i]<-.5){
								xyz[i]+=0.25f;
							}
					}
				}
			}, accelerometer, 500);
			this.setOnTouchListener(new View.OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					float x=event.getX();
					float y=event.getY();
					Paint paint=new Paint();
					paint.setTextSize(75f);
					if(buttons.get(1).isTouched(x, y)){
						send=true;
						location=1;
					}else if(buttons.get(2).isTouched(x, y)){
						send=true;
						location=2;
					}else if(buttons.get(3).isTouched(x, y)){
						send=true;
						location=3;
					}else if(buttons.get(4).isTouched(x, y)){
						send=true;
						location=4;
					}
					return false;
				}
			});
		}

		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int _width,int _height) {
			width=_width;
			height=_height;
			buttons.clear();
			buttons.add(new ButtonHolder("Pixel Protector",ButtonHolder.ALIGN_CENTER,.1f,width,height,true));
			buttons.add(new ButtonHolder("Classic",ButtonHolder.ALIGN_LEFT,.3f,width,height,false));
			buttons.add(new ButtonHolder("Survival",ButtonHolder.ALIGN_RIGHT,.5f,width,height,false));
			buttons.add(new ButtonHolder("High Scores",ButtonHolder.ALIGN_LEFT,.7f,width,height,false));
			buttons.add(new ButtonHolder("Settings",ButtonHolder.ALIGN_RIGHT,.9f,width,height,false));
			if(width>0&height>0){
				setRunning(true);
			}
		}

		@Override
		public void surfaceCreated(SurfaceHolder _holder) {
			setRunning(true);
		}

		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
			setRunning(false);
		}
		
		public void updateView(Canvas canvas){
			synchronized(holder){
				holder.unlockCanvasAndPost(canvas);
			}
		}
		
		public void setRunning(Boolean _running){
			if(_running&!running){
				background=BitmapFactory.decodeResource(getResources(),R.drawable.background);	
				layer1=BitmapFactory.decodeResource(getResources(),R.drawable.one);
				layer2=BitmapFactory.decodeResource(getResources(),R.drawable.two);
				for(int i=0;i<30;i++){
					asteroids.add(i,new Asteroid(width,height));
				}
				handler.postDelayed(runnable,500);
			}else if(!_running){
				background.recycle();
				layer1.recycle();
				layer2.recycle();
			}
			running=_running;
		}
		
	}

