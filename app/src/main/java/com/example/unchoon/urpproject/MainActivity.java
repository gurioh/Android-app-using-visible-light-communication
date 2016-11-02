package com.example.unchoon.urpproject;


import android.app.AlertDialog;
import android.app.Activity;
import android.content.DialogInterface;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import android.os.Handler;
import android.widget.Toast;

import java.util.logging.LogRecord;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    long result = 0;
    int count=0;
    final int White = -1;
    final int Black = -16777216;
    final String Header = "01110";
    public int index=0;
    private int[] packetArray = new int[10000];
    private Preview preview;
    private ImageView MyCameraPreview = null;
    private Button btnstart,btnstop;
    private TextView temperature;

    public SurfaceView mSurfaceView;
    public SurfaceHolder mHolder;

    //data packet array,string
    private String packets ="";

    //find preamble int array
    private int[] pi = new int[100];

    Handler mHandler = null;
    private TextviewThread textviewThread;

    long startTime=0;
    long endTime=0;
    int err=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        mSurfaceView =(SurfaceView)findViewById(R.id.surfaceView);
        mHolder =mSurfaceView.getHolder();
        MyCameraPreview =(ImageView)findViewById(R.id.imageView);
        temperature = (TextView)findViewById(R.id.textView);
        preview = new Preview(MyCameraPreview,this,MainActivity.this);

        btnstart = (Button)findViewById(R.id.button);
        btnstop = (Button)findViewById(R.id.button2);
        btnstart.setOnClickListener(this);
        btnstop.setOnClickListener(this);

        mHandler = new Handler();


    }


    @Override
    public void onClick(View v) {

        switch(v.getId()){

            case R.id.button:
                preview.isStart = true;
                textviewThread = new TextviewThread(true);
                textviewThread.start();
                break;

            case R.id.button2:


        }

    }

    private void printpixel(int[] pixel, int previewWidth, int previewHeight) {
        int count=0;
        int prebit=0;
        int temp=0;
        for(int i=0;i<previewHeight;i++){
            for(int j=0;j<previewWidth;j++){
                if(j == (preview.centerP)){
                   temp = pixel[j+i*previewWidth];
                    if(temp == White){
                        if(prebit == Black){
                            if(count>=4 && count <= 12){
                                packets = packets+"0";
                            }else if(count>=13 && count <=20){
                                packets = packets+"00";
                            }else if(i == previewHeight-1 || count >55){
                                packets = packets+"998";
                            }
                            count=1;
                        }else if((i+1)==previewHeight && prebit == White){
                            if(count>=4 && count<=11){
                                packets = packets+"1";
                            }else if(count>=13 && count<=18){
                                packets = packets+"11";
                            }else if(count >=20 && count <=24){
                                packets = packets+"111";
                            }else if(i == previewHeight-1 || count >60){
                                packets = packets+"999";
                            }
                        }else if(prebit == 0 || prebit == temp){
                            count = count+1;
                        }
                        prebit = temp;
                    }else{
                        if(prebit == White){
                            if(count>=4 && count<=11){
                                packets = packets+"1";
                            }else if(count>=13 && count<=18){
                                packets = packets+"11";
                            }else if(count >=20 && count <=24){
                                packets = packets+"111";
                            }else if(i == previewHeight-1 || count >60){
                                packets = packets+"999";
                            }
                            count=1;
                        }else if((i+1)==previewHeight && prebit == Black){
                            if(count>=4 && count <= 12){
                                packets = packets+"0";
                            }else if(count>=13 && count <=20){
                                packets = packets+"00";
                            }else if(i == previewHeight-1 || count >55){
                                packets = packets+"998";
                            }
                        }else if(prebit==0 || prebit==temp) {
                            count = count + 1;
                        }
                        prebit = temp;
                    }
                 //  Log.i("pixel: ",String.valueOf(pixel[j+i*previewWidth]+"  "+String.valueOf(i)+" "+String.valueOf(j)));
                   // Log.i("x,y : ",String.valueOf(i)+" "+String.valueOf(j));
                }
            }
        }



    }

    public void Kmp(){
        int i=0, j=0;

        while(i<packets.length()){
            if(j==-1||packets.charAt(i)==Header.charAt(j)){
                i++;
                j++;
            }else{
                j=pi[j];
            }
            if(j==Header.length()){
                if((i+16)<packets.length()) {
                    if(!packets.substring(i,i+16).contains("9")){
                        System.out.println(packets.substring(i, i + 16));

                        if(index ==packetArray.length) {
                            /*for(int k=0;k<packetArray.length;k++){
                                System.out.println("index: "+k+ "data: "+packetArray[k]);
                            }*/

                           for(int k=0;k<packetArray.length;k++){
                                if(packetArray[k] != 26) err++;
                            }
                            result = MODE();
                            index=0;
                            //err=0;
                        }else{
                            packetArray[index] = manchesterDecode(packets.substring(i, i + 16));
                            index++;
                        }
                    }

                }
                j = pi[j];
            }
        }
    }

    public int MODE(){

        int mode =0;
        int[] Arrayindex = new int[255];
        int max = Integer.MIN_VALUE;

        for(int i=0;i<packetArray.length;i++){
            Arrayindex[packetArray[i]]++;
        }
        for(int i=0;i<Arrayindex.length;i++){
            if(max<Arrayindex[i]){
                max=Arrayindex[i];
                mode=i;
            }
        }
        return mode;
    }

    public void PI(){
        int i=0,j=-1;
        pi[0] = -1;
        while(i<Header.length()){
            if(j==-1||Header.charAt(i) == Header.charAt(j))
                pi[++i] = ++j;
            else
                j = pi[j];
        }
    }

    public int manchesterDecode(String s){
        String decodestring="";
        for(int i=7;i>=0;i--){
            if(s.substring(i*2,i*2+2).equals("01")) decodestring += "1";
            else decodestring+="0";
        }
        return Integer.valueOf(decodestring,2);
    }

    class TextviewThread extends Thread{
        private boolean isPlay = false;
        int[] temp;

        String text="";
        public TextviewThread(boolean isPlay){
            this.isPlay = isPlay;
        }
        public void stopThread(boolean isPlay){
            this.isPlay = !isPlay;
        }
        @Override
        public void run(){
            super.run();
            PI();
            while (isPlay) {
                if(preview.queue.size()>0) {
                    temp = preview.queue.poll();
                    //packets=preview.Decoding(preview.previewWidth, preview.previewHeight,preview.centerP,temp);
                    printpixel(temp, preview.previewWidth, preview.previewHeight);

                   // System.out.println(packets);
                    Kmp();
                    text = String.valueOf(result);
                  //  System.out.println(text);
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            temperature.setText(String.valueOf(index));

                            if (!text.equals("0")) {
                                temperature.setText(text);

                                AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
                                alertDialog.setTitle(" Testing");
                                alertDialog.setMessage(String.valueOf(err)).setCancelable(false).setPositiveButton("종료", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        finish();
                                    }
                                }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                });
                                AlertDialog dialog = alertDialog.create();
                                dialog.show();
                                textviewThread.stopThread(false);
                                preview.isStart= false;
                            }

                        }
                    });
                    packets = "";
                }
            }
        }
    }


}
