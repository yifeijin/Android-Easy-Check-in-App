package example.com.shepherd.dbconnection;

import android.os.AsyncTask;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import example.com.shepherd.Event;

public class getEventList extends AsyncTask<Void, Void, List<Event>> {


    public List<Event> events = new ArrayList();
    public getEventList() {
    }


    @Override
    public List<Event> doInBackground(Void... arg0) {
        System.out.println("-------- PostgreSQL "
                + "JDBC Connection Testing ------------");

        try {

            Class.forName("org.postgresql.Driver");

        } catch (ClassNotFoundException e) {

            System.out.println("Where is your PostgreSQL JDBC Driver? "
                    + "Include in your library path!");
            e.printStackTrace();
            return null;

        }

        System.out.println("PostgreSQL JDBC Driver Registered!");

        Connection connection = null;

        try {

            connection = DriverManager.getConnection(
                    "jdbc:postgresql://baasu.db.elephantsql.com:5432/grqcridh", "grqcridh",
                    "rBnvjIGDSdpTyhRPh7CO_xD8rGG9olya");

        } catch (SQLException e) {

            System.out.println("Connection Failed! Check output console");
            e.printStackTrace();
            return null;

        }

        if (connection != null) {
            System.out.println("Connection successful!");
            try {
                Statement st = connection.createStatement();
                ResultSet event = st.executeQuery("SELECT * FROM event");
                event.next();
                do {
                    String name = event.getString("name");
                    String location = event.getString("location");
                    String description = event.getString("description");
                    Long startTime = event.getLong("start_time");
                    Date startDate = new Date(startTime);
                    events.add(new Event(name, location, startDate, null, description));
                    /*
                    events.add(event.getString("name") + "," +
                            event.getString("description") + "," +
                            event.getString("start_time"));
                            */
                } while (event.next());
                st.close();
                return events;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Failed to make connection!");
        }
        return null;
    }
}