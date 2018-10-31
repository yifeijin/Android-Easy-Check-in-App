package example.com.shepherd.data;

import android.content.ContentValues;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class TestUtil {
    public static void insertFakeData(SQLiteDatabase db){
        if(db == null){
            return;
        }
        //create a list of fake events
        List<ContentValues> list = new ArrayList<ContentValues>();

        ContentValues cv = new ContentValues();
        cv.put(EventContract.EventEntry.COLUMN_EVENT_NAME, "CORE Applications NOW OPEN!");
        cv.put(EventContract.EventEntry.COLUMN_DESCRIPTION, "Please apply for the CORE (Campus Organization Recognition of Excellence) Awards here! Applications are open until January 29, 2018 at midnight.\n" +
                "\n" +
                " \n" +
                "\n" +
                "Advisor of the Year: https://wpi.campuslabs.com/engage/submitter/form/start/146644 \n" +
                "\n" +
                "Organization of the Year: https://wpi.campuslabs.com/engage/submitter/form/start/146640\n" +
                "\n" +
                "Program of the Year: https://wpi.campuslabs.com/engage/submitter/form/start/146637\n" +
                "\n" +
                "Diversity Program of the Year: https://wpi.campuslabs.com/engage/submitter/form/start/146643\n" +
                "\n" +
                "Emerging Leader of the Year: https://wpi.campuslabs.com/engage/submitter/form/start/146642");
        cv.put(EventContract.EventEntry.COLUMN_LOCATION, "Student Activities Office");
        cv.put(EventContract.EventEntry.COLUMN_START_TIME, 1474678800000L);
        list.add(cv);

        cv = new ContentValues();
        cv.put(EventContract.EventEntry.COLUMN_EVENT_NAME, "Habitat for Humanity -- Restore Hours");
        cv.put(EventContract.EventEntry.COLUMN_DESCRIPTION, "Volunteer: The ReStore wouldn’t be successful without volunteers like you!  click here to learn more about volunteering at the ReStore.\n" +
                "\n" +
                " \n" +
                "\n" +
                "Volunteer with us at our ReStore to sort fun donations, or on a build site to hammer some nails! Join a committee, or start a campus chapter! Habitat wants to provide a welcoming environment for all skills and talents.\n" +
                "\n" +
                "These orientations are mandatory for those looking to volunteer at Habitat for Humanity MetroWest/Greater Worcester ReStore. Orientations cover the mission and vision of Habitat and ReStore as well as volunteer opportunities and expectations.\n" +
                "\n" +
                "November 18th- 2pm-3pm\n" +
                "\n" +
                "December 4th- 10am-11am\n" +
                "December 16th- 2pm-3pm\n" +
                "\n" +
                "January 8th- 10am-11am\n" +
                "January 27th- 2pm-3pm\n" +
                "\n" +
                " \n" +
                "\n" +
                "All orientations are held at the Administrative Offices located next to ReStore at 11 Distributor Road in Worcester MA 01605. You can RSVP for a session by signing up on CERVIS below.\n" +
                "\n" +
                " \n" +
                "\n" +
                "Have you already attended a volunteer training?:\n" +
                "\n" +
                "Sign up for a shift here: http://www.habitatmwgw.org/volunteer/\n" +
                "\n" +
                "General Information about Restore and Volunteering at this site:\n" +
                "\n" +
                "Volunteers at our ReStore will help with donations processing, merchandizing, customer service, and occasionally help with small construction projects. Volunteers must be 16 years of age or older to volunteer at ReStore, and all volunteers under the age of 18 must have a waiver form signed by a parent or guardian. If you are interested in scheduling a group day of service or have any volunteer inquiries please contact the Volunteer Coordinator by e-mail or phone at volunteer@habitatmwgw.org  or 508-799-9259 x 113.\n" +
                "\n" +
                "The Habitat for Humanity-MetroWest/Greater Worcester ReStore is a retail outlet that sells quality, gently-used and surplus construction and home improvement materials as well as home furnishings for a fraction of regular retail prices.  The ReStore offers great deals on building supplies, making your home improvements more affordable while providing much needed funding for the work of Habitat for Humanity-MetroWest/Greater Worcester.  \n" +
                "\n" +
                "-------\n" +
                "\n" +
                "Both Training AND ReStore Hours as FWS.  The ReStore is within Walking Distance from Campus.  You MUST attend an orientation before volunteering.");
        cv.put(EventContract.EventEntry.COLUMN_LOCATION, "Habitat for Humanity Restore");
        cv.put(EventContract.EventEntry.COLUMN_START_TIME, 1474678800000L);
        list.add(cv);

        cv = new ContentValues();
        cv.put(EventContract.EventEntry.COLUMN_EVENT_NAME, "CS 4518 Final Project Demos");
        cv.put(EventContract.EventEntry.COLUMN_DESCRIPTION, "What do you think this event is about?");
        cv.put(EventContract.EventEntry.COLUMN_LOCATION, "The Washburn Shops");
        cv.put(EventContract.EventEntry.COLUMN_START_TIME, 1513324800000L);
        list.add(cv);

        cv = new ContentValues();
        cv.put(EventContract.EventEntry.COLUMN_EVENT_NAME, "Study for Finals");
        cv.put(EventContract.EventEntry.COLUMN_DESCRIPTION, "Description.");
        cv.put(EventContract.EventEntry.COLUMN_LOCATION, "George C. Gordon Library");
        cv.put(EventContract.EventEntry.COLUMN_START_TIME, 1513324800000L);
        list.add(cv);


        //insert all events in one transaction
        try
        {
            db.beginTransaction();
            //clear the table first
            db.delete (EventContract.EventEntry.TABLE_NAME,null,null);
            //go through the list and add one by one
            for(ContentValues c:list){
                db.insert(EventContract.EventEntry.TABLE_NAME, null, c);
            }
            db.setTransactionSuccessful();
        }
        catch (SQLException e) {
            //too bad :(
        }
        finally
        {
            db.endTransaction();
        }

    }
}
