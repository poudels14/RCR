/**
 * Created by sagar on 4/7/15.
 */
public class CoursesTaken {
    private String courseCode;
    private String semesterTaken;
    private String individualRating;

    public String getCourseCode(){
        return this.courseCode;
    }

    public String getSemesterTaken(){
        return this.semesterTaken;
    }

    public String getRating(){
        return this.individualRating;
    }

    public void setSemsterTaken(String semster){
        this.semesterTaken = semster;
    }

    public void setRating(String rating){
        this.individualRating = rating;
    }
}
