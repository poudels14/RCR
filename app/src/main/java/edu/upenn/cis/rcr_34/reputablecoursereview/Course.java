package edu.upenn.cis.rcr_34.reputablecoursereview;

/**
 * Created by andrew on 4/12/15.
 */
public class Course {
    private String courseCode;
    private String semesterTaken;
    private String individualRating;

    public Course(String code, String semester){
        courseCode = code;
        semesterTaken = semester;
        individualRating = "";
    }

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

    public String toString(){
        return courseCode + "~" + semesterTaken;
    }

    public String getCourseCode(){
        return this.courseCode;
    }

    public String getSemesterTaken(){
        return this.semesterTaken;
    }

    public String getRating(){
        return this.individualRating;
    }

    public void setSemesterTaken(String semester){
        this.semesterTaken = semester;
    }

    public void setRating(String rating){
        this.individualRating = rating;
    }
}
