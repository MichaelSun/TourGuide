package com.find.guide.fragment;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.find.guide.R;
import com.find.guide.activity.BaseActivity;
import com.find.guide.activity.GuideEventDetailActivity;
import com.find.guide.activity.InviteEventDetailActivity;
import com.find.guide.activity.LoginActivity;
import com.find.guide.adapter.GuideRecordAdapter;
import com.find.guide.adapter.InviteRecordAdapter;
import com.find.guide.app.TourGuideApplication;
import com.find.guide.model.GuideEvent;
import com.find.guide.model.InviteEvent;
import com.find.guide.model.Tourist;
import com.find.guide.model.helper.GuideHelper;
import com.find.guide.model.helper.GuideHelper.OnGetHistoricalGuideEventsListener;
import com.find.guide.model.helper.InviteHelper;
import com.find.guide.model.helper.InviteHelper.OnGetHistoricalInviteEventsListener;
import com.find.guide.setting.SettingManager;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

public class RecordFragment extends Fragment {

    private static final int REQUEST_CODE_LOGIN = 1;
    private static final int REQUEST_CODE_INVITE_RECORD = 2;
    private static final int REQUEST_CODE_GUIDE_RECORD = 3;

    private PullToRefreshListView mListView;

    private InviteHelper mInviteHelper = null;
    private GuideHelper mGuideHelper = null;

    private SettingManager mSettingManager;

    private BaseAdapter mRecordAdapter;
    private List<InviteEvent> mInviteEvents = new ArrayList<InviteEvent>();
    private List<GuideEvent> mGuideEvents = new ArrayList<GuideEvent>();

    private AlertDialog mAlertDialog;

    private Handler mHandler = new Handler(Looper.getMainLooper());

    private boolean mIsRefreshing = false;

    private int ROWS = 21;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mInviteHelper = new InviteHelper(TourGuideApplication.getInstance());
        mGuideHelper = new GuideHelper(TourGuideApplication.getInstance());

        mSettingManager = SettingManager.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_record, null);

        mListView = (PullToRefreshListView) view.findViewById(R.id.listview);
        mListView.setShowIndicator(false);
        mListView.setMode(Mode.PULL_FROM_START);

        mListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {

            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                if (mSettingManager.getUserId() <= 0) {
                    showLoginDialog();
                } else {
                    refresh();
                }
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                loadMore();
            }
        });

        mListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int pos = position - mListView.getRefreshableView().getHeaderViewsCount();
                if (mRecordAdapter != null && mRecordAdapter instanceof GuideRecordAdapter) {
                    if (pos >= 0 && pos < mGuideEvents.size()) {
                        Intent intent = new Intent(getActivity(), GuideEventDetailActivity.class);
                        intent.putExtra(GuideEventDetailActivity.INTENT_EXTRA_GUIDE_EVENT_OBJ, mGuideEvents.get(pos));
                        startActivityForResult(intent, REQUEST_CODE_GUIDE_RECORD);
                    }
                } else if (mRecordAdapter != null && mRecordAdapter instanceof InviteRecordAdapter) {
                    if (pos >= 0 && pos < mInviteEvents.size()) {
                        Intent intent = new Intent(getActivity(), InviteEventDetailActivity.class);
                        intent.putExtra(InviteEventDetailActivity.INTENT_EXTRA_INVITE_EVENT_OBJ, mInviteEvents.get(pos));
                        startActivityForResult(intent, REQUEST_CODE_INVITE_RECORD);
                    }
                }
            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        // refresh();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            if (mSettingManager.getUserId() <= 0) {
                // 登录弹窗
                showLoginDialog();
            }
            setTitle();
            refresh();
        }
    }

    private void setTitle() {
        String title = null;
        if (mSettingManager.getUserId() <= 0) {
            title = getString(R.string.profile_record) + "(" + getString(R.string.unlogin) + ")";
            // 登录弹窗
            showLoginDialog();
        } else if (mSettingManager.getUserType() == Tourist.USER_TYPE_TOURGUIDE && mSettingManager.getGuideMode() == 0) {
            title = getString(R.string.profile_record) + "(" + getString(R.string.tourguide) + ")";
        } else {
            title = getString(R.string.profile_record) + "(" + getString(R.string.tourist) + ")";
        }
        ((BaseActivity) getActivity()).getActionBar().setTitle(title);
    }

    private void refresh() {
        if (mSettingManager.getUserId() <= 0) {
            if (mRecordAdapter != null) {
                mInviteEvents.clear();
                mGuideEvents.clear();
                mRecordAdapter.notifyDataSetChanged();
            }
        } else if (mSettingManager.getUserType() == Tourist.USER_TYPE_TOURGUIDE && mSettingManager.getGuideMode() == 0) {
            if (mRecordAdapter == null || !(mRecordAdapter instanceof GuideRecordAdapter)) {
                mRecordAdapter = new GuideRecordAdapter(getActivity(), mGuideEvents);
                mGuideEvents.clear();
                mInviteEvents.clear();
                mListView.setAdapter(mRecordAdapter);
            }
        } else {
            if (mRecordAdapter == null || !(mRecordAdapter instanceof InviteRecordAdapter)) {
                mRecordAdapter = new InviteRecordAdapter(getActivity(), mInviteEvents);
                mGuideEvents.clear();
                mInviteEvents.clear();
                mListView.setAdapter(mRecordAdapter);
            }
        }

        if (mSettingManager.getUserId() > 0) {
            if (!mIsRefreshing) {
                mIsRefreshing = true;
                if (mSettingManager.getUserType() == Tourist.USER_TYPE_TOURGUIDE && mSettingManager.getGuideMode() == 0) {
                    mGuideHelper.getHistoricalGuideEvents(0, 0, ROWS, mGetHistoricalGuideEventsListener);
                } else {
                    mInviteHelper.getHistoricalInviteEvents(0, ROWS, mGetHistoricalInviteEventsListener);
                }
            }
        } else {
            mIsRefreshing = false;
            mListView.onRefreshComplete();
        }
    }

    private void showLoginDialog() {
        dismissDialog();
        if (getActivity() != null) {
            mAlertDialog = new AlertDialog.Builder(getActivity()).setMessage(R.string.login_dialog_message)
                    .setPositiveButton(R.string.login_dialog_positive, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(getActivity(), LoginActivity.class);
                            startActivityForResult(intent, REQUEST_CODE_LOGIN);
                        }
                    }).setNegativeButton(R.string.login_dialog_negative, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }).create();
            mAlertDialog.show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CODE_LOGIN) {
                setTitle();
                refresh();
            } else if (requestCode == REQUEST_CODE_GUIDE_RECORD || requestCode == REQUEST_CODE_INVITE_RECORD) {
                refresh();
            }
        }
    }

    private void dismissDialog() {
        if (mAlertDialog != null && mAlertDialog.isShowing()) {
            mAlertDialog.dismiss();
            mAlertDialog = null;
        }
    }

    private void loadMore() {
        if (mSettingManager.getUserId() > 0) {
            if (!mIsRefreshing) {
                mIsRefreshing = true;
                int start = 0;
                if (mSettingManager.getUserType() == Tourist.USER_TYPE_TOURGUIDE && mSettingManager.getGuideMode() == 0) {
                    if (mGuideEvents != null)
                        start = mGuideEvents.size();
                    mGuideHelper.getHistoricalGuideEvents(0, start, ROWS, mGetHistoricalGuideEventsListener);
                } else {
                    if (mInviteEvents != null)
                        start = mInviteEvents.size();
                    mInviteHelper.getHistoricalInviteEvents(start, ROWS, mGetHistoricalInviteEventsListener);
                }
            }
        }
    }

    private OnGetHistoricalInviteEventsListener mGetHistoricalInviteEventsListener = new OnGetHistoricalInviteEventsListener() {
        @Override
        public void onGetHistoricalInviteEvents(final int result, final List<InviteEvent> inviteEvents) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mIsRefreshing = false;
                    mListView.onRefreshComplete();
                    if (result == InviteHelper.SUCCESS) {
                        if (inviteEvents != null && inviteEvents.size() > 0) {
                            if (inviteEvents.size() >= ROWS) {
                                mListView.setMode(Mode.BOTH);
                                inviteEvents.remove(inviteEvents.size() - 1);
                            } else {
                                mListView.setMode(Mode.PULL_FROM_START);
                            }
                            mInviteEvents.clear();
                            mInviteEvents.addAll(inviteEvents);
                            mRecordAdapter.notifyDataSetChanged();
                        } else {
                            mListView.setMode(Mode.PULL_FROM_START);
                        }
                    }
                }
            });
        }

        @Override
        public void onGetMoreHistoricalInviteEvents(final int result, final List<InviteEvent> inviteEvents) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mIsRefreshing = false;
                    mListView.onRefreshComplete();
                    if (result == InviteHelper.SUCCESS) {
                        if (inviteEvents != null && inviteEvents.size() > 0) {
                            if (inviteEvents.size() >= ROWS) {
                                mListView.setMode(Mode.BOTH);
                                inviteEvents.remove(inviteEvents.size() - 1);
                            } else {
                                mListView.setMode(Mode.PULL_FROM_START);
                            }
                            mInviteEvents.addAll(inviteEvents);
                            mRecordAdapter.notifyDataSetChanged();
                        } else {
                            mListView.setMode(Mode.PULL_FROM_START);
                        }
                    }
                }
            });
        }
    };

    private OnGetHistoricalGuideEventsListener mGetHistoricalGuideEventsListener = new OnGetHistoricalGuideEventsListener() {
        @Override
        public void onGetHistoricalGuideEvents(final int result, final List<GuideEvent> guideEvents) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mIsRefreshing = false;
                    mListView.onRefreshComplete();
                    if (result == GuideHelper.SUCCESS) {
                        if (guideEvents != null && guideEvents.size() > 0) {
                            if (guideEvents.size() >= ROWS) {
                                mListView.setMode(Mode.BOTH);
                                guideEvents.remove(guideEvents.size() - 1);
                            } else {
                                mListView.setMode(Mode.PULL_FROM_START);
                            }
                            mGuideEvents.clear();
                            mGuideEvents.addAll(guideEvents);
                            mRecordAdapter.notifyDataSetChanged();
                        } else {
                            mListView.setMode(Mode.PULL_FROM_START);
                        }
                    }
                }
            });
        }

        @Override
        public void onGetMoreHistoricalGuideEvents(final int result, final List<GuideEvent> guideEvents) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mIsRefreshing = false;
                    mListView.onRefreshComplete();
                    if (result == InviteHelper.SUCCESS) {
                        if (guideEvents != null && guideEvents.size() > 0) {
                            if (guideEvents.size() >= ROWS) {
                                mListView.setMode(Mode.BOTH);
                                guideEvents.remove(guideEvents.size() - 1);
                            } else {
                                mListView.setMode(Mode.PULL_FROM_START);
                            }
                            mGuideEvents.addAll(guideEvents);
                            mRecordAdapter.notifyDataSetChanged();
                        } else {
                            mListView.setMode(Mode.PULL_FROM_START);
                        }
                    }
                }
            });
        }
    };

    @Override
    public void onDestroy() {
        dismissDialog();
        if (mInviteHelper != null) {
            mInviteHelper.destroy();
            mInviteHelper = null;
        }
        if (mGuideHelper != null) {
            mGuideHelper.destroy();
            mGuideHelper = null;
        }
        super.onDestroy();
    }

}
