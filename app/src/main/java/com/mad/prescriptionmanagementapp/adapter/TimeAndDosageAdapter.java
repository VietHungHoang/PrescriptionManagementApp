package com.mad.prescriptionmanagementapp.adapter;

import static com.mad.prescriptionmanagementapp.util.Tools.formatNumber;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mad.prescriptionmanagementapp.data.model.TimeDosage;
import com.mad.prescriptionmanagementapp.data.model.Unit;
import com.mad.prescriptionmanagementapp.databinding.ViewholderTimeanddosageBinding;
import com.mad.prescriptionmanagementapp.ui.listener.OnTimeDosageClickListener;
import com.mad.prescriptionmanagementapp.ui.viewmodel.AddPrescriptionViewModel;
import com.mad.prescriptionmanagementapp.ui.viewmodel.AddScheduleViewModel;
import com.mad.prescriptionmanagementapp.util.Tools;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TimeAndDosageAdapter extends RecyclerView.Adapter<TimeAndDosageAdapter.TimeDosageViewHolder> {

    private final OnTimeDosageClickListener listener;
    private List<TimeDosage> timesList;

    private AddScheduleViewModel viewModel;

    public TimeAndDosageAdapter(List<TimeDosage> timeDosageList, AddScheduleViewModel viewModel,  OnTimeDosageClickListener listener) {
//        super(DIFF_CALLBACK);
        this.timesList = timeDosageList;
        this.listener = listener;
        this.viewModel = viewModel;
    }

    @NonNull
    @Override
    public TimeDosageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewholderTimeanddosageBinding binding = ViewholderTimeanddosageBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new TimeAndDosageAdapter.TimeDosageViewHolder(binding, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull TimeDosageViewHolder holder, int position) {
        // 6. Lấy dữ liệu tại vị trí 'position'
        TimeDosage currentItem = timesList.get(position);
        // 7. Gán dữ liệu vào các view trong ViewHolder
        holder.bind(position, currentItem, this.viewModel.getUnit().getValue());
    }

    @Override
    public int getItemCount() {
        // 8. Trả về số lượng item trong danh sách
        return timesList.size();
    }

    // --- ViewHolder ---

    // 3. Tạo lớp ViewHolder nội (inner class)
    public static class TimeDosageViewHolder extends RecyclerView.ViewHolder {
        // Khai báo các View trong item layout của bạn
        TextView textViewTitle;
        TextView textViewDescription;
        // Khai báo các View khác...

        // Hoặc dùng binding
        ViewholderTimeanddosageBinding binding;

        private final OnTimeDosageClickListener clickListener;

        public TimeDosageViewHolder(ViewholderTimeanddosageBinding binding, OnTimeDosageClickListener listener) {
//            super(itemView);
//            // Ánh xạ các View từ layout
//            textViewTitle = itemView.findViewById(R.id.textViewTitle); // ID từ list_item_layout.xml
//            textViewDescription = itemView.findViewById(R.id.textViewDescription); // ID từ list_item_layout.xml
//            // Ánh xạ các View khác...
            super(binding.getRoot());
            this.binding = binding;
            this.clickListener = listener;


        }

        // Phương thức tiện ích để gán dữ liệu (giúp onBindViewHolder gọn gàng hơn)
        public void bind(int position, TimeDosage item, Unit unit) {
            String time = String.format(Locale.US, "%02d:%02d", item.getHour(), item.getMinutes());
            this.binding.time.setText(time);
            this.binding.dosage.setText(String.format(Locale.US, "%s %s", formatNumber(item.getDosage()), unit.toString()));
            if (clickListener != null) {
                this.binding.timeAndDosage.setOnClickListener(v -> {
                    clickListener.onItemClick(item);
                });
                this.binding.btnRemove.setOnClickListener(v -> {
                    clickListener.onRemoveButtonClick(position);
                });
            }

        }
    }

    // --- Phương thức cập nhật dữ liệu ---

    // 4. Tạo phương thức để cập nhật danh sách dữ liệu từ bên ngoài (Activity/Fragment)
    public void setData(List<TimeDosage> newData) {
        if (newData == null) {
            newData = new ArrayList<>(); // Đảm bảo không bao giờ là null
        }
        // Cách đơn giản nhất (không hiệu quả cho danh sách lớn hoặc cập nhật thường xuyên)
        this.timesList.clear();
        this.timesList.addAll(newData);
        notifyDataSetChanged(); // Thông báo cho RecyclerView vẽ lại toàn bộ danh sách

        /*
         * CÁCH TỐT HƠN (Sử dụng DiffUtil):
         * Để tối ưu hiệu năng, đặc biệt với danh sách lớn hoặc thay đổi thường xuyên,
         * bạn nên sử dụng DiffUtil để chỉ cập nhật những item thực sự thay đổi.
         * Việc này phức tạp hơn một chút, cần tạo một lớp DiffUtil.Callback.
         * Nếu bạn cần, tôi có thể cung cấp ví dụ về DiffUtil.
         */
        // Ví dụ hàm dùng DiffUtil (cần tạo MyDiffCallback trước):
         /*
         public void submitList(List<MyDataModel> newList) {
             if (newList == null) {
                 newList = new ArrayList<>();
             }
             MyDiffCallback diffCallback = new MyDiffCallback(this.dataList, newList);
             DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);
             this.dataList.clear();
             this.dataList.addAll(newList);
             diffResult.dispatchUpdatesTo(this); // Cập nhật hiệu quả hơn
         }
         */
    }
}