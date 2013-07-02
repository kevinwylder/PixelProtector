package com.kevin.pixelprotector;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class ButtonHolder {
	
	public static int ALIGN_LEFT=0;
	public static int ALIGN_CENTER=1;
	public static int ALIGN_RIGHT=2;
	
	Paint paint=new Paint();
	String buttonText;
	float x;
	float y;
	float buttonWidth;
	float buttonHeight;
	int boundry;
	
	public ButtonHolder(String text,int position,float percent,int width,int height,boolean title){
		float textSize=(float)(75f/1920f)*height;
		if(title)textSize=(float)(130f/1920f)*height;
		paint.setTextSize(textSize);
		paint.setColor(Color.rgb(220, 220, 220));
		buttonText=text;
		buttonWidth=paint.measureText(text);
		buttonHeight=textSize;
		if(position==0)x=30;
		if(position==1)x=(width-buttonWidth)/2;
		if(position==2)x=width-30-buttonWidth;
		y=percent*height;
		boundry=(int)(width*.08);
	}
	
	public Boolean isTouched(float _x,float _y){
		if(_x>x-boundry&_x<x+buttonWidth+boundry&_y>y-boundry&_y<y+buttonHeight+boundry)return true;
		else return false;
	}
	
	public void Draw(Canvas canvas){
		canvas.drawText(buttonText, x, y, paint);
	}
	
	public void setPaint(Paint _paint){
		paint.set(_paint);
	}
	
}
