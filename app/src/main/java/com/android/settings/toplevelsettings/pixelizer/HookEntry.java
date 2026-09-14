package com.android.settings.toplevelsettings.pixelizer;

import android.content.Context;
import android.util.AttributeSet;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class HookEntry implements IXposedHookLoadPackage {

    private static final String TAG = "[SettingsOrder] ";
    private static final String SETTINGS_PKG = "com.android.settings";
    private static final String ACCOUNT_KEY = "top_level_account_category";
    private static final int NEW_ORDER = -140;

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) {
        if (!SETTINGS_PKG.equals(lpparam.packageName)) return;

        try {
            XposedHelpers.findAndHookConstructor(
                    "androidx.preference.Preference",
                    lpparam.classLoader,
                    Context.class,
                    AttributeSet.class,
                    int.class,
                    int.class,
                    new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) {
                            try {
                                Object thiz = param.thisObject;
                                Object key = XposedHelpers.callMethod(thiz, "getKey");
                                if (ACCOUNT_KEY.equals(key)) {
                                    XposedHelpers.callMethod(thiz, "setOrder", NEW_ORDER);
                                    XposedBridge.log(TAG + "matched key, order -> " + NEW_ORDER
                                            + " (class=" + thiz.getClass().getName() + ")");
                                }
                            } catch (Throwable t) {
                                XposedBridge.log(TAG + "ctor hook body error: " + t);
                            }
                        }
                    }
            );
            XposedBridge.log(TAG + "constructor hook installed on androidx.preference.Preference");
        } catch (Throwable t) {
            XposedBridge.log(TAG + "hook install failed: " + t);
        }
    }
}