package com.bottom.footballtv.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bottom.footballtv.R;
import com.bottom.footballtv.models.Event;
import com.bottom.footballtv.tools.SelectListener;
import com.bottom.footballtv.ui.StreamActivity;
import com.squareup.picasso.Picasso;

import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder> {

    private List<Event> eventList;
    private Context context;
    private SelectListener listener;

    public EventAdapter(List<Event> eventList, Context context, SelectListener listener) {
        this.eventList = eventList;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.each_event_view,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Event event = eventList.get(position);

        holder.matchNam.setText(event.getMatch());

        try {
            Picasso.get()
                    .load(event.getThumbnail())
                    .resize(50, 50)
                    .centerCrop()
                    .placeholder(R.drawable.sample_thumb)
                    .into(holder.thumbnailArt);
        } catch (Exception e){
            holder.thumbnailArt.setImageResource(R.drawable.place_holder);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onEventClick(event);
            }
        });
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView thumbnailArt;
        TextView matchNam;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            thumbnailArt = itemView.findViewById(R.id.event_Thumb);
            matchNam = itemView.findViewById(R.id.match_Name);
        }
    }
}
