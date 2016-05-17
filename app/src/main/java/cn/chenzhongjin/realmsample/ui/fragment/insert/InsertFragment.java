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

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.chenzhongjin.realmsample.R;
import cn.chenzhongjin.realmsample.database.RealmManager;
import cn.chenzhongjin.realmsample.entity.ExtendBean;
import cn.chenzhongjin.realmsample.entity.User;
import cn.chenzhongjin.realmsample.eventbus.DeleteEvent;
import cn.chenzhongjin.realmsample.eventbus.InsertEvent;
import cn.chenzhongjin.realmsample.eventbus.SelectEvent;
import cn.chenzhongjin.realmsample.eventbus.UpdateEvent;
import cn.chenzhongjin.realmsample.listeners.CustomItemClickListener;
import cn.chenzhongjin.realmsample.ui.activity.adapter.UserAdapter;
import cn.chenzhongjin.realmsample.ui.base.BaseRvFragment;
import cn.chenzhongjin.realmsample.utils.ValidateUtils;
import info.hoang8f.android.segmented.SegmentedGroup;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

/**
 * @author chenzj
 * @Title: InsertFragment
 * @Description: 类的描述 -
 * @date 2016/3/26 11:12
 * @email admin@chenzhongjin.cn
 */
public class InsertFragment extends BaseRvFragment {

    private static final String TAG = InsertFragment.class.getSimpleName();

    private List<User> mUsers;
    private UserAdapter mAdapter;

    private EditText mNameEt;
    private EditText mPhoneNumEt;
    private SegmentedGroup mSexSg;
    private SegmentedGroup mEducationSg;


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
        mUsers = new ArrayList<>();

        mAdapter = new UserAdapter(mUsers, new CustomItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Logger.t(TAG).i("itemClick:viewId=" + view.getId() + "  position=" + position);
            }
        });
        mRecycler.setAdapter(mAdapter);
        mRecycler.setRefreshListener(this);
        mAdapter.notifyDataSetChanged();

        selectData();
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
                        String sexStr = mSexSg.getCheckedRadioButtonId() == R.id.radio_button_man ? "男" : "女";
                        String educationStr = "";

                        switch (mEducationSg.getCheckedRadioButtonId()) {
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
                        Logger.t(TAG).i("nameStr=" + nameStr + "\nphoneNumStr=" + phoneNumStr + "\nsexStr=" + sexStr +
                                "\neducationoStr=" + educationStr);

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

                        //insert new data
                        Realm realm = RealmManager.getRealm();

                        realm.beginTransaction();
                        User user = realm.createObject(User.class);
                        user.name = nameStr;
                        user.phoneNum = phoneNumStr;
                        user.sex = sexStr;

                        ExtendBean extendBean = realm.createObject(ExtendBean.class);
                        extendBean.key = "education";
                        extendBean.value = educationStr;
                        user.mExtendBeanRealmList.add(extendBean);

                        realm.commitTransaction();

                        dialog.dismiss();
                        onRefresh();
                    }
                }).build();

        mNameEt = (EditText) dialog.getCustomView().findViewById(R.id.name);
        mPhoneNumEt = (EditText) dialog.getCustomView().findViewById(R.id.phone_number);
        mSexSg = (SegmentedGroup) dialog.getCustomView().findViewById(R.id.sex_segmented);
        mSexSg.check(R.id.radio_button_man);

        mEducationSg = (SegmentedGroup) dialog.getCustomView().findViewById(R.id.education_segmented);
        mEducationSg.check(R.id.high_school_rb);
        dialog.show();
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
                EventBus.getDefault().post(new InsertEvent(mAdapter.getData()));
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    void loadLatestData(DeleteEvent userDataEvent) {
        loadLatestData(userDataEvent.mUsers);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    void loadLatestData(SelectEvent userDataEvent) {
        loadLatestData(userDataEvent.mUsers);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    void loadLatestData(UpdateEvent userDataEvent) {
        loadLatestData(userDataEvent.mUsers);
    }

    void loadLatestData(List<User> users) {
        mAdapter.clear();
        mAdapter.addAll(users);
    }
}

