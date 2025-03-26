package com.mad.prescriptionmanagementapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class SelectUserTypeActivity extends AppCompatActivity {

    private CardView cardRegularUser, cardMedicalStudent, cardDoctor;
    private ImageView ivExpandBasic, ivExpandExperts;
    private Button btnContinue;
    private String selectedUserType = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_user_type);

        // Initialize views
        ImageButton btnBack = findViewById(R.id.btnBack);
        cardRegularUser = findViewById(R.id.cardRegularUser);
//        cardMedicalStudent = findViewById(R.id.cardMedicalStudent);
        cardDoctor = findViewById(R.id.cardDoctor);
        ivExpandBasic = findViewById(R.id.ivExpandBasic);
        ivExpandExperts = findViewById(R.id.ivExpandExperts);
        btnContinue = findViewById(R.id.btnContinue);

        // Set up back button
        btnBack.setOnClickListener(v -> finish());

        // Set up section expanders
        ivExpandBasic.setOnClickListener(v -> toggleBasicUsersSection());
        ivExpandExperts.setOnClickListener(v -> toggleExpertsSection());

        // Set up card selection
        setupCardSelection();

        // Set up continue button
        btnContinue.setOnClickListener(v -> {
            if (selectedUserType == null) {
                Toast.makeText(SelectUserTypeActivity.this,
                        "Vui lòng chọn loại người dùng", Toast.LENGTH_SHORT).show();
                return;
            }

            // Navigate to next screen based on selection
//            Intent intent;
//            switch (selectedUserType) {
//                case "regular_user":
//                    intent = new Intent(SelectUserTypeActivity.this, RegisterUserActivity.class);
//                    break;
//                case "medical_student":
//                    intent = new Intent(SelectUserTypeActivity.this, RegisterStudentActivity.class);
//                    break;
//                case "doctor":
//                    intent = new Intent(SelectUserTypeActivity.this, RegisterDoctorActivity.class);
//                    break;
//                default:
//                    return;
//            }

//            intent.putExtra("USER_TYPE", selectedUserType);
//            startActivity(intent);
        });
    }

    private void setupCardSelection() {
        // Set up card click listeners
        cardRegularUser.setOnClickListener(v -> {
            selectCard(cardRegularUser);
            deselectCard(cardMedicalStudent);
            deselectCard(cardDoctor);
            selectedUserType = "regular_user";
        });

        cardMedicalStudent.setOnClickListener(v -> {
            deselectCard(cardRegularUser);
            selectCard(cardMedicalStudent);
            deselectCard(cardDoctor);
            selectedUserType = "medical_student";
        });

        cardDoctor.setOnClickListener(v -> {
            deselectCard(cardRegularUser);
            deselectCard(cardMedicalStudent);
            selectCard(cardDoctor);
            selectedUserType = "doctor";
        });
    }

    private void selectCard(CardView card) {
        card.setCardBackgroundColor(getResources().getColor(R.color.selected_card_bg));
        card.setCardElevation(4f);
    }

    private void deselectCard(CardView card) {
        card.setCardBackgroundColor(getResources().getColor(R.color.white));
        card.setCardElevation(0f);
    }

    private void toggleBasicUsersSection() {
        boolean isVisible = cardRegularUser.getVisibility() == View.VISIBLE;
        cardRegularUser.setVisibility(isVisible ? View.GONE : View.VISIBLE);
        cardMedicalStudent.setVisibility(isVisible ? View.GONE : View.VISIBLE);
        ivExpandBasic.setImageResource(isVisible ?
                android.R.drawable.arrow_down_float : android.R.drawable.arrow_up_float);
    }

    private void toggleExpertsSection() {
        boolean isVisible = cardDoctor.getVisibility() == View.VISIBLE;
        cardDoctor.setVisibility(isVisible ? View.GONE : View.VISIBLE);
        ivExpandExperts.setImageResource(isVisible ?
                android.R.drawable.arrow_down_float : android.R.drawable.arrow_up_float);
    }
}

