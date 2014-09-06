package app.view.chat.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import app.view.login.R;
import com.easemob.util.DensityUtil;
import easemob.domain.VideoEntity;
import easemob.task.AsyncImageLoader;

import java.util.List;

public class ChooseVideoAdapter extends BaseAdapter {

    AsyncImageLoader.OnImageLoadListener imageLoadListener = new AsyncImageLoader.OnImageLoadListener() {

        @Override
        public void onImageLoad(Integer t, Bitmap bitmap) {
            System.out.println("posiIOn:" + t);
            ImageView view = (ImageView) gridView.findViewWithTag(t);
            if (view != null) {
                view.setImageBitmap(bitmap);
            }
        }

        @Override
        public void onError(Integer t) {
            System.out.println("onerror:" + t);
            VideoEntity entity = getItem(t);
            ImageView view = (ImageView) gridView.findViewWithTag(entity);
            if (view != null) {
                view.setImageResource(R.drawable.default_image);
            }

        }
    };
    AbsListView.OnScrollListener onScrollListener = new AbsListView.OnScrollListener() {

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            switch (scrollState) {
                case AbsListView.OnScrollListener.SCROLL_STATE_FLING:
                    asyncImageLoader.lock();
                    break;
                case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                    loadImage();
                    break;
                case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
                    asyncImageLoader.lock();
                    break;

                default:
                    break;
            }

        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            // TODO Auto-generated method stub

        }
    };
    private Context mContext;
    private List<VideoEntity> videoList;

    // LruCache<String, Bitmap> mLruCache = new LruCache<String, Bitmap>(
    // (int) (Runtime.getRuntime().maxMemory() / 16)) {
    // @Override
    // protected int sizeOf(String key, Bitmap value) {
    // return value.getRowBytes() * value.getHeight();
    // }
    // };
    private GridView gridView;
    private AsyncImageLoader asyncImageLoader;

    public ChooseVideoAdapter(Context context, List<VideoEntity> videoList,
                              GridView gridView) {
        this.mContext = context;
        this.videoList = videoList;
        this.gridView = gridView;
        asyncImageLoader = new AsyncImageLoader();
        this.gridView.setOnScrollListener(onScrollListener);
    }

    @Override
    public int getCount() {
        return videoList.size() + 1;
    }

    @Override
    public VideoEntity getItem(int position) {
        return (position == 0) ? null : videoList.get(position - 1);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.choose_griditem, null);
            convertView.setLayoutParams(new GridView.LayoutParams(DensityUtil
                    .dip2px(mContext, 100), DensityUtil.dip2px(mContext, 100)));
            holder.tv_duration = (TextView) convertView
                    .findViewById(R.id.chatting_length_iv);
            holder.tv_size = (TextView) convertView
                    .findViewById(R.id.chatting_size_iv);
            holder.videoImage = (ImageView) convertView
                    .findViewById(R.id.imageView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (position == 0) {
            holder.videoImage.setImageResource(R.drawable.default_image);

        } else {
            final VideoEntity videoEntity = getItem(position);

            holder.tv_duration.setText(videoEntity.duration + "");
            holder.tv_size.setText(videoEntity.size + "");
            holder.videoImage.setImageResource(R.drawable.default_image);
            holder.videoImage.setTag(position);
            asyncImageLoader.loadImage(position, videoEntity.filePath, imageLoadListener);


        }
        return convertView;
    }

    public void loadImage() {
        int start = gridView.getFirstVisiblePosition();
        int end = gridView.getLastVisiblePosition();

        if (end >= getCount()) {
            end = getCount() - 1;
        }
        asyncImageLoader.setLoadLimit(start, end);
        asyncImageLoader.unlock();

    }

    class ViewHolder {
        TextView tv_size;
        TextView tv_duration;
        ImageView videoImage;
    }


}
