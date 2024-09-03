package com.esys.leopardimpdemoapp;
import java.io.InputStream;
import java.io.OutputStream;
import com.evolute.bluetooth.BluetoothComm;
import com.leopard.api.Clscr;
import com.leopard.api.HexString;
import android.annotation.SuppressLint;
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
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class Act_Clscr extends Activity implements OnClickListener {
	/** Called when the activity is first created. */
	private Button butInitialize,butGetatq,butAuthenticate,butPowrdwn;
	private String TAG="Act_clscr";
	Clscr clscr;
	OutputStream outputStream;
	InputStream inputstream;
	Context context=this;
	public static Dialog dlgCustomdialog;
	private LinearLayout llprog;
	private Button btnOk;
	private static ProgressBar pbProgress;
	private int iWidth = 500;
	static ProgressDialog dlgCustom,dlgpd;
	private int iRetval=0;
	private static final int DEVICE_NOTCONNECTED = -100;
	String strHexstring=null, strUID=null;
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.act_clscr);
	    try {
			outputStream = BluetoothComm.mosOut;
			inputstream = BluetoothComm.misIn;
			clscr = new Clscr(Act_Main.setupInstance, outputStream, inputstream);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    butInitialize=(Button) findViewById(R.id.btn_initialize);
	    butInitialize.setOnClickListener(this);
	    butGetatq=(Button)findViewById(R.id.btn_getatq);
	    butGetatq.setOnClickListener(this);
	    butAuthenticate=(Button) findViewById(R.id.btn_sendrecvapdu);
	    butAuthenticate.setOnClickListener(this);
	    butPowrdwn=(Button) findViewById(R.id.btn_powerdown);
	    butPowrdwn.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_initialize:
			Log.e(TAG, "Initialize");
			InitializationAsync initiaize=new InitializationAsync();
			initiaize.execute(0);
		break;
		case R.id.btn_getatq:
			GetAtqAsync getatqasync=new GetAtqAsync();
			getatqasync.execute(0);
		break;
		case R.id.btn_sendrecvapdu:
			SendrecvAsync sendrecvasync=new SendrecvAsync();
			sendrecvasync.execute(0);
		break;
		case R.id.btn_powerdown:
			PowerdownAsync powerdwnasync=new PowerdownAsync();
			powerdwnasync.execute(0);
		break;
		default:
			break;
		}
	}

	public static void progressDialog(Context context, String msg) {
		dlgCustom = new ProgressDialog(context);
		dlgCustom.setMessage(msg);
		dlgCustom.setIndeterminate(true);
		dlgCustom.setCancelable(false);
		dlgCustom.show();
	}

	public class PowerdownAsync extends AsyncTask<Integer, Integer, Integer>{
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressDialog(context, "Please Wait");
		}
		@Override
		protected Integer doInBackground(Integer... params) {
			try {
				iRetval=clscr.iPowerdown();
				Log.e(TAG, "iret value of Power Down..."+iRetval);
			} catch (NullPointerException e) {
				e.printStackTrace();
			}
			return iRetval;
		}
		@Override
		protected void onPostExecute(Integer result) {
			dlgCustom.dismiss();
			if (iRetval==DEVICE_NOTCONNECTED) {
				clscrhandler.obtainMessage(1, "Device not connected").sendToTarget();
			}else if (iRetval==Clscr.SUCCESS) {
				clscrhandler.obtainMessage(1, "Succes").sendToTarget();
			}else if (iRetval==Clscr.DEMO_VERSION) {
				clscrhandler.obtainMessage(1, "Demo version").sendToTarget();
			}else if (iRetval==Clscr.FAILURE) {
				clscrhandler.obtainMessage(1, "Failure").sendToTarget();
			}else if (iRetval==Clscr.ILLEGAL_LIBRARY) {
				clscrhandler.obtainMessage(1, "Illegal Library").sendToTarget();
			}else if (iRetval==Clscr.INACTIVE_PERIPHERAL) {
				clscrhandler.obtainMessage(1, "Inactive Peripheral").sendToTarget();
			}else if (iRetval==Clscr.INVALID_DEVICE_ID) {
				clscrhandler.obtainMessage(1, "Invalid Device ID").sendToTarget();
			}else if (iRetval==Clscr.PARAM_ERROR) {
				clscrhandler.obtainMessage(1, "Param Error").sendToTarget();
			}else if (iRetval==Clscr.READ_TIME_OUT) {
				clscrhandler.obtainMessage(1, "Read Time Out").sendToTarget();
			}
			super.onPostExecute(result);
		}
	}

	public class SendrecvAsync extends AsyncTask<Integer, Integer, Integer>{
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressDialog(context, "Please Wait");
		}
		@Override
		protected Integer doInBackground(Integer... params) {
			try{
			String sUIDauth = "6032FFFFFFFFFFFF"+strUID;
			byte[] bApduCmd = HexString.hexToBuffer(sUIDauth);
			byte[] bRespBuffer = clscr.bSendRecvAPDU(bApduCmd);
			int iRetval = clscr.iGetReturnCode();
			byte[] bcheck={(byte)0x41,(byte)0x14};
			if (bcheck==bRespBuffer) {
				return -3;
			}else {
				Log.e(TAG, "it resulted succes...authetication");
			}
			Log.e(TAG,"brespbuffer..."+HexString.bufferToHex(bRespBuffer) );
			}catch(Exception e){
				e.printStackTrace();
			}
			return iRetval;
		}

		@Override
		protected void onPostExecute(Integer result) {
			dlgCustom.dismiss();
			if (iRetval==DEVICE_NOTCONNECTED) {
				clscrhandler.obtainMessage(1, "Device not connected").sendToTarget();
			}else if (iRetval==Clscr.SUCCESS) {
				clscrhandler.obtainMessage(1, "Succes").sendToTarget();
			}else if (iRetval==Clscr.DEMO_VERSION) {
				clscrhandler.obtainMessage(1, "Demo version").sendToTarget();
			}else if (iRetval==Clscr.FAILURE) {
				clscrhandler.obtainMessage(1, "Failure").sendToTarget();
			}else if (iRetval==Clscr.ILLEGAL_LIBRARY) {
				clscrhandler.obtainMessage(1, "Illegal Library").sendToTarget();
			}else if (iRetval==Clscr.INACTIVE_PERIPHERAL) {
				clscrhandler.obtainMessage(1, "Inactive Peripheral").sendToTarget();
			}else if (iRetval==Clscr.INVALID_DEVICE_ID) {
				clscrhandler.obtainMessage(1, "Invalid Device ID").sendToTarget();
			}else if (iRetval==Clscr.PARAM_ERROR) {
				clscrhandler.obtainMessage(1, "Param Error").sendToTarget();
			}else if (iRetval==Clscr.READ_TIME_OUT) {
				clscrhandler.obtainMessage(1, "Read Time Out").sendToTarget();
			}
			super.onPostExecute(result);
		}
	}

	public class GetAtqAsync extends AsyncTask<Integer, Integer, Integer>{
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressDialog(context, "Pleae Wait");
		}
		@Override
		protected Integer doInBackground(Integer... params) {
			try{
				byte[] bAtq=new byte[300];
				//bresp = new byte[260];
				bAtq = clscr.bGetATQ();
				iRetval=clscr.iGetReturnCode();
			//	atq=Activity_Main.clscr.bGetATQ();
				Log.e(TAG, "atq response.."+bAtq);
				Log.e(TAG, "byte[] respns of GetAtq"+HexString.bufferToHex(bAtq));
				if (iRetval>=0) {
					 strHexstring=HexString.bufferToHex(bAtq);
					 strUID=strHexstring.substring(16, 24);
				}
				Log.e(TAG, "value of byte converted to string..."+strHexstring);
				Log.e(TAG, "substring  value.."+strUID);
				Log.e(TAG, "byte[] respns of GetAtq"+HexString.bufferToHex(bAtq));
			}catch(NullPointerException e){
				e.printStackTrace();
			}
			return iRetval;
		}
		@Override
		protected void onPostExecute(Integer result) {
			dlgCustom.dismiss();
			if (iRetval==DEVICE_NOTCONNECTED) {
				clscrhandler.obtainMessage(1, "Device not connected").sendToTarget();
			}else if (iRetval==Clscr.SUCCESS) {
				clscrhandler.obtainMessage(1, "Succes..UID is.."+strUID).sendToTarget();
			}else if (iRetval==Clscr.DEMO_VERSION) {
				clscrhandler.obtainMessage(1, "Demo version").sendToTarget();
			}else if (iRetval==Clscr.FAILURE) {
				clscrhandler.obtainMessage(1, "Failure").sendToTarget();
			}else if (iRetval==Clscr.ILLEGAL_LIBRARY) {
				clscrhandler.obtainMessage(1, "Illegal Library").sendToTarget();
			}else if (iRetval==Clscr.INACTIVE_PERIPHERAL) {
				clscrhandler.obtainMessage(1, "Inactive Peripheral").sendToTarget();
			}else if (iRetval==Clscr.INVALID_DEVICE_ID) {
				clscrhandler.obtainMessage(1, "Invalid Device ID").sendToTarget();
			}else if (iRetval==Clscr.PARAM_ERROR) {
				clscrhandler.obtainMessage(1, "Param Error").sendToTarget();
			}else if (iRetval==Clscr.READ_TIME_OUT) {
				clscrhandler.obtainMessage(1, "Read Time Out").sendToTarget();
			}
			super.onPostExecute(result);
		}
	}

	public class InitializationAsync extends AsyncTask<Integer, Integer, Integer>{
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			Log.e(TAG, "on preexecute");
			progressDialog(context, "Please Wait");
		}
		@Override
		protected Integer doInBackground(Integer... params) {
			try{
				iRetval=clscr.iInitialize();
				Log.e(TAG, "iInitialize...iretval...."+iRetval);
			}catch(Exception e){
				e.printStackTrace();
			}
			return iRetval;
		}
		@Override
		protected void onPostExecute(Integer result) {
			dlgCustom.dismiss();
			if (iRetval==DEVICE_NOTCONNECTED) {
				clscrhandler.obtainMessage(1, "Device not connected").sendToTarget();
			}else if (iRetval==Clscr.SUCCESS) {
				clscrhandler.obtainMessage(1, "Succes").sendToTarget();
			}else if (iRetval==Clscr.DEMO_VERSION) {
				clscrhandler.obtainMessage(1, "Demo version").sendToTarget();
			}else if (iRetval==Clscr.FAILURE) {
				clscrhandler.obtainMessage(1, "Failure").sendToTarget();
			}else if (iRetval==Clscr.ILLEGAL_LIBRARY) {
				clscrhandler.obtainMessage(1, "Illegal Library").sendToTarget();
			}else if (iRetval==Clscr.INACTIVE_PERIPHERAL) {
				clscrhandler.obtainMessage(1, "Inactive Peripheral").sendToTarget();
			}else if (iRetval==Clscr.INVALID_DEVICE_ID) {
				clscrhandler.obtainMessage(1, "Invalid Device ID").sendToTarget();
			}else if (iRetval==Clscr.PARAM_ERROR) {
				clscrhandler.obtainMessage(1, "Param Error").sendToTarget();
			}else if (iRetval==Clscr.READ_TIME_OUT) {
				clscrhandler.obtainMessage(1, "Read Time Out").sendToTarget();
			}
			else  {
				clscrhandler.obtainMessage(1, "Card not present Out").sendToTarget();
			}
			super.onPostExecute(result);
		}
	}
	
	public void ShowDialog(String str) {
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
	@SuppressLint("HandlerLeak")
	Handler clscrhandler = new Handler(){
 		public void handleMessage(android.os.Message msg) {
 			switch (msg.what) {
			case 1:
				String str = (String) msg.obj;
				ShowDialog(str);
				break;
			default:
				break;
			}
 		};
 	};
}
