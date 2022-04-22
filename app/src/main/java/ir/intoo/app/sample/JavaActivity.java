package ir.intoo.app.sample;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import ir.intoo.api.tracker.TrackerInJava;

public class JavaActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_java);

        new TrackerInJava();
    }
}