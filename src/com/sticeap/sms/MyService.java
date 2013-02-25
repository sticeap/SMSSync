package com.sticeap.sms;

import java.io.IOException;
import java.net.UnknownHostException;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class MyService extends Service {
	private static final String TAG = "MyService";
	private BroadcastReceiver mIntentReceiver;
	public SmsServer s;
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onCreate() {
		//Toast.makeText(this, "My Service Created", Toast.LENGTH_LONG).show();
		Log.d(TAG, "onCreate");
	}
	
	 public void setMessageRead(Context context, String number, String body) {
		 
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		Uri uri = Uri.parse("content://sms/inbox");
		Cursor cursor = context.getContentResolver().query(uri, null, "address = ?", new String[] { number }, "date desc limit 1");

		while (cursor.moveToNext()) {
			if (cursor.getString(cursor.getColumnIndex("address")).equals(number)) {// && (cursor.getInt(cursor.getColumnIndex("read")) == 0)   || cursor.getString(cursor.getColumnIndex("address")).endsWith(number)
				Toast.makeText(context, "aici am gasit nr de tel", Toast.LENGTH_SHORT).show();
				if (cursor.getString(cursor.getColumnIndex("body")).startsWith(body) || cursor.getString(cursor.getColumnIndex("body")).equals(body)) {
					Toast.makeText(context, "aici am gasit sms-ul", Toast.LENGTH_SHORT).show();
					String SmsMessageId = cursor.getString(cursor.getColumnIndex("_id"));
					ContentValues values = new ContentValues();
				 		values.put("read", 1);
				 
				 	context.getContentResolver().update(Uri.parse("content://sms/inbox"), values, "_id=" + SmsMessageId, null);
				 	return;
				 }
			 }
		 }
	 }

	@Override
	public void onDestroy() {
		//Toast.makeText(this, "My Service Stopped", Toast.LENGTH_LONG).show();
		unregisterReceiver(mIntentReceiver);
		Log.d(TAG, "onDestroy");
		CancelNotification(getApplicationContext(), 1);
		
		try {
			s.stop();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		Log.i("serverSocket","Server stopped...");
	}
	
	@Override
	public void onStart(Intent intent, int startid) {
		//Toast.makeText(this, "My Service Started", Toast.LENGTH_LONG).show();
		Log.d(TAG, "onStart");
		startBroadcast();
		createNotification(getApplicationContext(), 1);
		
		int defaultPort = 4444;
		String ip = Utils.getIPAddress(true);
		
		try {
			s = new SmsServer( ip, defaultPort);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		s.start();
		Log.i("serverSocket", "server IP: " + ip);
		
	}
	
	
	public void startBroadcast(){
		IntentFilter intentFilter = new IntentFilter("SmsMessage.intent.MAIN");
        mIntentReceiver = new BroadcastReceiver() {
	        @Override
	        public void onReceive(Context context, Intent intent) {
		        String nr = intent.getStringExtra("nr");
		        String msg = intent.getStringExtra("msg");
		        
		        setMessageRead(context, nr, msg);
	
	        }
        };
        this.registerReceiver(mIntentReceiver, intentFilter);
	}
	
    @SuppressLint("NewApi")
	public void createNotification(Context context, int NOTIFICATION_ID){
    	// this
    	String ns = Context.NOTIFICATION_SERVICE;
    	NotificationManager mNotificationManager = (NotificationManager) getSystemService(ns);

    	int icon = R.drawable.ic_launcher;        
    	CharSequence tickerText = "SMS Sync - started"; // ticker-text
    	long when = System.currentTimeMillis();         
    	//Context context = getApplicationContext();     
    	CharSequence contentTitle = "SMS Sync";  
    	CharSequence contentText = "Sync SMS and Call";      
    	Intent notificationIntent = new Intent(this, SmsActivity.class);
    	PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
		Notification notification = new Notification(icon, tickerText, when);
	    	notification.flags = Notification.FLAG_ONGOING_EVENT;
	    	notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);

    	mNotificationManager.notify(NOTIFICATION_ID, notification);
    	
    }
    
    public void CancelNotification(Context context, int NOTIFICATION_ID) {
        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager nMgr = (NotificationManager) context.getSystemService(ns);
        nMgr.cancel(NOTIFICATION_ID);
    }
}