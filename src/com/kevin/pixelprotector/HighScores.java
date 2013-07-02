package com.kevin.pixelprotector;

import android.app.*;
import android.content.*;
import android.os.*;

public class HighScores extends Activity {
	
	HighScoresView view;
	
	@Override
	public void onCreate(Bundle sis){
		super.onCreate(sis);
		view=new HighScoresView(this);
		setContentView(view);
	}
	
	@Override
	public void onBackPressed() {
		Intent i=new Intent(this,MainScreenActivity.class);
		startActivity(i);
		finish();
		return;
	}
	
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
		    view.handler.post(view.runnable);
		}
	}
	
	public String time(int time){
		return "";
	}
	
}
