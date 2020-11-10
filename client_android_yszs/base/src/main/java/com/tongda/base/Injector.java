package com.tongda.base;

import java.util.HashMap;
import java.util.Map;

public class Injector {

    private static Map<String, GroupLoader> sModuleLoaderMap = new HashMap<>();
    private static Map<String, Class<? extends IService>> sServiceClassMap = new HashMap<>();
    private static Map<String, IService> sServiceMap = new HashMap<>();

    public static void inject() {
        try {
            GroupLoader mainGroupLoader = (GroupLoader) Class.forName("com.tongda.yszs.MainGroupLoader").newInstance();
            Map<String, GroupLoader> mainModuleLoaderMap = mainGroupLoader.injectModule();
            if (mainModuleLoaderMap != null) {
                sModuleLoaderMap.putAll(mainModuleLoaderMap);
            }
            Map<String, Class<? extends IService>> mainServiceMap = mainGroupLoader.injectService();
            if (mainServiceMap != null) {
                sServiceClassMap.putAll(mainServiceMap);
            }

            //
            GroupLoader CCBDirectBankGroupLoader = (GroupLoader) Class.forName("com.tongda.ccb_direct_bank.CCBMainGroupLoader").newInstance();
            Map<String, GroupLoader> CCBDirectBankModuleLoaderMap = CCBDirectBankGroupLoader.injectModule();
            if (CCBDirectBankModuleLoaderMap != null) {
                sModuleLoaderMap.putAll(CCBDirectBankModuleLoaderMap);
            }
            Map<String, Class<? extends IService>> CCBDirectBankServiceMap = CCBDirectBankGroupLoader.injectService();
            if (CCBDirectBankServiceMap != null) {
                sServiceClassMap.putAll(CCBDirectBankServiceMap);
            }

            //
            GroupLoader DebugMainGroupLoader = (GroupLoader) Class.forName("com.tongda.debug.DebugMainGroupLoader").newInstance();
            Map<String, GroupLoader> debugModuleLoaderMap = DebugMainGroupLoader.injectModule();
            if (debugModuleLoaderMap != null) {
                sModuleLoaderMap.putAll(debugModuleLoaderMap);
            }
            Map<String, Class<? extends IService>> debugServiceMap = DebugMainGroupLoader.injectService();
            if (debugServiceMap != null) {
                sServiceClassMap.putAll(debugServiceMap);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static GroupLoader getModuleLoader(String moduleName) {
        return sModuleLoaderMap.get(moduleName);
    }

    public static IService getService(String serviceName) {
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
