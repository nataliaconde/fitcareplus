package br.com.fitcareplus;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

public class LoginScreen extends AppCompatActivity {

  Button btnLogin;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_login_screen);

    SharedPreferences saved_values = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
    final SharedPreferences.Editor editor=saved_values.edit();

    final TextView username = (TextView) findViewById(R.id.edtUsername);
    final TextView password = (TextView) findViewById(R.id.edtPassword);

    Button btnForgotPassword = (Button) findViewById(R.id.txtForgotPassword);
    ImageView btnAboutUs = (ImageView) findViewById(R.id.btnAboutUs);

    btnAboutUs.setOnClickListener(new View.OnClickListener() {

      @Override
      public void onClick(View v) {
        Uri uri = Uri.parse(getString(R.string.back4app_server_url)); // missing 'http://' will cause crashed
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
      }
    });

    btnForgotPassword.setOnClickListener(new View.OnClickListener() {

      @Override
      public void onClick(View v) {
        Intent i = new Intent(LoginScreen.this, ResetPassword.class);
        startActivity(i);
      }
    });

    btnLogin = findViewById(R.id.btnLogin);

    btnLogin.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        btnLogin.setEnabled(false);
        btnLogin.setBackgroundColor(getResources().getColor(R.color.colorGrey));
        btnLogin.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        if(!isEmptyString(username) && !isEmptyString(password)){
          ParseUser.logInInBackground(username.getText().toString(), password.getText().toString(), new LogInCallback() {
            public void done(ParseUser user, ParseException e) {
              if (user != null) {
                editor.putString("sessiontoken", user.getSessionToken());

                if(user.getUsername() != null) editor.putString("username", user.getUsername());
                if(user.getEmail() != null) editor.putString("useremail", user.getEmail());

                if(user.getParseObject("user") != null) {
                  try {
                    ParseObject pointerUser = (ParseObject) user.get("user");
                    ParseFile url = pointerUser.fetchIfNeeded().getParseFile("file");
                    editor.putString("userfile", String.valueOf(url));
                  } catch (ParseException error) { }  }

                editor.commit();
                String profileType = user.get("profile").toString().trim();

                if(profileType.equals("pacient")){
                  Intent i;
                  i = new Intent(LoginScreen.this, PacientDetail.class);
                  i.putExtra("name", user.getUsername());
                  startActivity(i);
                } else {
                  Intent i;
                  i = new Intent(LoginScreen.this, DoctorView.class);
                  startActivity(i);
                }

              } else {
                Toast.makeText(LoginScreen.this, getString(R.string.errorLoginParse), Toast.LENGTH_LONG).show();
              }
              btnLogin.setEnabled(true);
              btnLogin.setBackgroundColor(getResources().getColor(R.color.colorAccent));
            }
          });
        }

      }
    });

  }

  public boolean isEmptyString(TextView txtView) {
    String text = txtView.getText().toString();
    if (text == null || text.trim().equals("null") || text.trim().length() <= 0){
      txtView.setError(getString(R.string.edtViewError));
      btnLogin.setEnabled(true);
      return true;
    } else {
      btnLogin.setEnabled(true);
      return false;
    }

  }
}
