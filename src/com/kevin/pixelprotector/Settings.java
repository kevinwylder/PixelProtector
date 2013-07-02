package com.kevin.pixelprotector;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Toast;

public class Settings extends PreferenceActivity
{
	
	Preference bg;
	Preference p;
	Preference a;
	Preference g;
	Preference f;
	Preference colors;
	Preference hiscores;
	Preference classic;
	Preference survival;
	Preference graphics;
	
	OnPreferenceClickListener listener=new OnPreferenceClickListener(){
		@Override
		public boolean onPreferenceClick(Preference preference) {
			Intent intent=new Intent(Settings.this,ColorPicker.class);
			if(preference==bg){
				intent.putExtra("type", 1);
			}else if(preference==p){
				intent.putExtra("type", 2);
			}else if(preference==a){
				intent.putExtra("type", 3);
			}else if(preference==g){
				intent.putExtra("type", 4);
			}else if(preference==f){
				intent.putExtra("type", 5);
			}
			startActivity(intent);
			return false;
		}
	};
	
	OnPreferenceClickListener remover=new OnPreferenceClickListener(){
		@Override
		public boolean onPreferenceClick(Preference preference) {
			AlertDialog.Builder builder=new AlertDialog.Builder(Settings.this);
			if(preference==colors){
				builder.setTitle("Reset Colors?");
				builder.setMessage("Are you sure you want to reset all colors to the default color scheme?");
				builder.setPositiveButton("Reset", new DialogInterface.OnClickListener(){
					@Override
					public void onClick(DialogInterface dialog, int which) {
						SharedPreferences.Editor editor=getSharedPreferences("prefs",MODE_WORLD_READABLE).edit();
						editor.putInt("BackgroundColor", Color.BLACK);
						editor.putInt("PlayerColor", Color.RED);
						editor.putInt("AsteroidColor", Color.LTGRAY);
						editor.putInt("GoalColor", Color.GREEN);
						editor.commit();
						Toast.makeText(Settings.this, "Colors Reset", Toast.LENGTH_SHORT).show();
					}
				});
			}else if(preference==hiscores){
				builder.setTitle("Reset High Scores?");
				builder.setMessage("Are you sure you want to reset all High Scores to 0? Both Classic and Survival scores will be lost.");
				builder.setPositiveButton("Reset",new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						SharedPreferences.Editor editor=getSharedPreferences("prefs",MODE_WORLD_READABLE).edit();
						editor.putInt("ClassicHighScore", 0);
						editor.putInt("SurvivalHighScore", 0);
						editor.putInt("GamesPlayed", 0);
						editor.putInt("TimePlayed", 0);
						editor.putString("ClassicName", "Nobody");
						editor.putString("SurvivalName", "Nobody");
						editor.commit();
						Toast.makeText(Settings.this, "Scores Reset", Toast.LENGTH_SHORT).show();
					}
				});
			}
			builder.setNegativeButton("Cancel", null);
			builder.create().show();
			return false;
		}
	};
	
	
	OnPreferenceChangeListener sensitive=new OnPreferenceChangeListener(){
		@Override
		public boolean onPreferenceChange(Preference preference, Object newValue) {
			SharedPreferences.Editor prefs=getSharedPreferences("prefs",MODE_WORLD_READABLE).edit();
			prefs.putFloat("movementSensitivity", Float.parseFloat((String)newValue));
			prefs.commit();
			return true;
		}
	};
	
	OnPreferenceChangeListener graphicsSwitch=new OnPreferenceChangeListener(){
		@Override
		public boolean onPreferenceChange(Preference preference, Object newValue) {
			SharedPreferences.Editor prefs=getSharedPreferences("prefs",MODE_WORLD_READABLE).edit();
			prefs.putBoolean("3DRendering",(Boolean)newValue);
			prefs.commit();
			return true;
		}
	};
	
	OnPreferenceClickListener helper=new OnPreferenceClickListener(){
		public boolean onPreferenceClick(Preference preference) {
			Intent intent=new Intent(Settings.this,HelpView.class);
			boolean extra=false;
			if(preference==classic){
				extra=true;
			}
			intent.putExtra("classic",extra);
			intent.putExtra("openAfter", false);
			startActivity(intent);
			return false;
		}
	};
	
	OnPreferenceClickListener depth=new OnPreferenceClickListener(){
		SharedPreferences.Editor prefs;
		@Override
		public boolean onPreferenceClick(Preference preference) {
			prefs=getSharedPreferences("prefs",MODE_WORLD_READABLE).edit();
			AlertDialog.Builder builder=new AlertDialog.Builder(Settings.this);
			builder.setTitle("3D Rendering Depth");
			SeekBar sb=new SeekBar(builder.getContext());
			sb.setOnSeekBarChangeListener(new OnSeekBarChangeListener(){
				@Override
				public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser) {
					float value=(progress/(float)seekBar.getMax())*.07f;
					prefs.putFloat("3Depth",value);
				}
				@Override
				public void onStartTrackingTouch(SeekBar seekBar) {}
				@Override
				public void onStopTrackingTouch(SeekBar seekBar) {}
			});
			int progress=(int)(((getSharedPreferences("prefs",MODE_WORLD_READABLE).getFloat("3Depth", .025f))/.07f)*100f);
			sb.setProgress(progress);
			builder.setView(sb);
			builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					prefs.commit();
				}
			});
			builder.setNegativeButton("Cancel",null);
			builder.setCancelable(true);
			builder.create().show();
			return false;
		}
	};
	
	@Override
	public void onCreate(Bundle sis){
		super.onCreate(sis);
		addPreferencesFromResource(R.xml.preferences);
		bg=findPreference("BackgroundColor");
		p=findPreference("PlayerColor");
		a=findPreference("AsteroidColor");
		g=findPreference("GoalColor");
		f=findPreference("FlashColor");
		colors=findPreference("colors");
		hiscores=findPreference("hiscores");
		findPreference("movementSensitivity").setOnPreferenceChangeListener(sensitive);
		classic=findPreference("classic");
		survival=findPreference("survival");
		graphics=findPreference("3DRendering");
		bg.setOnPreferenceClickListener(listener);
		p.setOnPreferenceClickListener(listener);
		a.setOnPreferenceClickListener(listener);
		g.setOnPreferenceClickListener(listener);
		f.setOnPreferenceClickListener(listener);
		colors.setOnPreferenceClickListener(remover);
		hiscores.setOnPreferenceClickListener(remover);
		classic.setOnPreferenceClickListener(helper);
		survival.setOnPreferenceClickListener(helper);
		graphics.setOnPreferenceChangeListener(graphicsSwitch);
		findPreference("3Depth").setOnPreferenceClickListener(depth);
	}
	
	@Override
	public void onBackPressed() {
		Intent i=new Intent(this,MainScreenActivity.class);
		startActivity(i);
		finish();
		return;
	}
	
}
