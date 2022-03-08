package project.xplat.launcher;


import android.app.*;
import android.content.*;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.MulticastLock;
import android.os.*;
import android.util.Log;

import java.util.*;
import java.io.*;
import android.view.*;



public class MainActivity extends Activity {
	/** Called when the activity is first created. */
	public static Activity context;
	private Intent intent;
	public static String gdxFlag="gdx";
	public static String sdlFlag="sdl";
	public static String webFlag="web";
	public static String shutdownFlag="shutdown";
	public static String rebootFlag="reboot";
	
	
	MulticastLock multicastLock;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		context = this;
		try{
			Runtime.getRuntime().exec("chmod 0777 " + context.getFilesDir().getAbsolutePath());
			
		}
		catch (IOException e) {}
		
		if(android.os.Build.VERSION.SDK_INT>=19){
			context.getExternalFilesDirs(null);
		}
		WifiManager wifiMgr = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
		multicastLock = wifiMgr.createMulticastLock("xplat");
		multicastLock.setReferenceCounted(false);
		multicastLock.acquire();
		
		
		
		try {
			AssetsCopy.loadAssets(this);
		} catch (IOException e) {
			finish();
		}
		launch();
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
		multicastLock.release();
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
