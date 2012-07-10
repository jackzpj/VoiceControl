package com.scut.vc.alarm;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
/**
 * ���������ݿ�İ����࣬�����ݿ�ĸ�����������ڴ˽��еġ�
 * @author Administrator
 *
 */
public class DatabaseHelper extends SQLiteOpenHelper{

	private static final String DB_NAME = "Voice_Control.db";
	static SQLiteDatabase db;
	public final static String ALARM_ID = "_id";
	public final static String ALARM_STATE = "ALARM_STATE";
	public final static String CLOCK_TIME = "CLOCK_TIME";
	public final static String CLOCK_DATE = "CLOCK_DATE";
	public final static String TIMES = "TIMES";
	public final static String ALARM_TABLE = "ALARM_INFO";
	

	public DatabaseHelper(Context context) {
		super(context, DB_NAME, null, 1);
		// TODO Auto-generated constructor stub
		createTable();
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO �������ݿ�󣬶����ݿ�Ĳ���  

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO �������ݿ�汾�Ĳ���

	}

	public void onOpen(SQLiteDatabase db)
	{
		super.onOpen(db);
		//ÿ�γɹ������ݿ�����ȱ�ִ��    
	}
	/**
	 * ����������
	 */
	private void createTable(){
//		String ti = "";
//		long ID = Long.parseLong(ti);
		try{
			db = this.getWritableDatabase();
			String TimeTable = "CREATE TABLE IF NOT EXISTS ALARM_INFO " + 
			"(" + ALARM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
			"ALARM_STATE VARCHAR, CLOCK_TIME VARCHAR, CLOCK_DATE VARCHAR, TIMES LONG);" ;
			db.execSQL(TimeTable);
			Log.e("Database", "Create success");
			db.close();
		}catch(SQLException ex){
			Log.e("Database", "Create error");
			db.close();
			return;
			
			
		}
	}

	/*
	 * ���½�Ϊ�Ը�����Ĳ���
	 */
	
	/**
	 * ����һ��ʱ����¼�
	 */
	public long insertTime(ContentValues cv) {
		db  = this.getWritableDatabase();
		return db.insert(ALARM_TABLE, null, cv);
		
	}

	/**
	 * ɾ��ĳһ��alarm
	 * @param idOfAlarm alarm��Id
	 */
	public void deleteTime(String id){
		SQLiteDatabase db = getWritableDatabase();
		String[] args = {id};
		db.delete("ALARM_INFO", ALARM_ID+ "=?", args);
		db.close();
	}


	/**
	 * ����һ�������¼���״̬
	 * @param id �������¼���id
	 * @param cv ������ʱ������������
	 * @return ���ؼ����±�Ĳ���
	 */
	public int updateTime(String id, ContentValues cv){
		db = this.getWritableDatabase();
		String where = ALARM_ID +"=?";
		String[] args = {id};
		return db.update(ALARM_TABLE, cv, where, args);	
	}
	
	/**
	 * ��ȡ���������¼�
	 * @param id �������id
	 * @return ���ش���������Ϣ��cursor
	 */
	public Cursor getAlarmTime(String id){
		db = this.getReadableDatabase();
		String where = ALARM_ID + "=?";
		String[] whereValues = {id};
		Cursor cursor = db.query(ALARM_TABLE, null, where, whereValues, null, null, null);
		return cursor;
	}
	
	/**
	 * ��ȡ���������¼�
	 * @return ���غ�������������Ϣ��cursor
	 */
	public Cursor selectAlarmTime(){
		db = this.getReadableDatabase();
		Cursor cursor = db.query(ALARM_TABLE, null, null, null, null, null, null);
		return cursor;
	}
	
}


