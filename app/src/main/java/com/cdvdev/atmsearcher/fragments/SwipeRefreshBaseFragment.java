package com.cdvdev.atmsearcher.fragments;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.cdvdev.atmsearcher.R;

public abstract class SwipeRefreshBaseFragment extends BaseListFragmentWithAds {

    private SwipeRefreshLayout mSwipeRefreshLayout;

    abstract int getLayoutResId();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //create SwipeRefreshLayout
        mSwipeRefreshLayout = new ListFragmentSwipeRefreshLayout(getActivity().getBaseContext());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View listFragment = inflater.inflate(getLayoutResId(), container, false);

        //add list view to SwipeRefreshLayout
        mSwipeRefreshLayout.addView(
                listFragment,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );


        //fill fragment
        mSwipeRefreshLayout.setLayoutParams(
                new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                )
        );

        //colors
        TypedArray colors = getResources().obtainTypedArray(R.array.swipe_refresh_colors);
        mSwipeRefreshLayout.setColorSchemeColors(
                colors.getColor(0, 0),
                colors.getColor(1, 0),
                colors.getColor(2, 0),
                colors.getColor(3, 0)
        );

        return mSwipeRefreshLayout;
    }

    public SwipeRefreshLayout getSwipeRefreshLayout(){
        return mSwipeRefreshLayout;
    }

    /**
     * Listener for refreshes
     * @param listener SwipeRefreshLayout.OnRefreshListener
     */
    public void setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener listener){
        mSwipeRefreshLayout.setOnRefreshListener(listener);
    }

    /**
     * Check refresh progress
     * @return true - showing
     */
    public boolean isRefreshing(){
        return mSwipeRefreshLayout.isRefreshing();
    }

    /**
     * Stop refresh progress
     */
    public void stopRefreshing(){
        mSwipeRefreshLayout.setRefreshing(false);
    }

    /**
     * Start refresh progress
     */
    public void startRefresh(){
        mSwipeRefreshLayout.setRefreshing(true);
    }


    private class ListFragmentSwipeRefreshLayout extends SwipeRefreshLayout{

        public ListFragmentSwipeRefreshLayout(Context context){
            super(context);
        }

        /**
         * signal when a swipe-to-refresh' is possible
         * @return true - ListView is visible
         */
        @Override
        public boolean canChildScrollUp() {
            ListView listView = getListView();

            if (listView.getVisibility() == View.VISIBLE) {
                return canListViewScrollUp(listView);
            } else {
                return false;
            }

        }
    }

    /**
     * * Utility method to check whether a ListView can scroll up from it's current position.
     * @param listView
     * @return Boolean
     */
    private static boolean canListViewScrollUp(ListView listView) {
        if (android.os.Build.VERSION.SDK_INT >= 14) {
            // For ICS and above we can call canScrollVertically() to determine this
            return ViewCompat.canScrollVertically(listView, -1);
        } else {
            // Pre-ICS we need to manually check the first visible item and the child view's top
            // value
            return listView.getChildCount() > 0 &&
                    (listView.getFirstVisiblePosition() > 0
                            || listView.getChildAt(0).getTop() < listView.getPaddingTop());
        }
    }
}
