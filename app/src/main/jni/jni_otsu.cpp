//
// Created by unchoon on 2016-07-22.
//

#include <jni.h>
#include <opencv2/imgproc/imgproc.hpp>
#include <opencv2/highgui/highgui_c.h>
#include <opencv2/opencv.hpp>


using namespace std;
using namespace cv;

extern "C" {
JNIEXPORT void JNICALL Java_com_example_unchoon_urpproject_OTSU(JNIEnv*, jobject, jlong addrRgba);

JNIEXPORT void JNICALL Java_com_example_unchoon_urpproject_OTSU(JNIEnv*, jobject, jlong addrRgba)
{
    Mat& mRgb = *(Mat*)addrRgba;
   // cvtColor(mRgb,mRgb,CV_BGR2GRAY);
    //adaptiveThreshold(mRgb,mRgb,255,CV_ADAPTIVE_THRESH_MEAN_C,THRESH_BINARY,25,5);
    //cvtColor(mRgb,mRgb,CV_GRAY2BGRA);
    threshold(mRgb,mRgb,128,255,CV_THRESH_TOZERO);

}
}