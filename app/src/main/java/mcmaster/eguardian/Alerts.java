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
import android.support.annotation.RequiresApi;
import android.os.Bundle;
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

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import mcmaster.eguardian.domain.Alert;

@RequiresApi(api = Build.VERSION_CODES.N)
public class Alerts extends Activity {
    private Button button;
    private Button deleteButton;
    final Context context = this;
   Calendar myCalendar = Calendar.getInstance();


    @TargetApi(Build.VERSION_CODES.N)
    @Override
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alerts);
        SugarContext.init(context);

        List<Alert> alerts = Alert.listAll(Alert.class);
        for  (Iterator<Alert> itr = alerts.iterator(); itr.hasNext();){
            Alert a = itr.next();
            update(a);
        }

        // components from main.xml
        button = (Button) findViewById(R.id.buttonPrompt);
        deleteButton = (Button) findViewById(R.id.buttonDelete);

        deleteButton.setOnClickListener(new View.OnClickListener() {

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View arg0) {
                Alert.deleteAll(Alert.class);
            }
        });
        // add button listener
        button.setOnClickListener(new View.OnClickListener() {

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View arg0) {

                // get prompts.xml view
                LayoutInflater li = LayoutInflater.from(context);
                final View promptsView = li.inflate(R.layout.prompts, null);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        context);
                // set prompts.xml to alertdialog builder
                alertDialogBuilder.setView(promptsView);

                final EditText alertName = (EditText) promptsView
                        .findViewById(R.id.editAlertName);

                final EditText message = (EditText) promptsView
                        .findViewById(R.id.editMessage);

                final EditText date = (EditText) promptsView
                        .findViewById(R.id.editDate);

                final EditText time = (EditText) promptsView
                        .findViewById(R.id.editTime);


                time.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        Calendar mcurrentTime = Calendar.getInstance();
                        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                        int minute = mcurrentTime.get(Calendar.MINUTE);
                        TimePickerDialog mTimePicker;
                        mTimePicker = new TimePickerDialog(Alerts.this, new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                                time.setText( selectedHour + ":" + selectedMinute);
                            }
                        }, hour, minute, true);// Yes 24 hour time
                        mTimePicker.setTitle("Select Time");
                        mTimePicker.show();

                    }
                });

                final DatePickerDialog.OnDateSetListener dateSet = new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear,
                                          int dayOfMonth) {
                        myCalendar.set(Calendar.YEAR, year);
                        myCalendar.set(Calendar.MONTH, monthOfYear);
                        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        updateLabel(date);
                    }

                };

                date.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        new DatePickerDialog(Alerts.this, dateSet, myCalendar
                                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                                myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                    }
                });

                // set dialog message
                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                                        // get user input and set it to result
                                        // edit text
                                        //result.setText("Alert " + alertName.getText() + " set by " + setBy.getText()
                                          //      + " with message " + message.getText());
                                        Alert alert = new Alert(alertName.getText().toString(), message.getText().toString(), date.getText().toString(), time.getText().toString());
                                        alert.save();
                                        update(alert);
                                    }
                                })
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();

                // show it
                alertDialog.show();

            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void updateLabel(TextView setBy) {

        String myFormat = "MM/dd/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        setBy.setText(sdf.format(myCalendar.getTime()));
    }

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

    private void update(Alert alert){
        TableLayout table = (TableLayout)findViewById(R.id.table);
        TableRow headers = (TableRow)findViewById(R.id.alertHeader);
        TextView alertNameConfig = (TextView) findViewById(R.id.alertName);
        TextView messageConfig = (TextView) findViewById(R.id.message);
        TextView dateConfig = (TextView) findViewById(R.id.date);
        TextView timeConfig = (TextView) findViewById(R.id.time);
        LinearLayout dividerConfig = (LinearLayout) findViewById(R.id.divider);

        TableRow newRow = new TableRow(this);
        newRow.setLayoutParams(headers.getLayoutParams());
        newRow.setBackgroundColor(Color.parseColor("#303F9F"));
        newRow.setPadding(headers.getPaddingLeft(),headers.getPaddingTop(), headers.getPaddingRight(), headers.getPaddingBottom());

        newRow.addView(addDivider(dividerConfig));
        newRow.addView(addColumn(alert.getName(), alertNameConfig));
        newRow.addView(addDivider(dividerConfig));
        newRow.addView(addColumn(alert.getMessage(), messageConfig));
        newRow.addView(addDivider(dividerConfig));
        newRow.addView(addColumn(alert.getDate(), dateConfig));
        newRow.addView(addDivider(dividerConfig));
        newRow.addView(addColumn(alert.getTime(), timeConfig));
        table.addView(newRow);
    }

    private void upload2Server(Alert alert){

    }
}
