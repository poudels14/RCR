package edu.upenn.cis.rcr_34.reputablecoursereview;

/**
 * Created by andrew on 4/12/15.
 */
public class Course {
    private String courseCode;
    private String semesterTaken;
    private String individualRating;

    //Construct course object for a given course code and semester
    public Course(String code, String semester){
        courseCode = code;
        semesterTaken = semester;
        individualRating = "";
    }

    //Construct course object from String format
    public Course (String base){
        String[] parts = base.split("~");
        courseCode = parts[0];
        if(parts.length == 1){
            semesterTaken = "";
        }
        else {
            semesterTaken = parts[1];
        }
        individualRating = "";
    }

    //Convert course object to a String so it can be stored in Parse
    public String toString(){
        return courseCode + "~" + semesterTaken;
    }

    //Get course code
    public String getCourseCode(){
        return this.courseCode;
    }

    //Get the semester taken
    public String getSemesterTaken(){
        return this.semesterTaken;
    }

    //Get the rating
    public String getRating(){
        return this.individualRating;
    }

    //Get the semester taken
    public void setSemesterTaken(String semester){
        this.semesterTaken = semester;
    }

    //Set the rating
    public void setRating(String rating){
        this.individualRating = rating;
    }
}
