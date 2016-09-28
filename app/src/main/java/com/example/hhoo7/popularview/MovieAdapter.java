package com.example.hhoo7.popularview;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.hhoo7.popularview.data.DatabaseContract;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {
    private String LOG_TAG = MovieAdapter.class.getSimpleName();

    private Context mContext;
    private Cursor mCursor;

    private AdapterOnClickHandler mAdapterOnClickHandler;

    public MovieAdapter(Context context, AdapterOnClickHandler newClickHandler) {
        mContext = context;
        mAdapterOnClickHandler = newClickHandler;
    }

    // 构建公共接口，当 recycleview 点击时获取 item 数据
    static interface AdapterOnClickHandler {
        void onClickHandler(String movieId, MovieViewHolder viewHolder);
    }


    public class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView mPosterView;
        TextView mTitleView;

        MovieViewHolder(View view) {
            super(view);
            mPosterView = (ImageView) view.findViewById(R.id.item_movie_poster);
            mTitleView = (TextView) view.findViewById(R.id.item_movie_title);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mCursor.moveToPosition(getAdapterPosition());
            String movieId = mCursor.getString(mCursor.getColumnIndex(DatabaseContract.DetailEntry.COLUMN_MOVIE_ID));
            mAdapterOnClickHandler.onClickHandler(movieId, this);
        }
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.gridview_item, parent, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {
        mCursor.moveToPosition(position);

        String posterUri = mCursor.getString(MovieFragment.COL_POSTER_PATH);
        Utility.loadPicture(mContext, posterUri, holder.mPosterView);

        String movieTile = mCursor.getString(MovieFragment.COL_MOVIE_TITLE);
        holder.mTitleView.setText(movieTile);

    }

    @Override
    public int getItemCount() {
        if (mCursor != null) {
            if (mCursor.getCount() >= 1) {
                return mCursor.getCount();
            }
        }
        return 0;
    }

    public void swapCursor(Cursor cursor) {
        mCursor = cursor;
        notifyDataSetChanged();
    }

}
