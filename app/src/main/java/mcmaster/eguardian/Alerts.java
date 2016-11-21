package mcmaster.eguardian;

import android.app.Activity;
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

import com.orm.SugarContext;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import mcmaster.eguardian.domain.Alert;

@RequiresApi(api = Build.VERSION_CODES.N)
public class Alerts extends Activity {
    private Button button;
    final Context context = this;
   Calendar myCalendar = Calendar.getInstance();


    @Override
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alerts);
        SugarContext.init(context);

        // components from main.xml
        button = (Button) findViewById(R.id.buttonPrompt);

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
                                        Alert alert = new Alert(alertName.getText().toString(), message.getText().toString(), setBy.getText().toString(), time.getText().toString());
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

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void update(Alert alert) {
        TableLayout table = (TableLayout)findViewById(R.id.table);

        TableRow headers = (TableRow)findViewById(R.id.alertHeader);
        TextView alertNameConfig = (TextView) findViewById(R.id.alertName);
        TextView messageConfig = (TextView) findViewById(R.id.message);
        TextView dateConfig = (TextView) findViewById(R.id.setBy);
        TextView timeConfig = (TextView) findViewById(R.id.time);
        LinearLayout dividerConfig = (LinearLayout) findViewById(R.id.divider);

        TableRow newRow = new TableRow(this);
        newRow.setLayoutParams(headers.getLayoutParams());
        newRow.setBackgroundColor(Color.parseColor("#303F9F"));
        newRow.setPadding(headers.getPaddingLeft(),headers.getPaddingTop(), headers.getPaddingRight(), headers.getPaddingBottom());

        LinearLayout div = new LinearLayout(this);
        div.setLayoutParams(dividerConfig.getLayoutParams());
        div.setBackgroundColor(dividerConfig.getSolidColor());
        newRow.addView(div);

        TextView aName = new TextView(this);
        aName.setLayoutParams(alertNameConfig.getLayoutParams());
        aName.setText(alert.getName());
        aName.setTextSize(16);
        aName.setPadding(alertNameConfig.getPaddingLeft(),alertNameConfig.getPaddingTop(), alertNameConfig.getPaddingRight(), alertNameConfig.getPaddingBottom());
        aName.setTextColor(alertNameConfig.getCurrentTextColor());
        aName.setTextAlignment(alertNameConfig.getTextAlignment());
        newRow.addView(aName);

        LinearLayout divm = new LinearLayout(this);
        divm.setLayoutParams(dividerConfig.getLayoutParams());
        divm.setBackgroundColor(dividerConfig.getSolidColor());
        newRow.addView(divm);

        TextView m = new TextView(this);
        m.setLayoutParams(messageConfig.getLayoutParams());
        m.setText(alert.getMessage());
        m.setTextSize(16);
        m.setPadding(messageConfig.getPaddingLeft(),messageConfig.getPaddingTop(),
                messageConfig.getPaddingRight(), messageConfig.getPaddingBottom());
        m.setTextColor(messageConfig.getCurrentTextColor());
        m.setTextAlignment(messageConfig.getTextAlignment());
        newRow.addView(m);

        LinearLayout divs = new LinearLayout(this);
        divs.setLayoutParams(dividerConfig.getLayoutParams());
        divs.setBackgroundColor(dividerConfig.getSolidColor());
        newRow.addView(divs);

        TextView date = new TextView(this);
        date.setLayoutParams(dateConfig.getLayoutParams());
        date.setText(alert.getDate());
        date.setTextSize(16);
        date.setPadding(dateConfig.getPaddingLeft(),dateConfig.getPaddingTop(),
                dateConfig.getPaddingRight(), dateConfig.getPaddingBottom());
        date.setTextColor(dateConfig.getCurrentTextColor());
        date.setTextAlignment(dateConfig.getTextAlignment());
        newRow.addView(date);

        LinearLayout divt = new LinearLayout(this);
        divt.setLayoutParams(dividerConfig.getLayoutParams());
        divt.setBackgroundColor(dividerConfig.getSolidColor());
        newRow.addView(divt);

        TextView time = new TextView(this);
        time.setLayoutParams(alertNameConfig.getLayoutParams());
        time.setText(alert.getTime());
        time.setTextSize(16);
        time.setPadding(alertNameConfig.getPaddingLeft(),alertNameConfig.getPaddingTop(),
                alertNameConfig.getPaddingRight(), alertNameConfig.getPaddingBottom());
        time.setTextColor(alertNameConfig.getCurrentTextColor());
        time.setTextAlignment(alertNameConfig.getTextAlignment());
        newRow.addView(time);

        table.addView(newRow);
    }


}
