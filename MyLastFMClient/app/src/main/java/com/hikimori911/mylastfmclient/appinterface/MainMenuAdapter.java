package com.hikimori911.mylastfmclient.appinterface;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hikimori911.mylastfmclient.R;
import com.hikimori911.mylastfmclient.data.AppPreferenceHelper;
import com.squareup.picasso.Picasso;

/**
 * Created by hikimori911 on 13.04.2015.
 */
public class MainMenuAdapter extends RecyclerView.Adapter<MainMenuAdapter.ViewHolder> {

    protected static final int TYPE_HEADER = 0;
    protected static final int TYPE_ITEM = 1;

    protected String[] mTitles;
    protected int mSelectedIndex;
    protected Context mCtx;
    protected Picasso mPicasso;

    public MainMenuAdapter(String[] Titles, Context ctx){
        mTitles = Titles;
        mCtx = ctx;
        mPicasso = Picasso.with(ctx);
        mSelectedIndex = 1;
    }

    public int getSelectedIndex(){
        return mSelectedIndex;
    }

    @Override
    public MainMenuAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_drawer_item_layout,parent,false);
            ViewHolder vhItem = new ViewHolder(v,viewType);
            return vhItem;
        } else if (viewType == TYPE_HEADER) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_drawer_header_layout,parent,false);
            ViewHolder vhHeader = new ViewHolder(v,viewType);
            return vhHeader;
        }
        return null;

    }

    @Override
    public void onBindViewHolder(MainMenuAdapter.ViewHolder holder, int position) {
        if(holder.Holderid ==1) {
            holder.textView.setText(mTitles[position - 1]);
            holder.view.setActivated(getSelectedIndex() == position);
        }else{
            mPicasso.load(AppPreferenceHelper.getUserAvatarURL(mCtx)).into(holder.imageView);
            holder.textView.setText(AppPreferenceHelper.getUserName(mCtx));
        }
    }

    @Override
    public int getItemCount() {
        return mTitles.length+1; // the number of items in the list will be +1 the titles including the header view.
    }


    @Override
    public int getItemViewType(int position) {
        if (isPositionHeader(position))
            return TYPE_HEADER;
        return TYPE_ITEM;
    }

    private boolean isPositionHeader(int position) {
        return position == 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        int Holderid;
        TextView textView;
        ImageView imageView;
        View view;

        public ViewHolder(View itemView, int ViewType){
            super(itemView);
            view = itemView;
            if(ViewType == TYPE_ITEM) {
                itemView.setClickable(true);
                itemView.setFocusable(true);
                itemView.setFocusableInTouchMode(true);
                textView = (TextView) itemView.findViewById(R.id.menu_item_name);
                Holderid = 1;
            }else{
                textView = (TextView) itemView.findViewById(R.id.user_name);
                imageView = (ImageView) itemView.findViewById(R.id.user_avatar);
                Holderid = 0;
            }
        }
    }
}
