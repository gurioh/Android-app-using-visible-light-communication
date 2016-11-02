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
JNIEXPORT jboolean JNICALL Java_com_example_unchoon_urpproject_Preview_Blur( JNIEnv* env, jobject thiz,
                                                                 jint width, jint height,
                                                                 jbyteArray NV21FrameData, jintArray outPixels);

JNIEXPORT jboolean JNICALL Java_com_example_unchoon_urpproject_Preview_Blur( JNIEnv* env, jobject thiz,
                                                                 jint width, jint height,
                                                                 jbyteArray NV21FrameData, jintArray outPixels)
{
    jbyte * pNV21FrameData = env->GetByteArrayElements(NV21FrameData, 0);
    jint * poutPixels = env->GetIntArrayElements(outPixels, 0);


    Mat mblur(height, width, CV_8UC1, (unsigned char *)pNV21FrameData);
    Mat mResult(height, width, CV_8UC4, (unsigned char *)poutPixels);

    blur(mblur,mblur,Size(2,2));
    Ptr<CLAHE> clahe = createCLAHE();
    clahe->setClipLimit(2);
    clahe->apply(mblur,mblur);
    cvtColor(mblur, mResult, CV_GRAY2BGRA);
    env->ReleaseByteArrayElements(NV21FrameData, pNV21FrameData, 0);
    env->ReleaseIntArrayElements(outPixels, poutPixels, 0);
    return true;

}
}