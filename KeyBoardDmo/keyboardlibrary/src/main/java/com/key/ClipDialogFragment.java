package com.key;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.key.bean.ClipBean;
import com.key.bean.DaoMaster;
import com.key.bean.DaoSession;

import org.greenrobot.greendao.database.Database;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/5/3 0003.
 */

public class ClipDialogFragment extends DialogFragment implements ClipboardManager.OnPrimaryClipChangedListener {
    private View popView;
    private RelativeLayout mCutContent;

    private ViewPager viewPager;
    private TabLayout tabLayout;
    private String[] titles = {"剪切板", "新增短语"};

    private DaoSession daoSession;
    private ClipboardManager cm;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        DaoMaster.OpenHelper helper = new DaoMaster.DevOpenHelper(getContext(), "clip_db");
        Database database = helper.getWritableDb();
        daoSession = new DaoMaster(database).newSession();
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.AlertCutDialog);
        popView = View.inflate(getContext(), R.layout.cut_pop_layout, null);
        mCutContent = (RelativeLayout) popView.findViewById(R.id.cut_pop_content);
        popView.findViewById(R.id.key_board_arrow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDismiss();
            }
        });
        viewPager = (ViewPager) popView.findViewById(R.id.clip_viewPager);
        tabLayout = (TabLayout) popView.findViewById(R.id.clip_tab_layout);
        List<View> list = new ArrayList<>();
        list.add(new RecyclerView(getContext()));
        viewPager.setAdapter(new AdapterViewpager(list));
        tabLayout.setupWithViewPager(viewPager);

        builder.setView(popView);
        AlertDialog alertDialog = builder.create();
        Window window = alertDialog.getWindow();
        window.setGravity(Gravity.BOTTOM);
        window.setBackgroundDrawableResource(R.drawable.translate);
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        window.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ERROR);
        popView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    onDismiss();

                }
                return false;
            }
        });
        mCutContent.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onGlobalLayout() {
                mCutContent.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                onShow();
            }
        });

        cm = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
        cm.addPrimaryClipChangedListener(this);

        return alertDialog;
    }


    public void onDismiss() {
        ObjectAnimator animator = ObjectAnimator.ofFloat(mCutContent, "translationY", mCutContent.getMeasuredHeight());
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                dismissAllowingStateLoss();
            }
        });
        animator.setDuration(300);
        animator.setInterpolator(new FastOutSlowInInterpolator());
        animator.start();
    }

    private void onShow() {
        ObjectAnimator animator = ObjectAnimator.ofFloat(mCutContent, "translationY", mCutContent.getMeasuredHeight(), 0);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
            }
        });
        animator.setDuration(300);
        animator.setInterpolator(new FastOutSlowInInterpolator());
        animator.start();
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        super.show(manager, tag);
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
    }

    @Override
    public void onPrimaryClipChanged() {
        if (cm == null)
            return;
        ClipData clipData = cm.getPrimaryClip();
        if (clipData != null)
            for (int i = 0; i < clipData.getItemCount(); i++) {
                ClipBean bean = new ClipBean();
                bean.setC_text(clipData.getItemAt(i).getText().toString());
                if (daoSession == null)
                    return;
                daoSession.getClipBeanDao().insertOrReplace(bean);
            }
    }

    public class AdapterViewpager extends PagerAdapter {
        private List<View> mViewList;

        public AdapterViewpager(List<View> mViewList) {
            this.mViewList = mViewList;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }

        @Override
        public int getCount() {//必须实现
            return mViewList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {//必须实现
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {//必须实现，实例化
            container.addView(mViewList.get(position));
            View view = mViewList.get(position);
            if (view != null && view instanceof RecyclerView) {
                ((RecyclerView) view).addItemDecoration(new DividerItemDecoration(container.getContext(), DividerItemDecoration.VERTICAL));
                ClipAdapter clipAdapter = new ClipAdapter();
                ((RecyclerView) view).setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
                ((RecyclerView) view).setAdapter(clipAdapter);
                clipAdapter.setData(daoSession.getClipBeanDao().loadAll());
            }
            return mViewList.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {//必须实现，销毁
            container.removeView(mViewList.get(position));
        }
    }


    class ClipAdapter extends RecyclerView.Adapter {

        private List<ClipBean> clipBeans = new ArrayList<>();

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.cut_pop_item_layout,parent,false);
            THolder holder = new THolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            ClipBean clipBean = clipBeans.get(position);
            ((TextView) holder.itemView).setText(clipBean.getC_text());
        }

        @Override
        public int getItemCount() {
            return clipBeans.size();
        }

        public void setData(List<ClipBean> list) {
            clipBeans.clear();
            clipBeans.addAll(list);
            notifyDataSetChanged();
        }


    }

    class THolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public THolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (itemClickListener != null)
                itemClickListener.onClipItemClick(v);

        }
    }

    private OnClipItemClickListener itemClickListener;

    public void setOnClipItemCickListener(OnClipItemClickListener listener) {
        itemClickListener = listener;
    }

    public interface OnClipItemClickListener {
        void onClipItemClick(View view);
    }
}
