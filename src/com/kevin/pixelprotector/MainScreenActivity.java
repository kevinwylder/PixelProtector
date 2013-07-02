package com.kevin.pixelprotector;


import android.app.*;
import android.content.*;
import android.graphics.*;
import android.os.*;
import android.util.Log;
import android.widget.*;


public class MainScreenActivity extends Activity{
	
	MainScreen view;
	
	public void onCreate(Bundle bundle){
		super.onCreate(bundle);
		view=new MainScreen(this);
		setContentView(view);
		initializeSettings();
	}
	
	@Override
	public void onBackPressed() {
		finish();
		return;
	}
	
	@Override
	public void onDestroy(){
		super.onDestroy();
		view.setRunning(false);
	}
	
	public void onPause(){
		super.onPause();
		view.setRunning(false);
		finish();
	}
	
	public void onResume(){
		super.onResume();
		view.setRunning(true);
	}
	
	public void initializeSettings(){
		if(getSharedPreferences("prefs",MODE_WORLD_READABLE).getBoolean("setUp",true)){
			SharedPreferences.Editor editor=getSharedPreferences("prefs",MODE_WORLD_READABLE).edit();
			editor.putBoolean("setUp",false);
			editor.putInt("BackgroundColor",Color.rgb(0, 0, 0));
			editor.putInt("PlayerColor", Color.RED);
			editor.putInt("AsteroidColor", Color.LTGRAY);
			editor.putInt("GoalColor", Color.GREEN);
			editor.putInt("FlashColor", Color.DKGRAY);
			editor.putInt("ClassicHighScore", 0);
			editor.putInt("SurvivalHighScore", 0);
			editor.putInt("GamesPlayed", 0);
			editor.putInt("TimePlayed", 0);
			editor.putFloat("movementSensitivity",1);
			editor.putBoolean("ClassicShowHelp", true);
			editor.putBoolean("SurvivalShowHelp",true);
			editor.putString("ClassicName", "Nobody");
			editor.putString("SurvivalName", "Nobody");
			editor.putBoolean("3DRendering", false);
			editor.putFloat("3Depth", 0.025f);
			editor.commit();
			Toast.makeText(this, "Initilizing Settings", Toast.LENGTH_LONG).show();
		}
	}
	
}
