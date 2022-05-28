package com.sme_book.lab_qr;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sme_book.lab_qr.R;

import java.util.ArrayList;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.CustomViewHolder> {

   private ArrayList<ListData> arrayList;

    public ListAdapter(ArrayList<ListData> arrayList) {
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    // 처음 생성될 때 생성주기
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list , parent , false);
        CustomViewHolder holder = new CustomViewHolder(view);

        return holder;
    }

    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {
        holder.tv_name.setText(arrayList.get(position).getName());
        holder.tv_population.setText(arrayList.get(position).getPopulation());
        holder.tv_start_time.setText(arrayList.get(position).getStart_time());
        holder.tv_finish_time.setText(arrayList.get(position).getFinish_time());
        holder.itemView.getTag(position);
    }

    @Override
    public int getItemCount() {
        return (null != arrayList ? arrayList.size() : 0);
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        protected TextView tv_name;
        protected TextView tv_population;
        protected TextView tv_start_time;
        protected TextView tv_finish_time;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            this.tv_name = (TextView) itemView.findViewById(R.id.tv_name);
            this.tv_population = (TextView) itemView.findViewById(R.id.tv_population);
            this.tv_start_time = (TextView) itemView.findViewById(R.id.tv_start_time);
            this.tv_finish_time = (TextView) itemView.findViewById(R.id.tv_finish_time);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if(pos != RecyclerView.NO_POSITION) {
                        if(mListener != null) {
                            mListener.onItemClick(v,pos);
                        }
                    }
                }
            });
        }
    }

    private OnItemClickListener mListener = null;

    public void setOnItemClickListener(OnItemClickListener listener) { this.mListener = listener; }

    public interface OnItemClickListener {
        void onItemClick(View v, int position);
    }
}
