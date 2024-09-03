package com.esys.leopardimpdemoapp;

import java.io.InputStream;
import java.io.OutputStream;

import com.evolute.bluetooth.BluetoothComm;



import com.leopard.api.SAM;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import com.leopard.api.HexString;
public class Act_SAM extends Activity implements OnClickListener {
	Button butInitialize;
	Button butCardstatus;
	Button but_powerdwn;
	SAM sam;
	OutputStream outputStream = null;
	InputStream inputStream = null;
	Context context = this;
	static ProgressDialog dialog;
	private static final boolean bLog = true;
 	int iRetVal;
 	private static final int MSG_DIALOG = 420;
 	private static final String TAG = "SAM API";
	public static final int DEVICENOTCONNECTED = -100;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sam);
		try {
			outputStream =  BluetoothComm.mosOut;
			inputStream =  BluetoothComm.misIn;
			sam = new SAM(Act_Main.setupInstance, outputStream, inputStream);
			Log.e(TAG, "sam is instantiated");
		} catch (Exception e) { 
			e.printStackTrace();
			Log.e(TAG, "error in instantiation.."+e);
		}
		butInitialize=(Button)findViewById(R.id.btn_saminitialize);
		butInitialize.setOnClickListener(this);
		
		butCardstatus=(Button)findViewById(R.id.btn_samsendrecvapdu);
		butCardstatus.setOnClickListener(this);
		
		but_powerdwn=(Button)findViewById(R.id.btn_samPowerdwn);
		but_powerdwn.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_saminitialize:
			SamCardAsyc smartcardasyc = new SamCardAsyc();
			smartcardasyc.execute(0);
			break;
		case R.id.btn_samsendrecvapdu:
			CardStatusAsync cardstatus = new CardStatusAsync();
			cardstatus.execute(0);
			break;
		case R.id.btn_samPowerdwn:
			PowerDown powerdown = new PowerDown();
			powerdown.execute(0);
			break;
		default:
			break;
		}
	}
	/*  To show response messages  */
	public void showdialog(String str) {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
		alertDialogBuilder.setTitle("Leopard Demo Application");
		alertDialogBuilder.setMessage(str).setCancelable(false)
		.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.dismiss();
			}
		});
		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();
		// show it
		alertDialog.show();
	}

	/* Handler to display UI response messages  */
	   Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MSG_DIALOG:
				String strMsg = (String) msg.obj;
				showdialog(strMsg);
				break;
			}
		};
	};
	
	public static void progressDialog(Context context, String msg) {
		dialog = new ProgressDialog(context);
		dialog.setMessage(msg);
		dialog.setIndeterminate(true);
		dialog.setCancelable(false);
		dialog.show();
	}
	
	public class SamCardAsyc extends AsyncTask<Integer, Integer, Integer> {
		byte[] respbuff=new byte[260];
		/* displays the progress dialog untill background task is completed*/
		@Override
		protected void onPreExecute() {
			progressDialog(context, "Please Wait ...");
			super.onPreExecute();
		}
		/* Task of SmartCardAsyc performing in the background*/
		@Override
		protected Integer doInBackground(Integer... params) {
			Log.e(TAG, "iInitalize do in background");
			try {
				byte blevel = 0x00;
				byte[] bRespdata = new byte[300];
				respbuff =sam.bPowerUpCommand(blevel);
				if (respbuff!=null)
				Log.e(TAG, "respbuf..."+HexString.bufferToHex(respbuff));
				else
					Log.e(TAG, "resp error");
				iRetVal = sam.iGetReturnCode();
				Log.e(TAG, "iInitialize iretval...."+iRetVal);
			} catch (NullPointerException e) {
				e.printStackTrace();
				Log.e(TAG, "Null pointer exception.."+e);
				iRetVal = DEVICENOTCONNECTED;
				return iRetVal;
			}
			return iRetVal;
		}

		/* This sends message to handler to display the status messages 
		 * of Diagnose in the dialog box */
		@Override
		protected void onPostExecute(Integer result) {
			Log.e(TAG,"Power up"+iRetVal);
			dialog.dismiss();
			if (respbuff!=null && respbuff.length>0 ) {
				samcardbox();
				handler.obtainMessage(MSG_DIALOG,"SamCard Powerup Success").sendToTarget();
			}else if (iRetVal== sam.DEMO_VERSION) {
				handler.obtainMessage(MSG_DIALOG,
						"Library is in demo version").sendToTarget();
			}else if (iRetVal==sam.INVALID_DEVICE_ID) {
				handler.obtainMessage(MSG_DIALOG,
						"Connected  device is not license authenticated.").sendToTarget();
			}else if (iRetVal==sam.ILLEGAL_LIBRARY) {
				handler.obtainMessage(MSG_DIALOG,
						"Library not Activated").sendToTarget();
			} else if (iRetVal == sam.FAILURE) {
				handler.obtainMessage(MSG_DIALOG,"Operation failed").sendToTarget();
			}  else if (iRetVal == sam.CARD_REMOVED) {
				handler.obtainMessage(MSG_DIALOG,"Sam Card  removed").sendToTarget();
			} else if (iRetVal == sam.SUCCESS) {
				samcardbox();
			} else if (iRetVal ==  sam.READ_TIME_OUT) {
				handler.obtainMessage(MSG_DIALOG,"Upon time out for read expires").sendToTarget();
			} else if (iRetVal == sam.PARAM_ERROR) {
				handler.obtainMessage(MSG_DIALOG,"Upon incorrect number of parameters has been sent").sendToTarget();
			}  else if (iRetVal == sam.NOT_INSERTED) {
				handler.obtainMessage(MSG_DIALOG,"Sam card not present").sendToTarget();
			}else {
				handler.obtainMessage(MSG_DIALOG,"Unsuccessful operation").sendToTarget();
			}
			super.onPostExecute(result);
		}
	}
	
	public class CardStatusAsync extends AsyncTask<Integer, Integer, Integer> {
		/* displays the progress dialog until background task is completed*/
		@Override
		protected void onPreExecute() {
			progressDialog(context, "Please Wait ...");
			super.onPreExecute();
		}
		/* Task of CardStatusAsync performing in the background*/
		@Override
		protected Integer doInBackground(Integer... params) {
			try {
				iRetVal = sam.iGetCardStatus();
			} catch (NullPointerException e) {
				iRetVal = DEVICENOTCONNECTED;
				return iRetVal;
			}
			return iRetVal;
		}

		/* This sends message to handler to display the status messages 
		 * of Diagnose in the dialog box */
		@Override
		protected void onPostExecute(Integer result) {
			dialog.dismiss();
			if (iRetVal == DEVICENOTCONNECTED) {
				handler.obtainMessage(DEVICENOTCONNECTED,"Device not Connected").sendToTarget();
			} else if (iRetVal == sam.FAILURE) {
				handler.obtainMessage(MSG_DIALOG,"Card Status Failed").sendToTarget();
			}   else if (iRetVal == sam.CARD_ABORTED) {
				handler.obtainMessage(MSG_DIALOG,"Sam Card is present").sendToTarget();
			}  else if (iRetVal ==  sam.READ_TIME_OUT) {
				handler.obtainMessage(MSG_DIALOG,"Upon time out for read expires").sendToTarget();
			} else if (iRetVal == sam.PARAM_ERROR) {
				handler.obtainMessage(MSG_DIALOG,"Upon incorrect number of parameters has been sent").sendToTarget();
			}else if (iRetVal == sam.NOT_INSERTED) {
				handler.obtainMessage(MSG_DIALOG,"Please insert sam card").sendToTarget();
			}else if (iRetVal== sam.DEMO_VERSION) {
				handler.obtainMessage(MSG_DIALOG,
						"Library is in demo version").sendToTarget();
			}else if (iRetVal==sam.INVALID_DEVICE_ID) {
				handler.obtainMessage(MSG_DIALOG,
						"Connected  device is not license authenticated.").sendToTarget();
			}  else if (iRetVal==sam.INSERTED_AND_POWERED) {
				handler.obtainMessage(MSG_DIALOG,
						"SAM card Inserted and Poweredd up").sendToTarget();
			}  
			super.onPostExecute(result);
		}
	}
	
	public class PowerDown extends AsyncTask<Integer, Integer, Integer> {
		/* displays the progress dialog until background task is completed*/
		@Override
		protected void onPreExecute() {
			Log.e(TAG, "Power down.onpreexecute...");
			progressDialog(context, "Please Wait ...");
			super.onPreExecute();
		}
	/* Task of CardStatusAsync performing in the background*/
		@Override
		protected Integer doInBackground(Integer... params) {
			try {
				iRetVal = sam.iPowerDown();	
				Log.e(TAG, "Powerdown....."+iRetVal);
			} catch (NullPointerException e) {
				iRetVal = DEVICENOTCONNECTED;
				return iRetVal;
			}
			return iRetVal;
		}

		/* This sends message to handler to display the status messages 
		 * of Diagnose in the dialog box */
		@Override
		protected void onPostExecute(Integer result) {
			dialog.dismiss();
			if (iRetVal == DEVICENOTCONNECTED) {
				handler.obtainMessage(MSG_DIALOG,"Device not Connected").sendToTarget();
			} else if (iRetVal == sam.FAILURE) {
				handler.obtainMessage(MSG_DIALOG,"Operation Failed").sendToTarget();
            } else if (iRetVal == sam.CARD_ABORTED) {
				handler.obtainMessage(MSG_DIALOG,"Sam Card is aborted").sendToTarget();
			} else if (iRetVal == sam.BYTE_READING_ERR) {
				handler.obtainMessage(MSG_DIALOG,"Sam Card Reader Error").sendToTarget();
			} else if (iRetVal == sam.UNKNOWN_READER_CMD) {
				handler.obtainMessage(MSG_DIALOG,"Unknown reader command").sendToTarget();
			} else if (iRetVal == sam.READ_TIME_OUT) {
				handler.obtainMessage(MSG_DIALOG,"Time Out").sendToTarget();
			} else if (iRetVal == sam.PARAM_ERROR) {
				handler.obtainMessage(MSG_DIALOG,"Upon incorrect number of parameters has been sent").sendToTarget();
			}else if (iRetVal == sam.NOT_INSERTED) {
				handler.obtainMessage(MSG_DIALOG,"Please insert sam card").sendToTarget();
			}else if (iRetVal== sam.DEMO_VERSION) {
				handler.obtainMessage(MSG_DIALOG,"Library is in demo version").sendToTarget();
			}else if (iRetVal==sam.INVALID_DEVICE_ID) {
				handler.obtainMessage(MSG_DIALOG,"Connected  device is not license authenticated.").sendToTarget();
			}else if (iRetVal==sam.ILLEGAL_LIBRARY) {
				handler.obtainMessage(MSG_DIALOG,"Library not Activated").sendToTarget();
			}else if (iRetVal == sam.SUCCESS) {
					handler.obtainMessage(MSG_DIALOG,"Power down success").sendToTarget();
				}
			super.onPostExecute(result);
		}
	}
	public void samcardbox() {
		 // deprecated
			final TextView sendapdu_tv;
			final Button sendapdu_but;
			final Dialog smartdialog = new Dialog(context);
			smartdialog.setContentView(R.layout.customsam);
			smartdialog.setTitle("Sam Card");
			smartdialog.setCancelable(true);
			sendapdu_tv = (TextView) smartdialog.findViewById(R.id.sendapdu_tv1);
			TextView textView1 = (TextView) smartdialog.findViewById(R.id.textView18);
			//textView1.setWidth(width);
			sendapdu_but = (Button) smartdialog.findViewById(R.id.sendapdu_but1);
			sendapdu_but.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Log.e(TAG, "sendapdu button onclick");
					v.setEnabled(false);
					String str = "";
					int len = 0;
					int iReturnvalue = 0;
					byte[] responsebuf = new byte[500];
					String apdu;
					byte[] apdcmd;
					try {
						//apdu="00A4000400";
						Log.e(TAG, "inside try");
						apdu="8060000000";
						/* apdcmd = HexString.hexToBuffer(apdu);
						 Log.e(TAG, "command  is...."+HexString.bufferToHex(apdcmd));
						 responsebuf = sam.bSendReceiveApduCmd(apdu);
						iReturnvalue = sam.iGetReturnCode();
						Log.e(TAG, "Apdu ireturnvalue...."+iReturnvalue);
						len = iReturnvalue;
						if (len>0) {
							str = HexString.bufferToHex(responsebuf, 0, len);
							if (bLog) Log.d(TAG,"Response  1 Data: " + str);
						}*/
						responsebuf = new byte[500];
						Log.e(TAG, "1");
					//	apdu="00A40004027000";
						// apdcmd = HexString.hexToBuffer(apdu);
						responsebuf = sam.bSendReceiveApduCmd(apdu);
						Log.e(TAG, "2");
						Log.e(TAG, "respnsebuff...");
						Log.e(TAG, "response buf...."+HexString.bufferToHex(responsebuf));
						int sendapdu3 = sam.iGetReturnCode();
						Log.e(TAG, "3");
						Log.e(TAG, "return value...."+sendapdu3);
						if (bLog) Log.d(TAG,"Response 3 Data three" + sendapdu3);
						String str3 ="";
						if (sendapdu3>0) {
							str3 = HexString.bufferToHex(responsebuf, 0,sendapdu3);
						}
						if (bLog) Log.d(TAG,"App Select MF :\n" 
								+ str + "\n" 
								+ "Select DF 7000 :\n" + str3 + "\n");
						if((str.equals("")&(str3.equals("")))) {
							handler.obtainMessage(1,"Sam Card is secured.Please Insert another card").sendToTarget();
						}else{
							sendapdu_tv.setText("Select MF :\n" + str + "\n"
									+ "Select DF 7000 :\n" + str3 + "\n");
						}
					} catch (Exception e) {
						Log.e(TAG, "Exception....."+e);
					}
				}
			});
		    smartdialog.show();
		}
}
