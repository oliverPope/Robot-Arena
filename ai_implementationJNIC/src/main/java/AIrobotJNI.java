
import java.util.HashMap;
import java.util.Map;

public class AIrobotJNI{
    static
    {

        //System.load("/Users/oliverpope/SECassignment2019/ai_implementationJNIC/build/libs/nativelibraries/libAIrobotJNI.jnlib");
        //load the native library
         System.loadLibrary("AIrobotJNI");
    }

    public native void runAI(RobotControl rc);

    public static void startAIC(RobotControl rc)
    {
        new AIrobotJNI().runAI(rc);
    }
}

