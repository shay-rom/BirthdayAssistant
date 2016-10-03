package com.srh.birthdayassistant;

import android.support.annotation.NonNull;
import android.os.Bundle;

import java.util.Calendar;

import notifications.NotificationManager;
import utils.BaseActivity;

public class ContactListActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);
    }
}
