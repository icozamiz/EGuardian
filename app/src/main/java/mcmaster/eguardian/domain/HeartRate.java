package mcmaster.eguardian.domain;

import com.orm.SugarRecord;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by i on 2016-11-22.
 */

public class HeartRate extends SugarRecord {

    private Date time;
    private Date date;
    private int bpm;

    public HeartRate(){
    }

    public HeartRate(String time, int bpm, String date){
        this.bpm = bpm;
        DateFormat timeFormatter = new SimpleDateFormat("HH:mm:ss");
        DateFormat dateFormatter = new SimpleDateFormat("MM/dd/yy");
        try {
            this.time = (Date)timeFormatter.parse(time);
            this.date = (Date)dateFormatter.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public String getTime(){
        return this.time.toString();
    }

    public int getBpm(){
        return this.bpm;
    }

    public String getDate(){
        return this.date.toString();
    }

}
