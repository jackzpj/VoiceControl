package com.scut.vc.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
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
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.iflytek.speech.RecognizerResult;
import com.iflytek.speech.SpeechConfig.RATE;
import com.iflytek.speech.SpeechError;
import com.iflytek.speech.SynthesizerPlayer;
import com.iflytek.ui.RecognizerDialog;
import com.iflytek.ui.RecognizerDialogListener;
import com.scut.vc.alarm.AlarmService;
import com.scut.vc.identifysemantic.ContactObsever;
import com.scut.vc.identifysemantic.IdentifyThread;
import com.scut.vc.identifysemantic.UtilityThread;
import com.scut.vc.utility.Alarm;
import com.scut.vc.utility.AppsManager;
import com.scut.vc.utility.Contact;
import com.scut.vc.utility.DeviceControl;
import com.scut.vc.utility.Task;
import com.scut.vc.utility.Weather;
import com.scut.vc.utility.WebSearch;
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


	private ImageButton ib; // ʶ��ť

	private boolean showProgressDiaglog = false;
	public static boolean EnableGoogleVoice = false;// ʹ��google API
	public static boolean EnableXunfeiVoice = true;// ʹ��Ѷ�� API

	private IdentifyThread mIdentifyThread;// ����ʶ����߳�
	private UtilityThread mUtilityThread;// ɨ�������ϵ���߳�
	
	private ContactObsever mContactObserver;// ������ϵ�˸���


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		Thread thread2 = new Thread((mUtilityThread = new UtilityThread(this)));
		thread2.start();

		inital();

		Thread thread1 = new Thread(
				(mIdentifyThread = new IdentifyThread(this)));
		thread1.start();

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
			
			} else if (voiceEngine.equals("2")) {// EnableGoogleVoice
				startVoiceRecognitionActivity();
	
			}
		}

		voiceString = "��qq";


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


		pd.setVisibility(View.INVISIBLE);
	

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
		
				} else if (voiceEngine.equals("2")) {// EnableGoogleVoice
					startVoiceRecognitionActivity();
			

				}
			}

		});

		mContactObserver = new ContactObsever( null);
		registerObsever();

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

				@SuppressWarnings("unchecked")
				ArrayList<AppsManager.Package_Info> appList = (ArrayList<AppsManager.Package_Info>) task
						.getTaskParam();
				ShowAppSelectDialog(appList, task);

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
				String weatherInfos = (String) task.getTaskParam();
				updateListView(R.layout.chat_helper, weatherInfos);
			}
				break;
			case Task.ShowProcess: {
				if (!showProgressDiaglog) {
					pd.setVisibility(View.VISIBLE);
				
					ib.setClickable(false);

					showProgressDiaglog = true;
				} else {
					
					ib.setClickable(true);
					showProgressDiaglog = false;
					pd.setVisibility(View.INVISIBLE);
				}
			}
				break;
			case Task.IdentifyError: {

	

				updateListView(R.layout.chat_helper2, "�Բ���Ŷ���Ҳ����������");

			}
			default: {
			
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
		if (!voiceTempString.equals("")) {
			voiceString = voiceTempString.substring(0,
					voiceTempString.length() - 1);
			updateListView(R.layout.chat_user, voiceString);
			voiceTempString = "";
		}
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
						AppsManager.Package_Info packInfo = list.get(which);
						mAppManager.Execute(packInfo.GetPackageName());
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
		mUtilityThread = null;// ����ʶ��Ķ��߳�
		mIdentifyThread = null;
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
	
	private void registerObsever() {
		Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
		this.getContentResolver().registerContentObserver(uri, false,
				mContactObserver);
	}
	
}