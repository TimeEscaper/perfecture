package ru.mail.tp.perfecture.places;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

import ru.mail.tp.perfecture.R;

class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {

    private List<String> urls;
    private final LayoutInflater inflater;

    ImageAdapter(final Context context, List<String> urls) {
        this.inflater = LayoutInflater.from(context);
        this.urls = urls;
    }

    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ImageViewHolder(inflater.inflate(R.layout.image_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ImageViewHolder holder, int position) {
        Picasso.with(holder.image.getContext()).load(urls.get(position)).into(holder.image);
    }

    @Override
    public int getItemCount() {
        return urls.size();
    }

    static class ImageViewHolder extends RecyclerView.ViewHolder {
        private final ImageView image;

        ImageViewHolder(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.image);
        }
    }
}
