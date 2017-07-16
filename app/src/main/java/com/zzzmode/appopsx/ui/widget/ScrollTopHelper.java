package com.zzzmode.appopsx.ui.widget;

import android.content.res.Resources;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.View;
import android.view.View.OnClickListener;
import com.h6ah4i.android.widget.advrecyclerview.expandable.RecyclerViewExpandableItemManager;

/**
 * Created by zl on 2017/7/13.
 */

public class ScrollTopHelper extends OnScrollListener {


  private RecyclerView recyclerView;
  private LinearLayoutManager linearLayoutManager;
  private RecyclerViewExpandableItemManager mRVExpandableItemManager;

  private View fab;

  public ScrollTopHelper(final RecyclerView recyclerView,
      LinearLayoutManager linearLayoutManager,
      RecyclerViewExpandableItemManager rVExpandableItemManager,View fab) {
    this.recyclerView = recyclerView;
    this.linearLayoutManager = linearLayoutManager;
    this.mRVExpandableItemManager = rVExpandableItemManager;
    this.fab = fab;
    recyclerView.addOnScrollListener(this);

    fab.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        if (childPos != RecyclerView.NO_POSITION) {

          Resources resources = v.getContext().getResources();
          int pad = (int) (resources.getDisplayMetrics().density * 10);
          int childItemHeight =
              resources.getDimensionPixelSize(android.R.dimen.app_icon_size) + pad * 2;
          int topMargin = (int) (resources.getDisplayMetrics().density * 16);
          int bottomMargin = topMargin;

          v.setEnabled(false);

          recyclerView
              .smoothScrollBy(0, -((childPos + 2) * childItemHeight + topMargin + bottomMargin));
        }

      }
    });
  }


  private void trackHeader() {

    long firstExpandablePosition = mRVExpandableItemManager
        .getExpandablePosition(linearLayoutManager.findFirstVisibleItemPosition());

    int fChildPos = RecyclerViewExpandableItemManager
        .getPackedPositionChild(firstExpandablePosition);
    int fGroupPos = RecyclerViewExpandableItemManager
        .getPackedPositionGroup(firstExpandablePosition);

    boolean fGroupExpanded = mRVExpandableItemManager.isGroupExpanded(fGroupPos);

    boolean show= false;

    if (fChildPos == RecyclerView.NO_POSITION) {
      //group position
      if (fGroupExpanded) {
        long lastExpandablePosition = mRVExpandableItemManager
            .getExpandablePosition(linearLayoutManager.findLastVisibleItemPosition());

        int lGroupPos = RecyclerViewExpandableItemManager
            .getPackedPositionGroup(lastExpandablePosition);

        show = (lGroupPos == fGroupPos && fGroupPos != 0);

      } else {
        hide();
      }

    } else {
      //child position
      long lastExpandablePosition = mRVExpandableItemManager
          .getExpandablePosition(linearLayoutManager.findLastVisibleItemPosition());
      int lGroupPos = RecyclerViewExpandableItemManager
          .getPackedPositionGroup(lastExpandablePosition);
      show = (lGroupPos == fGroupPos);
    }

    if(show){
      childPos = fChildPos;
      show();
    }else {
      hide();
    }

  }


  private void hide() {
    childPos = RecyclerView.NO_POSITION;
    fab.animate().alpha(0).start();
  }

  private int measuredHeight = 0;
  private int childPos = 0;

  private void show() {
    if (measuredHeight == 0) {
      ViewHolder viewHolder = recyclerView.findViewHolderForLayoutPosition(0);
      if (viewHolder != null && viewHolder.itemView != null) {
        measuredHeight = viewHolder.itemView
            .getMeasuredHeight();
      }

    }

    fab.setEnabled(true);

    fab.animate().alpha(1).start();

  }


  @Override
  public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
    super.onScrollStateChanged(recyclerView, newState);

    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
      trackHeader();
    }
  }


  public void release() {
    recyclerView.removeOnScrollListener(this);
  }
}
