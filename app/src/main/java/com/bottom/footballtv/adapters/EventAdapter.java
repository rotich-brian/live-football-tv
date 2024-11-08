package com.bottom.footballtv.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bottom.footballtv.MainActivity;
import com.bottom.footballtv.R;
import com.bottom.footballtv.models.Room.Event;
import com.bottom.footballtv.services.InterstitialAdService;
import com.bottom.footballtv.services.RewardedAdService;
import com.bottom.footballtv.services.SelectListener;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Objects;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder> {

    private List<com.bottom.footballtv.models.Room.Event> eventList;
    private Context context;
    private Activity activity;
    private SelectListener listener;
    private String type;

    private Event expandedEvent;

    public EventAdapter(List<com.bottom.footballtv.models.Room.Event> eventList, Context context, Activity activity, SelectListener listener, String type) {
        this.eventList = eventList;
        this.context = context;
        this.activity = activity;
        this.listener = listener;
        this.type = type;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.each_event_view,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        com.bottom.footballtv.models.Room.Event event = eventList.get(position);

        if (MainActivity.AdUnlock == 1) {
            holder.hdLayoutLink3.setBackgroundResource(R.drawable.gradient_premium_link_bg_unlocked);
            holder.hdOverlayFrame.setVisibility(View.GONE);
        } else  {
            holder.hdLayoutLink3.setBackgroundResource(R.drawable.gradient_premium_link_bg);
            holder.hdOverlayFrame.setVisibility(View.VISIBLE);
        }

        holder.matchNam.setText(event.getMatch());

        holder.cardLink1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openEventLink(event,1);
            }
        });

        holder.cardLink2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openEventLink(event, 2);
            }
        });

        holder.cardLink3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openHDEventLink(event, holder,3);
            }
        });

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

        if (Objects.equals(type, "event")) {
            boolean isExpanded = (expandedEvent != null && expandedEvent.equals(event));
            holder.eventLinksL.setVisibility(isExpanded ? View.VISIBLE : View.GONE);

            holder.expandButton.setImageResource(isExpanded ?
                    R.drawable.baseline_arrow_up_24 : R.drawable.baseline_navigate_next_24);

            holder.itemView.setOnClickListener(v -> {
                if (expandedEvent != null && expandedEvent.equals(event)) {
                    expandedEvent = null;
                } else {
                    expandedEvent = event;
                }
                notifyDataSetChanged();
            });
        } else {
            holder.eventLinksL.setVisibility(View.GONE);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openEventLink(event,0);
                }
            });
        }
    }

    private void openEventLink(Event event, int i) {
        MainActivity.AdClick +=1;
        if (MainActivity.AdClick % 2 == 0) {
            InterstitialAdService.showInterstitialAd(context, activity, context.getResources().getString(R.string.InterstitialAdId), new InterstitialAdService.ShowInterstitialAd() {
                @Override
                public void onAdDismissed() {
                    listener.onEventClick(event,i);
                }

                @Override
                public void onAdNull() {
                    listener.onEventClick(event,i);
                }
            });
        } else {
            listener.onEventClick(event,i);
        }
    }

    private void openHDEventLink(Event event, ViewHolder holder, int i) {
        if (MainActivity.AdUnlock == 0) {
            RewardedAdService.showRewardedAd(context, activity, context.getResources().getString(R.string.RewardedAdId), new RewardedAdService.ShowRewardedAd() {
                @Override
                public void onAdDismissed() {
                    listener.onEventClick(event,i);
                    holder.hdLayoutLink3.setBackgroundResource(R.drawable.gradient_premium_link_bg_unlocked);
                    holder.hdOverlayFrame.setVisibility(View.GONE);
                }

                @Override
                public void onAdNull() {
                    Toast.makeText(context, "Ad failed to load. Try again later.", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            listener.onEventClick(event,i);
        }
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
        CardView cardLink1, cardLink2, cardLink3;
        LinearLayout eventLinksL, hdLayoutLink3;
        FrameLayout hdOverlayFrame;
        ImageButton expandButton;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            thumbnailArt = itemView.findViewById(R.id.event_Thumb);
            matchNam = itemView.findViewById(R.id.match_Name);
            cardLink1 = itemView.findViewById(R.id.link1Card);
            cardLink2 = itemView.findViewById(R.id.link2Card);
            cardLink3 = itemView.findViewById(R.id.link3Card);
            eventLinksL = itemView.findViewById(R.id.eventLinksLayout);
            expandButton = itemView.findViewById(R.id.nextButton);
            hdLayoutLink3 = itemView.findViewById(R.id.hdLinearLink);
            hdOverlayFrame = itemView.findViewById(R.id.hdFrameOverlay);
        }
    }
}
