package com.scut.vc.alarm;

import android.content.Context;
import android.content.Intent;
import android.content.BroadcastReceiver;
import android.os.Bundle;

/**
 * ��������
 * @author Administrator
 *
 */
public class CallAlarm extends BroadcastReceiver
{
  @Override
  public void onReceive(Context context, Intent intent)
  {
	  /*
	   * ��ȡ�������������id���ٽ�֮���͸�AlarmAlert��ʹAlarmAlert�ܹ�����
	   * �����id��ȡ���Ӧ���¼�
	   */
	int id  = intent.getIntExtra("ID", -1);
    Intent i = new Intent(context, AlarmAlert.class);         
    Bundle bundleRet = new Bundle();
    bundleRet.putString("STR_CALLER", "");
    i.putExtras(bundleRet);
    i.putExtra("ID", id);
    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    context.startActivity(i);
  }
}
