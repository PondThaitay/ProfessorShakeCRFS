package com.cm_smarthome.professorshakecrfs;

import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by AdminPond on 6/5/2558.
 */
public class ServerDB {

    private int StatusINsert;
    protected String COUNT_SUBJECT_ID1;
    protected String COUNT_SUBJECT_ID2;

    //Insert
    public void Insert(String SubjectID, String Lat, String Long, String Status) {

        String url = "http://www.cm-smarthome.com/reg/professor.php";

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("sSubjectID", SubjectID));
        params.add(new BasicNameValuePair("sLatitude", Lat));
        params.add(new BasicNameValuePair("sLongitude", Long));
        params.add(new BasicNameValuePair("sStatus", Status));

        String resultServer = getHttpPost(url, params);

        String strStatusID = "0";
        //String strError = "Unknow Status!";

        JSONObject c;
        try {
            c = new JSONObject(resultServer);
            strStatusID = c.getString("StatusID");
            //strError = c.getString("Error");
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // Prepare Save Data
        if (strStatusID.equals("0")) {
            StatusINsert = 0;
        } else {
            StatusINsert = 1;
        }
    }
    //end Insert

    //Update
    public void Update(String SubjectID, String Lat, String Long, String Status) {

        String url = "http://www.cm-smarthome.com/reg/update.php";

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("sSubjectID", SubjectID));
        params.add(new BasicNameValuePair("sLatitude", Lat));
        params.add(new BasicNameValuePair("sLongitude", Long));
        params.add(new BasicNameValuePair("sStatus", Status));

        String resultServer = getHttpPost(url, params);

        String strStatusID = "0";
        //String strError = "Unknow Status!";

        JSONObject c;
        try {
            c = new JSONObject(resultServer);
            strStatusID = c.getString("StatusID");
            //strError = c.getString("Error");
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // Prepare Save Data
        if (strStatusID.equals("0")) {
            StatusINsert = 0;
        } else {
            StatusINsert = 1;
        }
    }
    //end update

    //Insert CheckName
    public void InsertCheckName(String StudentID, String SubjectID, String Type) {
        String url = "http://www.cm-smarthome.com/reg/checkname.php";

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("sStudentID", StudentID));
        params.add(new BasicNameValuePair("sSubjectID", SubjectID));
        params.add(new BasicNameValuePair("sType", Type));

        String resultServer = getHttpPost(url, params);

        String strStatusID = "0";
        JSONObject c;
        try {
            c = new JSONObject(resultServer);
            strStatusID = c.getString("StatusID");
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    //end InsertCheckName

    //Update StatusCheck
    public void UpdateStatusCheck(String SubjectID, String ID, String Qr, String Shake
            , String Barcode, String CheckList, String Qiz, String Time) {

        String url = "http://www.cm-smarthome.com/reg/statuscheck.php";

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("sSubjectID", SubjectID));
        params.add(new BasicNameValuePair("sID", ID));
        params.add(new BasicNameValuePair("sQr", Qr));
        params.add(new BasicNameValuePair("sShake", Shake));
        params.add(new BasicNameValuePair("sBarcode", Barcode));
        params.add(new BasicNameValuePair("sCheckList", CheckList));
        params.add(new BasicNameValuePair("sQiz", Qiz));
        params.add(new BasicNameValuePair("sPhase", Time));

        String resultServer = getHttpPost(url, params);

        String strStatusID = "0";
        //String strError = "Unknow Status!";

        JSONObject c;
        try {
            c = new JSONObject(resultServer);
            strStatusID = c.getString("StatusID");
            //strError = c.getString("Error");
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    //end Update StatusCheck

    //Check Out
    public void CheckOut(String SubjectID) {

        String url = "http://www.cm-smarthome.com/reg/updateStatusCheck.php";

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("sSubjectID", SubjectID));

        String resultServer = getHttpPost(url, params);

        String strStatusID = "0";
        //String strError = "Unknow Status!";

        JSONObject c;
        try {
            c = new JSONObject(resultServer);
            strStatusID = c.getString("StatusID");
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    //end Check Out

    //get Count SubjectID From statuscheck DB
    public void getCountSubjectID(String SubjectID) {
        String url = "http://www.cm-smarthome.com/reg/countSID.php";

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("sSID", SubjectID));

        String resultServer = getHttpPost(url, params);

        JSONObject c;
        try {
            c = new JSONObject(resultServer);
            COUNT_SUBJECT_ID1 = c.getString("CountSID");
            Log.e("COUNT______SubjectID1", SubjectID + "Count : " + COUNT_SUBJECT_ID1);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    //end get Count SubjectID From statuscheck DB

    //get Count SubjectID From checkname DB
    public void getCountSubjectIDCH(String SubjectID) {
        String url = "http://www.cm-smarthome.com/reg/countSIDPro.php";

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("sSID", SubjectID));

        String resultServer = getHttpPost(url, params);

        JSONObject c;
        try {
            c = new JSONObject(resultServer);
            COUNT_SUBJECT_ID2 = c.getString("CountSSID");
            Log.e("COUNT______SubjectID2", SubjectID + "Count : " + COUNT_SUBJECT_ID2);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    //end get Count SubjectID From checkname DB

    public String getHttpPost(String url, List<NameValuePair> params) {
        StringBuilder str = new StringBuilder();
        HttpClient client = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(url);

        try {
            httpPost.setEntity(new UrlEncodedFormEntity(params));
            HttpResponse response = client.execute(httpPost);
            StatusLine statusLine = response.getStatusLine();

            int statusCode = statusLine.getStatusCode();

            if (statusCode == 200)// Status OK
            {
                HttpEntity entity = response.getEntity();
                InputStream content = entity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(content));
                String line;

                while ((line = reader.readLine()) != null) {
                    str.append(line);
                }
            } else {
                Log.e("Log", "Failed to download result..");
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return str.toString();
    }
}