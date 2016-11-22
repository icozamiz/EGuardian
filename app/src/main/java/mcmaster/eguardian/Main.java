package mcmaster.eguardian;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;

import mcmaster.eguardian.domain.Alert;

/**
 * Created by i on 2016-11-20.
 */

public class Main extends AppCompatActivity{

            private ImageButton alertsButton;
            private ImageButton activityButton;
            private ImageButton sleepButton;
            private ImageButton dashboardButton;

            @Override
            protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.content_main);
                ImageButton alertsButton = (ImageButton) findViewById(R.id.AlertsButton);

                ImageButton activityButton = (ImageButton) findViewById(R.id.ActivityButton);

                ImageButton sleepButton = (ImageButton) findViewById(R.id.SleepActivityButton);

                ImageButton dashboardButton = (ImageButton) findViewById(R.id.dashBoardButton);

                alertsButton.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        Intent i = new Intent(Main.this, Alerts.class);
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

            public void onButtonClick(View v) {
                if (v.getId() == R.id.AlertsButton) {
                    Intent i = new Intent(Main.this, Alerts.class);
                    startActivity(i);
                }
            }

            public void onButtonClick2(View v) {
                if (v.getId() == R.id.ActivityButton) {
                    Intent j = new Intent(Main.this, HeartRate.class);
                    startActivity(j);
                }

            }

            public void onButtonClick3(View v) {
                if (v.getId() == R.id.SleepActivityButton) {
                    Intent k = new Intent(Main.this, SleepActivity.class);
                    startActivity(k);
                }
            }


}
