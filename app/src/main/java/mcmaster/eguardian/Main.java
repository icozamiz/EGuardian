package mcmaster.eguardian;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import mcmaster.eguardian.domain.Alert;

/**
 * Created by i on 2016-11-20.
 */

public class Main extends AppCompatActivity{
            private int numSteps = 100;
            private int bpm = 76;
            private double sleepHrs = 7.5;


            @Override
            protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.content_main);
                ImageButton alertsButton = (ImageButton) findViewById(R.id.AlertsButton);
                ImageButton activityButton = (ImageButton) findViewById(R.id.ActivityButton);
                ImageButton sleepButton = (ImageButton) findViewById(R.id.SleepActivityButton);
                ImageButton dashboardButton = (ImageButton) findViewById(R.id.dashBoardButton);
                final TextView sleepPattern = (TextView) findViewById(R.id.sleepPattern);
                final TextView activity = (TextView) findViewById(R.id.activity);
                final TextView heartRate = (TextView) findViewById(R.id.heartRate);

                Thread t = new Thread() {

                    @Override
                    public void run() {
                        try {
                            while (!isInterrupted()) {
                                Thread.sleep(1000);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        numSteps++;
                                        if (bpm == 76){
                                            bpm = 72;
                                        }else{
                                            bpm = 76;
                                        }
                                        sleepPattern.setText("Sleep Pattern: " + sleepHrs);
                                        activity.setText("Activity: "+ numSteps);
                                        heartRate.setText("Heart Rate: " +  bpm);
                                    }
                                });
                            }
                        } catch (InterruptedException e) {
                        }
                    }
                };

                t.start();

                sleepButton.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        Intent i = new Intent(Main.this, SleepActivity.class);
                        startActivity(i);
                    }
                });
                activityButton.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        Intent i = new Intent(Main.this, HeartRate.class);
                        startActivity(i);
                    }
                });
                dashboardButton.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        Intent i = new Intent(Main.this, Main.class);
                        startActivity(i);
                    }
                });
                alertsButton.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        Intent i = new Intent(Main.this, Alerts.class);
                        startActivity(i);
                    }
                });
            }


}
