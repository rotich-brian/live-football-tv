package com.bottom.footballtv.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bottom.footballtv.R;
import com.bottom.footballtv.models.Eventcat;
import com.bottom.footballtv.tools.SelectListener;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class EventCatAdapter extends RecyclerView.Adapter<EventCatAdapter.ViewHolder> {

    private List<Eventcat> eventcatList;
    private Context context;
    private String type;
    private SelectListener listener;

    public EventCatAdapter(List<Eventcat> eventcatList, Context context, String type, SelectListener listener) {
        this.eventcatList = eventcatList;
        this.context = context;
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
        Eventcat eventcat = eventcatList.get(position);

        holder.catTitle.setText(eventcat.getCategory());
        try {
            Picasso.get()
                    .load(eventcat.getThumbnail())
                    .resize(50, 50)
                    .centerCrop()
                    .placeholder(R.drawable.place_holder)
                    .into(holder.catThumb);
        } catch (Exception e){
            holder.catThumb.setImageResource(R.drawable.place_holder);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onCatItemClick(eventcat);

                if (Objects.equals(type, "games")){
//                    Navigation.findNavController(view).navigate(R.id.action_navigation_games_to_eventsFragment3);
                } else {
//                    Navigation.findNavController(view).navigate(R.id.action_navigation_home_to_naviagtion_events);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return eventcatList.size();
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
