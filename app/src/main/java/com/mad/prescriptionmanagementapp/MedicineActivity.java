package com.mad.prescriptionmanagementapp;

import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MedicineActivity extends AppCompatActivity {

    TextView tvTime, tvPrescription, tvMedicineList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_medicine_item); // đổi tên file layout đúng

        tvTime = findViewById(R.id.tvTime);
        tvPrescription = findViewById(R.id.tvPrescription);
        tvMedicineList = findViewById(R.id.tvMedicineList);

        // Gọi API
        new FetchMedicineDataTask().execute("https://your-api.com/medicine/next");
    }

    // AsyncTask để gọi API trong background
    private class FetchMedicineDataTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(5000);

                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;

                while ((line = in.readLine()) != null) {
                    response.append(line);
                }
                in.close();
                return response.toString();

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            if (result == null) return;
            try {
                JSONObject jsonObject = new JSONObject(result);
                String time = jsonObject.getString("time");
                String prescription = jsonObject.getString("prescription");
                JSONArray medicines = jsonObject.getJSONArray("medicineList");

                StringBuilder medicineListBuilder = new StringBuilder();
                for (int i = 0; i < medicines.length(); i++) {
                    medicineListBuilder.append(medicines.getString(i));
                    if (i < medicines.length() - 1) {
                        medicineListBuilder.append("\n");
                    }
                }

                // Gán vào TextView
                tvTime.setText(time);
                tvPrescription.setText(prescription);
                tvMedicineList.setText(medicineListBuilder.toString());

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
