package com.sticeap.sms;

import android.content.Context;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Collection;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import com.google.gson.Gson;


public class SmsServer extends WebSocketServer {
	public Gson gson;
	public Data data;
	
	public SmsServer(String host, int port) throws UnknownHostException, IOException{		
		super (new InetSocketAddress(host, port));
		
		gson = new Gson();
	}
	
	public void updateTV(final String str1){
        /*context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
            	String string = context.text.getText().toString();
                context.text.setText(string + "\n" +str1);
                context.text.setMovementMethod(new ScrollingMovementMethod());
            }
        });*/
		Log.i("serverSocket", str1);
    }
	
	public void setActivity(){	
		//this.text.addLog("=======");
		updateTV("Start Activity");
	}
	
	@Override
	public void onOpen( WebSocket conn, ClientHandshake handshake ) {
		this.sendToAll( "new connection: " + handshake.getResourceDescriptor() );
		data = new Data();
		data.response = "CONNECTED";
		data.message = "Someone connected to server!";
		data.status = true;
		
		//Log.i("socketServer", conn.getRemoteSocketAddress().getAddress().getHostAddress() + " entered the room!" );
		//Log.i("socketServer", log );
		updateTV(conn.getRemoteSocketAddress().getAddress().getHostAddress() + " entered the room!");
	}
	
	@Override
	public void onClose( WebSocket conn, int code, String reason, boolean remote ) {
		this.sendToAll( conn + " has left the room!" );
		//Log.i("socketServer", conn + " has left the room!" );
		updateTV(conn + " has left the room!" );
	}

	@Override
	public void onMessage( WebSocket conn, String message ) {
		this.sendToAll( message );
		//Log.i("socketServer", conn + ": " + message );
		updateTV(conn.getRemoteSocketAddress() + " > " + message);
		Data ms = gson.fromJson(message, Data.class);
		if(ms.name != null){
			updateTV(ms.name + " > identified");
		}
		if(message.equals("close")){
			conn.close(0);
		}
	}

	@Override
	public void onError( WebSocket conn, Exception ex ) {
		ex.printStackTrace();
		if( conn != null ) {
			// some errors like port binding failed may not be assignable to a specific websocket
		}
	}

	/**
	 * Sends <var>text</var> to all currently connected WebSocket clients.
	 * 
	 * @param text
	 *            The String to send across the network.
	 * @throws InterruptedException
	 *             When socket related I/O errors occur.
	 */
	public void sendToAll( String text ) {
		Collection<WebSocket> con = connections();
		synchronized ( con ) {
			for( WebSocket c : con ) {
				c.send( text );
			}
		}
	}

}