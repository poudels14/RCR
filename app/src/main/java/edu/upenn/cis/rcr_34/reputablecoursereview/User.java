package edu.upenn.cis.rcr_34.reputablecoursereview;

import android.util.Log;

import com.parse.FindCallback;
import com.parse.ParseException;
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
    private String firstName;
    private String lastName;
    private String picLink;
    private String year;
    private String major;
    private ArrayList<String> friendEmails;
    private ArrayList<String> pendingRequests;
    private ArrayList<ParseDataReceivedNotifier> liteners;
    private ArrayList<CoursesTaken> coursesTaken;
    private ArrayList<String> plannedCourses;
    private boolean isObjectReady;

    public User(final String email) {
        this.email = email;
        this.liteners = new ArrayList<ParseDataReceivedNotifier>();
        this.isObjectReady = false;

        // Retrieve user details
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
                        picLink = (String) user.get("profilePic");
                        friendEmails = (ArrayList) user.getList("friends");

                        // retrieve pending request
                        ParseQuery<ParseObject> friendRequest = new ParseQuery<ParseObject>("pendingFriendRequest");
                        friendRequest.whereEqualTo("sentTo", me.getEmail());
                        friendRequest.findInBackground(new FindCallback<ParseObject>() {
                            @Override
                            public void done(List list, com.parse.ParseException e) {
                                if (e == null) {
                                    if (list.size() > 0) {
                                        pendingRequests = new ArrayList<String>();
                                        for (ParseObject po : (List<ParseObject>) list){
                                            pendingRequests.add((String) po.get("sentBy"));
                                        }
                                    }
                                    isObjectReady = true;
                                    notifyListeners();
                                }
                            }
                        });
//                        plannedCourses = (ArrayList) user.getList("plannedCourses");

                    }
                }
            }
        });

        //Retrieve courses taken
        ParseQuery<ParseObject> coursesTaken = ParseQuery.getQuery("coursesTaken");
        coursesTaken.whereEqualTo("email", this.email);
        coursesTaken.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> courses, ParseException e) {
                if (e == null) {
                    if (courses.size() > 0) {
                        ArrayList<CoursesTaken> ct = new ArrayList<CoursesTaken>();
                        for (ParseObject ob : courses) {
                            String courseCode = (String) ob.get("courseName");
                            String sem = (String) ob.get("semester");
                            String year = (String) ob.get("year");
                            int rating = (int) ob.get("rating");
                            ct.add(new CoursesTaken(email, courseCode, sem, year, rating));
                        }
                    }
                }
            }
        });
    }

    public String getName() {
        if (isObjectReady) {
            this.name = this.firstName.substring(0, 1).toUpperCase() + this.firstName.substring(1);
            this.name += " " + this.lastName.substring(0, 1).toUpperCase() + this.lastName.substring(1);
            return this.name;
        } else {
            return "INVALID_RETURN_OBJECT";
        }
    }

    public String getEmail() {
        if (isObjectReady) {
            return email;
        } else {
            return "INVALID_RETURN_OBJECT";
        }
    }

    public String getYear() {
        if (isObjectReady) {
            return year;
        } else {
            return "INVALID_RETURN_OBJECT";
        }
    }

    public String getMajor() {
        if (isObjectReady) {
            return major;
        } else {
            return "INVALID_RETURN_OBJECT";
        }
    }

    public ArrayList<CoursesTaken> getCoursesTaken() {
        if (isObjectReady) {
            return this.coursesTaken;
        } else {
            return null;
        }
    }


    public String getProfilePic() {
        if (isObjectReady) {
            return picLink;
        } else {
            return "INVALID_RETURN_OBJECT";
        }
    }

    public ArrayList<String> getFriends() {
        if (isObjectReady) {
            return this.friendEmails;
        } else {
            return null;
        }
    }

    public ArrayList<String> getPendingRequests() {
        if (isObjectReady) {
            return this.pendingRequests;
        } else {
            return null;
        }
    }

    public void acceptRequest(final String sentBy) {
        if (!this.isObjectReady)
            return;

        if (friendEmails == null) {
            friendEmails = new ArrayList<String>();
        }

        if (this.friendEmails.contains(sentBy) || !this.pendingRequests.contains(sentBy)) {
            Log.d("PARSE", sentBy + " is already your friend or is not found in pending list");
            //Remove request from pending list
            ParseQuery<ParseObject> removeRequest = new ParseQuery<ParseObject>("pendingFriendRequest");
            removeRequest.whereEqualTo("sentBy", sentBy);
            removeRequest.whereEqualTo("sentTo", this.email);
            removeRequest.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> parseObjects, ParseException e) {
                    if (parseObjects.size() == 1){
                        parseObjects.get(0).deleteInBackground();
                    }
                }
            });
        } else {
            this.friendEmails.add(sentBy);

            //Remove request from pending list
            ParseObject removeRequest = new ParseObject("pendingFriendRequest");
            removeRequest.put("sentBy", sentBy);
            removeRequest.put("sentTo", this.email);
            removeRequest.deleteInBackground();

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

            //Now add this user to friend's friend list
            ParseQuery<ParseUser> query = ParseUser.getQuery();
            query.whereEqualTo("email", email);
            query.findInBackground(new FindCallback<ParseUser>() {
                @Override
                public void done(List list, com.parse.ParseException e) {
                    if (e == null) {
                        if (list.size() > 0) {
                            final ParseUser user = (ParseUser) list.get(0);
                            ArrayList<String> friends = (ArrayList) user.getList("friends");
                            if (friends == null) {
                                friends = new ArrayList<String>();
                            }
                            if (friends.contains(me.getEmail())) {
                                Log.d("PARSE", "how can I be friends alrady?");
                            } else {
                                friends.add(me.getEmail());
                                user.put("friends", friends);
                                user.saveInBackground(new SaveCallback() {
                                    public void done(ParseException e) {
                                        if (e != null) {
                                            Log.d("PARSE", "Friend added");
                                        } else {
                                            Log.d("USER.JAVA", "Friend couldn't be added: " + email);
                                        }
                                    }
                                });
                            }

                        }
                    }
                }
            });
        }
    }

    public void rejectRequest(final String email) {
        if (!this.isObjectReady)
            return;

        if (!this.pendingRequests.contains(email)) {
            Log.d("PARSE", email + " is not found in pending list");
        } else {
            this.pendingRequests.remove(email);

            this.me.put("pendingRequest", this.pendingRequests);
            this.me.saveInBackground(new SaveCallback() {
                public void done(ParseException e) {
                    if (e != null) {
                        Log.d("PARSE", "Pending friend request removed");
                    } else {
                        Log.d("USER.JAVA", "Pending friend request couldn't be removed : " + email);
                    }
                }
            });
        }
    }

    public void sendRequest(final String sendTo) {
        if (!this.isObjectReady)
            return;

        if (this.friendEmails != null && this.friendEmails.contains(sendTo)) {
            Log.d("PARSE", sendTo + " is already your friend");
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

    public void addCourse(final String course, final String semester, final String year) {
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
                } else {
                    Log.d("USER.JAVA", "Course couldn't be added properly: " + email);
                }
            }
        });
    }

    public void planCourse(final String course) {
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

    public void unplanCourse(final String course) {
        if (!this.isObjectReady) {
            return;
        }
        if (plannedCourses == null) {
            plannedCourses = new ArrayList<String>();
        }
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

    public void removeFriend(final String email) {
        if (!this.isObjectReady)
            return;

        if (friendEmails == null) {
            Log.d("PARSE", "Friends couldn't be removed");
        }

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