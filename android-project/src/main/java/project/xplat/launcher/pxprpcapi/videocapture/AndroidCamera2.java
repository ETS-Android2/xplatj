package project.xplat.launcher.pxprpcapi.videocapture;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.ImageFormat;
import android.hardware.camera2.*;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.os.Build;
import android.util.Size;
import android.view.Surface;
import project.xplat.launcher.pxprpcapi.ApiServer;
import pursuer.pxprpc.AsyncReturn;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AndroidCamera2 {
    public CameraManager camSvr;
    public Exception lastExc;
    public String uid="";
    public void  accuireCameraService(String uid) {
        this.uid=uid;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            camSvr=(CameraManager)ApiServer.defaultAndroidContext.getSystemService(Context.CAMERA_SERVICE);
        }
    }
    public void releaseCameraService(){
        this.uid="";
    }

    public String getUid(){
        return uid;
    }
    public String[] getCameraIdList(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            try {
                return camSvr.getCameraIdList();
            } catch (Exception e) {
                this.lastExc=e;
            }
        }
        return null;
    }
    public void clearException(){
        this.lastExc=null;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public String getBaseCameraInfo(String id){
        try {
            CameraCharacteristics info = camSvr.getCameraCharacteristics(id);
            StringBuilder sb=new StringBuilder();
            sb.append("face:");
            if(info.get(CameraCharacteristics.LENS_FACING)==0){
                sb.append("front");
            }else{
                sb.append("back");
            }
            sb.append("\n");
            sb.append("flashAvailable:");
            sb.append(info.get(CameraCharacteristics.FLASH_INFO_AVAILABLE)?1:0);
            sb.append("\n");
            StreamConfigurationMap sscm = info.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            sb.append("size:");
            Size[] sizes = sscm.getOutputSizes(ImageFormat.YUV_420_888);
            for(Size e : sizes){
                sb.append(e.getWidth()+","+e.getHeight());
                sb.append(" ");
            }
            sb.append("\n");
            return sb.toString();
        } catch (Exception e) {
            this.lastExc=e;
        }
        return null;
    }
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void openCamera(final AsyncReturn<CameraDevice> aret, String id){

        try {
            camSvr.openCamera(id, new CameraDevice.StateCallback() {
                @Override
                public void onOpened(CameraDevice camera) {
                    aret.result(camera);
                }

                @Override
                public void onDisconnected(CameraDevice camera) {
                }

                @Override
                public void onError(CameraDevice camera, int error) {
                    lastExc=new Exception("Android Camera2 Error:"+error);
                    aret.result(null);
                }

            },ApiServer.getHandler());
        } catch (Exception e) {
            lastExc=e;
            aret.result(null);
        }
    }
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void closeCamera(final AsyncReturn<CameraDevice> aret, CameraDevice cam){
        try {
            cam.close();
        } catch (Exception e) {
            lastExc=e;
            aret.result(null);
        }
    }

    private ImageReader imgReader;
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void requestPreviewSession(final AsyncReturn<CameraCaptureSession> aret,CameraDevice camDev, int width, int height){
        imgReader = ImageReader.newInstance(width,height,ImageFormat.YUV_420_888,2);
        try {
            final CaptureRequest.Builder capReq = camDev.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            capReq.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
            List<Surface> tarSurf = new ArrayList<Surface>();
            tarSurf.add(imgReader.getSurface());
            camDev.createCaptureSession(tarSurf, new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(CameraCaptureSession session) {
                    try{
                        session.setRepeatingRequest(capReq.build(),null,ApiServer.getHandler());
                    }catch (Exception e) {
                    }
                    aret.result(session);
                }
                @Override
                public void onConfigureFailed(CameraCaptureSession session) {
                    aret.result(null);
                }
            },ApiServer.getHandler());
            imgReader.setOnImageAvailableListener(new ImageReader.OnImageAvailableListener() {
                @Override
                public void onImageAvailable(ImageReader reader) {
                    //dispatch image available event
                }
            },ApiServer.getHandler());
        } catch (Exception e) {
            lastExc=e;
            aret.result(null);
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public List<Image.Plane> accuireLastestImageData(CameraCaptureSession session){
        Image.Plane[] plane1 = imgReader.acquireLatestImage().getPlanes();
        return Arrays.asList(plane1);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public String getPlaneInfo(Image.Plane plane1){
        StringBuilder sb=new StringBuilder();
        sb.append("pixelStride:"+plane1.getPixelStride()+"\n")
                .append("rowStride:"+plane1.getRowStride()+"\n");
        return sb.toString();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public byte[] getPlaneData(Image.Plane plane1){
        ByteBuffer buf1 = plane1.getBuffer();
        //avoid method signature error on low version android.
        byte[] buf2 = new byte[((ByteBuffer)buf1).remaining()];
        buf1.get(buf2);
        return buf2;
    }


}
