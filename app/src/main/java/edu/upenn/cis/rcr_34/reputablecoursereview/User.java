package edu.upenn.cis.rcr_34.reputablecoursereview;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by sagar on 4/1/15.
 */
public class User {
    private static HashMap<String, User> allUsers;
    private ParseUser me;
    private String email;
    private String name;
    private Bitmap profilePic;
    private String firstName;
    private String lastName;
    private String year;
    private String major;
    private ArrayList<String> friendEmails;
    private ArrayList<String> pendingRequests;
    private ArrayList<ParseDataReceivedNotifier> liteners;
    private ArrayList<CoursesTaken> coursesTaken;
    private ArrayList<String> plannedCourses;
    private boolean isObjectReady;

    // user creation
    public User(final String email) {
        this.email = email;
        this.liteners = new ArrayList<ParseDataReceivedNotifier>();
        this.isObjectReady = false;

        // finds user
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("email", email);
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List list, com.parse.ParseException e) {
                if (e == null) {
                    if (list.size() > 0) {
                        ParseUser user = (ParseUser) list.get(0);
                        me = user;
                        year = (String) user.get("year");
                        firstName = (String) user.get("firstName");
                        lastName = (String) user.get("lastName");
                        major = (String) user.get("major");
                        friendEmails = (ArrayList) user.getList("friends");
                        ParseFile imgFile = (ParseFile) user.get("profilePic");
                        try {
                            byte[] data = imgFile.getData();
                            profilePic = BitmapFactory.decodeByteArray(data, 0, data.length);
                        } catch (ParseException e1) {
                            e1.printStackTrace();
                        }

                        // retrieve pending request
                        ParseQuery<ParseObject> friendRequest = new ParseQuery<ParseObject>("pendingFriendRequest");
                        friendRequest.whereEqualTo("sentTo", me.getEmail());
                        friendRequest.whereEqualTo("accepted", false);
                        friendRequest.findInBackground(new FindCallback<ParseObject>() {
                            @Override
                            public void done(List list, com.parse.ParseException e) {
                                if (e == null) {
                                    if (list.size() > 0) {
                                        pendingRequests = new ArrayList<String>();
                                        List<ParseObject> pList =  (List<ParseObject>) list;
                                        for (int i = 0; i < pList.size(); i++) {
                                            ParseObject po = pList.get(i);
                                            pendingRequests.add((String) po.get("sentBy"));
                                        }
                                    }
                                }
                                isObjectReady = true;
                                notifyListeners();
                            }
                        });
                        plannedCourses = (ArrayList) user.getList("plannedCourses");
                    }
                }
            }
        });
    }

    // finds username of user
    public String getName() {
        if (isObjectReady) {
            this.name = this.firstName.substring(0, 1).toUpperCase() + this.firstName.substring(1);
            this.name += " " + this.lastName.substring(0, 1).toUpperCase() + this.lastName.substring(1);
            return this.name;
        } else {
            return "INVALID_RETURN_OBJECT";
        }
    }

    // finds email of user
    public String getEmail() {
        if (isObjectReady) {
            return email;
        } else {
            return "INVALID_RETURN_OBJECT";
        }
    }

    // finds year of user
    public String getYear() {
        if (isObjectReady) {
            return year;
        } else {
            return "INVALID_RETURN_OBJECT";
        }
    }

    // finds major of user
    public String getMajor() {
        if (isObjectReady) {
            return major;
        } else {
            return "INVALID_RETURN_OBJECT";
        }
    }

    /*
    * This will set the profile image once loaded
    * */
    public Bitmap getProfileImage(){
        if (isObjectReady){
            return this.profilePic;
        }
        return null;
    }


    // finds user's courses taken
    public void retrieveCoursesTaken(final ParseDataReceivedNotifier not) {
        //Retrieve courses taken
        ParseQuery<ParseObject> cT = ParseQuery.getQuery("coursesTaken");
        cT.whereEqualTo("userEmail", email);
        cT.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> courses, ParseException e) {
                if (e == null) {
                    coursesTaken = new ArrayList<CoursesTaken>();
                    if (courses.size() > 0) {
                        for (ParseObject ob : courses) {
                            String courseCode = (String) ob.get("courseName");
                            String sem = (String) ob.get("semester");
                            String year = (String) ob.get("year");
                            int rating = (int) ob.get("rating");
                            coursesTaken.add(new CoursesTaken(email, courseCode, sem, year, rating));
                        }
                    }
                    not.notifyListener();
                }
            }
        });
    }

    public ArrayList<CoursesTaken> getCoursesTaken() {
        if (coursesTaken == null) {
            System.err.println("Should retrive courses taken before getting courses taken");
            return null;
        } else {
            return this.coursesTaken;
        }
    }

    // finds users friends
    public ArrayList<String> getFriends() {
        if (isObjectReady) {
            return this.friendEmails;
        } else {
            return null;
        }
    }

    // finds users pending requests
    public ArrayList<String> getPendingRequests() {
        if (isObjectReady) {
            return this.pendingRequests;
        } else {
            return null;
        }
    }

    // accepts request
    public void acceptRequest(final String sentBy, final ParseDataReceivedNotifier listener) {
        if (!this.isObjectReady)
            return;

        if (friendEmails == null) {
            friendEmails = new ArrayList<String>();
        }

        if (!friendEmails.contains(sentBy)){
            // Only add to friends list of not already a friend
            this.friendEmails.add(sentBy);
        }

        //set accepted to true in pending list
        ParseQuery<ParseObject> setAcceptedInParse = new ParseQuery<ParseObject>("pendingFriendRequest");
        setAcceptedInParse.whereEqualTo("sentBy", sentBy);
        setAcceptedInParse.whereEqualTo("sentTo", this.email);
        setAcceptedInParse.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List list, com.parse.ParseException e) {
                if (e == null) {
                    if (list.size() > 0) {
                        ParseObject pendingRequest = (ParseObject) list.get(0);
                        pendingRequest.put("accepted", true);
                        pendingRequest.saveInBackground(new SaveCallback() {
                            public void done(ParseException e) {
                                if (e == null) {
                                    listener.notifyListener();
                                }
                            }
                        });
                    }
                }
            }
        });

        // update parse with new friends list
        this.me.put("friends", friendEmails);
        this.me.saveInBackground(new SaveCallback() {
            public void done(ParseException e) {
                if (e != null) {
                    Log.d("PARSE", "Friends saved properly");
                } else {
                    Log.d("USER.JAVA", "Friends couldn't be saved properly: " + sentBy);
                }
            }
        });
    }

    //reject friend request
    public void rejectRequest(final String sentBy) {
        if (!this.isObjectReady)
            return;

        final ParseQuery<ParseObject> friendRequest = new ParseQuery<ParseObject>("pendingFriendRequest");
        friendRequest.whereEqualTo("sentTo", me.getEmail());
        friendRequest.whereEqualTo("sentBy", sentBy);
        friendRequest.whereEqualTo("accepted", false);
        friendRequest.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List list, com.parse.ParseException e) {
                if (e == null) {
                    if (list.size() > 0) {
                        for (ParseObject po : (List<ParseObject>) list) {
                            // Drop request
                            po.deleteInBackground();
                        }
                    }
                }
            }
        });
    }

    // send friend request
    public void sendRequest(final String sendTo) {
        if (!this.isObjectReady)
            return;

        if (me.getEmail().equals(sendTo)){
            //Cant sent request to yourself
            return;
        }

        if (this.friendEmails != null && this.friendEmails.contains(sendTo)) {
            // He/she is already user's friend
        } else {
            ParseQuery<ParseObject> friendRequest = new ParseQuery<ParseObject>("pendingFriendRequest");
            friendRequest.whereContains("sentTo", sendTo);
            friendRequest.whereContains("sentBy", this.email);
            friendRequest.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List list, com.parse.ParseException e) {
                    if (e == null) {
                        if (list.size() <= 0) {
                            ParseObject sendRequest = new ParseObject("pendingFriendRequest");
                            sendRequest.put("sentBy", email);
                            sendRequest.put("sentTo", sendTo);
                            sendRequest.put("accepted", false);
                            sendRequest.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if (e == null) {
                                        Log.d("USER.JAVA", "Request sent");
                                    } else {
                                        Log.d("USER.JAVA", "Request not sent" + email);
                                    }
                                }
                            });
                        }
                    }
                }
            });
        }
    }

    public void checkIfFriendRequestIsAccepted(){
        // retrieve pending request
        final ParseQuery<ParseObject> friendRequest = new ParseQuery<ParseObject>("pendingFriendRequest");
        friendRequest.whereEqualTo("sentBy", me.getEmail());
        friendRequest.whereEqualTo("accepted", true);
        friendRequest.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List list, com.parse.ParseException e) {
                if (e == null) {
                    if (list.size() > 0) {
                        for (ParseObject po : (List<ParseObject>) list) {
                            String fr = (String) po.get("sentTo");
                            if (!friendEmails.contains(fr)){
                                friendEmails.add(fr);
                            }
                            po.deleteInBackground();
                        }

                        // update parse with new friends list
                        me.put("friends", friendEmails);
                        me.saveInBackground(new SaveCallback() {
                            public void done(ParseException e) {
                                if (e == null) {
                                    // Friends saved properly
                                }
                            }
                        });
                    }
                }
            }
        });
    }

    // add course to courses taken
    public void addCourse(String course, String semester, String year) {
        if (!this.isObjectReady) {
            return;
        }

        ParseObject courseTaken = new ParseObject("coursesTaken");
        courseTaken.put("userEmail", email);
        courseTaken.put("courseName", course);
        courseTaken.put("semester", semester);
        courseTaken.put("year", year);
        courseTaken.put("rating", -1);
        courseTaken.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.d("PARSE", "Course added properly");
                }
            }
        });
    }

    public boolean hasPlannedCourse(ParseObject course){
        updatePlannedCourses();
        String courseId = course.getObjectId();
        if(plannedCourses != null) {
            return plannedCourses.contains(courseId);
        }
        else{
            return false;
        }
    }

    private void updatePlannedCourses(){
        plannedCourses = (ArrayList<String>)me.get("plannedCourses");
    }

    // add planned course
    public void planCourse(final String course) {
        updatePlannedCourses();
        if (!this.isObjectReady) {
            return;
        }
        if (plannedCourses == null) {
            plannedCourses = new ArrayList<String>();
        }
        if (!this.plannedCourses.contains(course)) {
            this.plannedCourses.add(course);
            this.me.put("plannedCourses", plannedCourses);
            this.me.saveInBackground(new SaveCallback() {
                public void done(ParseException e) {
                    if (e != null) {
                        Log.d("PARSE", "Course added properly");
                    } else {
                        Log.d("USER.JAVA", "Course couldn't be added properly: " + email);
                    }
                }
            });
        }
    }

    // remove course from planned list
    public void unplanCourse(final String course) {
        updatePlannedCourses();
        if (!this.isObjectReady) {
            return;
        }
        if (plannedCourses == null) {
            plannedCourses = new ArrayList<String>();
        }
        // find course
        if (this.plannedCourses.contains(course)) {
            this.plannedCourses.remove(course);
            this.me.put("plannedCourses", plannedCourses);
            this.me.saveInBackground(new SaveCallback() {
                public void done(ParseException e) {
                    if (e != null) {
                        Log.d("PARSE", "Course removed properly");
                    } else {
                        Log.d("USER.JAVA", "Course couldn't be removed properly: " + email);
                    }
                }
            });
        }
    }

    // remove friend from list
    public void removeFriend(final String email) {
        if (!this.isObjectReady)
            return;
        // no email provided
        if (friendEmails == null) {
            Log.d("PARSE", "Friends couldn't be removed");
        }

        // find friend and try to remove
        if (this.friendEmails.contains(email)) {
            this.friendEmails.remove(email);
            this.me.put("friends", friendEmails);
            this.me.saveInBackground(new SaveCallback() {
                public void done(ParseException e) {
                    if (e != null) {
                        Log.d("PARSE", "Friends removed properly");
                    } else {
                        Log.d("USER.JAVA", "Friends couldn't be removed properly: " + email);
                    }
                }
            });
        }
    }

    public void notifyListeners() {
        if (isObjectReady) {
            for (ParseDataReceivedNotifier l : this.liteners) {
                l.notifyListener();
            }
        }
    }

    public void addListener(ParseDataReceivedNotifier l) {
        this.liteners.add(l);
        if (isObjectReady) {
            l.notifyListener();
        }
    }
}