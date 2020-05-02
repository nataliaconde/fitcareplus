package br.com.fitcareplus;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;

import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;

import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.InputStream;
import java.net.URL;

public class BaseActivity extends AppCompatActivity
  implements NavigationView.OnNavigationItemSelectedListener {

  @Override
  protected void onCreate(Bundle savedInstanceState) {

    super.onCreate(savedInstanceState);

    SharedPreferences saved_values = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

    setContentView(R.layout.activity_base);
    Toolbar toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    DrawerLayout drawer = findViewById(R.id.drawer_layout);
    NavigationView navigationView = findViewById(R.id.nav_view);
    ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
      this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
    drawer.addDrawerListener(toggle);
    toggle.syncState();
    navigationView.setNavigationItemSelectedListener(this);

    TextView userName = (TextView) navigationView.getHeaderView(0).findViewById(R.id.userName);
    TextView userEmail = (TextView) navigationView.getHeaderView(0).findViewById(R.id.userEmail);
    ImageView userPhoto = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.userImage);

    setUserValues(userName, userEmail, userPhoto, saved_values);
  }

  private void setUserValues(final TextView userName, final TextView userEmail, final ImageView userPhoto, final SharedPreferences saved_values) {
    if (saved_values.getString("username", "") == null){
      changeActivity(LoginScreen.class);
    } else {
      ParseUser.becomeInBackground(saved_values.getString("sessiontoken", "") , new LogInCallback() {
        public void done(ParseUser user, ParseException e) {
          if (user != null) {
            if(saved_values.getString("username", "") != null) userName.setText(user.getUsername());
            if(saved_values.getString("useremail", "") != null) userEmail.setText(user.getEmail());

            if(user.getParseObject("user") != null) {
              try {
                ParseObject pointerUser = (ParseObject) user.get("user");
                ParseFile url = pointerUser.fetchIfNeeded().getParseFile("file");
                new DownLoadImageTask(userPhoto).execute(url.getUrl());
              } catch (ParseException error) {  }  }
          } else {
            changeActivity(LoginScreen.class);
          }
        }
      });
    }
  }

  @Override
  public void onBackPressed() {
    DrawerLayout drawer = findViewById(R.id.drawer_layout);
    if (drawer.isDrawerOpen(GravityCompat.START)) {
      drawer.closeDrawer(GravityCompat.START);
    } else {
      super.onBackPressed();
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.main, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();

    return super.onOptionsItemSelected(item);
  }

  @SuppressWarnings("StatementWithEmptyBody")
  @Override
  public boolean onNavigationItemSelected(MenuItem item) {
    String admin = "pacient";
    int id = item.getItemId();
    if (id == R.id.nav_home) {
      if(admin.equals("admin")){
        changeActivity(DoctorView.class);
      } else {
        changeActivity(PacientDetail.class);
      }
    } else if (id == R.id.nav_user_details) {
      changeActivity(UserDetail.class);
    } else if (id == R.id.nav_send) {
      changeActivity(Contact.class);
    } else if (id == R.id.nav_connections) {
      changeActivity(Connection.class);
    }

    DrawerLayout drawer = findViewById(R.id.drawer_layout);
    drawer.closeDrawer(GravityCompat.START);
    return true;
  }

  public void changeActivity(Class classExpected){
    Intent intent = new Intent(BaseActivity.this, classExpected);
    startActivity(intent);
  }

  private class DownLoadImageTask extends AsyncTask<String,Void,Bitmap> {
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
