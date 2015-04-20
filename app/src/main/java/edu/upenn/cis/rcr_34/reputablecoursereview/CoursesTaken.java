package edu.upenn.cis.rcr_34.reputablecoursereview;

import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sagar on 4/7/15.
 */
public class CoursesTaken {
    private String courseCode;
    private String semesterTaken;
    private String year;
    private int rating;
    private String userEmail;

    public CoursesTaken(String email, String courseCode, String semesterTaken, String year, int rating) {
        userEmail = email;
        this.courseCode = courseCode;
        this.semesterTaken = semesterTaken;
        this.year = year;
        this.rating = rating;
    }

    public CoursesTaken(String email, String courseCode, String semesterTaken, String year) {
        userEmail = email;
        this.courseCode = courseCode;
        this.semesterTaken = semesterTaken;
        this.year = year;
        this.rating = -1;
    }

    public String getCourseCode() {
        return this.courseCode;
    }

    public String getSemesterTaken() {
        return this.semesterTaken;
    }

    public String getYearTaken() {
        return this.year;
    }

    public int getRating() {
        return this.rating;
    }
}
