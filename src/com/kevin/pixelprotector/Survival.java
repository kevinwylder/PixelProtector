package com.kevin.pixelprotector;

import android.app.*;
import android.content.*;
import android.os.*;
import android.view.*;

public class Survival extends Activity {
	
	SurvivalView view;
	
	@Override
	public void onCreate(Bundle sis){
		super.onCreate(sis);
		view=new SurvivalView(this);
		setContentView(view);
	}
	
	@Override
	public void onDestroy(){
		super.onDestroy();
		view.running=false;
	}

	public void onPause(){
		super.onPause();
		view.running=false;
	}

	public void onResume(){
		super.onResume();
		if(!view.running){
			view.running=true;
			view.setPaused();
		    view.handler.post(view.runnable);
		}
	}
	
	@Override
	public void onBackPressed() {
		if(view.paused){
    		Intent i=new Intent(this,MainScreenActivity.class);
    		startActivity(i);
    		finish();
		}else{
			view.setPaused();
		}
		return;
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event) { 
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            if(!view.paused)view.setPaused();
			else{
				view.running=true;
				view.paused=false;
				view.handler.post(view.runnable);
				view.buttons.clear();
			}
            return true;
        }
        return super.onKeyDown(keyCode, event); 
    } 
	
}
