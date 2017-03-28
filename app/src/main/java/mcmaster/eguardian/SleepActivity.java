package mcmaster.eguardian;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.TimePicker;

import com.orm.SugarContext;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import mcmaster.eguardian.domain.Alert;
import mcmaster.eguardian.domain.HeartRate;

/**
 * Created by i on 2016-11-20.
 */

public class SleepActivity extends Activity {


    final Context context = this;
    @TargetApi(Build.VERSION_CODES.N)
    @Override
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sleep_activity);
        SugarContext.init(context);
        DateFormat timeFormatter = new SimpleDateFormat("HH:mm:ss");
        DateFormat dateFormatter = new SimpleDateFormat("MM/dd/yy");
        Calendar cal = Calendar.getInstance();
        for (int i = 0; i < 7; i++){
            update(new HeartRate(), cal.getTime());
            cal.add(Calendar.DAY_OF_MONTH,-1);
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private TextView addColumn(String value, TextView config){
        TextView column = new TextView(this);
        column.setLayoutParams(config.getLayoutParams());
        column.setText(value);
        column.setTextSize(16);
        column.setPadding(config.getPaddingLeft(),config.getPaddingTop(), config.getPaddingRight(), config.getPaddingBottom());
        column.setTextColor(config.getCurrentTextColor());
        column.setTextAlignment(config.getTextAlignment());
        return column;
    }

    private LinearLayout addDivider(LinearLayout config){
        LinearLayout div = new LinearLayout(this);
        div.setLayoutParams(config.getLayoutParams());
        div.setBackgroundColor(config.getSolidColor());
        return div;
    }

    private void update(HeartRate hr, Date date){
        TableLayout table = (TableLayout)findViewById(R.id.table);
        TableRow headers = (TableRow)findViewById(R.id.header);
        TextView dateConfig = (TextView) findViewById(R.id.date);
        TextView sleepConfig = (TextView) findViewById(R.id.hoursOfSleep);
        LinearLayout dividerConfig = (LinearLayout) findViewById(R.id.divider);

        TableRow newRow = new TableRow(this);
        newRow.setLayoutParams(headers.getLayoutParams());
        newRow.setBackgroundColor(Color.parseColor("#303F9F"));
        newRow.setPadding(headers.getPaddingLeft(),headers.getPaddingTop(), headers.getPaddingRight(), headers.getPaddingBottom());


        SimpleDateFormat dateFormatter = new SimpleDateFormat("MM/dd/yy");
        //Date date = new Date();
        String outDate = dateFormatter.format(date.getTime());
        newRow.addView(addDivider(dividerConfig));
        newRow.addView(addColumn(outDate, dateConfig));
        newRow.addView(addDivider(dividerConfig));
        newRow.addView(addColumn(hr.getHoursOfSleep(date), sleepConfig));
        table.addView(newRow);
    }
}
