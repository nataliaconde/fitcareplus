package br.com.fitcareplus;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.RequestPasswordResetCallback;

import java.util.regex.Pattern;

public class ResetPassword extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        final Button btnForgotPassword = (Button) findViewById(R.id.btnUpdateData);
        final EditText edtEmail = (EditText) findViewById(R.id.edtEmail);

        btnForgotPassword.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                btnForgotPassword.setEnabled(false);
                btnForgotPassword.setBackgroundColor(getResources().getColor(R.color.colorGrey));
                if(!validEmail(edtEmail)) {
                    ParseUser.requestPasswordResetInBackground(edtEmail.getText().toString(), new RequestPasswordResetCallback() {
                        public void done(ParseException e) {
                            if (e == null) {
                                alertDisplayer(getString(R.string.validResetPassword), getString(R.string.textEmailVerificationSent));
                            } else {
                                alertDisplayer(getString(R.string.invalidResetPassword), getString(R.string.textEmailVerificationNotSent));
                            }
                            btnForgotPassword.setEnabled(true);
                            btnForgotPassword.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                        }
                    });

                }
            }
        });
    }
    public boolean validEmail(TextView txtView) {
        String text = txtView.getText().toString();
        if (text == null || text.trim().equals("null") || text.trim().length() <= 0){
            txtView.setError(getString(R.string.edtViewError));
            return true;
        } else {
            if (!checkEmail(txtView.getText().toString())){
                txtView.setError(getString(R.string.edtEmailIncorrectFormat));
                return true;
            } else {
                return false;
            }
        }
    }

    private void alertDisplayer(String title, String message){
        @SuppressLint("ResourceType") AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setNeutralButton(getString(R.string.positiveFeedback), new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        Intent intent = new Intent(ResetPassword.this, LoginScreen.class);
                        startActivity(intent);
                        finish();
                    }
                });
        AlertDialog ok = builder.create();
        ok.show();
    }

    public static final Pattern EMAIL_ADDRESS_PATTERN = Pattern.compile(
            "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                    "\\@" +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                    "(" +
                    "\\." +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                    ")+"
    );


    private boolean checkEmail(String email) {
        return EMAIL_ADDRESS_PATTERN.matcher(email).matches();
    }
}
