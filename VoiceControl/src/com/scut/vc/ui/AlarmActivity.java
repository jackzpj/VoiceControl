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
 * ��Activity�Ƕ�������������õ�
 * @author Administrator
 *
 */

public class AlarmActivity extends Activity{

	ImageButton  voiceBtn;               
	DatabaseHelper mydb;                              //����һ�����ݿ�ʵ��
	private ListView alarmListView;
	private Cursor cursor;
	private List<String> ids;                        //�����ݿ������������¼���id�洢��һ��������
	private List<String> tempTimes;                   //�ݴ����ݿ��е�ʱ����Ϣ
	private List<String> tempDate;
	private List<String> states;
	private String id ="";                             //���������id
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
	 * ����listview�������ݿ��е�������䵽listview��
	 */
	public void refresh(){
		cursor = mydb.selectAlarmTime();
		ids = new ArrayList<String>();
		tempTimes = new ArrayList<String>();
		states = new ArrayList<String>();
		tempDate = new ArrayList<String>();
		int count = cursor.getCount();
		//ֻ�����ݿ���������ʱ��������
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
	 * ���÷��ؼ�ɱ����ǰActivity��Ϊ�˷�ֹActivity���ɼ�ʱ�Զ�Service
	 * ��������
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


