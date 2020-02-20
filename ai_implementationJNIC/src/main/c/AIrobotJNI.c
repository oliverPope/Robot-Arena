#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <pthread.h>
#include <jni.h>
#include <string.h>
#include <unistd.h>
#include <stdbool.h>

#include "AIrobotJNI.h"

JNIEnv *env;

JNIEXPORT void JNICALL Java_aiCJNI_runAI(JNIEnv *inEnv, jobject rc)
{
    /*creates a pthread with the start method*/
    pthread_t aiC;
    int val1 = pthread_create(&aiC, NULL, start,rc);
    *env = *inEnv;
    pthread_join(aiC, NULL);
}



void *start(jobject rc)
{
    char directionStr[5];
    strcpy(directionStr, "east");

    /*loop until thread is exited*/
    while(1)
    {
            /*get reference to the robot control class*/
            jclass rcClass = (*env)->GetObjectClass(env,rc);
            /*get reference to the robot control methods*/
            jmethodID fire = (*env)->GetMethodID(
                env, rcClass, "fire", "Z");

            jmethodID moveNorth = (*env)->GetMethodID(
                env, rcClass, "moveNorth", "Z");

            jmethodID moveSouth = (*env)->GetMethodID(
                    env, rcClass, "moveSouth", "Z");

            jmethodID moveEast = (*env)->GetMethodID(
                    env, rcClass, "moveEast", "Z");

            jmethodID moveWest = (*env)->GetMethodID(
                    env, rcClass, "moveWest", "Z");

            jmethodID getRobot = (*env)->GetMethodID(
                        env, rcClass, "getRobot", "LRobotInfo");

            jmethodID isWinner = (*env)->GetMethodID(
                        env, rcClass, "isWinner", "V");

            jmethodID isAlive = (*env)->GetMethodID(
                        env, rcClass, "isAlive", "V");


            jmethodID getAllRobots = (*env)->GetMethodID(
                        env, rcClass, "getAllRobots", "Ljava/util/ArrayList;");

            jobject curRobot = (*env)->CallObjectMethod(env,rc, getRobot);

            /*call isWinner and isAlive and handle possible exceptions thrown. If an exception occurrs exit the thread*/
            (*env)->CallVoidMethod(env, rcClass, isWinner);
            if((*env)->ExceptionCheck(env))
            {
                /* Clean up resources if necessary. */
                pthread_exit(0);
                return 0;
            }

             (*env)->CallVoidMethod(env, rcClass, isAlive);
             if((*env)->ExceptionCheck(env))
             {
                /* Clean up resources if necessary. */
                pthread_exit(0);
                return 0;
             }

            /*access the array list of robot infos and iterate through them*/
            jobject allRobotsList = (*env)->CallObjectMethod(env, rc, getAllRobots);
            jclass arrayClass = (*env)->FindClass(env,"java/util/ArrayList");
            jmethodID listSize = (*env)->GetMethodID(env, arrayClass, "size", "()I");
            int size = (*env)->CallObjectMethod(env, arrayClass, listSize);

            jmethodID get = (*env)->GetMethodID(env, arrayClass, "get", "LRobotInfo");

            int count = 0;

            while(count < size)
            {
                /*get the robot at the count*/
                jobject robot = (*env)->CallObjectMethod(env,arrayClass, "get",get,count);
                jclass robotInfoClass = (*env)->GetObjectClass(env, robot);
                /*access the acquire target method*/
                jmethodID acquireTarget = (*env)->GetMethodID(env, rcClass, "acquireTarget", acquireTarget);
                jboolean validTarget = (*env)->CallObjectMethod(env, rcClass, "acquireTarget", robot, curRobot);
                if(validTarget)
                {
                    sleep(500);
                    jmethodID getRow = (*env)->GetMethodID(env, robotInfoClass, "getRow", "()I");
                    jmethodID getColumn = (*env)->GetMethodID(env, robotInfoClass, "getColumn", "()I");
                    jobject row = (*env)->CallObjectMethod(env, robot, getRow);
                    jobject col = (*env)->CallObjectMethod(env, robot, getColumn);
                    jboolean fire = (*env)->CallObjectMethod(env, rcClass, "fire",row, col);
                    count++;
                    break;
                }

                count++;
            }


            /*depending on current set direction will call the appropriate move method*/
            if (strcmp(directionStr, "north") == 0)
            {
                 if(!(*env)->CallBooleanMethod(env,rc,moveNorth))
                 {
                    strcpy(directionStr, "east");
                 }
                 break;
            }
            else if (strcmp(directionStr, "east") == 0)
            {
                if(!(*env)->CallBooleanMethod(env,rc,moveEast))
                 {
                    strcpy(directionStr, "south");

                 }
                 break;
            }
            else if (strcmp(directionStr, "south") == 0)
            {
                if(!(*env)->CallBooleanMethod(env,rc,moveSouth))
                 {
                    strcpy(directionStr, "west");
                 }
                 break;
            }
            else if (strcmp(directionStr, "west") == 0)
            {
                if(!(*env)->CallBooleanMethod(env,rc,moveWest))
                 {
                    strcpy(directionStr, "north");
                 }
                 break;
            }

            /*check if winner/alive again and handle exceptions*/
            (*env)->CallVoidMethod(env, rcClass, isWinner);
            if((*env)->ExceptionCheck(env))
            {
                /* Clean up resources if necessary. */
                pthread_exit(0);
                return 0;
            }

             (*env)->CallVoidMethod(env, rcClass, isAlive);
             if((*env)->ExceptionCheck(env))
             {
                /* Clean up resources if necessary. */
                pthread_exit(0);
                return 0;
             }
            sleep(1000);
    }
}