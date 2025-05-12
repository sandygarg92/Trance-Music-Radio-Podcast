package com.onlineradiofm.trancemusicradio.databinding;
import com.onlineradiofm.trancemusicradio.R;
import com.onlineradiofm.trancemusicradio.BR;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.View;
@SuppressWarnings("unchecked")
public class ItemHeaderLibraryBindingImpl extends ItemHeaderLibraryBinding  {

    @Nullable
    private static final androidx.databinding.ViewDataBinding.IncludedLayouts sIncludes;
    @Nullable
    private static final android.util.SparseIntArray sViewsWithIds;
    static {
        sIncludes = null;
        sViewsWithIds = new android.util.SparseIntArray();
        sViewsWithIds.put(R.id.my_radio_card_view, 1);
        sViewsWithIds.put(R.id.layout_my_radio, 2);
        sViewsWithIds.put(R.id.layout_radio_img, 3);
        sViewsWithIds.put(R.id.tv_my_radio, 4);
        sViewsWithIds.put(R.id.img_radio_chevron, 5);
        sViewsWithIds.put(R.id.add_radio_card_view, 6);
        sViewsWithIds.put(R.id.layout_add_radio, 7);
        sViewsWithIds.put(R.id.layout_add_radio_img, 8);
        sViewsWithIds.put(R.id.tv_add_radio, 9);
        sViewsWithIds.put(R.id.img_add_radio_chevron, 10);
        sViewsWithIds.put(R.id.download_card_view, 11);
        sViewsWithIds.put(R.id.layout_download_podcast, 12);
        sViewsWithIds.put(R.id.layout_img, 13);
        sViewsWithIds.put(R.id.tv_downloaded_podcast, 14);
        sViewsWithIds.put(R.id.img_chevron, 15);
        sViewsWithIds.put(R.id.tv_record, 16);
    }
    // views
    @NonNull
    private final android.widget.LinearLayout mboundView0;
    // variables
    // values
    // listeners
    // Inverse Binding Event Handlers

    public ItemHeaderLibraryBindingImpl(@Nullable androidx.databinding.DataBindingComponent bindingComponent, @NonNull View root) {
        this(bindingComponent, root, mapBindings(bindingComponent, root, 17, sIncludes, sViewsWithIds));
    }
    private ItemHeaderLibraryBindingImpl(androidx.databinding.DataBindingComponent bindingComponent, View root, Object[] bindings) {
        super(bindingComponent, root, 0
            , (androidx.cardview.widget.CardView) bindings[6]
            , (androidx.cardview.widget.CardView) bindings[11]
            , (androidx.appcompat.widget.AppCompatTextView) bindings[10]
            , (androidx.appcompat.widget.AppCompatTextView) bindings[15]
            , (androidx.appcompat.widget.AppCompatTextView) bindings[5]
            , (android.widget.RelativeLayout) bindings[7]
            , (androidx.constraintlayout.widget.ConstraintLayout) bindings[8]
            , (android.widget.RelativeLayout) bindings[12]
            , (androidx.constraintlayout.widget.ConstraintLayout) bindings[13]
            , (android.widget.RelativeLayout) bindings[2]
            , (androidx.constraintlayout.widget.ConstraintLayout) bindings[3]
            , (androidx.cardview.widget.CardView) bindings[1]
            , (androidx.appcompat.widget.AppCompatTextView) bindings[9]
            , (androidx.appcompat.widget.AppCompatTextView) bindings[14]
            , (androidx.appcompat.widget.AppCompatTextView) bindings[4]
            , (androidx.appcompat.widget.AppCompatTextView) bindings[16]
            );
        this.mboundView0 = (android.widget.LinearLayout) bindings[0];
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