package cn.chenzhongjin.realmsample.ui.fragment.delete.adapter;

import android.annotation.SuppressLint;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.daimajia.swipe.SimpleSwipeListener;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;
import com.orhanobut.logger.Logger;

import java.util.List;

import cn.chenzhongjin.realmsample.R;
import cn.chenzhongjin.realmsample.entity.User;
import cn.chenzhongjin.realmsample.listeners.CustomItemClickListener;


/**
 * @author: chenzj
 * @Title: UserDeleteAdapter
 * @Description:
 * @date: 2016/3/24 23:13
 * @email: admin@chenzhongjin.cn
 */
public class UserDeleteAdapter extends RecyclerSwipeAdapter<UserDeleteAdapter.ViewHolder> {

    private static final String TAG = UserDeleteAdapter.class.getSimpleName();

    private int mSelectIndex = -1;
    private CloseTouchListener mCloseTouchListener;
    private List<User> mData;
    private CustomItemClickListener mCustomItemClickListener;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_info_delete, parent, false);
        return new ViewHolder(view, viewType, mCustomItemClickListener);
    }

    public UserDeleteAdapter(List<User> userList, CustomItemClickListener customItemClickListener) {
        mData = userList;
        mCloseTouchListener = new CloseTouchListener();
        mCustomItemClickListener = customItemClickListener;
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.userinfo_swipe_layout;
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        final User itemData = mData.get(position);
        holder.mNameTv.setText(String.format("姓名:%s", itemData.name));
        holder.mSexTv.setText(String.format("性别:%s", itemData.sex));
        holder.mPhoneNumTv.setText(String.format("电话号码:%d", itemData.phoneNum));

        holder.mSwipeLayout.setShowMode(SwipeLayout.ShowMode.LayDown);
        holder.mSwipeLayout.addSwipeListener(new SimpleSwipeListener() {
            @Override
            public void onOpen(SwipeLayout layout) {
                YoYo.with(Techniques.Tada).duration(500).delay(100).playOn(layout.findViewById(R.id.trash));
            }
        });
        mItemManger.bindView(holder.itemView, position);
    }

    @Override
    public int getItemCount() {
        return null == mData ? 0 : mData.size();
    }

    public User getItemData(int position) {
        return mData == null ? null : mData.size() < position ? null : mData.get(position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private CustomItemClickListener mCustomItemClickListener;
        SwipeLayout mSwipeLayout;
        ImageView mDelImv;

        TextView mNameTv;
        TextView mSexTv;
        TextView mPhoneNumTv;

        public ViewHolder(View itemView, int viewType, CustomItemClickListener customItemClickListener) {
            super(itemView);

            mCustomItemClickListener = customItemClickListener;

            mNameTv = (TextView) itemView.findViewById(R.id.user_info_name);
            mSexTv = (TextView) itemView.findViewById(R.id.user_info_sex);
            mPhoneNumTv = (TextView) itemView.findViewById(R.id.user_info_phone_number);
            mSwipeLayout = (SwipeLayout) itemView.findViewById(R.id.userinfo_swipe_layout);
            mDelImv = (ImageView) itemView.findViewById(R.id.trash);

            mDelImv.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (null != mCustomItemClickListener) {
                mCustomItemClickListener.onItemClick(v, getAdapterPosition());
            }
        }
    }

    public void add(User user) {
        insert(user, mData.size());
    }

    public void insert(User user, int position) {
        mData.add(position, user);
        notifyItemInserted(position);
    }

    public void remove(int position) {
        mData.remove(position);
        notifyItemRemoved(position);
    }

    public void clear() {
        int size = mData.size();
        mData.clear();
        notifyItemRangeRemoved(0, size);
    }

    public void addAll(List<User> userList) {

        if (null != userList && userList.size() > 0) {
            int startIndex = mData.size();
            mData.addAll(startIndex, userList);
            notifyItemRangeInserted(startIndex, userList.size());
        } else {
            Logger.t(TAG).i("userList addall is null or size = 0");
        }
    }

    public void addStartAll(List<User> userList) {
        if (null != userList && userList.size() > 0) {
            mData.addAll(0, userList);
            notifyItemRangeInserted(0, userList.size());
        } else {
            Logger.t(TAG).i("chatMsgs addall is null or size = 0");
        }
    }


    class CloseTouchListener implements View.OnTouchListener {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (mSelectIndex != -1) {
                final int position = mSelectIndex;
                mSelectIndex = -1;
                Log.i("chenzj", "mSelectIndex=" + mSelectIndex + "\nposition=" + position);
                notifyItemChanged(position);
                return true;
            }
            return false;
        }
    }
}
