package project.xplat.launcher;



import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.MulticastLock;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import java.util.*;
import java.io.*;
import android.view.*;



public class MainActivity extends Activity {
	/** Called when the activity is first created. */
	public static Context context;
	private Intent intent;
	public static String gdxFlag="gdx";
	public static String sdlFlag="sdl";
	public static String webFlag="web";
	public static String shutdownFlag="shutdown";
	public static String rebootFlag="reboot";
	
	
	MulticastLock multicastLock;

	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		initEnviron();
	}

	public void initEnviron(){
		try{
			Runtime.getRuntime().exec("chmod 0777 " + context.getFilesDir().getAbsolutePath());
		}
		catch (IOException e) {}

		if(Build.VERSION.SDK_INT>=19){
			context.getExternalFilesDirs(null);
		}
		WifiManager wifiMgr = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.DONUT) {
			multicastLock = wifiMgr.createMulticastLock("xplat");
			multicastLock.setReferenceCounted(false);
			multicastLock.acquire();
		}
		try {
			AssetsCopy.loadAssets(this);
		} catch (IOException e) {
			finish();
		}
		launch();
	}
	String[] dangerousPerm=new String[]{"android.permission.READ_CALENDAR","android.permission.WRITE_CALENDAR",
			"android.permission.CAMERA","android.permission.READ_CONTACTS","android.permission.WRITE_CONTACTS",
			"android.permission.GET_ACCOUNTS","android.permission.ACCESS_FINE_LOCATION","android.permission.RECORD_AUDIO",
			"android.permission.READ_PHONE_STATE","android.permission.CALL_PHONE","android.permission.READ_CALL_LOG",
			"android.permission.WRITE_CALL_LOG","android.permission.ADD_VOICEMAIL","android.permission.USE_SIP",
			"android.permission.BODY_SENSORS","android.permission.SEND_SMS","android.permission.RECEIVE_SMS",
			"android.permission.READ_SMS","android.permission.RECEIVE_WAP_PUSH","android.permission.RECEIVE_MMS",
			"android.permission.READ_EXTERNAL_STORAGE","android.permission.WRITE_EXTERNAL_STORAGE"};



	@TargetApi(Build.VERSION_CODES.M)
	public String[] getPermissionNotGranted(){
		ArrayList<String> permNotGranted=new ArrayList<String>();
		for(String perm:dangerousPerm){
			if(!(this.checkSelfPermission(perm) == PackageManager.PERMISSION_GRANTED)){
				permNotGranted.add(perm);
			}
		}
		return permNotGranted.toArray(new String[0]);
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);

		MainActivity.context = this.getApplicationContext();

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			String[] reqPerms=getPermissionNotGranted();
			if(reqPerms.length>0) this.requestPermissions(reqPerms,1);
		}else{
			initEnviron();
		}


	}
	public void launch(){
		try {
			Scanner scan=new Scanner(new FileInputStream(AssetsCopy.assetsDir+"/flat"));
			String param=scan.next();
			scan.close();

			if(gdxFlag.equals(param)){
				intent=new Intent();
				intent.setClass(this,Class.forName("project.gdx.MainActivity"));
				this.startActivityForResult(intent,1);
			}else if(sdlFlag.equals(param)){
				intent=new Intent();
				intent.setClass(this,Class.forName("project.sdl.MainActivity"));
				this.startActivityForResult(intent,1);
			}else if(webFlag.equals(param)){
				intent=new Intent();
				intent.setClass(this,Class.forName("project.webapp.MainActivity"));
				this.startActivityForResult(intent,1);
			}
		} catch (FileNotFoundException | ClassNotFoundException e) {
			finish();
		}
		
	}
	@Override
	protected void onDestroy() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.DONUT) {
			multicastLock.release();
		}
		super.onDestroy();
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		boolean shutdown=true;
		try{
			Scanner scan=new Scanner(new FileInputStream(new File(AssetsCopy.assetsDir+"/flat")));
			scan.next();
			String param=scan.next();
			scan.close();
			if(shutdownFlag.equals(param)){
				shutdown=true;
			}else if(rebootFlag.equals(param)){
				shutdown=false;
			}
		}catch(Exception e){
		}
		if(!shutdown){
			launch();
		}else{
			finish();
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
}
