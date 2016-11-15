package mcmaster.eguardian;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class Alerts extends AppCompatActivity {
    private Button button;
    private TextView result;
    final Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alerts);


        // components from main.xml
        button = (Button) findViewById(R.id.buttonPrompt);
        result = (TextView) findViewById(R.id.alertResult);

        // add button listener
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                // get prompts.xml view
                LayoutInflater li = LayoutInflater.from(context);
                View promptsView = li.inflate(R.layout.prompts, null);

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
                                        update(alertName.getText().toString(), message.getText().toString(), setBy.getText().toString());
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

    private void update(String alertName, String message, String setBy) {
        TableLayout table = (TableLayout)findViewById(R.id.table);

        TableRow headers = (TableRow)findViewById(R.id.alertHeader);
        TextView alertNameConfig = (TextView) findViewById(R.id.alertName);
        TextView messageConfig = (TextView) findViewById(R.id.message);
        TextView setByConfig = (TextView) findViewById(R.id.setBy);

        TableRow newRow = new TableRow(this);
        newRow.setLayoutParams(headers.getLayoutParams());
        TextView aName = new TextView(this);
        aName.setLayoutParams(alertNameConfig.getLayoutParams());
        aName.setText(alertName);
        newRow.addView(aName);

        TextView m = new TextView(this);
        m.setLayoutParams(messageConfig.getLayoutParams());
        m.setText(message);
        newRow.addView(m);

        TextView sB = new TextView(this);
        sB.setLayoutParams(setByConfig.getLayoutParams());
        sB.setText(setBy);
        newRow.addView(sB);

        table.addView(newRow);

    }
}
