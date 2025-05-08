package com.mad.prescriptionmanagementapp.ui.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

// Import DTO Prescription của bạn nếu cần cho việc thêm/sửa


import com.mad.prescriptionmanagementapp.data.model.Drug;
import com.mad.prescriptionmanagementapp.data.model.DrugInPres;
import com.mad.prescriptionmanagementapp.data.model.Prescription;
import com.mad.prescriptionmanagementapp.data.model.TimeDosage;
import com.mad.prescriptionmanagementapp.data.model.Unit;
import com.mad.prescriptionmanagementapp.data.model.relation.PrescriptionWithDrugDetails;
import com.mad.prescriptionmanagementapp.data.remote.dto.response.DrugResponse;
import com.mad.prescriptionmanagementapp.data.repository.MedicationRepository;
import com.mad.prescriptionmanagementapp.util.AlarmScheduler;
import com.mad.prescriptionmanagementapp.util.Frequency;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PrescriptionListViewModel extends AndroidViewModel {
    private final MedicationRepository repository;
    private final LiveData<List<PrescriptionWithDrugDetails>> allPrescriptionsWithDetails;

    public PrescriptionListViewModel(@NonNull Application application) {
        super(application);
        repository = new MedicationRepository(application);
        allPrescriptionsWithDetails = repository.getAllPrescriptionsWithDetails();
    }

    public LiveData<List<PrescriptionWithDrugDetails>> getAllPrescriptionsWithDetails() {
        return allPrescriptionsWithDetails;
    }

    // Hàm này sẽ được gọi khi bạn nhận được dữ liệu đơn thuốc mới từ API
    // hoặc khi người dùng tạo đơn thuốc mới trong app
    public void insertPrescription(Prescription prescriptionApiDto) {
        // Giả sử prescriptionApiDto là đối tượng Prescription bạn đã định nghĩa
        repository.saveFullPrescriptionAndGenerateReminders(prescriptionApiDto);
        // Sau khi insert, AlarmScheduler cần được kích hoạt để đặt báo thức
        // (Sẽ xử lý trong phần AlarmScheduler)
    }

    public void deletePrescription(Long prescriptionId) {
        repository.deletePrescriptionAndAssociatedReminders(prescriptionId);
        // Sau khi xóa, AlarmScheduler cần được kích hoạt để hủy báo thức
        // (Sẽ xử lý trong phần AlarmScheduler)
    }

    // Hàm mới để thêm đơn thuốc test
    public void addSamplePrescriptionForTesting() {
        // Tạo đối tượng Prescription (dùng model của bạn)
        Prescription samplePrescription = new Prescription();
        samplePrescription.setId(System.currentTimeMillis()); // ID tạm thời
        samplePrescription.setHospital("Bệnh viện Demo");
        samplePrescription.setDoctorName("Dr. Test");
        samplePrescription.setConsultationDate(LocalDate.now());
        samplePrescription.setFollowUpDate(LocalDate.now().plusMonths(1));

        List<DrugInPres> drugsInPresList = new ArrayList<>();

        // --- Thuốc 1: Paracetamol, uống hàng ngày ---
        DrugInPres paracetamolInPres = new DrugInPres();

        Drug paracetamolDrug = new Drug();
        paracetamolDrug.setId(101L); // ID thuốc
        paracetamolDrug.setName("Paracetamol 500mg");
        paracetamolDrug.setTitle("Giảm đau, hạ sốt");
        // paracetamolDrug.setImage("url_to_image"); // Tùy chọn
        // paracetamolDrug.setSections(...); // Tùy chọn

        paracetamolInPres.setDrugResponse(new DrugResponse(paracetamolDrug.getId(), paracetamolDrug.getName())); // DrugResponse chứa Drug

        Unit viênUnit = new Unit();
        viênUnit.setId(1L); // ID đơn vị
        viênUnit.setName("viên");
        paracetamolInPres.setUnit(viênUnit);

        // paracetamolInPres.setDate(LocalDate.now().toString()); // Ngày bắt đầu (string "YYYY-MM-DD")
        paracetamolInPres.setDate(LocalDate.now().format(java.time.format.DateTimeFormatter.ISO_LOCAL_DATE));
        paracetamolInPres.setFrequency(Frequency.DAILY); // Enum Frequency từ model của bạn

        List<TimeDosage> paracetamolTimes = new ArrayList<>();
        // Uống vào 8:00 sáng, 1 viên
        TimeDosage time1_para = new TimeDosage();
        time1_para.setHour(LocalTime.now().getHour()); // Đặt giờ hiện tại để test ngay
        time1_para.setMinutes(LocalTime.now().getMinute() + 1); // Phút hiện tại + 1 để test ngay
        time1_para.setDosage(1.0);
        paracetamolTimes.add(time1_para);

        // Uống vào 14:00 chiều, 1 viên
        TimeDosage time2_para = new TimeDosage();
        time2_para.setHour(5);
        time2_para.setMinutes(50);
        time2_para.setDosage(1.0);
        // paracetamolTimes.add(time2_para); // Tạm thời comment để test nhanh 1 liều

        paracetamolInPres.setTimeDosages(paracetamolTimes);
        drugsInPresList.add(paracetamolInPres);


        // --- Thuốc 2: Amoxicillin, uống mỗi 2 ngày ---
        DrugInPres amoxicillinInPres = new DrugInPres();

        Drug amoxicillinDrug = new Drug();
        amoxicillinDrug.setId(102L);
        amoxicillinDrug.setName("Amoxicillin 250mg");
        amoxicillinDrug.setTitle("Kháng sinh");

        amoxicillinInPres.setDrugResponse(new DrugResponse(amoxicillinDrug.getId(), amoxicillinDrug.getName()));

        Unit goiUnit = new Unit();
        goiUnit.setId(2L);
        goiUnit.setName("gói");
        amoxicillinInPres.setUnit(goiUnit);

        // amoxicillinInPres.setDate(LocalDate.now().plusDays(1).toString()); // Bắt đầu từ ngày mai
        amoxicillinInPres.setDate(LocalDate.now().plusDays(1).format(java.time.format.DateTimeFormatter.ISO_LOCAL_DATE));
        amoxicillinInPres.setFrequency(Frequency.EVERY_N_DAYS);
        amoxicillinInPres.setEveryNDays(2); // Uống mỗi 2 ngày

        List<TimeDosage> amoxicillinTimes = new ArrayList<>();
        // Uống vào 9:30 sáng, 1 gói
        TimeDosage time1_amox = new TimeDosage();
        time1_amox.setHour(5);
        time1_amox.setMinutes(45);
        time1_amox.setDosage(1.0);
        amoxicillinTimes.add(time1_amox);

        amoxicillinInPres.setTimeDosages(amoxicillinTimes);
        // drugsInPresList.add(amoxicillinInPres); // Tạm thời comment để test 1 loại thuốc trước

        // --- Thuốc 3: Vitamin C, uống vào Thứ 2, 4, 6 ---
        DrugInPres vitaminCInPres = new DrugInPres();

        Drug vitaminCDrug = new Drug();
        vitaminCDrug.setId(103L);
        vitaminCDrug.setName("Vitamin C 1000mg");
        vitaminCDrug.setTitle("Bổ sung Vitamin");
        vitaminCInPres.setDrugResponse(new DrugResponse(vitaminCDrug.getId(), vitaminCDrug.getName()));

        vitaminCInPres.setUnit(viênUnit); // Dùng lại unit "viên"

        // vitaminCInPres.setDate(LocalDate.now().toString());
        vitaminCInPres.setDate(LocalDate.now().format(java.time.format.DateTimeFormatter.ISO_LOCAL_DATE));
        vitaminCInPres.setFrequency(Frequency.SPECIFIC_DAYS);
        // SPECIFIC_DATES: Giả sử List<Integer> này là các DayOfWeek.getValue() (MONDAY=1, TUESDAY=2, ..., SUNDAY=7)
        List<Integer> specificDays = Arrays.asList(
                java.time.DayOfWeek.MONDAY.getValue(),
                java.time.DayOfWeek.WEDNESDAY.getValue(),
                java.time.DayOfWeek.FRIDAY.getValue()
        );
        vitaminCInPres.setSpecificDays(specificDays);

        List<TimeDosage> vitaminCTimes = new ArrayList<>();
        TimeDosage time1_vitC = new TimeDosage();
        time1_vitC.setHour(10);
        time1_vitC.setMinutes(0);
        time1_vitC.setDosage(1.0);
        vitaminCTimes.add(time1_vitC);
        vitaminCInPres.setTimeDosages(vitaminCTimes);
        // drugsInPresList.add(vitaminCInPres); // Tạm thời comment

        // Gán danh sách thuốc vào đơn
        samplePrescription.setDrugs(drugsInPresList);

        // Gọi repository để lưu
        // Phương thức này đã có trong ViewModel của bạn
        insertPrescription(samplePrescription);
        Log.i("ViewModelTest", "Sample prescription added for testing.");

        // Sau khi thêm, cần trigger AlarmScheduler để đặt lịch
        // Điều này nên được xử lý bởi Repository hoặc một cơ chế lắng nghe sự thay đổi DB
        // Tạm thời, có thể gọi trực tiếp từ đây hoặc từ nơi gọi hàm này
        new AlarmScheduler(getApplication()).scheduleAllPendingReminders();
        Log.i("ViewModelTest", "Triggered alarm scheduling after adding sample.");

    }
}