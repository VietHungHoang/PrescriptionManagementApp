//package com.mad.prescriptionmanagementapp.di;
//
//
//import android.app.AlarmManager;
//import android.content.Context;
//import javax.inject.Singleton;
//import dagger.Binds;
//import dagger.Module;
//import dagger.Provides;
//import dagger.hilt.InstallIn;
//import dagger.hilt.android.qualifiers.ApplicationContext;
//import dagger.hilt.components.SingletonComponent;
//
//@Module
//@InstallIn(SingletonComponent.class)
//public class SchedulerModule {
//
//    @Provides
//    @Singleton
//    public AlarmManager provideAlarmManager(@ApplicationContext Context context) {
//        return (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
//    }
//}
//
//// Module riêng để bind interface và implementation
//@Module
//@InstallIn(SingletonComponent.class)
//abstract class AlarmSchedulerBindingModule {
//
//    @Binds
//    @Singleton
//    abstract AlarmScheduler bindAlarmScheduler(AlarmSchedulerImpl impl);
//}
