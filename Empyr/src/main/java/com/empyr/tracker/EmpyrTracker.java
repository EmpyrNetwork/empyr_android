package com.empyr.tracker;

import android.content.Context;

import com.empyr.api.EmpyrClient;
import com.empyr.api.HttpRequest;
import com.empyr.api.util.CommonsHttpRequestUtil;
import com.empyr.api.util.HttpRequestUtil;
import com.empyr.api.util.MethodType;

import org.apache.commons.lang.StringUtils;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Main entry point for performing impression tracking on Android for
 * offers on the Empyr Network.
 *
 * Created by jbausewein on 4/2/18.
 */
public class EmpyrTracker
{
    private static final String TRACKER_BASE_URL = "https://t.mogl.com/t/m.png";
    private static final int FLUSH_INTERVAL = 30;

    public static enum Tracker {
        PROFILE_VIEW,
        SEARCH_VIEW
    }

    private volatile static EmpyrTracker mainInstance = null;

    private EmpyrClient api;
    private HttpRequestUtil requestUtil = new CommonsHttpRequestUtil();
    private Map<Tracker,Set<Integer>> trackerEvents = new HashMap<>();
    private ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private WeakReference<Context> context;

    private EmpyrTracker(EmpyrClient client, Context context )
    {
        this.api = client;
        this.context = new WeakReference<Context>( context );

        Runnable task = new Runnable()
        {
            public void run()
            {
                flush();
            }
        };

        scheduler.scheduleAtFixedRate( task, FLUSH_INTERVAL, FLUSH_INTERVAL, TimeUnit.SECONDS.SECONDS );
    }

    public static EmpyrTracker getInstance( EmpyrClient api, Context context )
    {
        if( mainInstance == null )
        {
            mainInstance = new EmpyrTracker( api, context );
        }

        return mainInstance;
    }

    public static EmpyrTracker getInstance()
    {
        if( mainInstance == null )
        {
            throw new RuntimeException( "You must initialize the EmpyrTracker with an API first." );
        }

        return mainInstance;
    }

    public void track( Integer offerId, Tracker tracker )
    {
        synchronized( trackerEvents )
        {
            Set<Integer> offerIds = trackerEvents.get( tracker );
            if( offerIds == null )
            {
                offerIds = new HashSet<>();
                trackerEvents.put( tracker, offerIds );
            }
            offerIds.add( offerId );
        }
    }

    private void flush()
    {
        Map<Tracker,Set<Integer>> flushEvents = null;

        // Copy the events to the flush queue and clear the
        // events.
        synchronized( trackerEvents ) {
            if( trackerEvents.size() == 0 )
            {
                return;
            }

            flushEvents = new HashMap<Tracker,Set<Integer>>( trackerEvents );
            trackerEvents.clear();
        }

        HttpRequest request = HttpRequest.createHttpRequest( MethodType.GET, TRACKER_BASE_URL )
                .addParams( "client_id", api.getClientId() )
                .addParams( "dev_id", getDeviceId() )
                .addParams( "ut", api.getUserToken() );

        for( Map.Entry<Tracker, Set<Integer>> entry : flushEvents.entrySet() )
        {
            request.addParams( entry.getKey().name(), StringUtils.join( entry.getValue(), "," ) );
        }

        // Execute.
        requestUtil.executeMethod( request.getMethod(), request.getEndPoint(), request.getRequestParams() );
    }

    private String getDeviceId()
    {
        AdHelper.AdvertisingInfo info = AdHelper.fetchAdvertisingId( context.get() );
        return info != null ? info.advertisingId : null;
    }
}
