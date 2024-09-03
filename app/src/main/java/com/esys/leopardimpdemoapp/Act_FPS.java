package com.esys.leopardimpdemoapp;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.esys.leopardimpdemoapp.R;
import com.evolute.bluetooth.BluetoothComm;
import com.leopard.api.FPS;
import com.leopard.api.FpsConfig;
import com.leopard.api.FpsImageAPI;
import com.leopard.api.HexString;

public class Act_FPS extends Activity implements OnClickListener {
	private static final String TAG = "FPSAPI";
	// private static final boolean D = BluetoothConnect.D;
	private Button btn_capturefinger, btn_Verify, btn_Imagecompressed, btn_ImageUncompressed;
	FPS fps;
	private LinearLayout llImage;
	OutputStream outputStream;
	InputStream inputstream;
	Context context = this;
	static ProgressDialog dlgCustom,dlgpd;
	FpsConfig fpsconfig = new FpsConfig();
	private byte[] brecentminituaedata = {};
	//private byte[] image = {};
	//private byte[] image1 = {};
	private int iRetVal;
	//private boolean conStatus = false;
	public static final int DEVICE_NOTCONNECTED = -100;
	private static final int MESSAGE_BOX = 1;
	private boolean blVerifyfinger = false;
	private boolean blVerifycompressed = false;
	private boolean blVerifyuncompressed = false;
	private ImageView imgCapture;

	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fps);
		// Obtaining Input and Output Streams from Bluetooth Connection
		try {
			outputStream = BluetoothComm.mosOut;
			inputstream = BluetoothComm.misIn;
			fps = new FPS(Act_Main.setupInstance, outputStream, inputstream);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// initialize the buttons
		llImage = (LinearLayout)findViewById(R.id.llImage);
		btn_capturefinger = (Button) findViewById(R.id.btn_Capturefinger);
		btn_capturefinger.setOnClickListener(this);
		btn_Verify = (Button) findViewById(R.id.btn_Verify);
		btn_Verify.setOnClickListener(this);
		btn_Imagecompressed = (Button) findViewById(R.id.btn_Imagecompressed);
		btn_Imagecompressed.setOnClickListener(this);
		btn_ImageUncompressed = (Button) findViewById(R.id.btn_ImageUncompressed);
		btn_ImageUncompressed.setOnClickListener(this);
		imgCapture = (ImageView) findViewById(R.id.captureimge);
	}

	/* providing options for various functionalities of FPS */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_Capturefinger:
			/* CaptureFinger undergoes AsynTask operation */
			CaptureFingerAsyn captureFinger = new CaptureFingerAsyn();
			captureFinger.execute(0);
			break;
		case R.id.btn_Verify:
			/* VerifyTempleAsync undergoes AsynTask operation */
			VerifyTempleAsync verifyTemp = new VerifyTempleAsync();
			verifyTemp.execute(0);
			break;
		case R.id.btn_Imagecompressed:
			/* FpsImagecompressed undergoes AsynTask operation */
			FpsImagecompressed imageCompressed = new FpsImagecompressed();
			imageCompressed.execute(0);
			break;
		case R.id.btn_ImageUncompressed:
			/* UncompressedImage undergoes AsynTask operation */
			UncompressedImage uncompressImage = new UncompressedImage();
			uncompressImage.execute(0);
			break;
		default:
			break;
		}
	}

	/* Handler to display UI response messages */
	@SuppressLint("HandlerLeak")
	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MESSAGE_BOX:
				String str = (String) msg.obj;
				ShowDialog(str);
				break;
			default:
				break;
			}
		};
	};

	/* To show response messages */
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

	/* This method shows the CaptureFinger AsynTask operation */
	public class CaptureFingerAsyn extends AsyncTask<Integer, Integer, Integer> {
		/* displays the progress dialog until background task is completed */
		@Override
		protected void onPreExecute() {
			progressDialog(context, "Place your finger on FPS ...");
			super.onPreExecute();
		}
		/* Task of CaptureFinger performing in the background */
		@Override
		protected Integer doInBackground(Integer... params) {
			try {
				brecentminituaedata = new byte[3500];
				fpsconfig = new FpsConfig(0, (byte) 0x0F);
				brecentminituaedata = fps.bFpsCaptureMinutiae(fpsconfig);
				iRetVal = fps.iGetReturnCode();
				byte[] bMinutiaeData =fps.bGetMinutiaeData();
				String str1 = HexString.bufferToHex(bMinutiaeData);
				Log.i("Capture","Finger Data:\n"+str1);
			} catch (NullPointerException e) {
				iRetVal = DEVICE_NOTCONNECTED;
				return iRetVal;
			}
			return iRetVal;
		}

		/*
		 * This function sends message to handler to display the status messages
		 * of Diagnose in the dialog box
		 */
		@Override
		protected void onPostExecute(Integer result) {
			dlgCustom.dismiss();
			if (iRetVal == DEVICE_NOTCONNECTED) {
				handler.obtainMessage(DEVICE_NOTCONNECTED,"Device not connected").sendToTarget();
			} else if (iRetVal == FPS.SUCCESS) {
				blVerifyfinger = true;
				handler.obtainMessage(MESSAGE_BOX, "Capture finger success").sendToTarget();
			} else if (iRetVal == FPS.FPS_INACTIVE_PERIPHERAL) {
				handler.obtainMessage(MESSAGE_BOX,"Peripheral is inactive").sendToTarget();
			} else if (iRetVal == FPS.TIME_OUT) {
				handler.obtainMessage(MESSAGE_BOX, "Capture finger time out").sendToTarget();
			} else if (iRetVal == FPS.FAILURE) {
				handler.obtainMessage(MESSAGE_BOX, "Capture finger failed").sendToTarget();
			} else if (iRetVal == FPS.PARAMETER_ERROR) {
				handler.obtainMessage(MESSAGE_BOX, "Parameter error").sendToTarget();
			} else if (iRetVal == FPS.FPS_INVALID_DEVICE_ID) {
				handler.obtainMessage(MESSAGE_BOX,"Connected  device is not license authenticated.").sendToTarget();
			} else if (iRetVal == FPS.FPS_ILLEGAL_LIBRARY) {
				handler.obtainMessage(MESSAGE_BOX,"Library not valid").sendToTarget();
			}
			super.onPostExecute(result);
		}
	}

	/* This method shows the VerifyTempleAsync AsynTask operation */
	public class VerifyTempleAsync extends AsyncTask<Integer, Integer, Integer> {
		/* displays the progress dialog until background task is completed */
		@Override
		protected void onPreExecute() {
			progressDialog(context, "Place your finger on FPS ...");
			super.onPreExecute();
		}

		/* Task of VerifyTempleAsync performing in the background */
		@Override
		protected Integer doInBackground(Integer... params) {
			if (blVerifyfinger == false) {
				dlgCustom.dismiss();
				handler.obtainMessage(MESSAGE_BOX,"Please capture finger and then verify").sendToTarget();
			} else if (blVerifyfinger == true) {
				try {
					iRetVal = fps.iFpsVerifyMinutiae(brecentminituaedata,new FpsConfig(1, (byte) 0x0f));
				} catch (NullPointerException e) {
					iRetVal = DEVICE_NOTCONNECTED;
					return iRetVal;
				}
			}
			return iRetVal;
		}

		/*
		 * This function sends message to handler to display the status messages
		 * of Diagnose in the dialog box
		 */
		@Override
		protected void onPostExecute(Integer result) {
			dlgCustom.dismiss();
			if (iRetVal == DEVICE_NOTCONNECTED) {
				handler.obtainMessage(DEVICE_NOTCONNECTED,"Device not connected").sendToTarget();
			} else if (iRetVal == FPS.SUCCESS) {
				handler.obtainMessage(MESSAGE_BOX,"Captured template verification is success").sendToTarget();
				//blVerifyfinger=false;
			} else if (iRetVal == FPS.FPS_INACTIVE_PERIPHERAL) {
				handler.obtainMessage(MESSAGE_BOX,"Peripheral is inactive").sendToTarget();
			} else if (iRetVal == FPS.TIME_OUT) {
				handler.obtainMessage(MESSAGE_BOX, "Capture finger time out").sendToTarget();
			} else if (iRetVal == FPS.FPS_ILLEGAL_LIBRARY) {
				handler.obtainMessage(MESSAGE_BOX, "Illegal library").sendToTarget();
			} else if (iRetVal == FPS.FAILURE) {
				handler.obtainMessage(MESSAGE_BOX,"Captured template verification is failed,\nWrong finger placed").sendToTarget();
			} else if (iRetVal == FPS.PARAMETER_ERROR) {
				handler.obtainMessage(MESSAGE_BOX, "Parameter error").sendToTarget();
			} else if (iRetVal == FPS.FPS_INVALID_DEVICE_ID) {
				handler.obtainMessage(MESSAGE_BOX,"Library is in demo version").sendToTarget();
			} else if (iRetVal == FPS.FPS_INVALID_DEVICE_ID) {
				handler.obtainMessage(MESSAGE_BOX,"Connected  device is not license authenticated.").sendToTarget();
			} else if (iRetVal == FPS.FPS_ILLEGAL_LIBRARY) {
				handler.obtainMessage(MESSAGE_BOX,"Library not valid").sendToTarget();
			}
			super.onPostExecute(result);
		}
	}

	Bitmap bitmapimage = null;
	byte[] bCmpData;
	byte[] bUncmpData;
	byte[] bBmpData;

	/* This method shows the FpsImagecompressed AsynTask operation */
	public class FpsImagecompressed extends AsyncTask<Integer, Integer, Integer> {
		/* displays the progress dialog until background task is completed */
		@Override
		protected void onPreExecute() {
			Horigontalprogress(context,"Place your finger for capture and please wait for the response...");
			super.onPreExecute();
		}

		/* Task of FpsImagecompressed performing in the background */
		@Override
		protected Integer doInBackground(Integer... params) {
			try {
				//byte[] image1 = new byte[3500];
				byte[] bufvalue1 = new byte[3500];
				iRetVal = fps.iGetFingerImageCompressed(bufvalue1,new FpsConfig(1, (byte) 0x0f));
				if(iRetVal<0){
					return iRetVal;
				}
				bCmpData = fps.bGetImageData();
				brecentminituaedata =fps.bGetMinutiaeData();
				String image = HexString.bufferToHex(brecentminituaedata);
				Log.i("Minutiae","Image Compressed Data:\n"+image);
				bUncmpData = FpsImageAPI.bGetUncompressedRawData(bCmpData);
				bBmpData = FpsImageAPI.bConvertRaw2bmp(bUncmpData);
			} catch (NullPointerException e) {
				iRetVal = -100;
				return iRetVal;
			}
			return iRetVal;
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			// TODO Auto-generated method stub
			dlgpd.setIndeterminate(false);
			super.onProgressUpdate(values);
		}

		/*
		 * This function sends message to handler to display the status messages
		 * of Diagnose in the dialog box
		 */
		@Override
		protected void onPostExecute(Integer result) {
			dlgpd.dismiss();
			if (iRetVal == DEVICE_NOTCONNECTED) {
				handler.obtainMessage(DEVICE_NOTCONNECTED,"Device not connected").sendToTarget();
			} else if (iRetVal > 0) {
				blVerifycompressed = true;
				blVerifyfinger = true;
				handler.obtainMessage(MESSAGE_BOX, "Capture finger success").sendToTarget();
				llImage.setVisibility(View.VISIBLE);
				Bitmap bmpfinalnew = BitmapFactory.decodeByteArray(bBmpData, 0,bBmpData.length);
				imgCapture.setImageBitmap(bmpfinalnew);
			} else if (iRetVal == FPS.FPS_INACTIVE_PERIPHERAL) {
				handler.obtainMessage(MESSAGE_BOX,"Peripheral is inactive").sendToTarget();
			} else if (iRetVal == FPS.TIME_OUT) {
				handler.obtainMessage(MESSAGE_BOX, "Capture finger time out").sendToTarget();
			} else if (iRetVal == FPS.FPS_ILLEGAL_LIBRARY) {
				handler.obtainMessage(MESSAGE_BOX, "Illegal library").sendToTarget();
			} else if (iRetVal == FPS.FAILURE) {
				handler.obtainMessage(MESSAGE_BOX,"Captured template verification is failed,Wrong finger placed").sendToTarget();
			} else if (iRetVal == FPS.PARAMETER_ERROR) {
				handler.obtainMessage(MESSAGE_BOX, "Parameter error").sendToTarget();
			} else if (iRetVal == FPS.FPS_INVALID_DEVICE_ID) {
				handler.obtainMessage(MESSAGE_BOX,"Library is in demo version").sendToTarget();
			} else if (iRetVal == FPS.FPS_INVALID_DEVICE_ID) {
				handler.obtainMessage(MESSAGE_BOX,"Connected  device is not license authenticated.").sendToTarget();
			} else if (iRetVal == FPS.FPS_ILLEGAL_LIBRARY) {
				handler.obtainMessage(MESSAGE_BOX,"Library not valid").sendToTarget();
			}
			super.onPostExecute(result);
		}
	}

	/* This method shows the UncompressedImage AsynTask operation */
	public class UncompressedImage extends AsyncTask<Integer, Integer, Integer> {
		/* displays the progress dialog until background task is completed */
		@Override
		protected void onPreExecute() {
			Horigontalprogress(context,"Place your finger for capture and please wait for the response...");
			super.onPreExecute();
		}
		byte[] value;
		/* Task of UncompressedImage performing in the background */
		@SuppressLint("NewApi")
		@Override
		protected Integer doInBackground(Integer... params) {
			try {
				//image1 = new byte[3500];
				byte[] bufvalue2=new byte[3500];
				iRetVal = fps.iGetFingerImageUnCompressed(bufvalue2, new FpsConfig(1, (byte) 0x0f));
				//Log.i("","new"+image1);
				if(iRetVal<0){
					return iRetVal;
				}
				brecentminituaedata =fps.bGetMinutiaeData();
				String str = HexString.bufferToHex(brecentminituaedata);
				Log.i("Minutiae","Image UnCompressed Data:\n"+str);
				
				} catch (NullPointerException e) {
				iRetVal = -100;
				e.printStackTrace();
				return iRetVal;
			}
			return iRetVal;
		}

		/*
		 * This function sends message to handler to display the status messages
		 * of Diagnose in the dialog box
		 */
		@SuppressLint("NewApi")
		@Override
		protected void onPostExecute(Integer result) {
			dlgpd.dismiss();
			System.out.println("UncompressedImage....>"+ iRetVal);
			if (iRetVal == DEVICE_NOTCONNECTED) {
				handler.obtainMessage(DEVICE_NOTCONNECTED,"Device not connected").sendToTarget();
			} else if (iRetVal > 0) {
				blVerifyuncompressed = true;
				blVerifyfinger = true;
				handler.obtainMessage(MESSAGE_BOX,"Get unCompressed image data success").sendToTarget();
				byte[] imageBytes = fps.bGetImageData();
				value = FpsImageAPI.bConvertRaw2bmp(imageBytes);
				Bitmap bmpfinalnew = BitmapFactory.decodeByteArray(value, 0,value.length);
				llImage.setVisibility(View.VISIBLE);
				imgCapture.setImageBitmap(bmpfinalnew);
			} else if (iRetVal == FPS.FPS_INACTIVE_PERIPHERAL) {
				handler.obtainMessage(MESSAGE_BOX,"Peripheral is inactive").sendToTarget();
			} else if (iRetVal == FPS.TIME_OUT) {
				handler.obtainMessage(MESSAGE_BOX, "Capture finger time out").sendToTarget();
			} else if (iRetVal == FPS.FAILURE) {
				handler.obtainMessage(MESSAGE_BOX,"Captured template verification is failed,\nWrong finger placed").sendToTarget();
			} else if (iRetVal == FPS.PARAMETER_ERROR) {
				handler.obtainMessage(MESSAGE_BOX, "Parameter error").sendToTarget();
			} else if (iRetVal == FPS.FPS_INVALID_DEVICE_ID) {
				handler.obtainMessage(MESSAGE_BOX,"Library is in demo version").sendToTarget();
			} else if (iRetVal == FPS.FPS_INVALID_DEVICE_ID) {
				handler.obtainMessage(MESSAGE_BOX,"Connected  device is not license authenticated.").sendToTarget();
			} else if (iRetVal == FPS.FPS_ILLEGAL_LIBRARY) {
				handler.obtainMessage(MESSAGE_BOX,"Library not valid").sendToTarget();
			}
			super.onPostExecute(result);
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			// TODO Auto-generated method stub
			dlgpd.setIndeterminate(false);
			super.onProgressUpdate(values);
		}
	}

	/* This performs Progress dialog box to show the progress of operation */
	public static void progressDialog(Context context, String msg) {
		dlgCustom = new ProgressDialog(context);
		dlgCustom.setMessage(msg);
		dlgCustom.setIndeterminate(true);
		dlgCustom.setCancelable(false);
		dlgCustom.show();
	}

	@SuppressLint("NewApi")
	private void Horigontalprogress(Context context, String string) {
		// TODO Auto-generated method stub
		dlgpd = new ProgressDialog(context);
		dlgpd.setMessage(string);
		dlgpd.setCancelable(false);
		dlgpd.setIndeterminate(true);
		dlgpd.setProgressNumberFormat(null);
		dlgpd.setProgressPercentFormat(null);
		dlgpd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		dlgpd.show();
	}
	}
