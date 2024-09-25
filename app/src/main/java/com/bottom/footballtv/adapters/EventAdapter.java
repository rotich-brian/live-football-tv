package com.bottom.footballtv.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bottom.footballtv.MainActivity;
import com.bottom.footballtv.R;
import com.bottom.footballtv.models.Room.Event;
import com.bottom.footballtv.services.InterstitialAdService;
import com.bottom.footballtv.services.SelectListener;
import com.squareup.picasso.Picasso;

import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder> {

    private List<com.bottom.footballtv.models.Room.Event> eventList;
    private Context context;
    private Activity activity;
    private SelectListener listener;

    public EventAdapter(List<com.bottom.footballtv.models.Room.Event> eventList, Context context, Activity activity, SelectListener listener) {
        this.eventList = eventList;
        this.context = context;
        this.activity = activity;
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
        com.bottom.footballtv.models.Room.Event event = eventList.get(position);

        holder.matchNam.setText(event.getMatch());

        try {
            Picasso.get()
                    .load(event.getThumbnail())
                    .resize(150, 150)
                    .centerCrop()
                    .placeholder(R.drawable.place_holder)
                    .into(holder.thumbnailArt);
        } catch (Exception e){
            holder.thumbnailArt.setImageResource(R.drawable.place_holder);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                MainActivity.AdClick +=1;
                if (MainActivity.AdClick % 2 == 0) {
                    InterstitialAdService.showInterstitialAd(context, activity, context.getResources().getString(R.string.InterstitialAdId), new InterstitialAdService.ShowInterstitialAd() {
                        @Override
                        public void onAdDismissed() {
                            listener.onEventClick(event);
                        }

                        @Override
                        public void onAdNull() {
                            listener.onEventClick(event);
                        }
                    });
                } else {
                    listener.onEventClick(event);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public void updateEvents(List<Event> events) {
        this.eventList = events;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{

        ImageView thumbnailArt;
        TextView matchNam;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            thumbnailArt = itemView.findViewById(R.id.event_Thumb);
            matchNam = itemView.findViewById(R.id.match_Name);
        }
    }
}
