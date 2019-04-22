package de.linusdev.colorpicker;

import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class ColorPrefabsAdapter extends RecyclerView.Adapter<ColorPrefabsAdapter.ViewHolder> {

    private int[] prefabs;
    private ClickListener clickListener = null;

    public ColorPrefabsAdapter(int[] prefabs){
        this.prefabs = prefabs;
    }


    @NonNull
    @Override
    public ColorPrefabsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.color_prefab_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ColorPrefabsAdapter.ViewHolder holder, int position) {
        holder.color = prefabs[position];
        holder.colorView.setImageTintList(ColorStateList.valueOf(holder.color));
        holder.colorView.setImageTintMode(PorterDuff.Mode.SRC_ATOP);
        holder.colorView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(clickListener != null){
                    clickListener.onColorClicked(holder.color);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return prefabs.length;
    }

    public void setClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public int color;

        private final ImageView colorView;

        public ViewHolder(View itemView) {
            super(itemView);
            colorView = itemView.findViewById(R.id.imageView);
        }
    }

    public interface ClickListener{
        void onColorClicked(int color);
    }
}
