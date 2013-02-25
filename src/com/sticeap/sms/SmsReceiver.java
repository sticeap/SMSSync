package com.sticeap.sms;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.widget.Toast;

public class SmsReceiver extends BroadcastReceiver
{
	@SuppressLint("NewApi")
	@Override
	public void onReceive(Context context, Intent intent) 
	{
        //---get the SMS message passed in---
        Bundle bundle = intent.getExtras();        
        SmsMessage[] msgs = null;
        if (bundle != null)
        {
            //---retrieve the SMS message received---
            Object[] pdus = (Object[]) bundle.get("pdus");
            msgs = new SmsMessage[pdus.length];            
            for (int i=0; i<msgs.length; i++){            	
            	SmsMessage SMessage = SmsMessage.createFromPdu((byte[]) pdus[i]);
                String sender = SMessage.getOriginatingAddress();
                String body = SMessage.getMessageBody().toString();

                // A custom Intent that will used as another Broadcast
                Intent in = new Intent("SmsMessage.intent.MAIN")
                	.putExtra("nr", sender)
                	.putExtra("msg", body);

                //You can place your check conditions here(on the SMS or the sender)            
                //and then send another broadcast 
                context.sendBroadcast(in);
            }
            //abortBroadcast();
        }
	}
	
	 public void setMessageRead(Context context, String number, String body) {
		 
		 Uri uri = Uri.parse("content://sms/inbox");
		 //Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
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
}