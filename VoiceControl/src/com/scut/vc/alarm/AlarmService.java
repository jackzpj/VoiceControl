package com.scut.vc.alarm;


import java.util.Calendar;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.os.IBinder;
import android.util.Log;


public class AlarmService extends Service{

	/**
	 * ��Service��Ϊ�˷���������ض����õ�
	 * @return null
	 */
	private static AlarmService as = null;
	private DatabaseHelper mydb;                //���ݿ�
	private String idOfAlarm ="";                     //�����id
	private long alarmTime = 0;                    //�������趨��ʱ���뵱ǰϵͳʱ��Ĳ�ֵ
	private String delId = "";                 //ɾ��������ID

	/*
	 * ����һ����̬��������Serviceʵ�壬���������ط����Ի�ȡ.
	 * private static Service getService as = null; �����洢�Լ���ʵ��
	 * ��onCreate()��ʹ��sms = this�����洢ʵ��
	 * ��дһ����̬��getService()������ʵ��Ϳ�����
	 */
	public static Service getService()
	{
		return as;
	}

	public void onCreate()
	{
		super.onCreate();
		as = this;
		reCountTime();	
	}

	/*
	 * reCountTime()��Ϊ�����¼������ӵ�ʱ�䣬���ʵ��ʱ�����
	 * Ԥ��ʱ�䣬�򲻻��з�Ӧ�������������趨����ʱ��
	 */
	public void reCountTime(){
		mydb = new DatabaseHelper(AlarmService.this);
		//alarmList = mydb.getAllAlarmTime();
		Cursor cursor = mydb.selectAlarmTime();
		Calendar cl = Calendar.getInstance();
		int count = cursor.getCount();
		if(count>0){
			for(int i = 0; i< count; i++){
				cursor.moveToPosition(i);
				int alarmId = Integer.parseInt(cursor.getString(0));
				//String states = cursor.getString(1);
				long times = cursor.getLong(4);
				AlarmManager am = (AlarmManager)getSystemService(Service.ALARM_SERVICE);
				//ֻ�е��趨��ʱ����ڵ�ǰϵͳ��ʱ��ʱ���Żᷢ���㲥
				if(times > cl.getTimeInMillis()){
					Intent it = new Intent(this,CallAlarm.class);
					it.putExtra("ID", alarmId);
					PendingIntent pit = PendingIntent.getBroadcast
							(this, alarmId, it, 0);
					am.set(AlarmManager.RTC_WAKEUP, 
							times, pit);					
					Log.v("Work", "Alarm id "+ alarmId +" times " + times);
				}else{
					//Toast.makeText(AlarmService.this, "�����ˣ�", Toast.LENGTH_SHORT);
				}
			}
		}	
	}

	/*
	 * (non-Javadoc)
	 * @see android.app.Service#onStart(android.content.Intent, int)
	 * ÿ������һ��alarmʱ���ͻ��ٴε��øú���
	 */
	public void onStart(Intent intent, int startId)
	{
		super.onStart(intent, startId);		
		Log.v("Work", "Service start!!!");
		Log.v("Work", intent.getAction().equals("DELID")+"");
		//		
		if(intent.getAction().equals("DELID")){

			delId = intent.getStringExtra("DELID"); 
			Log.e("Work", "  ---delID---- " + delId);
			AlarmManager am = (AlarmManager)getSystemService(Service.ALARM_SERVICE);
			Intent it = new Intent(AlarmService.this,CallAlarm.class);
			//it.putExtra(DatabaseHelper.ALARM_ID, delId);
			PendingIntent pi = PendingIntent.getBroadcast
					(this, Integer.parseInt(delId), it, 0);
			am.cancel(pi);
		}else if(intent.getAction().equals("SETTING")){
			Log.e("Work", "Wrong!!!!");
			//��ȡAlarmManager
			alarmTime = intent.getLongExtra("Time",0);
			idOfAlarm = intent.getStringExtra("ID");
			Log.e("Work", "ID "+ idOfAlarm +" Time "+  alarmTime);
			AlarmManager am = (AlarmManager)getSystemService(Service.ALARM_SERVICE);		
			Intent it = new Intent(AlarmService.this,CallAlarm.class);
			PendingIntent pit = PendingIntent.getBroadcast
					(this, Integer.parseInt(idOfAlarm), it, 0);
			am.set(AlarmManager.RTC_WAKEUP, 
					alarmTime, pit);
			Log.d("Work", "Alarm "+ idOfAlarm +" Start!!");
		}else{
			Log.v("Work", "Service Wrong!");
		}



	}
	/*
	 * (non-Javadoc)
	 * @see android.app.Service#onDestroy()
	 * Ŀǰ��û�κ�����
	 */
	public void onDestroy()
	{
		super.onDestroy();
		AlarmManager am = (AlarmManager)getSystemService
				(Service.ALARM_SERVICE);
		Log.v("Work", "Service End");

	}

	@Override
	public IBinder onBind(Intent intent)
	{
		// TODO Auto-generated method stub
		return null;
	}	

}
