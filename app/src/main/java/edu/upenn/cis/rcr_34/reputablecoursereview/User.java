package edu.upenn.cis.rcr_34.reputablecoursereview;

import android.util.Log;

import com.parse.FindCallback;
import com.parse.ParseException;
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
    private ArrayList<Course> courses;
    private boolean isObjectReady;

    public User(final String email){
        this.email = email;
        this.liteners = new ArrayList<ParseDataReceivedNotifier>();
        this.isObjectReady = false;

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
                        pendingRequests = (ArrayList) user.getList("pendingRequest");
                        isObjectReady = true;
                        notifyListeners();
                    } else {
                    }
                }
            }
        });
    }

    public String getName(){
        if (isObjectReady){
            this.name = this.firstName.substring(0, 1).toUpperCase() + this.firstName.substring(1);
            this.name += " " + this.lastName.substring(0, 1).toUpperCase() + this.lastName.substring(1);
            return this.name;
        }
        else{
            return "INVALID_RETURN_OBJECT";
        }
    }

    public String getEmail(){
        if(isObjectReady){
            return email;
        }
        else{
            return "INVALID_RETURN_OBJECT";
        }
    }

    public String getYear(){
        if(isObjectReady){
            return year;
        }
        else{
            return "INVALID_RETURN_OBJECT";
        }
    }

    public String getMajor(){
        if(isObjectReady){
            return major;
        }
        else{
            return "INVALID_RETURN_OBJECT";
        }
    }

    public String getProfilePic(){
        if(isObjectReady){
            return picLink;
        }
        else{
            return "INVALID_RETURN_OBJECT";
        }
    }

    public ArrayList<String> getFriends(){
        if(isObjectReady){
            return this.friendEmails;
        }
        else{
            return null;
        }
    }

    public ArrayList<String> getPendingRequests(){
        if(isObjectReady){
            return this.pendingRequests;
        }
        else{
            return null;
        }
    }

    public void acceptRequest(final String email){
        if (!this.isObjectReady)
            return;

        if (friendEmails == null){
            friendEmails = new ArrayList<String>();
        }

        if (this.friendEmails.contains(email) || !this.pendingRequests.contains(email)) {
            Log.d("PARSE", email + " is already your friend or is not found in pending list");
        }
        else {
            this.pendingRequests.remove(email);
            this.friendEmails.add(email);

            this.me.put("friends", friendEmails);
            this.me.put("pendingRequest", this.pendingRequests);
            this.me.saveInBackground(new SaveCallback() {
                public void done(ParseException e) {
                    if (e != null) {
                        Log.d("PARSE", "Friends saved properly");
                    } else {
                        Log.d("USER.JAVA", "Friends couldn't be saved properly: " + email);
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
                            if (friends == null){
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

    public void sendRequest(final String email){
        if (!this.isObjectReady)
            return;

        if (this.friendEmails != null && this.friendEmails.contains(email)) {
            Log.d("PARSE", email + " is already your friend");
        }
        else {
            ParseQuery<ParseUser> query = ParseUser.getQuery();
            query.whereEqualTo("email", email);
            query.findInBackground(new FindCallback<ParseUser>() {
                @Override
                public void done(List list, com.parse.ParseException e) {
                    if (e == null) {
                        if (list.size() > 0) {
                            final ParseUser user = (ParseUser) list.get(0);
                            ArrayList<String> pendingEmails = (ArrayList) user.getList("pendingRequest");
                            if (pendingEmails == null){
                                pendingEmails = new ArrayList<String>();
                            }

                            if (pendingEmails.contains(me.getEmail())){
                                Log.d("PARSE", "Friends request already sent");
                            }
                            else{
                                pendingEmails.add(me.getEmail());
                                user.put("pendingRequest", pendingEmails);
                                user.saveInBackground(new SaveCallback() {
                                    public void done(ParseException e) {
                                        if (e != null) {
                                            Log.d("PARSE", "Friends request sent to: " + user.getEmail());
                                        } else {
                                            Log.d("USER.JAVA", "Friends request couldn't be sent: " + email);
                                        }
                                    }
                                });
                            }

                        }
                        else {
                            Log.d("PARSE", "Friends request couldn't be sent: " + email);
                            Log.d("PARSE", "Error : User nor found!");
                        }
                    }
                    else {
                        Log.d("PARSE", "Friends request couldn't be sent: " + email);
                        Log.d("PARSE", "Error : " + e.getMessage());
                    }
                }
            });
        }
    }

    public void removeFriend(final String email){
        if (!this.isObjectReady)
            return;

        if (friendEmails == null){
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

    public void notifyListeners(){
        if (isObjectReady){
            for (ParseDataReceivedNotifier l : this.liteners){
                l.notifyListener();
            }
        }
    }

    public void addListener(ParseDataReceivedNotifier l){
        this.liteners.add(l);
        if (isObjectReady){
            l.notifyListener();
        }
    }
}