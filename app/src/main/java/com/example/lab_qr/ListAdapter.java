package com.example.lab_qr;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {
        holder.tv_name.setText(arrayList.get(position).getName());
        holder.tv_stid.setText(arrayList.get(position).getStid());
        holder.tv_start_time.setText(arrayList.get(position).getStart_time());
        holder.tv_finish_time.setText(arrayList.get(position).getFinish_time());

        holder.itemView.getTag(position);

        // 짧게 눌렀을 때 확인용 토스트메시지 -> 추후에 사진에 팝업되게 수정할 예정
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(view.getContext(), "짧게 누르기", Toast.LENGTH_SHORT).show();
            }
        });

        // 오래 눌렸을 때 리스트에 삭제하는 코드, 확인용으로 추후에 없앨 예정
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                removelist(holder.getAdapterPosition());
                Toast.makeText(view.getContext(), "오래 누르기", Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return (null != arrayList ? arrayList.size() : 0);
    }

    public void removelist(int position){
        try{
            arrayList.remove(position);
            notifyItemRemoved(position);
        } catch (IndexOutOfBoundsException exception){
            exception.printStackTrace();
        }
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        protected TextView tv_name;
        protected TextView tv_stid;
        protected TextView tv_start_time;
        protected TextView tv_finish_time;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            this.tv_name = (TextView) itemView.findViewById(R.id.tv_name);
            this.tv_stid = (TextView) itemView.findViewById(R.id.tv_stid);
            this.tv_start_time = (TextView) itemView.findViewById(R.id.tv_start_time);
            this.tv_finish_time = (TextView) itemView.findViewById(R.id.tv_finish_time);
        }
    }
}
