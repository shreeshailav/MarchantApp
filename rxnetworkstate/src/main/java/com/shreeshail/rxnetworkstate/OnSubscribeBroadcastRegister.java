package com.shreeshail.rxnetworkstate;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Action0;
import rx.subscriptions.Subscriptions;

import static android.content.ContentValues.TAG;

/**
 * Created by Shreeshail on 29/08/2019.
 */

public class OnSubscribeBroadcastRegister implements Observable.OnSubscribe<Intent> {
    private final Context context;
    private final IntentFilter intentFilter;
    private final String broadcastPermission;
    private final Handler schedulerHandler;

    public BroadcastReceiver mainbroadcastReceiver = null;


    public OnSubscribeBroadcastRegister(Context context, IntentFilter intentFilter, String broadcastPermission, Handler schedulerHandler) {
        this.context = context;
        this.intentFilter = intentFilter;
        this.broadcastPermission = broadcastPermission;
        this.schedulerHandler = schedulerHandler;
    }
    @Override
    public void call(final Subscriber<? super Intent> subscriber) {
        final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                subscriber.onNext(intent);
            }
        };

        final Subscription subscription = Subscriptions.create(new Action0() {
            @Override
            public void call() {
                context.unregisterReceiver(broadcastReceiver);
            }
        });


        subscriber.add(subscription);
        subscriber.onError(new IOException("Network Issue"));
        context.registerReceiver(broadcastReceiver,intentFilter,broadcastPermission,schedulerHandler);

    }

}
