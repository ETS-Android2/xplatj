package project.gdx;
import java.io.*;
import java.util.*;
import xplatj.platform.storage.*;
import android.os.storage.*;
import android.content.*;

public class AndroidStorage implements Storage
{
	
	@Override
	public String[] getStoragePathList() {
		ArrayList<String> paths=new ArrayList<String>();
		if(android.os.Build.VERSION.SDK_INT>=19){
			File[] dirs=MainActivity.thisActivity.getExternalFilesDirs(null);
			for(File ef:dirs){
				if(ef!=null){
					paths.add(ef.getAbsolutePath());
					MainActivity.thisActivity.getSystemService(Context.STORAGE_SERVICE);
				}
			}
		}else{
			paths.add(MainActivity.thisActivity.getExternalFilesDir(null).getAbsolutePath());
		}
		paths.add(MainActivity.thisActivity.getFilesDir().getAbsolutePath());
		return paths.toArray(new String[0]);
	}
	
}
