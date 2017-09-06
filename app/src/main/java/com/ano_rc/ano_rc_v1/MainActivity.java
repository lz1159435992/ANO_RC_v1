package com.ano_rc.ano_rc_v1;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends Activity {

	private int intCounter = 0;
	private ImageView mImageView01;//LOGO1
	
	public static int VAL_ACC_X = 0;
	public static int VAL_ACC_Y = 0;
	public static int VAL_ACC_Z = 0;
	public static int VAL_GYR_X = 0;
	public static int VAL_GYR_Y = 0;
	public static int VAL_GYR_Z = 0;
	public static float VAL_ANG_X = 0;
	public static float VAL_ANG_Y = 0;
	public static float VAL_ANG_Z = 0;
	public static int VAL_PID_ROL_P=0,VAL_PID_ROL_I=0,VAL_PID_ROL_D=0,VAL_PID_PIT_P=0,VAL_PID_PIT_I=0,VAL_PID_PIT_D=0,
			VAL_PID_YAW_P=0,VAL_PID_YAW_I=0,VAL_PID_YAW_D=0;
	public static int VAL_PID_PID1_P=0,VAL_PID_PID1_I=0,VAL_PID_PID1_D=0,VAL_PID_PID2_P=0,VAL_PID_PID2_I=0,VAL_PID_PID2_D=0,
			VAL_PID_PID3_P=0,VAL_PID_PID3_I=0,VAL_PID_PID3_D=0;
	public static int VAL_VOTAGE1 = 0;
	
	// Message types sent from the BluetoothRfcommClient Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;
    
    // Key names received from the BluetoothRfcommClient Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";
	
	// Intent request codes
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;
	
    // Name of the connected device
    private String mConnectedDeviceName = null;
    // Local Bluetooth adapter
    private BluetoothAdapter mBluetoothAdapter = null;
    // Member object for the RFCOMM services
    private static BluetoothRfcommClient mRfcommClient = null;
    
	// Menu
 	private MenuItem mItemConnect;
 	private MenuItem mItemOptions;
 	private MenuItem mItemAbout;
	 	
 	private TextView mTxtStatus;//用于显示蓝牙连接状态
 	
 	
    @SuppressWarnings("deprecation")
	@Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        //*****************************************************
        //蓝牙
        //*****************************************************
        mTxtStatus = (TextView) findViewById(R.id.txt_status);
        mTxtStatus.setAlpha(0);
        // Get local Bluetooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        
        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        
        // If BT is not on, request that it be enabled.
    	if (!mBluetoothAdapter.isEnabled()){
    		Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
    		startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
    	}
    	
    	// Initialize the BluetoothRfcommClient to perform bluetooth connections
        mRfcommClient = new BluetoothRfcommClient(this, mHandler);
        
        Button mbutton = (Button) findViewById(R.id.btn_val);
        mbutton.setAlpha(0);
        mbutton = (Button) findViewById(R.id.btn_col);
        mbutton.setAlpha(0);
        
        mbutton = (Button)findViewById(R.id.btn_val);
		mbutton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent serverIntent = new Intent();
				serverIntent.setClass(MainActivity.this,ActivityBTDeviceList.class);
	        	startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
			}
		});
		
		mbutton = (Button)findViewById(R.id.btn_col);
		mbutton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent=new Intent();
				intent.setClass(MainActivity.this,ActivityControl.class);
				startActivity(intent);
			}
		});
		

        
		mImageView01 = (ImageView) findViewById(R.id.imageView1);
        mImageView01.setAlpha(0);
        mImageView01 = (ImageView) findViewById(R.id.imageView2);
        mImageView01.setAlpha(0);
        mHandler.post(fadeInTask);
    }

    /* 设定ImageView的透明度渐显出来 */
	private Runnable fadeInTask = new Runnable() {
		@SuppressWarnings("deprecation")
		public void run() {
			intCounter = intCounter + 1;
			mImageView01 = (ImageView) findViewById(R.id.imageView1);
			mImageView01.setAlpha(intCounter * 10);
			if(intCounter == 25) 
			{
		        mImageView01 = (ImageView) findViewById(R.id.imageView2);
		        mImageView01.setAlpha(255);
				Button mbutton = (Button) findViewById(R.id.btn_val);
		        mbutton.setAlpha(1);
		        mbutton = (Button) findViewById(R.id.btn_col);
		        mbutton.setAlpha(1);
		        mTxtStatus.setAlpha(1);
			}
			else mHandler.postDelayed(fadeInTask, 120);
		}
	};
	//创建菜单项
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	mItemConnect = menu.add("连接");
    	//mItemOptions = menu.add("设置");
    	mItemAbout = menu.add("关于");
    	return (super.onCreateOptionsMenu(menu));
    	
    }
    //菜单项动作处理
	@SuppressWarnings("deprecation")
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	if ( item == mItemConnect ) 
    	{
    		Intent serverIntent = new Intent(this, ActivityBTDeviceList.class);
        	startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
    	} 
    	else if ( item == mItemOptions ) 
    	{
    		
    	} 
    	else if ( item == mItemAbout ) 
    	{
    		AlertDialog about = new AlertDialog.Builder(this).create();
    		about.setCancelable(false);
    		about.setMessage("欢迎使用匿名科创安卓控制程序v0.1\n匿名官网:www.anotc.com");
    		about.setButton("OK", new DialogInterface.OnClickListener() 
    		{
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
    		about.show();
    	}
    	return super.onOptionsItemSelected(item);
    }
	
	@Override
    public synchronized void onResume() {
    	super.onResume();
    	if (mRfcommClient != null) {
            // Only if the state is STATE_NONE, do we know that we haven't started already
            if (mRfcommClient.getState() == BluetoothRfcommClient.STATE_NONE) {
              // Start the Bluetooth  RFCOMM services
              mRfcommClient.start();
            }
        }    	
    }
    
    @Override
    public void onDestroy() {
    	// Stop the Bluetooth RFCOMM services
        if (mRfcommClient != null) mRfcommClient.stop();
        super.onDestroy();
    }
    //退出提醒
    @Override
    public void onBackPressed() {
    	new AlertDialog.Builder(this)
    	.setTitle("匿名科创")
    	.setMessage("关闭程序?")
    	.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				finish();				
			}
		})
		.setNegativeButton("No", null)
		.show();
    }
    
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    	switch (requestCode){
    	case REQUEST_CONNECT_DEVICE:
    		// When DeviceListActivity returns with a device to connect
    		if (resultCode == Activity.RESULT_OK) {
    			// Get the device MAC address
    			String address = data.getExtras().getString(ActivityBTDeviceList.EXTRA_DEVICE_ADDRESS);
    			// Get the BLuetoothDevice object
                BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
                // Attempt to connect to the device
                mRfcommClient.connect(device);
    		}
    		break;
    	case REQUEST_ENABLE_BT:
    		// When the request to enable Bluetooth returns
    		if (resultCode != Activity.RESULT_OK) {
            	// User did not enable Bluetooth or an error occurred
                Toast.makeText(this, R.string.bt_not_enabled_leaving, Toast.LENGTH_SHORT).show();
                finish();
            }
    		break;
    	}
    }
	// The Handler that gets information back from the BluetoothRfcommClient
    @SuppressLint("HandlerLeak")
	private final Handler mHandler = new Handler() {
		@Override
        public void handleMessage(Message msg) {
    		switch (msg.what) {
    		case MESSAGE_STATE_CHANGE:
    			switch (msg.arg1) {
    			case BluetoothRfcommClient.STATE_CONNECTED:
    				mTxtStatus.setText(R.string.title_connected_to);
    				mTxtStatus.append(" " + mConnectedDeviceName);
    				break;
    			case BluetoothRfcommClient.STATE_CONNECTING:
    				mTxtStatus.setText(R.string.title_connecting);
    				break;
    			case BluetoothRfcommClient.STATE_NONE:
    				mTxtStatus.setText(R.string.title_not_connected);
    				break;
    			}
    			break;
    		case MESSAGE_READ:
    			 byte[] readBuf = (byte[]) msg.obj;
    			 DataAnl(readBuf,msg.arg1);
    			break;
    		case MESSAGE_DEVICE_NAME:
    			// save the connected device's name
                mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
                Toast.makeText(getApplicationContext(), "Connected to "
                        + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
    			break;
    		case MESSAGE_TOAST:
    			Toast.makeText(getApplicationContext(), msg.getData().getString(TOAST),
                        Toast.LENGTH_SHORT).show();
    			break;
    		}
    	}
    };
    static void SendData(String message)
    {
    	// Check that we're actually connected before trying anything
    	if (mRfcommClient.getState() != BluetoothRfcommClient.STATE_CONNECTED) {
    		// Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT).show();
    		return;
    	}
    	// Check that there's actually something to send
    	if (message.length() > 0) {
    		// Get the message bytes and tell the BluetoothRfcommClient to write
    		byte[] send = message.getBytes();
    		mRfcommClient.write(send);
    	}
    }
    static void SendData_Byte(byte[] data)
    {
    	// Check that we're actually connected before trying anything
    	if (mRfcommClient.getState() != BluetoothRfcommClient.STATE_CONNECTED) {
    		// Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT).show();
    		return;
    	}
    	mRfcommClient.write(data);
    }
    static void Send_Command(byte data)
    {
    	byte[] bytes = new byte[6];
    	byte sum=0;
    	// Check that we're actually connected before trying anything
    	if (mRfcommClient.getState() != BluetoothRfcommClient.STATE_CONNECTED) {
    		// Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT).show();
    		return;
    	}
    	bytes[0] = (byte) 0xaa;
		bytes[1] = (byte) 0xaf;
		bytes[2] = (byte) 0x01;
		bytes[3] = (byte) 0x01;
		bytes[4] = data;
		for(int i=0;i<5;i++) sum += bytes[i];
		bytes[5] = sum;
		SendData_Byte(bytes);
    }
    static void Send_Command02(byte data)
    {
    	byte[] bytes = new byte[6];
    	byte sum=0;
    	// Check that we're actually connected before trying anything
    	if (mRfcommClient.getState() != BluetoothRfcommClient.STATE_CONNECTED) {
    		// Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT).show();
    		return;
    	}
    	bytes[0] = (byte) 0xaa;
		bytes[1] = (byte) 0xaf;
		bytes[2] = (byte) 0x02;
		bytes[3] = (byte) 0x01;
		bytes[4] = data;
		for(int i=0;i<5;i++) sum += bytes[i];
		bytes[5] = sum;
		SendData_Byte(bytes);
    }
    static void Send_PID1()
    {
    	byte[] bytes = new byte[23];
    	byte sum=0;
    	// Check that we're actually connected before trying anything
    	if (mRfcommClient.getState() != BluetoothRfcommClient.STATE_CONNECTED) {
    		// Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT).show();
    		return;
    	}
    	bytes[0] = (byte) 0xaa;
		bytes[1] = (byte) 0xaf;
		bytes[2] = (byte) 0x10;
		bytes[3] = (byte) 18;
		bytes[4] = (byte) (VAL_PID_ROL_P/256);
		bytes[5] = (byte) (VAL_PID_ROL_P%256);
		bytes[6] = (byte) (VAL_PID_ROL_I/256);
		bytes[7] = (byte) (VAL_PID_ROL_I%256);
		bytes[8] = (byte) (VAL_PID_ROL_D/256);
		bytes[9] = (byte) (VAL_PID_ROL_D%256);
		
		bytes[10] = (byte) (VAL_PID_PIT_P/256);
		bytes[11] = (byte) (VAL_PID_PIT_P%256);
		bytes[12] = (byte) (VAL_PID_PIT_I/256);
		bytes[13] = (byte) (VAL_PID_PIT_I%256);
		bytes[14] = (byte) (VAL_PID_PIT_D/256);
		bytes[15] = (byte) (VAL_PID_PIT_D%256);
		
		bytes[16] = (byte) (VAL_PID_YAW_P/256);
		bytes[17] = (byte) (VAL_PID_YAW_P%256);
		bytes[18] = (byte) (VAL_PID_YAW_I/256);
		bytes[19] = (byte) (VAL_PID_YAW_I%256);
		bytes[20] = (byte) (VAL_PID_YAW_D/256);
		bytes[21] = (byte) (VAL_PID_YAW_D%256);
		
		for(int i=0;i<22;i++) sum += bytes[i];
		bytes[22] = sum;
		
		SendData_Byte(bytes);
    }
    static void Send_PID2()
    {
    	byte[] bytes = new byte[23];
    	byte sum=0;
    	// Check that we're actually connected before trying anything
    	if (mRfcommClient.getState() != BluetoothRfcommClient.STATE_CONNECTED) {
    		// Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT).show();
    		return;
    	}
    	bytes[0] = (byte) 0xaa;
		bytes[1] = (byte) 0xaf;
		bytes[2] = (byte) 0x11;
		bytes[3] = (byte) 18;
		bytes[4] = (byte) (VAL_PID_PID1_P/256);
		bytes[5] = (byte) (VAL_PID_PID1_P%256);
		bytes[6] = (byte) (VAL_PID_PID1_I/256);
		bytes[7] = (byte) (VAL_PID_PID1_I%256);
		bytes[8] = (byte) (VAL_PID_PID1_D/256);
		bytes[9] = (byte) (VAL_PID_PID1_D%256);
		
		bytes[10] = (byte) (VAL_PID_PID2_P/256);
		bytes[11] = (byte) (VAL_PID_PID2_P%256);
		bytes[12] = (byte) (VAL_PID_PID2_I/256);
		bytes[13] = (byte) (VAL_PID_PID2_I%256);
		bytes[14] = (byte) (VAL_PID_PID2_D/256);
		bytes[15] = (byte) (VAL_PID_PID2_D%256);
		
		bytes[16] = (byte) (VAL_PID_PID3_P/256);
		bytes[17] = (byte) (VAL_PID_PID3_P%256);
		bytes[18] = (byte) (VAL_PID_PID3_I/256);
		bytes[19] = (byte) (VAL_PID_PID3_I%256);
		bytes[20] = (byte) (VAL_PID_PID3_D/256);
		bytes[21] = (byte) (VAL_PID_PID3_D%256);
		
		for(int i=0;i<22;i++) sum += bytes[i];
		bytes[22] = sum;
		
		SendData_Byte(bytes);
    }
    static int COM_BUF_LEN = 1000;
    static byte[] RX_Data = new byte[COM_BUF_LEN];	//接收到的数据，AA开头
    static int rxstate = 0;
    static int rxlen = 0;//该帧已经接收到的长度
    static int rxcnt = 0;//该写入哪字节
    static void DataAnl(byte[] data, int len)
    {
    	for(int i=0;i<len;i++)
    	{
    		if(rxstate==0)//寻找开头AA
    		{
    			if(data[i]==(byte)0xaa)
    			{
    				rxstate = 1;
    				RX_Data[0] = (byte) 0xaa;
    			}
    		}
    		else if(rxstate==1)//寻找第二个AA
    		{
    			if(data[i]==(byte)0xaa)
    			{
    				rxstate = 2;
    				RX_Data[1] = (byte) 0xaa;
    			}
    			else
    				rxstate = 0;
    		}
    		else if(rxstate==2)//接收功能字
    		{
    			rxstate = 3;
    			RX_Data[2] = data[i];
    		}
    		else if(rxstate==3)//接收len
    		{
    			if(data[i]>45)
    				rxstate = 0;
    			else
    			{
    				rxstate = 4;
    				RX_Data[3] = data[i];
    				rxlen = RX_Data[3];
    				if(rxlen<0)
    					rxlen = -rxlen;
    				rxcnt = 4;
    			}
    		}
    		else if(rxstate==4)
    		{
    			rxlen--;
    			RX_Data[rxcnt] = data[i];
    			rxcnt++;
    			if(rxlen<=0)
    				rxstate = 5;
    		}
    		else if(rxstate==5)//接收sum
    		{
    			RX_Data[rxcnt] = data[i];
    			if(rxcnt<=(COM_BUF_LEN-1))
    				FrameAnl(rxcnt+1);
    			//Toast.makeText(getApplicationContext(), "DataAnl OK", Toast.LENGTH_SHORT).show();
    			rxstate = 0;
    		}
    	}
    }
    static void FrameAnl(int len)
    {
    	byte sum = 0;
    	for(int i=0;i<(len-1);i++)
    		sum += RX_Data[i];
    	if(sum==RX_Data[len-1])
    	{
    		//Toast.makeText(getApplicationContext(), "FrameAnl OK", Toast.LENGTH_SHORT).show();
    		if(RX_Data[2]==1)//status
    		{
    			VAL_ANG_X = ((float)(BytetoUint(4)))/100;
    			VAL_ANG_Y = ((float)(BytetoUint(6)))/100;
    			VAL_ANG_Z = ((float)(BytetoUint(8)))/100;
    		}
    		if(RX_Data[2]==2)//senser
    		{
    			VAL_ACC_X = BytetoUint(4);
    			VAL_ACC_Y = BytetoUint(6);
    			VAL_ACC_Z = BytetoUint(8);
    			VAL_GYR_X = BytetoUint(10);
    			VAL_GYR_Y = BytetoUint(12);
    			VAL_GYR_Z = BytetoUint(14);
    		}
    		if(RX_Data[2]==5)//votage
    		{
    			VAL_VOTAGE1 = BytetoUint(4);
    		}
    	}
    }
    static short BytetoUint(int cnt)
    {
		short r = 0;  
		r <<= 8;  
		r |= (RX_Data[cnt] & 0x00ff);  
		r <<= 8;  
		r |= (RX_Data[cnt+1] & 0x00ff);  
		return r;	
    }
}
