package com.mad.prescriptionmanagementapp.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mad.prescriptionmanagementapp.adapter.AlphabetAdapter;
import com.mad.prescriptionmanagementapp.adapter.MedicineAdapter;
import com.mad.prescriptionmanagementapp.data.RetrofitClient;
import com.mad.prescriptionmanagementapp.data.model.Drug;
import com.mad.prescriptionmanagementapp.data.remote.api.DrugService;
import com.mad.prescriptionmanagementapp.data.remote.dto.response.ResponseObject;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.mad.prescriptionmanagementapp.R;

public class MedicineSearchActivity extends Activity {

    private static final String TAG = "MedicineSearchActivity";
    private EditText searchInput;
    private RecyclerView medicineListRecyclerView, alphabetIndexRecyclerView;
    private MedicineAdapter medicineAdapter;
    private AlphabetAdapter alphabetAdapter;
    private List<Drug> drugList;
    private List<Drug> filteredDrugList;
    private List<String> alphabetList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicine_search);

        try {
            // Initialize views
            searchInput = findViewById(R.id.search_input);
            medicineListRecyclerView = findViewById(R.id.medicine_list);
            alphabetIndexRecyclerView = findViewById(R.id.alphabet_index);
            Log.d(TAG, "Initialized views");

            // Initialize data
            drugList = new ArrayList<>();
            filteredDrugList = new ArrayList<>();
            alphabetList = new ArrayList<>();
            for (char c = 'A'; c <= 'Z'; c++) {
                alphabetList.add(String.valueOf(c));
            }
            Log.d(TAG, "Initialized data lists");

            // Setup RecyclerViews
            medicineListRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            medicineAdapter = new MedicineAdapter(filteredDrugList, drug -> {
                try {
                    ObjectMapper mapper = new ObjectMapper();
                    String jsonData = mapper.writeValueAsString(drug);
                    Intent intent = new Intent(this, DrugDetailActivity.class);
                    intent.putExtra("drug_data", jsonData);
                    startActivity(intent);
                    Log.d(TAG, "Opened DrugDetailActivity with drug: " + (drug.getName() != null ? drug.getName() : "unknown"));
                } catch (Exception e) {
                    Log.e(TAG, "Error starting DrugDetailActivity", e);
                    Toast.makeText(this, "Không thể mở chi tiết thuốc", Toast.LENGTH_SHORT).show();
                }
            });
            medicineListRecyclerView.setAdapter(medicineAdapter);
            Log.d(TAG, "Set up medicine RecyclerView");

            alphabetIndexRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
            alphabetAdapter = new AlphabetAdapter(alphabetList, letter -> {
                int position = findFirstDrugPositionByLetter(letter);
                if (position != -1) {
                    medicineListRecyclerView.scrollToPosition(position);
                    Log.d(TAG, "Scrolled to position " + position + " for letter " + letter);
                } else {
                    Log.d(TAG, "No drug found for letter " + letter);
                }
            });
            alphabetIndexRecyclerView.setAdapter(alphabetAdapter);
            Log.d(TAG, "Set up alphabet RecyclerView");

            // Load data from API
            loadDrugsFromApi();

            // Setup search
//            searchInput.addTextChangedListener(new TextWatcher() {
//                @Override
//                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
//                @Override
//                public void onTextChanged(CharSequence s, int start, int before, int count) {}
//                @Override
//                public void afterTextChanged(Editable s) {
//                    String query = s != null ? s.toString() : "";
//                    if (query.trim().isEmpty()) {
//                        filteredDrugList.clear();
//                        filteredDrugList.addAll(drugList);
//                        medicineAdapter.notifyDataSetChanged();
//                    } else {
//                        searchDrugsFromApi(query);
//                    }
//                }
//            });
//            Log.d(TAG, "Set up search TextWatcher");
            searchInput.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {}

                @Override
                public void afterTextChanged(Editable s) {
                    String query = s != null ? s.toString().trim().toLowerCase() : "";
                    if (query.isEmpty()) {
                        filteredDrugList.clear();
                        filteredDrugList.addAll(drugList);
                        medicineAdapter.notifyDataSetChanged();
                        Log.d(TAG, "Cleared search query, restored full drug list");
                    } else {
                        searchDrugsFromApi(query);
                    }
                }
            });
            Log.d(TAG, "Set up search TextWatcher");
        } catch (Exception e) {
            Log.e(TAG, "Error initializing activity", e);
            Toast.makeText(this, "Khởi tạo ứng dụng thất bại", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void loadDrugsFromApi() {
        DrugService drugService = RetrofitClient.getDrugService();
        Call<ResponseObject<List<Drug>>> call = drugService.getAllDrugs();
        call.enqueue(new Callback<ResponseObject<List<Drug>>>() {
            @Override
            public void onResponse(Call<ResponseObject<List<Drug>>> call, Response<ResponseObject<List<Drug>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    drugList.clear();
                    drugList.addAll(response.body().getData());
                    filteredDrugList.clear();
                    filteredDrugList.addAll(drugList);
                    medicineAdapter.notifyDataSetChanged();
                    Log.d(TAG, "Loaded drugs from API, drugList size: " + drugList.size());
                    if (drugList.isEmpty()) {
                        Toast.makeText(MedicineSearchActivity.this, "Không có dữ liệu thuốc", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e(TAG, "Failed to load drugs from API, response code: " + response.code());
                    Toast.makeText(MedicineSearchActivity.this, "Không thể tải dữ liệu thuốc", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseObject<List<Drug>>> call, Throwable t) {
                Log.e(TAG, "Error loading drugs from API", t);
                Toast.makeText(MedicineSearchActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
//    searchDrugsFromApi
//    private void searchDrugsFromApi(String query) {
//        DrugService drugService = RetrofitClient.getDrugService();
//        Call<ResponseObject<List<Drug>>> call = drugService.searchDrugs(query);
//        call.enqueue(new Callback<ResponseObject<List<Drug>>>() {
//            @Override
//            public void onResponse(Call<ResponseObject<List<Drug>>> call, Response<ResponseObject<List<Drug>>> response) {
//                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
//                    filteredDrugList.clear();
//                    filteredDrugList.addAll(response.body().getData());
//                    medicineAdapter.notifyDataSetChanged();
//                    Log.d(TAG, "Search results for query '" + query + "', filteredDrugList size: " + filteredDrugList.size());
//                } else {
//                    Log.e(TAG, "Failed to search drugs, response code: " + response.code());
//                    Toast.makeText(MedicineSearchActivity.this, "Không thể tìm kiếm thuốc", Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<ResponseObject<List<Drug>>> call, Throwable t) {
//                Log.e(TAG, "Error searching drugs", t);
//                Toast.makeText(MedicineSearchActivity.this, "Lỗi tìm kiếm: " + t.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
    private void searchDrugsFromApi(String query) {
        filteredDrugList.clear();
        for (Drug drug : drugList) {
            if (drug.getName() != null && drug.getName().toLowerCase().startsWith(query)) {
                filteredDrugList.add(drug);
            }
        }
        medicineAdapter.notifyDataSetChanged();
        Log.d(TAG, "Filtered drugs for query: " + query + ", found: " + filteredDrugList.size());
    }
    private int findFirstDrugPositionByLetter(String letter) {
        if (letter == null) {
            Log.w(TAG, "Letter is null in findFirstDrugPositionByLetter");
            return -1;
        }
        for (int i = 0; i < filteredDrugList.size(); i++) {
            Drug drug = filteredDrugList.get(i);
            String drugName = drug.getName() != null ? drug.getName().toUpperCase() : "";
            if (drugName.startsWith(letter)) {
                return i;
            }
        }
        return -1;
    }
}