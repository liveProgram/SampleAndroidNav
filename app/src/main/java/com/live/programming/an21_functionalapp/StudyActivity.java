package com.live.programming.an21_functionalapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class StudyActivity extends AppCompatActivity {


    Study studyInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study);

        studyInfo = (Study) getIntent().getSerializableExtra("all_info");


    }
}