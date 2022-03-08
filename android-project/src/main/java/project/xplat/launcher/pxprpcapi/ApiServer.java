package project.xplat.launcher.pxprpcapi;

import android.content.Context;

import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import project.xplat.launcher.pxprpcapi.videocapture.AndroidCamera2;
import pursuer.pxprpc_ex.TCPBackend;


import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.TreeMap;


public class ApiServer {
    protected TCPBackend pxpServ;
    public static Context defaultAndroidContext;
    public static HandlerThread handlerThread;
    public static Handler handler;
    public static Map<String, Object> funcMap=new TreeMap<String, Object>();
    public static int port=2050;
    public static Handler getHandler(){
        return handler;
    }
    public void start() throws IOException {
        pxpServ= new TCPBackend();
        handlerThread = new HandlerThread("PxpRpcHandlerThread");
        handlerThread.start();
        while(handlerThread.getLooper()==null){}
        handler=new Handler(handlerThread.getLooper());
        pxpServ.bindAddr= new InetSocketAddress(
                Inet4Address.getByAddress(new byte[]{(byte)0,(byte)0,(byte)0,(byte)0}),port);

        pxpServ.funcMap.put("AndroidCamera2",new AndroidCamera2());
        pxpServ.funcMap.putAll(ApiServer.funcMap);
        Log.d("PxpRpc", "start: listen");
        pxpServ.listenAndServe();

    }
}
