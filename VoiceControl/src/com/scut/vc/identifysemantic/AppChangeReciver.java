package com.scut.vc.identifysemantic;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AppChangeReciver extends BroadcastReceiver {




	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		// ���հ�װ�㲥
		if (intent.getAction().equals("android.intent.action.PACKAGE_ADDED")) {
			SemanticIdentify.appLock = true;
	
		}
		// ����ж�ع㲥
		if (intent.getAction().equals("android.intent.action.PACKAGE_REMOVED")) {
			SemanticIdentify.appLock = true;

		}
	}

}
