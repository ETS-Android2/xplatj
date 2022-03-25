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
import project.xplat.launcher.pxprpcapi.Utils;
import pursuer.pxprpc.AsyncReturn;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class AndroidCamera2 {
    public CameraManager camSvr;
    public String uid="";
    public void  accuireCameraService(String uid) {
        this.uid=uid;
        camSvr=(CameraManager)ApiServer.defaultAndroidContext.getSystemService(Context.CAMERA_SERVICE);
    }
    public void releaseCameraService(){
        this.uid="";
    }

    public String getUid(){
        return uid;
    }
    public String getCameraIdList() throws CameraAccessException {
        return Utils.joinStringList(Arrays.asList(camSvr.getCameraIdList()),"\n");
    }

    public String getBaseCameraInfo(String id) throws CameraAccessException {
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
    }
    public void openCamera(final AsyncReturn<Object> aret, String id) {
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
                    aret.result(new Exception("Android Camera2 Error:" + error));
                }

            }, ApiServer.getHandler());
        }catch(Exception e){
            aret.result(e);
        }
    }
    public void closeCamera(final AsyncReturn<CameraDevice> aret, CameraDevice cam){
        cam.close();
    }

    private ImageReader imgReader;
    public void requestPreviewSession(final AsyncReturn<Object> aret,CameraDevice camDev, int width, int height){
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
                        aret.result(e);
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
            aret.result(e);
        }
    }

    public List<Image.Plane> accuireLastestImageData(CameraCaptureSession session){
        Image.Plane[] plane1 = imgReader.acquireLatestImage().getPlanes();
        return Arrays.asList(plane1);
    }

    public String getPlaneInfo(Image.Plane plane1){
        StringBuilder sb=new StringBuilder();
        sb.append("pixelStride:"+plane1.getPixelStride()+"\n")
                .append("rowStride:"+plane1.getRowStride()+"\n");
        return sb.toString();
    }

    public byte[] getPlaneData(Image.Plane plane1){
        ByteBuffer buf1 = plane1.getBuffer();
        //avoid method signature error on low version android.
        byte[] buf2 = new byte[((ByteBuffer)buf1).remaining()];
        buf1.get(buf2);
        return buf2;
    }


}
