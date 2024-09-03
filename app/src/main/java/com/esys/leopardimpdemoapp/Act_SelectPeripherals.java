package com.esys.leopardimpdemoapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.esys.leopardimpdemoapp.R;
import com.evolute.bluetooth.BluetoothComm;
import com.leopard.api.BaudChange;
public class Act_SelectPeripherals extends Activity implements OnClickListener{
	Context context = this;
	Button ptr_but,fps_but,mag_but,smart_but,serial_but,clscr_but,sam_but;
	Button information_but,exit_but;
	String to,subject,message;
	EditText textTo,textSubject,textMessage;
	BaudChange bdchage = Act_Main.bdchange;
	Dialog dlgSupport;
	int peripheral;
	private GlobalPool mGP = null;
	static ProgressDialog dialog;
	private static final boolean D = true;//BluetoothConnect.D;
	public static final String TAG = "SelectPeripherals";
	public static final int SUCCESS = 10;
	public static final int FAIL = -1;
	String str;
	int value;
	int iBaudRateRetValue;
	int baudrateval, baudratechange;
	TextView tv_printer,tv_fps,tv_mag,tv_smart,tv_sam,tv_clscr,tv_serlprt;
	TextView tv_Title;
	protected void onCreate(Bundle savedInstanceState) { //TODO
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_selectperi);
		tv_Title=(TextView)findViewById(R.id.titletxtvw);
		// initialize the buttons
		ptr_but = (Button)findViewById(R.id.ptr_but);
		ptr_but.setOnClickListener(this);
		
		fps_but =(Button)findViewById(R.id.fps_but);
		fps_but.setOnClickListener(this);
		
		mag_but=(Button)findViewById(R.id.mag_but);
		mag_but.setOnClickListener(this);
		
		smart_but=(Button)findViewById(R.id.smart_but);
		smart_but.setOnClickListener(this);
		
		serial_but=(Button)findViewById(R.id.serial_but);
		serial_but.setOnClickListener(this);
		
		clscr_but=(Button)findViewById(R.id.clscr_but);
		clscr_but.setOnClickListener(this);
		
		
		sam_but=(Button)findViewById(R.id.sam_but);
		sam_but.setOnClickListener(this);
		
		information_but = (Button)findViewById(R.id.information_but);
		information_but.setOnClickListener(this);
		
		exit_but = (Button)findViewById(R.id.exit_but);
		exit_but.setOnClickListener(this);
		
		tv_printer=(TextView) findViewById(R.id.textViewptr);
		tv_fps=(TextView) findViewById(R.id.textViewfps);
		tv_mag=(TextView) findViewById(R.id.textViewmag);

		tv_smart=(TextView) findViewById(R.id.textViewsmart);
		tv_serlprt=(TextView) findViewById(R.id.textViewserlprt);
		tv_clscr=(TextView) findViewById(R.id.textViewclscr);
		tv_sam=(TextView) findViewById(R.id.textViewsam);
		this.mGP = ((GlobalPool)this.getApplicationContext());
	}
	
	@Override
	 protected void onResume() {
	  super.onResume();
	  Animation animset_right = AnimationUtils.loadAnimation(this, R.anim.set_right);
	  Animation animset_left= AnimationUtils.loadAnimation(this, R.anim.set);
	  Animation animset_bottom= AnimationUtils.loadAnimation(this, R.anim.set_bottom);
	  Animation animalpha = AnimationUtils.loadAnimation(this, R.anim.alpha);
	  Animation animrotate= AnimationUtils.loadAnimation(this, R.anim.rotate);
	  Animation animbounce= AnimationUtils.loadAnimation(this,R.anim.bounce);
	  Animation animfadein= AnimationUtils.loadAnimation(this,R.anim.fadein);
	  Animation animffadein= AnimationUtils.loadAnimation(this,R.anim.ffadein);
	  ptr_but.setAnimation(animffadein);
	  fps_but.setAnimation(animffadein);
	  mag_but.setAnimation(animffadein);
	  smart_but.setAnimation(animffadein);
	  serial_but.setAnimation(animffadein);
	  clscr_but.setAnimation(animffadein);
	  sam_but.setAnimation(animffadein);
	  tv_printer.setAnimation(animbounce);
	  tv_fps.setAnimation(animbounce);
	  tv_mag.setAnimation(animbounce);
	  tv_smart.setAnimation(animbounce);
	  tv_serlprt.setAnimation(animbounce);
	  tv_clscr.setAnimation(animbounce);

	  tv_sam.setAnimation(animbounce);
	  information_but.setAnimation(animrotate);
	}
	//Button Events
	@Override
	public void onClick(View v) { //TODO
		switch (v.getId()) {
		case R.id.ptr_but:
			Intent printer = new Intent(getApplicationContext(),Act_Printer.class);
			startActivity(printer);
			break;
		case R.id.fps_but:
			Intent fps = new Intent(getApplicationContext(),Act_FPS.class);
			startActivity(fps);
			break;
		case R.id.mag_but:
			Intent mag = new Intent(getApplicationContext(),Act_MagCard.class);
			startActivity(mag);
			break;
		case R.id.smart_but:
			Intent smart = new Intent(getApplicationContext(),Act_SmartCard.class);
			startActivity(smart);
			break;
		case R.id.serial_but:
			Intent serial = new Intent(getApplicationContext(),Act_Serial.class);
			startActivity(serial);
			break;
			
		case R.id.clscr_but:
			Intent clscr = new Intent(getApplicationContext(),Act_Clscr.class);
			startActivity(clscr);
			break;
			
		case R.id.sam_but:
			Intent samInt = new Intent(getApplicationContext(),Act_SAM.class);
			startActivity(samInt);
			break;
		case R.id.information_but:
			dlgInformationBox();
			break;
		case R.id.exit_but:
			dlgExit();
			break;
		default:
			break;
		}
	}
	
	// display information dialog box
	public void dlgInformationBox() { //TODO
		Dialog alert = new Dialog(context);
		alert.getWindow();
		alert.requestWindowFeature(Window.FEATURE_NO_TITLE);
		// custom layout for information display
		alert.setContentView(R.layout.dlg_informationbox);
		TextView site_tv = (TextView) alert.findViewById(R.id.site_tv);
		String str_links = "<a href='http://www.evolute-sys.com'>www.evolute-sys.com</a>";
		site_tv.setLinksClickable(true);
		site_tv.setMovementMethod(LinkMovementMethod.getInstance());
		site_tv.setText(Html.fromHtml(str_links));
		//site_tv.setText("www.evolute-sys.com");
		TextView supportteam_tv = (TextView) alert.findViewById(R.id.supportteam_tv);
		String supportteam_links = "<a href='http://supportteam'>sales@evolute-sys.com</a>";
		supportteam_tv.setText(Html.fromHtml(supportteam_links));
		//supportteam_tv.setText("supportteam");
		supportteam_tv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dlgSupportEmail("sales@evolute-sys.com");
			}
		});
		TextView feedbck_tv = (TextView) alert.findViewById(R.id.feedbck_tv);
		String feedback_links = "<a href='http://feedback'>support@evolute-sys.com</a>";
		feedbck_tv.setText(Html.fromHtml(feedback_links));
		//feedbck_tv.setText("feedback");
		feedbck_tv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dlgSupportEmail("support@evolute-sys.com");
			}
		});
		alert.show();
	}
	
	// displays a dialog box for composing a mail
		public void dlgSupportEmail(String stEmailId) { //TODO
			Button buttonSend;
			Display display = getWindowManager().getDefaultDisplay(); 
			@SuppressWarnings("deprecation")
			int width = display.getWidth();  
			dlgSupport = new Dialog(context);
			dlgSupport.getWindow();
			dlgSupport.requestWindowFeature(Window.FEATURE_NO_TITLE);
			dlgSupport.setContentView(R.layout.mail_bdteamsupport);
			textTo = (EditText) dlgSupport.findViewById(R.id.editTextTo);
			textTo.setText(stEmailId);
			textTo.setWidth(width);
			textSubject = (EditText) dlgSupport.findViewById(R.id.editTextSubject);
			textMessage = (EditText) dlgSupport.findViewById(R.id.editTextMessage);
			buttonSend = (Button) dlgSupport.findViewById(R.id.buttonSend);
			buttonSend.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					to = textTo.getText().toString();
					subject = textSubject.getText().toString();
					message = textMessage.getText().toString();
					Intent email = new Intent(Intent.ACTION_SEND);
					email.putExtra(Intent.EXTRA_EMAIL, new String[] { to });
					email.putExtra(Intent.EXTRA_SUBJECT, subject);
					email.putExtra(Intent.EXTRA_TEXT, message);
					email.setType("message/rfc822");
					startActivity(Intent.createChooser(email,"Choose an Email client :"));
					dlgSupport.cancel();
				}
			});
			dlgSupport.show();
		}
		// if back key is pressed prompts for a exit confirmation dialog box
	public boolean onKeyDown(int keyCode, KeyEvent event) { //TODO
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			dlgExit();
		}
		return super.onKeyDown(keyCode, event);
	}
	
   //Exit confirmation dialog box
	public void dlgExit() {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
		// set title
		alertDialogBuilder.setTitle("Leopard Demo Application");
		//alertDialogBuilder.setIcon(R.drawable.icon);
		// set dialog message
		if(Act_Main.blnResetBtnEnable==true) {
			alertDialogBuilder.setMessage( "Do You want to Reset Baud rate for 9600bps\nOR\n"
					+"Do you want to exit Leopard Demo application");
		} else {
			alertDialogBuilder.setMessage(
					"Do you want to exit from Leopard Demo application");
		}
		alertDialogBuilder.setCancelable(false);
		alertDialogBuilder.setPositiveButton("Yes",new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,int id) {
				//Main.bdchange.iResetPeripheral9600();
				try {
					BluetoothComm.mosOut = null;
					BluetoothComm.misIn = null;
				} catch(NullPointerException e) { }
				if (BluetoothComm.misIn != null)
					BluetoothComm.misIn=null;
				if(BluetoothComm.mosOut!=null)
					BluetoothComm.mosOut=null;
				// Make sure we're not doing discoveryanymore
				if (Act_BTDiscovery.mBT != null) {
					Act_BTDiscovery.mBT.cancelDiscovery();
				}
				System.gc();
				//System.exit(0);
				Act_SelectPeripherals.this.finish();
			}
		});
		alertDialogBuilder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,
					int id) {
				// if this button is clicked, just close
				// the dialog box and do nothing
				dialog.cancel();
			}
		});
		alertDialogBuilder.setNeutralButton("Reset", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				try {
					bdchage = new BaudChange(Act_Main.setupInstance,
 							BluetoothComm.mosOut,BluetoothComm.misIn);
 					if(bdchage==null)
 					{
 						Log.e("BADCHANG","NULL");
 					}else {
 						Log.e("BADCHANG","NOT NULL");
					}
 				} catch (Exception e) { }
				try {
				Reset9600ASYC resetASYC = new 	Reset9600ASYC();
				resetASYC.execute(0);
				
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();
		if(Act_Main.blnResetBtnEnable==true) {
			alertDialog.getButton(AlertDialog.BUTTON3).setEnabled(true); //
		} else {
			alertDialog.getButton(AlertDialog.BUTTON3).setEnabled(false);	
		}
		alertDialog.show();
	}
	
	/* Handler to display UI response messages   */
	Handler resethandler = new Handler()
	{
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case SUCCESS:
				str = (String) msg.obj;
				showdialog(str);
				break;
			case FAIL:
				str = (String)msg.obj;
				showdialog(str);
				break;
			default:
				break;
			}
		};
	};
	//display error information dialog box
	public void showdialog(String str) {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
		alertDialogBuilder.setTitle("Leopard Demo Application");
		alertDialogBuilder.setMessage(str).setCancelable(false)
		.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.dismiss();
			}
		});
		/* create alert dialog*/
		AlertDialog alertDialog = alertDialogBuilder.create();
		/* show alert dialog*/
		alertDialog.show();
	}
	/* This method shows the Reset9600 AsynTask operation*/
	public class Reset9600ASYC extends AsyncTask<Integer, Integer, Integer> {
		/* displays the progress dialog until background task is completed*/
		@Override
		protected void onPreExecute() {
			progressDialog(context, "Please Wait ...");
			super.onPreExecute();
		}
		/* Task of Reset9600 performing in the background*/
		@Override
		protected Integer doInBackground(Integer... params) {
			Act_Main.blnResetBtnEnable=false;
			Log.e(TAG, "Change the peripheral Speed");
			try {
				if(bdchage==null)
					Log.e("Reset Peri","Instance is NULL");
				value = bdchage.iResetPeripheral9600();
				Log.e("Reset Peri","All>>>>>>>>>>>>>"+value);
				Thread.sleep(2000);
				BluetoothComm.mosOut=null;
				BluetoothComm.misIn=null;
				mGP.closeConn();
				Thread.sleep(2000);
			if (Act_Main.mBT != null) {
				Act_Main.mBT.cancelDiscovery();
			}
				Thread.sleep(2000);
				Log.d(TAG, "Reconnect to same device");
				boolean b =mGP.createConn(Act_Main.mBDevice.getAddress());
				if(b==true)
					//new connSocketTask().execute(mBDevice.getAddress());
					//mGP.mBTcomm.isConnect();
				{
					Thread.sleep(2000);
					Log.e("For Loop","............>"+true);
					baudratechange	= bdchage.iResetBT9600(
					BluetoothComm.mosOut,BluetoothComm.misIn);
				}else {
					Log.e("For Loop","............>"+false);
					Toast.makeText(getApplicationContext(), "Connection is not down", Toast.LENGTH_LONG).show();
				}

				Log.e(TAG, "DONE the BT Speed"+baudratechange);
			} catch (Exception e) {
				Log.d(".........>", "value"+e.getMessage());
				e.printStackTrace();
			}
			
		    return baudratechange;
		
			
		}

		/* This displays the status messages of Reset9600 in the dialog box */
		@Override
		protected void onPostExecute(Integer result) {
			Log.e(TAG, "DONE the BT Speed...<<<<<>>"+baudratechange);
			dialog.dismiss();
			if(value==bdchage.BC_SUCCESS)
			{
			    resethandler.obtainMessage(SUCCESS, "Baud rate Changed 9600bps Successfully").sendToTarget();
				
			}else if (value==bdchage.BC_FAILURE) {
				resethandler.obtainMessage(FAIL, "Baud rate Changed 9600bps Fail").sendToTarget();
			}
			super.onPostExecute(result);
		}
	}
	// displays a progress dialog with message
		public static void progressDialog(Context context, String msg) {
			dialog = new ProgressDialog(context);
			dialog.setMessage(msg);
			dialog.setIndeterminate(true);
			dialog.setCancelable(false);
			dialog.show();
		}
		
}
