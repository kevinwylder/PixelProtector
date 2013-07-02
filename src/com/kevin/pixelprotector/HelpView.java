package com.kevin.pixelprotector;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout.LayoutParams;


public class HelpView extends Activity {
	
	ImageView view;
	int index=0;
	int max;
	Integer[] pictures;
	Activity act;
	Class inquestion;
	
	public void onCreate(Bundle sis){
		super.onCreate(sis);
		view=new ImageView(this);
		LinearLayout root=new LinearLayout(this);
		view.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT));
		root.addView(view);
		setContentView(root);
		act=this;
		if(getIntent().getBooleanExtra("classic", true)){
			max=4;
			pictures=new Integer[]{
					R.drawable.classicone,
					R.drawable.classictwo,
					R.drawable.classicthree,
					R.drawable.classicfour,
					R.drawable.classicfive
			};
			inquestion=Classic.class;
		}else{
			max=7;
			pictures=new Integer[]{
					R.drawable.survivalone,
					R.drawable.survivaltwo,
					R.drawable.survivalthree,
					R.drawable.survivalfour,
					R.drawable.survivalfive,
					R.drawable.survivalsix,
					R.drawable.survivalseven,
					R.drawable.survivaleight
			};
			inquestion=Survival.class;
		}
		view.setBackgroundResource(pictures[0]);
		view.setOnTouchListener(new View.OnTouchListener(){
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				index++;
				if(index<=max){
					view.setBackgroundResource(pictures[index]);
				}else{
					if(getIntent().getBooleanExtra("openAfter",true)){
						Intent intent=new Intent(act,inquestion);
						act.startActivity(intent);
					}
					act.finish();
				}
				return false;
			}
		});
	}
	
}
