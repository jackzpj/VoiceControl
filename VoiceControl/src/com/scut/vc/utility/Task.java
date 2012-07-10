package com.scut.vc.utility;


/**
 * ����������ģ���
 * ��������
 * ��������ִ��
 * @author fatboy
 *
 */
public class Task {

	private int mTaskId =0;
	/**
	 * ��绰�ͷ���Ϣ�Ĳ�����List<Contact.ContactPerson>
	 * �豸���Ʋ�����String
	 * ��Ӧ�õĲ�����String
	 * Websearch�Ĳ�����String
	 */
	private Object mTaskParam = null;
	
	public final static int CALL = 1;//��绰
	public final static int SendMessage = 2;//����Ϣ
	public final static int OpenApp = 3;//��һ��Ӧ�� 
	public final static int SwitchOnDevice = 4;//��һ��Ӳ��
	public final static int Search = 5;//����
	public final static int SetAlarm = 6;//��������
	public final static int Weather = 7;//��������
	public final static int IdentifyError = -1;//ƥ�����
	
	public final static int ShowProcess = -2;//��ʾʶ���еĽ�����

	public Task(int task, Object param) {
		mTaskId = task;
		mTaskParam = param;
	}
	
	public Task() {
		mTaskId = Task.IdentifyError;
	}
	

	
	public int getTaskID() {
		return mTaskId;
	}

	public void setTaskID(int taskID) {
		mTaskId = taskID;
	}

	public Object getTaskParam() {
		return mTaskParam;
	}

	public void setTaskParam(Object taskParam) {
		mTaskParam = taskParam;
	}
}
