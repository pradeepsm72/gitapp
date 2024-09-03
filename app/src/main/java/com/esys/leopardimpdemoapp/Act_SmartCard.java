package com.esys.leopardimpdemoapp;

import java.io.InputStream;
import java.io.OutputStream;

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
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.esys.leopardimpdemoapp.R;
import com.evolute.bluetooth.BluetoothComm;

import com.leopard.api.HexString;
import com.leopard.api.SmartCard;
public class Act_SmartCard extends Activity implements OnClickListener {
	private static final String TAG = "SmartCardAPI";
    private static final boolean D = true;//BluetoothConnect.D;
	OutputStream outputStream = null;
	InputStream inputStream = null;
	SmartCard smart;
	private Button btn_PRPowerup,btn_PRCardstatus,btn_PRPowerdown,btn_SECPowerup,btn_SECCardstatus,btn_SECPowerdown;
	private Button btn_PRIMARY,btn_SECONDARY;
	Context context = this;
	private static ProgressDialog dlgPg;
	private int iRetVal;
	private final static int MESSAGE_BOX = 1;
	/*   List of Return codes for the respective response */
	public static final int DEVICENOTCONNECTED = -100;
	
	LinearLayout linrlayot_primary,linrlayot_secondary;
	
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_smartcard);
		//Obtaining Input and Output Streams from Bluetooth Connection
		try {
			outputStream = BluetoothComm.mosOut;
			inputStream = BluetoothComm.misIn;
			smart = new SmartCard(Act_Main.setupInstance, outputStream, inputStream);
		} catch (Exception e) { }
		linrlayot_primary=(LinearLayout) findViewById(R.id.second01);
		linrlayot_secondary=(LinearLayout) findViewById(R.id.second02);
		
		btn_PRIMARY=(Button) findViewById(R.id.btn_primrysmrtcrd);
		btn_SECONDARY=(Button) findViewById(R.id.btn_secndrysmrtcard);

		btn_PRIMARY.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				linrlayot_primary.setVisibility(View.VISIBLE);
				linrlayot_secondary.setVisibility(View.GONE);
			}
		});
		btn_SECONDARY.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				linrlayot_primary.setVisibility(View.GONE);
				linrlayot_secondary.setVisibility(View.VISIBLE);
			}
		});
		
		btn_PRPowerup=(Button) findViewById(R.id.btn_PrimryPwrup);
		btn_PRPowerup.setOnClickListener(this);
		btn_PRCardstatus=(Button) findViewById(R.id.btn_Primrycardstatus);
		btn_PRCardstatus.setOnClickListener(this);
		btn_PRPowerdown=(Button) findViewById(R.id.btn_PrimrypPwrdwn);
		btn_PRPowerdown.setOnClickListener(this);
		btn_SECPowerup=(Button)findViewById(R.id.btn_SecondPwrUp);
		btn_SECPowerup.setOnClickListener(this);
		btn_SECCardstatus=(Button)findViewById(R.id.btn_SecndCardStatus);
		btn_SECCardstatus.setOnClickListener(this);
		btn_SECPowerdown=(Button)findViewById(R.id.btn_SecndPwrDwn);
		btn_SECPowerdown.setOnClickListener(this);
	}

	/* Handler to display UI response messages  */
	@SuppressLint("HandlerLeak")
	Handler SCard = new Handler() {
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
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_PrimryPwrup:
			/* SmartCardAsyc  undergoes AsynTask operation*/
			SmartCardAsyc smartcardasyc = new SmartCardAsyc();
			smartcardasyc.execute(0);
			break;
		case R.id.btn_Primrycardstatus:
			/* CardStatusAsync  undergoes AsynTask operation*/
			CardStatusAsync cardstatus = new CardStatusAsync();
			cardstatus.execute(0);
			break;
		case R.id.btn_PrimrypPwrdwn:
			PowerDownAsyc powerdown = new PowerDownAsyc();
			powerdown.execute(0);
			break;
		case R.id.btn_SecondPwrUp:
			SmartCardAsyc1 smartcardasyc1 = new SmartCardAsyc1();
			smartcardasyc1.execute(0);
			break;
		case R.id.btn_SecndCardStatus:
			CardStatusAsync1 cardstatus1 = new CardStatusAsync1();
			cardstatus1.execute(0);
			break;
		case R.id.btn_SecndPwrDwn:
			PowerDownAsyc1 powerdown1 = new PowerDownAsyc1();
			powerdown1.execute(0);
			break;
		default:
			break;
		}
	}

	/*   This method shows the CardStatusAsync AsynTask operation */
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
				iRetVal = smart.iSelectSCReader(SmartCard.SC_PrimarySCReader);
 				 Log.e(TAG,"LEOPARD Smart doInBackground Val"+iRetVal);
				if(iRetVal==SmartCard.SC_SUCCESS){
					iRetVal = smart.iSCGetCardStatus();
				}else {
					SCard.obtainMessage(1,"PrimarySCReader Unsuccessfull").sendToTarget();
				}
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
			dlgPg.dismiss();
			if (iRetVal == DEVICENOTCONNECTED) {
				SCard.obtainMessage(DEVICENOTCONNECTED,"Device not Connected").sendToTarget();
			} else if (iRetVal == SmartCard.SC_FAILURE) {
				SCard.obtainMessage(MESSAGE_BOX,"Unsuccessful operation").sendToTarget();
			} else if (iRetVal ==  SmartCard.NOT_IN_SMARTCARD_MODE) {
				SCard.obtainMessage(MESSAGE_BOX,"Smart card mode is not selected").sendToTarget();
			} else if (iRetVal == SmartCard.SC_INSERTED_BUT_NOT_POWERED) {
				SCard.obtainMessage(MESSAGE_BOX,"Smart Card present but not powered up").sendToTarget();
			} else if (iRetVal == SmartCard.SC_INSERTED_AND_POWERED) {
				SCard.obtainMessage(MESSAGE_BOX,"Smart Card is present and powered up").sendToTarget();
			} else if (iRetVal ==  SmartCard.READ_TIME_OUT) {
				SCard.obtainMessage(MESSAGE_BOX,"Upon time out for read expires").sendToTarget();
			} else if (iRetVal == SmartCard.PARAM_ERROR) {
				SCard.obtainMessage(MESSAGE_BOX,"Upon incorrect number of parameters has been sent").sendToTarget();
			}else if (iRetVal == SmartCard.UNKNOWN_DRIVER) {
				SCard.obtainMessage(MESSAGE_BOX,"Card returns unknown driver or command").sendToTarget();
			}else if (iRetVal == SmartCard.IMPOSSIBLE_OP_DRIVER) {
				SCard.obtainMessage(MESSAGE_BOX,"Card returns operation Impossible with this driver").sendToTarget();
			}else if (iRetVal == SmartCard.INCORRECT_ARGUMENTS) {
				SCard.obtainMessage(MESSAGE_BOX,"Card returns incorrect number of arguments").sendToTarget();
			}else if (iRetVal == SmartCard.UNKNOWN_READER_CMD) {
				SCard.obtainMessage(MESSAGE_BOX,"Card returns reader command unknown").sendToTarget();
			}else if (iRetVal == SmartCard.RESP_BUFFER_OVERFLOW) {
				SCard.obtainMessage(MESSAGE_BOX,"Card returns response exceeds buffer capacity").sendToTarget();
			}else if (iRetVal == SmartCard.WRONG_RES_UPON_RESET) {
				SCard.obtainMessage(MESSAGE_BOX,"Card returns wrong response upon card reset").sendToTarget();
			}else if (iRetVal == SmartCard.MSG_LEN_EXCEEDS) {
				SCard.obtainMessage(MESSAGE_BOX,"Card returns message is too long").sendToTarget();
			}else if (iRetVal == SmartCard.BYTE_READING_ERR) {
				SCard.obtainMessage(MESSAGE_BOX,"Byte reading error").sendToTarget();
			}else if (iRetVal == SmartCard.CARD_POWERED_DOWN) {
				SCard.obtainMessage(MESSAGE_BOX,"Card powered down").sendToTarget();
			}else if (iRetVal == SmartCard.CMD_INCORRECT_PARAM) {
				SCard.obtainMessage(MESSAGE_BOX,"Command with an incorrect parameters has been sent").sendToTarget();
			}else if (iRetVal == SmartCard.INCORRECT_TCK_BYTE) {
				SCard.obtainMessage(MESSAGE_BOX,"TCK check byte is incorrect in a microprocessor card ATR").sendToTarget();
			}else if (iRetVal == SmartCard.CARD_RESET_ERROR) {
				SCard.obtainMessage(MESSAGE_BOX,"Card returns error in the card reset response").sendToTarget();
			}else if (iRetVal == SmartCard.PROTOCOL_ERROR) {
				SCard.obtainMessage(MESSAGE_BOX,"Protocol error").sendToTarget();
			}else if (iRetVal == SmartCard.PARITY_ERROR) {
				SCard.obtainMessage(MESSAGE_BOX,"Parity error during a microprocessor exchange").sendToTarget();
			}else if (iRetVal == SmartCard.CARD_ABORTED) {
				SCard.obtainMessage(MESSAGE_BOX,"Card has aborted chaining").sendToTarget();
			}else if (iRetVal == SmartCard.READER_ABORTED) {
				SCard.obtainMessage(MESSAGE_BOX,"Reader has aborted chaining").sendToTarget();
			}else if (iRetVal == SmartCard.RESYNCH_SUCCESS) {
				SCard.obtainMessage(MESSAGE_BOX,"RESYNCH successfully performed").sendToTarget();
			}else if (iRetVal == SmartCard.PROTOCOL_PARAM_ERR) {
				SCard.obtainMessage(MESSAGE_BOX,"Protocol Parameter Selection Error").sendToTarget();
			}else if (iRetVal == SmartCard.ALREADY_CARD_POWERED_DOWN) {
				SCard.obtainMessage(MESSAGE_BOX,"Card already powered on").sendToTarget();
			}else if (iRetVal == SmartCard.PCLINK_CMD_NOT_SUPPORTED) {
				SCard.obtainMessage(MESSAGE_BOX,"PC-Link command not supported").sendToTarget();
			}else if (iRetVal == SmartCard.INVALID_PROCEDUREBYTE) {
				SCard.obtainMessage(MESSAGE_BOX,"Invalid 'Procedure byte").sendToTarget();
			}else if (iRetVal == SmartCard.SC_NOT_INSERTED) {
				SCard.obtainMessage(MESSAGE_BOX,"Please insert smart card").sendToTarget();
			}else if (iRetVal== SmartCard.SC_DEMO_VERSION) {
				SCard.obtainMessage(MESSAGE_BOX,"Library is in demo version").sendToTarget();
			}else if (iRetVal==SmartCard.SC_INVALID_DEVICE_ID) {
				SCard.obtainMessage(MESSAGE_BOX,"Connected  device is not license authenticated.").sendToTarget();
			}else if (iRetVal==SmartCard.SC_ILLEGAL_LIBRARY) {
				SCard.obtainMessage(MESSAGE_BOX,"Library not valid").sendToTarget();
			}else {
				if (iRetVal == SmartCard.SC_SUCCESS) {
					smartcardbox();
				}
			}
			super.onPostExecute(result);
		}
	}

	
	public class CardStatusAsync1 extends AsyncTask<Integer, Integer, Integer> {
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
				iRetVal = smart.iSelectSCReader(SmartCard.SC_SecondarySCReader);
 				 Log.e(TAG,"LEOPARD Smart doInBackground Val"+iRetVal);
				if(iRetVal==SmartCard.SC_SUCCESS){
					iRetVal = smart.iSCGetCardStatus();
				}else {
					SCard.obtainMessage(1,"PrimarySCReader Unsuccessfull").sendToTarget();
				}
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
			dlgPg.dismiss();
			if (iRetVal == DEVICENOTCONNECTED) {
				SCard.obtainMessage(DEVICENOTCONNECTED,"Device not Connected").sendToTarget();
			} else if (iRetVal == SmartCard.SC_FAILURE) {
				SCard.obtainMessage(MESSAGE_BOX,"Unsuccessful operation").sendToTarget();
			} else if (iRetVal ==  SmartCard.NOT_IN_SMARTCARD_MODE) {
				SCard.obtainMessage(MESSAGE_BOX,"Smart card mode is not selected").sendToTarget();
			} else if (iRetVal == SmartCard.SC_INSERTED_BUT_NOT_POWERED) {
				SCard.obtainMessage(MESSAGE_BOX,"Smart Card present but not powered up").sendToTarget();
			} else if (iRetVal == SmartCard.SC_INSERTED_AND_POWERED) {
				SCard.obtainMessage(MESSAGE_BOX,"Smart Card is present and powered up").sendToTarget();
			} else if (iRetVal ==  SmartCard.READ_TIME_OUT) {
				SCard.obtainMessage(MESSAGE_BOX,"Upon time out for read expires").sendToTarget();
			} else if (iRetVal == SmartCard.PARAM_ERROR) {
				SCard.obtainMessage(MESSAGE_BOX,"Upon incorrect number of parameters has been sent").sendToTarget();
			}else if (iRetVal == SmartCard.UNKNOWN_DRIVER) {
				SCard.obtainMessage(MESSAGE_BOX,"Card returns unknown driver or command").sendToTarget();
			}else if (iRetVal == SmartCard.IMPOSSIBLE_OP_DRIVER) {
				SCard.obtainMessage(MESSAGE_BOX,"Card returns operation Impossible with this driver").sendToTarget();
			}else if (iRetVal == SmartCard.INCORRECT_ARGUMENTS) {
				SCard.obtainMessage(MESSAGE_BOX,"Card returns incorrect number of arguments").sendToTarget();
			}else if (iRetVal == SmartCard.UNKNOWN_READER_CMD) {
				SCard.obtainMessage(MESSAGE_BOX,"Card returns reader command unknown").sendToTarget();
			}else if (iRetVal == SmartCard.RESP_BUFFER_OVERFLOW) {
				SCard.obtainMessage(MESSAGE_BOX,"Card returns response exceeds buffer capacity").sendToTarget();
			}else if (iRetVal == SmartCard.WRONG_RES_UPON_RESET) {
				SCard.obtainMessage(MESSAGE_BOX,"Card returns wrong response upon card reset").sendToTarget();
			}else if (iRetVal == SmartCard.MSG_LEN_EXCEEDS) {
				SCard.obtainMessage(MESSAGE_BOX,"Card returns message is too long").sendToTarget();
			}else if (iRetVal == SmartCard.BYTE_READING_ERR) {
				SCard.obtainMessage(MESSAGE_BOX,"Byte reading error").sendToTarget();
			}else if (iRetVal == SmartCard.CARD_POWERED_DOWN) {
				SCard.obtainMessage(MESSAGE_BOX,"Card powered down").sendToTarget();
			}else if (iRetVal == SmartCard.CMD_INCORRECT_PARAM) {
				SCard.obtainMessage(MESSAGE_BOX,"Command with an incorrect parameters has been sent").sendToTarget();
			}else if (iRetVal == SmartCard.INCORRECT_TCK_BYTE) {
				SCard.obtainMessage(MESSAGE_BOX,"TCK check byte is incorrect in a microprocessor card ATR").sendToTarget();
			}else if (iRetVal == SmartCard.CARD_RESET_ERROR) {
				SCard.obtainMessage(MESSAGE_BOX,"Card returns error in the card reset response").sendToTarget();
			}else if (iRetVal == SmartCard.PROTOCOL_ERROR) {
				SCard.obtainMessage(MESSAGE_BOX,"Protocol error").sendToTarget();
			}else if (iRetVal == SmartCard.PARITY_ERROR) {
				SCard.obtainMessage(MESSAGE_BOX,"Parity error during a microprocessor exchange").sendToTarget();
			}else if (iRetVal == SmartCard.CARD_ABORTED) {
				SCard.obtainMessage(MESSAGE_BOX,"Card has aborted chaining").sendToTarget();
			}else if (iRetVal == SmartCard.READER_ABORTED) {
				SCard.obtainMessage(MESSAGE_BOX,"Reader has aborted chaining").sendToTarget();
			}else if (iRetVal == SmartCard.RESYNCH_SUCCESS) {
				SCard.obtainMessage(MESSAGE_BOX,"RESYNCH successfully performed").sendToTarget();
			}else if (iRetVal == SmartCard.PROTOCOL_PARAM_ERR) {
				SCard.obtainMessage(MESSAGE_BOX,"Protocol Parameter Selection Error").sendToTarget();
			}else if (iRetVal == SmartCard.ALREADY_CARD_POWERED_DOWN) {
				SCard.obtainMessage(MESSAGE_BOX,"Card already powered on").sendToTarget();
			}else if (iRetVal == SmartCard.PCLINK_CMD_NOT_SUPPORTED) {
				SCard.obtainMessage(MESSAGE_BOX,"PC-Link command not supported").sendToTarget();
			}else if (iRetVal == SmartCard.INVALID_PROCEDUREBYTE) {
				SCard.obtainMessage(MESSAGE_BOX,"Invalid 'Procedure byte").sendToTarget();
			}else if (iRetVal == SmartCard.SC_NOT_INSERTED) {
				SCard.obtainMessage(MESSAGE_BOX,"Please insert smart card").sendToTarget();
			}else if (iRetVal== SmartCard.SC_DEMO_VERSION) {
				SCard.obtainMessage(MESSAGE_BOX,"Library is in demo version").sendToTarget();
			}else if (iRetVal==SmartCard.SC_INVALID_DEVICE_ID) {
				SCard.obtainMessage(MESSAGE_BOX,"Connected  device is not license authenticated.").sendToTarget();
			}else if (iRetVal==SmartCard.SC_ILLEGAL_LIBRARY) {
				SCard.obtainMessage(MESSAGE_BOX,"Library not valid").sendToTarget();
			}else {
				if (iRetVal == SmartCard.SC_SUCCESS) {
					smartcardbox();
				}
			}
			super.onPostExecute(result);
		}
	}
	/*   This method shows the PowerDown AsynTask operation */
	public class PowerDownAsyc extends AsyncTask<Integer, Integer, Integer> {
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
				iRetVal = smart.iSelectSCReader(SmartCard.SC_PrimarySCReader);
 				 Log.e(TAG,"LEOPARD Smart PowerDown InBackground Val"+iRetVal);
				if(iRetVal==SmartCard.SC_SUCCESS){
					iRetVal = smart.iSCPowerDown();
				}else {
					SCard.obtainMessage(1,"PrimarySCReader Unsuccessfull").sendToTarget();
				}
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
			dlgPg.dismiss();
			if (iRetVal == DEVICENOTCONNECTED) {
				SCard.obtainMessage(DEVICENOTCONNECTED,"Device not Connected").sendToTarget();
			} else if (iRetVal == SmartCard.SC_NOT_INSERTED) {
				SCard.obtainMessage(MESSAGE_BOX,"Card Not Inserted").sendToTarget();
			} else if (iRetVal == SmartCard.SC_INSERTED_BUT_NOT_POWERED) {
				SCard.obtainMessage(MESSAGE_BOX,"Card Inserted but not Powered").sendToTarget();
			} else if (iRetVal == SmartCard.SC_INSERTED_AND_POWERED) {
				SCard.obtainMessage(MESSAGE_BOX,"Card Inserted and Powered").sendToTarget();
			} else if (iRetVal == SmartCard.SC_FAILURE) {
				SCard.obtainMessage(MESSAGE_BOX,"Unsuccessful operation").sendToTarget();
			} else if (iRetVal ==  SmartCard.NOT_IN_SMARTCARD_MODE) {
				SCard.obtainMessage(MESSAGE_BOX,"Smart card mode is not selected").sendToTarget();
			} else if (iRetVal ==  SmartCard.READ_TIME_OUT) {
				SCard.obtainMessage(MESSAGE_BOX,"Upon time out for read expires").sendToTarget();
			} else if (iRetVal == SmartCard.PARAM_ERROR) {
				SCard.obtainMessage(MESSAGE_BOX,"Upon incorrect number of parameters has been sent").sendToTarget();
			}else if (iRetVal == SmartCard.UNKNOWN_DRIVER) {
				SCard.obtainMessage(MESSAGE_BOX,"Card returns unknown driver or command").sendToTarget();
			}else if (iRetVal == SmartCard.IMPOSSIBLE_OP_DRIVER) {
				SCard.obtainMessage(MESSAGE_BOX,"Card returns operation Impossible with this driver").sendToTarget();
			}else if (iRetVal == SmartCard.INCORRECT_ARGUMENTS) {
				SCard.obtainMessage(MESSAGE_BOX,"Card returns incorrect number of arguments").sendToTarget();
			}else if (iRetVal == SmartCard.UNKNOWN_READER_CMD) {
				SCard.obtainMessage(MESSAGE_BOX,"Card returns reader command unknown").sendToTarget();
			}else if (iRetVal == SmartCard.RESP_BUFFER_OVERFLOW) {
				SCard.obtainMessage(MESSAGE_BOX,"Card returns response exceeds buffer capacity").sendToTarget();
			}else if (iRetVal == SmartCard.WRONG_RES_UPON_RESET) {
				SCard.obtainMessage(MESSAGE_BOX,"Card returns wrong response upon card reset").sendToTarget();
			}else if (iRetVal == SmartCard.MSG_LEN_EXCEEDS) {
				SCard.obtainMessage(MESSAGE_BOX,"Card returns message is too long").sendToTarget();
			}else if (iRetVal == SmartCard.BYTE_READING_ERR) {
				SCard.obtainMessage(MESSAGE_BOX,"Byte reading error").sendToTarget();
			}else if (iRetVal == SmartCard.CARD_POWERED_DOWN) {
				SCard.obtainMessage(MESSAGE_BOX,"Card powered down").sendToTarget();
			}else if (iRetVal == SmartCard.CMD_INCORRECT_PARAM) {
				SCard.obtainMessage(MESSAGE_BOX,"Command with an incorrect parameters has been sent").sendToTarget();
			}else if (iRetVal == SmartCard.INCORRECT_TCK_BYTE) {
				SCard.obtainMessage(MESSAGE_BOX,"TCK check byte is incorrect in a microprocessor card ATR").sendToTarget();
			}else if (iRetVal == SmartCard.CARD_RESET_ERROR) {
				SCard.obtainMessage(MESSAGE_BOX,"Card returns error in the card reset response").sendToTarget();
			}else if (iRetVal == SmartCard.PROTOCOL_ERROR) {
				SCard.obtainMessage(MESSAGE_BOX,"Protocol error").sendToTarget();
			}else if (iRetVal == SmartCard.PARITY_ERROR) {
				SCard.obtainMessage(MESSAGE_BOX,"Parity error during a microprocessor exchange").sendToTarget();
			}else if (iRetVal == SmartCard.CARD_ABORTED) {
				SCard.obtainMessage(MESSAGE_BOX,"Card has aborted chaining").sendToTarget();
			}else if (iRetVal == SmartCard.READER_ABORTED) {
				SCard.obtainMessage(MESSAGE_BOX,"Reader has aborted chaining").sendToTarget();
			}else if (iRetVal == SmartCard.RESYNCH_SUCCESS) {
				SCard.obtainMessage(MESSAGE_BOX,"RESYNCH successfully performed").sendToTarget();
			}else if (iRetVal == SmartCard.PROTOCOL_PARAM_ERR) {
				SCard.obtainMessage(MESSAGE_BOX,"Protocol Parameter Selection Error").sendToTarget();
			}else if (iRetVal == SmartCard.ALREADY_CARD_POWERED_DOWN) {
				SCard.obtainMessage(MESSAGE_BOX,"Card already powered on").sendToTarget();
			}else if (iRetVal == SmartCard.PCLINK_CMD_NOT_SUPPORTED) {
				SCard.obtainMessage(MESSAGE_BOX,"PC-Link command not supported").sendToTarget();
			}else if (iRetVal == SmartCard.INVALID_PROCEDUREBYTE) {
				SCard.obtainMessage(MESSAGE_BOX,"Invalid 'Procedure byte").sendToTarget();
			}else if (iRetVal == SmartCard.SC_NOT_INSERTED) {
				SCard.obtainMessage(MESSAGE_BOX,"Please insert smart card").sendToTarget();
			}else if (iRetVal== SmartCard.SC_DEMO_VERSION) {
				SCard.obtainMessage(MESSAGE_BOX,"Library is in demo version").sendToTarget();
			}else if (iRetVal==SmartCard.SC_INVALID_DEVICE_ID) {
				SCard.obtainMessage(MESSAGE_BOX,"Connected  device is not license authenticated.").sendToTarget();
			}else if (iRetVal==SmartCard.SC_ILLEGAL_LIBRARY) {
				SCard.obtainMessage(MESSAGE_BOX,"Library not valid").sendToTarget();
			}else {
				if (iRetVal == SmartCard.SC_SUCCESS) {
					SCard.obtainMessage(MESSAGE_BOX,"Power down success").sendToTarget();
				}
			}
			super.onPostExecute(result);
		}
	}
	
	public class PowerDownAsyc1 extends AsyncTask<Integer, Integer, Integer> {
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
				iRetVal = smart.iSelectSCReader(SmartCard.SC_SecondarySCReader);
 				 Log.e(TAG,"LEOPARD Smart PowerDown InBackground Val"+iRetVal);
				if(iRetVal==SmartCard.SC_SUCCESS){
					iRetVal = smart.iSCPowerDown();
				}else {
					SCard.obtainMessage(1,"PrimarySCReader Unsuccessfull").sendToTarget();
				}
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
			dlgPg.dismiss();
			if (iRetVal == DEVICENOTCONNECTED) {
				SCard.obtainMessage(DEVICENOTCONNECTED,"Device not Connected").sendToTarget();
			} else if (iRetVal == SmartCard.SC_NOT_INSERTED) {
				SCard.obtainMessage(MESSAGE_BOX,"Card Not Inserted").sendToTarget();
			} else if (iRetVal == SmartCard.SC_INSERTED_BUT_NOT_POWERED) {
				SCard.obtainMessage(MESSAGE_BOX,"Card Inserted but not Powered").sendToTarget();
			} else if (iRetVal == SmartCard.SC_INSERTED_AND_POWERED) {
				SCard.obtainMessage(MESSAGE_BOX,"Card Inserted and Powered").sendToTarget();
			} else if (iRetVal == SmartCard.SC_FAILURE) {
				SCard.obtainMessage(MESSAGE_BOX,"Unsuccessful operation").sendToTarget();
			} else if (iRetVal ==  SmartCard.NOT_IN_SMARTCARD_MODE) {
				SCard.obtainMessage(MESSAGE_BOX,"Smart card mode is not selected").sendToTarget();
			} else if (iRetVal ==  SmartCard.READ_TIME_OUT) {
				SCard.obtainMessage(MESSAGE_BOX,"Upon time out for read expires").sendToTarget();
			} else if (iRetVal == SmartCard.PARAM_ERROR) {
				SCard.obtainMessage(MESSAGE_BOX,"Upon incorrect number of parameters has been sent").sendToTarget();
			}else if (iRetVal == SmartCard.UNKNOWN_DRIVER) {
				SCard.obtainMessage(MESSAGE_BOX,"Card returns unknown driver or command").sendToTarget();
			}else if (iRetVal == SmartCard.IMPOSSIBLE_OP_DRIVER) {
				SCard.obtainMessage(MESSAGE_BOX,"Card returns operation Impossible with this driver").sendToTarget();
			}else if (iRetVal == SmartCard.INCORRECT_ARGUMENTS) {
				SCard.obtainMessage(MESSAGE_BOX,"Card returns incorrect number of arguments").sendToTarget();
			}else if (iRetVal == SmartCard.UNKNOWN_READER_CMD) {
				SCard.obtainMessage(MESSAGE_BOX,"Card returns reader command unknown").sendToTarget();
			}else if (iRetVal == SmartCard.RESP_BUFFER_OVERFLOW) {
				SCard.obtainMessage(MESSAGE_BOX,"Card returns response exceeds buffer capacity").sendToTarget();
			}else if (iRetVal == SmartCard.WRONG_RES_UPON_RESET) {
				SCard.obtainMessage(MESSAGE_BOX,"Card returns wrong response upon card reset").sendToTarget();
			}else if (iRetVal == SmartCard.MSG_LEN_EXCEEDS) {
				SCard.obtainMessage(MESSAGE_BOX,"Card returns message is too long").sendToTarget();
			}else if (iRetVal == SmartCard.BYTE_READING_ERR) {
				SCard.obtainMessage(MESSAGE_BOX,"Byte reading error").sendToTarget();
			}else if (iRetVal == SmartCard.CARD_POWERED_DOWN) {
				SCard.obtainMessage(MESSAGE_BOX,"Card powered down").sendToTarget();
			}else if (iRetVal == SmartCard.CMD_INCORRECT_PARAM) {
				SCard.obtainMessage(MESSAGE_BOX,"Command with an incorrect parameters has been sent").sendToTarget();
			}else if (iRetVal == SmartCard.INCORRECT_TCK_BYTE) {
				SCard.obtainMessage(MESSAGE_BOX,"TCK check byte is incorrect in a microprocessor card ATR").sendToTarget();
			}else if (iRetVal == SmartCard.CARD_RESET_ERROR) {
				SCard.obtainMessage(MESSAGE_BOX,"Card returns error in the card reset response").sendToTarget();
			}else if (iRetVal == SmartCard.PROTOCOL_ERROR) {
				SCard.obtainMessage(MESSAGE_BOX,"Protocol error").sendToTarget();
			}else if (iRetVal == SmartCard.PARITY_ERROR) {
				SCard.obtainMessage(MESSAGE_BOX,"Parity error during a microprocessor exchange").sendToTarget();
			}else if (iRetVal == SmartCard.CARD_ABORTED) {
				SCard.obtainMessage(MESSAGE_BOX,"Card has aborted chaining").sendToTarget();
			}else if (iRetVal == SmartCard.READER_ABORTED) {
				SCard.obtainMessage(MESSAGE_BOX,"Reader has aborted chaining").sendToTarget();
			}else if (iRetVal == SmartCard.RESYNCH_SUCCESS) {
				SCard.obtainMessage(MESSAGE_BOX,"RESYNCH successfully performed").sendToTarget();
			}else if (iRetVal == SmartCard.PROTOCOL_PARAM_ERR) {
				SCard.obtainMessage(MESSAGE_BOX,"Protocol Parameter Selection Error").sendToTarget();
			}else if (iRetVal == SmartCard.ALREADY_CARD_POWERED_DOWN) {
				SCard.obtainMessage(MESSAGE_BOX,"Card already powered on").sendToTarget();
			}else if (iRetVal == SmartCard.PCLINK_CMD_NOT_SUPPORTED) {
				SCard.obtainMessage(MESSAGE_BOX,"PC-Link command not supported").sendToTarget();
			}else if (iRetVal == SmartCard.INVALID_PROCEDUREBYTE) {
				SCard.obtainMessage(MESSAGE_BOX,"Invalid 'Procedure byte").sendToTarget();
			}else if (iRetVal == SmartCard.SC_NOT_INSERTED) {
				SCard.obtainMessage(MESSAGE_BOX,"Please insert smart card").sendToTarget();
			}else if (iRetVal== SmartCard.SC_DEMO_VERSION) {
				SCard.obtainMessage(MESSAGE_BOX,"Library is in demo version").sendToTarget();
			}else if (iRetVal==SmartCard.SC_INVALID_DEVICE_ID) {
				SCard.obtainMessage(MESSAGE_BOX,"Connected  device is not license authenticated.").sendToTarget();
			}else if (iRetVal==SmartCard.SC_ILLEGAL_LIBRARY) {
				SCard.obtainMessage(MESSAGE_BOX,"Library not valid").sendToTarget();
			}else {
				if (iRetVal == SmartCard.SC_SUCCESS) {
					SCard.obtainMessage(MESSAGE_BOX,"Power down success").sendToTarget();
				}
			}
			super.onPostExecute(result);
		}
	}
	byte[] bATRResp = new byte[300];
	/*   This method shows the SmartCardAsyc  AsynTask operation */
	public class SmartCardAsyc extends AsyncTask<Integer, Integer, Integer> {
		/* displays the progress dialog until background task is completed*/
		@Override
		protected void onPreExecute() {
			progressDialog(context, "Please Wait ...");
			super.onPreExecute();
		}
		/* Task of SmartCardAsyc performing in the background*/
		@Override
		protected Integer doInBackground(Integer... params) {
			try {
				iRetVal = smart.iSelectSCReader(SmartCard.SC_PrimarySCReader);
				 Log.e(TAG,"LEOPARD FPS Smart doInBackground Val"+iRetVal);
				if(iRetVal==SmartCard.SC_SUCCESS){
					iRetVal = smart.iSCPowerUpCommand((byte) 0x27, bATRResp);
				}else {
					SCard.obtainMessage(1,"PrimarySCReader Unsuccessfull").sendToTarget();
				}
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
			System.out.println("Power up"+iRetVal);
			Log.e("Power value",">>>>>>>"+iRetVal);
				dlgPg.dismiss();
				if (iRetVal > 0) { // Receiverd ATR Response
					Log.d(TAG,"Power UP ATR Response : "+HexString.bufferToHex(bATRResp,0,iRetVal));
					smartcardbox();
				} else if (iRetVal == SmartCard.SC_NOT_INSERTED) {
					SCard.obtainMessage(MESSAGE_BOX,"Card Not Inserted").sendToTarget();
				} else if (iRetVal == SmartCard.SC_INSERTED_BUT_NOT_POWERED) {
					SCard.obtainMessage(MESSAGE_BOX,"Card Inserted but not Powered").sendToTarget();
				} else if (iRetVal == SmartCard.SC_INSERTED_AND_POWERED) {
					SCard.obtainMessage(MESSAGE_BOX,"Card Inserted and Powered").sendToTarget();
				} else if (iRetVal == SmartCard.SC_FAILURE) {
					SCard.obtainMessage(MESSAGE_BOX,"Unsuccessful operation").sendToTarget();
				} else if (iRetVal ==  SmartCard.NOT_IN_SMARTCARD_MODE) {
					SCard.obtainMessage(MESSAGE_BOX,"Smart card mode is not selected").sendToTarget();
				} else if (iRetVal ==  SmartCard.READ_TIME_OUT) {
					SCard.obtainMessage(MESSAGE_BOX,"Upon time out for read expires").sendToTarget();
				} else if (iRetVal == SmartCard.PARAM_ERROR) {
					SCard.obtainMessage(MESSAGE_BOX,"Upon incorrect number of parameters has been sent").sendToTarget();
				}else if (iRetVal == SmartCard.UNKNOWN_DRIVER) {
					SCard.obtainMessage(MESSAGE_BOX,"Card returns unknown driver or command").sendToTarget();
				}else if (iRetVal == SmartCard.IMPOSSIBLE_OP_DRIVER) {
					SCard.obtainMessage(MESSAGE_BOX,"Card returns operation Impossible with this driver").sendToTarget();
				}else if (iRetVal == SmartCard.INCORRECT_ARGUMENTS) {
					SCard.obtainMessage(MESSAGE_BOX,"Card returns incorrect number of arguments").sendToTarget();
				}else if (iRetVal == SmartCard.UNKNOWN_READER_CMD) {
					SCard.obtainMessage(MESSAGE_BOX,"Card returns reader command unknown").sendToTarget();
				}else if (iRetVal == SmartCard.RESP_BUFFER_OVERFLOW) {
					SCard.obtainMessage(MESSAGE_BOX,"Card returns response exceeds buffer capacity").sendToTarget();
				}else if (iRetVal == SmartCard.WRONG_RES_UPON_RESET) {
					SCard.obtainMessage(MESSAGE_BOX,"Card returns wrong response upon card reset").sendToTarget();
				}else if (iRetVal == SmartCard.MSG_LEN_EXCEEDS) {
					SCard.obtainMessage(MESSAGE_BOX,"Card returns message is too long").sendToTarget();
				}else if (iRetVal == SmartCard.BYTE_READING_ERR) {
					SCard.obtainMessage(MESSAGE_BOX,"Byte reading error").sendToTarget();
				}else if (iRetVal == SmartCard.CARD_POWERED_DOWN) {
					SCard.obtainMessage(MESSAGE_BOX,"Card powered down").sendToTarget();
				}else if (iRetVal == SmartCard.CMD_INCORRECT_PARAM) {
					SCard.obtainMessage(MESSAGE_BOX,"Command with an incorrect parameters has been sent").sendToTarget();
				}else if (iRetVal == SmartCard.INCORRECT_TCK_BYTE) {
					SCard.obtainMessage(MESSAGE_BOX,"TCK check byte is incorrect in a microprocessor card ATR").sendToTarget();
				}else if (iRetVal == SmartCard.CARD_RESET_ERROR) {
					SCard.obtainMessage(MESSAGE_BOX,"Card returns error in the card reset response").sendToTarget();
				}else if (iRetVal == SmartCard.PROTOCOL_ERROR) {
					SCard.obtainMessage(MESSAGE_BOX,"Protocol error").sendToTarget();
				}else if (iRetVal == SmartCard.PARITY_ERROR) {
					SCard.obtainMessage(MESSAGE_BOX,"Parity error during a microprocessor exchange").sendToTarget();
				}else if (iRetVal == SmartCard.CARD_ABORTED) {
					SCard.obtainMessage(MESSAGE_BOX,"Card has aborted chaining").sendToTarget();
				}else if (iRetVal == SmartCard.READER_ABORTED) {
					SCard.obtainMessage(MESSAGE_BOX,"Reader has aborted chaining").sendToTarget();
				}else if (iRetVal == SmartCard.RESYNCH_SUCCESS) {
					SCard.obtainMessage(MESSAGE_BOX,"RESYNCH successfully performed").sendToTarget();
				}else if (iRetVal == SmartCard.PROTOCOL_PARAM_ERR) {
					SCard.obtainMessage(MESSAGE_BOX,"Protocol Parameter Selection Error").sendToTarget();
				}else if (iRetVal == SmartCard.ALREADY_CARD_POWERED_DOWN) {
					SCard.obtainMessage(MESSAGE_BOX,"Card already powered on").sendToTarget();
				}else if (iRetVal == SmartCard.PCLINK_CMD_NOT_SUPPORTED) {
					SCard.obtainMessage(MESSAGE_BOX,"PC-Link command not supported").sendToTarget();
				}else if (iRetVal == SmartCard.INVALID_PROCEDUREBYTE) {
					SCard.obtainMessage(MESSAGE_BOX,"Invalid 'Procedure byte").sendToTarget();
				}else if (iRetVal == SmartCard.SC_NOT_INSERTED) {
					SCard.obtainMessage(MESSAGE_BOX,"Please insert smart card").sendToTarget();
				}else if (iRetVal == SmartCard.SC_NOT_INSERTED) {
					SCard.obtainMessage(MESSAGE_BOX,"Please insert smart card").sendToTarget();
				}else if (iRetVal== SmartCard.SC_DEMO_VERSION) {
				    SCard.obtainMessage(MESSAGE_BOX,"Library is in demo version").sendToTarget();
				}else if (iRetVal==SmartCard.SC_INVALID_DEVICE_ID) {
					SCard.obtainMessage(MESSAGE_BOX,"Connected  device is not license authenticated.").sendToTarget();
			    }else if (iRetVal==SmartCard.SC_ILLEGAL_LIBRARY) {
					SCard.obtainMessage(MESSAGE_BOX,"Library not valid").sendToTarget();
				}else if (iRetVal == DEVICENOTCONNECTED) {
					SCard.obtainMessage(DEVICENOTCONNECTED,"Device not Connected").sendToTarget();
				}else if (iRetVal == SmartCard.SC_INACTIVE_PERIPHERAL) {
					SCard.obtainMessage(MESSAGE_BOX,"Inacive Peripheral").sendToTarget();
				}
			super.onPostExecute(result);
		}
	}

	byte[] bAtrResp2 = new byte[300];
	public class SmartCardAsyc1 extends AsyncTask<Integer, Integer, Integer> {
		/* displays the progress dialog until background task is completed*/
		@Override
		protected void onPreExecute() {
			progressDialog(context, "Please Wait ...");
			super.onPreExecute();
		}
		/* Task of SmartCardAsyc performing in the background*/
		@Override
		protected Integer doInBackground(Integer... params) {
			try {
				iRetVal = smart.iSelectSCReader(SmartCard.SC_SecondarySCReader);
				 Log.e(TAG,"LEOPARD FPS Smart doInBackground Val"+iRetVal);
				if(iRetVal==SmartCard.SC_SUCCESS){
					iRetVal = smart.iSCPowerUpCommand((byte) 0x27, bAtrResp2);
				}else {
					SCard.obtainMessage(1,"PrimarySCReader Unsuccessfull").sendToTarget();
				}
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
			System.out.println("Power up"+iRetVal);
			Log.e("Power value",">>>>>>>"+iRetVal);
				dlgPg.dismiss();
				if (iRetVal > 0) { // Receiverd ATR Response
					Log.d(TAG,"Power UP ATR Response : "+HexString.bufferToHex(bATRResp,0,iRetVal));
					smartcardbox();
				} else if (iRetVal == SmartCard.SC_NOT_INSERTED) {
					SCard.obtainMessage(MESSAGE_BOX,"Card Not Inserted").sendToTarget();
				} else if (iRetVal == SmartCard.SC_INSERTED_BUT_NOT_POWERED) {
					SCard.obtainMessage(MESSAGE_BOX,"Card Inserted but not Powered").sendToTarget();
				} else if (iRetVal == SmartCard.SC_INSERTED_AND_POWERED) {
					SCard.obtainMessage(MESSAGE_BOX,"Card Inserted and Powered").sendToTarget();
				} else if (iRetVal == SmartCard.SC_FAILURE) {
					SCard.obtainMessage(MESSAGE_BOX,"Unsuccessful operation").sendToTarget();
				} else if (iRetVal ==  SmartCard.NOT_IN_SMARTCARD_MODE) {
					SCard.obtainMessage(MESSAGE_BOX,"Smart card mode is not selected").sendToTarget();
				} else if (iRetVal ==  SmartCard.READ_TIME_OUT) {
					SCard.obtainMessage(MESSAGE_BOX,"Upon time out for read expires").sendToTarget();
				} else if (iRetVal == SmartCard.PARAM_ERROR) {
					SCard.obtainMessage(MESSAGE_BOX,"Upon incorrect number of parameters has been sent").sendToTarget();
				}else if (iRetVal == SmartCard.UNKNOWN_DRIVER) {
					SCard.obtainMessage(MESSAGE_BOX,"Card returns unknown driver or command").sendToTarget();
				}else if (iRetVal == SmartCard.IMPOSSIBLE_OP_DRIVER) {
					SCard.obtainMessage(MESSAGE_BOX,"Card returns operation Impossible with this driver").sendToTarget();
				}else if (iRetVal == SmartCard.INCORRECT_ARGUMENTS) {
					SCard.obtainMessage(MESSAGE_BOX,"Card returns incorrect number of arguments").sendToTarget();
				}else if (iRetVal == SmartCard.UNKNOWN_READER_CMD) {
					SCard.obtainMessage(MESSAGE_BOX,"Card returns reader command unknown").sendToTarget();
				}else if (iRetVal == SmartCard.RESP_BUFFER_OVERFLOW) {
					SCard.obtainMessage(MESSAGE_BOX,"Card returns response exceeds buffer capacity").sendToTarget();
				}else if (iRetVal == SmartCard.WRONG_RES_UPON_RESET) {
					SCard.obtainMessage(MESSAGE_BOX,"Card returns wrong response upon card reset").sendToTarget();
				}else if (iRetVal == SmartCard.MSG_LEN_EXCEEDS) {
					SCard.obtainMessage(MESSAGE_BOX,"Card returns message is too long").sendToTarget();
				}else if (iRetVal == SmartCard.BYTE_READING_ERR) {
					SCard.obtainMessage(MESSAGE_BOX,"Byte reading error").sendToTarget();
				}else if (iRetVal == SmartCard.CARD_POWERED_DOWN) {
					SCard.obtainMessage(MESSAGE_BOX,"Card powered down").sendToTarget();
				}else if (iRetVal == SmartCard.CMD_INCORRECT_PARAM) {
					SCard.obtainMessage(MESSAGE_BOX,"Command with an incorrect parameters has been sent").sendToTarget();
				}else if (iRetVal == SmartCard.INCORRECT_TCK_BYTE) {
					SCard.obtainMessage(MESSAGE_BOX,"TCK check byte is incorrect in a microprocessor card ATR").sendToTarget();
				}else if (iRetVal == SmartCard.CARD_RESET_ERROR) {
					SCard.obtainMessage(MESSAGE_BOX,"Card returns error in the card reset response").sendToTarget();
				}else if (iRetVal == SmartCard.PROTOCOL_ERROR) {
					SCard.obtainMessage(MESSAGE_BOX,"Protocol error").sendToTarget();
				}else if (iRetVal == SmartCard.PARITY_ERROR) {
					SCard.obtainMessage(MESSAGE_BOX,"Parity error during a microprocessor exchange").sendToTarget();
				}else if (iRetVal == SmartCard.CARD_ABORTED) {
					SCard.obtainMessage(MESSAGE_BOX,"Card has aborted chaining").sendToTarget();
				}else if (iRetVal == SmartCard.READER_ABORTED) {
					SCard.obtainMessage(MESSAGE_BOX,"Reader has aborted chaining").sendToTarget();
				}else if (iRetVal == SmartCard.RESYNCH_SUCCESS) {
					SCard.obtainMessage(MESSAGE_BOX,"RESYNCH successfully performed").sendToTarget();
				}else if (iRetVal == SmartCard.PROTOCOL_PARAM_ERR) {
					SCard.obtainMessage(MESSAGE_BOX,"Protocol Parameter Selection Error").sendToTarget();
				}else if (iRetVal == SmartCard.ALREADY_CARD_POWERED_DOWN) {
					SCard.obtainMessage(MESSAGE_BOX,"Card already powered on").sendToTarget();
				}else if (iRetVal == SmartCard.PCLINK_CMD_NOT_SUPPORTED) {
					SCard.obtainMessage(MESSAGE_BOX,"PC-Link command not supported").sendToTarget();
				}else if (iRetVal == SmartCard.INVALID_PROCEDUREBYTE) {
					SCard.obtainMessage(MESSAGE_BOX,"Invalid 'Procedure byte").sendToTarget();
				}else if (iRetVal == SmartCard.SC_NOT_INSERTED) {
					SCard.obtainMessage(MESSAGE_BOX,"Please insert smart card").sendToTarget();
				}else if (iRetVal == SmartCard.SC_NOT_INSERTED) {
					SCard.obtainMessage(MESSAGE_BOX,"Please insert smart card").sendToTarget();
				}else if (iRetVal== SmartCard.SC_DEMO_VERSION) {
				    SCard.obtainMessage(MESSAGE_BOX,"Library is in demo version").sendToTarget();
				}else if (iRetVal==SmartCard.SC_INVALID_DEVICE_ID) {
					SCard.obtainMessage(MESSAGE_BOX,"Connected  device is not license authenticated.").sendToTarget();
			    }else if (iRetVal==SmartCard.SC_ILLEGAL_LIBRARY) {
					SCard.obtainMessage(MESSAGE_BOX,"Library not valid").sendToTarget();
				}else if (iRetVal == DEVICENOTCONNECTED) {
					SCard.obtainMessage(DEVICENOTCONNECTED,"Device not Connected").sendToTarget();
				}else if (iRetVal == SmartCard.SC_INACTIVE_PERIPHERAL) {
					SCard.obtainMessage(MESSAGE_BOX,"Inacive Peripheral").sendToTarget();
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
		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();
		// show it
		alertDialog.show();
	}

	public void smartcardbox() {
		Display display = getWindowManager().getDefaultDisplay(); 
		@SuppressWarnings("deprecation")
		int width = display.getWidth();  // deprecated
		final TextView sendapdu_tv;
		final Button sendapdu_but;
		final Dialog smartdialog = new Dialog(context);
		smartdialog.setContentView(R.layout.dlg_customsmart);
		smartdialog.setTitle("Smart Card");
		smartdialog.setCancelable(true);
		sendapdu_tv = (TextView) smartdialog.findViewById(R.id.sendapdu_tv);
		TextView textView1 = (TextView) smartdialog.findViewById(R.id.textView1);
		textView1.setWidth(width);
		sendapdu_but = (Button) smartdialog.findViewById(R.id.sendapdu_but);
		sendapdu_but.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				v.setEnabled(false);
				String str = "";
				int len = 0;
				int iReturnvalue = 0;
				byte[] responsebuf = new byte[500];
				try {
					iReturnvalue = smart.iSendReceiveApduCommand("00A4000400",responsebuf);
					len = iReturnvalue;
					if (len>0) {
						str = HexString.bufferToHex(responsebuf, 0, len);
						if (D) Log.d(TAG,"Response  1 Data: " + str);
					}
					responsebuf = new byte[500];
					int sendapdu3 = smart.iSendReceiveApduCommand("00A40004027000", responsebuf);
					if (D) Log.d(TAG,"Response 3 Data three" + sendapdu3);
					String str3 ="";
					if (sendapdu3>0) {
						str3 = HexString.bufferToHex(responsebuf, 0,sendapdu3);
					}
					if (D) Log.d(TAG,"App Select MF :\n" + str + "\n"+ "Select DF 7000 :\n" + str3 + "\n");
					if((str.equals("")&(str3.equals("")))) {
						SCard.obtainMessage(MESSAGE_BOX,"Smart Card is secured.Please Insert another card").sendToTarget();
					}else{
						sendapdu_tv.setText("Select MF :\n" + str + "\n"+ "Select DF 7000 :\n" + str3 + "\n");
					}
				} catch (Exception e) {
				}
			}
		});
		smartdialog.show();
	}
}
