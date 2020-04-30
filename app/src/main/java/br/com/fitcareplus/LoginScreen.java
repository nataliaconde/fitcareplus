package br.com.fitcareplus;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

public class LoginScreen extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_login_screen);

    final TextView username = (TextView) findViewById(R.id.edtUsername);
    final TextView password = (TextView) findViewById(R.id.edtPassword);

    TextView txtForgotPassword = (TextView) findViewById(R.id.txtForgotPassword);
    txtForgotPassword.setOnClickListener(new View.OnClickListener() {

      @Override
      public void onClick(View v) {
        Intent i = new Intent(LoginScreen.this, ResetPassword.class);
        startActivity(i);
      }
    });

    final Button btnLogin = (Button) findViewById(R.id.btnLogin);

    btnLogin.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        if(!isEmptyString(username) && !isEmptyString(password)){
          ParseUser.logInInBackground(username.getText().toString(), password.getText().toString(), new LogInCallback() {
            public void done(ParseUser user, ParseException e) {
              if (user != null) {
                Intent i = new Intent(LoginScreen.this, PacientView.class);
                startActivity(i);
              } else {
                Toast.makeText(LoginScreen.this, getString(R.string.errorLoginParse), Toast.LENGTH_LONG).show();
              }
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
      return true;
    } else {
      return false;
    }
  }
}
