package example.com.shepherd;

/**
 * Created by zluo2 on 12/13/17.
 */
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


public class GeocodingLocation {

    private static final String TAG = "GeocodingLocation";
    String value;


    public void getAddress(final String locationAddress, final Context context,final Handler handler) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                Geocoder geocoder = new Geocoder(context, Locale.getDefault());
                String result = null;
                try {
                    List<Address> addressList = geocoder.getFromLocationName(locationAddress, 1);
                    if (addressList != null && addressList.size() > 0) {
                        Address address = addressList.get(0);
                        StringBuilder sb = new StringBuilder();
                        value = address.getLatitude() + "," + address.getLongitude();
                    }
                } catch (IOException e) {
                    Log.e(TAG, "Unable to connect to Geocoder", e);
                } finally {
                    if(value != null) {
                        Message message = Message.obtain();
                        message.setTarget(handler);
                        message.what = 1;
                        Bundle bundle = new Bundle();
                        bundle.putString("address",value);
                        message.setData(bundle);
                        message.sendToTarget();
                    } else
                    {
                        Message message = Message.obtain();
                        message.setTarget(handler);
                        message.what = 1;
                        Bundle bundle = new Bundle();
                        bundle.putString("address", "Did not find the location");
                        message.setData(bundle);
                        message.sendToTarget();
                    }

                }
            }
        }).start();
    }
}
