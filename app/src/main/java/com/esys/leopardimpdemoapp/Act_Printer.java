package com.esys.leopardimpdemoapp;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.esys.leopardimpdemoapp.R;
import com.esys.leopardimpdemoapp.R.drawable;
import com.evolute.bluetooth.BluetoothComm;

import com.evolute.qrimage.QRCodeGenerator;
import com.evolute.textimage.TextGenerator;
import com.evolute.textimage.TextGenerator.ImageWidth;
import com.evolute.textimage.TextGenerator.Justify;
import com.leopard.api.Printer;
public class Act_Printer extends Activity implements OnClickListener {
	private ImageView img_Galery;
	private String sPicturePath="";
	Context context = this;
	private static byte bFontstyle;
	private static byte bBarcodestyle;
	private static int bacodepostion = 1;
	private Button btn_TestPrint, btn_LogoPrint, btn_CustomText, btn_Bitmap,
	btn_BarCode, btn_PaperFeed, btn_Diagnostics, btn_Lineprint,btn_unicode,btn_grayscaleprint,btn_qrcodeprint;
	public static Printer ptr;
	InputStream inputStream = null;
	OutputStream outputStream = null;
	private int iRetVal;
	private EditText edt_Text, edt_BarCode, edt_AddLine;
	private static ProgressDialog dlgPg;
	private String sBarCode, sAddData;
	private byte bAddLineSyle;
	private Dialog dlgBarCode;
	TextView tv_selectLang;
	Spinner sp_SelectLanguage,sp_SelectPosition;
	public static String str;
	public static Justify  justfypostion;
	private String[] sEnterTextFont = { 
			"FONT LARGE NORMAL", "FONT LARGE BOLD",
			"FONT SMALL NORMAL", "FONT SMALL BOLD", "FONT ULLARGE NORMAL",
			"FONT ULLARGE BOLD", "FONT ULSMALL NORMAL", "FONT ULSMALL BOLD",
			"FONT 180LARGE NORMAL", "FONT 180LARGE BOLD",
			"FONT 180SMALL NORMAL", "FONT 180 SMALLBOLD",
			"FONT 180ULLARGE NORMAL", "FONT 180ULLARGE BOLD",
			"FONT 180ULSMALL NORMAL", "FONT 180ULSMALL BOLD" 
	};
	private String[] sBarCodeStyle = {
			"BARCODE 2OF5 COMPRESSED","BARCODE 2OF5 UNCOMPRESSED", 
			"BARCODE 3OF9 COMPRESSED","BARCODE 3OF9 UNCOMPRESSED", 
			"EAN13/UPC-A" 
	};
	/*   List of Return codes for the respective response */
	public static final int DEVICE_NOTCONNECTED = -100;
	public static int RESULT_LOAD_IMAGE = 10;
	private int iWidth ;
	private final static int MESSAGE_BOX = 1;
	public static  Button cust_btn;
	public static  EditText cust_edt;
	private static int selecpostion;
	static LinearLayout linrlayout;
	static LinearLayout linrtxtvw;
	static Boolean beditprint=false;
	private Button btnOk, btnUnicode11;
	private static Button btnconfirm;
	public static Boolean bConfirm=false;
	EditText edt_qrcode;
	String sQrcode;
	static int counter=0;
	private Boolean bGrayscale=false;
	@SuppressWarnings("deprecation")
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_printer);
		Display display = getWindowManager().getDefaultDisplay(); 
		iWidth = display.getWidth();
		// initialize the buttons
		btn_TestPrint = (Button) findViewById(R.id.btn_TestPrint);
		btn_TestPrint.setOnClickListener(this);

		btn_LogoPrint = (Button) findViewById(R.id.btn_LogoPrint);
		btn_LogoPrint.setOnClickListener(this);

		btn_CustomText = (Button) findViewById(R.id.btn_CustomText);
		btn_CustomText.setOnClickListener(this);

		btn_Bitmap = (Button) findViewById(R.id.btn_Bitmap);
		btn_Bitmap.setOnClickListener(this);

		btn_BarCode = (Button) findViewById(R.id.btn_BarCode);
		btn_BarCode.setOnClickListener(this);

		btn_PaperFeed = (Button) findViewById(R.id.btn_PaperFeed);
		btn_PaperFeed.setOnClickListener(this);

		btn_Diagnostics = (Button) findViewById(R.id.btn_Diagnostics);
		btn_Diagnostics.setOnClickListener(this);
		
		btn_unicode = (Button)findViewById(R.id.btn_unicode);
		btn_unicode.setOnClickListener(this);

		btn_Lineprint = (Button) findViewById(R.id.btn_Lineprint);
		btn_Lineprint.setOnClickListener(this);
		
		btn_grayscaleprint=(Button) findViewById(R.id.btn_grayscale);
		btn_grayscaleprint.setOnClickListener(this);
		
		btn_qrcodeprint=(Button) findViewById(R.id.btn_Qrcode);
		btn_qrcodeprint.setOnClickListener(this);

		try {
			OutputStream outSt = BluetoothComm.	mosOut;
			InputStream inSt = BluetoothComm.misIn;
			ptr = new Printer(Act_Main.setupInstance, outSt, inSt);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/* Handler to display UI response messages   */
	@SuppressLint("HandlerLeak")
	Handler ptrhandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MESSAGE_BOX:
				String str = (String) msg.obj;
				showDialog(str);
				break;
			default:
				break;
			}
		};
	};

	/*  To show response messages  */
	public void showDialog(String str) {
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
	
	//Button Events
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_TestPrint:
			/* ITestPrint  undergoes AsynTask operation*/
			TestPrintTask testprint = new TestPrintTask();
			testprint.execute(0);
			break;
		case R.id.btn_LogoPrint:
			/* ILogPrint  undergoes AsynTask operation*/
			LogPrintTask logprint = new LogPrintTask();
			logprint.execute(0);
			break;
		case R.id.btn_CustomText:
			EnterText();
			break;
		case R.id.btn_Bitmap:
			ShowImagDialog();
			break;
       case R.id.btn_grayscale:
    	   bGrayscale=true;
    	   ShowImagDialog();
			break;
		case R.id.btn_BarCode:
			BarCodeDialog();
			break;
		case R.id.btn_PaperFeed:
			/* PaperFeed  undergoes AsynTask operation*/
			PaperFeedTask paperFeed = new PaperFeedTask();
			paperFeed.execute(0);
			break;
		case R.id.btn_Diagnostics:
			/* DiagnousPrint  undergoes AsynTask operation*/
			DiagnousTask diagonous = new DiagnousTask();
			diagonous.execute(0);
			break;
		case R.id.btn_Lineprint:
			AddLinebox();
			break;
		case R.id.btn_unicode:
			Unicode();
			break;
		case R.id.btn_Qrcode:
			Log.e("","++++++++++++++++++++++qrcode print++++++++++++++++++++++");
			Qrcodeprint();
			break;
		default:
			break;
		}
	}
	Dialog dlgqrcode;
	private void Qrcodeprint(){
		Log.e("", "++++++++Qrcode dialog++");
	 dlgqrcode = new Dialog(context);
		Log.e("", "++++++++Qrcode dialog++");
		dlgqrcode.setContentView(R.layout.dlg_qrcode);
		Log.e("", "++++++++Qrcode dialog++");	
		dlgqrcode.setTitle("Enter Text to Print Qrcode");
		dlgqrcode.setCancelable(true);
		 edt_qrcode=(EditText)dlgqrcode.findViewById(R.id.edt_qrcode);
		Button but_qrcodeprint=(Button)dlgqrcode.findViewById(R.id.but_qrcodeprnt);
		but_qrcodeprint.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (edt_qrcode.length()==0) {
					Toast.makeText(Act_Printer.this, "Please Enter text to print Qrcode", Toast.LENGTH_LONG).show();
				}else{
				Qrcode qrcodeprnt=new Qrcode();
				qrcodeprnt.execute(0);
			}}
		});
		dlgqrcode.show();
	}
	
	class Qrcode extends AsyncTask<Integer, Integer, Integer>{
		@Override
		protected void onPreExecute() {
			progressDialog(context, "Please Wait....");
			super.onPreExecute();
		}
		@Override
		protected Integer doInBackground(Integer... params) {
			sQrcode=edt_qrcode.getText().toString();
			Log.e(">>QR", "in method...>>");
			Bitmap bmpDrawQRCode = null;
			try {
				bmpDrawQRCode = QRCodeGenerator.bmpDrawQRCode(ImageWidth.Inch_2,sQrcode);
				Log.e(">>QR", "before api");
				byte[] bBmpFileData = TextGenerator.bGetBmpFileData(bmpDrawQRCode);
				Log.d("leg", "byte data...."+com.leopard.api.HexString.bufferToHex(bBmpFileData));//bBmpFileData);
				ByteArrayInputStream bis = new ByteArrayInputStream(bBmpFileData);
				iRetVal=ptr.iBmpPrint(bis);
				Log.e("QR", "result"+iRetVal);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (Exception e) {
				iRetVal=DEVICE_NOTCONNECTED;
				e.printStackTrace();
			}
			return iRetVal;
		}
		@Override
		protected void onPostExecute(Integer result) {
			dlgPg.dismiss();
			if (iRetVal == DEVICE_NOTCONNECTED) {
				ptrhandler.obtainMessage(1, "Device not connected")
						.sendToTarget();
			} else if (iRetVal == Printer.PR_SUCCESS) {
				ptrhandler.obtainMessage(1, "Printing Successful")
						.sendToTarget();
				dlgqrcode.dismiss();
			} else if (iRetVal == Printer.PR_PLATEN_OPEN) {
				ptrhandler.obtainMessage(1, "Platen open").sendToTarget();
			} else if (iRetVal == Printer.PR_PAPER_OUT) {
				ptrhandler.obtainMessage(1, "Paper out").sendToTarget();
			} else if (iRetVal == Printer.PR_IMPROPER_VOLTAGE) {
				ptrhandler.obtainMessage(1, "Printer at improper voltage")
						.sendToTarget();
			} else if (iRetVal == Printer.PR_FAIL) {
				ptrhandler.obtainMessage(1, "Printing failed").sendToTarget();
			} else if (iRetVal == Printer.PR_PARAM_ERROR) {
				ptrhandler.obtainMessage(1, "Parameter error").sendToTarget();
			} else if (iRetVal == Printer.PR_NO_RESPONSE) {
				ptrhandler.obtainMessage(1, "No response from Legend device")
						.sendToTarget();
			} else if (iRetVal ==Printer.PR_DEMO_VERSION) {
				ptrhandler.obtainMessage(1, "Library in demo version")
						.sendToTarget();
			} else if (iRetVal == Printer.PR_INVALID_DEVICE_ID) {
				ptrhandler.obtainMessage(1,
						"Connected  device is not authenticated.")
						.sendToTarget();
			} else {
				ptrhandler.obtainMessage(1, "Unknown Response from Device")
						.sendToTarget();
			}
			super.onPostExecute(result);
		}
	}

	public void AddLinebox() {
		final Dialog addline_dailog = new Dialog(context);
		addline_dailog.setTitle("Line Printing");
		addline_dailog.setContentView(R.layout.dlg_addline);
		edt_AddLine = (EditText) addline_dailog.findViewById(R.id.addline_edt);
		Spinner sp_AddLine = (Spinner) addline_dailog.findViewById(R.id.addline_sp);
		ArrayAdapter<String> arryFontStyle = new ArrayAdapter<String>(this,android.R.layout.select_dialog_item, sEnterTextFont);
		arryFontStyle.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		sp_AddLine.setAdapter(arryFontStyle);
		sp_AddLine.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				switch (position) {
				case 0:
					bAddLineSyle = (byte) 0x01;
					break;
				case 1:
					bAddLineSyle = (byte) 0x02;
					break;
				case 2:
					bAddLineSyle = (byte) 0x03;
					break;
				case 3:
					bAddLineSyle = (byte) 0x04;
					break;
				case 4:
					bAddLineSyle = (byte) 0x05;
					break;
				case 5:
					bAddLineSyle = (byte) 0x06;
					break;
				case 6:
					bAddLineSyle = (byte) 0x07;
					break;
				case 7:
					bAddLineSyle = (byte) 0x08;
					break;
				case 8:
					bAddLineSyle = (byte) 0x09;
					break;
				case 9:
					bAddLineSyle = (byte) 0x0A;
					break;
				case 10:
					bAddLineSyle = (byte) 0x0B;
					break;
				case 11:
					bAddLineSyle = (byte) 0x0C;
					break;
				case 12:
					bAddLineSyle = (byte) 0x0D;
					break;
				case 13:
					bAddLineSyle = (byte) 0x0E;
					break;
				case 14:
					bAddLineSyle = (byte) 0x0F;
					break;
				case 15:
					bAddLineSyle = (byte) 0x10;
					break;
				}
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
			}
		});
		
		Button btn_AddLine = (Button) addline_dailog.findViewById(R.id.btn_Addline);
		btn_AddLine.setWidth(iWidth);
		btn_AddLine.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String addstr = edt_AddLine.getText().toString();
				if (addstr.length() == 0) {
					ptrhandler.obtainMessage(MESSAGE_BOX,"Enter Single character").sendToTarget();
				} else if (addstr.length() > 0) {
					addline_dailog.dismiss();
					AddLineAsyc addline = new AddLineAsyc();
					addline.execute(0);
				}
			}
		});
		addline_dailog.show();
	}

	/*   This method shows the ITestPrint  AsynTask operation */
	public class TestPrintTask extends AsyncTask<Integer, Integer, Integer> {
		/* displays the progress dialog until background task is completed*/
		@Override
		protected void onPreExecute() {
			progressDialog(context, "Please Wait ...");
			super.onPreExecute();
		}
		/* Task of ITestPrint performing in the background*/
		@Override
		protected Integer doInBackground(Integer... params) {
			try {
				ptr.iFlushBuf();
				iRetVal =   ptr.iTestPrint();
			} catch (NullPointerException e) {
				iRetVal = DEVICE_NOTCONNECTED;
				return iRetVal;
			}
			return iRetVal;
		}

		/* This displays the status messages of ITestPrint in the dialog box */
		@Override
		protected void onPostExecute(Integer result) {
			dlgPg.dismiss();
			if (iRetVal == DEVICE_NOTCONNECTED) {
				ptrhandler.obtainMessage(DEVICE_NOTCONNECTED,"Device not connected").sendToTarget();
			} else if (iRetVal == Printer.PR_SUCCESS) {
				ptrhandler.obtainMessage(MESSAGE_BOX, "Test print success").sendToTarget();
			} else if (iRetVal == Printer.PR_PLATEN_OPEN) {
				ptrhandler.obtainMessage(MESSAGE_BOX,"Printer platen is open").sendToTarget();
			} else if (iRetVal == Printer.PR_PAPER_OUT) {
				ptrhandler.obtainMessage(MESSAGE_BOX,"Printer paper is out").sendToTarget();
			} else if (iRetVal == Printer.PR_IMPROPER_VOLTAGE) {
				ptrhandler.obtainMessage(MESSAGE_BOX,"Printer improper voltage").sendToTarget();
			} else if (iRetVal == Printer.PR_FAIL) {
				ptrhandler.obtainMessage(MESSAGE_BOX,"Printer failed").sendToTarget();
			} else if (iRetVal == Printer.PR_PARAM_ERROR) {
				ptrhandler.obtainMessage(MESSAGE_BOX,"Printer param error").sendToTarget();
			} else if (iRetVal == Printer.PR_NO_RESPONSE) {
				ptrhandler.obtainMessage(MESSAGE_BOX,"No response from Leopard device").sendToTarget();
			}else if (iRetVal== Printer.PR_DEMO_VERSION) {
				ptrhandler.obtainMessage(MESSAGE_BOX,"Library is in demo version").sendToTarget();
			}else if (iRetVal==Printer.PR_INVALID_DEVICE_ID) {
				ptrhandler.obtainMessage(MESSAGE_BOX,"Connected  device is not license authenticated.").sendToTarget();
			}else if (iRetVal==Printer.PR_ILLEGAL_LIBRARY) {
				ptrhandler.obtainMessage(MESSAGE_BOX,"Library not valid").sendToTarget();
			}
			super.onPostExecute(result);
		}
	}

	/*   This method shows the ILogPrint  AsynTask operation */
	public class LogPrintTask extends AsyncTask<Integer, Integer, Integer> {
		/* displays the progress dialog until background task is completed*/
		@Override
		protected void onPreExecute() {
			progressDialog(context, "Please Wait ...");
			super.onPreExecute();
		}
		/* Task of ILogPrint performing in the background*/
		@Override
		protected Integer doInBackground(Integer... params) {
			try {
				ptr.iFlushBuf();
				//iRetVal =   ptr.iLogoPrint(Logos.EVOLUTER);
				iRetVal =   ptr.iBmpPrint(context,R.drawable.logo3);
				if (iRetVal == Printer.PR_SUCCESS) {
					String empty = "\n";
					ptr.iPrinterAddData((byte) 0x01, empty);
					ptr.iStartPrinting(1);
				}
			} catch (NullPointerException e) {
				iRetVal = DEVICE_NOTCONNECTED;
				return iRetVal;
			}
			return iRetVal;
		}
		/* This displays the status messages of ILogPrint in the dialog box */
		@Override
		protected void onPostExecute(Integer result) {
			dlgPg.dismiss();
			if (iRetVal == DEVICE_NOTCONNECTED) {
				ptrhandler.obtainMessage(DEVICE_NOTCONNECTED,"Device not connected").sendToTarget();
			} else if (iRetVal == Printer.PR_SUCCESS) {
				ptrhandler.obtainMessage(MESSAGE_BOX, "Print Success").sendToTarget();
			} else if (iRetVal == Printer.PR_PLATEN_OPEN) {
				ptrhandler.obtainMessage(MESSAGE_BOX,"Printer platen is open").sendToTarget();
			} else if (iRetVal == Printer.PR_PAPER_OUT) {
				ptrhandler.obtainMessage(MESSAGE_BOX,"Printer paper is out").sendToTarget();
			} else if (iRetVal == Printer.PR_IMPROPER_VOLTAGE) {
				ptrhandler.obtainMessage(MESSAGE_BOX,"Printer improper voltage").sendToTarget();
			} else if (iRetVal == Printer.PR_FAIL) {
				ptrhandler.obtainMessage(MESSAGE_BOX, "Printer failed").sendToTarget();
			} else if (iRetVal == Printer.PR_PARAM_ERROR) {
				ptrhandler.obtainMessage(MESSAGE_BOX,"Printer param error").sendToTarget();
			}else if (iRetVal == Printer.PR_NO_RESPONSE) {
				ptrhandler.obtainMessage(MESSAGE_BOX,"No response from Leopard device").sendToTarget();
			}else if (iRetVal== Printer.PR_DEMO_VERSION) {
				ptrhandler.obtainMessage(MESSAGE_BOX,"Library is in demo version").sendToTarget();
			}else if (iRetVal==Printer.PR_INVALID_DEVICE_ID) {
				ptrhandler.obtainMessage(MESSAGE_BOX,"Connected  device is not license authenticated.").sendToTarget();
			}else if (iRetVal==Printer.PR_ILLEGAL_LIBRARY) {
				ptrhandler.obtainMessage(MESSAGE_BOX,"Library not valid").sendToTarget();
			}
			super.onPostExecute(result);
		}
	}

	public void BarCodeDialog() {
	    dlgBarCode = new Dialog(context);
		dlgBarCode.setTitle("Enter Data to Print Barcode");
		dlgBarCode.setContentView(R.layout.dlg_barcode);
		edt_BarCode = (EditText) dlgBarCode.findViewById(R.id.barcode_edt);
		Spinner sp_BarCode = (Spinner) dlgBarCode.findViewById(R.id.barcode_sp);
		ArrayAdapter<String> arryBarCode = new ArrayAdapter<String>(this,android.R.layout.select_dialog_item, sBarCodeStyle);
		arryBarCode.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		sp_BarCode.setAdapter(arryBarCode);
		sp_BarCode.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View v,
					int position, long arg3) {
				// TODO Auto-generated method stub
				switch (position) {
				case 0:
					bacodepostion = 1;
					bBarcodestyle = (byte) (0X01);
					break;
				case 1:
					bacodepostion = 2;
					bBarcodestyle = (byte) (0X02);
					break;
				case 2:
					bacodepostion = 3;
					bBarcodestyle = (byte) (0X03);
					break;
				case 3:
					bacodepostion = 4;
					bBarcodestyle = (byte) (0X04);
					break;
				case 4:
					bacodepostion = 5;
					bBarcodestyle = (byte) (0X05);
					break;
				}
				InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				mgr.hideSoftInputFromWindow(v.getWindowToken(), 0);
				edt_BarCode.setText("");
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
			}
		});

		edt_BarCode.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {// TODO 
				switch (bacodepostion) {
				case 1:
					edt_BarCode.setInputType(InputType.TYPE_CLASS_NUMBER);
					edt_BarCode.setFilters(new InputFilter[] { new InputFilter.LengthFilter(25)});
					break;
				case 2:
					edt_BarCode.setInputType(InputType.TYPE_CLASS_NUMBER);
					edt_BarCode.setFilters(new InputFilter[] { new InputFilter.LengthFilter(12)});
					break;
				case 3:
					int itype = InputType.TYPE_CLASS_TEXT ;
					edt_BarCode.setInputType(itype);
					edt_BarCode.setFilters(new InputFilter[] { new InputFilter.LengthFilter(25), 
							new InputFilter.AllCaps() });
					break;
				case 4:
					edt_BarCode.setInputType(InputType.TYPE_CLASS_TEXT);
					edt_BarCode.setFilters(new InputFilter[] { new InputFilter.LengthFilter(12), 
							new InputFilter.AllCaps()});
					break;
				case 5:
					edt_BarCode.setInputType(InputType.TYPE_CLASS_NUMBER);
					edt_BarCode.setFilters(new InputFilter[] { new InputFilter.LengthFilter(12)});
					break;
				default:
					break;
				}

			}
		});

		Button btn_BarCode = (Button) dlgBarCode.findViewById(R.id.btn_BarCode);
		btn_BarCode.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				sBarCode = edt_BarCode.getText().toString();
				char[] ccc = sBarCode.toCharArray();
				if (sBarCode.length() == 0) {
					ptrhandler.obtainMessage(MESSAGE_BOX, "Enter text")
					.sendToTarget();
				} else if (sBarCode.length() > 0) {
					switch (bacodepostion) {
					case 3:
						BarCodeTask barcode = new BarCodeTask();
						barcode.execute(0);
						break;
					case 1:
						int i;
						for (i = 0; i < sBarCode.length(); i++) {
							if (!(ccc[i] >= '0' && ccc[i] <= '9')) {
								break;
							}
						}
						if (i != sBarCode.length()) {
							ptrhandler.obtainMessage(MESSAGE_BOX,
									"Please enter numeric characters")
									.sendToTarget();
						} else {
							BarCodeTask barcode1 = new BarCodeTask();
							barcode1.execute(0);
						}
						break;
					case 2:
						sBarCode = edt_BarCode.getText().toString();
						if (sBarCode.length() > 12) {
							ptrhandler.obtainMessage(MESSAGE_BOX,
									"Enter data less than 13 characters")
									.sendToTarget();
						} else {
							for (i = 0; i < sBarCode.length(); i++) {
								if (!(ccc[i] >= '0' && ccc[i] <= '9')) {
									break;
								}
							}
							if (i != sBarCode.length()) {
								ptrhandler.obtainMessage(MESSAGE_BOX,
										"Please enter numeric characters")
										.sendToTarget();
							} else {
								BarCodeTask barcode1 = new BarCodeTask();
								barcode1.execute(0);
							}
						}
						break;
					case 4:
						sBarCode = edt_BarCode.getText().toString();
						if (sBarCode.length() > 12) {

							ptrhandler.obtainMessage(MESSAGE_BOX,
									"Enter data less than 13 characters")
									.sendToTarget();
						} else {
							BarCodeTask barcode1 = new BarCodeTask();
							barcode1.execute(0);
						}
						break;
					case 5:
						sBarCode = edt_BarCode.getText().toString();
						if (sBarCode.length() > 13) {
							ptrhandler.obtainMessage(MESSAGE_BOX,
									"Enter data less than 13").sendToTarget();
						} else {
							// int i;
							for (i = 0; i < sBarCode.length(); i++) {
								if (!(ccc[i] >= '0' && ccc[i] <= '9')) {
									break;
								}
							}
							if (i != sBarCode.length()) {
								ptrhandler.obtainMessage(MESSAGE_BOX,
										"Please enter numerics only")
										.sendToTarget();
							} else {
								BarCodeTask barcode1 = new BarCodeTask();
								barcode1.execute(0);
							}
						}
						break;
					}
				}
			}
		});
		dlgBarCode.show();
	}
	
	/*   This method shows the BarCodePrint  AsynTask operation */
	public class BarCodeTask extends AsyncTask<Integer, Integer, Integer> {
		/* displays the progress dialog until background task is completed*/
		@Override
		protected void onPreExecute() {
			progressDialog(context, "Please Wait...");
			super.onPreExecute();
		}
		/* Task of BarCodePrint performing in the background*/	
		@Override
		protected Integer doInBackground(Integer... params) {
			try {
				ptr.iFlushBuf();
				sBarCode = edt_BarCode.getText().toString();
				iRetVal = ptr.iPrintBarcode(bBarcodestyle, sBarCode);
				if (iRetVal==Printer.PR_SUCCESS) {
					SystemClock.sleep(100);
					String empty = " \n" + " \n" + " \n" + " \n";
					ptr.iPrinterAddData((byte) 0x01, empty);
					ptr.iStartPrinting(1);
				}
			} catch (NullPointerException e) {
				iRetVal = DEVICE_NOTCONNECTED;
				return iRetVal;
			}
			return iRetVal;
		}

		/* This displays the status messages of BarCodePrint in the dialog box */
		@Override
		protected void onPostExecute(Integer result) {
			dlgPg.dismiss();
			if (iRetVal == DEVICE_NOTCONNECTED) {
				ptrhandler.obtainMessage(DEVICE_NOTCONNECTED,"Device not connected").sendToTarget();
			} else if (iRetVal == Printer.PR_SUCCESS) {
				dlgBarCode.dismiss();
				ptrhandler.obtainMessage(MESSAGE_BOX, "Print Success").sendToTarget();
			} else if (iRetVal == Printer.PR_PLATEN_OPEN) {
				ptrhandler.obtainMessage(MESSAGE_BOX,"Printer platen is open").sendToTarget();
			} else if (iRetVal == Printer.PR_PAPER_OUT) {
				ptrhandler.obtainMessage(MESSAGE_BOX,"Printer paper is out").sendToTarget();
			} else if (iRetVal == Printer.PR_IMPROPER_VOLTAGE) {
				ptrhandler.obtainMessage(MESSAGE_BOX,"Printer improper voltage").sendToTarget();
			} else if (iRetVal == Printer.PR_FAIL) {
				ptrhandler.obtainMessage(MESSAGE_BOX, "Printer failed").sendToTarget();
			} else if (iRetVal == Printer.PR_PARAM_ERROR) {
				ptrhandler.obtainMessage(MESSAGE_BOX,"Printer param error").sendToTarget();
			} else if (iRetVal == Printer.PR_NO_RESPONSE) {
				ptrhandler.obtainMessage(MESSAGE_BOX,"No response from Leopard device").sendToTarget();
			}else if (iRetVal== Printer.PR_DEMO_VERSION) {
				ptrhandler.obtainMessage(MESSAGE_BOX,"Library is in demo version").sendToTarget();
			}else if (iRetVal==Printer.PR_INVALID_DEVICE_ID) {
				ptrhandler.obtainMessage(MESSAGE_BOX,"Connected  device is not license authenticated.").sendToTarget();
			}else if (iRetVal==Printer.PR_ILLEGAL_LIBRARY) {
				ptrhandler.obtainMessage(MESSAGE_BOX,"Library not valid").sendToTarget();
			}else if (iRetVal==Printer.PR_CHARACTER_NOT_SUPPORTED) {
				ptrhandler.obtainMessage(MESSAGE_BOX,"character not supported").sendToTarget();
			}else if (iRetVal==Printer.PR_LIMIT_EXCEEDED) {
				ptrhandler.obtainMessage(MESSAGE_BOX,"Limit exceeded").sendToTarget();
			}
			super.onPostExecute(result);
		}
	}

	public void EnterText() {	//TODO
		final Dialog dlgEnterText = new Dialog(context);
		dlgEnterText.setContentView(R.layout.dlg_entertext);
		dlgEnterText.setTitle("Enter Text to Print");
		dlgEnterText.setCancelable(true);
		edt_Text = (EditText) dlgEnterText.findViewById(R.id.editText1);
		edt_Text.setText("Evolute Systems");
		Spinner sp_FontStyle = (Spinner) dlgEnterText.findViewById(R.id.font_sty);
		ArrayAdapter<String> arrFontStyle = new ArrayAdapter<String>(this,android.R.layout.select_dialog_item, sEnterTextFont);
		arrFontStyle.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		sp_FontStyle.setAdapter(arrFontStyle);
		sp_FontStyle.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				switch (position) {
				case  0: bFontstyle = Printer.PR_FONTLARGENORMAL;		break; //0x01;
				case  1: bFontstyle = Printer.PR_FONTLARGEBOLD;			break; //0x02;
				case  2: bFontstyle = Printer.PR_FONTSMALLNORMAL;		break; //0x03;
				case  3: bFontstyle = Printer.PR_FONTSMALLBOLD;			break; //0x04;
				case  4: bFontstyle = Printer.PR_FONTULLARGENORMAL;		break; //0x05;
				case  5: bFontstyle = Printer.PR_FONTULLARGEBOLD;		break; //0x06;
				case  6: bFontstyle = Printer.PR_FONTULSMALLNORMAL;		break; //0x07;
				case  7: bFontstyle = Printer.PR_FONTULSMALLBOLD;		break; //0x08;
				case  8: bFontstyle = Printer.PR_FONT180LARGENORMAL;	break; //0x09;
				case  9: bFontstyle = Printer.PR_FONT180LARGEBOLD;		break; //0x0A;
				case 10: bFontstyle = Printer.PR_FONT180SMALLNORMAL;	break; //0x0B;
				case 11: bFontstyle = Printer.PR_FONT180SMALLBOLD;		break; //0x0C;
				case 12: bFontstyle = Printer.PR_FONT180ULLARGENORMAL;	break; //0x0D;
				case 13: bFontstyle = Printer.PR_FONT180ULLARGEBOLD;	break; //0x0E;
				case 14: bFontstyle = Printer.PR_FONT180ULSMALLNORMAL;	break; //0x0F;
				case 15: bFontstyle = Printer.PR_FONT180ULSMALLBOLD;	break; //0x10;
				}
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
		
		Button dlgOk = (Button) dlgEnterText.findViewById(R.id.ok);
		dlgOk.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				sAddData = edt_Text.getText().toString();
				if (sAddData.length() == 0) {
					ptrhandler.obtainMessage(MESSAGE_BOX, "Enter Text").sendToTarget();
				} else if (sAddData.length() > 0) {
					dlgEnterText.dismiss();
					EnterTextTask asynctask = new EnterTextTask();
					asynctask.execute(0);
				}
			}
		});
		dlgEnterText.show();
	}

	/* This performs Progress dialog box to show the progress of operation */
	public static void progressDialog(Context context, String msg) {
		dlgPg = new ProgressDialog(context);
		dlgPg.setMessage(msg);
		dlgPg.setIndeterminate(true);
		dlgPg.setCancelable(false);
		dlgPg.show();
	}

	/*   This method shows the EnterTextAsyc  AsynTask operation */
	public class EnterTextTask extends AsyncTask<Integer, Integer, Integer> {
		/* displays the progress dialog until background task is completed*/
		@Override
		protected void onPreExecute() {
			progressDialog(context, "Please Wait...");
			super.onPreExecute();
		}
		/* Task of EnterTextAsyc performing in the background*/	
		@Override
		protected Integer doInBackground(Integer... params) {
			try {
				sAddData = edt_Text.getText().toString();
				ptr.iFlushBuf();
				String empty = sAddData ;//+ "\n" + "\n" + "\n" + "\n" + "\n"+ "\n";
				ptr.iPrinterAddData(bFontstyle, empty);
				ptr.iPrinterAddData(Printer.PR_FONTLARGENORMAL, " \n \n \n \n \n \n");
				ptr.iPrinterAddData(Printer.PR_FONT180LARGENORMAL, " \n \n \n \n \n \n");
				iRetVal = ptr.iStartPrinting(1);
			} catch (NullPointerException e) {
				iRetVal = DEVICE_NOTCONNECTED;
				return iRetVal;
			}
			return iRetVal;
		}
		/* This displays the status messages of EnterTextAsyc in the dialog box */
		@Override
		protected void onPostExecute(Integer result) {
			dlgPg.dismiss();
			if (iRetVal == DEVICE_NOTCONNECTED) {
				ptrhandler.obtainMessage(DEVICE_NOTCONNECTED,"Device not connected").sendToTarget();
			} else if (iRetVal == Printer.PR_SUCCESS) {
				ptrhandler.obtainMessage(MESSAGE_BOX, "Print Success").sendToTarget();
			} else if (iRetVal == Printer.PR_PLATEN_OPEN) {
				ptrhandler.obtainMessage(MESSAGE_BOX,"Printer platen is open").sendToTarget();
			} else if (iRetVal == Printer.PR_PAPER_OUT) {
				ptrhandler.obtainMessage(MESSAGE_BOX,"Printer paper is out").sendToTarget();
			} else if (iRetVal == Printer.PR_IMPROPER_VOLTAGE) {
				ptrhandler.obtainMessage(MESSAGE_BOX,"Printer improper voltage").sendToTarget();
			} else if (iRetVal == Printer.PR_FAIL) {
				ptrhandler.obtainMessage(MESSAGE_BOX, "Printer failed").sendToTarget();
			} else if (iRetVal == Printer.PR_PARAM_ERROR) {
				ptrhandler.obtainMessage(MESSAGE_BOX,"Printer param error").sendToTarget();
			} else if (iRetVal == Printer.PR_NO_RESPONSE) {
				ptrhandler.obtainMessage(MESSAGE_BOX,"No response from Leopard device").sendToTarget();
			} else if (iRetVal== Printer.PR_DEMO_VERSION) {
				ptrhandler.obtainMessage(MESSAGE_BOX,"Library is in demo version").sendToTarget();
			} else if (iRetVal==Printer.PR_INVALID_DEVICE_ID) {
				ptrhandler.obtainMessage(MESSAGE_BOX,"Connected  device is not license authenticated.").sendToTarget();
			} else if (iRetVal==Printer.PR_ILLEGAL_LIBRARY) {
				ptrhandler.obtainMessage(MESSAGE_BOX,"Library not valid").sendToTarget();
			}
			super.onPostExecute(result);
		}
	}

	/*   This method shows the PrintBitmap  AsynTask operation */
	public class BitmapTask extends AsyncTask<Integer, Integer, Integer> {
		/* displays the progress dialog until background task is completed*/
		@Override
		protected void onPreExecute() {
			progressDialog(context, "Please Wait ...");
			super.onPreExecute();
		}
		/* Task of PrintBitmap performing in the background*/		
		@Override
		protected Integer doInBackground(Integer... params) {
			try {
				if (sPicturePath==""){
					iRetVal = -555;
					return iRetVal;
				}
				ptr.iFlushBuf();
				
				if (!bGrayscale) {
					iRetVal =   ptr.iBmpPrint(sPicturePath); 
				}else{
					iRetVal =   ptr.iGreyscalePrint(sPicturePath); 
					bGrayscale=false;
				}
				//iRetVal =   ptr.iBmpPrint(sPicturePath); 
				if (iRetVal == Printer.PR_SUCCESS) {
					String empty = "\n";
					ptr.iPrinterAddData((byte) 0x01, empty);
					ptr.iStartPrinting(1);
				}
			} catch (NullPointerException e) {
				iRetVal = DEVICE_NOTCONNECTED;
				return iRetVal;
			}catch(Exception e){
				e.printStackTrace();
				Log.e("TAG",""+ e);
				iRetVal=-777;
				return iRetVal;
			}
			return iRetVal;
		}
		/* This displays the status messages of PrintBitmap in the dialog box */
		@Override
		protected void onPostExecute(Integer result) {
			Log.e("tag", "onPostExecute iretval..."+iRetVal);
			dlgPg.dismiss();
			if (iRetVal == DEVICE_NOTCONNECTED) {
				ptrhandler.obtainMessage(DEVICE_NOTCONNECTED,"Device not connected").sendToTarget();
			} else if (iRetVal == -777) {
				ptrhandler.obtainMessage(MESSAGE_BOX,"File Size is very large").sendToTarget();
			} else if (iRetVal == -555) {
				ptrhandler.obtainMessage(MESSAGE_BOX, "No Image is Selected").sendToTarget();
			} else if (iRetVal == Printer.PR_SUCCESS) {
				ptrhandler.obtainMessage(MESSAGE_BOX, "Print Success").sendToTarget();
			} else if (iRetVal == Printer.PR_PLATEN_OPEN) {
				ptrhandler.obtainMessage(MESSAGE_BOX,"Printer platen is open").sendToTarget();
			} else if (iRetVal == Printer.PR_PAPER_OUT) {
				ptrhandler.obtainMessage(MESSAGE_BOX,"Printer paper is out").sendToTarget();
			} else if (iRetVal == Printer.PR_IMPROPER_VOLTAGE) {
				ptrhandler.obtainMessage(MESSAGE_BOX,"Printer improper voltage").sendToTarget();
			} else if (iRetVal == Printer.PR_FAIL) {
				ptrhandler.obtainMessage(MESSAGE_BOX, "Printer failed").sendToTarget();
			} else if (iRetVal == Printer.PR_PARAM_ERROR) {
				ptrhandler.obtainMessage(MESSAGE_BOX,"Printer param error").sendToTarget();
			} else if (iRetVal == Printer.PR_NO_RESPONSE) {
				ptrhandler.obtainMessage(MESSAGE_BOX,"No response from Leopard device").sendToTarget();
			} else if (iRetVal == Printer.PR_DEMO_VERSION) {
				ptrhandler.obtainMessage(MESSAGE_BOX,"Library is in demo version").sendToTarget();
			} else if (iRetVal == Printer.PR_INVALID_DEVICE_ID) {
				ptrhandler.obtainMessage(MESSAGE_BOX,"Connected  device is not license authenticated.").sendToTarget();
			} else if (iRetVal == Printer.PR_ILLEGAL_LIBRARY) {
				ptrhandler.obtainMessage(MESSAGE_BOX,"Library not valid").sendToTarget();
			} else if (iRetVal == Printer.PR_BMP_FILE_NOT_FOUND) {
				ptrhandler.obtainMessage(MESSAGE_BOX,"File not found or File Size is large").sendToTarget();
			}
			super.onPostExecute(result);
		}
	}

	/*   This method shows the AddLine  AsynTask operation */
	public class AddLineAsyc extends AsyncTask<Integer, Integer, Integer> {
		/* displays the progress dialog until background task is completed*/
		@Override
		protected void onPreExecute() {
			progressDialog(context, "Please Wait ...");
			super.onPreExecute();
		}
		/* Task of AddLine  performing in the background*/	
		@Override
		protected Integer doInBackground(Integer... params) {
			try {
				ptr.iFlushBuf();
				String addlinestr = edt_AddLine.getText().toString();
				Character ch = addlinestr.charAt(0);
				ptr.iPrinterAddLine(bAddLineSyle, ch);
				String empty = "\n" + "\n" + "\n" + "\n";
				ptr.iPrinterAddData((byte) 0x01, empty);
				iRetVal =   ptr.iStartPrinting(1);
			} catch (NullPointerException e) {
				iRetVal = DEVICE_NOTCONNECTED;
				return iRetVal;
			}
			return iRetVal;
		}

		/* This displays the status messages of AddLine in the dialog box */
		@Override
		protected void onPostExecute(Integer result) {
			dlgPg.dismiss();
			if (iRetVal == DEVICE_NOTCONNECTED) {
				ptrhandler.obtainMessage(DEVICE_NOTCONNECTED,"Device not connected").sendToTarget();
			} else if (iRetVal == Printer.PR_SUCCESS) {
				ptrhandler.obtainMessage(MESSAGE_BOX, "Print Success").sendToTarget();
			} else if (iRetVal == Printer.PR_PLATEN_OPEN) {
				ptrhandler.obtainMessage(MESSAGE_BOX,"Printer platen is open").sendToTarget();
			} else if (iRetVal == Printer.PR_PAPER_OUT) {
				ptrhandler.obtainMessage(MESSAGE_BOX,"Printer paper is out").sendToTarget();
			} else if (iRetVal == Printer.PR_IMPROPER_VOLTAGE) {
				ptrhandler.obtainMessage(MESSAGE_BOX,"Printer improper voltage").sendToTarget();
			} else if (iRetVal == Printer.PR_FAIL) {
				ptrhandler.obtainMessage(MESSAGE_BOX, "Printer failed").sendToTarget();
			} else if (iRetVal == Printer.PR_PARAM_ERROR) {
				ptrhandler.obtainMessage(MESSAGE_BOX,"Printer param error").sendToTarget();
			}else if (iRetVal == Printer.PR_NO_RESPONSE) {
				ptrhandler.obtainMessage(MESSAGE_BOX,"No response from Leopard device").sendToTarget();
			}else if (iRetVal== Printer.PR_DEMO_VERSION) {
				ptrhandler.obtainMessage(MESSAGE_BOX,"Library is in demo version").sendToTarget();
			}else if (iRetVal==Printer.PR_INVALID_DEVICE_ID) {
				ptrhandler.obtainMessage(MESSAGE_BOX,"Connected  device is not license authenticated.").sendToTarget();
			}else if (iRetVal==Printer.PR_ILLEGAL_LIBRARY) {
				ptrhandler.obtainMessage(MESSAGE_BOX,"Library not valid").sendToTarget();
			}
			super.onPostExecute(result);
		}
	}

	/*   This method shows the PaperFeed  AsynTask operation */
	public class PaperFeedTask extends AsyncTask<Integer, Integer, Integer> {
		/* displays the progress dialog until background task is completed*/
		@Override
		protected void onPreExecute() {
			progressDialog(context, "Please Wait ...");
			super.onPreExecute();
		}
		/* Task of PaperFeed  performing in the background*/	
		@Override
		protected Integer doInBackground(Integer... params) {
			try {
				iRetVal =   ptr.iPaperFeed();
			} catch (NullPointerException e) {
				iRetVal = DEVICE_NOTCONNECTED;
				return iRetVal;
			}
			return iRetVal;
		}
		/* This displays the status messages of PaperFeed in the dialog box */
		@Override
		protected void onPostExecute(Integer result) {
			dlgPg.dismiss();
			if (iRetVal == DEVICE_NOTCONNECTED) {
				ptrhandler.obtainMessage(DEVICE_NOTCONNECTED,"Device not connected").sendToTarget();
			} else if (iRetVal == Printer.PR_SUCCESS) {
				ptrhandler.obtainMessage(MESSAGE_BOX, "Paperfeed is success").sendToTarget();
			} else if (iRetVal == Printer.PR_PLATEN_OPEN) {
				ptrhandler.obtainMessage(MESSAGE_BOX,"Printer platen is open").sendToTarget();
			} else if (iRetVal == Printer.PR_PAPER_OUT) {
				ptrhandler.obtainMessage(MESSAGE_BOX,"Printer paper is out").sendToTarget();
			} else if (iRetVal == Printer.PR_IMPROPER_VOLTAGE) {
				ptrhandler.obtainMessage(MESSAGE_BOX,"Printer improper voltage").sendToTarget();
			} else if (iRetVal == Printer.PR_FAIL) {
				ptrhandler.obtainMessage(MESSAGE_BOX, "Printer failed").sendToTarget();
			} else if (iRetVal == Printer.PR_PARAM_ERROR) {
				ptrhandler.obtainMessage(MESSAGE_BOX,"Printer param error").sendToTarget();
			}else if (iRetVal == Printer.PR_NO_RESPONSE) {
				ptrhandler.obtainMessage(MESSAGE_BOX,"No response from Leopard device").sendToTarget();
			}else if (iRetVal== Printer.PR_DEMO_VERSION) {
				ptrhandler.obtainMessage(MESSAGE_BOX,"Library is in demo version").sendToTarget();
			}else if (iRetVal==Printer.PR_INVALID_DEVICE_ID) {
				ptrhandler.obtainMessage(MESSAGE_BOX,"Connected  device is not license authenticated.").sendToTarget();
			}else if (iRetVal==Printer.PR_ILLEGAL_LIBRARY) {
				ptrhandler.obtainMessage(MESSAGE_BOX,"Library not valid").sendToTarget();
			}
			super.onPostExecute(result);
		}
	}

	/*   This method shows the Diagnose AsynTask operation */
	public class DiagnousTask extends AsyncTask<Integer, Integer, Integer> {
		/* displays the progress dialog until background task is completed*/
		@Override
		protected void onPreExecute() {
			progressDialog(context, "Please Wait ...");
			super.onPreExecute();
		}

		/* Task of Diagnose performing in the background*/
		@Override
		protected Integer doInBackground(Integer... params) {
			try {
				iRetVal =   ptr.iPrinterDiagnostics();
			} catch (NullPointerException e) {
				iRetVal = DEVICE_NOTCONNECTED;
				return iRetVal;
			}
			return iRetVal;
		}
		/* This sends message to handler to display the status messages 
		 * of Diagnose in the dialog box */
		@Override
		protected void onPostExecute(Integer result) {
			dlgPg.dismiss();
			if (iRetVal == DEVICE_NOTCONNECTED) {
				ptrhandler.obtainMessage(DEVICE_NOTCONNECTED,"Device not connected").sendToTarget();
			} else if (iRetVal == Printer.PR_SUCCESS) {
				ptrhandler.obtainMessage(MESSAGE_BOX,"Printer is in good condition").sendToTarget();
			} else if (iRetVal == Printer.PR_PLATEN_OPEN) {
				ptrhandler.obtainMessage(MESSAGE_BOX,"Printer platen is open").sendToTarget();
			} else if (iRetVal == Printer.PR_PAPER_OUT) {
				ptrhandler.obtainMessage(MESSAGE_BOX,"Printer paper is out").sendToTarget();
			} else if (iRetVal == Printer.PR_IMPROPER_VOLTAGE) {
				ptrhandler.obtainMessage(MESSAGE_BOX,"Printer improper voltage").sendToTarget();
			} else if (iRetVal == Printer.PR_FAIL) {
				ptrhandler.obtainMessage(MESSAGE_BOX, "Printer failed").sendToTarget();
			} else if (iRetVal == Printer.PR_PARAM_ERROR) {
				ptrhandler.obtainMessage(MESSAGE_BOX,"Printer param error").sendToTarget();
			}else if (iRetVal == Printer.PR_NO_RESPONSE) {
				ptrhandler.obtainMessage(MESSAGE_BOX,"No response from Leopard device").sendToTarget();
			}else if (iRetVal== Printer.PR_DEMO_VERSION) {
				ptrhandler.obtainMessage(MESSAGE_BOX,"Library is in demo version").sendToTarget();
			}else if (iRetVal==Printer.PR_INVALID_DEVICE_ID) {
				ptrhandler.obtainMessage(MESSAGE_BOX,"Connected  device is not license authenticated.").sendToTarget();
			}else if (iRetVal==Printer.PR_ILLEGAL_LIBRARY) {
				ptrhandler.obtainMessage(MESSAGE_BOX,"Library not valid").sendToTarget();
			}
			super.onPostExecute(result);
		}
	}
	//Button btn_Print;
	Dialog dlgImg;
	//LinearLayout llImage;
	public void ShowImagDialog()
	{
		sPicturePath="";
		dlgImg = new Dialog(context);
		dlgImg.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
		dlgImg.setTitle("Select BMP File from storage");
		dlgImg.setContentView(R.layout.dlg_bitmap);
		final Button btn_Print = (Button)dlgImg.findViewById(R.id.Print_but);
		img_Galery = (ImageView)dlgImg.findViewById(R.id.galery_img);
		LinearLayout llImage =(LinearLayout)dlgImg.findViewById(R.id.imaglay);
		final Button selectimage_but = (Button)dlgImg.findViewById(R.id.selectimage_but);
		selectimage_but.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				selectimage_but.setEnabled(true);
				Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				startActivityForResult(i, RESULT_LOAD_IMAGE);
				btn_Print.setEnabled(true);
				TextView selectpath_tv = (TextView)dlgImg.findViewById(R.id.selectpath_tv);
				selectpath_tv.setText(sPicturePath);
			}
		});
		btn_Print.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dlgImg.dismiss();
				BitmapTask bmp = new BitmapTask();
				bmp.execute(0);
			}
		});
		dlgImg.show();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == RESULT_LOAD_IMAGE){// && resultCode == RESULT_OK && null != data) {
			try {
				Uri selectedImage = data.getData();
				String[] filePathColumn = { MediaStore.Images.Media.DATA };
				Cursor cursor = getContentResolver().query(selectedImage,filePathColumn, null, null, null);
				cursor.moveToFirst();
				int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
				sPicturePath = cursor.getString(columnIndex);
				cursor.close();
				img_Galery.setImageBitmap(BitmapFactory.decodeFile(sPicturePath));
			} catch (Exception e) {
				e.printStackTrace();
				sPicturePath = "";
				Toast.makeText(Act_Printer.this, "No Image Selected", Toast.LENGTH_SHORT).show();
			}         
		}
	}
	
	public void Unicode()
	{
		final Dialog dlgUnicodeText = new Dialog(context);
		dlgUnicodeText.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dlgUnicodeText.setContentView(R.layout.act_unicode);
		TextView tvTitel = (TextView)dlgUnicodeText.findViewById(R.id.tv_unicodecode);
		cust_btn=(Button)dlgUnicodeText.findViewById(R.id.custom_btn);
		cust_edt=(EditText)dlgUnicodeText.findViewById(R.id.custom_edt);
		linrlayout=(LinearLayout) dlgUnicodeText.findViewById(R.id.linearLayout123);
		linrtxtvw=(LinearLayout) dlgUnicodeText.findViewById(R.id.linearLayout456);
		tvTitel.setWidth(500);
		tv_selectLang = (TextView)dlgUnicodeText.findViewById(R.id.tv_selectLang);
		Spinner sp_SelectLanguage = (Spinner)dlgUnicodeText.findViewById(R.id.sp_SelectLanguage);
		List<String> lang = new ArrayList<String>();
		lang.add("Hindi Text");
		lang.add("Kannada Text");
		lang.add("Telugu Text");
		lang.add("Tamil Text");
		lang.add("Edit Text");
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item,lang);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		sp_SelectLanguage.setAdapter(dataAdapter);
		sp_SelectLanguage.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
					if(position==0){
					Log.e("", "hindiii");
					linrlayout.setVisibility(View.INVISIBLE);
					linrtxtvw.setVisibility(View.VISIBLE);
					tv_selectLang.setText("एवोलुते सिस्टम प्राइवेट लिमिटेड");
					Log.e("Str value","<<<unicode1>>>>"+str);
					str = "एवोलुते सिस्टम प्राइवेट लिमिटेड "+"\n"+"\n"+"\n"+"\n"+"\n"+"\n"+"\n"+"\n";
					Log.e("Str value","<<<unicode2>>>>"+str);
					beditprint=false;
				}else if (position==1) {
					Log.e("", "kanada>>>>>>");
					linrlayout.setVisibility(View.INVISIBLE);
					linrtxtvw.setVisibility(View.VISIBLE);
					tv_selectLang.setText("ಎವೊಲ್ಯುತ್ ಸಿಸ್ಟಮ್ಸ್ ಪ್ರೈವೇಟ್ ಲಿಮಿಟೆಡ್ ");
					Log.e("Str value","<<<unicode1>>>>"+str);
					str = "ಎವೊಲುತೆ ಸಿಸ್ಟಮ್ ಪ್ರೈವೇಟ್ ಲಿಮಿಟೆಡ್ "+"\n"+"\n"+"\n"+"\n"+"\n"+"\n"+"\n"+"\n";	
					Log.e("Str value","<<<unicode2>>>>"+str);
					beditprint=false;
				}else if (position==2) {
					Log.e("", "telugu");
					linrlayout.setVisibility(View.INVISIBLE);
					linrtxtvw.setVisibility(View.VISIBLE);
					tv_selectLang.setText("ఎవోలుటే సిస్టం ప్రైవేటు లిమిటెడ్");
					str = "ఎవోలుటే సిస్టం ప్రైవేటు లిమిటెడ్"+"\n"+"\n"+"\n"+"\n"+"\n"+"\n"+"\n"+"\n";
					beditprint=false;
				}else if (position==3) {
					linrlayout.setVisibility(View.INVISIBLE);
					linrtxtvw.setVisibility(View.VISIBLE);
					tv_selectLang.setText("எவோளுடே சிஸ்டம்ஸ் பவத் ல்த்து");
					str = "எவோளுடே சிஸ்டம்ஸ் பவத் ல்த்து"+"\n"+"\n"+"\n"+"\n"+"\n"+"\n";
					beditprint=false;
				}else if (position==4) {
					str="";
					tv_selectLang.setText("");
					linrlayout.setVisibility(View.VISIBLE);
					linrtxtvw.setVisibility(View.INVISIBLE);
					Log.e("custom cassee", "spinner9 4");
					beditprint=true;
					custom();
				}
			}
		
			private void custom() {
				final Dialog dlgUnicodeText = new Dialog(context);
				dlgUnicodeText.requestWindowFeature(Window.FEATURE_NO_TITLE);
				Log.e("Leopard", "in try");
			}
			
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}});
		
	Spinner	sp_SelectPosition = (Spinner)dlgUnicodeText.findViewById(R.id.sp_SelectPosition);
		List<String> list = new ArrayList<String>();
		list.add("Left");
		list.add("Center");
		list.add("Right");
		ArrayAdapter<String> dataAdapter1 = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item,list);
		dataAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		sp_SelectPosition.setAdapter(dataAdapter1);
		sp_SelectPosition.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,int position, long arg3) {
				if(position==0){
					justfypostion = Justify.ALIGN_LEFT;
				}else if (position==1) {
					justfypostion = Justify.ALIGN_CENTER;	
				}else if (position==2) {
					justfypostion = Justify.ALIGN_RIGHT;	
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
		Log.e("legend", "in try>>>> 0");
		cust_edt.getText().toString();
		Log.e("legend", "in try>>>> 1");
		tv_selectLang.setText(cust_edt.getText().toString());
		Log.e("legend", "in try>>> 2");
		 btnconfirm = (Button)dlgUnicodeText. findViewById(R.id.custom_btn);
		Log.e("legend", "in try>>> 3");
		cust_btn.setOnClickListener(new View.OnClickListener() {
		        @Override
		        public void onClick(View v) {
		        	Log.e("legend", "in try>>> 4");
		        bConfirm=true;
		        	if(cust_edt.length()==0 || cust_edt.equals("")  || cust_edt==null  )
		            {
		        		cust_edt.setError("Input Text is required");
		            }
		        	linrtxtvw.setVisibility(View.VISIBLE);
		        	tv_selectLang.setText(cust_edt.getText().toString());
		        }
		    });
		
		Button btn_unicode = (Button)dlgUnicodeText. findViewById(R.id.btn_unicodeprnttt);
		btn_unicode.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
					//dlgUnicodeText.dismiss();
					String ss=tv_selectLang.getText().toString();
					String sedit=cust_edt.getText().toString();
					if(ss.length()==0 && sedit.length()==0 ){
						Toast.makeText(context, "No Input's selected", Toast.LENGTH_LONG).show();
					}else if (!bConfirm && beditprint) {
						Toast.makeText(context,  "Confirm Data once & print", Toast.LENGTH_LONG).show();
					}else if(sedit.length()==0 && ss.length()==0  ){
						Log.e("legend", "counter");
						Toast.makeText(context, "Confirm Data once & print", Toast.LENGTH_LONG).show();
					if (counter>0) {
						Log.e("legend", "counter");
						UnicodeASync uniasynctask = new UnicodeASync();
						uniasynctask.execute(0);
					}
					dlgUnicodeText.show();
					}
					else{
						Log.e("legend", "else");
					UnicodeASync uniasynctask = new UnicodeASync();
					uniasynctask.execute(0);
					}
				}
		});
		dlgUnicodeText.show();
}
	   //This method shows the EnterTextAsyc  AsynTask operation 
	public class UnicodeASync extends AsyncTask<Integer, Integer, Integer> {
			int xx=0;
		// displays the progress dialog until background task is completed
		@Override
		protected void onPreExecute() {
		//	progressDialog(context, "Please Wait...");
			progressDialog(context,"Please wait..");
			super.onPreExecute();
		}
		 //Task of EnterTextAsyc performing in the background	
		@Override
		protected Integer doInBackground(Integer... params) {
			try {
				String newString = cust_edt.getText().toString();
            	if(newString.length()==0){
            		Log.e("", "inside if>>>>>");
            		newString= tv_selectLang.getText().toString();
            	}else{
            		Log.e("", "else block"+newString);
            	}
            	Bitmap bmpImg1 = TextGenerator.bmpDrawText(ImageWidth.Inch_2, newString, 30,justfypostion);
    			Log.e("Str value","<<<unicode 3>>>>"+str);
    			Log.e("Str value","<<<unicode 3>>>>"+justfypostion);
				Bitmap bmpfinal = TextGenerator.bmpConvertTo_24Bit(bmpImg1);
				Log.d("bmp","Result : abt to prn ");
				byte[] bBmpFileData = TextGenerator.bGetBmpFileData(bmpfinal);
				ByteArrayInputStream bis = new ByteArrayInputStream(
						bBmpFileData);
				 xx =ptr.iBmpPrint(bis);
				Log.e("", "xx value>>>>>>>>>"+xx);
				Log.d("bmp","Result : "+xx);
				String sdcardBmpPath = Environment
						.getExternalStorageDirectory().getAbsolutePath()
						+ "/myImage.bmp";	
			} catch (NullPointerException e) {
				iRetVal = DEVICE_NOTCONNECTED;
				return iRetVal;
			}
			return iRetVal;
		}
		// This displays the status messages of EnterTextAsyc in the dialog box 
		@Override
		protected void onPostExecute(Integer result) {
			cust_edt.setText("");
			bConfirm=false;
			Log.e("DATA ","+++++TIMER STOPED+++++++");
			dlgPg.dismiss();
			if (xx == DEVICE_NOTCONNECTED) {
				ptrhandler.obtainMessage(1,"Device not connected").sendToTarget();
			} else if (xx == Printer.PR_SUCCESS) {
				ptrhandler.obtainMessage(1, "Print Success").sendToTarget();
			} else if (xx == Printer.PR_PLATEN_OPEN) {
				ptrhandler.obtainMessage(1,"Printer platen is open").sendToTarget();
			} else if (xx == Printer.PR_PAPER_OUT) {
				ptrhandler.obtainMessage(1,"Printer paper is out").sendToTarget();
			} else if (xx == Printer.PR_IMPROPER_VOLTAGE) {
				ptrhandler.obtainMessage(1,"Printer improper voltage").sendToTarget();
			} else if (xx == Printer.PR_PARAM_ERROR) {
				ptrhandler.obtainMessage(1,"Printer param error").sendToTarget();
			}else if (xx == Printer.PR_NO_RESPONSE) {
				ptrhandler.obtainMessage(1,"No response from device").sendToTarget();
			}else if (xx== Printer.PR_DEMO_VERSION) {
				ptrhandler.obtainMessage(1,"Library is in demo version").sendToTarget();
			}
			super.onPostExecute(result);
		}
			}
}
