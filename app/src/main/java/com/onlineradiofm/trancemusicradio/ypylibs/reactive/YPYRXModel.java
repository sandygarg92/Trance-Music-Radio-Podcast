package com.onlineradiofm.trancemusicradio.ypylibs.reactive;


import com.onlineradiofm.trancemusicradio.ypylibs.task.IYPYCallback;

import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;

/**
 * @author:Radio Polska
 * @Skype: 
 * @Mobile : 
 * @Email: 
 * @Website: http://radiopolska.com
 * Created by radiopolska on 7/11/18.
 */
public class YPYRXModel {

    private final Scheduler observeOn;
    private final Scheduler subscribeOn;

    private CompositeDisposable mCompositeDisposable;

    public YPYRXModel(Scheduler subscribeOn, Scheduler observeOn) {
        this.subscribeOn=subscribeOn;
        this.observeOn=observeOn;
        checkCompositeDisposable();
    }

    public void onDestroy(){
        try {
            if(mCompositeDisposable!=null){
                mCompositeDisposable.clear();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void addDisposable(Disposable mDispo){
        try{
            if(mDispo!=null) {
                checkCompositeDisposable();
                this.mCompositeDisposable.add(mDispo);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public <T extends Object> Disposable addObservableToObserver(Observable<T> mObservable, DisposableObserver<T> mObserver){
        try{
            if(mObservable!=null) {
                checkCompositeDisposable();
                Disposable mDispo= mObservable.subscribeOn(subscribeOn)
                        .observeOn(observeOn)
                        .subscribeWith(mObserver);
                this.mCompositeDisposable.add(mDispo);
                return mDispo;
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    private void checkCompositeDisposable(){
        if(mCompositeDisposable==null){
            this.mCompositeDisposable =new CompositeDisposable();
        }
    }

    public <T extends Object> Disposable makeDisposableFromCallBack(IYPYMakeObservable<T> mCallback, DisposableObserver<T> mObserver){
        try{
            if(mCallback!=null) {
                checkCompositeDisposable();
                Observable<T> mObservable = makeObservableFromCallBack(mCallback);
                if(mObservable!=null){
                    Disposable mDispo= mObservable.subscribeOn(subscribeOn).observeOn(observeOn)
                            .subscribeWith(mObserver);
                    this.mCompositeDisposable.add(mDispo);
                    return mDispo;
                }
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public <T extends Object> Observable<T> makeObservableFromCallBack(IYPYMakeObservable<T> mCallback){
        try{
            if(mCallback!=null) {
                return Observable.create(emitter -> {
                    try{
                        T mListModels = mCallback.getModels();
                        if(mListModels!=null){
                            emitter.onNext(mListModels);
                            emitter.onComplete();
                        }
                        else{
                            emitter.onError(new Exception("No Result Found"));
                        }
                    }
                    catch (Exception e){
                        e.printStackTrace();
                        emitter.onError(e);
                    }

                });
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public Disposable makeDisposableFromCallBack(IYPYCallback mCallback, DisposableObserver<IYPYCallback> mObserver){
        try{
            if(mCallback!=null) {
                checkCompositeDisposable();
                Observable<IYPYCallback> mObservable = Observable.create(emitter -> {
                    try{
                        mCallback.onAction();
                        emitter.onNext(mCallback);
                        emitter.onComplete();
                    }
                    catch (Exception e){
                        e.printStackTrace();
                        emitter.onError(e);
                    }
                });
                Disposable mDispo= mObservable.subscribeOn(subscribeOn).observeOn(observeOn)
                        .subscribeWith(mObserver);
                this.mCompositeDisposable.add(mDispo);
                return mDispo;
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public Disposable makeDisposableFromListCallBack(DisposableObserver<IYPYCallback> mObserver, IYPYCallback... mCallbacks){
        try{
            if(mCallbacks!=null) {
                checkCompositeDisposable();
                Observable<IYPYCallback> mObservable = Observable.fromArray(mCallbacks);
                Disposable mDispo= mObservable.subscribeOn(subscribeOn).doOnNext(iypyCallback -> {
                    if(iypyCallback!=null){
                        iypyCallback.onAction();
                    }
                }).observeOn(observeOn)
                        .subscribeWith(mObserver);
                this.mCompositeDisposable.add(mDispo);
                return mDispo;
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }





}
