package com.tech42.mari.taskmanager;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by mari on 12/15/16.
 */

public class UpdateTask extends Activity {

    EditText task, dat, tim, priority;
    Spinner spinner;
    String prior[] = new String[]{"High", "Medium", "Low"};
    String oldtask="";
    String olddate="";
    String oldprior;
    String oldtime="";
    String newtask="";String newdate="";String newtime="";String newpriority;
    ImageButton date , time;
    Calendar cal = Calendar.getInstance();
    Button button , delete;
    Intent i;
    Bundle extras;
    int y,m,d,h,mi=0;
    private int year=cal.get(Calendar.YEAR);
    private int month=cal.get(Calendar.MONTH);
    private int day=cal.get(Calendar.DATE);
    int hour = cal.get(Calendar.HOUR_OF_DAY);
    int minute = cal.get(Calendar.MINUTE);
    final Context context = this;
    static final int DATE_DIALOG_ID = 0;
    static final int TIME_DIALOG_ID = 1;

    private ProgressDialog pDialog;
    JSONParser jsonParser = new JSONParser();
    private static final String url_update_task = "http://www.bestandroidtrainingchennai.in/androidapi/service/mari/update_task.php";
    private static final String url_delete_task = "http://www.bestandroidtrainingchennai.in/androidapi/service/mari/delete_task.php";
    private static final String url_task_details = "http://www.bestandroidtrainingchennai.in/androidapi/service/mari/sget_task_details.php";

    private static final String TAG_SUCCESS = "success";
    private static final String TAG_TID = "tid";
    private static final String TAG_TASK = "task";
    private static final String TAG_DATE = "date";
    private static final String TAG_PRIORITY = "priority";
    private static final String TAG_TIME = "time";

    String tasks="";
    String dates="";
    String priorities="";
    String times="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edittask);
        spinner = (Spinner) findViewById(R.id.editprior);
        ArrayAdapter aa = new ArrayAdapter(this , android.R.layout.simple_spinner_item , prior);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(aa);
        button = (Button) findViewById(R.id.addbutton);
        task = (EditText) findViewById(R.id.editutask);
        dat = (EditText) findViewById(R.id.editudate);
        tim = (EditText) findViewById(R.id.edittime);
        dat.setEnabled(false);
        tim.setEnabled(false);
        date = (ImageButton) findViewById(R.id.imagedate);
        time = (ImageButton)findViewById(R.id.imagetime);
        extras = getIntent().getExtras();
        newtask = getIntent().getStringExtra(TAG_TASK);
        newdate = extras.getString("Date");
        newtime = extras.getString("Time");
        newpriority = extras.getString("Prior");

        task.setText(newtask);
        dat.setText(newdate);
        tim.setText(newtime);
        if(newpriority.equals("High")){ spinner.setSelection(0);}
        if(newpriority.equals("Low")){ spinner.setSelection(2);}
        if(newpriority.equals("Medium")){ spinner.setSelection(1);}
        oldtask=task.getText().toString();
        olddate=dat.getText().toString();
        oldtime=tim.getText().toString();

        oldprior=spinner.getSelectedItem().toString();
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
        new GetTaskDetails().execute();
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!task.getText().toString().isEmpty() && !dat.getText().toString().isEmpty() && !tim.getText().toString().isEmpty() && !spinner.getSelectedItem().toString().isEmpty())
                {
                    new SaveTaskDetails().execute();
                    Toast.makeText(getApplicationContext(), "One Task Updated", Toast.LENGTH_SHORT).show();
                    i = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(i);
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Fill All the Fields", Toast.LENGTH_SHORT).show();
                }
            }
        });
         delete = (Button) findViewById(R.id.button2);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                alertDialogBuilder.setTitle("Confirmation to Delete Task");
                alertDialogBuilder

                        .setCancelable(false)
                        .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                new DeleteTask().execute();
                                Toast.makeText(getApplicationContext(), "One Task Deleted", Toast.LENGTH_SHORT).show();
                                i = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(i);
                            }
                        })
                        .setNegativeButton("No",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();

            }
        });
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DATE_DIALOG_ID:
                return new DatePickerDialog(this, datePickerListener,
                        year, month,day);
            case TIME_DIALOG_ID:
                return new TimePickerDialog(this , timePickerListener , hour , minute , false);
        }
        return null;
    }

    private TimePickerDialog.OnTimeSetListener timePickerListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker timePicker, int i, int i1) {
            hour = i;
            minute = i1;
            h=i;mi=i1;
            String format="";
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
            tim.setText(new StringBuilder().append(i)
                    .append(" ").append(":").append(" ").append(minute).append(" ").append(format));

        }
    };

    private DatePickerDialog.OnDateSetListener datePickerListener
            = new DatePickerDialog.OnDateSetListener() {

        public void onDateSet(DatePicker view, int selectedYear,
                              int selectedMonth, int selectedDay) {
            year = selectedYear;
            month = selectedMonth;
            day = selectedDay;
            y=selectedYear;
            m=selectedMonth+1;
            d=selectedDay;

            dat.setText(new StringBuilder().append(month + 1)
                    .append("-").append(day).append("-").append(year)
                    .append(" "));

        }
    };


    class SaveTaskDetails extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(UpdateTask.this);
            pDialog.setMessage("Saving task ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        protected String doInBackground(String... args) {

            UpdateTask.this.runOnUiThread(new Runnable() {
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

            JSONObject json = jsonParser.makeHttpRequest(url_update_task, "POST", params);

            try {
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    Intent i = getIntent();
                    setResult(100, i);
                    finish();
                } else {
                    // failed to update task
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

    class DeleteTask extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(UpdateTask.this);
            pDialog.setMessage("Deleting Task...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        protected String doInBackground(String... args) {

            int success;
            try {
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("task", newtask));

                JSONObject json = jsonParser.makeHttpRequest(url_delete_task, "POST", params);
                Log.d("Delete Task", json.toString());

                success = json.getInt(TAG_SUCCESS);
                if (success == 1) {
                    Intent i = getIntent();
                    setResult(100, i);
                    finish();
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

    class GetTaskDetails extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(UpdateTask.this);
            pDialog.setMessage("Loading task details. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        protected String doInBackground(String... params) {

            runOnUiThread(new Runnable() {
                public void run() {
                    int success;
                    try {
                        List<NameValuePair> params = new ArrayList<NameValuePair>();
                        params.add(new BasicNameValuePair("task", newtask));

                        JSONObject json = jsonParser.makeHttpRequest(url_task_details, "GET", params);
                        Log.d("Single Product Details", json.toString());

                        success = json.getInt(TAG_SUCCESS);
                        if (success == 1) {

                            JSONArray productObj = json.getJSONArray(TAG_TASK);
                            JSONObject product = productObj.getJSONObject(0);
                            task.setText(product.getString(TAG_TASK));
                            dat.setText(product.getString(TAG_DATE));
                            priority.setText(product.getString(TAG_PRIORITY));
                            tim.setText(product.getString(TAG_TIME));


                        }else{
                            // task not found
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
            return null;
        }

        protected void onPostExecute(String file_url) {
            pDialog.dismiss();
        }
    }
}
