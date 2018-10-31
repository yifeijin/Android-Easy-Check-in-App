package example.com.shepherd;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import example.com.shepherd.data.EventContract;
import example.com.shepherd.data.DbHelper;
import example.com.shepherd.data.TestUtil;

public class EventListActivity extends AppCompatActivity implements EventAdapter.ListItemClickListener {

    private EventAdapter mAdapter;
    private RecyclerView mEventsList;

    private SQLiteDatabase mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_list);

        mEventsList = findViewById(R.id.rv_event_list);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mEventsList.setLayoutManager(layoutManager);

        mEventsList.setHasFixedSize(true);

        DbHelper dbHelper = new DbHelper(this);
        mDb = dbHelper.getWritableDatabase();

        TestUtil.insertFakeData(mDb);

        Cursor cursor = getAllGuests();

        mAdapter = new EventAdapter(this, cursor);
        mEventsList.setAdapter(mAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.event_list_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.logout_menu_item:
                SharedPreferences.Editor prefs = getSharedPreferences("shepherd",0).edit();
                prefs.remove("email");
                prefs.apply();

                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onListItemClick(int id) {
        Intent intent = new Intent(this, EventDetailActivity.class);
        intent.putExtra("com.example.shepherd.eventId", id);
        startActivity(intent);
    }

    /**
     * Query the mDb and get all guests from the waitlist table
     *
     * @return Cursor containing the list of guests
     */
    private Cursor getAllGuests() {
        return mDb.query(
                EventContract.EventEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                EventContract.EventEntry.COLUMN_EVENT_NAME
        );
    }
}
