package com.mad.prescriptionmanagementapp.ui.fragment.addprescription;

import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.mad.prescriptionmanagementapp.R;
import com.mad.prescriptionmanagementapp.adapter.DrugsAdapter;
import com.mad.prescriptionmanagementapp.adapter.SelectedDrugAdapter;
import com.mad.prescriptionmanagementapp.data.model.DrugInPres;
import com.mad.prescriptionmanagementapp.data.remote.dto.request.PrescriptionRequest;
import com.mad.prescriptionmanagementapp.databinding.FragmentAddPrescriptionInfoBinding;
import com.mad.prescriptionmanagementapp.ui.activity.AddPrescriptionActivity;
import com.mad.prescriptionmanagementapp.ui.fragment.dialog.ErrorDialog;
import com.mad.prescriptionmanagementapp.ui.listener.OnDrugClickListener;
import com.mad.prescriptionmanagementapp.ui.listener.OnSelectedDrugClickListener;
import com.mad.prescriptionmanagementapp.ui.viewmodel.AddPrescriptionInfoViewModel;
import com.mad.prescriptionmanagementapp.ui.viewmodel.AddPrescriptionViewModel;
import com.mad.prescriptionmanagementapp.util.ErrorType;
import com.mad.prescriptionmanagementapp.util.FragmentName;
import com.mad.prescriptionmanagementapp.util.Tools;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class AddPrescriptionInfoFragment extends Fragment {
    private FragmentAddPrescriptionInfoBinding binding;
    private AddPrescriptionViewModel shareViewModel;
    private AddPrescriptionInfoViewModel viewModel;

    public static AddPrescriptionInfoFragment newInstance() {
        return new AddPrescriptionInfoFragment();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.binding = FragmentAddPrescriptionInfoBinding.inflate(inflater, container, false);
        this.initViewModel();
        return this.binding.getRoot();
    }

    private void initViewModel() {
        this.shareViewModel = new ViewModelProvider(this.requireActivity()).get(AddPrescriptionViewModel.class);
        this.viewModel = new ViewModelProvider(this).get(AddPrescriptionInfoViewModel.class);
        this.binding.setViewModel(shareViewModel);
        this.binding.setLifecycleOwner(this.getViewLifecycleOwner());
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((AddPrescriptionActivity) requireActivity()).setCustomTitle("Thêm đơn thuốc");
        this.setupUI();
        this.setOnclickView();
        this.setOnFocusEditText();
        this.observeError();
        this.setAdapter();
    }

    private void setupUI() {
        PrescriptionRequest pres = this.shareViewModel.getPrescription().getValue();
        this.binding.edtPrescriptionName.setText(pres.getName());
        this.binding.edtHospital.setText(pres.getHospital());
        this.binding.edtDoctor.setText(pres.getDoctorName());
        this.binding.edtConsultationDate.setText(pres.getConsultationDate());
        this.binding.edtFollowUpDate.setText(pres.getFollowUpDate());
    }

    private void setOnclickView() {
        this.binding.btnAddDrug.setOnClickListener(v -> {
            Tools.changeFragment(this.requireActivity(), new SelectDrugFragment(), FragmentName.API_TO_SD);
        });

        this.binding.btnSave.setOnClickListener(v -> {
            if (this.viewModel.validateAddPrescriptionInfo(this.shareViewModel.getPrescription().getValue(), this.shareViewModel.getSelectedDrugs().getValue())) {
                this.shareViewModel.handleBtnSavePres(this.requireContext());
            }

        });

        this.binding.edtConsultationDate.setOnClickListener(v -> {
            Tools.showDatePickerDialog(requireContext(), this.binding.edtConsultationDate);
        });

        this.binding.edtFollowUpDate.setOnClickListener(v -> {
            Tools.showDatePickerDialog(requireContext(), this.binding.edtFollowUpDate);
        });

        this.binding.switchMedicalInfo.setOnCheckedChangeListener((buttonView, isChecked) -> {
            this.viewModel.setSwitchState(isChecked);
        });
    }

    private void setOnFocusEditText() {
        this.binding.edtPrescriptionName.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                this.viewModel.setErrorMessage(null);
            }
            if (!hasFocus) {
                String text = ((EditText) v).getText().toString();
                this.shareViewModel.addPresName(text);
            }
        });

        this.binding.edtHospital.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                String text = ((EditText) v).getText().toString();
                this.shareViewModel.addHospital(text);
            }
        });

        this.binding.edtDoctor.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                String text = ((EditText) v).getText().toString();
                this.shareViewModel.addDoctor(text);
            }
        });

        this.binding.edtConsultationDate.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                String text = ((EditText) v).getText().toString();
                this.shareViewModel.addConsultionDate(text);
            }
        });

        this.binding.edtFollowUpDate.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                String text = ((EditText) v).getText().toString();
                this.shareViewModel.addFollowUpDate(text);
            }
        });
    }

    private void setAdapter() {
        SelectedDrugAdapter selectedDrugAdapter = new SelectedDrugAdapter(this.shareViewModel.getSelectedDrugs().getValue(), new OnSelectedDrugClickListener() {
            @Override
            public void onDrugClick(DrugInPres drugInPres) {
                List<DrugInPres> drugs = AddPrescriptionInfoFragment.this.shareViewModel.getSelectedDrugs().getValue();

                if (drugs != null) {
                    AddPrescriptionInfoFragment.this.shareViewModel.setCurrentDrug(drugs.stream().filter(drug -> Objects.equals(drug.getDrug().getId(), drugInPres.getDrug().getId())).findFirst().orElse(null));
                    Fragment newFragment = AddScheduleFragment.newInstance(drugInPres.getDrug().getId(), true);
                    Tools.changeFragment(requireActivity(), newFragment, null);
                }
            }
        });
        this.binding.rcvDrugInfo.setLayoutManager(new LinearLayoutManager(this.getContext()));
        this.binding.rcvDrugInfo.setAdapter(selectedDrugAdapter);
    }

    private void enableMedicalInfo(boolean isEnabled) {
        this.binding.cardMedicalInfo.setVisibility(isEnabled ? View.VISIBLE : View.GONE);
    }

    private void observeError() {
        this.viewModel.getErrorMessage().observe(this.getViewLifecycleOwner(), errorMessage -> {
            if (errorMessage != null) {
                if (errorMessage.first == ErrorType.NAME_EMPTY) {
                    this.binding.txtErrorName.setText(errorMessage.second);
                    this.binding.txtErrorName.setVisibility(View.VISIBLE);
                    this.binding.edtPrescriptionName.getBackground().setState(new int[]{R.attr.state_error});
                } else {
                    new ErrorDialog(this, errorMessage.second, true).showDialog();
                    this.viewModel.setErrorMessage(null);
                }
            } else {
                this.binding.txtErrorName.setVisibility(View.GONE);
            }
        });

        this.viewModel.getSwitchState().observe(getViewLifecycleOwner(), this::enableMedicalInfo);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    private void updatePres() {
        if (this.shareViewModel.getPrescription().getValue() != null) {
            this.shareViewModel.updatePrescription(this.binding.edtPrescriptionName.getText().toString()
                    , this.binding.switchMedicalInfo.isChecked()
                    , this.binding.edtHospital.getText().toString()
                    , this.binding.edtDoctor.getText().toString()
                    , this.binding.edtConsultationDate.getText().toString()
                    , this.binding.edtFollowUpDate.getText().toString()
            );
        }
    }
}