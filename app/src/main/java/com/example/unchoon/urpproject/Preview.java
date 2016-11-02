package com.example.unchoon.urpproject;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.ImageView;

import java.io.IOError;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Queue;

/**
 * Created by unchoon on 2016-07-23.
 */
public class Preview implements SurfaceHolder.Callback, Camera.PreviewCallback {
    public SurfaceHolder mHolder;
    Camera mCamera;
    private ImageView MyCameraPreview = null;

    private int[] pixels = null;
    private Bitmap bitmap = null;
    private byte[] FrameData = null;
    public int previewWidth = 640;
    public int previewHeight = 480;
    public int centerP = 0;

    public Queue<int[]> queue = new LinkedList<int[]>();
    public boolean isStart = false;

    private CountDownTimer timer;
    Handler mHandler = new Handler(Looper.getMainLooper());

    static {
        System.loadLibrary("mixed_sample");
    }

    public Preview(ImageView myCameraPreview, Context context, MainActivity mainActivity) {

        mHolder = mainActivity.mHolder;
        mHolder.setFixedSize(680,480);
        mHolder.addCallback(this);
        MyCameraPreview = myCameraPreview;
        pixels = new int[previewWidth * previewHeight];
        bitmap = Bitmap.createBitmap(previewWidth, previewHeight, Bitmap.Config.ARGB_8888);
    }

    @Override
    public void onPreviewFrame(byte[] arg0, Camera arg1) {
        FrameData = arg0;
       mHandler.post(DoImageProcessing);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mCamera = Camera.open();

        try {
            mCamera.setPreviewDisplay(holder);
            mCamera.setPreviewCallback(this);
        } catch (IOException e) {
            mCamera.release();
            mCamera = null;
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Camera.Parameters parameters = mCamera.getParameters();
        parameters.setPreviewSize(previewWidth, previewHeight);
        // parameters.setAutoWhiteBalanceLock(false);
        // parameters.setAutoExposureLock(false);
        parameters.setPictureSize(640, 480);
        // 짧은 exposure time은 band구별 능력 향상
        int minexpore = parameters.getMinExposureCompensation();
        parameters.setExposureCompensation(-4);
        parameters.setAutoWhiteBalanceLock(false);
        parameters.setAntibanding(parameters.ANTIBANDING_OFF);
        parameters.setAutoExposureLock(false);
        parameters.setPreviewFrameRate(24);
        //parameters.setFocusMode(parameters.FOCUS_MODE_FIXED);
        Log.i("TAG", "Surrpoted Exposere Mode: " + minexpore);
        Log.i("TAG", "Surrpoted Exposere Mode: " + parameters.get("exposure"));
        Log.i("TAG", "Surrpoted Exposere Mode: " + parameters.get("whitebalance"));
        Log.i("anti banding", "Surrpoted Exposere Mode: " + parameters.getAntibanding());
        Log.i("vidio", "Surrpoted Exposere Mode: " + String.valueOf(parameters.getVideoStabilization()));
        Log.i("white", "Surrpoted Exposere Mode: " + parameters.getWhiteBalance());
        Log.i("auto", "Surrpoted Exposere Mode: " + parameters.getFocusMode());
        Log.i("auto", "Surrpoted Exposere Mode: " + parameters.getPreviewFrameRate());
        mCamera.setParameters(parameters);
        mCamera.setDisplayOrientation(90);
        mCamera.startPreview();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mCamera.stopPreview();
        mCamera = null;
    }

    private Runnable DoImageProcessing = new Runnable() {

        @SuppressLint("NewApi")
        public void run() {
            Blur(previewWidth, previewHeight, FrameData, pixels);
           int temp = ImageProcessing(previewWidth, previewHeight, FrameData, pixels);
            if(temp !=0)
                centerP = temp;

            //Hough(previewWidth, previewHeight, FrameData, pixels);
            // ImageProcessing(previewWidth, previewHeight, FrameData, pixels);
            bitmap.setPixels(pixels, 0, previewWidth, 0, 0, previewWidth, previewHeight);
            //Log.i("cetnerX", String.valueOf(centerP));

            if (isStart) {
                queue.offer(pixels);
               // Log.i("queueSize1:", String.valueOf(queue.size()));
            }
           // if(queue.size()==10) isStart = false;
            MyCameraPreview.setImageBitmap(bitmap);
          //  System.out.println(MyCameraPreview.getWidth() + " " + MyCameraPreview.getHeight());
            MyCameraPreview.setRotation(90);
        }
    };

    public native int ImageProcessing(int width, int heigh, byte[] NV21FrameData, int[] pixels);

    public native String Decoding(int width, int heigh, int offset, int[] pixels);

    public native boolean Blur(int width, int heigh, byte[] NV21FrameData, int[] pixels);

    public native void OTSU(long matAddrRgba);

    public native void Canny(long matAddrRgba);

    public native void Hough(int width, int heigh, byte[] NV21FrameData, int[] pixels);
}
