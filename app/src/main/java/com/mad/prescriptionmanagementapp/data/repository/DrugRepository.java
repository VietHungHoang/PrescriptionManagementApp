package com.mad.prescriptionmanagementapp.data.repository;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.mad.prescriptionmanagementapp.data.database.AppDatabase;
import com.mad.prescriptionmanagementapp.data.database.DrugDao;
import com.mad.prescriptionmanagementapp.data.database.UnitDao;
import com.mad.prescriptionmanagementapp.data.mapper.DrugMapper;
import com.mad.prescriptionmanagementapp.data.mapper.UnitMapper;
import com.mad.prescriptionmanagementapp.data.model.Unit;
import com.mad.prescriptionmanagementapp.data.model.entity.DrugEntity;
import com.mad.prescriptionmanagementapp.data.remote.NetworkClient;
import com.mad.prescriptionmanagementapp.data.remote.api.DrugService;
import com.mad.prescriptionmanagementapp.data.remote.dto.response.SimpleDrug;
import com.mad.prescriptionmanagementapp.data.remote.dto.response.ResponseObject;
import com.mad.prescriptionmanagementapp.data.remote.dto.response.UnitResponse;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DrugRepository {
    private static final String TAG = "DrugRepository";

    private final DrugDao drugDao;
    private final UnitDao unitDao;
    private final ExecutorService databaseExecutor;
    private final DrugService drugService;

    // LiveData chính để Fragment observe - Luôn đọc từ cache Room
    private final LiveData<List<DrugEntity>> cachedDrugs;
//    private final LiveData<List<Unit>> unitList;

//    // LiveData báo trạng thái đang làm mới từ mạng
//    private final MutableLiveData<Boolean> isRefreshing = new MutableLiveData<>(false);
//    // LiveData báo lỗi (nếu cần)
//    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    // Cờ để tránh gọi API liên tục
    private volatile boolean isFetchInProgress = false;
    private volatile boolean isFetchUnit = false;
    // Constructor nhận Application context để lấy DAO và Executor
    // (Trong thực tế, các thành phần này nên được inject bởi Hilt/Dagger)
    public DrugRepository(Application application) {
        this.drugService = NetworkClient.getDrugService();
        AppDatabase database = AppDatabase.getDatabase(application);
        this.drugDao = database.drugDao(); // Lấy DAO từ AppDatabase
        this.unitDao = database.unitDao();
        this.databaseExecutor = AppDatabase.databaseWriteExecutor; // Lấy Executor từ AppDatabase
        this.cachedDrugs = drugDao.getAllDrugsFromCache();
//        this.unitList = unitDao.getAllUnits();
        Integer t = drugDao.getDrugCount1().getValue(); // Lấy LiveData từ DAO
        List<DrugEntity> test = this.cachedDrugs.getValue();
    }

    /**
     * Lấy LiveData chứa danh sách thuốc TỪ CACHE.
     * Đồng thời kích hoạt kiểm tra và làm mới từ mạng nếu cần.
     */
    public LiveData<List<DrugEntity>> getCachedDrugs() {
        this.refreshDrugsIfNeeded(); // Kích hoạt kiểm tra/làm mới khi có người quan sát
        return cachedDrugs;
    }

//    public LiveData<List<Unit>> getUnits() {
//        this.refreshUnit();
//        return this.unitList;
//    }


    /**
     * Lấy LiveData báo trạng thái đang làm mới từ mạng.
     */
//    public LiveData<Boolean> getIsRefreshing() {
//        return isRefreshing;
//    }

    /**
     * Lấy LiveData chứa thông báo lỗi (nếu có).
     */
//    public LiveData<String> getErrorMessage() {
//        return errorMessage;
//    }

    /**
     * Kiểm tra xem có cần làm mới dữ liệu từ API hay không và thực hiện nếu cần.
     * Logic 'nếu cần' có thể phức tạp hơn (dựa vào thời gian cache, mạng...).
     * Ở đây làm đơn giản: Làm mới nếu cache rỗng hoặc khi được gọi rõ ràng.
     */
    public void refreshDrugsIfNeeded() {
        // Thực hiện kiểm tra và gọi API trên background thread
        databaseExecutor.execute(() -> {
            // Kiểm tra xem có cần fetch không (đang fetch hoặc cache đã có?)
            // Logic đơn giản: luôn fetch nếu chưa fetch lần nào / cache rỗng?
            boolean needsFetch = drugDao.getDrugCount() == 0; // Ví dụ: fetch nếu cache rỗng
            Log.d(TAG, Integer.toString(drugDao.getDrugCount()));
            // Hoặc bạn có thể thêm logic kiểm tra thời gian cache ở đây

            if (isFetchInProgress) {
                Log.d(TAG, "Fetch already in progress.");
                return;
            }

            // Nếu cần fetch (hoặc bạn muốn luôn kiểm tra API khi gọi hàm này)
            if (needsFetch /* || shouldForceRefresh */) {
                this.fetchDrugsFromApi();
            } else {
                Log.d(TAG, "Cache is populated, skipping network fetch for now.");
            }
        });
    }

    /**
     * Buộc thực hiện làm mới dữ liệu từ API, bất kể trạng thái cache.
     * Hữu ích cho chức năng "Pull-to-refresh".
     */
    public void forceRefreshDrugs() {
        if (isFetchInProgress) {
            Log.d(TAG, "Fetch already in progress.");
            return;
        }
        fetchDrugsFromApi();
    }



    /**
     * Thực hiện gọi API để lấy danh sách thuốc và cập nhật cache.
     */
    private void fetchDrugsFromApi() {
        this.isFetchInProgress = true;
//        isRefreshing.postValue(true); // Báo đang làm mới (dùng postValue vì có thể gọi từ bg thread)
//        errorMessage.postValue(null); // Xóa lỗi cũ
        Log.d(TAG, "Fetching drugs from API...");

        Call<ResponseObject<List<SimpleDrug>>> call = this.drugService.getDrugsSimple();
        call.enqueue(new Callback<ResponseObject<List<SimpleDrug>>>() {
            @Override
            public void onResponse(@NonNull Call<ResponseObject<List<SimpleDrug>>> call,
                                   @NonNull Response<ResponseObject<List<SimpleDrug>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "API call successful. Processing data...");
                    List<SimpleDrug> drugRespons = response.body().getData();

                    if (drugRespons != null) {
                        // Chuyển đổi DTO sang Entity và lưu vào Room DB trên background thread
                        databaseExecutor.execute(() -> {
                            Log.d(TAG, "Saving fetched data to Room cache...");
                            List<DrugEntity> drugEntities = drugRespons.stream()
                                    .map(DrugMapper::responseToCache)
                                    .collect(Collectors.toList());

                            // Chiến lược: Xóa cache cũ, chèn mới
                            drugDao.deleteAll();
                            drugDao.insertAll(drugEntities);
                            Log.d(TAG, "Room cache updated successfully.");

                            // Đã xong, báo hiệu kết thúc refresh
//                            isRefreshing.postValue(false);
                            isFetchInProgress = false;
                        });
                    } else {
                        Log.w(TAG, "API response data is null.");
//                        errorMessage.postValue("Không nhận được dữ liệu thuốc.");
//                        isRefreshing.postValue(false);
                        isFetchInProgress = false;
                    }
                } else {
                    // Xử lý lỗi API (mã lỗi HTTP hoặc isSuccess=false)
                    String errorMsg = "Lỗi tải danh sách thuốc: ";
                    if (response.body() != null && response.body().getMessage() != null) {
                        errorMsg += response.body().getMessage();
                    } else {
                        errorMsg += response.code() + " " + response.message();
                    }
                    Log.e(TAG, errorMsg);
//                    errorMessage.postValue(errorMsg);
//                    isRefreshing.postValue(false);
                    isFetchInProgress = false;
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseObject<List<SimpleDrug>>> call, @NonNull Throwable t) {
                // Xử lý lỗi mạng hoặc lỗi khác khi thực hiện request
                Log.e(TAG, "API call failed: " + t.getMessage(), t);
//                errorMessage.postValue("Lỗi kết nối mạng: " + t.getMessage());
//                isRefreshing.postValue(false);
                isFetchInProgress = false;
            }
        });
    }

    public void getAllDrugsSimple(@NonNull Callback<ResponseObject<List<SimpleDrug>>> callback) {
        Call<ResponseObject<List<SimpleDrug>>> call = this.drugService.getDrugsSimple();
        call.enqueue(callback);
    }
}