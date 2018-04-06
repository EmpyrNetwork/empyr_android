package com.empyr.tracker;

import android.content.Context;
import android.util.Log;

import java.lang.reflect.Method;

/**
 * Created by jbausewein on 4/5/18.
 */
public class AdHelper
{
    private static String advertisingIdClientClassName = "com.google.android.gms.ads.identifier.AdvertisingIdClient";
    private static String advertisingInfoClassName = advertisingIdClientClassName + "$Info";

    private static AdvertisingInfo cachedAdInfo = null;

    public static class AdvertisingInfo
    {
        public String advertisingId;
        public boolean limitAdTracking;

        public AdvertisingInfo( String adId, boolean limitAdTracking )
        {
            this.advertisingId = adId;
            this.limitAdTracking = limitAdTracking;
        }
    }

    public static AdvertisingInfo fetchAdvertisingId( final Context context )
    {
        if( cachedAdInfo != null )
        {
            return cachedAdInfo;
        }

        if( context == null )
        {
            return null;
        }

        String adId = null;
        boolean limitAdTracking = false;

        try
        {
            Class<?> adClientCls = Class.forName( advertisingIdClientClassName );
            Method m = adClientCls.getMethod( "getAdvertisingIdInfo", Context.class );
            Object adInfoObj = m.invoke( null, context );

            Class<?> adInfoCls = Class.forName( advertisingInfoClassName );
            adId = (String)adInfoCls.getMethod( "getId", null ).invoke( adInfoObj, null );
            limitAdTracking = (Boolean)adInfoCls.getMethod( "isLimitAdTrackingEnabled", null ).invoke( adInfoObj, null );
        }catch( Exception e )
        {
            Log.w( "EmpyrTracker","Unable to retrieve AAID. Make sure GooglePlayServices was included.", e );
            return null;
        }

        cachedAdInfo = new AdvertisingInfo( adId, limitAdTracking );

        return cachedAdInfo;
    }
}
