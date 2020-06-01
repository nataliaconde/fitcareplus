package br.com.fitcareplus;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;

public class UserDetail extends BaseActivity {
    private  static final int PICK_IMAGE = 100;
    Uri imageUri;
    ImageView imageView;
    EditText txtName;
    EditText txtEmail;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // don’t set any content view here, since its already set in DrawerActivity
        FrameLayout frameLayout = (FrameLayout)findViewById(R.id.activity_frame);
        // inflate the custom activity layout
        LayoutInflater layoutInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View activityView = layoutInflater.inflate(R.layout.activity_user_detail, null,false);
        // add the custom layout of this activity to frame layout.
        frameLayout.addView(activityView);
        // now you can do all your other stuffs

        this.getSupportActionBar().setTitle(getString(R.string.menu_user));
        Button updatePhoto = (Button) findViewById(R.id.btnUpdatePhoto);
        final Button updateUserDetails = (Button) findViewById(R.id.btnUpdateData);
        imageView = (ImageView) findViewById(R.id.imgUser);
        txtName = (EditText) findViewById(R.id.edtUsername);
        txtEmail = (EditText) findViewById(R.id.edtEmail);

        final ParseUser user = ParseUser.getCurrentUser();

        if(user.getParseObject("user") != null) {

            try {
                ParseObject pointerUser = (ParseObject) user.get("user");
                ParseFile url = pointerUser.fetchIfNeeded().getParseFile("file");
                if (url != null)  new DownLoadImageTask(imageView).execute(url.getUrl());

            } catch (ParseException error) { }
            if(user.getEmail() != null) txtEmail.setText(user.getEmail());
            if(user.getUsername() != null) txtName.setText(user.getUsername());
        }

        updatePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });
        updateUserDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUserDetails.setBackgroundColor(getResources().getColor(R.color.colorGrey));
                updateUserDetails.setEnabled(false);
                updateUserDetails.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                convertDataToArray(user.getObjectId(), updateUserDetails);

            }
        });

    }

    private void convertDataToArray(final String userId, final Button updateUserDetails) {
        BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
        Bitmap bitmap = drawable.getBitmap();

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
        byte[] bitmapBytes = stream.toByteArray();

        final ParseFile image = new ParseFile("userImage.png", bitmapBytes);

        image.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    HashMap<String, Object> params = new HashMap<String, Object>();
                    params.put("parsefile", image);
                    params.put("name", txtName.getText().toString());
                    params.put("email", txtEmail.getText().toString());
                    params.put("objectId", userId);
                    ParseCloud.callFunctionInBackground("uploadImage", params, new FunctionCallback<String>() {
                        public void done(String result, ParseException e) {
                            if (e == null) {
                                ImageView userImage = (ImageView) findViewById(R.id.userImage);
                                TextView userName = (TextView) findViewById(R.id.userName);
                                TextView userEmail = (TextView) findViewById(R.id.userEmail);
                                new DownLoadImageTask(userImage).execute(image.getUrl());
                                userName.setText(txtName.getText().toString());
                                userEmail.setText(txtEmail.getText().toString());
                                Toast.makeText(UserDetail.this,
                                        result,
                                        Toast.LENGTH_LONG).show();
                                updateUserDetails.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                                updateUserDetails.setTextColor(getResources().getColor(R.color.colorBlank));
                                updateUserDetails.setEnabled(true);
                            }
                        }
                    });

                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    public void openGallery(){
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        gallery.setType("image/*");
        String[] mimeTypes = {"image/jpeg", "image/png"};
        gallery.putExtra(Intent.EXTRA_MIME_TYPES,mimeTypes);
        startActivityForResult(gallery, PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && requestCode == PICK_IMAGE){
            imageUri = data.getData();
            imageView.setImageURI(imageUri);
        }
    }

    private void alertDisplayer(String title, String message){
        @SuppressLint("ResourceType") AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message);
        AlertDialog ok = builder.create();
        ok.show();
    }

    private class DownLoadImageTask extends AsyncTask<String,Void, Bitmap> {
        ImageView imageView;

        public DownLoadImageTask(ImageView imageView){
            this.imageView = imageView;
        }

        protected Bitmap doInBackground(String...urls){
            String urlOfImage = urls[0];
            Bitmap logo = null;
            try{
                InputStream is = new URL(urlOfImage).openStream();
                /*
                    decodeStream(InputStream is)
                        Decode an input stream into a bitmap.
                 */
                logo = BitmapFactory.decodeStream(is);
            }catch(Exception e){ // Catch the download exception
                e.printStackTrace();
            }
            return logo;
        }

        /*
            onPostExecute(Result result)
                Runs on the UI thread after doInBackground(Params...).
         */
        protected void onPostExecute(Bitmap result){
            imageView.setImageBitmap(result);
        }
    }

}
