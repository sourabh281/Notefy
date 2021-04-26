package com.example.notefy;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.notefy.databinding.ActivityLoginBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity implements View.OnClickListener {

    private ActivityLoginBinding binding;
    private FirebaseAuth auth;

    enum State{
        SIGNUP , LOGIN
    }
    private State state;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        state = State.LOGIN;
        binding.linearLayoutForgotPassword.setVisibility(View.VISIBLE);

        binding.btnLoginOrSignUp.setOnClickListener(this);
      //  binding.txtResetPassword.setOnClickListener(this);
        binding.btnOneTimeLogin.setOnClickListener(this);

        auth = FirebaseAuth.getInstance();

        FirebaseUser firebaseUser = auth.getCurrentUser();
        if (firebaseUser != null) transferToMainActivity();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)  {

        getMenuInflater().inflate(R.menu.login_menu , menu);
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

       if (item.getItemId() == R.id.loginMenu){

           if (state == State.LOGIN){
               state = State.SIGNUP;
               item.setTitle("LOGIN");
               binding.btnLoginOrSignUp.setText(R.string.txtSignUpChange);
               binding.linearLayoutForgotPassword.setVisibility(View.INVISIBLE);

           }else if (state == State.SIGNUP){

               state = State.LOGIN;
               item.setTitle("SIGNUP");
               binding.btnLoginOrSignUp.setText(R.string.txtLoginChange);
               binding.linearLayoutForgotPassword.setVisibility(View.VISIBLE);

           }

       }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.btnLoginOrSignUp){

            if (state == State.SIGNUP){

                auth.createUserWithEmailAndPassword(binding.edtLoginEmail.getText().toString(),
                        binding.edtLoginPassword.getText().toString() )
                        .addOnCompleteListener(Login.this, task -> {

                            if (task.isSuccessful()){

                                Toast.makeText(Login.this, getString(R.string.txtWelcomeToNotefy), Toast.LENGTH_SHORT).show();
                                transferToMainActivity();

                            } else {

                                errorDialog();
                            }
                            binding.edtLoginEmail.getText().clear();
                            binding.edtLoginPassword.getText().clear();

                });

            } else if (state == State.LOGIN){

                auth.signInWithEmailAndPassword(binding.edtLoginEmail.getText().toString(),
                        binding.edtLoginPassword.getText().toString())
                        .addOnCompleteListener(this, task -> {

                            if (task.isSuccessful()){

                                Toast.makeText(Login.this , getString(R.string.txtWelcomeToNotefy), Toast.LENGTH_SHORT).show();
                                transferToMainActivity();

                            } else {

                                errorDialog();

                            }

                        });

            }

        } else if (view.getId() == R.id.btnOneTimeLogin){

            AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);

            builder.setTitle(getString(R.string.txtAnonymousAlertDialogTitle))
                    .setMessage(R.string.txtOneTimeLoginALertDialogMessege)
                    .setPositiveButton("ok", (dialog, which) -> auth.signInAnonymously().addOnCompleteListener(Login.this, task -> {
                        if (task.isSuccessful()) {

                            Toast.makeText(Login.this, getString(R.string.txtWelcomeToNotefy), Toast.LENGTH_SHORT).show();
                            transferToMainActivity();

                        } else {

                            errorDialog();
                        }
                    }))
                    .setNegativeButton("cancel", (dialog, which) -> dialog.dismiss());

            builder.show();
        }


    }

    private void errorDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);

        builder
                .setTitle("ERROR!")
                .setMessage("Please SIGNUP before LOGGING IN")
                .setPositiveButton("Try Again", (dialog, which) -> dialog.dismiss()).show();

    }

    private void transferToMainActivity() {

        Intent intent = new Intent(Login.this , MainActivity.class);
        startActivity(intent);
        finish();

    }
}