package com.kevin.pixelprotector;

import android.graphics.Color;
import android.graphics.Paint;


public class Asteroid {
	
	public Asteroid(int canvasWidth, int canvasHeight){
		int side=(int)(Math.random()*4);
		double angle=0;
		if(side==0){
			x=(float)Math.random()*canvasWidth;
			y=-100;
			angle=Math.PI*1/2;
		}else if(side==1){
			x=canvasWidth+100;
			y=(float)Math.random()*canvasHeight;
			angle=Math.PI;
		}else if(side==2){
			x=(float)Math.random()*canvasWidth;
			y=canvasHeight+100;
			angle=Math.PI*3/2;
		}else if(side==3){
			x=-100;
			y=(float)Math.random()*canvasHeight;
			angle=0;
		}
		double velocity=(Math.random()*(4f/1200f)*(float)canvasHeight)+((1f/1200f)*(float)canvasHeight);
		angle+=(Math.PI*Math.random())-(Math.PI/2);
		vx=(float)(velocity*Math.cos(angle));
		vy=(float)(velocity*Math.sin(angle));
		width=(int)(((float)(Math.ceil(Math.random()*40)+30)/1800f)*canvasHeight);
		xBoundry=canvasWidth+101;
		yBoundry=canvasHeight+101;
		shade.setColor(Color.CYAN);
	}
	
	public Asteroid(int canvasWidth,int canvasHeight,int side){
		double angle=0;
		if(side==0){
			x=(float)Math.random()*canvasWidth;
			y=-100;
			angle=Math.PI*1/2;
		}else if(side==1){
			x=canvasWidth+100;
			y=(float)Math.random()*canvasHeight;
			angle=Math.PI;
		}else if(side==2){
			x=(float)Math.random()*canvasWidth;
			y=canvasHeight+100;
			angle=Math.PI*3/2;
		}else if(side==3){
			x=-100;
			y=(float)Math.random()*canvasHeight;
			angle=0;
		}
		double velocity=(Math.random()*(4f/1200f)*(float)canvasHeight)+((1f/1200f)*(float)canvasHeight);
		angle+=(Math.PI*Math.random())-(Math.PI/2);
		vx=(float)(velocity*Math.cos(angle));
		vy=(float)(velocity*Math.sin(angle));
		width=(int)(((float)(Math.ceil(Math.random()*40)+30)/1800f)*canvasHeight);
		xBoundry=canvasWidth+101;
		yBoundry=canvasHeight+101;
		shade.setColor(Color.CYAN);
	}
	
	public Asteroid(float _x,float _y,float _vx,float _vy, int _width,int canvasWidth,int canvasHeight){
		x=_x;
		y=_y;
		vx=_vx;
		vy=_vy;
		width=_width;
		xBoundry=canvasWidth+101;
		yBoundry=canvasHeight+101;
		shade.setColor(Color.CYAN);
	}
	
	public boolean update(){
		x+=vx;
		y+=vy;
		if(fancyGraphics){
			if(x+width<(xBoundry-101)/2){
				float a=(xBoundry-101)/2-(x+width);
				float b1=(yBoundry-101)/2-(y+width);
				float b2=(yBoundry-101)/2-(y);
				double angle1=Math.atan2(b1, a);
				double angle2=Math.atan2(b2, a);
				verts1=new float[]{
						x+width,y+width,
						(float)(a*.025)+(x+width),(float)(a*Math.tan(angle1)*.025)+(y+width),
						x+width,y,
						(float)(a*.025)+(x+width),(float)(a*Math.tan(angle2)*.025)+(y),
						(float)(a*.025)+(x+width),(float)(a*Math.tan(angle1)*.025)+(y+width),
						(float)(a*.025)+(x+width),(float)(a*Math.tan(angle2)*.025)+(y),
				};
			}else if(x>(xBoundry-101)/2){
				float a=(xBoundry-101)/2-(x);
				float b1=(yBoundry-101)/2-(y+width);
				float b2=(yBoundry-101)/2-(y);
				double angle1=Math.atan2(b1, a);
				double angle2=Math.atan2(b2, a);
				verts1=new float[]{
						x,y+width,
						(float)(a*.025)+(x),(float)(a*Math.tan(angle1)*.025)+(y+width),
						x,y,
						(float)(a*.025)+(x),(float)(a*Math.tan(angle2)*.025)+(y),
						(float)(a*.025)+(x),(float)(a*Math.tan(angle1)*.025)+(y+width),
						(float)(a*.025)+(x),(float)(a*Math.tan(angle2)*.025)+(y),
				};
			}else{
				verts1=new float[]{
						0,0,
						0,0
				};
			}
			
			if(y+width<(yBoundry-101)/2){
				float a=(yBoundry-101)/2-(y+width);
				float b1=(xBoundry-101)/2-(x);
				float b2=(xBoundry-101)/2-(x+width);
				double angle1=Math.atan2(b1, a);
				double angle2=Math.atan2(b2, a);
				verts2=new float[]{
						x+width,y+width,
						(float)(a*Math.tan(angle1)*.025)+(x+width),(float)(a*.025)+(y+width),
						x,y+width,
						(float)(a*Math.tan(angle2)*.025)+(x),(float)(a*.025)+(y+width),
						(float)(a*Math.tan(angle1)*.025)+(x+width),(float)(a*.025)+(y+width),
						(float)(a*Math.tan(angle2)*.025)+(x),(float)(a*.025)+(y+width),
				};
			}else if(y>(yBoundry-101)/2){
				float a=(yBoundry-101)/2-(y);
				float b1=(xBoundry-101)/2-(x);
				float b2=(xBoundry-101)/2-(x+width);
				double angle1=Math.atan2(b1, a);
				double angle2=Math.atan2(b2, a);
				verts2=new float[]{
						x+width,y,
						(float)(a*Math.tan(angle1)*.025)+(x+width),(float)(a*.025)+(y),
						x,y,
						(float)(a*Math.tan(angle2)*.025)+(x),(float)(a*.025)+(y),
						(float)(a*Math.tan(angle1)*.025)+(x+width),(float)(a*.025)+(y),
						(float)(a*Math.tan(angle2)*.025)+(x),(float)(a*.025)+(y),
				};
			}else{
				verts2=new float[]{
						0,0,
						0,0
				};
			}
		}
		if(x>xBoundry|x<-101|y>yBoundry|y<-101)return false;
		else return true;
	}
	
	Boolean fancyGraphics=false;
	float xBoundry;
	float yBoundry;
	float x;
	float y;
	float vx;
	float vy;
	int width;
	
	
	float[] verts1=new float[8];
	float[] verts2=new float[8];
	
	Paint shade=new Paint();
	
	
}
