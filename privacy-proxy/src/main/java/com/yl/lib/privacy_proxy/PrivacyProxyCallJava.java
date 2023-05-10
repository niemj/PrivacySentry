package com.yl.lib.privacy_proxy;

import android.content.ClipboardManager;
import android.net.wifi.WifiManager;
import androidx.annotation.Keep;
import com.yl.lib.privacy_annotation.MethodInvokeOpcode;
import com.yl.lib.privacy_annotation.PrivacyClassBlack;
import com.yl.lib.privacy_annotation.PrivacyClassProxy;
import com.yl.lib.privacy_annotation.PrivacyMethodProxy;
import com.yl.lib.sentry.hook.PrivacySentry;
import com.yl.lib.sentry.hook.PrivacySentryConstant;
import com.yl.lib.sentry.hook.cache.CachePrivacyManager;
import com.yl.lib.sentry.hook.cache.CacheUtils;
import com.yl.lib.sentry.hook.util.PrivacyClipBoardManager;
import com.yl.lib.sentry.hook.util.PrivacyProxyUtil;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Lambda;

/**
 * @author yulun
 * @since 2022-11-30 17:47
 * kotlin Boolean 和 java boolean需要特殊处理，不然方法代理不了，在这里直接用java更方便
 */
@PrivacyClassProxy
@Keep
public class PrivacyProxyCallJava {


    @PrivacyMethodProxy(
            originalClass = ClipboardManager.class,
            originalMethod = "hasPrimaryClip",
            originalOpcode = MethodInvokeOpcode.INVOKEVIRTUAL
    )
    public static boolean hasPrimaryClip(ClipboardManager manager) {
        String key = PrivacySentryConstant.Companion.getCLIPBOARDMANAGER_HASPRIMARYCLIP();
        if (PrivacySentry.Privacy.INSTANCE.inDangerousState(key)) {
            PrivacyProxyUtil.Util.INSTANCE.doFilePrinter(key, "读取系统剪贴板是否有值-hasPrimaryClip", "", true, false);
            return false;
        }
        if (!PrivacyClipBoardManager.Companion.isReadClipboardEnable()) {
            PrivacyProxyUtil.Util.INSTANCE.doFilePrinter(key, "读取系统剪贴板是否有值-拦截", "", false, false);
            return false;
        }
        PrivacyProxyUtil.Util.INSTANCE.doFilePrinter(key, "读取系统剪贴板是否有值-hasPrimaryClip", "", false, false);
        return manager.hasPrimaryClip();
    }

    /**
     * WIFI是否开启
     */
    @PrivacyMethodProxy(
            originalClass = WifiManager.class,
            originalMethod = "isWifiEnabled",
            originalOpcode = MethodInvokeOpcode.INVOKEVIRTUAL
    )
    public static boolean isWifiEnabled(WifiManager manager) {
        String key = PrivacySentryConstant.Companion.getWIFIMANAGER_ISWIFIENABLED();
        if (PrivacySentry.Privacy.INSTANCE.inDangerousState(key)) {
            PrivacyProxyUtil.Util.INSTANCE.doFilePrinter(key, "读取WiFi状态", "", true, false);
            return true;
        }
        PrivacyProxyUtil.Util.INSTANCE.doFilePrinter(key, "读取WiFi状态", "", false, false);
        return CachePrivacyManager.Manager.INSTANCE.loadWithTimeMemoryCache(
                key,
                "isWifiEnabled",
                true,
                CacheUtils.Utils.MINUTE * 5,
                (new PrivacyProxyCallJavaWifiEnabled(manager)));
    }


    @PrivacyClassBlack
    public static class PrivacyProxyCallJavaWifiEnabled extends Lambda<Boolean> implements Function0<Boolean> {
        final /* synthetic */ WifiManager $manager;

        PrivacyProxyCallJavaWifiEnabled(WifiManager wifiManager) {
            super(0);
            this.$manager = wifiManager;
        }

        @Override
        public Boolean invoke() {
            return this.$manager.isWifiEnabled();
        }
    }

}


