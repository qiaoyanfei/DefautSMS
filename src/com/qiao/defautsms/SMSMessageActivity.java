package com.qiao.defautsms;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Telephony;
import android.telephony.SmsManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class SMSMessageActivity extends Activity {
	
	private EditText mPhoneNum;
	private EditText mBody;
	private Uri  mUri;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mPhoneNum=(EditText)findViewById(R.id.phoneNum);
		mBody=(EditText)findViewById(R.id.smsInfo);
		((Button)findViewById(R.id.save)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				saveMsg(mPhoneNum.getText().toString(),mBody.getText().toString());
				
			}
		});
		
	((Button)findViewById(R.id.delete)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(mUri != null)
				{
					delMsg(mUri);
				}
				
			}
		});
	((Button)findViewById(R.id.send)).setOnClickListener(new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			sendMsg(mPhoneNum.getText().toString(),mBody.getText().toString());
			
		}
	});
	
	
		final String myPackageName = getPackageName();
		if ((Build.VERSION.RELEASE).startsWith("4.4")
				&& (!Telephony.Sms.getDefaultSmsPackage(this).equals(
						myPackageName))) {
			
			dialog(Build.VERSION.RELEASE,myPackageName);
		}
	
			
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void saveMsg(String address, String body)
	{
		ContentValues values = new ContentValues();
		values.put("address", address);
		values.put("body", body);
		values.put("date", System.currentTimeMillis());
		mUri = getContentResolver().insert(Uri.parse("content://sms/inbox"), values);
	}
	private void delMsg(Uri uri)
	{
		getContentResolver().delete(uri, null, null);
	}
	
	private void sendMsg(String address, String body) {

		ArrayList<String> msgs = null;
		SmsManager smsManager = SmsManager.getDefault();
		msgs = smsManager.divideMessage(body);
		int messageCount = msgs.size();
		ArrayList<PendingIntent> sentIntents = new ArrayList<PendingIntent>(
				messageCount);
		for (int k = 0; k < messageCount; k++) {

			Intent intent = new Intent("com.qiao.defautsms.SMS_SENT");
			intent.putExtra("ADDRESS", address);

			if (k == messageCount - 1) {
				// Changing the requestCode so that a different pending
				// intent
				// is created for the last fragment with
				// EXTRA_MESSAGE_SENT_SEND_NEXT set to true.
				intent.putExtra("EXTRA_MESSAGE_SENT_SEND_NEXT", true);
				sentIntents.add(PendingIntent.getBroadcast(this,
						(int) System.currentTimeMillis(), intent,
						PendingIntent.FLAG_UPDATE_CURRENT));
			} else {
				intent.putExtra("EXTRA_MESSAGE_SENT_SEND_NEXT", false);
				sentIntents.add(PendingIntent.getBroadcast(this,
						(int) System.currentTimeMillis(), intent,
						PendingIntent.FLAG_UPDATE_CURRENT));
			}

		}
		smsManager.sendMultipartTextMessage(address, null, msgs, sentIntents,
				null);
	}
	
	protected void dialog(String osVersion,final String myPackageName) {
		AlertDialog.Builder builder = new Builder(this);
		builder.setMessage("检测当前SDK版本为Android"
				+ osVersion + ",若想使用短信功能，须将该应用设置为默认短信APP");
		builder.setTitle("提示");
		builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				Intent intent = new Intent(
						Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
				intent.putExtra(
						Telephony.Sms.Intents.EXTRA_PACKAGE_NAME,
						myPackageName);
				startActivity(intent);
			}
			});
		
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				SMSMessageActivity.this.finish();
				}
			});
		builder.create().show();
		}
	

}
