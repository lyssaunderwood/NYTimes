package com.example.lyssaunderwood.nytimessearch.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.lyssaunderwood.nytimessearch.DatePickerFragment;
import com.example.lyssaunderwood.nytimessearch.Filters;
import com.example.lyssaunderwood.nytimessearch.R;

import org.parceler.Parcels;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FilterActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener{
    DatePickerFragment newFragment;

    @BindView(R.id.spSort) Spinner spSort;
    @BindView(R.id.etDate) EditText etDate;
    String date;
    Filters filter;
    //Spinner spSort;
    //EditText etDate;
    Calendar c;
    String stringMonth;
    String stringDay;
    int year;
    int month;
    int day;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);
        ButterKnife.bind(this);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#800080")));
        filter = new Filters(false, false, false, null, null);
        //spSort = (Spinner) findViewById(R.id.spSort);
        //etDate = (EditText) findViewById(R.id.etDate);
        etDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                c = Calendar.getInstance();
                year = c.get(Calendar.YEAR);
                month = c.get(Calendar.MONTH);
                day = c.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePicker = new DatePickerDialog(FilterActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int currYear, int monthOfYear, int dayOfMonth) {
                        if (monthOfYear < 9) {
                            stringMonth = "0" + String.valueOf(monthOfYear + 1);
                        } else {
                            stringMonth = String.valueOf(monthOfYear + 1);
                        }

                        if (dayOfMonth < 10) {
                            stringDay = "0" + String.valueOf(dayOfMonth);
                        } else {
                            stringDay = String.valueOf(dayOfMonth);
                        }

                        date = String.valueOf(currYear) + stringMonth + stringDay;

                        String showDate = String.valueOf(monthOfYear + 1) + " - " + String.valueOf(dayOfMonth) + " - " + String.valueOf(currYear);
                        etDate.setText(showDate);
                    }
                }, year, day, month);
                datePicker.setTitle("Select Date");
                datePicker.show();
            }
        });
    }

    public void onSave(View view) {
        //Toast.makeText(getApplicationContext(), newFragment.getDate(), Toast.LENGTH_SHORT).show();
        //DatePickerFragment date = new DatePickerFragment();
        String spinnerVal = spSort.getSelectedItem().toString();
        //Toast.makeText(getApplicationContext(), spinnerVal, Toast.LENGTH_SHORT).show();

        filter.setDate(date);
        filter.setSpinnerVal(spinnerVal);

        Intent k = new Intent();
        k.putExtra("vals", Parcels.wrap(filter));
        setResult(RESULT_OK, k);
        finish();

    }

    // attach to an onclick handler to show the date picker
    public void showDatePickerDialog(View v) {
        newFragment = new DatePickerFragment();
        newFragment.show(getFragmentManager(), "datePicker");
    }

    // handle the date selected
    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        Toast.makeText(getApplicationContext(), "Text Activity", Toast.LENGTH_SHORT).show();

        // store the values selected into a Calendar instance
        c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, monthOfYear);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        //updateLabel();

//        etDate = (EditText) findViewById(R.id.etDate);
//        etDate.setText(newFragment.getDate());


    }

//    private void updateLabel() {
//        String myFormat = "YYYY/DD/MM";
//        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
//        etDate = (EditText) findViewById(R.id.etDate);
//        etDate.setText(sdf.format(c.getTime()));
//    }

    public void onCheckboxClicked(View view) {
        // Is the view now checked?
        boolean checked = ((CheckBox) view).isChecked();

        // Check which checkbox was clicked
        switch(view.getId()) {
            case R.id.cbArts:
                if (checked) {
                    //Toast.makeText(getApplicationContext(), "Arts", Toast.LENGTH_SHORT).show();
                    filter.setArts(true);
                } else {
                    // none
                    filter.setArts(false);
                }
                break;
            case R.id.cbFashion:
                if (checked) {
                    //Toast.makeText(getApplicationContext(), "Fashion", Toast.LENGTH_SHORT).show();
                    filter.setFashion(true);
                } else {
                    // none
                    filter.setFashion(false);
                }
                break;
            case R.id.cbSports:
                if (checked) {
                    //Toast.makeText(getApplicationContext(), "Sports", Toast.LENGTH_SHORT).show();
                    filter.setSports(true);
                } else {
                    // none
                    filter.setSports(false);
                }
                break;
        }
    }

}

