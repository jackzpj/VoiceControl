package com.scut.vc.ui;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.ImageButton;
import android.widget.ListView;

import com.scut.vc.alarm.DatabaseHelper;
import com.scut.vc.alarm.MyAlarmAdapter;

/**
 * 该Activity是对整个闹铃的设置的
 * @author Administrator
 *
 */

public class AlarmActivity extends Activity{

	ImageButton  voiceBtn;               
	DatabaseHelper mydb;                              //声明一个数据库实例
	private ListView alarmListView;
	private Cursor cursor;
	private List<String> ids;                        //将数据库中所有闹铃事件的id存储在一个链表中
	private List<String> tempTimes;                   //暂存数据库中的时间信息
	private List<String> tempDate;
	private List<String> states;
	private String id ="";                             //单个闹铃的id
	public long times=0;
	AlertDialog builder = null;    
	Calendar c=Calendar.getInstance();

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.clock);
		Log.v("Work", "Activity start!");
		mydb = new DatabaseHelper(AlarmActivity.this);
		refresh();	    	    
	}


	/**
	 * 更新listview并将数据库中的数据填充到listview中
	 */
	public void refresh(){
		cursor = mydb.selectAlarmTime();
		ids = new ArrayList<String>();
		tempTimes = new ArrayList<String>();
		states = new ArrayList<String>();
		tempDate = new ArrayList<String>();
		int count = cursor.getCount();
		//只有数据库中有数据时才能运行
		if(count>0){
			for(int i = 0; i< count; i ++){
				cursor.moveToPosition(i);
				ids.add(cursor.getString(0));
				states.add(cursor.getString(1));
				tempTimes.add(cursor.getString(2));
				tempDate.add(cursor.getString(3));

			}			
		}else{
		}	
		cursor.close();
		alarmListView = (ListView)findViewById(R.id.alarmList);
		alarmListView.setAdapter(new MyAlarmAdapter(AlarmActivity.this, states,tempTimes,tempDate,ids));
	}

	/**
	 * 设置返回键杀死当前Activity是为了防止Activity不可见时仍对Service
	 * 产生作用
	 */
	@SuppressLint("ParserError")
	public boolean onKeyDown(int keyCode, KeyEvent event){
		if(keyCode == KeyEvent.KEYCODE_BACK){
			AlarmActivity.this.finish();
			Log.v("Work", "AlActivity End");
		}
		return super.onKeyDown(keyCode, event);
	}

}


