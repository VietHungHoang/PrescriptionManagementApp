//package com.mad.prescriptionmanagementapp.data.repository;
//
//
//import androidx.lifecycle.LiveData; // Có thể trả về LiveData trực tiếp nếu muốn
//
//import com.mad.prescriptionmanagementapp.data.model.MedicationReminderDetails;
//import com.mad.prescriptionmanagementapp.data.remote.dto.response.ReminderLogResponse;
//
//public interface ReminderRepository {
//
//    // Lấy chi tiết nhắc nhở (có thể trả về trực tiếp hoặc qua callback/LiveData)
//    // Ví dụ trả về trực tiếp (cần gọi trong background thread)
//    MedicationReminderDetails getReminderDetailsBlocking(long scheduleId);
//
//    // Hoặc dùng LiveData (Room hỗ trợ trả về LiveData)
//    LiveData<MedicationReminderDetails> getReminderDetailsLiveData(long scheduleId);
//
//    // Gửi log hành động (cần chạy bất đồng bộ)
//    // Sử dụng Result wrapper để trả về kết quả/lỗi
//    // Hàm này nên được gọi từ một background thread
//    Result<ReminderLogResponse> syncReminderLogBlocking(
//            long scheduleId,
//            Long medicationId,
//            long scheduledTimeMillis,
//            long actionTimeMillis,
//            String status
//    );
//
//    // Hoặc cung cấp một phương thức với callback nếu không muốn dùng blocking call
//     /*
//     interface SyncCallback {
//         void onSuccess(ReminderLogResponse response);
//         void onError(Exception e);
//     }
//     void syncReminderLogAsync(
//             long scheduleId,
//             Long medicationId,
//             long scheduledTimeMillis,
//             long actionTimeMillis,
//             String status,
//             SyncCallback callback
//     );
//     */
//}