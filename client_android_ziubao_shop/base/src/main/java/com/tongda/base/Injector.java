package com.tongda.base;

import java.util.HashMap;
import java.util.Map;

public class Injector {

    private static Map<String, GroupLoader> sModuleLoaderMap = new HashMap<>();
    private static Map<String, Class<? extends Service>> sServiceClassMap = new HashMap<>();
    private static Map<String, Service> sServiceMap = new HashMap<>();

    public static void inject() {
        //
        try {
            GroupLoader groupLoader = (GroupLoader) Class.forName("com.tongda.delivery.MainGroupLoader").newInstance();
            Map<String, GroupLoader> moduleLoaderMap = groupLoader.injectModule();
            if (moduleLoaderMap != null) {
                sModuleLoaderMap.putAll(moduleLoaderMap);
            }
            Map<String, Class<? extends Service>> serviceMap = groupLoader.injectService();
            if (serviceMap != null) {
                sServiceClassMap.putAll(serviceMap);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        //
        try {
            GroupLoader groupLoader = (GroupLoader) Class.forName("com.tongda.debug.MainGroupLoader").newInstance();
            Map<String, GroupLoader> moduleLoaderMap = groupLoader.injectModule();
            if (moduleLoaderMap != null) {
                sModuleLoaderMap.putAll(moduleLoaderMap);
            }
            Map<String, Class<? extends Service>> serviceMap = groupLoader.injectService();
            if (serviceMap != null) {
                sServiceClassMap.putAll(serviceMap);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        //
        try {
            GroupLoader groupLoader = (GroupLoader) Class.forName("com.tongda.map.MainGroupLoader").newInstance();
            Map<String, GroupLoader> moduleLoaderMap = groupLoader.injectModule();
            if (moduleLoaderMap != null) {
                sModuleLoaderMap.putAll(moduleLoaderMap);
            }
            Map<String, Class<? extends Service>> serviceMap = groupLoader.injectService();
            if (serviceMap != null) {
                sServiceClassMap.putAll(serviceMap);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static GroupLoader getModuleLoader(String moduleName) {
        return sModuleLoaderMap.get(moduleName);
    }

    public static Service getService(String serviceName) {
        if (sServiceMap.get(serviceName) != null) {
            return sServiceMap.get(serviceName);
        }

        if (sServiceClassMap.get(serviceName) != null) {
            try {
                sServiceMap.put(serviceName, sServiceClassMap.get(serviceName).newInstance());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return sServiceMap.get(serviceName);
    }
}
