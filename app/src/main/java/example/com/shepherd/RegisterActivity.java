package example.com.shepherd;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.concurrent.ExecutionException;

import example.com.shepherd.data.AccountContract;
import example.com.shepherd.data.DbHelper;
import example.com.shepherd.dbconnection.addAccount;

public class RegisterActivity extends AppCompatActivity {

    private EditText mEmail;
    private EditText mPassword;

    private SQLiteDatabase mDb;
    private String new_userid;
    private String new_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mEmail = findViewById(R.id.register_email);
        mPassword = findViewById(R.id.register_password);

//        DbHelper dbHelper = new DbHelper(this);
//        mDb = dbHelper.getReadableDatabase();

        Button toSignin = findViewById(R.id.to_signin_button);
        toSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        Button register = findViewById(R.id.register_button);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //insert register and signin code
                if(true) {
                    ContentValues cv = new ContentValues();
                    new_userid = mEmail.getText().toString();
                    new_password = mPassword.getText().toString();
                    final addAccount mAddAccount = new addAccount();
                    try {
                        String error = mAddAccount.execute(new_userid,new_userid).get();
                        if(error != null)
                        {
                            Toast.makeText(getApplicationContext(), error,
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            SharedPreferences.Editor prefs = getSharedPreferences("shepherd",0).edit();
                            prefs.putString("email", mEmail.getText().toString());
                            prefs.apply();

                            Intent intent = new Intent(getApplicationContext(), EventListActivity.class);
                            Toast.makeText(getApplicationContext(), "Successful!",
                                    Toast.LENGTH_SHORT).show();
                            startActivity(intent);
                            finish();
                        }

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
//                    cv.put(AccountContract.AccountEntry.COLUMN_USER_ID, mEmail.getText().toString());
//                    cv.put(AccountContract.AccountEntry.COLUMN_PASSWORD, "temp password");
//                    mDb.insert(AccountContract.AccountEntry.TABLE_NAME, null, cv);



                }
            }
        });
    }
}