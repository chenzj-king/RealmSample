package cn.chenzhongjin.realmsample.ui.fragment.update;

import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
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
 * @Title: UpdateFragment
 * @Description: 类的描述 -
 * @date 2016/3/26 11:12
 * @email admin@chenzhongjin.cn
 */
public class UpdateFragment extends BaseRvFragment {

    private static final String TAG = UpdateFragment.class.getSimpleName();

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
        return R.layout.fragment_update;

    }

    @Override
    protected void initViews(View view) {
        ButterKnife.bind(this, view);

        mUsers = new ArrayList<>();

        //init cache data
        mAdapter = new UserAdapter(mUsers, new CustomItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                showUpdateDialog(mAdapter.getItemData(position), position);
            }
        });
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
                EventBus.getDefault().post(new UpdateEvent(mAdapter.getData()));
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

                        if (TextUtils.isEmpty(nameStr)) {
                            mNameEt.setError(getString(R.string.input_name_error));
                            return;
                        }
                        if (!ValidateUtils.isPhoneNumber(phoneNumStr)) {
                            mPhoneNumEt.setError(getString(R.string.input_phone_num_error));
                            return;
                        }
                        if (TextUtils.isEmpty(educationStr)) {
                            showToast("请选择学历!");
                            return;
                        }

                        RealmResults<ExtendBean> realmResults = user.mExtendBeanRealmList.where()
                                .equalTo("key", "education")
                                .findAll();

                        String oldEduction = "";
                        if (realmResults.size() == 1) {
                            oldEduction = realmResults.get(0).value;
                        }

                        if (nameStr.equals(user.name) && phoneNumStr.equals(user.phoneNum)
                                && sexStr.equals(user.sex) && educationStr.equals(oldEduction)) {
                            showToast("没有修改任何内容,请修改后再进行提交!");
                            return;
                        }

                        Realm realm = RealmManager.getRealm();
                        realm.beginTransaction();

                        user.name = nameStr;
                        user.phoneNum = phoneNumStr;
                        user.sex = sexStr;

                        if (realmResults.size() == 0) {
                            ExtendBean extendBean = realm.createObject(ExtendBean.class);
                            extendBean.key = "education";
                            extendBean.value = educationStr;
                            user.mExtendBeanRealmList.add(extendBean);
                        } else if (realmResults.size() == 1) {
                            realmResults.get(0).value = educationStr;
                        }
                        realm.commitTransaction();

                        dialog.dismiss();
                        onRefresh();
                    }
                }).build();

        mNameEt = (EditText) dialog.getCustomView().findViewById(R.id.name);
        mPhoneNumEt = (EditText) dialog.getCustomView().findViewById(R.id.phone_number);
        mSexSg = (SegmentedGroup) dialog.getCustomView().findViewById(R.id.sex_segmented);
        mEducationSg = (SegmentedGroup) dialog.getCustomView().findViewById(R.id.education_segmented);

        //init data
        mNameEt.setText(user.name);
        mPhoneNumEt.setText(String.valueOf(user.phoneNum));
        boolean isMan = user.sex.equals("男");
        if (isMan) {
            mSexSg.check(R.id.radio_button_man);
        } else {
            mSexSg.check(R.id.radio_button_woman);
        }

        RealmResults<ExtendBean> realmResults = user.mExtendBeanRealmList.where()
                .equalTo("key", "education")
                .findAll();
        if (realmResults.size() == 1) {
            switch (realmResults.get(0).value) {
                case "高中":
                    mEducationSg.check(R.id.high_school_rb);
                    break;
                case "大专":
                    mEducationSg.check(R.id.junior_college_rb);
                    break;
                case "本科":
                    mEducationSg.check(R.id.regular_college_course_rb);
                    break;
                case "硕士":
                    mEducationSg.check(R.id.master_rb);
                    break;
            }
        }

        dialog.show();
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
    void loadLatestData(SelectEvent userDataEvent) {
        loadLatestData(userDataEvent.mUsers);
    }

    void loadLatestData(List<User> users) {
        mAdapter.clear();
        mAdapter.addAll(users);
    }
}

