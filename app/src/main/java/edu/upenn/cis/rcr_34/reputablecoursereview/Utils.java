package edu.upenn.cis.rcr_34.reputablecoursereview;

/**
 * Created by sagar on 4/7/15.
 */
public class Utils {
    private static int viewsID = 1001;

    public static int getUniqueID() {
        return viewsID++;
    }
}