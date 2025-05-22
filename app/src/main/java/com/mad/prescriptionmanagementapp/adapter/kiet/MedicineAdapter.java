package com.mad.prescriptionmanagementapp.adapter.kiet;

import static android.view.View.GONE;

import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.mad.prescriptionmanagementapp.data.model.kiet.MedicineItem;
import com.mad.prescriptionmanagementapp.R;
import com.mad.prescriptionmanagementapp.ui.listener.kiet.OnMedicineActionListener;

import java.util.List;

public class MedicineAdapter extends RecyclerView.Adapter<MedicineAdapter.ViewHolder> {
    private List<MedicineItem> medicineList;
    private OnMedicineActionListener listener;

    public MedicineAdapter(List<MedicineItem> medicineList, OnMedicineActionListener listener) {
        this.medicineList = medicineList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_medicine_item_kiet, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MedicineItem item = medicineList.get(position);
        holder.tvTime.setText(item.getTime());

        // Hiển thị danh sách thuốc (tối đa 3 dòng)
        String[] medicines = item.getMedicineList().split("\n");
        int maxItems = 3;
        String displayText = medicines.length > maxItems
                ? String.join("\n", java.util.Arrays.copyOfRange(medicines, 0, maxItems))
                : item.getMedicineList();
        holder.tvMedicineList.setText(displayText);

        // Sự kiện nhấn vào "Xem chi tiết"
        holder.tvViewDetails.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDetailClicked(position);
            }
        });

        // Xử lý UI dựa vào trạng thái ưu tiên: dùng muộn > đã dùng > bỏ qua > trạng thái ban đầu
        if (item.isUsedLate()) {
            // Hiển thị icon và chữ dùng muộn, ẩn nút bỏ qua
            holder.btnUsed.setVisibility(View.VISIBLE);
            holder.btnSkip.setVisibility(GONE);
            holder.imgUsed.setImageResource(R.drawable.ic_use_late_kiet); // icon dùng muộn màu xám
            holder.tvUsed.setText("Dùng muộn");
            holder.tvUsed.setTextColor(Color.GRAY);


            ((LinearLayout) holder.btnUsed.getParent()).setGravity(Gravity.CENTER_HORIZONTAL);
        } else if (item.isUsed()) {
            holder.btnUsed.setVisibility(View.VISIBLE);
            holder.btnSkip.setVisibility(GONE);
            holder.imgUsed.setImageResource(R.drawable.check_selector_kiet);
            holder.tvUsed.setText(" Đã dùng ");
            holder.tvUsed.setTextColor(Color.parseColor("#2196F3"));

            ((LinearLayout) holder.btnUsed.getParent()).setGravity(Gravity.CENTER_HORIZONTAL);
        } else if (item.isSkipped()) {
            holder.btnUsed.setVisibility(GONE);
            holder.btnSkip.setVisibility(View.VISIBLE);
            holder.imgSkip.setImageResource(R.drawable.skip_selector_kiet);
            holder.tvSkip.setText(" Bỏ qua ");
            holder.tvSkip.setTextColor(Color.RED);
            // Disable nút để không thể click
            holder.btnUsed.setClickable(false);
            holder.btnUsed.setEnabled(false);
            holder.btnSkip.setClickable(false);
            holder.btnSkip.setEnabled(false);
            ((LinearLayout) holder.btnSkip.getParent()).setGravity(Gravity.CENTER_HORIZONTAL);
        } else {
            // Trạng thái ban đầu
            holder.btnUsed.setVisibility(View.VISIBLE);
            holder.btnSkip.setVisibility(View.VISIBLE);
            holder.imgUsed.setImageResource(R.drawable.check___off_kiet);
            holder.tvUsed.setText("Dùng");
            holder.tvUsed.setTextColor(Color.parseColor("#2196F3"));

            holder.imgSkip.setImageResource(R.drawable.skip___off_kiet);
            holder.tvSkip.setText("Bỏ qua");
            holder.tvSkip.setTextColor(Color.BLACK);

            ((LinearLayout) holder.btnUsed.getParent()).setGravity(Gravity.CENTER_HORIZONTAL);
        }

        // Sự kiện "Dùng"
        holder.btnUsed.setOnClickListener(v -> {
            if (listener != null) {
                item.setUsed(true);
                item.setSkipped(false);
                item.setUsedLate(false); // reset trạng thái dùng muộn khi nhấn dùng
                notifyItemChanged(position);
                listener.onUsedClicked(position);
            }
        });

        // Sự kiện "Bỏ Qua"
        holder.btnSkip.setOnClickListener(v -> {
            if (listener != null) {
                item.setSkipped(true);
                item.setUsed(false);
                item.setUsedLate(false);
                notifyItemChanged(position);
                listener.onSkippedClicked(position);
            }
        });
    }



    @Override
    public int getItemCount() {
        return medicineList.size();
    }

    // Hàm cập nhật danh sách thuốc mới
    public void updateList(List<MedicineItem> newList) {
        this.medicineList = newList;
        notifyDataSetChanged(); // Cập nhật lại RecyclerView
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTime, tvMedicineList, tvUsed, tvSkip, tvViewDetails;
        LinearLayout btnUsed, btnSkip;
        ImageView imgUsed, imgSkip;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvMedicineList = itemView.findViewById(R.id.tvMedicineList);
            tvViewDetails = itemView.findViewById(R.id.tvDetail);
            btnUsed = itemView.findViewById(R.id.btnUsed);
            btnSkip = itemView.findViewById(R.id.btnSkip);
            imgUsed = itemView.findViewById(R.id.imgUsed);
            imgSkip = itemView.findViewById(R.id.imgSkip);
            tvUsed = itemView.findViewById(R.id.tvUsed);
            tvSkip = itemView.findViewById(R.id.tvSkip);
        }
    }
}
