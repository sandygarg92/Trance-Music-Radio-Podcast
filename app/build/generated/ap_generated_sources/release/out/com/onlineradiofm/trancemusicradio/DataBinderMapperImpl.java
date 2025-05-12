package com.onlineradiofm.trancemusicradio;

import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.View;
import androidx.databinding.DataBinderMapper;
import androidx.databinding.DataBindingComponent;
import androidx.databinding.ViewDataBinding;
import com.onlineradiofm.trancemusicradio.databinding.DialogSleepTimeBindingImpl;
import com.onlineradiofm.trancemusicradio.databinding.DialogStoragePermissionBindingImpl;
import com.onlineradiofm.trancemusicradio.databinding.DialogTermOfConditionBindingImpl;
import com.onlineradiofm.trancemusicradio.databinding.FragmentAddRadioBindingImpl;
import com.onlineradiofm.trancemusicradio.databinding.ItemDownloadProcessBindingImpl;
import com.onlineradiofm.trancemusicradio.databinding.ItemFormSearchBindingImpl;
import com.onlineradiofm.trancemusicradio.databinding.ItemFormSearchBindingLandImpl;
import com.onlineradiofm.trancemusicradio.databinding.ItemFormSearchPodcastBindingImpl;
import com.onlineradiofm.trancemusicradio.databinding.ItemFormSearchPodcastBindingLandImpl;
import com.onlineradiofm.trancemusicradio.databinding.ItemFormSearchStationBindingImpl;
import com.onlineradiofm.trancemusicradio.databinding.ItemFormSearchStationBindingLandImpl;
import com.onlineradiofm.trancemusicradio.databinding.ItemHeaderCloudFavoriteBindingImpl;
import com.onlineradiofm.trancemusicradio.databinding.ItemHeaderCloudFavoriteBindingLandImpl;
import com.onlineradiofm.trancemusicradio.databinding.ItemHeaderDetailPodcastBindingImpl;
import com.onlineradiofm.trancemusicradio.databinding.ItemHeaderFeatureBindingImpl;
import com.onlineradiofm.trancemusicradio.databinding.ItemHeaderFeatureBindingLandImpl;
import com.onlineradiofm.trancemusicradio.databinding.ItemHeaderGenreTitleBindingImpl;
import com.onlineradiofm.trancemusicradio.databinding.ItemHeaderGenreTitleBindingLandImpl;
import com.onlineradiofm.trancemusicradio.databinding.ItemHeaderLibraryBindingImpl;
import com.onlineradiofm.trancemusicradio.databinding.ItemHeaderLibraryBindingLandImpl;
import com.onlineradiofm.trancemusicradio.databinding.ItemHeaderRadioTitleBindingImpl;
import com.onlineradiofm.trancemusicradio.databinding.ItemHeaderRadioTitleBindingLandImpl;
import com.onlineradiofm.trancemusicradio.databinding.ItemHeaderSearchBindingImpl;
import com.onlineradiofm.trancemusicradio.databinding.ItemHeaderSearchBindingLandImpl;
import com.onlineradiofm.trancemusicradio.databinding.ItemPlayControlBindingImpl;
import com.onlineradiofm.trancemusicradio.databinding.ItemPlayInfoBindingImpl;
import com.onlineradiofm.trancemusicradio.databinding.ItemSeekbarPodcastBindingImpl;
import com.onlineradiofm.trancemusicradio.databinding.ItemSeekbarVolumeBindingImpl;
import com.onlineradiofm.trancemusicradio.databinding.ItemSettingBindingImpl;
import java.lang.IllegalArgumentException;
import java.lang.Integer;
import java.lang.Object;
import java.lang.Override;
import java.lang.RuntimeException;
import java.lang.String;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DataBinderMapperImpl extends DataBinderMapper {
  private static final int LAYOUT_DIALOGSLEEPTIME = 1;

  private static final int LAYOUT_DIALOGSTORAGEPERMISSION = 2;

  private static final int LAYOUT_DIALOGTERMOFCONDITION = 3;

  private static final int LAYOUT_FRAGMENTADDRADIO = 4;

  private static final int LAYOUT_ITEMDOWNLOADPROCESS = 5;

  private static final int LAYOUT_ITEMFORMSEARCH = 6;

  private static final int LAYOUT_ITEMFORMSEARCHPODCAST = 7;

  private static final int LAYOUT_ITEMFORMSEARCHSTATION = 8;

  private static final int LAYOUT_ITEMHEADERCLOUDFAVORITE = 9;

  private static final int LAYOUT_ITEMHEADERDETAILPODCAST = 10;

  private static final int LAYOUT_ITEMHEADERFEATURE = 11;

  private static final int LAYOUT_ITEMHEADERGENRETITLE = 12;

  private static final int LAYOUT_ITEMHEADERLIBRARY = 13;

  private static final int LAYOUT_ITEMHEADERRADIOTITLE = 14;

  private static final int LAYOUT_ITEMHEADERSEARCH = 15;

  private static final int LAYOUT_ITEMPLAYCONTROL = 16;

  private static final int LAYOUT_ITEMPLAYINFO = 17;

  private static final int LAYOUT_ITEMSEEKBARPODCAST = 18;

  private static final int LAYOUT_ITEMSEEKBARVOLUME = 19;

  private static final int LAYOUT_ITEMSETTING = 20;

  private static final SparseIntArray INTERNAL_LAYOUT_ID_LOOKUP = new SparseIntArray(20);

  static {
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.onlineradiofm.trancemusicradio.R.layout.dialog_sleep_time, LAYOUT_DIALOGSLEEPTIME);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.onlineradiofm.trancemusicradio.R.layout.dialog_storage_permission, LAYOUT_DIALOGSTORAGEPERMISSION);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.onlineradiofm.trancemusicradio.R.layout.dialog_term_of_condition, LAYOUT_DIALOGTERMOFCONDITION);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.onlineradiofm.trancemusicradio.R.layout.fragment_add_radio, LAYOUT_FRAGMENTADDRADIO);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.onlineradiofm.trancemusicradio.R.layout.item_download_process, LAYOUT_ITEMDOWNLOADPROCESS);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.onlineradiofm.trancemusicradio.R.layout.item_form_search, LAYOUT_ITEMFORMSEARCH);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.onlineradiofm.trancemusicradio.R.layout.item_form_search_podcast, LAYOUT_ITEMFORMSEARCHPODCAST);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.onlineradiofm.trancemusicradio.R.layout.item_form_search_station, LAYOUT_ITEMFORMSEARCHSTATION);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.onlineradiofm.trancemusicradio.R.layout.item_header_cloud_favorite, LAYOUT_ITEMHEADERCLOUDFAVORITE);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.onlineradiofm.trancemusicradio.R.layout.item_header_detail_podcast, LAYOUT_ITEMHEADERDETAILPODCAST);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.onlineradiofm.trancemusicradio.R.layout.item_header_feature, LAYOUT_ITEMHEADERFEATURE);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.onlineradiofm.trancemusicradio.R.layout.item_header_genre_title, LAYOUT_ITEMHEADERGENRETITLE);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.onlineradiofm.trancemusicradio.R.layout.item_header_library, LAYOUT_ITEMHEADERLIBRARY);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.onlineradiofm.trancemusicradio.R.layout.item_header_radio_title, LAYOUT_ITEMHEADERRADIOTITLE);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.onlineradiofm.trancemusicradio.R.layout.item_header_search, LAYOUT_ITEMHEADERSEARCH);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.onlineradiofm.trancemusicradio.R.layout.item_play_control, LAYOUT_ITEMPLAYCONTROL);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.onlineradiofm.trancemusicradio.R.layout.item_play_info, LAYOUT_ITEMPLAYINFO);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.onlineradiofm.trancemusicradio.R.layout.item_seekbar_podcast, LAYOUT_ITEMSEEKBARPODCAST);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.onlineradiofm.trancemusicradio.R.layout.item_seekbar_volume, LAYOUT_ITEMSEEKBARVOLUME);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.onlineradiofm.trancemusicradio.R.layout.item_setting, LAYOUT_ITEMSETTING);
  }

  @Override
  public ViewDataBinding getDataBinder(DataBindingComponent component, View view, int layoutId) {
    int localizedLayoutId = INTERNAL_LAYOUT_ID_LOOKUP.get(layoutId);
    if(localizedLayoutId > 0) {
      final Object tag = view.getTag();
      if(tag == null) {
        throw new RuntimeException("view must have a tag");
      }
      switch(localizedLayoutId) {
        case  LAYOUT_DIALOGSLEEPTIME: {
          if ("layout/dialog_sleep_time_0".equals(tag)) {
            return new DialogSleepTimeBindingImpl(component, view);
          }
          throw new IllegalArgumentException("The tag for dialog_sleep_time is invalid. Received: " + tag);
        }
        case  LAYOUT_DIALOGSTORAGEPERMISSION: {
          if ("layout/dialog_storage_permission_0".equals(tag)) {
            return new DialogStoragePermissionBindingImpl(component, view);
          }
          throw new IllegalArgumentException("The tag for dialog_storage_permission is invalid. Received: " + tag);
        }
        case  LAYOUT_DIALOGTERMOFCONDITION: {
          if ("layout/dialog_term_of_condition_0".equals(tag)) {
            return new DialogTermOfConditionBindingImpl(component, view);
          }
          throw new IllegalArgumentException("The tag for dialog_term_of_condition is invalid. Received: " + tag);
        }
        case  LAYOUT_FRAGMENTADDRADIO: {
          if ("layout/fragment_add_radio_0".equals(tag)) {
            return new FragmentAddRadioBindingImpl(component, view);
          }
          throw new IllegalArgumentException("The tag for fragment_add_radio is invalid. Received: " + tag);
        }
        case  LAYOUT_ITEMDOWNLOADPROCESS: {
          if ("layout/item_download_process_0".equals(tag)) {
            return new ItemDownloadProcessBindingImpl(component, view);
          }
          throw new IllegalArgumentException("The tag for item_download_process is invalid. Received: " + tag);
        }
        case  LAYOUT_ITEMFORMSEARCH: {
          if ("layout/item_form_search_0".equals(tag)) {
            return new ItemFormSearchBindingImpl(component, view);
          }
          if ("layout-land/item_form_search_0".equals(tag)) {
            return new ItemFormSearchBindingLandImpl(component, view);
          }
          throw new IllegalArgumentException("The tag for item_form_search is invalid. Received: " + tag);
        }
        case  LAYOUT_ITEMFORMSEARCHPODCAST: {
          if ("layout/item_form_search_podcast_0".equals(tag)) {
            return new ItemFormSearchPodcastBindingImpl(component, view);
          }
          if ("layout-land/item_form_search_podcast_0".equals(tag)) {
            return new ItemFormSearchPodcastBindingLandImpl(component, view);
          }
          throw new IllegalArgumentException("The tag for item_form_search_podcast is invalid. Received: " + tag);
        }
        case  LAYOUT_ITEMFORMSEARCHSTATION: {
          if ("layout-land/item_form_search_station_0".equals(tag)) {
            return new ItemFormSearchStationBindingLandImpl(component, view);
          }
          if ("layout/item_form_search_station_0".equals(tag)) {
            return new ItemFormSearchStationBindingImpl(component, view);
          }
          throw new IllegalArgumentException("The tag for item_form_search_station is invalid. Received: " + tag);
        }
        case  LAYOUT_ITEMHEADERCLOUDFAVORITE: {
          if ("layout/item_header_cloud_favorite_0".equals(tag)) {
            return new ItemHeaderCloudFavoriteBindingImpl(component, view);
          }
          if ("layout-land/item_header_cloud_favorite_0".equals(tag)) {
            return new ItemHeaderCloudFavoriteBindingLandImpl(component, view);
          }
          throw new IllegalArgumentException("The tag for item_header_cloud_favorite is invalid. Received: " + tag);
        }
        case  LAYOUT_ITEMHEADERDETAILPODCAST: {
          if ("layout/item_header_detail_podcast_0".equals(tag)) {
            return new ItemHeaderDetailPodcastBindingImpl(component, view);
          }
          throw new IllegalArgumentException("The tag for item_header_detail_podcast is invalid. Received: " + tag);
        }
        case  LAYOUT_ITEMHEADERFEATURE: {
          if ("layout/item_header_feature_0".equals(tag)) {
            return new ItemHeaderFeatureBindingImpl(component, view);
          }
          if ("layout-land/item_header_feature_0".equals(tag)) {
            return new ItemHeaderFeatureBindingLandImpl(component, view);
          }
          throw new IllegalArgumentException("The tag for item_header_feature is invalid. Received: " + tag);
        }
        case  LAYOUT_ITEMHEADERGENRETITLE: {
          if ("layout-land/item_header_genre_title_0".equals(tag)) {
            return new ItemHeaderGenreTitleBindingLandImpl(component, view);
          }
          if ("layout/item_header_genre_title_0".equals(tag)) {
            return new ItemHeaderGenreTitleBindingImpl(component, view);
          }
          throw new IllegalArgumentException("The tag for item_header_genre_title is invalid. Received: " + tag);
        }
        case  LAYOUT_ITEMHEADERLIBRARY: {
          if ("layout/item_header_library_0".equals(tag)) {
            return new ItemHeaderLibraryBindingImpl(component, view);
          }
          if ("layout-land/item_header_library_0".equals(tag)) {
            return new ItemHeaderLibraryBindingLandImpl(component, view);
          }
          throw new IllegalArgumentException("The tag for item_header_library is invalid. Received: " + tag);
        }
        case  LAYOUT_ITEMHEADERRADIOTITLE: {
          if ("layout-land/item_header_radio_title_0".equals(tag)) {
            return new ItemHeaderRadioTitleBindingLandImpl(component, view);
          }
          if ("layout/item_header_radio_title_0".equals(tag)) {
            return new ItemHeaderRadioTitleBindingImpl(component, view);
          }
          throw new IllegalArgumentException("The tag for item_header_radio_title is invalid. Received: " + tag);
        }
        case  LAYOUT_ITEMHEADERSEARCH: {
          if ("layout/item_header_search_0".equals(tag)) {
            return new ItemHeaderSearchBindingImpl(component, view);
          }
          if ("layout-land/item_header_search_0".equals(tag)) {
            return new ItemHeaderSearchBindingLandImpl(component, view);
          }
          throw new IllegalArgumentException("The tag for item_header_search is invalid. Received: " + tag);
        }
        case  LAYOUT_ITEMPLAYCONTROL: {
          if ("layout/item_play_control_0".equals(tag)) {
            return new ItemPlayControlBindingImpl(component, view);
          }
          throw new IllegalArgumentException("The tag for item_play_control is invalid. Received: " + tag);
        }
        case  LAYOUT_ITEMPLAYINFO: {
          if ("layout/item_play_info_0".equals(tag)) {
            return new ItemPlayInfoBindingImpl(component, view);
          }
          throw new IllegalArgumentException("The tag for item_play_info is invalid. Received: " + tag);
        }
        case  LAYOUT_ITEMSEEKBARPODCAST: {
          if ("layout/item_seekbar_podcast_0".equals(tag)) {
            return new ItemSeekbarPodcastBindingImpl(component, view);
          }
          throw new IllegalArgumentException("The tag for item_seekbar_podcast is invalid. Received: " + tag);
        }
        case  LAYOUT_ITEMSEEKBARVOLUME: {
          if ("layout/item_seekbar_volume_0".equals(tag)) {
            return new ItemSeekbarVolumeBindingImpl(component, view);
          }
          throw new IllegalArgumentException("The tag for item_seekbar_volume is invalid. Received: " + tag);
        }
        case  LAYOUT_ITEMSETTING: {
          if ("layout/item_setting_0".equals(tag)) {
            return new ItemSettingBindingImpl(component, view);
          }
          throw new IllegalArgumentException("The tag for item_setting is invalid. Received: " + tag);
        }
      }
    }
    return null;
  }

  @Override
  public ViewDataBinding getDataBinder(DataBindingComponent component, View[] views, int layoutId) {
    if(views == null || views.length == 0) {
      return null;
    }
    int localizedLayoutId = INTERNAL_LAYOUT_ID_LOOKUP.get(layoutId);
    if(localizedLayoutId > 0) {
      final Object tag = views[0].getTag();
      if(tag == null) {
        throw new RuntimeException("view must have a tag");
      }
      switch(localizedLayoutId) {
      }
    }
    return null;
  }

  @Override
  public int getLayoutId(String tag) {
    if (tag == null) {
      return 0;
    }
    Integer tmpVal = InnerLayoutIdLookup.sKeys.get(tag);
    return tmpVal == null ? 0 : tmpVal;
  }

  @Override
  public String convertBrIdToString(int localId) {
    String tmpVal = InnerBrLookup.sKeys.get(localId);
    return tmpVal;
  }

  @Override
  public List<DataBinderMapper> collectDependencies() {
    ArrayList<DataBinderMapper> result = new ArrayList<DataBinderMapper>(1);
    result.add(new androidx.databinding.library.baseAdapters.DataBinderMapperImpl());
    return result;
  }

  private static class InnerBrLookup {
    static final SparseArray<String> sKeys = new SparseArray<String>(1);

    static {
      sKeys.put(0, "_all");
    }
  }

  private static class InnerLayoutIdLookup {
    static final HashMap<String, Integer> sKeys = new HashMap<String, Integer>(29);

    static {
      sKeys.put("layout/dialog_sleep_time_0", com.onlineradiofm.trancemusicradio.R.layout.dialog_sleep_time);
      sKeys.put("layout/dialog_storage_permission_0", com.onlineradiofm.trancemusicradio.R.layout.dialog_storage_permission);
      sKeys.put("layout/dialog_term_of_condition_0", com.onlineradiofm.trancemusicradio.R.layout.dialog_term_of_condition);
      sKeys.put("layout/fragment_add_radio_0", com.onlineradiofm.trancemusicradio.R.layout.fragment_add_radio);
      sKeys.put("layout/item_download_process_0", com.onlineradiofm.trancemusicradio.R.layout.item_download_process);
      sKeys.put("layout/item_form_search_0", com.onlineradiofm.trancemusicradio.R.layout.item_form_search);
      sKeys.put("layout-land/item_form_search_0", com.onlineradiofm.trancemusicradio.R.layout.item_form_search);
      sKeys.put("layout/item_form_search_podcast_0", com.onlineradiofm.trancemusicradio.R.layout.item_form_search_podcast);
      sKeys.put("layout-land/item_form_search_podcast_0", com.onlineradiofm.trancemusicradio.R.layout.item_form_search_podcast);
      sKeys.put("layout-land/item_form_search_station_0", com.onlineradiofm.trancemusicradio.R.layout.item_form_search_station);
      sKeys.put("layout/item_form_search_station_0", com.onlineradiofm.trancemusicradio.R.layout.item_form_search_station);
      sKeys.put("layout/item_header_cloud_favorite_0", com.onlineradiofm.trancemusicradio.R.layout.item_header_cloud_favorite);
      sKeys.put("layout-land/item_header_cloud_favorite_0", com.onlineradiofm.trancemusicradio.R.layout.item_header_cloud_favorite);
      sKeys.put("layout/item_header_detail_podcast_0", com.onlineradiofm.trancemusicradio.R.layout.item_header_detail_podcast);
      sKeys.put("layout/item_header_feature_0", com.onlineradiofm.trancemusicradio.R.layout.item_header_feature);
      sKeys.put("layout-land/item_header_feature_0", com.onlineradiofm.trancemusicradio.R.layout.item_header_feature);
      sKeys.put("layout-land/item_header_genre_title_0", com.onlineradiofm.trancemusicradio.R.layout.item_header_genre_title);
      sKeys.put("layout/item_header_genre_title_0", com.onlineradiofm.trancemusicradio.R.layout.item_header_genre_title);
      sKeys.put("layout/item_header_library_0", com.onlineradiofm.trancemusicradio.R.layout.item_header_library);
      sKeys.put("layout-land/item_header_library_0", com.onlineradiofm.trancemusicradio.R.layout.item_header_library);
      sKeys.put("layout-land/item_header_radio_title_0", com.onlineradiofm.trancemusicradio.R.layout.item_header_radio_title);
      sKeys.put("layout/item_header_radio_title_0", com.onlineradiofm.trancemusicradio.R.layout.item_header_radio_title);
      sKeys.put("layout/item_header_search_0", com.onlineradiofm.trancemusicradio.R.layout.item_header_search);
      sKeys.put("layout-land/item_header_search_0", com.onlineradiofm.trancemusicradio.R.layout.item_header_search);
      sKeys.put("layout/item_play_control_0", com.onlineradiofm.trancemusicradio.R.layout.item_play_control);
      sKeys.put("layout/item_play_info_0", com.onlineradiofm.trancemusicradio.R.layout.item_play_info);
      sKeys.put("layout/item_seekbar_podcast_0", com.onlineradiofm.trancemusicradio.R.layout.item_seekbar_podcast);
      sKeys.put("layout/item_seekbar_volume_0", com.onlineradiofm.trancemusicradio.R.layout.item_seekbar_volume);
      sKeys.put("layout/item_setting_0", com.onlineradiofm.trancemusicradio.R.layout.item_setting);
    }
  }
}
