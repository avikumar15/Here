package com.here;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.here.models.LocalBusiness;

import java.util.List;

public class ResponseAdapter extends RecyclerView.Adapter<ResponseAdapter.ResponseHolder> {

    private Context mContext;

    public ResponseAdapter(Context mContext) {
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ResponseHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.business_layout,parent,false);
        return new ResponseHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ResponseHolder holder, int position) {
        TextView t = holder.itemView.findViewById(R.id.tv_main);
        // TODO: Replace with localBusiness data.
        t.setText("localBusinessList.get(position).toString()");

        System.out.println("The msg is "+"localBusinessList.get(position).toString()");
    }

    @Override
    public int getItemCount() {
        return 5;//localBusinessList.size();
    }

    public static class ResponseHolder extends RecyclerView.ViewHolder {

        public View itemView;

        public ResponseHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
        }

    }


}
