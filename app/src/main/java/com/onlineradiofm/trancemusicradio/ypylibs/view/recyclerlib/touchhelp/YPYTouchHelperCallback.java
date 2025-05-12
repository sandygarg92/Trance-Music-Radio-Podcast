package com.onlineradiofm.trancemusicradio.ypylibs.view.recyclerlib.touchhelp;

import android.graphics.Canvas;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @author:Radio Polska
 * @Skype: 
 * @Mobile : 
 * @Email: 
 * @Website: http://radiopolska.com
 * Created by radiopolska on 10/11/18.
 */
public class YPYTouchHelperCallback extends ItemTouchHelper.Callback {

    public static final float ALPHA_FULL = 1.0f;

    private int dragFrom = -1;
    private int dragTo = -1;

    private YPYTouchHelperAdapter mAdapter;
    private boolean isAllowSwipe=true;
    private int swipeFragOfList= ItemTouchHelper.START | ItemTouchHelper.END;

    public YPYTouchHelperCallback(YPYTouchHelperAdapter adapter) {
        mAdapter = adapter;
    }

    public void setAdapter(YPYTouchHelperAdapter mAdapter) {
        this.mAdapter = mAdapter;
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return false;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return true;
    }


    public void setSwipeFragOfList(int swipeFragOfList) {
        this.swipeFragOfList = swipeFragOfList;
    }

    public void setAllowSwipe(boolean allowSwipe) {
        isAllowSwipe = allowSwipe;
    }

    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        // Set movement flags based on the layout manager
        RecyclerView.LayoutManager mLayoutMng = recyclerView.getLayoutManager();
        if(mLayoutMng!=null){
            if (mLayoutMng instanceof GridLayoutManager) {
                final int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
                final int swipeFlags = 0;
                return makeMovementFlags(dragFlags, swipeFlags);
            }
            else {
                final int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
                int swipeFlags=0;
                int fromPosition = viewHolder.getAdapterPosition();
                if(isAllowSwipe){
                    if(mAdapter.hasItemHeader() && fromPosition==0){
                        swipeFlags=0;
                    }
                    else{
                        swipeFlags = swipeFragOfList;
                    }
                }
                return makeMovementFlags(dragFlags, swipeFlags);
            }
        }
        return 0;

    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder source, @NonNull RecyclerView.ViewHolder target) {
        if(mAdapter!=null){
            if (source.getItemViewType() != target.getItemViewType()) {
                return false;
            }
            int fromPosition = source.getAdapterPosition();
            int toPosition = target.getAdapterPosition();

            dragFrom =  fromPosition;
            dragTo = toPosition;

            mAdapter.onItemMove(fromPosition, toPosition);
            return true;
        }
        return false;
    }



    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        if(mAdapter!=null){
            mAdapter.onItemDismiss(viewHolder.getAdapterPosition());
        }
    }

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            // Fade out the view as it is swiped out of the parent's bounds
            final float alpha = ALPHA_FULL - Math.abs(dX) / (float) viewHolder.itemView.getWidth();
            viewHolder.itemView.setAlpha(alpha);
            viewHolder.itemView.setTranslationX(dX);
        }
        else {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    }

    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        // We only want the active item to change
        if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
            if (viewHolder instanceof YPYTouchHelperViewHolder) {
                // Let the view holder know that this item is being moved or dragged
                YPYTouchHelperViewHolder itemViewHolder = (YPYTouchHelperViewHolder) viewHolder;
                itemViewHolder.onItemSelected();
            }
        }
        super.onSelectedChanged(viewHolder, actionState);
    }

    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);
        viewHolder.itemView.setAlpha(ALPHA_FULL);
        if (viewHolder instanceof YPYTouchHelperViewHolder) {
            YPYTouchHelperViewHolder itemViewHolder = (YPYTouchHelperViewHolder) viewHolder;
            itemViewHolder.onItemClear();

        }
        if(dragFrom != -1 && dragTo != -1 && dragFrom != dragTo) {
            if(mAdapter!=null){
                mAdapter.onItemMoved(dragFrom,dragTo);
            }
        }
        dragFrom = dragTo = -1;
    }
}
