package io.raas;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

public class MyServerActivity extends Activity {
	Socket s;
	ServerSocket ss = null;
	String mClientMsg = "";
	Thread myCommsThread = null;

	protected static final int MSG_ID = 0x1337;
	public static final int SERVERPORT = 6000;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		TextView tv = (TextView) findViewById(R.id.TextView01);
		tv.setText("Nothing from client yet");
		this.getDataFromBoard(); // arduino to android
		//this.sendDataToBoard("*"); // android to arduino
	}

	@Override
	protected void onStop() {
		super.onStop();
		try {
			// make sure you close the socket upon exiting
			ss.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	Handler myUpdateHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_ID:
				/////////////////////////////////////////////////////////////
				// Code to do whatever with the message received from arduino

				TextView tv = (TextView) findViewById(R.id.TextView01);
				tv.setText(mClientMsg);

				/////////////////////////////////////////////////////////////
				break;
			default:
				break;
			}
			super.handleMessage(msg);
		}
	};

	// Read data received from arduino (to android)
	public void getDataFromBoard() {

		try {
			ss = new ServerSocket(SERVERPORT );
		} catch (IOException e) {
			e.printStackTrace();
		}
		while (!Thread.currentThread().isInterrupted()) {
			Message m = new Message();
			m.what = MSG_ID;
			try {
				if (s == null)
					s = ss.accept();
				BufferedReader input = new BufferedReader(new InputStreamReader(s.getInputStream()));
				String clientSentence = null;
				clientSentence = input.readLine();
				////////////////////////////////////////////////
				// clientSentence has data received from arduino
				// do whatever with the clientSentence
				// We capitalize the clientSentence in this case
				// and write it to the output stream
				 String capitalizedSentence = clientSentence.toUpperCase() + '\n';
				 DataOutputStream outToClient = new DataOutputStream(s.getOutputStream());
				 outToClient.writeBytes(capitalizedSentence);
				////////////////////////////////////////////////
				
				mClientMsg = clientSentence;
				myUpdateHandler.sendMessage(m);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void sendDataToBoard(String strToSendToArduino) {
		try {
			ss = new ServerSocket(SERVERPORT );
		} catch (IOException e) {
			e.printStackTrace();
		}
		while (!Thread.currentThread().isInterrupted()) {
			Message m = new Message();
			m.what = MSG_ID;
			try {
				if (s == null)
					s = ss.accept();
				////////////////////////////////////////////////
				// clientSentence has data received from arduino
				// do whatever with the clientSentence
				// We capitalize the clientSentence in this case
				String capitalizedSentence = strToSendToArduino;
				DataOutputStream outToClient = new DataOutputStream(s.getOutputStream());
				outToClient.writeBytes(capitalizedSentence);
				//


				myUpdateHandler.sendMessage(m);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	} // runa

}

