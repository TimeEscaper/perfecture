package ru.mail.tp.perfecture.places.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ru.mail.tp.perfecture.R;
import ru.mail.tp.perfecture.api.Place;
import ru.mail.tp.perfecture.places.PlaceInfoActivity;

public class PlacesAdapter extends RecyclerView.Adapter<PlacesAdapter.PlaceViewHolder> {

    private final List<Place> places;
    private final LayoutInflater inflater;
    private final Context context;

    public PlacesAdapter(final Context context, List<Place> places) {
        this.inflater = LayoutInflater.from(context);
        this.places = places;
        this.context = context;
    }

    @Override
    public PlaceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new PlaceViewHolder(inflater.inflate(R.layout.place_title_item, parent, false));
    }

    @Override
    public void onBindViewHolder(PlaceViewHolder holder, int position) {
        holder.bind(places.get(position).getId(), places.get(position).getTitle());
    }

    @Override
    public int getItemCount() {
        return places.size();
    }

    @SuppressWarnings("WeakerAccess")
    final class PlaceViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;
        private long placeId;
        private String title;

        public PlaceViewHolder(View view) {
            super(view);
            textView = (TextView) view.findViewById(R.id.place_title_item);
        }

        public void bind(final long placeId, final String title) {
            this.placeId = placeId;
            this.title = title;
            textView.setText(title);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, PlaceInfoActivity.class);
                    intent.putExtra(PlaceInfoActivity.EXTRA_PLACE_TAG, placeId);
                    context.startActivity(intent);
                }
            });
        }


    }
}
