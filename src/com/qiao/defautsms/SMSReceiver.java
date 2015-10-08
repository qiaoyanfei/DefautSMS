package com.qiao.defautsms;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

public class SMSReceiver extends BroadcastReceiver {

	public static final String SMS_RECEIVED_ACTION = "android.provider.Telephony.SMS_RECEIVED";

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		if (intent.getAction().equals(SMS_RECEIVED_ACTION)) {
			Log.e("SMSReceiver", "Received!");
			StringBuffer sb = new StringBuffer();
			Bundle bundle = intent.getExtras();
			if (bundle != null) {
				// 通过pdus获得接收到的所有短信消息，获取短信内容；
				Object[] pdus = (Object[]) bundle.get("pdus");
				// 构建短信对象数组；
				SmsMessage[] mges = new SmsMessage[pdus.length];
				for (int i = 0; i < pdus.length; i++) {
					// 获取单条短信内容，以pdu格式存,并生成短信对象；
					mges[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
				}
				for (SmsMessage mge : mges) {
					sb.append("短信来自：" + mge.getDisplayOriginatingAddress()
							+ "\n");
					sb.append("短信内容：" + mge.getMessageBody());

			
				}
				Toast.makeText(context, sb.toString(), Toast.LENGTH_LONG)
						.show();
			}

		}

	}
}
