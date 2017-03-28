package mcmaster.eguardian.domain;

import com.orm.SugarRecord;

import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import mcmaster.eguardian.SleepActivity;

/**
 * Created by i on 2016-11-22.
 */

public class HeartRate extends SugarRecord {

    private Date time;
    private Date date;
    private int bpm;

    public HeartRate(){
        date = new Date();
    }

    public HeartRate(String time, int bpm, String date){
        this.bpm = bpm;
        DateFormat timeFormatter = new SimpleDateFormat("hh:mm:ss");
        DateFormat dateFormatter = new SimpleDateFormat("MM/dd/yy");
        try {
            this.time = (Date)timeFormatter.parse(time);
            this.date = (Date)dateFormatter.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public Date getTime(){
        return this.time;
    }

    public int getBpm(){
        return this.bpm;
    }

    public Date getDate(){
        return this.date;
    }

    public String getHoursOfSleep(Date startDate){
        long t = 8;
        Calendar cal = Calendar.getInstance();
        cal.setTime(startDate);
        cal.add(Calendar.DATE,1);
        List<HeartRate> notes;
        String [] args = {String.valueOf(startDate.getTime()), String.valueOf(cal.getTime().getTime()), "35", "55"};
        notes = HeartRate.findWithQuery(HeartRate.class,
                "SELECT * FROM HEART_RATE WHERE (DATE BETWEEN ? AND ?) AND (BPM BETWEEN ? AND ?)", args);
        if (notes.size() > 0){
            HeartRate start = notes.get(0);
            HeartRate finish = notes.get(notes.size()-1);
            t = getDateDiff(start.getTime(),finish.getTime(),TimeUnit.HOURS);
        }
        return  String.valueOf(t);
    }

    public String getSteps(Date startDate){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE,1);
        Calendar cal2 = Calendar.getInstance();
        cal2.add(Calendar.DATE, -1);
        List<HeartRate> notes;
        String [] args = {String.valueOf(cal2.getTime().getTime()), String.valueOf(cal.getTime().getTime()), "85", "150"};
        notes = HeartRate.findWithQuery(HeartRate.class,
                "SELECT * FROM HEART_RATE WHERE (DATE BETWEEN ? AND ?) AND (BPM BETWEEN ? AND ?)", args);
        return  String.valueOf(notes.size());
    }

    public static long getDateDiff(Date date1, Date date2, TimeUnit timeUnit) {
        long diffInMillies = date2.getTime() - date1.getTime();
        return timeUnit.convert(diffInMillies,TimeUnit.MILLISECONDS);
    }
}
