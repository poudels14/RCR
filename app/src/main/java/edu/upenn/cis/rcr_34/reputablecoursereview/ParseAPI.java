package edu.upenn.cis.rcr_34.reputablecoursereview;

import android.content.Context;

import com.parse.Parse;

/**
 * Created by sagar on 3/30/15.
 *
 * This class should be used to initiate Parse since Parse crashes is initiated multiple times and we might change API keys sometime later
 */
public class ParseAPI {
    private static boolean isInitiated = false;
    public static void init(Context ctx){
        if (!isInitiated){
            isInitiated = true;
            Parse.enableLocalDatastore(ctx);
            Parse.initialize(ctx, "W5JnCKDZoSYR2AHncCRPZ7TZ94e3x9RJcAQjoc0a",
                    "9vWs3BE45BCEtigsvl9ezo14wAg2ECoPxADTxtoC");
        }
    }
}
