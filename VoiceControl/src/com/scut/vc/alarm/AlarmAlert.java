package com.scut.vc.alarm;

import java.io.IOException;

import android.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
/**
 * ����������Activity
 * @author Administrator
 *
 */

public class AlarmAlert extends Activity
{
	// MediaPlayerʵ��   
	private MediaPlayer player; 
	private DatabaseHelper mydb;
	private String stateInfo = "��������ƶ��ļƻ���!!!";                //��ʾ���е���Ϣ
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);		
		getAlarmState();		
		palyMusic();
		new AlertDialog.Builder(AlarmAlert.this)
		.setIcon(R.drawable.ic_dialog_alert)
		.setTitle("��������!!")
		.setMessage(stateInfo)
		.setPositiveButton("�ص���",
				new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int whichButton)
			{
				AlarmAlert.this.finish();
				stopMusic();
			}
		})
		.show();
	} 
	//��������
	public void palyMusic(){
		if(player == null)
		{
			Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
			try {
				player = new MediaPlayer();
				player.setDataSource(this, uri);
				final AudioManager audioManager = (AudioManager)this
				.getSystemService(Context.AUDIO_SERVICE);
				if (audioManager.getStreamVolume(AudioManager.STREAM_ALARM) != 0) {
					player.setAudioStreamType(AudioManager.STREAM_ALARM);
					player.setLooping(true);
					player.prepare();
				}
			} catch (IllegalStateException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if(!player.isPlaying()) {
            player.start();
        }
	}
	//ֹͣ����
	 public void stopMusic() {   
	        if (player != null) { 
	            player.stop();   
	            try {   
	                // �ڵ���stop�������Ҫ�ٴ�ͨ��start���в���,��Ҫ֮ǰ����prepare����   
	                player.prepare();   
	            } catch (IOException ex) {   
	                ex.printStackTrace();   
	            }   
	        }
	    }
	 
	 /**
	  * ������ʾ���е���Ϣ��Ĭ��Ϊ"��������ƶ��ļƻ���!!!"
	  */
	 public void getAlarmState(){
			mydb = new DatabaseHelper(AlarmAlert.this);
			Intent i = getIntent();
			int id  = i.getIntExtra("ID",-1);
			if(id == -1){
				Log.d("Work", "SB    +     id   ");
				stateInfo = "��������ƶ��ļƻ���!!!";
			}else{
				Log.d("Work", "SB    +     id   " + id);
				Cursor cursor = mydb.getAlarmTime(""+ id);
				if(cursor.moveToFirst()){
					stateInfo = cursor.getString(cursor.getColumnIndex(DatabaseHelper.ALARM_STATE));	
				}
				
			}
	 }
	
}
