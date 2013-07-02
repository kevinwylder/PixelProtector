package com.kevin.pixelprotector;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;


public class ColorPicker extends Activity {
	
	int red;
	int green;
	int blue;
	int type;
	ImageView screen;
	TextView redView;
	TextView greenView;
	TextView blueView;
	SeekBar redBar;
	SeekBar greenBar;
	SeekBar blueBar;
	OnSeekBarChangeListener listener=new OnSeekBarChangeListener(){
		@Override
		public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
			if(fromUser)updateLayout(false);
		}
		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub
		}
		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub
			
		}
	};
	
	@Override
	public void onCreate(Bundle sis){
		super.onCreate(sis);
		setContentView(R.layout.colorpicker);
		screen=(ImageView)findViewById(R.id.imageView1);
		redView=(TextView)findViewById(R.id.textView2);
		greenView=(TextView)findViewById(R.id.textView4);
		blueView=(TextView)findViewById(R.id.textView6);
		redBar=(SeekBar)findViewById(R.id.seekBar1);
		greenBar=(SeekBar)findViewById(R.id.seekBar2);
		blueBar=(SeekBar)findViewById(R.id.seekBar3);
		redBar.setOnSeekBarChangeListener(listener);
		greenBar.setOnSeekBarChangeListener(listener);
		blueBar.setOnSeekBarChangeListener(listener);
		type=getIntent().getIntExtra("type", 0);
		int color=0;
		String title="";
		if(type==1){
			color=getSharedPreferences("prefs",MODE_WORLD_READABLE).getInt("BackgroundColor", Color.BLACK);
			title="Background Color";
		}else if(type==2){
			color=getSharedPreferences("prefs",MODE_WORLD_READABLE).getInt("PlayerColor",Color.RED);
			title="Player Color";
		}else if(type==3){
			color=getSharedPreferences("prefs",MODE_WORLD_READABLE).getInt("AsteroidColor",Color.LTGRAY);
			title="Asteroid Color";
		}else if(type==4){
			color=getSharedPreferences("prefs",MODE_WORLD_READABLE).getInt("GoalColor",Color.GREEN);
			title="Classic Goal Color";
		}else if(type==5){
			color=getSharedPreferences("prefs",MODE_WORLD_READABLE).getInt("FlashColor",Color.DKGRAY);
			title="Survival Flash Color";
		}else{
			this.finish();
		}
		red=(color >> 16) &0xFF;
		green=(color >> 8)&0xFF;
		blue=color & 0xFF;
		if(android.os.Build.VERSION.SDK_INT>10)getActionBar().setTitle(title);
		updateLayout(true);
	}
	
	public void updateLayout(boolean initial){
		if(initial){
			redBar.setProgress(red);
			greenBar.setProgress(green);
			blueBar.setProgress(blue);
		}else{
			red=redBar.getProgress();
			green=greenBar.getProgress();
			blue=blueBar.getProgress();
		}
		screen.setBackgroundColor(Color.rgb(red,green,blue));
		redView.setText(""+red);
		greenView.setText(""+green);
		blueView.setText(""+blue);
	}
	
	public void Save(View v){
		SharedPreferences.Editor editor=getSharedPreferences("prefs",MODE_WORLD_READABLE).edit();
		if(type==1){
			editor.putInt("BackgroundColor", Color.rgb(red,green,blue));
		}else if(type==2){
			editor.putInt("PlayerColor", Color.rgb(red,green,blue));
		}else if(type==3){
			editor.putInt("AsteroidColor", Color.rgb(red,green,blue));
		}else if(type==4){
			editor.putInt("GoalColor", Color.rgb(red,green,blue));
		}
		editor.commit();
		this.finish();
	}
	
}
