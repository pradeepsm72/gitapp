package com.esys.leopardimpdemoapp;

import java.io.InputStream;
import java.io.OutputStream;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.esys.leopardimpdemoapp.R;
import com.evolute.bluetooth.BluetoothComm;
import com.leopard.api.HexString;
public class Act_Serial extends Activity implements OnClickListener{
	private static final String TAG = "SerialPort11API";
    private static final boolean D = false;//BluetoothConnect.D;
	private com.leopard.api.SerialPort1 SerialPort1;
	private com.leopard.api.SerialPort2 SerialPort22;
	InputStream inputStream;
	OutputStream outputStream;
	private Button btn_ScanBarcode1,btn_Senddata1,btn_ScanBarcode2,btn_Senddata2;
	/*   List of Return codes for the respective response */
	private static final int DEVICENOTCONNECTED = -100;
	private static final int SP_ENTERSOMEDATE =-101;
	private static ProgressDialog dlgPg;
	Context context = this;
	private int iRetVal;
	private final static int MESSAGE_BOX = 1;
	private String scanebarcodedata = null;
	private String str1 ;
	byte bdata[];
	LinearLayout second02,second01;
	Button SerialPort11,SerialPort2;
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_serialport1);
		
		second02 = (LinearLayout)findViewById(R.id.second02);
		second01 = (LinearLayout)findViewById(R.id.second01);
		
		SerialPort11 = (Button)findViewById(R.id.btn_SerialPort11);
		SerialPort11.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				second01.setVisibility(View.VISIBLE);
				second02.setVisibility(View.GONE);
			}
		});
		
		SerialPort2 = (Button)findViewById(R.id.btn_SerialPort2);
		SerialPort2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				second02.setVisibility(View.VISIBLE);
				second01.setVisibility(View.GONE);
			}
		});
		
		btn_Senddata1 = (Button)findViewById(R.id.btn_Senddata1);
		btn_Senddata1.setOnClickListener(this);

		btn_ScanBarcode1 = (Button)findViewById(R.id.btn_ScanBarcode1);
		btn_ScanBarcode1.setOnClickListener(this);

		btn_Senddata2 = (Button)findViewById(R.id.btn_Senddata2);
		btn_Senddata2.setOnClickListener(this);

		btn_ScanBarcode2 = (Button)findViewById(R.id.btn_ScanBarcode2);
		btn_ScanBarcode2.setOnClickListener(this);  
		
		/*obtain the input and output streams from the Bluetooth connection */
		try {
			
			inputStream = BluetoothComm.misIn;//BluetoothConnect.mBluetoothService.mmInStream;
			outputStream = BluetoothComm.mosOut;//BluetoothConnect.mBluetoothService.mmOutStream;
			SerialPort1 = new com.leopard.api.SerialPort1(Act_Main.setupInstance, outputStream, inputStream);
			SerialPort22 = new com.leopard.api.SerialPort2(Act_Main.setupInstance, outputStream, inputStream);
		}catch(Exception e) { }
	}
	
	/* Handler to display UI response messages  */
	@SuppressLint("HandlerLeak")
	Handler serialhand = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {

			switch (msg.what) {
			case MESSAGE_BOX:
				String str = (String) msg.obj;
				showdialog(str);
				break;
			
			default:
				break;
			}
		
			};
	};
	
	//Button Events
	@SuppressWarnings("static-access")
	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.btn_ScanBarcode1:	
			/* ScanBarcodAsyc  undergoes AsynTask operation*/
			ShowSendData();
		break;
		case R.id.btn_Senddata1:
			scanebarcodedata = "";
			ScanBarcodAsyc scanbarcodAsyc = new ScanBarcodAsyc();
			scanbarcodAsyc.execute(0);
			break;
		case R.id.btn_ScanBarcode2:	
			/* ScanBarcodAsyc  undergoes AsynTask operation*/
			ShowSendData1();
		break;
		case R.id.btn_Senddata2:
			scanebarcodedata = "";
			ScanBarcodAsyc1 scanbarcodAsyc1 = new ScanBarcodAsyc1();
			scanbarcodAsyc1.execute(0);
			break;
		default:
			break;
		}
	}
	
	EditText edt_Entetext;
	public void ShowSendData() {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle("Senddata");
		builder.setMessage("Enter String");
		edt_Entetext = new EditText(context);
		builder.setView(edt_Entetext);
		builder.setPositiveButton("SendData", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				/* SendDataAsyc  undergoes AsynTask operation*/
				SendDataAsyc senddataAsyc =  new SendDataAsyc();
				senddataAsyc.execute(0);
			}
		});
		builder.show();
	}
	//EditText edt_Entetext;
	public void ShowSendData1() {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle("Senddata");
		builder.setMessage("Enter String");
		edt_Entetext = new EditText(context);
		builder.setView(edt_Entetext);
		builder.setPositiveButton("SendData", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				/* SendDataAsyc  undergoes AsynTask operation*/
				SendDataAsyc1 senddataAsyc =  new SendDataAsyc1();
				senddataAsyc.execute(0);
			}
		});
		builder.show();
	}
	
	/*   This method shows the SendData  AsynTask operation */
	public class SendDataAsyc extends AsyncTask<Integer, Integer, Integer> {
		/* displays the progress dialog until background task is completed*/
		@Override
		protected void onPreExecute() {
			progressDialog(context, "Please Wait");
			super.onPreExecute();
		}
		/* Task of SendDataAsyc performing in the background*/
		@Override
		protected Integer doInBackground(Integer... params) {
			try
			{
			String str = edt_Entetext.getText().toString();
			if(str.length()>1)
			{
			String stBinaryData = HexString.bufferToHex(str.getBytes());
			SerialPort1.vSendData(stBinaryData);
			iRetVal=0;
			iRetVal  = SerialPort1.iGetReturnCode();
			}else{
				iRetVal = SP_ENTERSOMEDATE;
				return iRetVal;
			}
			}catch(Exception e)
			{
				iRetVal = DEVICENOTCONNECTED;
				return iRetVal;
			}
			return iRetVal;
		}
		/* This sends message to handler to display the status messages 
		 * of Diagnose in the dialog box */
		@Override
		protected void onPostExecute(Integer result) {
			dlgPg.dismiss();
			if(iRetVal==SP_ENTERSOMEDATE)
			{
			serialhand.obtainMessage(MESSAGE_BOX, "Please Enter Text").sendToTarget();	
			}else if(iRetVal == DEVICENOTCONNECTED){
				serialhand.obtainMessage(DEVICENOTCONNECTED, "Device not Connected").sendToTarget();	
				}
				else if(iRetVal == com.leopard.api.SerialPort1.SP1_SUCCESS ){
					serialhand.obtainMessage(MESSAGE_BOX, "Successful Operation").sendToTarget();
				}else if (iRetVal == com.leopard.api.SerialPort1.SP1_FAILED) {
					serialhand.obtainMessage(MESSAGE_BOX,"Unsuccessful Operation").sendToTarget();
				}else if (iRetVal == com.leopard.api.SerialPort1.SP1_NO_DATA) {
					serialhand.obtainMessage(MESSAGE_BOX,"Data not available").sendToTarget();
				}else if (iRetVal == com.leopard.api.SerialPort1.SP1_TIME_OUT) {
					serialhand.obtainMessage(MESSAGE_BOX,"Time out").sendToTarget();
				}else if (iRetVal == com.leopard.api.SerialPort1.SP1_PARAM_ERROR) {
					serialhand.obtainMessage(MESSAGE_BOX,"Parameter error").sendToTarget();
				}else if (iRetVal == com.leopard.api.SerialPort1.SP1_ILLEGAL_LIBRARY) {
					serialhand.obtainMessage(MESSAGE_BOX,"Illegal library").sendToTarget();
				}else if (iRetVal == com.leopard.api.SerialPort1.SP1_DEMO_VERSION) {
					serialhand.obtainMessage(MESSAGE_BOX,"Serial port in Demo version").sendToTarget();
				}else if (iRetVal == com.leopard.api.SerialPort1.SP1_INACTIVE_PERIPHERAL) {
					serialhand.obtainMessage(com.leopard.api.SerialPort1.SP1_INACTIVE_PERIPHERAL,"Inactive peripheral").sendToTarget();
				}else if (iRetVal == com.leopard.api.SerialPort1.SP1_INVALID_DEVICE_ID) {
					serialhand.obtainMessage(MESSAGE_BOX,"Device is not license authenticated").sendToTarget();
				}
			super.onPostExecute(result);
		}
	}
	/*   This method shows the SendData  AsynTask operation */
	public class SendDataAsyc1 extends AsyncTask<Integer, Integer, Integer> {
		/* displays the progress dialog until background task is completed*/
		@Override
		protected void onPreExecute() {
			progressDialog(context, "Please Wait");
			super.onPreExecute();
		}
		/* Task of SendDataAsyc performing in the background*/
		@Override
		protected Integer doInBackground(Integer... params) {
			str1="";
			try
			{
			String str = edt_Entetext.getText().toString();
			if(str.length()>1)
			{
			String stBinaryData = HexString.bufferToHex(str.getBytes());
			SerialPort22.vSendData(stBinaryData);
			iRetVal=0;
			iRetVal  = SerialPort22.iGetReturnCode();
			}else{
				iRetVal = SP_ENTERSOMEDATE;
				return iRetVal;
			}
			}catch(Exception e)
			{
				iRetVal = DEVICENOTCONNECTED;
				return iRetVal;
			}
			return iRetVal;
		}
		/* This sends message to handler to display the status messages 
		 * of Diagnose in the dialog box */
		@Override
		protected void onPostExecute(Integer result) {
			dlgPg.dismiss();
			if(iRetVal==SP_ENTERSOMEDATE)
			{
			serialhand.obtainMessage(MESSAGE_BOX, "Please Enter Text").sendToTarget();	
			}else if(iRetVal == DEVICENOTCONNECTED){
				serialhand.obtainMessage(DEVICENOTCONNECTED, "Device not Connected").sendToTarget();	
				//serialhand.obtainMessage(1, "Data Received:\n"+HexString.bufferToHex(bdata)).sendToTarget();
				Log.d(TAG,"Inside onPostExecute.....<<<<<<6>>>>"+scanebarcodedata);
				}
				else if(iRetVal == com.leopard.api.SerialPort2.SP2_SUCCESS){	
					serialhand.obtainMessage(MESSAGE_BOX, "Successful Operation").sendToTarget();
				}else if (iRetVal == com.leopard.api.SerialPort2.SP2_FAILED) {
					serialhand.obtainMessage(MESSAGE_BOX,"Unsuccessful Operation").sendToTarget();
				}else if (iRetVal == com.leopard.api.SerialPort2.SP2_NO_DATA) {
					serialhand.obtainMessage(MESSAGE_BOX,"Data not available").sendToTarget();
				}else if (iRetVal == com.leopard.api.SerialPort2.SP2_TIME_OUT) {
					serialhand.obtainMessage(MESSAGE_BOX,"Time out").sendToTarget();
				}else if (iRetVal == com.leopard.api.SerialPort2.SP2_PARAM_ERROR) {
					serialhand.obtainMessage(MESSAGE_BOX,"Parameter error").sendToTarget();
				}else if (iRetVal == com.leopard.api.SerialPort2.SP2_ILLEGAL_LIBRARY) {
					serialhand.obtainMessage(MESSAGE_BOX,"Illegal library").sendToTarget();
				}else if (iRetVal == com.leopard.api.SerialPort2.SP2_DEMO_VERSION) {
					serialhand.obtainMessage(MESSAGE_BOX,"Serial port in Demo version").sendToTarget();
				}else if (iRetVal == com.leopard.api.SerialPort2.SP2_INACTIVE_PERIPHERAL) {
					serialhand.obtainMessage(com.leopard.api.SerialPort2.SP2_INACTIVE_PERIPHERAL,"Inactive peripheral").sendToTarget();
				}else if (iRetVal == com.leopard.api.SerialPort1.SP1_INVALID_DEVICE_ID) {
					serialhand.obtainMessage(MESSAGE_BOX,"Device is not license authenticated").sendToTarget();
				}
			super.onPostExecute(result);
		}
	}
	
	/*   This method shows the ScanBarcode data  AsynTask operation */
	public class ScanBarcodAsyc extends AsyncTask<Integer, Integer, Integer> {
		/* displays the progress dialog until background task is completed*/
		@Override
		protected void onPreExecute() {
			progressDialog(context, "Please Wait...");
			super.onPreExecute();
		}
		/* Task of ScanBarcode performing in the background*/
		@Override
		protected Integer doInBackground(Integer... params) {
			str1="";
			try{
				Log.d(TAG,"Inside doinbackground.....<<<<<<3>>>>"+scanebarcodedata);
				scanebarcodedata = new String("");	
				bdata = SerialPort1.bReadBinaryData(10000, 50);
				iRetVal = SerialPort1.iGetReturnCode();
				 Log.d(TAG,"Inside doinbackground.....<<<<<<4>>>>"+scanebarcodedata);
			   }catch (Exception e) {
				iRetVal = DEVICENOTCONNECTED;
				return iRetVal;
			}
			return iRetVal;
		}
		/* This sends message to handler to display the status messages 
		 * of Diagnose in the dialog box */
		@SuppressWarnings("static-access")
		@Override
		protected void onPostExecute(Integer result) {
			dlgPg.dismiss();
			if(iRetVal == DEVICENOTCONNECTED){
				serialhand.obtainMessage(DEVICENOTCONNECTED, "Device not Connected").sendToTarget();	
				}
				else if(iRetVal == com.leopard.api.SerialPort1.SP1_SUCCESS ||iRetVal == com.leopard.api.SerialPort1.SP1_PARTIAL_DATA_RECEIVED){	
					 Log.d(TAG,"Inside onPostExecute.....<<<<<<5>>>>"+scanebarcodedata);
					// serialhand.obtainMessage(MESSAGE_BOX, "Data Received:"+"\n"+scanebarcodedata).sendToTarget();
					 serialhand.obtainMessage(1, "Data Received:\n"+HexString.bufferToHex(bdata)).sendToTarget();
				}else if (iRetVal == com.leopard.api.SerialPort1.SP1_FAILED) {
					serialhand.obtainMessage(MESSAGE_BOX,"Unsuccessful Operation").sendToTarget();
				}else if (iRetVal == com.leopard.api.SerialPort1.SP1_NO_DATA) {
					serialhand.obtainMessage(MESSAGE_BOX,"Data not available").sendToTarget();
				}else if (iRetVal == com.leopard.api.SerialPort1.SP1_TIME_OUT) {
					serialhand.obtainMessage(MESSAGE_BOX,"Time out").sendToTarget();
				}else if (iRetVal == com.leopard.api.SerialPort1.SP1_PARAM_ERROR) {
					serialhand.obtainMessage(MESSAGE_BOX,"Parameter error").sendToTarget();
				}else if (iRetVal == com.leopard.api.SerialPort1.SP1_ILLEGAL_LIBRARY) {
					serialhand.obtainMessage(MESSAGE_BOX,"Illegal library").sendToTarget();
				}else if (iRetVal == com.leopard.api.SerialPort1.SP1_DEMO_VERSION) {
					serialhand.obtainMessage(MESSAGE_BOX,"Serial port in Demo version").sendToTarget();
				}else if (iRetVal == com.leopard.api.SerialPort1.SP1_INACTIVE_PERIPHERAL) {
					serialhand.obtainMessage(MESSAGE_BOX,"Inactive peripheral").sendToTarget();
				}else if (iRetVal == com.leopard.api.SerialPort1.SP1_INVALID_DEVICE_ID) {
					serialhand.obtainMessage(MESSAGE_BOX,"Device is not license authenticated").sendToTarget();
				}
			super.onPostExecute(result);
		}
	}
	
	/*   This method shows the ScanBarcode data  AsynTask operation */
	public class ScanBarcodAsyc1 extends AsyncTask<Integer, Integer, Integer> {
		/* displays the progress dialog until background task is completed*/
		@Override
		protected void onPreExecute() {
			progressDialog(context, "Please Wait...");
			super.onPreExecute();
		}
		/* Task of ScanBarcode performing in the background*/
		@Override
		protected Integer doInBackground(Integer... params) {
			str1="";
			try{
				Log.d(TAG,"Inside doinbackground.....<<<<<<3>>>>"+scanebarcodedata);
				scanebarcodedata = new String("");	
				//scanebarcodedata	= SerialPort22.stScanBarCode(10000);
				bdata=SerialPort22.bReadBinaryData(10000, 50);
				iRetVal = SerialPort22.iGetReturnCode();
				 Log.d(TAG,"Inside doinbackground.....<<<<<<4>>>>"+scanebarcodedata);
			   }catch (Exception e) {
				iRetVal = DEVICENOTCONNECTED;
				return iRetVal;
			}
			return iRetVal;
		}
		/* This sends message to handler to display the status messages 
		 * of Diagnose in the dialog box */
		@SuppressWarnings("static-access")
		@Override
		protected void onPostExecute(Integer result) {
			dlgPg.dismiss();
			if(iRetVal == DEVICENOTCONNECTED){
				serialhand.obtainMessage(DEVICENOTCONNECTED, "Device not Connected").sendToTarget();	
				}
				else if(iRetVal == com.leopard.api.SerialPort2.SP2_SUCCESS || iRetVal==com.leopard.api.SerialPort2.SP2_PARTIAL_DATA_RECEIVED){	
					 Log.d(TAG,"Inside onPostExecute.....<<<<<<5>>>>"+scanebarcodedata);
					 //serialhand.obtainMessage(MESSAGE_BOX, "Data Received:"+"\n"+scanebarcodedata).sendToTarget();
					 serialhand.obtainMessage(1, "Data Received:\n"+HexString.bufferToHex(bdata)).sendToTarget();
					 Log.d(TAG,"Inside onPostExecute.....<<<<<<6>>>>"+scanebarcodedata);
				}else if (iRetVal == com.leopard.api.SerialPort2.SP2_FAILED) {
					serialhand.obtainMessage(MESSAGE_BOX,"Unsuccessful Operation").sendToTarget();
				}else if (iRetVal == com.leopard.api.SerialPort2.SP2_NO_DATA) {
					serialhand.obtainMessage(MESSAGE_BOX,"Data not available").sendToTarget();
				}else if (iRetVal == com.leopard.api.SerialPort2.SP2_TIME_OUT) {
					serialhand.obtainMessage(MESSAGE_BOX,"Time out").sendToTarget();
				}else if (iRetVal == com.leopard.api.SerialPort2.SP2_PARAM_ERROR) {
					serialhand.obtainMessage(MESSAGE_BOX,"Parameter error").sendToTarget();
				}else if (iRetVal == com.leopard.api.SerialPort2.SP2_ILLEGAL_LIBRARY) {
					serialhand.obtainMessage(MESSAGE_BOX,"Illegal library").sendToTarget();
				}else if (iRetVal == com.leopard.api.SerialPort2.SP2_DEMO_VERSION) {
					serialhand.obtainMessage(MESSAGE_BOX,"Serial port in Demo version").sendToTarget();
				}else if (iRetVal == com.leopard.api.SerialPort2.SP2_INACTIVE_PERIPHERAL) {
					serialhand.obtainMessage(MESSAGE_BOX,"Inactive peripheral").sendToTarget();
				}else if (iRetVal == com.leopard.api.SerialPort2.SP2_INVALID_DEVICE_ID) {
					serialhand.obtainMessage(MESSAGE_BOX,"Device is not license authenticated").sendToTarget();
				}
			super.onPostExecute(result);
		}
	}
	/* This performs Progress dialog box to show the progress of operation */
	public static void progressDialog(Context context, String msg) {
		dlgPg = new ProgressDialog(context);
		dlgPg.setMessage(msg);
		dlgPg.setIndeterminate(true);
		dlgPg.setCancelable(false);
		dlgPg.show();
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
		/* create alert dialog */
		AlertDialog alertDialog = alertDialogBuilder.create();
		/* show alert dialogbox */
		alertDialog.show();
	}
	
}
