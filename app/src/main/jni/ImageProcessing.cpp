//
// Created by unchoon on 2016-07-23.
//

#include <jni.h>

#include <opencv2/core/core.hpp>
#include <opencv2/imgproc/imgproc.hpp>
#include <opencv2/highgui/highgui.hpp>
#include <android/log.h>

using namespace std;
using namespace cv;

Mat * mOtsu = NULL;

extern "C"
jint
Java_com_example_unchoon_urpproject_Preview_ImageProcessing(
        JNIEnv* env, jobject thiz,
        jint width, jint height,
        jbyteArray NV21FrameData, jintArray outPixels)
{
    jbyte * pNV21FrameData = env->GetByteArrayElements(NV21FrameData, 0);
    jint * poutPixels = env->GetIntArrayElements(outPixels, 0);

    int centerX=0;
    int radius=0;
    if ( mOtsu == NULL )
    {
        mOtsu = new Mat(height, width, CV_8UC1);
    }

  // Mat image(height,width,CV_8UC4,(unsigned char *)pNV21FrameData);
    Mat mGray(height, width, CV_8UC1, (unsigned char *)pNV21FrameData);
    Mat mResult(height, width, CV_8UC4, (unsigned char *)poutPixels);
    Mat OtsuImg = *mOtsu;
    int thresh = 0;


    //adaptiveThreshold(mGray,OtsuImg,255,CV_ADAPTIVE_THRESH_MEAN_C,THRESH_BINARY,55,5);
    adaptiveThreshold(mGray,OtsuImg,255,CV_ADAPTIVE_THRESH_MEAN_C,THRESH_BINARY,75,5);
    //threshold( mGray, OtsuImg, thresh, 255, THRESH_BINARY | THRESH_OTSU );



    cvtColor(OtsuImg, mResult, CV_GRAY2BGRA);


    vector<Vec3f> circles;
    HoughCircles(mGray,circles,CV_HOUGH_GRADIENT,2,200,200,25,60,70);

    for(size_t i=0;i<circles.size();i++){
        Point center(cvRound(circles[i][0]),cvRound(circles[i][1]));
        centerX = cvRound(circles[i][0]);
        radius = cvRound(circles[i][2]);
        circle(mResult,center,3,Scalar(255,0,0,255),-1,8,0);
        //__android_log_print(ANDROID_LOG_DEBUG,"CHK","Center valus is %d",center.x);
        //__android_log_print(ANDROID_LOG_DEBUG,"CHK","radius is %d",radius);
        circle(mResult,center,radius,Scalar(255,0,0,255),3,8,0);
       // circle(mResult, center, 10, Scalar(255,0,0,255));

    }


    env->ReleaseByteArrayElements(NV21FrameData, pNV21FrameData, 0);
    env->ReleaseIntArrayElements(outPixels, poutPixels, 0);

    return centerX+radius+radius/2;
}