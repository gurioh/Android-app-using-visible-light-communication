//
// Created by unchoon on 2016-08-14.
//

#include <jni.h>
#include <string.h>
#include <stdio.h>


jint White = -1;
jint Black = -16777216;
extern "C"
jstring
Java_com_example_unchoon_urpproject_Preview_Decoding(
        JNIEnv* env, jobject thiz,
        jint width, jint height,
        jint offset, jintArray outPixels)
{

    jint *poutPixels = env->GetIntArrayElements(outPixels,0);
    int count=0;
    int prebit=0;
    int temp=0;
   char packets[60];
    jstring result;

    for(int i=0;i<height;i++){
        for(int j=0;j<width;j++){
            if(j == (offset)){
                temp = poutPixels[j+i*width];
                if(temp == White){
                    if(prebit == Black){
                        if(count>=5 && count <= 12){
                            strcat(packets,"0");
                        }else if(count>=13 && count <=20){
                            strcat(packets,"00");
                        }else if(i == height-1 || count >55){
                            strcat(packets,"998");
                        }
                        count=1;
                    }else if((i+1)==height && prebit == White){
                        if(count>=4 && count<=11){
                            strcat(packets,"1");
                        }else if(count>=13 && count<=18){
                            strcat(packets,"11");
                        }else if(count >=20 && count <=24){
                            strcat(packets,"111");
                        }else if(i == height-1 || count >60){
                            strcat(packets,"999");
                        }
                    }else if(prebit == 0 || prebit == temp){
                        count = count+1;
                    }
                    prebit = temp;
                }else{
                    if(prebit == White){
                        if(count>=4 && count<=11){
                            strcat(packets,"1");
                        }else if(count>=13 && count<=18){
                            strcat(packets,"11");
                        }else if(count >=20 && count <=24){
                            strcat(packets,"111");
                        }else if(i == height-1 || count >60){
                            strcat(packets,"999");
                        }
                        count=1;
                    }else if((i+1)==height && prebit == Black){
                        if(count>=5 && count <= 12){
                            strcat(packets,"0");
                        }else if(count>=13 && count <=20){
                            strcat(packets,"00");
                        }else if(i == height-1 || count >55){
                            strcat(packets,"998");
                        }
                    }else if(prebit==0 || prebit==temp) {
                        count = count + 1;
                    }
                    prebit = temp;
                }
            }
        }
    }
    env->ReleaseIntArrayElements(outPixels, poutPixels, 0);
    puts(packets);
    result = env->NewStringUTF(packets);

    return  env->NewStringUTF(packets);
}
