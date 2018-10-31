package example.com.shepherd;

import android.Manifest;
import android.app.DialogFragment;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.Date;

import example.com.shepherd.data.EventContract;
import example.com.shepherd.data.DbHelper;
import example.com.shepherd.dbconnection.addAttendance;

public class EventDetailActivity extends AppCompatActivity implements OnCompleteListener<Void>, CheckInDialogFragment.NoticeDialogListener {

    /* Below was taken from FinalDB+GPS */
    private static final String TAG = EventDetailActivity.class.getSimpleName();

    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;

    private static final long GEOFENCE_EXPIRATION_IN_HOURS = 12;

    private final long GEOFENCE_EXPIRATION_IN_MILLISECONDS = GEOFENCE_EXPIRATION_IN_HOURS * 60 * 60 * 1000;
    private final float GEOFENCE_RADIUS_IN_METERS = 200;

    //create an instance of the Geofencing client
    private GeofencingClient mGeofencingClient;

    //The list of geofences.
    private Geofence mGeofence;

    private enum PendingGeofenceTask {ADD, REMOVE, NONE}
    private PendingIntent mGeofencePendingIntent;
    private PendingGeofenceTask mPendingGeofenceTask = PendingGeofenceTask.NONE;

    private double lat;
    private double lon;
    private String event_name;
    /* Above was taken from FinalDB+GPS */

    private TextView mEventNameTextView;
    private TextView mEventDescriptionTextView;
    private TextView mEventStartTimeTextView;
    private TextView mEventLocationTextView;

    private SQLiteDatabase mDb;

    private int mEventId;
    private Event mEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);

        mEventId = getIntent().getIntExtra("com.example.shepherd.eventId", -1);

        if (mEventId == -1) {
            finish();
            return;
        }

        DbHelper dbHelper = new DbHelper(this);
        mDb = dbHelper.getReadableDatabase();

        mEvent = getEventFromDb(mEventId);

        setTitle(mEvent.name);

        mEventNameTextView = (TextView) findViewById(R.id.tv_event_name);
        mEventNameTextView.append(mEvent.name);

        mEventDescriptionTextView = (TextView) findViewById(R.id.tv_event_description);
        mEventDescriptionTextView.append(mEvent.description);

        mEventStartTimeTextView = (TextView) findViewById(R.id.tv_event_start_time);
        mEventStartTimeTextView.append(mEvent.startTime.toString());

        mEventLocationTextView = (TextView) findViewById(R.id.tv_event_location);
        mEventLocationTextView.append(mEvent.location.toString());
        final String loc = mEvent.location.replaceAll(" ", "+");
        mEventLocationTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("google.navigation:q=" + loc));
                startActivity(intent);
            }
        });

        /* Below was taken from FinalDB+GPS */
        mGeofence = null;
        mGeofencePendingIntent = null;

        String gpsLoc = mEvent.location;
        Log.d("gpsLoc", gpsLoc);
        //!!!Need to change the location to latitude and longtitude
        String gpsID = mEvent.name;
        event_name = gpsID;
        GeocodingLocation mLocation = new GeocodingLocation();
        GeocoderHandler mHandler = new GeocoderHandler();
        mLocation.getAddress(gpsLoc, getApplicationContext(), mHandler);
        //lat = 42.274228;
        //lon = -71.8063829;
        //populateGeofenceList(event_name, lat, lon);
        mGeofencingClient = LocationServices.getGeofencingClient(this);
        /* Above was taken from FinalDB+GPS */

        CheckBox mSolvedCheckbox = (CheckBox) findViewById(R.id.cb_is_attending);
        mSolvedCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if (isChecked) {
                    /* from addGeofencesButtonHandler */
                    if (!checkPermissions()) {
                        mPendingGeofenceTask = PendingGeofenceTask.ADD;
                        requestPermissions();
                        return;
                    }
                    addGeofences();
                } else {
                    /* from removeGeofencesButtonHandler */
                    if (!checkPermissions()) {
                        mPendingGeofenceTask = PendingGeofenceTask.REMOVE;
                        requestPermissions();
                        return;
                    }
                    removeGeofences();
                }
            }
        });

        boolean fireCheckInDialog = getIntent().getBooleanExtra("com.example.shepherd.fireCheckInDialog", false);
        if (fireCheckInDialog) {
            new CheckInDialogFragment().show(getFragmentManager(), TAG);
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        if (!checkPermissions()) {
            requestPermissions();
        } else {
            performPendingGeofenceTask();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        Log.i(TAG, "onRequestPermissionResult");
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length <= 0) {
                // If user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
                Log.i(TAG, "User interaction was cancelled.");
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.i(TAG, "Permission granted.");
                performPendingGeofenceTask();
            } else {
                // Permission denied.
                mPendingGeofenceTask = PendingGeofenceTask.NONE;
            }
        }
    }

    private boolean checkPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION);


        if (shouldProvideRationale) {
            Log.i(TAG, "Displaying permission rationale to provide additional context.");
        } else {
            Log.i(TAG, "Requesting permission");
            // Request permission.
            ActivityCompat.requestPermissions(EventDetailActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

    //Add to geofencelist
    private void populateGeofenceList(String name,double lat, double lon) {
        mGeofence = new Geofence.Builder()
                // Set the circular region of this geofence.
                .setRequestId(name)
                .setCircularRegion(
                        lat,
                        lon,
                        GEOFENCE_RADIUS_IN_METERS
                )
                // Set the expiration duration of the geofence.
                .setExpirationDuration(GEOFENCE_EXPIRATION_IN_MILLISECONDS)

                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                        Geofence.GEOFENCE_TRANSITION_EXIT)
                // Create the geofence.
                .build();
    }

    private void performPendingGeofenceTask() {
        if (mPendingGeofenceTask == PendingGeofenceTask.ADD) {
            addGeofences();
        } else if (mPendingGeofenceTask == PendingGeofenceTask.REMOVE) {
            removeGeofences();
        }
    }

    @SuppressWarnings("MissingPermission")
    private void addGeofences() {
        if (!checkPermissions()) {
            return;
        }

        mGeofencingClient.addGeofences(getGeofencingRequest(), getGeofencePendingIntent())
                .addOnCompleteListener(this);
    }

    @SuppressWarnings("MissingPermission")
    private void removeGeofences() {
        if (!checkPermissions()) {
            return;
        }

        mGeofencingClient.removeGeofences(getGeofencePendingIntent()).addOnCompleteListener(this);
    }

    @Override
    public void onComplete(@NonNull Task<Void> task) {
        mPendingGeofenceTask = PendingGeofenceTask.NONE;
        if (task.isSuccessful()) {
            /*
            updateGeofencesAdded(!getGeofencesAdded());
            //setButtonsEnabledState();     // TODO: don't need this?

            int messageId = getGeofencesAdded() ? R.string.geofences_added :
                    R.string.geofences_removed;
            Toast.makeText(this, getString(messageId), Toast.LENGTH_LONG).show();
            */
            // TODO: write to SharedPreferences
            Log.d("onComplete", "Task successful");
        } else {
            return;
        }
    }

    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();

        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);

        // Add the geofence to be monitored by geofencing service.
        builder.addGeofence(mGeofence);

        // Return a GeofencingRequest.
        return builder.build();
    }

    //Define an Intent for geofence transitions
    private PendingIntent getGeofencePendingIntent() {
        // Reuse the PendingIntent if we already have it.
        if (mGeofencePendingIntent != null) {
            return mGeofencePendingIntent;
        }
        Intent intent = new Intent(this, GeofenceTransitionsIntentService.class);
        intent.putExtra("com.example.shepherd.eventId", mEventId);
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when
        // calling addGeofences() and removeGeofences().
        mGeofencePendingIntent = PendingIntent.getService(this, 0, intent, PendingIntent.
                FLAG_UPDATE_CURRENT);
        return mGeofencePendingIntent;
    }

    /**
     * Query the mDb and get one event from the events table, based on the given id
     *
     * @return Event created from the events table
     */
    private Event getEventFromDb(int id) {
        Cursor cursor = mDb.query(
                EventContract.EventEntry.TABLE_NAME,
                null,
                EventContract.EventEntry._ID + "=" + id,
                null,
                null,
                null,
                null
        );
        if (!cursor.moveToFirst()) {
            return null; // bail if returned null
        }
        String name = cursor.getString(cursor.getColumnIndex(EventContract.EventEntry.COLUMN_EVENT_NAME));
        String location = cursor.getString(cursor.getColumnIndex(EventContract.EventEntry.COLUMN_LOCATION));
        String description = cursor.getString(cursor.getColumnIndex(EventContract.EventEntry.COLUMN_DESCRIPTION));
        Long startTime = cursor.getLong(cursor.getColumnIndex(EventContract.EventEntry.COLUMN_START_TIME));
        Date startDate = new Date(startTime);
        return new Event(name, location, startDate, null, description);
    }

    //Give lat and lon and set geofences
    private class GeocoderHandler extends Handler {
        @Override
        public void handleMessage(Message message) {
            String addr;
            switch (message.what) {
                case 1:
                    Bundle bundle = message.getData();
                    addr = bundle.getString("address");
                    break;
                default:
                    addr = null;
            }
            String[] latlon = addr.split(",");
            lat = Double.parseDouble(latlon[0]);
            lon = Double.parseDouble(latlon[1]);
            System.out.println("lat: " + lat+ " lon: "+lon);
            populateGeofenceList(event_name, lat, lon);
        }
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        SharedPreferences prefs = getSharedPreferences("shepherd",0);
        String email = prefs.getString("email",null);
        new addAttendance().execute(email, mEvent.name);
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {

    }
}
