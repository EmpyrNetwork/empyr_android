package com.empyrdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.empyr.api.EmpyrClient;
import com.empyr.tracker.EmpyrTracker;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EmpyrClient client = EmpyrClient.getInstance( "123345-01949949-029928818849938" );
        EmpyrTracker tracker = EmpyrTracker.getInstance( client, this );

        tracker.track( 12345, EmpyrTracker.Tracker.PROFILE_VIEW );
        tracker.track( 67890, EmpyrTracker.Tracker.PROFILE_VIEW );

        setContentView(R.layout.activity_main);
    }
}
