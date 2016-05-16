package cn.chenzhongjin.realmsample.ui.fragment.update;

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

import java.util.List;

import butterknife.ButterKnife;
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
 * @Title: UpdateFragment
 * @Description: 类的描述 -
 * @date 2016/3/26 11:12
 * @email admin@chenzhongjin.cn
 */
public class UpdateFragment extends BaseRvFragment {

    private static final String TAG = UpdateFragment.class.getSimpleName();

    private List<User> mUserList;
    private UserAdapter mAdapter;

    private EditText mNameEt;
    private EditText mPhoneNumEt;
    private SegmentedGroup mSegmentedGroup;

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
        return R.layout.fragment_update;

    }

    @Override
    protected void initViews(View view) {
        ButterKnife.bind(this, view);

        //init cache data
        mAdapter = new UserAdapter(mUserList, new CustomItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                showUpdateDialog(mAdapter.getItemData(position), position);
            }
        });
        mRecycler.setAdapter(mAdapter);
        mRecycler.setRefreshListener(this);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onRefresh() {
        getHandler().post(new Runnable() {
            @Override
            public void run() {

            }
        });
    }

    void showUpdateDialog(final User user, final int position) {

        MaterialDialog dialog = new MaterialDialog.Builder(getActivity())
                .title("修改用户数据")
                .customView(R.layout.dialog_insert_customview, true)
                .positiveText("修改")
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

                        if (TextUtils.isEmpty(nameStr)) {
                            mNameEt.setError(getString(R.string.input_name_error));
                            return;
                        }
                        if (!ValidateUtils.isPhoneNumber(phoneNumStr)) {
                            mPhoneNumEt.setError(getString(R.string.input_phone_num_error));
                            return;
                        }

                        if (nameStr.equals(user.name) && phoneNumStr.equals(String.valueOf(user.phoneNum))
                                && sexStr.equals(user.sex)) {
                            showToast("没有修改任何内容,请修改后再进行提交!");
                            return;
                        }

                        //update message
                        User mNewUser = user;


                        //refresh ui
                        mAdapter.remove(position);
                        mAdapter.insert(mNewUser, position);

                        dialog.dismiss();

                    }
                }).build();

        mNameEt = (EditText) dialog.getCustomView().findViewById(R.id.name);
        mPhoneNumEt = (EditText) dialog.getCustomView().findViewById(R.id.phone_number);
        mSegmentedGroup = (SegmentedGroup) dialog.getCustomView().findViewById(R.id.segmented);

        //init data
        mNameEt.setText(user.name);
        mPhoneNumEt.setText(String.valueOf(user.phoneNum));
        boolean isMan = user.sex.equals("男");
        if (isMan) {
            mSegmentedGroup.check(R.id.radio_button_man);
        } else {
            mSegmentedGroup.check(R.id.radio_button_woman);
        }
        dialog.show();
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

