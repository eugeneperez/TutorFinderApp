package com.mobdeve.tutorfinderapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.ArrayList;

public class SearchPage extends AppCompatActivity {

    private Spinner spinnerAdapter;
    private EditText search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_page);

        spinnerAdapter = findViewById(R.id.spinner_searchpage);

        ArrayList<String> arrayList = new ArrayList<>();

        arrayList.add("People");
        arrayList.add("Category");
        arrayList.add("Specialization");

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, arrayList);

        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAdapter.setAdapter(arrayAdapter);
    }
}