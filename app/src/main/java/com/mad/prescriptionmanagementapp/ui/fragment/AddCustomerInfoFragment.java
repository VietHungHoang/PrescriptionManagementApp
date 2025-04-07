package com.mad.prescriptionmanagementapp.ui.fragment;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Spinner;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mad.prescriptionmanagementapp.R;
import com.mad.prescriptionmanagementapp.data.model.Country;
import com.mad.prescriptionmanagementapp.databinding.FragmentAddCustomInfoBinding;
import com.mad.prescriptionmanagementapp.ui.activity.FragmentInteractionListener;
import com.mad.prescriptionmanagementapp.ui.viewmodel.UserDataCollectionViewModel;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class AddCustomerInfoFragment extends Fragment {
    private FragmentAddCustomInfoBinding binding;
    private UserDataCollectionViewModel dataViewModel;
    private LocalDate selectedDate;
    private SharedPreferences sharedPreferences;
    private FragmentInteractionListener listener;


    public AddCustomerInfoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.dataViewModel = new ViewModelProvider(requireActivity()).get(UserDataCollectionViewModel.class);
        this.selectedDate = LocalDate.now();
        this.sharedPreferences = getActivity().getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);


    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof FragmentInteractionListener) {
            this.listener = (FragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement FragmentInteractionListener");
        }
    }

    private void showDatePickerDialog() {
        int year = this.selectedDate.getYear();
        int month = this.selectedDate.getMonthValue();
        int day = this.selectedDate.getDayOfMonth();


        // Tạo DatePickerDialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this.requireContext(),
                dateSetListener,    // Listener để nhận kết quả khi người dùng chọn xong
                year,
                month,
                day
        );

        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());

        datePickerDialog.show();
    }

    // Listener lắng nghe sự kiện khi người dùng chọn xong ngày tháng năm
    private DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            selectedDate = LocalDate.of(year, monthOfYear + 1, dayOfMonth);
            AddCustomerInfoFragment.this.updateLabel(selectedDate);
        }
    };

    // Hàm cập nhật text cho EditText
    private void updateLabel(LocalDate selectedDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String formattedDate = selectedDate.format(formatter);
        this.binding.edtDob.setText(formattedDate);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        this.binding = DataBindingUtil.inflate(inflater, R.layout.fragment_add_custom_info, container, false);
        this.binding.setViewModel(this.dataViewModel);
        this.binding.setLifecycleOwner(this.getViewLifecycleOwner());
        return this.binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        if (listener != null) {
            listener.setToolbarTitle("Cập nhật thông tin");
            String btnText = this.dataViewModel.getRoleId().getValue() == 1 ? "Hoàn tất" : "Tiếp tục";
            listener.enableContinueButton(true , btnText);
        }

        Bundle bundle = getArguments();
        if (bundle != null) {
            String value = bundle.getString("fullName"); // Lấy giá trị từ Bundle
            this.binding.edtFullName.setText(value);
        }
        this.binding.edtDob.setOnClickListener(v -> showDatePickerDialog());
        List<Country> countries = this.<List<Country>>getObjectFromSharedPreferences(getActivity(), "countries", new TypeReference<List<Country>>() {});
        if(countries != null) {
            this.updateSpinnerCoutry(view, countries);
        } else {
            this.dataViewModel.getUserCountry();
        }
        this.showGender(view);

        this.binding.spinnerCountry.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Lấy đối tượng Country được chọn từ adapter dựa vào vị trí
                Country selectedCountry = (Country) parent.getItemAtPosition(position);
                if (selectedCountry != null) {
                    dataViewModel.setCountryId(selectedCountry.getId());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                dataViewModel.setCountryId(1L);
            }
        });

        this.binding.spinnerGender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Lấy đối tượng Country được chọn từ adapter dựa vào vị trí

                String selectedGender = (String) parent.getItemAtPosition(position);
                if(selectedGender != null) {
                    AddCustomerInfoFragment.this.dataViewModel.setGender(selectedGender);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                AddCustomerInfoFragment.this.dataViewModel.setGender("Nam");
            }
        });

        this.dataViewModel.getAllCountry().observe(this.getActivity(), countryList -> {
            updateSpinnerCoutry(view, countryList);
            SharedPreferences.Editor editor = this.sharedPreferences.edit();
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                String json = objectMapper.writeValueAsString(countryList);
                // Lưu vào SharedPreferences
                editor.putString("countries", json);
                editor.apply();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void updateSpinnerCoutry(View view, List<Country> countries) {
        Spinner spinnerCountry = view.findViewById(R.id.spinnerCountry);
        ArrayAdapter<Country> adapter = new ArrayAdapter<>(requireContext(), R.layout.spinner_dropdown_item, countries);
        adapter.setDropDownViewResource(R.layout.item_in_dropdown_spinner);
        spinnerCountry.setAdapter(adapter);


//        List<String> genders = countries.stream()
//                .map(Country::getCode)
//                .collect(Collectors.toList());
//        Spinner spinnerGender = view.findViewById(R.id.spinnerCountryCode);
//        adapter = new ArrayAdapter<>(requireContext(), R.layout.spinner_dropdown_item, genders);
//        adapter.setDropDownViewResource(R.layout.item_in_dropdown_spinner);
//        spinnerGender.setAdapter(adapter);
    }

    private void showGender(View view) {
        List<String> genders = List.of("Nam", "Nữ");
        Spinner spinnerGender = view.findViewById(R.id.spinnerGender);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), R.layout.spinner_dropdown_item, genders);
        adapter.setDropDownViewResource(R.layout.item_in_dropdown_spinner);
        spinnerGender.setAdapter(adapter);
    }

    public <T> T getObjectFromSharedPreferences(Context context, String key, TypeReference<T> clazz) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);

        String json = sharedPreferences.getString(key, null);

        if (json != null) {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                return objectMapper.readValue(json, clazz);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}