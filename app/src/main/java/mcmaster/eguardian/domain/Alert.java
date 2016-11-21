package mcmaster.eguardian.domain;

import com.orm.SugarRecord;

import java.util.Date;

/**
 * Created by i on 2016-11-21.
 */

public class Alert extends SugarRecord {
    private String name;
    private String message;
    private String date;
    private String time;

    public Alert(){
    }

    public Alert(String name, String message, String date, String time){
        this.name = name;
        this.message = message;
        this.date = date;
        this.time = time;
    }

    public String getName(){
        return this.name;
    }

    public String getMessage(){
        return this.message;
    }

    public String getDate(){
        return this.date;
    }

    public String getTime(){
        return this.time;
    }
}
