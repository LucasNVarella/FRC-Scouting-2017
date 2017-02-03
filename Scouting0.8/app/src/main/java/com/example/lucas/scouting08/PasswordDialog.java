package com.example.lucas.scouting08;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

public class PasswordDialog extends Activity
{

    EditText txtPassword;

    protected void onCreate(Bundle savedInstanceState) {
        // Auto generated code
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.password_dialog);

        txtPassword = (EditText) findViewById(R.id.txtPassword);
        txtPassword.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View view, MotionEvent motion) {
                if (txtPassword.getText().toString().length() == 5)
                {
                    finish();
                }
                return true;
            }
        });

    }
}