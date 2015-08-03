package com.cm_smarthome.professorshakecrfs;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.sql.Time;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Map;


public class MainActivity extends ActionBarActivity {

    ServerDB db = new ServerDB();
    Context context = this;
    GPSTracker gps;

    public static final int REQUEST_QR_SCAN = 4;

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private ShakeDetector mShakeDetector;
    private final String TAG = "Professor";
    private String longitude;
    private String latitude;
    private String SUBJECT_ID;
    private static final String STATUS_CHECKED_IN = "1";
    private static final String STATUS_CHECKED_OUT = "0";
    private int COUNT;

    private RadioGroup ans1;
    private int THMP;
    private Button btnChose;
    private RadioButton ans1_1, ans1_2, ans1_3, ans1_4, ans1_5;

    private Button btnScanBarcode;
    private Button btnHistory;
    private Button btnCheckout;

    //DecimalFormat df = new DecimalFormat("####0.0000");

    private Spinner spinner, spinner2;
    private ArrayList<String> subjectID = new ArrayList<String>();
    private ArrayList<String> time = new ArrayList<String>();
    private String TIME;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ans1 = (RadioGroup) findViewById(R.id.ans1);
        btnChose = (Button) findViewById(R.id.btnChose);

        ans1_1 = (RadioButton) findViewById(R.id.ans1_1);
        ans1_2 = (RadioButton) findViewById(R.id.ans1_2);
        ans1_3 = (RadioButton) findViewById(R.id.ans1_3);
        ans1_4 = (RadioButton) findViewById(R.id.ans1_4);
        ans1_5 = (RadioButton) findViewById(R.id.ans1_5);

        spinner = (Spinner) findViewById(R.id.spinner);
        spinner2 = (Spinner) findViewById(R.id.spinner2);

        btnScanBarcode = (Button) findViewById(R.id.btnBarcode);
        btnHistory = (Button) findViewById(R.id.btnHistory);
        btnCheckout = (Button) findViewById(R.id.btnCheckout);

        final AlertDialog.Builder popDialogQRCode = new AlertDialog.Builder(this);
        final LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);

        setRadioButtonFalse();
        CreateSubjectID();
        CreateTime();

        // Adapter for SubjectID
        ArrayAdapter<String> adapterThai = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, subjectID);
        spinner.setAdapter(adapterThai);

        // Adapter for Time
        ArrayAdapter<String> adapterThai1 = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, time);
        spinner2.setAdapter(adapterThai1);

        ans1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.ans1_1) {
                    THMP = 1;
                    COUNT = 0;
                    btnScanBarcode.setVisibility(View.GONE);
                } else if (checkedId == R.id.ans1_2) {
                    THMP = 2;
                    COUNT = 1;
                    btnScanBarcode.setVisibility(View.GONE);
                } else if (checkedId == R.id.ans1_3) {
                    THMP = 3;
                    COUNT = 0;
                } else if (checkedId == R.id.ans1_4) {
                    THMP = 4;
                    COUNT = 0;
                    btnScanBarcode.setVisibility(View.GONE);
                } else if (checkedId == R.id.ans1_5) {
                    THMP = 5;
                    COUNT = 0;
                    btnScanBarcode.setVisibility(View.GONE);
                }
            }

        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) parent.getChildAt(0)).setTextColor(Color.WHITE);
                ((TextView) parent.getChildAt(0)).setTextSize(20);
                SUBJECT_ID = subjectID.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) parent.getChildAt(0)).setTextColor(Color.WHITE);
                ((TextView) parent.getChildAt(0)).setTextSize(20);
                TIME = time.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        btnChose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (THMP == 1) {
                    myAsyncTaskUpdateStatusCheck taskUpdateStatusCheck = new myAsyncTaskUpdateStatusCheck();
                    taskUpdateStatusCheck.execute(SUBJECT_ID, "1", "1", "0", "0", "0", "0", TIME);
                    Toast.makeText(context, "QR Code", Toast.LENGTH_SHORT).show();

                    View layout = inflater.inflate(R.layout.qrcodeim,
                            (ViewGroup) findViewById(R.id.layout_popup));

                    popDialogQRCode.setIcon(R.drawable.qrcode_icon);
                    popDialogQRCode.setTitle("QR Code");
                    popDialogQRCode.setView(layout);

                    final ImageView imageViewQRCode = (ImageView) layout.findViewById(R.id.imQRCode);
                    final TextView tvShowID = (TextView) layout.findViewById(R.id.tvShowID);

                    tvShowID.setText("รหัสวิชา : " + SUBJECT_ID);
                    imageViewQRCode.setImageBitmap(CreateQRCode(SUBJECT_ID));

                    popDialogQRCode.setPositiveButton("ปิด", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    popDialogQRCode.create();
                    popDialogQRCode.show();
                } else if (THMP == 2) {
                    myAsyncTaskUpdateStatusCheck taskUpdateStatusCheck = new myAsyncTaskUpdateStatusCheck();
                    taskUpdateStatusCheck.execute(SUBJECT_ID, "1", "0", "1", "0", "0", "0", TIME);
                    Toast.makeText(context, "Shake", Toast.LENGTH_SHORT).show();
                } else if (THMP == 3) {
                    myAsyncTaskUpdateStatusCheck taskUpdateStatusCheck = new myAsyncTaskUpdateStatusCheck();
                    taskUpdateStatusCheck.execute(SUBJECT_ID, "1", "0", "0", "1", "0", "0", TIME);
                    btnScanBarcode.setVisibility(View.VISIBLE);
                    Toast.makeText(context, "Barcode", Toast.LENGTH_SHORT).show();
                } else if (THMP == 4) {
                    myAsyncTaskUpdateStatusCheck taskUpdateStatusCheck = new myAsyncTaskUpdateStatusCheck();
                    taskUpdateStatusCheck.execute(SUBJECT_ID, "1", "0", "0", "0", "1", "0", TIME);
                    Toast.makeText(context, "Check List", Toast.LENGTH_SHORT).show();
                } else if (THMP == 5) {
                    myAsyncTaskUpdateStatusCheck taskUpdateStatusCheck = new myAsyncTaskUpdateStatusCheck();
                    taskUpdateStatusCheck.execute(SUBJECT_ID, "1", "0", "0", "0", "0", "1", TIME);
                    Toast.makeText(context, "Qiz", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnScanBarcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent("com.google.zxing.client.android.SCAN");
                startActivityForResult(Intent.createChooser(intent, "Scan with"), REQUEST_QR_SCAN);
            }
        });

        btnHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ans1.clearCheck();
                Intent intent = new Intent(context, History.class);
                intent.putExtra("SubjectID", SUBJECT_ID);
                startActivity(intent);
            }
        });

        btnCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ans1.clearCheck();
                AsyncTaskCheckOut taskCheckOut = new AsyncTaskCheckOut();
                taskCheckOut.execute(SUBJECT_ID);
                if (COUNT == 1) {
                    myAsyncTaskUpdate taskUpdate = new myAsyncTaskUpdate();
                    taskUpdate.execute();
                }
            }
        });

        gps = new GPSTracker(MainActivity.this);
        if (gps.canGetLocation()) {
            latitude = String.valueOf(gps.getLatitude());
            longitude = String.valueOf(gps.getLongitude());
        } else {
            gps.showSettingsAlert();
        }

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager
                .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mShakeDetector = new ShakeDetector();
        mShakeDetector.setOnShakeListener(new ShakeDetector.OnShakeListener() {

            @Override
            public void onShake(int count) {
                if (THMP == 2) {
                    handleShakeEvent(count);
                }
            }
        });
    }

    private void handleShakeEvent(int count) {
        myAsyncTask myAsyncTask = new myAsyncTask();
        myAsyncTask.execute();
    }

    private class myAsyncTask extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            Log.i(TAG, "doInBackground");
            db.Insert(SUBJECT_ID, latitude, longitude, STATUS_CHECKED_IN);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            Toast.makeText(context, "Checked in", Toast.LENGTH_SHORT).show();
            Log.e("Lat Long", latitude + "/" + longitude);
        }
    }

    private class myAsyncTaskUpdate extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            db.Update(SUBJECT_ID, latitude, longitude, STATUS_CHECKED_OUT);
            return null;
        }
    }

    private class myAsyncTaskUpdateStatusCheck extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            Log.i(TAG, "doInBackground");
            db.UpdateStatusCheck(params[0], params[1], params[2], params[3]
                    , params[4], params[5], params[6], params[7]);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            Toast.makeText(context, "Opened Checked in...", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mSensorManager.registerListener(mShakeDetector, mAccelerometer, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    public void onPause() {
        mSensorManager.unregisterListener(mShakeDetector);
        super.onPause();
    }

    private void setRadioButtonFalse() {
        ans1_1.setChecked(false);
        ans1_2.setChecked(false);
        ans1_3.setChecked(false);
        ans1_4.setChecked(false);
        ans1_5.setChecked(false);
    }

    private void CreateSubjectID() {
        subjectID.add("225492");
        subjectID.add("225496");
        subjectID.add("235015");
        subjectID.add("222321");
    }

    private void CreateTime() {
        time.add("13.00-17.00");
        time.add("09.00-12.00");
        time.add("13.00-15.00");
        time.add("15.00-17.00");
        time.add("08.00-10.00");
        time.add("10.00-12.00");
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == REQUEST_QR_SCAN && resultCode == RESULT_OK) {
            String contents = intent.getStringExtra("SCAN_RESULT");
            AsyncTaskInsertBarcode taskInsertBarcode = new AsyncTaskInsertBarcode();
            taskInsertBarcode.execute(contents, SUBJECT_ID, "Barcode");
        }
    }

    private class AsyncTaskInsertBarcode extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            db.InsertCheckName(params[0], params[1], params[2]);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            Toast.makeText(context, "ทำการบันทึกข้อมูลนิสิตสำเร็จแล้ว", Toast.LENGTH_SHORT).show();
        }
    }

    private class AsyncTaskCheckOut extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            db.CheckOut(params[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            Toast.makeText(context, "ได้ทำการปืดให้นิสิตเช็คชื่อแล้ว", Toast.LENGTH_SHORT).show();
        }
    }

    public void ShowAlertDialog(String input) {
        final AlertDialog.Builder dDialog = new AlertDialog.Builder(context);
        dDialog.setMessage(input);
        dDialog.setPositiveButton("ปิด", null);
        dDialog.show();
    }

    public Bitmap CreateQRCode(String SubjectID) {
        Bitmap bitmapQRCode = null;
        try {

            bitmapQRCode = encodeAsBitmap(SubjectID, BarcodeFormat.QR_CODE, 500, 500);
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return bitmapQRCode;
    }

    private static final int WHITE = 0xFFFFFFFF;
    private static final int BLACK = 0xFF000000;

    Bitmap encodeAsBitmap(String contents, BarcodeFormat format, int img_width, int img_height) throws WriterException {
        String contentsToEncode = contents;
        if (contentsToEncode == null) {
            return null;
        }
        Map<EncodeHintType, Object> hints = null;
        String encoding = guessAppropriateEncoding(contentsToEncode);
        if (encoding != null) {
            hints = new EnumMap<EncodeHintType, Object>(EncodeHintType.class);
            hints.put(EncodeHintType.CHARACTER_SET, encoding);
        }
        MultiFormatWriter writer = new MultiFormatWriter();
        BitMatrix result;
        try {
            result = writer.encode(contentsToEncode, format, img_width, img_height, hints);
        } catch (IllegalArgumentException iae) {
            // Unsupported format
            return null;
        }
        int width = result.getWidth();
        int height = result.getHeight();
        int[] pixels = new int[width * height];
        for (int y = 0; y < height; y++) {
            int offset = y * width;
            for (int x = 0; x < width; x++) {
                pixels[offset + x] = result.get(x, y) ? BLACK : WHITE;
            }
        }

        Bitmap bitmap = Bitmap.createBitmap(width, height,
                Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }

    private static String guessAppropriateEncoding(CharSequence contents) {
        // Very crude at the moment
        for (int i = 0; i < contents.length(); i++) {
            if (contents.charAt(i) > 0xFF) {
                return "UTF-8";
            }
        }
        return null;
    }
}