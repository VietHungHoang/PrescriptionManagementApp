package com.mad.prescriptionmanagementapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.mad.prescriptionmanagementapp.R;
import java.util.List;

public class AlphabetAdapter extends RecyclerView.Adapter<AlphabetAdapter.ViewHolder> {

    private List<String> alphabetList;
    private OnLetterClickListener listener;

    public interface OnLetterClickListener {
        void onLetterClick(String letter);
    }

    public AlphabetAdapter(List<String> alphabetList, OnLetterClickListener listener) {
        this.alphabetList = alphabetList;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_alphabet, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String letter = alphabetList.get(position);
        holder.letterText.setText(letter);
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onLetterClick(letter);
            }
        });
    }

    @Override
    public int getItemCount() {
        return alphabetList != null ? alphabetList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView letterText;

        public ViewHolder(View itemView) {
            super(itemView);
            letterText = itemView.findViewById(R.id.letter_text);
        }
    }
}