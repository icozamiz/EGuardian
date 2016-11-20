package mcmaster.eguardian;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DialogTitle;
import android.support.v7.widget.LinearLayoutCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Locale;

@RequiresApi(api = Build.VERSION_CODES.N)
public class Alerts extends AppCompatActivity {
    private Button button;
    private TextView result;
    final Context context = this;
    Calendar myCalendar = Calendar.getInstance();


    @Override
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alerts);

        // components from main.xml
        button = (Button) findViewById(R.id.buttonPrompt);
        result = (TextView) findViewById(R.id.alertResult);

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

                final EditText setBy = (EditText) promptsView
                        .findViewById(R.id.editSetBy);

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

                final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear,
                                          int dayOfMonth) {
                        // TODO Auto-generated method stub
                        myCalendar.set(Calendar.YEAR, year);
                        myCalendar.set(Calendar.MONTH, monthOfYear);
                        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        updateLabel(setBy);
                    }

                };

                setBy.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        new DatePickerDialog(Alerts.this, date, myCalendar
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
                                        update(alertName.getText().toString(), message.getText().toString(), setBy.getText().toString(), time.getText().toString());
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

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void update(String alertName, String message, String setBy, String time) {
        TableLayout table = (TableLayout)findViewById(R.id.table);

        TableRow headers = (TableRow)findViewById(R.id.alertHeader);
        TextView alertNameConfig = (TextView) findViewById(R.id.alertName);
        TextView messageConfig = (TextView) findViewById(R.id.message);
        TextView setByConfig = (TextView) findViewById(R.id.setBy);
        TextView timeConfig = (TextView) findViewById(R.id.time);
        LinearLayout dividerConfig = (LinearLayout) findViewById(R.id.divider);

        TableRow newRow = new TableRow(this);
        newRow.setLayoutParams(headers.getLayoutParams());
        newRow.setBackgroundColor(Color.BLACK);
        newRow.setPadding(headers.getPaddingLeft(),headers.getPaddingTop(), headers.getPaddingRight(), headers.getPaddingBottom());

        LinearLayout div = new LinearLayout(this);
        div.setLayoutParams(dividerConfig.getLayoutParams());
        div.setBackgroundColor(Color.BLACK);
        newRow.addView(div);

        TextView aName = new TextView(this);
        aName.setLayoutParams(alertNameConfig.getLayoutParams());
        aName.setText(alertName);
        aName.setTextSize(16);
        aName.setPadding(alertNameConfig.getPaddingLeft(),alertNameConfig.getPaddingTop(), alertNameConfig.getPaddingRight(), alertNameConfig.getPaddingBottom());
        aName.setTextColor(alertNameConfig.getCurrentTextColor());
        aName.setTextAlignment(alertNameConfig.getTextAlignment());
        newRow.addView(aName);

        LinearLayout divm = new LinearLayout(this);
        divm.setLayoutParams(dividerConfig.getLayoutParams());
        divm.setBackgroundColor(Color.BLACK);
        newRow.addView(divm);

        TextView m = new TextView(this);
        m.setLayoutParams(messageConfig.getLayoutParams());
        m.setText(message);
        m.setTextSize(16);
        m.setPadding(messageConfig.getPaddingLeft(),messageConfig.getPaddingTop(),
                messageConfig.getPaddingRight(), messageConfig.getPaddingBottom());
        m.setTextColor(messageConfig.getCurrentTextColor());
        m.setTextAlignment(messageConfig.getTextAlignment());
        newRow.addView(m);

        LinearLayout divs = new LinearLayout(this);
        divs.setLayoutParams(dividerConfig.getLayoutParams());
        divs.setBackgroundColor(Color.BLACK);
        newRow.addView(divs);

        TextView sB = new TextView(this);
        sB.setLayoutParams(setByConfig.getLayoutParams());
        sB.setText(setBy);
        sB.setTextSize(16);
        sB.setPadding(setByConfig.getPaddingLeft(),setByConfig.getPaddingTop(),
                setByConfig.getPaddingRight(), setByConfig.getPaddingBottom());
        sB.setTextColor(setByConfig.getCurrentTextColor());
        sB.setTextAlignment(setByConfig.getTextAlignment());
        newRow.addView(sB);

        LinearLayout divt = new LinearLayout(this);
        divt.setLayoutParams(dividerConfig.getLayoutParams());
        divt.setBackgroundColor(Color.BLACK);
        newRow.addView(divt);

        TextView ttime = new TextView(this);
        ttime.setLayoutParams(alertNameConfig.getLayoutParams());
        ttime.setText(time);
        ttime.setTextSize(16);
        ttime.setPadding(alertNameConfig.getPaddingLeft(),alertNameConfig.getPaddingTop(), alertNameConfig.getPaddingRight(), alertNameConfig.getPaddingBottom());
        ttime.setTextColor(alertNameConfig.getCurrentTextColor());
        ttime.setTextAlignment(alertNameConfig.getTextAlignment());
        newRow.addView(ttime);

        table.addView(newRow);
    }



}
