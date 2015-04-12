/**
 * Created by sagar on 4/7/15.
 */
public class CoursesTaken {
    private String courseCode;
    private String semesterTaken;
    private String individualRating;

    public CoursesTaken(String code, String semester){
        courseCode = code;
        semesterTaken = semester;
        individualRating = "";
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
