package com.tech42.mari.taskmanager;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.R.attr.id;
import static com.tech42.mari.taskmanager.R.id.task;

public class MainActivity extends AppCompatActivity {


    ListView listView;
    ProgressDialog progressDialog;
    JSONParser parser = new JSONParser();

    ArrayList<HashMap<String, String>> tasksList;

    private static String url_all_tasks = "http://www.bestandroidtrainingchennai.in/androidapi/service/mari/get_all_tasks.php";

    private static final String TAG_SUCCESS = "success";
    private static final String TAG_TASKS = "Todolist";
    private static final String TAG_TID = "tid";
    private static final String TAG_TASK = "task";
    private static final String TAG_DATE = "date";
    private static final String TAG_PRIORITY = "priority";
    private static final String TAG_TIME = "time";

    JSONArray tasks = null;

    private String task[];
    private String date[];
    private String priority[];
    private String time[];
    int i=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i =new Intent(MainActivity.this , Addtask.class);
                startActivity(i);
            }
        });

        tasksList = new ArrayList<HashMap<String, String>>();

        new LoadAllProducts().execute();

        listView = (ListView) findViewById(R.id.listView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String task = ((TextView) view.findViewById(R.id.task)).getText().toString();
                Intent in = new Intent(getApplicationContext(), UpdateTask.class);
                in.putExtra(TAG_TASK, task);
                startActivityForResult(in, 100);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


class LoadAllProducts extends AsyncTask<String, String, String> {

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setMessage("Loading tasks. Please wait...");
        progressDialog.setIndeterminate(false);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    protected String doInBackground(String... args) {

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        JSONObject json = parser.makeHttpRequest(url_all_tasks, "GET", params);

        try {
            int success = json.getInt(TAG_SUCCESS);

            if (success == 1) {
                Log.d("All Tasks: ", json.toString());
                tasks = json.getJSONArray(TAG_TASKS);
                task = new String[tasks.length()];
                date = new String[tasks.length()];
                time = new String[tasks.length()];
                priority = new String[tasks.length()];

                for (int i = 0; i < tasks.length(); i++) {
                    JSONObject c = tasks.getJSONObject(i);

                    task[i] = c.getString(TAG_TASK);
                    date[i] = c.getString(TAG_DATE);
                    priority[i] = c.getString(TAG_PRIORITY);
                    time[i] = c.getString(TAG_TIME);

                    /*HashMap<String, String> map = new HashMap<String, String>();
                    map.put(TAG_TID, id);
                    map.put(TAG_TASK, task);
                    map.put(TAG_DATE, date);
                    map.put(TAG_PRIORITY, priority);
                    map.put(TAG_TIME , time);
                    tasksList.add(map);*/
                }
            } else {
                i=1;
                Intent i = new Intent(getApplicationContext(), Addtask.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    protected void onPostExecute(String file_url) {

        progressDialog.dismiss();
        runOnUiThread(new Runnable() {
            public void run() {
                if(i==0) {
                    CustomAdapter adapter = new CustomAdapter(MainActivity.this, task, date, priority, time);
                    listView.setAdapter(adapter);
                }
            }
        });
    }
  }
 }