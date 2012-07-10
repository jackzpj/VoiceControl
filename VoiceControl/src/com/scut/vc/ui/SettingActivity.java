package com.scut.vc.ui;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.widget.Toast;

public class SettingActivity extends PreferenceActivity implements
		OnSharedPreferenceChangeListener {

	private ListPreference mVoiceEngine;
	private ListPreference mSearchEngine;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.setting);
		this.getWindow().setBackgroundDrawableResource(R.drawable.mywb);
		/*
		 * // ��ȡ���ݣ����ڷ������ final SharedPreferences sp = PreferenceManager
		 * .getDefaultSharedPreferences(this);
		 * System.out.println("0000000000000"); String selectEngine =
		 * sp.getString("voiceEngine", "error~~");
		 * System.out.println("selectEngine = " + selectEngine);
		 * System.out.println("111111111111");
		 */

		// ��ȡѡ��ؼ�
		mVoiceEngine = (ListPreference) getPreferenceScreen().findPreference(
				"voiceEngine");
		mSearchEngine = (ListPreference) getPreferenceScreen().findPreference(
				"searchEngine");
		// ��ʼ��ʾʱ����ʾ��Ӧ��summary

		CharSequence entryVoice = mVoiceEngine.getEntry();
		mVoiceEngine.setSummary(entryVoice);
		System.out.println(entryVoice);

		CharSequence entrySearch = mSearchEngine.getEntry();
		mSearchEngine.setSummary(entrySearch);
		System.out.println(entrySearch);

		/*
		 * System.out.println(mVoiceEngine.getKey());//voiceEngine
		 * System.out.println(mVoiceEngine.getValue());//2
		 * System.out.println(mVoiceEngine.getTitle());//ʶ����������
		 * System.out.println(mVoiceEngine.getEntry());//�ȸ�����
		 */

		// ע��ı������
		getPreferenceScreen().getSharedPreferences()
				.registerOnSharedPreferenceChangeListener(this);

	}

	@Override
	protected void onPause() {
		super.onPause();

		// ��ȡ���ݣ����ύ����������activity�������ݵĶ�ȡ
		final SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(this);
		// ��ȡ����ʶ������ѡ������ݣ����ύ
		String voicetEngine = sp.getString("voiceEngine", "error~~"); // voiceEngine�ǿؼ���ID
		Editor sharedata_voice = getSharedPreferences("voiceEngine", 0).edit();
		sharedata_voice.putString("voiceEngine", voicetEngine);// voiceEngine�Ǹ��ⲿactivity������id
		sharedata_voice.commit();
		// ��ȡ��������ѡ������ݣ����ύ
		String searchEngine = sp.getString("searchEngine", "error~~"); // searchEngine�ǿؼ���ID
		Editor sharedata_search = getSharedPreferences("searchEngine", 0)
				.edit();
		sharedata_search.putString("searchEngine", searchEngine);// searchEngine�Ǹ��ⲿactivity������id
		sharedata_search.commit();
		// ��ȡ������ʶ��ѡ������ݣ����ύ
		boolean startTurn = sp.getBoolean("startTurn", false); // startTurn�ǿؼ���ID
		Editor sharedata_start = getSharedPreferences("startTurn", 0)
				.edit();
		sharedata_start.putBoolean("startTurn", startTurn);// startTurn�Ǹ��ⲿactivity������id
		sharedata_start.commit();

		Toast.makeText(this, "�����ѱ���", Toast.LENGTH_SHORT).show();
	}

	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		// TODO Auto-generated method stub
		if (key.equals("voiceEngine")) {
			CharSequence entry = mVoiceEngine.getEntry();
			mVoiceEngine.setSummary(entry);
			System.out.println(entry);
		}
		if (key.equals("searchEngine")) {
			CharSequence entry = mSearchEngine.getEntry();
			mSearchEngine.setSummary(entry);
			System.out.println(entry);
		}
	}
}
