package com.onlineradiofm.trancemusicradio.adapter;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.onlineradiofm.trancemusicradio.R;
import com.onlineradiofm.trancemusicradio.constants.IRadioConstants;
import com.onlineradiofm.trancemusicradio.model.PremiumModel;
import com.onlineradiofm.trancemusicradio.setting.XRadioSettingManager;
import com.onlineradiofm.trancemusicradio.ypylibs.adapter.YPYRecyclerViewAdapter;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;


public class PremiumAdapter extends YPYRecyclerViewAdapter<PremiumModel> implements IRadioConstants {

    private final int mPurchasedColor;
    private final int mBuyingColor;
    private final int mBuyingSecondColor;
    private final int mPurchasedSecondColor;

    public PremiumAdapter(Context mContext, ArrayList<PremiumModel> listObjects) {
        super(mContext, listObjects);

        this.mPurchasedColor = mContext.getResources().getColor(R.color.text_purchased_color);
        this.mPurchasedSecondColor = mContext.getResources().getColor(R.color.text_purchased_second_color);

        this.mBuyingColor = mContext.getResources().getColor(R.color.text_buy_color);
        this.mBuyingSecondColor = mContext.getResources().getColor(R.color.text_buy_second_color);
    }


    @Override
    public void onBindNormalViewHolder(RecyclerView.ViewHolder holder, int position) {
        final PremiumModel premiumModel = mListModels.get(position);
        final PremiumHolder premiumHolder = (PremiumHolder) holder;

        int idMember = XRadioSettingManager.getIdMember(mContext);
        if (idMember == premiumModel.getId() && idMember > 0) {
            premiumHolder.mTvMember.setText(String.format(mContext.getString(R.string.format_info_member), premiumModel.getName()));
            premiumHolder.mTvInfo1.setText(String.format(mContext.getString(R.string.format_info_member), premiumModel.getDuration()));
        }
        else {
            premiumHolder.mTvMember.setText(premiumModel.getName());
            premiumHolder.mTvInfo1.setText(String.format(mContext.getString(R.string.format_buy_pro1), premiumModel.getDuration()));
        }
        premiumHolder.mImgMember.setImageResource(premiumModel.getResId());
        premiumHolder.mTvPrice.setText(premiumModel.getPrice());
        premiumHolder.mTvBuy.setText(premiumModel.getLabelBtnBuy());

        int status = premiumModel.getStatusBtn();
        if (status == PremiumModel.STATUS_BTN_PURCHASED) {
            premiumHolder.mLayoutRoot.setAlpha(1f);
            premiumHolder.mIconBuy.setVisibility(View.VISIBLE);
            if (!isDarkMode) {
                premiumHolder.setTextColor(mPurchasedColor, mPurchasedSecondColor);
            }
            premiumHolder.mBtnBuy.setBackgroundResource(R.drawable.bg_purchased);
            premiumHolder.mTvPrice.setVisibility(View.GONE);
        }
        else if (status == PremiumModel.STATUS_BTN_SKIP) {
            premiumHolder.mLayoutRoot.setAlpha(0.6f);
            premiumHolder.mIconBuy.setVisibility(View.GONE);
            if (!isDarkMode) {
                premiumHolder.setTextColor(mPurchasedColor, mPurchasedSecondColor);
            }
            premiumHolder.mBtnBuy.setBackgroundResource(R.drawable.bg_skip);
            premiumHolder.mTvPrice.setVisibility(View.GONE);
        }
        else if (status == PremiumModel.STATUS_BTN_BUY) {
            premiumHolder.mLayoutRoot.setAlpha(1f);
            premiumHolder.mIconBuy.setVisibility(View.GONE);
            if (!isDarkMode) {
                premiumHolder.setTextColor(mBuyingColor, mBuyingSecondColor);
            }
            premiumHolder.mBtnBuy.setBackgroundResource(isDarkMode ? R.drawable.bg_buy_dark : R.drawable.bg_buy);
            premiumHolder.mTvPrice.setVisibility(View.VISIBLE);
        }
        premiumHolder.mTvInfo1.setText(premiumModel.getInfo1());
        premiumHolder.mBtnBuy.setOnClickListener(view -> {
            if (listener != null) {
                listener.onViewDetail(premiumModel);
            }
        });
    }


    @Override
    public RecyclerView.ViewHolder onCreateNormalViewHolder(ViewGroup v, int viewType) {
        View mView = mInflater.inflate(R.layout.item_premium, v, false);
        return new PremiumHolder(mView);
    }

    @Override
    public void updateDarkMode(@NonNull RecyclerView.ViewHolder holder) {
        super.updateDarkMode(holder);
        PremiumHolder mHolder = (PremiumHolder) holder;
        mHolder.mCardView.setCardBackgroundColor(mDarkBgCardColor);
        mHolder.mLayoutRoot.setBackgroundColor(mDarkBgCardColor);

        mHolder.mTvMember.setTextColor(mDarkTextMainColor);
        mHolder.mTvInfo1.setTextColor(mDarkTextSecondColor);
        mHolder.mTvInfo2.setTextColor(mDarkTextSecondColor);

        mHolder.mTvBuy.setTextColor(mDarkTextMainColor);
        mHolder.mIconBuy.setTextColor(mDarkTextMainColor);

        mHolder.mTvPrice.setTextColor(mContext.getResources().getColor(R.color.dark_color_accent));
    }

    public class PremiumHolder extends ViewNormalHolder {

        public ImageView mImgMember;
        public TextView mTvMember;
        public TextView mTvPrice;
        public TextView mTvInfo1;
        public TextView mTvInfo2;
        public RelativeLayout mBtnBuy;
        public TextView mTvBuy;
        public AppCompatTextView mIconBuy;
        public View mLayoutRoot;
        public CardView mCardView;


        PremiumHolder(View convertView) {
            super(convertView);
        }

        @Override
        public void onFindView(View convertView) {
            mImgMember = convertView.findViewById(R.id.img_member);
            mTvMember = convertView.findViewById(R.id.tv_member);
            mTvPrice = convertView.findViewById(R.id.tv_price);
            mTvInfo1 = convertView.findViewById(R.id.tv_info1);
            mTvInfo2 = convertView.findViewById(R.id.tv_info2);
            mBtnBuy = convertView.findViewById(R.id.btn_buy);
            mTvBuy = convertView.findViewById(R.id.tv_buy);
            mIconBuy = convertView.findViewById(R.id.ic_buy);
            mLayoutRoot = convertView.findViewById(R.id.layout_root);
            mCardView = convertView.findViewById(R.id.card_view);
        }

        void setTextColor(int color, int secondColor) {
            mTvMember.setTextColor(color);
            mTvInfo1.setTextColor(secondColor);
            mTvInfo2.setTextColor(secondColor);
        }

        @Override
        public void onUpdateUIWhenSupportRTL() {
            mTvMember.setGravity(Gravity.END);
            mTvInfo1.setGravity(Gravity.END);
            mTvInfo2.setGravity(Gravity.END);
        }
    }


}
