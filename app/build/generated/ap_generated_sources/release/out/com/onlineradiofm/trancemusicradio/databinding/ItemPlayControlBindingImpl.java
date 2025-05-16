package com.onlineradiofm.trancemusicradio.databinding;
import com.onlineradiofm.trancemusicradio.R;
import com.onlineradiofm.trancemusicradio.BR;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.View;
@SuppressWarnings("unchecked")
public class ItemPlayControlBindingImpl extends ItemPlayControlBinding  {

    @Nullable
    private static final androidx.databinding.ViewDataBinding.IncludedLayouts sIncludes;
    @Nullable
    private static final android.util.SparseIntArray sViewsWithIds;
    static {
        sIncludes = null;
        sViewsWithIds = new android.util.SparseIntArray();
        sViewsWithIds.put(R.id.layout_control, 1);
        sViewsWithIds.put(R.id.layout_download, 2);
        sViewsWithIds.put(R.id.btn_download, 3);
        sViewsWithIds.put(R.id.layout_record, 4);
        sViewsWithIds.put(R.id.btn_record, 5);
        sViewsWithIds.put(R.id.layout_replay, 6);
        sViewsWithIds.put(R.id.btn_replay, 7);
        sViewsWithIds.put(R.id.layout_prev, 8);
        sViewsWithIds.put(R.id.btn_prev, 9);
        sViewsWithIds.put(R.id.fb_play, 10);
        sViewsWithIds.put(R.id.layout_next, 11);
        sViewsWithIds.put(R.id.btn_next, 12);
        sViewsWithIds.put(R.id.layout_forward, 13);
        sViewsWithIds.put(R.id.btn_forward, 14);
        sViewsWithIds.put(R.id.btn_favorite, 15);
        sViewsWithIds.put(R.id.play_progressBar, 16);
    }
    // views
    @NonNull
    private final android.widget.RelativeLayout mboundView0;
    // variables
    // values
    // listeners
    // Inverse Binding Event Handlers

    public ItemPlayControlBindingImpl(@Nullable androidx.databinding.DataBindingComponent bindingComponent, @NonNull View root) {
        this(bindingComponent, root, mapBindings(bindingComponent, root, 17, sIncludes, sViewsWithIds));
    }
    private ItemPlayControlBindingImpl(androidx.databinding.DataBindingComponent bindingComponent, View root, Object[] bindings) {
        super(bindingComponent, root, 0
            , (androidx.appcompat.widget.AppCompatImageView) bindings[3]
            , (com.like.LikeButton) bindings[15]
            , (androidx.appcompat.widget.AppCompatImageView) bindings[14]
            , (androidx.appcompat.widget.AppCompatImageView) bindings[12]
            , (androidx.appcompat.widget.AppCompatImageView) bindings[9]
            , (androidx.appcompat.widget.AppCompatImageView) bindings[5]
            , (androidx.appcompat.widget.AppCompatImageView) bindings[7]
            , (com.google.android.material.floatingactionbutton.FloatingActionButton) bindings[10]
            , (android.widget.RelativeLayout) bindings[1]
            , (com.balysv.materialripple.MaterialRippleLayout) bindings[2]
            , (com.balysv.materialripple.MaterialRippleLayout) bindings[13]
            , (com.balysv.materialripple.MaterialRippleLayout) bindings[11]
            , (com.balysv.materialripple.MaterialRippleLayout) bindings[8]
            , (com.balysv.materialripple.MaterialRippleLayout) bindings[4]
            , (com.balysv.materialripple.MaterialRippleLayout) bindings[6]
            , (com.wang.avi.AVLoadingIndicatorView) bindings[16]
            );
        this.mboundView0 = (android.widget.RelativeLayout) bindings[0];
        this.mboundView0.setTag(null);
        setRootTag(root);
        // listeners
        invalidateAll();
    }

    @Override
    public void invalidateAll() {
        synchronized(this) {
                mDirtyFlags = 0x1L;
        }
        requestRebind();
    }

    @Override
    public boolean hasPendingBindings() {
        synchronized(this) {
            if (mDirtyFlags != 0) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean setVariable(int variableId, @Nullable Object variable)  {
        boolean variableSet = true;
            return variableSet;
    }

    @Override
    protected boolean onFieldChange(int localFieldId, Object object, int fieldId) {
        switch (localFieldId) {
        }
        return false;
    }

    @Override
    protected void executeBindings() {
        long dirtyFlags = 0;
        synchronized(this) {
            dirtyFlags = mDirtyFlags;
            mDirtyFlags = 0;
        }
        // batch finished
    }
    // Listener Stub Implementations
    // callback impls
    // dirty flag
    private  long mDirtyFlags = 0xffffffffffffffffL;
    /* flag mapping
        flag 0 (0x1L): null
    flag mapping end*/
    //end
}