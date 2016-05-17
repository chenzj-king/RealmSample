package cn.chenzhongjin.realmsample.ui.fragment.select;

import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.orhanobut.logger.Logger;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.chenzhongjin.realmsample.R;
import cn.chenzhongjin.realmsample.database.RealmManager;
import cn.chenzhongjin.realmsample.entity.User;
import cn.chenzhongjin.realmsample.eventbus.DeleteEvent;
import cn.chenzhongjin.realmsample.eventbus.InsertEvent;
import cn.chenzhongjin.realmsample.eventbus.SelectEvent;
import cn.chenzhongjin.realmsample.eventbus.UpdateEvent;
import cn.chenzhongjin.realmsample.ui.activity.adapter.UserAdapter;
import cn.chenzhongjin.realmsample.ui.base.BaseRvFragment;
import info.hoang8f.android.segmented.SegmentedGroup;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmQuery;
import io.realm.RealmResults;

/**
 * @author chenzj
 * @Title: SelectFragment
 * @Description: 类的描述 -
 * @date 2016/3/26 11:12
 * @email admin@chenzhongjin.cn
 */
public class SelectFragment extends BaseRvFragment {

    private static final String TAG = SelectFragment.class.getSimpleName();

    private List<User> mUsers;
    private UserAdapter mAdapter;

    private EditText mNameEt;
    private EditText mPhoneNumEt;
    private SegmentedGroup mSexSg;
    private SegmentedGroup mEductionSg;

    @Override
    protected boolean isRegisterEvent() {
        return true;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_select;

    }

    @Override
    protected RecyclerView.LayoutManager getLayoutManager() {
        return new LinearLayoutManager(getActivity());
    }

    @Override
    protected void initViews(View view) {
        ButterKnife.bind(this, view);

        mUsers = new ArrayList<>();

        //init cache data
        mAdapter = new UserAdapter(mUsers, null);
        mRecycler.setAdapter(mAdapter);
        mRecycler.setRefreshListener(this);
        mAdapter.notifyDataSetChanged();
        selectData();
    }

    private void selectData() {
        mAdapter.clear();
        Realm realm = RealmManager.getRealm();

        RealmResults<User> userRealmResults = realm.where(User.class).findAll();
        for (User user : userRealmResults) {
            mAdapter.add(user);
        }
        userRealmResults.addChangeListener(new RealmChangeListener<RealmResults<User>>() {
            @Override
            public void onChange(RealmResults<User> element) {
                mAdapter.clear();
                for (User user : element) {
                    mAdapter.add(user);
                }
            }
        });
    }

    @Override
    public void onRefresh() {
        getHandler().post(new Runnable() {
            @Override
            public void run() {
                selectData();
                EventBus.getDefault().post(new SelectEvent(mAdapter.getData()));
            }
        });
    }

    @OnClick(R.id.select_userinfo_button)
    void clickSelectButton() {
        MaterialDialog dialog = new MaterialDialog.Builder(getActivity())
                .title("查询用户数据")
                .customView(R.layout.dialog_insert_customview, true)
                .positiveText("查询")
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
                        String sexStr = "";
                        String educationStr = "";

                        if (mSexSg.getCheckedRadioButtonId() == R.id.radio_button_man) {
                            sexStr = "男";
                        } else if (mSexSg.getCheckedRadioButtonId() == R.id.radio_button_woman) {
                            sexStr = "女";
                        }

                        switch (mEductionSg.getCheckedRadioButtonId()) {
                            case R.id.high_school_rb:
                                educationStr = "高中";
                                break;
                            case R.id.junior_college_rb:
                                educationStr = "大专";
                                break;
                            case R.id.regular_college_course_rb:
                                educationStr = "本科";
                                break;
                            case R.id.master_rb:
                                educationStr = "硕士";
                                break;
                        }

                        selectData(nameStr, phoneNumStr, sexStr, educationStr, dialog);
                    }
                }).build();

        mNameEt = (EditText) dialog.getCustomView().findViewById(R.id.name);
        mPhoneNumEt = (EditText) dialog.getCustomView().findViewById(R.id.phone_number);
        mSexSg = (SegmentedGroup) dialog.getCustomView().findViewById(R.id.sex_segmented);
        mEductionSg = (SegmentedGroup) dialog.getCustomView().findViewById(R.id.education_segmented);
        dialog.show();
    }

    void selectData(String name, String phoneNum, String sex, String education, final MaterialDialog materialDialog) {

        Logger.t(TAG).i("name=" + name + "\nphoneNum=" + phoneNum + "\nsex=" + sex + "\neducation=" + education);

        Realm realm = RealmManager.getRealm();

        RealmResults<User> userRealmResults;

        RealmQuery<User> realmQuery = realm.where(User.class)
                .contains("name", name)
                .contains("phoneNum", phoneNum);

        if (!TextUtils.isEmpty(sex)) {
            realmQuery.equalTo("sex", sex);
        }

        if (!TextUtils.isEmpty(education)) {
            realmQuery.equalTo("mExtendBeanRealmList.value", education);
        }
        userRealmResults = realmQuery.findAll();
        loadLatestData(userRealmResults);

        materialDialog.dismiss();
    }

    void loadLatestData(List<User> users) {
        mAdapter.clear();
        mAdapter.addAll(users);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    void loadLatestData(InsertEvent userDataEvent) {
        loadLatestData(userDataEvent.mUsers);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    void loadLatestData(DeleteEvent userDataEvent) {
        loadLatestData(userDataEvent.mUsers);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    void loadLatestData(UpdateEvent userDataEvent) {
        loadLatestData(userDataEvent.mUsers);
    }
}

