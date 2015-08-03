package com.cm_smarthome.professorshakecrfs;

import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class History extends ActionBarActivity {

    ServerDB serverDB = new ServerDB();

    Context context = this;

    private String jsonResult;
    private ListView listView;
    private TextView tvShowHistory;

    private String SUBJECT_ID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        SUBJECT_ID = getIntent().getStringExtra("SubjectID");

        listView = (ListView) findViewById(R.id.listView);
        tvShowHistory = (TextView) findViewById(R.id.tvShowHistory);

        myAsyncTaskGetCountSubjectID taskGetCountSubjectID = new myAsyncTaskGetCountSubjectID();
        taskGetCountSubjectID.execute(SUBJECT_ID);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_history, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class myAsyncTaskGetCountSubjectID extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            serverDB.getCountSubjectID(params[0]);
            serverDB.getCountSubjectIDCH(params[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            tvShowHistory.setText("ประวัติการเช็คชื่อในรายวิชา : " + SUBJECT_ID +
                    "\n" + "มีการเปิดเช็คชื่อทั้งหมด : " + serverDB.COUNT_SUBJECT_ID1 + " ครั้ง" +
                    "\n" + "นิสิตเข้าเช็คชื่อทั้งหมด : " + serverDB.COUNT_SUBJECT_ID2 + " ครั้ง");

            JsonReadTask jsonReadTask = new JsonReadTask();
            jsonReadTask.execute(SUBJECT_ID);
        }
    }

    // Async Task to access the web
    private class JsonReadTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {

            String url = "http://www.cm-smarthome.com/reg/getHistoryPro.php";

            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("sSID", params[0]));

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(url);

            try {
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            try {
                HttpResponse response = httpclient.execute(httppost);
                jsonResult = inputStreamToString(
                        response.getEntity().getContent()).toString();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        private StringBuilder inputStreamToString(InputStream is) {
            String rLine = "";
            StringBuilder answer = new StringBuilder();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));

            try {
                while ((rLine = rd.readLine()) != null) {
                    answer.append(rLine);
                }
            } catch (IOException e) {
                // e.printStackTrace();
                ShowAlertDialog("ไม่พบข้อมูล");
            }
            return answer;
        }

        @Override
        protected void onPostExecute(String result) {
            ListDrwaer();
        }
    }// end async task

    // build hash set for list view
    public void ListDrwaer() {
        List<Map<String, String>> employeeList = new ArrayList<Map<String, String>>();

        try {
            JSONObject jsonResponse = new JSONObject(jsonResult);
            JSONArray jsonMainNode = jsonResponse.optJSONArray("emp_info");

            for (int i = 0; i < jsonMainNode.length(); i++) {
                JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
                String number = jsonChildNode.optString("StudentID");
                String x = jsonChildNode.optString("Type");
                String x1 = jsonChildNode.optString("Date");

                String outPut = "รหัสนิสิต : " + number + "\n" + "รูปแบบการเช็คชื่อ : " + x
                        + "\n" + "วัน/เดือน/ปี : " + x1.replaceAll(" ", " เวลา : ");

                employeeList.add(createEmployee("employees", outPut));

            }
        } catch (JSONException e) {
            ShowAlertDialog("ไม่พบข้อมูล");
        }

        SimpleAdapter simpleAdapter = new SimpleAdapter(context, employeeList,
                android.R.layout.simple_list_item_1,
                new String[]{"employees"}, new int[]{android.R.id.text1});
        listView.setAdapter(simpleAdapter);
    }

    private HashMap<String, String> createEmployee(String name, String number) {
        HashMap<String, String> employeeNameNo = new HashMap<String, String>();
        employeeNameNo.put(name, number);
        return employeeNameNo;
    }

    public void ShowAlertDialog(String input) {
        final AlertDialog.Builder dDialog = new AlertDialog.Builder(context);
        dDialog.setMessage(input);
        dDialog.setPositiveButton("ปิด", null);
        dDialog.show();
    }
}
