package com.scut.vc.identifysemantic;

import android.app.Activity;
import android.os.Message;

import com.scut.vc.ui.MainActivity;
import com.scut.vc.utility.Task;

public class IdentifyThread implements Runnable {

	private Activity mActivity;// ��������mainactivity������
	private SemanticIdentify mSemanticIdentify;

	public void run() {
		// TODO Auto-generated method stub
		while (true) {
			try {
				while ((SemanticIdentify.appLock)
						|| (SemanticIdentify.contactLock)) {
					Thread.sleep(10);
				}

				if (!MainActivity.voiceString.equals("")) {
					ShowProcess();
					Task task = mSemanticIdentify
							.Identify((MainActivity.voiceString));
					Message msg = new Message();
					msg.obj = task;
					((MainActivity) mActivity).mhandler.sendMessage(msg);
					MainActivity.voiceString = "";

					ShowProcess();
				} else {
					Thread.sleep(1000);
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	public IdentifyThread(Activity activity) {
		mActivity = activity;
		mSemanticIdentify = new SemanticIdentify(activity);
	}

	/**
	 * ������������ʾ������
	 */
	private void ShowProcess() {
		Task task = new Task(Task.ShowProcess, null);
		Message msg = new Message();
		msg.obj = task;
		((MainActivity) mActivity).mhandler.sendMessage(msg);
	}

}
