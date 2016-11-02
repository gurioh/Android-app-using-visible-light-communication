//
// Created by unchoon on 2016-07-25.
//

#include <jni.h>

#include <opencv2/core/core.hpp>
#include <opencv2/imgproc/imgproc.hpp>
#include <opencv2/highgui/highgui.hpp>

using namespace std;
using namespace cv;



extern "C"
jboolean
Java_com_example_unchoon_urpproject_Preview_Hough( JNIEnv* env, jobject thiz,
                                                   jint width, jint height,
                                                   jbyteArray NV21FrameData, jintArray outPixels)

{
    jbyte * pNV21FrameData = env->GetByteArrayElements(NV21FrameData, 0);
    jint * poutPixels = env->GetIntArrayElements(outPixels, 0);


    Mat mGray(height, width, CV_8UC1, (unsigned char *)pNV21FrameData);
    Mat mResult(height, width, CV_8UC4, (unsigned char *)poutPixels);

    cvtColor(mGray, mResult, CV_GRAY2BGRA);

    vector<Vec3f> circles;
    HoughCircles(mGray,circles,CV_HOUGH_GRADIENT,1,100,200,25,30,50);


    //findContours(OtsuImg,contours,hierarchy,CV_RETR_EXTERNAL,CV_CHAIN_APPROX_SIMPLE);



    for(size_t i=0;i<circles.size();i++){
        Point center(cvRound(circles[i][0]),cvRound(circles[i][1]));
        int radius = cvRound(circles[i][2]);
        circle(mResult,center,3,CV_RGB(255,0,0),-1,8,0);
        circle(mResult,center,radius,CV_RGB(255,0,0),3,8,0);
        //drawContours(mResult,contours,i,Scalar(0,0,255),thickness,8,hierarchy);
    }
    /// thresh = 0 is ignored





    env->ReleaseByteArrayElements(NV21FrameData, pNV21FrameData, 0);
    env->ReleaseIntArrayElements(outPixels, poutPixels, 0);

    return true;

}