package example.com.shepherd;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
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
import example.com.shepherd.dbconnection.getPass;

public class LoginActivity extends AppCompatActivity {

    private EditText mEmail;
    private EditText mPassword;
    private String password;


//    private SQLiteDatabase mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mEmail = findViewById(R.id.login_email);
        mPassword = findViewById(R.id.login_password);

//        DbHelper dbHelper = new DbHelper(this);
//        mDb = dbHelper.getReadableDatabase();

        Button toRegister = findViewById(R.id.to_register_button);
        toRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });

        Button login = findViewById(R.id.login_button);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //signin code
//                Cursor cursor = mDb.query(
//                        AccountContract.AccountEntry.TABLE_NAME,
//                        null,
//                        AccountContract.AccountEntry.COLUMN_USER_ID + "='" + mEmail.getText().toString() + "'",
//                        null,
//                        null,
//                        null,
//                        null
//                );
//                if(cursor.getCount() > 0) {
//                    SharedPreferences.Editor prefs = getSharedPreferences("shepherd",0).edit();
//                    prefs.putString("email", mEmail.getText().toString());
//                    prefs.apply();
                //                }
                try {
                    final getPass mGetPassword = new getPass();
                    password = mGetPassword.execute(mEmail.getText().toString()).get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
                if(password.equals(mPassword.getText().toString())) {
                    SharedPreferences.Editor prefs = getSharedPreferences("shepherd",0).edit();
                    prefs.putString("email", mEmail.getText().toString());
                    prefs.apply();

                    Intent intent = new Intent(getApplicationContext(), EventListActivity.class);
                    startActivity(intent);
                    finish();
                } else if (password.equals("NO_USERID"))
                {
                    Toast.makeText(getApplicationContext(), "New User Needs To Sign Up!",
                            Toast.LENGTH_SHORT).show();
                } else
                {
                    Toast.makeText(getApplicationContext(), "Wrong Password!",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
