package com.tech42.mari.taskmanager;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by mari on 12/13/16.
 */

public class Addtask extends Activity {

    EditText task, dat, tim;
    Spinner spinner;
    String prior[] = new String[]{"High", "Medium", "Low"};
    ImageButton date, time;
    Calendar cal = Calendar.getInstance();
    Button button;

    ArrayList<Calendar> calender = new ArrayList();
    int getMinute=0;
    int getHour=0;
    int getDay=0;
    int getYear=0;
    int getMonth=0;
    String format="";
    private int year = cal.get(Calendar.YEAR);
    private int month = cal.get(Calendar.MONTH);
    private int day = cal.get(Calendar.DATE);
    int hour = cal.get(Calendar.HOUR_OF_DAY);
    int minute = cal.get(Calendar.MINUTE);
    static final int DATE_DIALOG_ID = 0;
    static final int TIME_DIALOG_ID = 1;
    static int req=0;

    AlarmManager manager;
    Intent myIntent;
    PendingIntent pendingIntent;
    private ProgressDialog pDialog;

    JSONParser jsonParser = new JSONParser();
    private static String url_create_task = "http://www.bestandroidtrainingchennai.in/androidapi/service/mari/create_task.php";
    private static final String TAG_SUCCESS = "success";

    String tasks="";
    String dates="";
    String priorities="";
    String times="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addtask);
        spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter aa = new ArrayAdapter(this, android.R.layout.simple_spinner_item, prior);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(aa);
        manager = (AlarmManager)getSystemService(ALARM_SERVICE);
        button = (Button) findViewById(R.id.button);
        task = (EditText) findViewById(R.id.editText);
        dat = (EditText) findViewById(R.id.editText2);
        tim = (EditText) findViewById(R.id.editText3);
        dat.setEnabled(false);
        tim.setEnabled(false);
        date = (ImageButton) findViewById(R.id.imageButton);
        time = (ImageButton) findViewById(R.id.imageButton2);
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog(DATE_DIALOG_ID);
            }
        });
        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog(TIME_DIALOG_ID);
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!task.getText().toString().isEmpty() && !dat.getText().toString().isEmpty() && !tim.getText().toString().isEmpty() && !spinner.getSelectedItem().toString().isEmpty()) {
                    new CreateNewTask().execute();

                    setAlarm(task.getText().toString() , getYear , getMonth , getDay , getHour , getMinute , format , req);
                    req++;
                    Intent i = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(i);
                } else {
                    Toast.makeText(getApplicationContext(), "Fill All the Fields", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DATE_DIALOG_ID:
                return new DatePickerDialog(this, datePickerListener,
                        year, month, day);
            case TIME_DIALOG_ID:
                return new TimePickerDialog(this, timePickerListener, hour, minute, false);
        }
        return null;
    }

    private TimePickerDialog.OnTimeSetListener timePickerListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker timePicker, int i, int i1) {
            hour = i;
            minute = i1;
            getMinute=i1;
            getHour=i;
            format=getFormat();
            tim.setText(new StringBuilder().append(i)
                    .append(" ").append(":").append(" ").append(minute).append(" ").append(format));
        }
    };

    @NonNull
    private String getFormat() {

        if (hour == 0) {
            hour += 12;
            format = "AM";
        } else if (hour == 12) {
            format = "PM";
        } else if (hour > 12) {
            hour -= 12;
            format = "PM";
        } else {
            format = "AM";
        }
        return format;
    }

    private DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {

        public void onDateSet(DatePicker view, int selectedYear,
                              int selectedMonth, int selectedDay) {
            year = selectedYear;
            month = selectedMonth;
            day = selectedDay;
            getYear= year;
            getMonth=month;
            getDay=day;

            dat.setText(new StringBuilder().append(month + 1)
                    .append("-").append(day).append("-").append(year)
                    .append(" "));

        }
    };

    private  void setAlarm(String task , int getYear , int getMonth , int getDay , int getHour , int getMinute , String format , int req)
    {
        Calendar newcalendar = Calendar.getInstance();

        newcalendar.set(Calendar.MONTH, getMonth);
        newcalendar.set(Calendar.YEAR, getYear);
        newcalendar.set(Calendar.DAY_OF_MONTH, getDay);

        newcalendar.set(Calendar.HOUR_OF_DAY, getHour);
        newcalendar.set(Calendar.MINUTE, getMinute);
        newcalendar.set(Calendar.SECOND, 0);
        newcalendar.set(Calendar.MILLISECOND , 0);
        if(format.equals("PM")) {
            newcalendar.set(Calendar.AM_PM, Calendar.PM);
        }
        else if(format.equals("AM")) {
            newcalendar.set(Calendar.AM_PM, Calendar.AM);
        }

        calender.add(newcalendar);

        myIntent = new Intent(Addtask.this , MyReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(Addtask.this, (int) System.currentTimeMillis(), myIntent, 0);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, newcalendar.getTimeInMillis(), pendingIntent);
        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            manager.setExact(AlarmManager.RTC_WAKEUP, newcalendar.getTimeInMillis(), pendingIntent);
        else
            manager.set(AlarmManager.RTC_WAKEUP, newcalendar.getTimeInMillis(), pendingIntent);
     }

    class CreateNewTask extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Addtask.this);
            pDialog.setMessage("Creating Task..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        protected String doInBackground(String... args) {

            Addtask.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tasks = task.getText().toString();
                    dates = dat.getText().toString();
                    priorities = spinner.getSelectedItem().toString();
                    times = tim.getText().toString();
                }
            });

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("task", tasks));
            params.add(new BasicNameValuePair("date", dates));
            params.add(new BasicNameValuePair("priority", priorities));
            params.add(new BasicNameValuePair("time", times));

            JSONObject json = jsonParser.makeHttpRequest(url_create_task, "POST", params);
            Log.d("Create Response", json.toString());
            try {
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    Intent i = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(i);
                    finish();
                } else {
                    // failed to create task
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(String file_url) {
            pDialog.dismiss();
        }
    }
}