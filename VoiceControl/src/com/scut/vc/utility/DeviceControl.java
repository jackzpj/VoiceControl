/**
 * ������Ҫ������permission * 
 */
// <uses-permission android:name="android.permission.CAMERA" />
//    <uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
//    <uses-permission android:name="android.permission.CHANGE_WIFI_STATE" />
//     <uses-permission android:name="android.permission.CHANGE_NETWORK_STATE"/>
//     <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>
//    <uses-permission android:name="android.permission.WAKE_LOCK" />
//    <uses-permission android:name="android.permission.BLUETOOTH_ADMIN" />
//    <uses-permission android:name="android.permission.BLUETOOTH" />
//    <uses-permission android:name="android.permission.WRITE_SETTINGS"/>

package com.scut.vc.utility;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.app.Activity;
import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.FeatureInfo;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.provider.Settings;

/**
 * ����������豸���Ƶ� Ϊ����Ӧandroid2.1 �����õĶ��Ǻ��ϵİ취 û���õ�SETTING��
 * 
 * @author fatboy
 * 
 */
public class DeviceControl {
	/**
	 * �����activity
	 */
	private Activity mActivity;

	private Camera mCamera = null;// �ֻ�Ͳ
	private Camera.Parameters parameter; // �ֵ�Ͳ����
	private WifiManager mWifiManager;// wifi������
	private BluetoothAdapter mBlueTooth;// ����������
	private ConnectivityManager mConnMan;// �ֻ����ӹ�����

	private Intent mGpsIntent;

	private final int Flash = 1;
	private final int Wifi = 2;
	private final int BlueTooth = 3;
	private final int Gprs = 4;
	private final int airmode = 5;
	private final int Gps = 6;

	public DeviceControl(Activity activity) {
		mActivity = activity;

		initial();// ��ʼ��
	}

	/**
	 * �ֵ�Ͳ��ʼ�� ��Ͳ���������ʼ����
	 */

	private void OpenLightOn() {
		if (null == mCamera) {
			mCamera = Camera.open();
		}
		// m_Camera.takePicture(null, null, null, null);

		Camera.Parameters parameters = mCamera.getParameters();
		System.out.println(parameters.getFlashMode());
		parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
		System.out.println(parameters.getFlashMode());
		mCamera.setParameters(parameters);
		mCamera.autoFocus(new Camera.AutoFocusCallback() {
			public void onAutoFocus(boolean success, Camera camera) {

			}
		});
		mCamera.startPreview();
	}

	private void CloseLightOff() {
		if (mCamera != null) {
			Camera.Parameters parameters = mCamera.getParameters();
			System.out.println(parameters.getFlashMode());
			parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
			System.out.println(parameters.getFlashMode());
			mCamera.setParameters(parameters);

			mCamera.stopPreview();
			mCamera.release();
			mCamera = null;
		}
	}

	private void initial() {

		// initialTorch();

		/**
		 * GPS
		 */
		mGpsIntent = new Intent();
		mGpsIntent.setClassName("com.android.settings",
				"com.android.settings.widget.SettingsAppWidgetProvider");
		mGpsIntent.addCategory("android.intent.category.ALTERNATIVE");
		mGpsIntent.setData(Uri.parse("custom:3"));

		// try {
		// PendingIntent.getBroadcast(this, 0, mGpsIntent, 0).send();
		// } catch (CanceledException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }

		/**
		 * WIFI
		 * 
		 */
		mWifiManager = (WifiManager) mActivity
				.getSystemService(Context.WIFI_SERVICE);

		/**
		 * GPRS
		 */
		mConnMan = (ConnectivityManager) mActivity
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		/**
		 * ����
		 */
		mBlueTooth = BluetoothAdapter.getDefaultAdapter();
	}

	/**
	 * ��������
	 * 
	 * @param enable
	 */
	private void EnableBlueTooth(boolean enable) {
		if (enable) {
			mBlueTooth.enable();

		} else {
			mBlueTooth.disable();
		}
	}

	/**
	 * ����WIFI
	 */
	private void EnableWiFi(boolean enable) {
		if (enable) {
			mWifiManager.setWifiEnabled(true);

		} else {
			mWifiManager.setWifiEnabled(false);
		}
	}

	/**
	 * ����GPS
	 */
	private void EnableGps() {
		try {
			PendingIntent.getBroadcast(mActivity, 0, mGpsIntent, 0).send();
		} catch (CanceledException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * �����ֵ�Ͳ
	 * 
	 * @param enable
	 */
	private void EnableTorch(boolean enable) {
		if (enable) {

			parameter.setFlashMode(Camera.Parameters.FLASH_MODE_ON);

			mCamera.setParameters(parameter);
		} else {
			parameter.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
			mCamera.setParameters(parameter);

		}
	}

	/**
	 * ����GPRS
	 * 
	 * @param enable
	 */
	private void EnableGprs(boolean enable) {
		Class<?> conMgrClass = null; // ConnectivityManager��
		Field iConMgrField = null; // ConnectivityManager���е��ֶ�
		Object iConMgr = null; // IConnectivityManager�������
		Class<?> iConMgrClass = null; // IConnectivityManager��
		Method setMobileDataEnabledMethod = null; // setMobileDataEnabled����

		// ȡ��ConnectivityManager��
		try {
			conMgrClass = Class.forName(mConnMan.getClass().getName());
			// ȡ��ConnectivityManager���еĶ���mService
			iConMgrField = conMgrClass.getDeclaredField("mService");
			// ����mService�ɷ���
			iConMgrField.setAccessible(true);
			// ȡ��mService��ʵ������IConnectivityManager
			iConMgr = iConMgrField.get(mConnMan);
			// ȡ��IConnectivityManager��
			iConMgrClass = Class.forName(iConMgr.getClass().getName());
			// ȡ��IConnectivityManager���е�setMobileDataEnabled(boolean)����
			setMobileDataEnabledMethod = iConMgrClass.getDeclaredMethod(
					"setMobileDataEnabled", Boolean.TYPE);
			// ����setMobileDataEnabled�����ɷ���
			setMobileDataEnabledMethod.setAccessible(true);
			// ����setMobileDataEnabled����
			setMobileDataEnabledMethod.invoke(iConMgr, enable);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * ���ط���ģʽ
	 * 
	 * @param enable
	 */
	private void EnableFlyingMode(boolean enable) {
		if (enable) {
			Settings.System.putInt(mActivity.getContentResolver(),
					Settings.System.AIRPLANE_MODE_ON, 1);
			Intent intent = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);
			intent.putExtra("state", true);
			mActivity.sendBroadcast(intent);
		} else {
			Settings.System.putInt(mActivity.getContentResolver(),
					Settings.System.AIRPLANE_MODE_ON, 0);
			Intent intent = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);
			intent.putExtra("state", false);
			mActivity.sendBroadcast(intent);
		}
	}

	/**
	 * ���ϵķ��� ��������GPS�Ƿ����
	 * 
	 * @return
	 */
	private boolean isGpsEnabled() {
		String str = Settings.Secure.getString(mActivity.getContentResolver(),
				Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
		if (str != null) {
			return str.contains("gps");
		} else {
			return false;
		}
	}

	public void Release() {
		if (mCamera != null) {
			CloseLightOff();
		}
	}

	public void Execute(Device device) {
		switch (getCode(device.mDevice)) {
		case BlueTooth: {
			if (!hasBlueTooth()) {
				return;
			}
			if (!(mBlueTooth.isEnabled() ^ device.flag)) {
				return;
			}
			if (mBlueTooth.isEnabled()) {
				EnableBlueTooth(false);
			} else {
				EnableBlueTooth(true);
			}

		}
			break;
		case Flash: {
			if (!hasFlashMode()) {
				return;
			}
			if (!((null != mCamera)

			^ device.flag)) {

				return;
			}
			if ((null != mCamera)) {
				CloseLightOff();

			} else {
				OpenLightOn();
			}
		}
			break;

		case Wifi: {
			if (!hasWiFi()) {
				return;
			}
			if (!(mWifiManager.isWifiEnabled() ^ device.flag)) {
				return;
			}
			if (mWifiManager.isWifiEnabled()) {
				EnableWiFi(false);
			} else {
				EnableWiFi(true);
			}
		}
			break;

		case Gprs: {
			boolean flag = mConnMan.getNetworkInfo(
					ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ? true
					: false;

			if (!(flag ^ device.flag)) {
				return;
			}
			if (device.flag) {
				EnableGprs(false);
			} else {
				EnableGprs(true);
			}
		}
			break;
		case airmode: {
			EnableFlyingMode(true);
		}
			break;
		case Gps: {
			if (!hasGps()) {
				return;
			}
			if (!(isGpsEnabled() ^ device.flag)) {
				return;
			}

			EnableGps();

		}
			break;
		default: {

		}

		}
	}

	public class Device {
		String mDevice;// ���õ�����
		boolean flag;// true��ʶ�ſ��豸,false����ζ�ص�����

		public Device(String device, boolean _flag) {
			mDevice = device;
			flag = _flag;
		}
	}

	/**
	 * �ж��Ƿ���������
	 */
	private boolean hasFlashMode() {
		FeatureInfo[] feature = mActivity.getPackageManager()
				.getSystemAvailableFeatures();
		for (FeatureInfo featureInfo : feature) {
			if (PackageManager.FEATURE_CAMERA_FLASH.equals(featureInfo.name)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * �ж���û��GPS
	 * 
	 * @return
	 */
	private boolean hasGps() {
		FeatureInfo[] feature = mActivity.getPackageManager()
				.getSystemAvailableFeatures();
		for (FeatureInfo featureInfo : feature) {
			if (PackageManager.FEATURE_LOCATION_GPS.equals(featureInfo.name)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * �ж���û������
	 */
	private boolean hasBlueTooth() {
		FeatureInfo[] feature = mActivity.getPackageManager()
				.getSystemAvailableFeatures();
		for (FeatureInfo featureInfo : feature) {
			if (PackageManager.FEATURE_BLUETOOTH.equals(featureInfo.name)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * �ж���û��wifi
	 * 
	 * @return
	 */
	private boolean hasWiFi() {
		FeatureInfo[] feature = mActivity.getPackageManager()
				.getSystemAvailableFeatures();
		for (FeatureInfo featureInfo : feature) {
			if (PackageManager.FEATURE_WIFI.equals(featureInfo.name)) {
				return true;
			}
		}
		return false;
	}

	private int getCode(String device) {
		if (device.equals("wifi")) {
			return Wifi;
		} else if (device.equals("gprs")) {
			return Gprs;
		} else if (device.equals("gps")) {
			return Gps;
		} else if (device.equals("bluetooh")) {
			return BlueTooth;
		} else if (device.equals("flash")) {
			return Flash;
		} else if (device.equals("airplanemode")) {
			return airmode;
		}
		return 0;
	}

}
