package com.bottom.footballtv.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bottom.footballtv.MainActivity;
import com.bottom.footballtv.R;
import com.bottom.footballtv.models.Room.Eventcat;
import com.bottom.footballtv.services.InterstitialAdService;
import com.bottom.footballtv.services.SelectListener;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class EventCatAdapter extends RecyclerView.Adapter<EventCatAdapter.ViewHolder> {

    private List<com.bottom.footballtv.models.Room.Eventcat> eventcatList;
    private Context context;
    private String type;
    private Activity activity;
    private SelectListener listener;

    public EventCatAdapter(List<com.bottom.footballtv.models.Room.Eventcat> eventcatList, Context context, Activity activity, String type, SelectListener listener) {
        this.eventcatList = eventcatList;
        this.context = context;
        this.activity = activity;
        this.type = type;
        this.listener =listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (Objects.equals(type, "games")){
            view= LayoutInflater.from(context).inflate(R.layout.games_item_view,parent,false);
        } else {
            view= LayoutInflater.from(context).inflate(R.layout.cat_item_view,parent,false);
        }

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        com.bottom.footballtv.models.Room.Eventcat eventcat = eventcatList.get(position);

        holder.catTitle.setText(eventcat.getCategory());
        try {
            Picasso.get()
                    .load(eventcat.getThumbnail())
                    .resize(150, 150)
                    .centerCrop()
                    .placeholder(R.drawable.place_holder)
                    .into(holder.catThumb);
        } catch (Exception e){
            holder.catThumb.setImageResource(R.drawable.place_holder);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                MainActivity.AdClick +=1;
                if (MainActivity.AdClick % 2 == 0) {
                    InterstitialAdService.showInterstitialAd(context, activity, context.getResources().getString(R.string.InterstitialAdId), new InterstitialAdService.ShowInterstitialAd() {
                        @Override
                        public void onAdDismissed() {
                            listener.onCatItemClick(eventcat);
                        }

                        @Override
                        public void onAdNull() {
                            listener.onCatItemClick(eventcat);
                        }
                    });
                } else {
                    listener.onCatItemClick(eventcat);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return eventcatList.size();
    }

    public void updateEventCategories(List<Eventcat> eventcats) {
        this.eventcatList = eventcats;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        CircleImageView catThumb;
        TextView catTitle;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            catThumb = itemView.findViewById(R.id.cat_Thumb);
            catTitle = itemView.findViewById(R.id.cat_Title);
        }
    }
}
