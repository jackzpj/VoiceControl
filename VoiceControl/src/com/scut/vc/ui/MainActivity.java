package com.scut.vc.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.iflytek.a.a;
import com.iflytek.speech.RecognizerResult;
import com.iflytek.speech.SpeechConfig.RATE;
import com.iflytek.speech.SpeechError;
import com.iflytek.speech.SynthesizerPlayer;
import com.iflytek.ui.RecognizerDialog;
import com.iflytek.ui.RecognizerDialogListener;
import com.scut.vc.alarm.AlarmService;
import com.scut.vc.identifysemantic.IdentifyThread;
import com.scut.vc.identifysemantic.SemanticIdentify;
import com.scut.vc.utility.Alarm;
import com.scut.vc.utility.AppsManager;
import com.scut.vc.utility.Contact;
import com.scut.vc.utility.DeviceControl;
import com.scut.vc.utility.Task;
import com.scut.vc.utility.Weather;
import com.scut.vc.utility.WebSearch;
import com.scut.vc.utility.Contact.ContactPerson;
import com.scut.vc.xflib.ChatAdapter;
import com.scut.vc.xflib.ChatEng;

public class MainActivity extends Activity implements RecognizerDialogListener,
		OnClickListener {
	/** Called when the activity is first created. */
	private static final int VOICE_RECOGNITION_REQUEST_CODE = 1234;

	private AppsManager mAppManager;
	private Contact mContact;
	private DeviceControl mDevCon;
	private WebSearch mWebSearch;

	private Weather mWeather;

	private ArrayList<ChatEng> list;
	private ChatAdapter cad;
	private ListView chatList;

	private RecognizerDialog iatDialog;
	private String infos;

	public static String voiceString = "";// ���������ṩ�̷��صĴ����ַ���
	public static String voiceTempString = ""; // Ѷ������������ʱ��ŵ��ַ���

	public ProgressBar pd;// ʶ���н�����

	// public TextView tv; //ʶ���е�����˵��
	// public ImageView iv; //ʶ���еı���
	private ImageButton ib; // ʶ��ť

	private boolean showProgressDiaglog = false;
	public static boolean EnableGoogleVoice = false;// ʹ��google API
	public static boolean EnableXunfeiVoice = true;// ʹ��Ѷ�� API

	private IdentifyThread mThread;// ����ʶ����߳�

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		inital();
		Thread thread = new Thread((mThread = new IdentifyThread(this)));
		thread.start();

		/**
		 * ��ȴ���;
		 */
		updateListView(R.layout.chat_helper, "��ʲô���԰ﵽ����");

		// ������ʶ��
		SharedPreferences sharedata_start = getSharedPreferences("startTurn",
				MODE_WORLD_READABLE);
		boolean startTurn = sharedata_start.getBoolean("startTurn", false);// ���������ȷ��ȡ������ʶ���ѡ����ԡ�0��ΪĬ��ֵ����ʾ��������ʶ��
		System.out.println("startTurn = " + startTurn);

		if (startTurn) {
			SharedPreferences sharedata1 = getSharedPreferences("voiceEngine",
					MODE_WORLD_READABLE);
			String voiceEngine = sharedata1.getString("voiceEngine", "1");// ���������ȷ��ȡ��������ѡ������ݣ����Ե�һ��Ϊֵ
			System.out.println("voiceEngine = " + voiceEngine);

			// ���ڲ��������˵��ֻ����йȸ��Դ��������⣬��������Ĭ���Կƴ�Ѷ������
			if (voiceEngine.equals("1")) {// EnableXunfeiVoice
				showIatDialog();
				// voiceString = "23�㿪��";
				// updateListView(R.layout.chat_user, voiceString);
			} else if (voiceEngine.equals("2")) {// EnableGoogleVoice
				startVoiceRecognitionActivity();
				// voiceString = "23��뿪��";
				// updateListView(R.layout.chat_user, voiceString);
			}
		}

		ArrayList<Contact.ContactPerson> callTarget = new ArrayList<Contact.ContactPerson>();// ��绰�б�
		Contact.ContactPerson contactPerson1 = mContact.new ContactPerson(
				"�й��ƶ�A", "10086");

		Contact.ContactPerson contactPerson2 = mContact.new ContactPerson(
				"�й��ƶ�B", "13800138000");

		callTarget.add(contactPerson1);

		callTarget.add(contactPerson2);
		// Task task = new Task(Task.OpenApp, "com.ihandysoft.alarmclock");
		// Task task = new Task(Task.Search, "com.android.soundrecorder");

		DeviceControl.Device device = mDevCon.new Device("flash", true);
		Task task = new Task(Task.SwitchOnDevice, device);

		// Task task = new Task(Task.SetAlarm, "�����칬����");
		//Test(task);

		// mDevCon.Release();
//		ArrayList<AppsManager.Package_Info> appList = new ArrayList<AppsManager.Package_Info>();
//		AppsManager.Package_Info info1 = mAppManager.new Package_Info("���",
//				"com.miui.camera");
//		AppsManager.Package_Info info2 = mAppManager.new Package_Info("���춯��",
//				"com.sds.android.ttpod");
//		// voiceString = "�����";
//		appList.add(info1);
//		appList.add(info2);
//		Task task = new Task(Task.OpenApp, appList);
		//Test(task);
		//voiceString = "����������������";


		// Test(task);
		voiceString = "��绰������";

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		/*
		 * add()�������ĸ������������ǣ� 1��������������Ļ���дMenu.NONE,
		 * 2��Id���������Ҫ��Android�������Id��ȷ����ͬ�Ĳ˵� 3��˳���Ǹ��˵�������ǰ������������Ĵ�С����
		 * 4���ı����˵�����ʾ�ı�
		 */
		menu.add(Menu.NONE, Menu.FIRST + 1, 1, "����");
		menu.add(Menu.NONE, Menu.FIRST + 2, 2, "����");
		menu.add(Menu.NONE, Menu.FIRST + 3, 3, "�����б�");
		menu.add(Menu.NONE, Menu.FIRST + 4, 4, "�˳�");
		return true;

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		/*
		 * // ��ȡ������ȫ�ֵľ��������Preference SharedPreferences sharedata1 =
		 * getSharedPreferences("list1",MODE_WORLD_READABLE); String data =
		 * sharedata1.getString("item", null); System.out.println("data = " +
		 * data);
		 */

		switch (item.getItemId()) {
		case Menu.FIRST + 1:
			Toast.makeText(this, "�����ý���", Toast.LENGTH_SHORT).show();
			Intent intent2 = new Intent();
			intent2.setClass(this, SettingActivity.class);
			startActivity(intent2);
			break;
		case Menu.FIRST + 2:
			Toast.makeText(this, "�򿪰�������", Toast.LENGTH_SHORT).show();
			Intent intent1 = new Intent();
			intent1.setClass(this, HelpActivity.class);
			startActivity(intent1);
			break;

		case Menu.FIRST + 3:
			Toast.makeText(this, "�������б�", Toast.LENGTH_SHORT).show();
			Intent intent3 = new Intent();
			intent3.setClass(this, AlarmActivity.class);
			startActivity(intent3);
			break;
		case Menu.FIRST + 4:
			Toast.makeText(this, "�˳�Ӧ�ó���", Toast.LENGTH_SHORT).show();
			Intent i = new Intent(MainActivity.this, AlarmService.class);
			stopService(i);
			android.os.Process.killProcess(android.os.Process.myPid());

			break;
		}
		return false;
	}

	/**
	 * ��ʼ��ʵ����
	 */
	private void inital() {
		/**
		 * ��ʼ��һЩ���ƶ���
		 */
		mAppManager = new AppsManager(this);
		mContact = new Contact(this);
		mDevCon = new DeviceControl(this);
		mWebSearch = new WebSearch(this);

		list = new ArrayList<ChatEng>();
		cad = new ChatAdapter(MainActivity.this, list);
		chatList = (ListView) findViewById(R.id.chatlist);
		ib = (ImageButton) findViewById(R.id.helper_voice);

		/**
		 * �������ʱ��progressBar��ʾ������˵��
		 */

		pd = (ProgressBar) findViewById(R.id.progressBar2);
		// tv = (TextView)findViewById(R.id.textView1);
		// iv = (ImageView)findViewById(R.id.imageView1);

		pd.setVisibility(View.INVISIBLE);
		// tv.setVisibility(View.INVISIBLE);
		// iv.setVisibility(View.INVISIBLE);

		/**
		 * Ѷ�ɴ��ڳ�ʼ��
		 */
		iatDialog = new RecognizerDialog(this, "appid="
				+ getString(R.string.app_id));
		iatDialog.setListener(this);

		/**
		 * �Ի�Ͳ��ť����Ӧ
		 */
		ib.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub

				return false;
			}
		});

		ib.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub

				// ��ȡ��������ѡ�������
				// ��һ�������汾�ϵļ���
				// SharedPreferences sharedata1 = getSharedPreferences(
				// "voiceEngine", MODE_WORLD_READABLE | MODE_MULTI_PROCESS);
				SharedPreferences sharedata1 = getSharedPreferences(
						"voiceEngine", MODE_WORLD_READABLE);
				String voiceEngine = sharedata1.getString("voiceEngine", "1");// ���������ȷ��ȡ��������ѡ������ݣ����Ե�һ��Ϊֵ
				System.out.println("voiceEngine = " + voiceEngine);

				// ���ڲ��������˵��ֻ����йȸ��Դ��������⣬��������Ĭ���Կƴ�Ѷ������
				if (voiceEngine.equals("1")) {// EnableXunfeiVoice
					showIatDialog();
					// voiceString = "23�㿪��";
					// updateListView(R.layout.chat_user, voiceString);
				} else if (voiceEngine.equals("2")) {// EnableGoogleVoice
					startVoiceRecognitionActivity();
					// voiceString = "23��뿪��";
					// updateListView(R.layout.chat_user, voiceString);

				}
			}

		});

	}

	/**
	 * ����Ĵ���
	 */
	public Handler mhandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub

			Task task = (Task) msg.obj;
			switch (task.getTaskID()) {
			case Task.CALL: {
				@SuppressWarnings("unchecked")
				ArrayList<Contact.ContactPerson> callList = (ArrayList<Contact.ContactPerson>) task
						.getTaskParam();
				ShowContactSelectDialog(callList, task);

			}
				break;
			case Task.SendMessage: {

				@SuppressWarnings("unchecked")
				ArrayList<Contact.ContactPerson> msgList = (ArrayList<Contact.ContactPerson>) task
						.getTaskParam();

				ShowContactSelectDialog(msgList, task);

			}
				break;
			case Task.OpenApp: {

				ArrayList<AppsManager.Package_Info> appList = (ArrayList<AppsManager.Package_Info>) task
						.getTaskParam();
				if (0 == appList.size()) {

				} else if (1 == appList.size()) {
					String packname = ((AppsManager.Package_Info) appList
							.get(0)).GetPackageName();
					String appName = ((AppsManager.Package_Info) appList.get(0))
							.GetAppName();
					if (appName.contains("���") || appName.contains("Camera")
							|| appName.contains("camera")) {
						mDevCon.Release();
					}
					mAppManager.Execute(packname);
				} else if (1 < appList.size()) {
					ShowAppSelectDialog(appList, task);
				}

			}
				break;
			case Task.Search: {
				String search = (String) task.getTaskParam();
				mWebSearch.Execute(search);
			}
				break;
			case Task.SwitchOnDevice: {
				DeviceControl.Device device = (DeviceControl.Device) task
						.getTaskParam();
				mDevCon.Execute(device);
			}
				break;
			case Task.SetAlarm: {
				String strvoice = (String) task.getTaskParam();
				Alarm alarm = new Alarm(MainActivity.this, strvoice);
				alarm.Execute();
			}
				break;
			case Task.Weather: {
				HashMap weatherInfos = (HashMap) task.getTaskParam();
				String city = (String) weatherInfos.get("city");
				int day = (Integer) weatherInfos.get("day");
				System.out.println(city + ":" + day);
				mWeather = new Weather(city, day, MainActivity.this);
				String weatherInfo;
				try {
					weatherInfo = mWeather.execute();
					updateListView(R.layout.chat_helper, weatherInfo);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SAXException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ParserConfigurationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
				break;
			case Task.ShowProcess: {
				if (!showProgressDiaglog) {
					pd.setVisibility(View.VISIBLE);
					// tv.setVisibility(View.VISIBLE);

					// iv.setVisibility(View.VISIBLE);
					// iv.setAlpha(100);
					ib.setClickable(false);

					showProgressDiaglog = true;
				} else {
					// pd.setVisibility(View.INVISIBLE);
					// tv.setVisibility(View.INVISIBLE);

					// iv.setVisibility(View.INVISIBLE);
					ib.setClickable(true);
					showProgressDiaglog = false;
					pd.setVisibility(View.INVISIBLE);
				}
			}
				break;
			case Task.IdentifyError: {

				// speakString("�Բ���Ŷ���Ҳ����������");

				updateListView(R.layout.chat_helper2, "�Բ���Ŷ���Ҳ����������");

			}
			default: {
				// updateListView("�Բ���Ŷ���Ҳ������");
			}
			}

			super.handleMessage(msg);
		}
	};

	/**
	 * ����ǻ���������resId�͸�ֵR.layout.chat_helper; ������˽�����resId�͸�ֵR.layout.chat_user
	 * 
	 * @param resId
	 * @param speekInfo
	 */
	public void updateListView(int resId, String speekInfo) {

		ChatEng ce = new ChatEng(speekInfo, resId);
		list.add(ce);
		chatList.setAdapter(cad);
		// cad.notify();

	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	/**
	 * Fire an intent to start the speech recognition activity.
	 */
	private void startVoiceRecognitionActivity() {
		Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
				RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
		intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
				"Speech recognition demo");
		startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);
	}

	/**
	 * Handle the results from the recognition activity. �ȸ�API���صĽ��
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == VOICE_RECOGNITION_REQUEST_CODE
				&& resultCode == RESULT_OK) {
			// Fill the list view with the strings the recognizer thought it
			// could have heard
			ArrayList matches = data
					.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

			voiceString = matches.get(0).toString();
			updateListView(R.layout.chat_user, voiceString);
		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	public void showIatDialog() {
		// TODO Auto-generated method stub
		String engine = "sms";
		String area = null;
		// voiceString = "";
		iatDialog.setEngine(engine, area, null);
		iatDialog.setSampleRate(RATE.rate8k);

		iatDialog.show();

	}

	@Override
	public void onStart() {
		super.onStart();

		String engine = "sms";
		String[] engineEntries = getResources().getStringArray(
				R.array.preference_entries_iat_engine);
		String[] engineValues = getResources().getStringArray(
				R.array.preference_values_iat_engine);
		for (int i = 0; i < engineValues.length; i++) {
			if (engineValues[i].equals(engine)) {
				infos = engineEntries[i];
				break;
			}
		}

	}

	public void onClick(View arg0) {
		// TODO Auto-generated method stub

	}

	public void onEnd(SpeechError arg0) {
		// TODO Auto-generated method stub
		voiceString = voiceTempString
				.substring(0, voiceTempString.length() - 1);
		updateListView(R.layout.chat_user, voiceString);
		voiceTempString = "";

	}

	/**
	 * Ѷ�ɴ��صĽ��
	 */
	public void onResults(ArrayList<RecognizerResult> arg0, boolean arg1) {
		// TODO Auto-generated method stub
		// voiceString = "";
		for (int i = 0; i < arg0.size(); i++) {
			RecognizerResult recognizerResult = arg0.get(i);
			voiceTempString += recognizerResult.text;
		}

	}

	/**
	 * Ѷ�������ϳ�
	 * 
	 * @param helperStr
	 */
	public void speakString(String helperStr) {

		SynthesizerPlayer player = SynthesizerPlayer.createSynthesizerPlayer(
				this, "appid=" + getString(R.string.app_id));
		player.setVoiceName(getString(R.string.preference_default_tts_role));
		player.setSampleRate(RATE.rate16k);

		player.setSpeed(75);
		player.setVolume(75);
		player.playText(helperStr, "ent=vivi21,bft=2", null);

	}

	/**
	 * ��ϵ�˶��б�������
	 * 
	 * @param items
	 * @param task
	 */
	public void ShowContactSelectDialog(
			final ArrayList<Contact.ContactPerson> list, final Task task) {
		final String[] items = new String[list.size()];
		for (int n = 0; n < list.size(); n++) {
			items[n] = ((Contact.ContactPerson) list.get(n)).GetName();
		}
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("��ѡ��").setItems(items,
				new android.content.DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						String num = ((Contact.ContactPerson) list.get(which))
								.GetNumber();
						if (task.getTaskID() == Task.CALL) {
							mContact.CallPerson(num);
						} else if (task.getTaskID() == Task.SendMessage) {
							mContact.SendMsg(num, "");
						}

					}
				});

		AlertDialog dialog = builder.create();
		dialog.show();

	}

	/**
	 * Ӧ�ó�����б�������
	 * 
	 * @param items
	 * @param task
	 */
	public void ShowAppSelectDialog(
			final ArrayList<AppsManager.Package_Info> list, final Task task) {
		final String[] items = new String[list.size()];
		for (int n = 0; n < list.size(); n++) {
			items[n] = ((AppsManager.Package_Info) list.get(n)).GetAppName();
		}
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("��ѡ��").setItems(items,
				new android.content.DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						ArrayList<AppsManager.Package_Info> _list = new ArrayList<AppsManager.Package_Info>();
						_list.add(list.get(which));
						Task _task = new Task(task.getTaskID(), _list);
						Message msg = new Message();
						msg.obj = _task;
						mhandler.sendMessage(msg);
					}
				});

		AlertDialog dialog = builder.create();
		dialog.show();

	}

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		DeviceControl.Device device = mDevCon.new Device("flash", false);
		mDevCon.Execute(device);
		super.onStop();
	}

	/**
	 * ����ڴ�
	 */
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		mAppManager = null;
		mContact = null;
		mDevCon.Release();
		mDevCon = null;
		mWebSearch = null;

		list = null;
		cad = null;
		chatList = null;

		iatDialog = null;
		voiceString = "";// ���������ṩ�̷��صĴ����ַ���
		pd = null;
		android.os.Process.killProcess(android.os.Process.myPid());
		mThread = null;// ����ʶ��Ķ��߳�
		super.onDestroy();
	}

	private void Test(Task task) {
		Message msg = new Message();
		msg.obj = task;
		mhandler.sendMessage(msg);
	}

	public DeviceControl getDevice() {
		return mDevCon;
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			MainActivity.this.finish();
			Intent i = new Intent(MainActivity.this, AlarmService.class);
			stopService(i);

			Log.v("Work", "MainActivity End");
		}
		return super.onKeyDown(keyCode, event);
	}
}