package project.gdx;

import android.net.wifi.*;
import android.os.*;
import com.badlogic.gdx.backends.android.*;
import xplatj.gdxconfig.GdxEntry;
import xplatj.gdxconfig.Gdx2;

public class MainActivity extends AndroidApplication {
	/** Called when the activity is first created. */
	AndroidApplicationConfiguration acfg;
	GdxEntry gmain;
	WifiManager.MulticastLock wifilock;
	public static MainActivity thisActivity;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		thisActivity=this;
		acfg = new AndroidApplicationConfiguration();
		gmain = new GdxEntry();
		Gdx2.module=new AndroidModule(this);
		Gdx2.storage=new AndroidStorage();
		initialize(gmain, acfg);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
}
