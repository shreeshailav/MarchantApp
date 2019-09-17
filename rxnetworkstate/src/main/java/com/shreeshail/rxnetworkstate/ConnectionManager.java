package com.shreeshail.rxnetworkstate;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * Created by Shreeshail on 29/08/2019.
 */

public class ConnectionManager {

    Context context;
    public boolean hasNetwork;
    ConnectionTracer connectionTracer;
    public Subscription subscription;

    public ConnectionManager(Context context, ConnectionTracer connectionTracer) {
        this.context = context;
        this.connectionTracer = connectionTracer;
        initRxNetwork();
    }


    public static class Builder {
        private Context context;
        private ConnectionTracer connectionTracer;


        public Builder setContext(Context context) {
            this.context = context;
            return this;
        }

        public Builder setStatusView(ConnectionTracer connectionTracer) {
            this.connectionTracer = connectionTracer;
            return this;
        }

        public ConnectionManager build() {
            return new ConnectionManager(context, connectionTracer);
        }
    }

    public void initRxNetwork() {
        subscription = RxNetwork.stream(context)
                .map(new Func1<Boolean, Boolean>() {
                    @Override
                    public Boolean call(Boolean hasInternet) {
                        hasNetwork = hasInternet;
                        if (!hasInternet) {
                            return hasInternet;
                        }
                        return true;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Boolean>() {
                               @Override
                               public void call(Boolean isOnline) {
                                   if (!isOnline)
                                       connectionTracer.connectionState(0);
                                   else
                                       connectionTracer.connectionState(1);
                               }
                           }
                );



    }

    public void unsubcribeme(){
         if(subscription!=null && !subscription.isUnsubscribed())
            subscription.unsubscribe();


    }


}
