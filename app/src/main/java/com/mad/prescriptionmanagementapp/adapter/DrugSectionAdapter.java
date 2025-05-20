package com.mad.prescriptionmanagementapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import com.mad.prescriptionmanagementapp.R;
import com.mad.prescriptionmanagementapp.data.DrugSection;

public class DrugSectionAdapter extends RecyclerView.Adapter<DrugSectionAdapter.ViewHolder> {

    private List<DrugSection> sectionList;

    public DrugSectionAdapter(List<DrugSection> sectionList) {
        this.sectionList = sectionList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_drug_section, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        DrugSection section = sectionList.get(position);
        holder.sectionTitle.setText(section.getTitle());
        holder.sectionContent.setText(section.getContent());
        holder.sectionContent.setVisibility(section.isExpanded() ? View.VISIBLE : View.GONE);

        holder.sectionTitle.setOnClickListener(v -> {
            section.setExpanded(!section.isExpanded());
            notifyItemChanged(position);
        });
    }

    @Override
    public int getItemCount() {
        return sectionList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView sectionTitle, sectionContent;

        public ViewHolder(View itemView) {
            super(itemView);
            sectionTitle = itemView.findViewById(R.id.section_title);
            sectionContent = itemView.findViewById(R.id.section_content);
        }
    }
}
