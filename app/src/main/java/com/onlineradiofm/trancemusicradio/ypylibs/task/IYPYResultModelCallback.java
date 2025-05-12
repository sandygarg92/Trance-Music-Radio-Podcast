package com.onlineradiofm.trancemusicradio.ypylibs.task;


import com.onlineradiofm.trancemusicradio.ypylibs.model.ResultModel;

/**
 * @author:Radio Polska
 * @Skype: 
 * @Mobile : 
 * @Email: 
 * @Website: http://radiopolska.com
 * Created by radiopolska on 12/13/18.
 */

public interface IYPYResultModelCallback<T>  {
    void onAction(ResultModel<T> resultModel);
}
