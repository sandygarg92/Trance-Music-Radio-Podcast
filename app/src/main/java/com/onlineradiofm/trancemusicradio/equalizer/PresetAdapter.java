
package com.onlineradiofm.trancemusicradio.equalizer;

import android.content.Context;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.onlineradiofm.trancemusicradio.R;
import com.onlineradiofm.trancemusicradio.setting.XRadioSettingManager;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

public class PresetAdapter extends ArrayAdapter<String>{

	private final LayoutInflater mInflater;
	private final String[] mListString;
	private IPresetListener presetListener;

	private final int mainColor;
	private final int bgColor;

	public PresetAdapter(Context context, int resource, String[] objects) {
		super(context, resource, objects);
		this.mListString = objects;
		boolean isDark = XRadioSettingManager.isDarkMode(context);
		this.mainColor = ContextCompat.getColor(context, isDark ? R.color.dark_text_main_color: R.color.light_text_main_color);
		this.bgColor = ContextCompat.getColor(context, isDark ? R.color.dark_list_bg_color: R.color.light_list_bg_color);
		this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@NonNull
	@Override
	public View getView(int position, View convertView, @NonNull ViewGroup parent) {
		final ViewHolder mHolder;
		if (convertView == null) {
			mHolder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.equalizer_item_preset_name, null);
			convertView.setTag(mHolder);
			mHolder.mTvName =convertView.findViewById(R.id.tv_name);
			mHolder.mTvName.setTextColor(mainColor);
		}
		else {
			mHolder = (ViewHolder) convertView.getTag();
		}
		mHolder.mTvName.setText(mListString[position]);
		return convertView;
	}


	@Override
	public View getDropDownView(final int position, View convertView, @NonNull ViewGroup parent) {
		ViewDropHolder mViewDropHolder;
		if(convertView==null){
			mViewDropHolder = new ViewDropHolder();
			convertView = mInflater.inflate(R.layout.equalizer_item_drop_down, null);
			convertView.setTag(mViewDropHolder);
			mViewDropHolder.mTvName = convertView.findViewById(R.id.tv_drop_down_name);
			mViewDropHolder.mTvName.setTextColor(mainColor);
			convertView.findViewById(R.id.layout_root).setBackgroundColor(bgColor);
		}
		else{
			mViewDropHolder= (ViewDropHolder) convertView.getTag();
		}
		mViewDropHolder.mTvName.setText(mListString[position]);
		mViewDropHolder.mTvName.setOnClickListener(v -> {
			View root = v.getRootView();
			root.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACK));
			root.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_BACK));
			if(presetListener!=null){
				presetListener.onSelectItem(position);
			}
		});
		return convertView;
	}

	private static class ViewDropHolder {
		public TextView mTvName;
	}
	

	private static class ViewHolder {
		public TextView mTvName;
	}

	public interface IPresetListener{
		void onSelectItem(int position);
	}

	public void setPresetListener(IPresetListener presetListener) {
		this.presetListener = presetListener;
	}
	
	
}
