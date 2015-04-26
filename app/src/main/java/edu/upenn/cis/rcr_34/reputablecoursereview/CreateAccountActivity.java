package edu.upenn.cis.rcr_34.reputablecoursereview;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import com.parse.FindCallback;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.List;


public class CreateAccountActivity extends ActionBarActivity {
    private static int RESULT_LOAD_IMAGE = 1;
    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ParseAPI.init(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
    }

    /*
    * This method will help choosing a profile image from gallery
    * */
    public void selectImageClicked(View c){
        String path = Environment.getExternalStorageDirectory()
                + "/images/imagename.jpg";
        Intent i = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, RESULT_LOAD_IMAGE);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK
                && null != data) {

            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            bitmap = BitmapFactory.decodeFile(picturePath);

        } else {

            Log.i("SonaSys", "resultCode: " + resultCode);
            switch (resultCode) {
                case 0:
                    Log.i("SonaSys", "User cancelled");
                    break;
            }

        }

    }


    public void registerClicked(View v) {

        EditText firstNameField = (EditText) findViewById(R.id.createaccount_firstname);
        EditText lastNameField = (EditText) findViewById(R.id.createaccount_lastname);
        EditText emailField = (EditText) findViewById(R.id.createaccount_email);
        EditText yearField = (EditText) findViewById(R.id.createaccount_year);
        EditText majorField = (EditText) findViewById(R.id.createaccount_major);
        EditText passwordField = (EditText) findViewById(R.id.createaccount_password);
        EditText passwordConfirmField = (EditText) findViewById(R.id.createaccount_passwordconfirmation);


        String firstName = firstNameField.getText().toString();
        String lastName = lastNameField.getText().toString();
        String email = emailField.getText().toString();
        String year = yearField.getText().toString();
        String major = majorField.getText().toString();
        String password = passwordField.getText().toString();
        String passwordConfirm = passwordConfirmField.getText().toString();

        //Make sure valid email used and the email is from upenn
        if(!email.equals("")){
            String[] emailSplitatAt = email.split("@");
            if (emailSplitatAt.length < 2){
                Toast.makeText(getApplicationContext(), "Please enter a valid email!",
                        Toast.LENGTH_SHORT).show();
            }
            else{
                String[] emailSplitAtDot = emailSplitatAt[1].split("\\.");
                int length = emailSplitAtDot.length;

                if (length < 2 || !emailSplitAtDot[length - 1].equals("edu") || !emailSplitAtDot[length - 2].equals("upenn")){
                    Toast.makeText(getApplicationContext(), "Only Penn emails are accepted!",
                            Toast.LENGTH_SHORT).show();
                }
            }
        }

        if (email.equals("")){
            Toast.makeText(getApplicationContext(), "Please Enter Email",
                    Toast.LENGTH_SHORT).show();
        }
        else if (firstName.equals("")){
            Toast.makeText(getApplicationContext(), "Please Enter First Name",
                    Toast.LENGTH_SHORT).show();
        } else if (lastName.equals("")){
            Toast.makeText(getApplicationContext(), "Please Enter Last Name",
                    Toast.LENGTH_SHORT).show();
        }else if (major.equals("")){
            Toast.makeText(getApplicationContext(), "Please Enter Major",
                    Toast.LENGTH_SHORT).show();
        }  else if (year.equals("")){
            Toast.makeText(getApplicationContext(), "Please Enter Year of Graduation",
                    Toast.LENGTH_SHORT).show();
        }
        else{
            // Check if password matches
            if (password.length() < 5) {
                Toast.makeText(getApplicationContext(),
                        "Password should be at least 5 characters long", Toast.LENGTH_SHORT).show();
                passwordField.setText("");
                passwordConfirmField.setText("");
            }
            else if (!password.equals(passwordConfirm)) {
                Toast.makeText(getApplicationContext(), "Password doesn't match!",
                        Toast.LENGTH_SHORT).show();
                passwordField.setText("");
                passwordConfirmField.setText("");
            }else {
                createNewUser(firstName, lastName, email, major, year, password);
            }
        }
    }

    public void cancelClicked(View v) {
        Intent i = new Intent();
        setResult(RESULT_OK, i);
        finish();
    }

    private void createNewUser(String firstName, String lastName, final String email, String major,
                               String year, final String password) {
        final ParseUser user = new ParseUser();
        user.put("firstName", firstName);
        user.put("lastName", lastName);
        String userName = firstName + " " + lastName;
        userName = userName.toUpperCase();
        user.put("userName", userName);

        user.put("major", major);
        user.put("year", year);

        user.setEmail(email);
        user.setUsername(email);
        user.setPassword(password);

        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("email", email);
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List list, com.parse.ParseException e) {
                if (e == null) {
                    if (list.size() > 0) {
                        Toast.makeText(getApplicationContext(), "User Already Exists!",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        user.signUpInBackground(new SignUpCallback() {
                            @Override
                            public void done(com.parse.ParseException e) {
                                // Now upload image
                                if (bitmap != null){
                                    //Save profile image
                                    int bitmapSize = bitmap.getByteCount();

                                    ByteBuffer buffer = ByteBuffer.allocate(bitmapSize); //Create a new buffer
                                    bitmap.copyPixelsToBuffer(buffer); //Move the byte data to the buffer

                                    byte[] bitmapArray = buffer.array();

                                    ParseFile profilePic = new ParseFile(email + "_profilepic",bitmapArray);
                                    user.put("profilePic", profilePic);
                                    user.saveInBackground();
                                }

                                Toast.makeText(getApplicationContext(), "Account Created!",
                                        Toast.LENGTH_SHORT).show();
                                Intent i = new Intent();
                                setResult(RESULT_OK, i);
                                finish();
                            }
                        });
                    }
                }
            }
        });
    }
}