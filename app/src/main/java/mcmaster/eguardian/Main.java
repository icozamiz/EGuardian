package mcmaster.eguardian;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

/**
 * Created by i on 2016-11-20.
 */

public class Main extends AppCompatActivity{

            @Override
            protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.activity_main);
            }

            public void onButtonClick(View v) {
                if (v.getId() == R.id.alerts) {
                    Intent i = new Intent(Main.this, Alerts.class);
                    startActivity(i);
                }
            }

            public void onButtonClick2(View v) {
                if (v.getId() == R.id.heartRate) {
                    Intent j = new Intent(Main.this, HeartRate.class);
                    startActivity(j);
                }

            }

            public void onButtonClick3(View v) {
                if (v.getId() == R.id.sleepActivity) {
                    Intent k = new Intent(Main.this, SleepActivity.class);
                    startActivity(k);
                }
            }


}
