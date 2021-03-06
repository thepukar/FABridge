package com.zhy.fabridge.lib;

import android.support.v4.util.ArrayMap;

import java.lang.ref.SoftReference;

/**
 * Created by zhy on 16/4/12.
 */
public class Fabridge
{
    private static final String SUFFIX = "$$FabridgeProxy";

    private static ArrayMap<String, SoftReference<FabridgeProxy>> mCache = new ArrayMap<>();

    public static void call(Object host, String id, Object... params)
    {
        FabridgeProxy fabridgeProxy = findFabridgeProxy(host);
        fabridgeProxy.call(host, id, params);
    }

    private static FabridgeProxy findFabridgeProxy(Object host)
    {
        try
        {
            Class clazz = host.getClass();
            String className = clazz.getName() + SUFFIX;
            SoftReference<FabridgeProxy> fabridgeRef = null;
            synchronized (Fabridge.class)
            {
                fabridgeRef = mCache.get(className);
            }
            if (fabridgeRef == null || fabridgeRef.get() == null)
            {
                Class injectorClazz = Class.forName(className);
                FabridgeProxy fabridgeProxy = (FabridgeProxy) injectorClazz.newInstance();
                fabridgeRef = new SoftReference<FabridgeProxy>(fabridgeProxy);
                synchronized (Fabridge.class)
                {
                    mCache.put(className, fabridgeRef);
                }
                return fabridgeProxy;
            } else
            {
                return fabridgeRef.get();
            }
        } catch (ClassNotFoundException e)
        {
//            e.printStackTrace();
            return new FabridgeProxy()
            {
                @Override
                public void call(Object source, String id, Object... params)
                {

                }
            };
        } catch (InstantiationException e)
        {
            e.printStackTrace();
        } catch (IllegalAccessException e)
        {
            e.printStackTrace();
        }
        throw new RuntimeException(String.format("can not find %s , something wrong when compiler.", host.getClass().getSimpleName() + SUFFIX));
    }
}
