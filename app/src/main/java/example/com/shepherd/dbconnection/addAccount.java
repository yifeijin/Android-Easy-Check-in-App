package example.com.shepherd.dbconnection;

import android.os.AsyncTask;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by zluo2 on 12/13/17.
 */

public class addAccount extends AsyncTask<String, Void, String> {

    private String error;
    @Override
    public String doInBackground(String...strings) {
        String userid = strings[0];
        String password = strings[1];

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
                st.executeUpdate("INSERT INTO accounts " + "VALUES ('" + userid + "','" + password + "')");
                st.close();
            } catch (SQLException e) {
                error = "Account already exist";
                e.printStackTrace();
            }
        } else {
            System.out.println("Failed to make connection!");
        }
        return error;
    }

}
