package cn.chenzhongjin.realmsample.ui.fragment.insert;

import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.orhanobut.logger.Logger;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.chenzhongjin.realmsample.R;
import cn.chenzhongjin.realmsample.entity.User;
import cn.chenzhongjin.realmsample.eventbus.UserUpdateEvent;
import cn.chenzhongjin.realmsample.listeners.CustomItemClickListener;
import cn.chenzhongjin.realmsample.ui.activity.adapter.UserAdapter;
import cn.chenzhongjin.realmsample.ui.base.BaseRvFragment;
import cn.chenzhongjin.realmsample.utils.ValidateUtils;
import info.hoang8f.android.segmented.SegmentedGroup;

/**
 * @author chenzj
 * @Title: InsertFragment
 * @Description: 类的描述 -
 * @date 2016/3/26 11:12
 * @email admin@chenzhongjin.cn
 */
public class InsertFragment extends BaseRvFragment {

    private static final String TAG = InsertFragment.class.getSimpleName();

    private List<User> mUserList;
    private UserAdapter mAdapter;

    private EditText mNameEt;
    private EditText mPhoneNumEt;
    private SegmentedGroup mSegmentedGroup;

    private List<User> mUsers;

    @Override
    protected boolean isRegisterEvent() {
        return true;
    }

    @Override
    protected RecyclerView.LayoutManager getLayoutManager() {
        return new LinearLayoutManager(getActivity());
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_insert;

    }

    @Override
    protected void initViews(View view) {
        ButterKnife.bind(this, view);

        //init cache data
        mUserList = null == mUserList ? new ArrayList<User>() : mUserList;
        mAdapter = new UserAdapter(mUserList, new CustomItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Logger.t(TAG).i("itemClick:viewId=" + view.getId() + "  position=" + position);
            }
        });
        mRecycler.setAdapter(mAdapter);
        mRecycler.setRefreshListener(this);
        mAdapter.notifyDataSetChanged();

    }


    @OnClick(R.id.select_userinfo_button)
    void clickInsertButton() {
        MaterialDialog dialog = new MaterialDialog.Builder(getActivity())
                .title("新增用户数据")
                .customView(R.layout.dialog_insert_customview, true)
                .positiveText("新增")
                .negativeText(android.R.string.cancel)
                .autoDismiss(false)
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                })
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        String nameStr = mNameEt.getText().toString();
                        String phoneNumStr = mPhoneNumEt.getText().toString();
                        String sexStr = mSegmentedGroup.getCheckedRadioButtonId() == R.id.radio_button_man ? "男" : "女";

                        Logger.t(TAG).i("nameStr=" + nameStr + "\nphoneNumStr=" + phoneNumStr + "\nsexStr=" + sexStr);

                        if (TextUtils.isEmpty(nameStr)) {
                            mNameEt.setError("姓名不能为空");
                            return;
                        }
                        if (TextUtils.isEmpty(phoneNumStr)) {
                            mPhoneNumEt.setError("手机号码不能为空");
                            return;
                        } else if (!ValidateUtils.isPhoneNumber(phoneNumStr)) {
                            mPhoneNumEt.setError("请输入正确的手机号码");
                            return;
                        }

                        //insert to database
                        User user = new User();

                        //refresh ui
                        mAdapter.insert(user, 0);

                        dialog.dismiss();

                    }
                }).build();

        mNameEt = (EditText) dialog.getCustomView().findViewById(R.id.name);
        mPhoneNumEt = (EditText) dialog.getCustomView().findViewById(R.id.phone_number);
        mSegmentedGroup = (SegmentedGroup) dialog.getCustomView().findViewById(R.id.segmented);
        mSegmentedGroup.check(R.id.radio_button_man);
        dialog.show();
    }

    @Override
    public void onRefresh() {
        getHandler().post(new Runnable() {
            @Override
            public void run() {

            }
        });
    }

    void loadLatestData(List<User> users) {
        mAdapter.clear();
        mAdapter.addAll(users);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    void loadLatestData(UserUpdateEvent userDataEvent) {
        Logger.t(TAG).i("revice loadLatestData event");
        loadLatestData(userDataEvent.mUsers);
    }
}

