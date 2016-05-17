package cn.chenzhongjin.realmsample.ui.fragment.delete;

import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.daimajia.swipe.util.Attributes;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import cn.chenzhongjin.realmsample.R;
import cn.chenzhongjin.realmsample.database.RealmManager;
import cn.chenzhongjin.realmsample.entity.User;
import cn.chenzhongjin.realmsample.eventbus.DeleteEvent;
import cn.chenzhongjin.realmsample.eventbus.InsertEvent;
import cn.chenzhongjin.realmsample.eventbus.SelectEvent;
import cn.chenzhongjin.realmsample.eventbus.UpdateEvent;
import cn.chenzhongjin.realmsample.listeners.CustomItemClickListener;
import cn.chenzhongjin.realmsample.ui.base.BaseRvFragment;
import cn.chenzhongjin.realmsample.ui.fragment.delete.adapter.UserDeleteAdapter;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

/**
 * @author chenzj
 * @Title: DeleteFragment
 * @Description: 类的描述 -
 * @date 2016/3/26 10:51
 * @email admin@chenzhongjin.cn
 */
public class DeleteFragment extends BaseRvFragment {

    private static final String TAG = DeleteFragment.class.getSimpleName();

    private UserDeleteAdapter mAdapter;
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
        return R.layout.fragment_delete;

    }

    @Override
    protected void initViews(View view) {
        ButterKnife.bind(this, view);
        //init cache data
        mUsers = new ArrayList<>();

        mAdapter = new UserDeleteAdapter(mUsers, new CustomItemClickListener() {
            @Override
            public void onItemClick(View view, final int position) {
                if (view.getId() == R.id.trash) {

                    getBaseAct().showBaseNotitleDialog("是否删除此条数据", getString(android.R.string.ok),
                            getString(android.R.string.cancel), new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    if (which == DialogAction.POSITIVE) {

                                        Realm realm = RealmManager.getRealm();
                                        realm.beginTransaction();
                                        User delUser = mUsers.get(position);
                                        delUser.deleteFromRealm();
                                        realm.commitTransaction();

                                        dialog.dismiss();
                                        onRefresh();
                                    }
                                }
                            });
                }
            }
        });
        mAdapter.setMode(Attributes.Mode.Single);
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
                EventBus.getDefault().post(new DeleteEvent(mAdapter.getData()));
            }
        });
    }

    void loadLatestData(List<User> userList) {
        mAdapter.clear();
        mAdapter.addAll(userList);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    void loadLatestData(InsertEvent userDataEvent) {
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
}

