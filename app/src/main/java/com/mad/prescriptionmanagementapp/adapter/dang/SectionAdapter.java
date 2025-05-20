package com.mad.prescriptionmanagementapp.adapter.dang;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.TransitionManager;
import com.mad.prescriptionmanagementapp.R;
import com.mad.prescriptionmanagementapp.data.model.Section;
import java.util.ArrayList;
import java.util.List;

public class SectionAdapter extends RecyclerView.Adapter<SectionAdapter.SectionViewHolder> {

    private final List<Section> sections;
    private final List<Boolean> isExpandedList; // Theo dõi trạng thái mở rộng/thu gọn

    public SectionAdapter(List<Section> sections) {
        this.sections = sections;
        this.isExpandedList = new ArrayList<>();
        // Khởi tạo tất cả section ở trạng thái thu gọn
        for (int i = 0; i < sections.size(); i++) {
            isExpandedList.add(false);
        }
    }

    @NonNull
    @Override
    public SectionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_section, parent, false);
        return new SectionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SectionViewHolder holder, int position) {
        Section section = sections.get(position);
        boolean isExpanded = isExpandedList.get(position);

        // Thiết lập tiêu đề và nội dung
        holder.titleTextView.setText(section.getTitle() != null ? section.getTitle() : "Không có tiêu đề");
        holder.contentTextView.setText(section.getContent() != null ? section.getContent() : "Không có nội dung");

        // Cập nhật giao diện dựa trên trạng thái mở rộng/thu gọn
        holder.contentTextView.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        holder.expandIcon.setImageResource(isExpanded ? R.drawable.ic_expand_less : R.drawable.ic_expand_more);

        // Xử lý sự kiện nhấn vào tiêu đề với hiệu ứng chuyển đổi
        holder.headerLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Bắt đầu hiệu ứng chuyển đổi
                TransitionManager.beginDelayedTransition((ViewGroup) holder.itemView.getParent());
                isExpandedList.set(position, !isExpanded);
                notifyItemChanged(position);
                // Yêu cầu cập nhật layout của RecyclerView
                holder.itemView.getParent().requestLayout();
            }
        });
    }

    @Override
    public int getItemCount() {
        return sections.size();
    }

    static class SectionViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView contentTextView;
        ImageView expandIcon;
        LinearLayout headerLayout;

        SectionViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.section_title);
            contentTextView = itemView.findViewById(R.id.section_content);
            expandIcon = itemView.findViewById(R.id.expand_icon);
            headerLayout = itemView.findViewById(R.id.section_header);
        }
    }
}