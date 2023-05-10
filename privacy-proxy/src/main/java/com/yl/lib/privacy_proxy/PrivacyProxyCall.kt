package com.yl.lib.privacy_proxy

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.bluetooth.BluetoothAdapter
import android.content.*
import android.content.ClipDescription.MIMETYPE_TEXT_PLAIN
import android.content.pm.*
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.DhcpInfo
import android.net.wifi.ScanResult
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Environment
import android.provider.Settings
import android.telephony.CellInfo
import android.telephony.TelephonyManager
import androidx.annotation.Keep
import androidx.annotation.RequiresApi
import com.yl.lib.privacy_annotation.MethodInvokeOpcode
import com.yl.lib.privacy_annotation.PrivacyClassProxy
import com.yl.lib.privacy_annotation.PrivacyMethodProxy
import com.yl.lib.sentry.hook.PrivacySentry
import com.yl.lib.sentry.hook.PrivacySentryConstant
import com.yl.lib.sentry.hook.cache.CachePrivacyManager
import com.yl.lib.sentry.hook.cache.CacheUtils
import com.yl.lib.sentry.hook.util.PrivacyClipBoardManager
import com.yl.lib.sentry.hook.util.PrivacyProxyUtil
import com.yl.lib.sentry.hook.util.PrivacyUtil
import java.io.File
import java.net.Inet4Address
import java.net.InetAddress
import java.net.NetworkInterface

/**
 * @author yulun
 * @since 2021-12-22 14:23
 * 大部分敏感api拦截代理
 */
@Keep
open class PrivacyProxyCall {

    // kotlin里实际解析的是这个PrivacyProxyCall$Proxy 内部类
    @PrivacyClassProxy
    @Keep
    object Proxy {
        // 这个方法的注册放在了PrivacyProxyCall2中，提供了一个java注册的例子
        @PrivacyMethodProxy(
            originalClass = ActivityManager::class,
            originalMethod = "getRunningTasks",
            originalOpcode = MethodInvokeOpcode.INVOKEVIRTUAL
        )
        @JvmStatic
        fun getRunningTasks(
            manager: ActivityManager,
            maxNum: Int
        ): List<ActivityManager.RunningTaskInfo?>? {
            var key = PrivacySentryConstant.ACTIVITYMANAGER_GETRUNNINGTASKS
            if (PrivacySentry.Privacy.inDangerousState(key)) {
                PrivacyProxyUtil.Util.doFilePrinter(
                    key,
                    methodDocumentDesc = "当前运行中的任务",
                    bVisitorModel = true
                )
                return emptyList()
            }
            PrivacyProxyUtil.Util.doFilePrinter(key, methodDocumentDesc = "当前运行中的任务")

            return manager.getRunningTasks(maxNum)
        }

        @JvmStatic
        @PrivacyMethodProxy(
            originalClass = ActivityManager::class,
            originalMethod = "getRecentTasks",
            originalOpcode = MethodInvokeOpcode.INVOKEVIRTUAL
        )
        fun getRecentTasks(
            manager: ActivityManager,
            maxNum: Int,
            flags: Int
        ): List<ActivityManager.RecentTaskInfo>? {
            var key = PrivacySentryConstant.ACTIVITYMANAGER_GETRECENTTASKS
            if (PrivacySentry.Privacy.inDangerousState(key)) {
                PrivacyProxyUtil.Util.doFilePrinter(
                    key,
                    methodDocumentDesc = "最近运行中的任务",
                    bVisitorModel = true
                )
                return emptyList()
            }
            PrivacyProxyUtil.Util.doFilePrinter(key, methodDocumentDesc = "最近运行中的任务")

            return manager.getRecentTasks(maxNum, flags)
        }


        @PrivacyMethodProxy(
            originalClass = ActivityManager::class,
            originalMethod = "getRunningAppProcesses",
            originalOpcode = MethodInvokeOpcode.INVOKEVIRTUAL
        )
        @JvmStatic
        fun getRunningAppProcesses(manager: ActivityManager): List<ActivityManager.RunningAppProcessInfo> {
            var key = PrivacySentryConstant.ACTIVITYMANAGER_GETRUNNINGAPPPROCESSES
            if (PrivacySentry.Privacy.inDangerousState(key)) {
                PrivacyProxyUtil.Util.doFilePrinter(
                    key,
                    methodDocumentDesc = "当前运行中的进程",
                    bVisitorModel = true
                )
                return emptyList()
            }
            PrivacyProxyUtil.Util.doFilePrinter(
                key,
                methodDocumentDesc = "当前运行中的进程"
            )

            var appProcess: List<ActivityManager.RunningAppProcessInfo> = emptyList()
            try {
                // 线上三星11和12的机子 有上报，量不大
                appProcess = manager.runningAppProcesses
            } catch (e: Throwable) {
                e.printStackTrace()
            }
            return appProcess
        }

        @PrivacyMethodProxy(
            originalClass = PackageManager::class,
            originalMethod = "getInstalledPackages",
            originalOpcode = MethodInvokeOpcode.INVOKEVIRTUAL
        )
        @JvmStatic
        fun getInstalledPackages(manager: PackageManager, flags: Int): List<PackageInfo> {
            var key = PrivacySentryConstant.PACKAGEMANAGER_GETINSTALLEDPACKAGES
            if (PrivacySentry.Privacy.inDangerousState(key)) {
                PrivacyProxyUtil.Util.doFilePrinter(
                    key,
                    methodDocumentDesc = "安装包-getInstalledPackages",
                    bVisitorModel = true
                )
                return emptyList()
            }
            PrivacyProxyUtil.Util.doFilePrinter(
                key,
                methodDocumentDesc = "安装包-getInstalledPackages"
            )

            return manager.getInstalledPackages(flags)
        }

        @RequiresApi(Build.VERSION_CODES.O)
        @PrivacyMethodProxy(
            originalClass = PackageManager::class,
            originalMethod = "getPackageInfo",
            originalOpcode = MethodInvokeOpcode.INVOKEVIRTUAL
        )
        @JvmStatic
        fun getPackageInfo(
            manager: PackageManager, versionedPackage: VersionedPackage,
            flags: Int
        ): PackageInfo? {
            var key = PrivacySentryConstant.PACKAGEMANAGER_GETPACKAGEINFO
            if (PrivacySentry.Privacy.inDangerousState(key)) {
                PrivacyProxyUtil.Util.doFilePrinter(
                    key,
                    methodDocumentDesc = "安装包-getPackageInfo-${versionedPackage.packageName}",
                    bVisitorModel = true
                )
                throw PackageManager.NameNotFoundException("getPackageInfo-${versionedPackage.packageName}")
            }
            PrivacyProxyUtil.Util.doFilePrinter(
                key,
                methodDocumentDesc = "安装包-getPackageInfo-${versionedPackage.packageName}"
            )

            return manager.getPackageInfo(versionedPackage, flags)
        }

        @PrivacyMethodProxy(
            originalClass = PackageManager::class,
            originalMethod = "getPackageInfo",
            originalOpcode = MethodInvokeOpcode.INVOKEVIRTUAL
        )
        @JvmStatic
        fun getPackageInfo(
            manager: PackageManager,
            packageName: String,
            flags: Int
        ): PackageInfo? {
            var key = PrivacySentryConstant.PACKAGEMANAGER_GETPACKAGEINFO
            if (PrivacySentry.Privacy.inDangerousState(key)) {
                PrivacyProxyUtil.Util.doFilePrinter(
                    key,
                    methodDocumentDesc = "安装包-getPackageInfo-${packageName}",
                    bVisitorModel = true
                )
                throw PackageManager.NameNotFoundException("getPackageInfo-${packageName}")
            }
            PrivacyProxyUtil.Util.doFilePrinter(
                key,
                methodDocumentDesc = "安装包-getPackageInfo-${packageName}"
            )

            return manager.getPackageInfo(packageName, flags)
        }

        @PrivacyMethodProxy(
            originalClass = PackageManager::class,
            originalMethod = "getInstalledPackagesAsUser",
            originalOpcode = MethodInvokeOpcode.INVOKEVIRTUAL
        )
        @JvmStatic
        fun getInstalledPackagesAsUser(
            manager: PackageManager,
            flags: Int,
            userId: Int
        ): List<PackageInfo> {
            var key = PrivacySentryConstant.PACKAGEMANAGER_GETINSTALLEDPACKAGESASUSER
            if (PrivacySentry.Privacy.inDangerousState(key)) {
                PrivacyProxyUtil.Util.doFilePrinter(
                    key,
                    methodDocumentDesc = "安装包-getInstalledPackagesAsUser",
                    bVisitorModel = true
                )
                return emptyList()
            }
            PrivacyProxyUtil.Util.doFilePrinter(
                key,
                methodDocumentDesc = "安装包-getInstalledPackagesAsUser"
            )

            return getInstalledPackages(manager, flags);
        }

        @PrivacyMethodProxy(
            originalClass = PackageManager::class,
            originalMethod = "getInstalledApplications",
            originalOpcode = MethodInvokeOpcode.INVOKEVIRTUAL
        )
        @JvmStatic
        fun getInstalledApplications(manager: PackageManager, flags: Int): List<ApplicationInfo> {
            var key = PrivacySentryConstant.PACKAGEMANAGER_GETINSTALLEDAPPLICATIONS
            if (PrivacySentry.Privacy.inDangerousState(key)) {
                PrivacyProxyUtil.Util.doFilePrinter(
                    key,
                    methodDocumentDesc = "安装包-getInstalledApplications",
                    bVisitorModel = true
                )
                return emptyList()
            }
            PrivacyProxyUtil.Util.doFilePrinter(
                key,
                methodDocumentDesc = "安装包-getInstalledApplications"
            )

            return manager.getInstalledApplications(flags)
        }

        @PrivacyMethodProxy(
            originalClass = PackageManager::class,
            originalMethod = "getInstalledApplicationsAsUser",
            originalOpcode = MethodInvokeOpcode.INVOKEVIRTUAL
        )
        @JvmStatic
        fun getInstalledApplicationsAsUser(
            manager: PackageManager, flags: Int,
            userId: Int
        ): List<ApplicationInfo> {
            var key = PrivacySentryConstant.PACKAGEMANAGER_GETINSTALLEDAPPLICATIONSASUSER
            if (PrivacySentry.Privacy.inDangerousState(key)) {
                PrivacyProxyUtil.Util.doFilePrinter(
                    key,
                    methodDocumentDesc = "安装包-getInstalledApplicationsAsUser",
                    bVisitorModel = true
                )
                return emptyList()
            }
            PrivacyProxyUtil.Util.doFilePrinter(
                key,
                methodDocumentDesc = "安装包-getInstalledApplicationsAsUser"
            )

            return getInstalledApplications(manager, flags);
        }


        // 这个方法比较特殊，是否合规完全取决于intent参数
        // 如果指定了自己的包名，那可以认为是合规的，因为是查自己APP的AC
        // 如果没有指定包名，那就是查询了其他APP的Ac，这不合规
        // 思考，直接在SDK里拦截肯定不合适，对于业务方来说太黑盒了，如果触发bug开发会崩溃的，所以我们只打日志为业务方提供信息
        @PrivacyMethodProxy(
            originalClass = PackageManager::class,
            originalMethod = "queryIntentActivities",
            originalOpcode = MethodInvokeOpcode.INVOKEVIRTUAL
        )
        @JvmStatic
        fun queryIntentActivities(
            manager: PackageManager,
            intent: Intent,
            flags: Int
        ): List<ResolveInfo> {
            var paramBuilder = StringBuilder()
            var legal = true
            intent?.also {
                intent?.categories?.also {
                    paramBuilder.append("-categories:").append(it.toString()).append("\n")
                }
                intent?.`package`?.also {
                    paramBuilder.append("-packageName:").append(it).append("\n")
                }
                intent?.data?.also {
                    paramBuilder.append("-data:").append(it.toString()).append("\n")
                }
                intent?.component?.packageName?.also {
                    paramBuilder.append("-packageName:").append(it).append("\n")
                }
            }

            if (paramBuilder.isEmpty()) {
                legal = false
            }

            //不指定包名，我们认为这个查询不合法
            if (!paramBuilder.contains("packageName")) {
                legal = false
            }
            paramBuilder.append("-合法查询:${legal}").append("\n")

            var key = PrivacySentryConstant.PACKAGEMANAGER_QUERYINTENTACTIVITIES
            if (PrivacySentry.Privacy.inDangerousState(key)) {
                PrivacyProxyUtil.Util.doFilePrinter(
                    key,
                    methodDocumentDesc = "读安装列表-queryIntentActivities${paramBuilder?.toString()}",
                    bVisitorModel = true
                )
                return emptyList()
            }
            PrivacyProxyUtil.Util.doFilePrinter(
                key,
                methodDocumentDesc = "读安装列表-queryIntentActivities${paramBuilder?.toString()}"
            )

            return manager.queryIntentActivities(intent, flags)
        }

        @PrivacyMethodProxy(
            originalClass = PackageManager::class,
            originalMethod = "queryIntentActivityOptions",
            originalOpcode = MethodInvokeOpcode.INVOKEVIRTUAL
        )
        @JvmStatic
        fun queryIntentActivityOptions(
            manager: PackageManager,
            caller: ComponentName?,
            specifics: Array<Intent?>?,
            intent: Intent,
            flags: Int
        ): List<ResolveInfo> {
            var key = PrivacySentryConstant.PACKAGEMANAGER_QUERYINTENTACTIVITYOPTIONS
            if (PrivacySentry.Privacy.inDangerousState(key)) {
                PrivacyProxyUtil.Util.doFilePrinter(
                    key,
                    methodDocumentDesc = "读安装列表-queryIntentActivityOptions",
                    bVisitorModel = true
                )
                return emptyList()
            }
            PrivacyProxyUtil.Util.doFilePrinter(
                key,
                methodDocumentDesc = "读安装列表-queryIntentActivityOptions"
            )

            return manager.queryIntentActivityOptions(caller, specifics, intent, flags)
        }


        /**
         * 基站信息，需要开启定位
         */
        @JvmStatic
        @SuppressLint("MissingPermission")
        @PrivacyMethodProxy(
            originalClass = TelephonyManager::class,
            originalMethod = "getAllCellInfo",
            originalOpcode = MethodInvokeOpcode.INVOKEVIRTUAL
        )
        fun getAllCellInfo(manager: TelephonyManager): List<CellInfo>? {
            var key = PrivacySentryConstant.TELEPHONYMANAGER_GETALLCELLINFO
            if (PrivacySentry.Privacy.inDangerousState(key)) {
                PrivacyProxyUtil.Util.doFilePrinter(
                    key,
                    methodDocumentDesc = "定位-基站信息",
                    bVisitorModel = true
                )
                return emptyList()
            }
            PrivacyProxyUtil.Util.doFilePrinter(
                key,
                methodDocumentDesc = "定位-基站信息"
            )

            return manager.getAllCellInfo()
        }

        @PrivacyMethodProxy(
            originalClass = ClipboardManager::class,
            originalMethod = "getPrimaryClip",
            originalOpcode = MethodInvokeOpcode.INVOKEVIRTUAL
        )
        @JvmStatic
        fun getPrimaryClip(manager: ClipboardManager): ClipData? {
            var key = PrivacySentryConstant.CLIPBOARDMANAGER_GETPRIMARYCLIP
            if (PrivacySentry.Privacy.inDangerousState(key)) {
                PrivacyProxyUtil.Util.doFilePrinter(
                    key,
                    methodDocumentDesc = "剪贴板内容-getPrimaryClip",
                    bVisitorModel = true
                )
                return ClipData.newPlainText("Label", "")
            }
            if (!PrivacyClipBoardManager.isReadClipboardEnable()) {
                PrivacyProxyUtil.Util.doFilePrinter(
                    key,
                    methodDocumentDesc = "读取系统剪贴板关闭"
                )
                return ClipData.newPlainText("Label", "")
            }
            PrivacyProxyUtil.Util.doFilePrinter(
                key,
                methodDocumentDesc = "剪贴板内容-getPrimaryClip"
            )

            return manager.primaryClip
        }

        @PrivacyMethodProxy(
            originalClass = ClipboardManager::class,
            originalMethod = "getPrimaryClipDescription",
            originalOpcode = MethodInvokeOpcode.INVOKEVIRTUAL
        )
        @JvmStatic
        fun getPrimaryClipDescription(manager: ClipboardManager): ClipDescription? {
            var key = PrivacySentryConstant.CLIPBOARDMANAGER_GETPRIMARYCLIPDESCRIPTION
            if (PrivacySentry.Privacy.inDangerousState(key)) {
                PrivacyProxyUtil.Util.doFilePrinter(
                    key,
                    methodDocumentDesc = "剪贴板内容-getPrimaryClipDescription",
                    bVisitorModel = true
                )
                return ClipDescription("", arrayOf(MIMETYPE_TEXT_PLAIN))
            }

            if (!PrivacyClipBoardManager.isReadClipboardEnable()) {
                PrivacyProxyUtil.Util.doFilePrinter(key, methodDocumentDesc = "读取系统剪贴板关闭")
                return ClipDescription("", arrayOf(MIMETYPE_TEXT_PLAIN))
            }

            PrivacyProxyUtil.Util.doFilePrinter(
                key,
                methodDocumentDesc = "剪贴板内容-getPrimaryClipDescription"
            )

            return manager.primaryClipDescription
        }

        @PrivacyMethodProxy(
            originalClass = ClipboardManager::class,
            originalMethod = "getText",
            originalOpcode = MethodInvokeOpcode.INVOKEVIRTUAL
        )
        @JvmStatic
        fun getText(manager: ClipboardManager): CharSequence? {
            var key = PrivacySentryConstant.CLIPBOARDMANAGER_GETTEXT
            if (PrivacySentry.Privacy.inDangerousState(key)) {
                PrivacyProxyUtil.Util.doFilePrinter(
                    key,
                    methodDocumentDesc = "剪贴板内容-getText",
                    bVisitorModel = true
                )
                return ""
            }

            if (!PrivacyClipBoardManager.isReadClipboardEnable()) {
                PrivacyProxyUtil.Util.doFilePrinter(key, methodDocumentDesc = "读取系统剪贴板关闭")
                return ""
            }
            PrivacyProxyUtil.Util.doFilePrinter(key, methodDocumentDesc = "剪贴板内容-getText")

            return manager.text
        }

        @PrivacyMethodProxy(
            originalClass = ClipboardManager::class,
            originalMethod = "setPrimaryClip",
            originalOpcode = MethodInvokeOpcode.INVOKEVIRTUAL
        )
        @JvmStatic
        fun setPrimaryClip(manager: ClipboardManager, clip: ClipData?) {
            var key = PrivacySentryConstant.CLIPBOARDMANAGER_SETPRIMARYCLIP
            if (PrivacySentry.Privacy.inDangerousState(key)) {
                PrivacyProxyUtil.Util.doFilePrinter(
                    key,
                    methodDocumentDesc = "设置剪贴板内容-setPrimaryClip",
                    bVisitorModel = true
                )
                return
            }
            PrivacyProxyUtil.Util.doFilePrinter(key, "设置剪贴板内容-setPrimaryClip")

            clip?.let { manager.setPrimaryClip(it) }
        }

        @PrivacyMethodProxy(
            originalClass = ClipboardManager::class,
            originalMethod = "setText",
            originalOpcode = MethodInvokeOpcode.INVOKEVIRTUAL
        )
        @JvmStatic
        fun setText(manager: ClipboardManager, clip: CharSequence?) {
            var key = PrivacySentryConstant.CLIPBOARDMANAGER_SETTEXT
            if (PrivacySentry.Privacy.inDangerousState(key)) {
                PrivacyProxyUtil.Util.doFilePrinter(
                    key,
                    methodDocumentDesc = "设置剪贴板内容-setText",
                    bVisitorModel = true
                )
                return
            }
            PrivacyProxyUtil.Util.doFilePrinter(key, methodDocumentDesc = "设置剪贴板内容-setText")

            manager.text = clip
        }

        /**
         * WIFI的SSID
         */
        @JvmStatic
        @PrivacyMethodProxy(
            originalClass = WifiInfo::class,
            originalMethod = "getSSID",
            originalOpcode = MethodInvokeOpcode.INVOKEVIRTUAL
        )
        fun getSSID(manager: WifiInfo): String? {
            var key = PrivacySentryConstant.WIFIINFO_GETSSID
            if (PrivacySentry.Privacy.inDangerousState(key)) {
                PrivacyProxyUtil.Util.doFilePrinter(
                    key,
                    methodDocumentDesc = "读取SSID",
                    bVisitorModel = true
                )
                return ""
            }
            PrivacyProxyUtil.Util.doFilePrinter(key, methodDocumentDesc = "读取SSID")
            return CachePrivacyManager.Manager.loadWithTimeMemoryCache(
                key,
                "getSSID",
                "",
                duration = CacheUtils.Utils.MINUTE * 5
            ) { manager.ssid }
        }

        /**
         * WIFI的IpAddress
         */
        @JvmStatic
        @PrivacyMethodProxy(
            originalClass = WifiInfo::class,
            originalMethod = "getIpAddress",
            originalOpcode = MethodInvokeOpcode.INVOKEVIRTUAL
        )
        fun getIpAddress(manager: WifiInfo): Int {
            val key = PrivacySentryConstant.WIFIINFO_GETIPADDRESS
            if (PrivacySentry.Privacy.inDangerousState(key)) {
                PrivacyProxyUtil.Util.doFilePrinter(
                    key,
                    methodDocumentDesc = "读取WifiInfo-getIpAddress",
                    bVisitorModel = true
                )
                return 0
            }
            PrivacyProxyUtil.Util.doFilePrinter(key, methodDocumentDesc = "读取WifiInfo-getIpAddress")
            return CachePrivacyManager.Manager.loadWithTimeMemoryCache(
                key,
                "getIpAddress",
                0,
                duration = CacheUtils.Utils.MINUTE * 5
            ) { manager.ipAddress }
        }

        /**
         * WIFI的BSSI
         */
        @JvmStatic
        @PrivacyMethodProxy(
            originalClass = WifiInfo::class,
            originalMethod = "getBSSID",
            originalOpcode = MethodInvokeOpcode.INVOKEVIRTUAL
        )
        fun getBSSID(manager: WifiInfo): String? {
            var key = PrivacySentryConstant.WIFIINFO_GETBSSID
            if (PrivacySentry.Privacy.inDangerousState(key)) {
                PrivacyProxyUtil.Util.doFilePrinter(
                    key,
                    methodDocumentDesc = "getBSSID",
                    bVisitorModel = true
                )
                return ""
            }
            PrivacyProxyUtil.Util.doFilePrinter(key, methodDocumentDesc = "getBSSID")
            return CachePrivacyManager.Manager.loadWithTimeMemoryCache(
                key,
                "getBSSID",
                "",
                duration = CacheUtils.Utils.MINUTE * 5
            ) { manager.bssid }
        }

        /**
         * WIFI扫描结果
         */
        @JvmStatic
        @PrivacyMethodProxy(
            originalClass = WifiManager::class,
            originalMethod = "getScanResults",
            originalOpcode = MethodInvokeOpcode.INVOKEVIRTUAL
        )
        fun getScanResults(manager: WifiManager): List<ScanResult>? {
            var key = PrivacySentryConstant.WIFIMANAGER_GETSCANRESULTS
            if (PrivacySentry.Privacy.inDangerousState(key)) {
                PrivacyProxyUtil.Util.doFilePrinter(
                    key,
                    methodDocumentDesc = "WIFI扫描结果",
                    bVisitorModel = true
                )
                return emptyList()
            }

            return CachePrivacyManager.Manager.loadWithTimeMemoryCache(
                key,
                "WIFI扫描结果",
                emptyList(),
                duration = CacheUtils.Utils.MINUTE * 5
            ) { manager.scanResults }
        }

        /**
         * DHCP信息
         */
        @JvmStatic
        @PrivacyMethodProxy(
            originalClass = WifiManager::class,
            originalMethod = "getDhcpInfo",
            originalOpcode = MethodInvokeOpcode.INVOKEVIRTUAL
        )
        fun getDhcpInfo(manager: WifiManager): DhcpInfo? {
            var key = PrivacySentryConstant.WIFIMANAGER_GETDHCPINFO
            if (PrivacySentry.Privacy.inDangerousState(key)) {
                PrivacyProxyUtil.Util.doFilePrinter(
                    key,
                    methodDocumentDesc = "DHCP地址",
                    bVisitorModel = true
                )
                // 这里直接写空可能有风险
                return null
            }
            PrivacyProxyUtil.Util.doFilePrinter(key, methodDocumentDesc = "DHCP地址")

            return manager.dhcpInfo
        }

        /**
         * DHCP信息
         */
        @SuppressLint("MissingPermission")
        @JvmStatic
        @PrivacyMethodProxy(
            originalClass = WifiManager::class,
            originalMethod = "getConfiguredNetworks",
            originalOpcode = MethodInvokeOpcode.INVOKEVIRTUAL
        )
        fun getConfiguredNetworks(manager: WifiManager): List<WifiConfiguration>? {
            var key = PrivacySentryConstant.WIFIMANAGER_GETCONFIGUREDNETWORKS
            if (PrivacySentry.Privacy.inDangerousState(key)) {
                PrivacyProxyUtil.Util.doFilePrinter(
                    key,
                    methodDocumentDesc = "前台用户配置的所有网络的列表",
                    bVisitorModel = true
                )
                return emptyList()
            }
            PrivacyProxyUtil.Util.doFilePrinter(key, methodDocumentDesc = "前台用户配置的所有网络的列表")

            return manager.getConfiguredNetworks()
        }


        /**
         * 位置信息
         */
        @JvmStatic
        @SuppressLint("MissingPermission")
        @PrivacyMethodProxy(
            originalClass = LocationManager::class,
            originalMethod = "getLastKnownLocation",
            originalOpcode = MethodInvokeOpcode.INVOKEVIRTUAL
        )
        fun getLastKnownLocation(
            manager: LocationManager, provider: String
        ): Location? {
            var key = PrivacySentryConstant.LOCATIONMANAGER_GETLASTKNOWNLOCATION + "_${provider}"
            if (PrivacySentry.Privacy.inDangerousState(key)) {
                PrivacyProxyUtil.Util.doFilePrinter(
                    key,
                    methodDocumentDesc = "上一次的位置信息",
                    bVisitorModel = true
                )
                // 这里直接写空可能有风险
                return null
            }

            var locationStr = CachePrivacyManager.Manager.loadWithTimeDiskCache(
                key,
                "上一次的位置信息",
                ""
            ) { PrivacyUtil.Util.formatLocation(manager.getLastKnownLocation(provider)) }

            var location: Location? = null
            locationStr.also {
                location = PrivacyUtil.Util.formatLocation(it)
            }
            if (location == null) {
                return manager.getLastKnownLocation(provider)
            }
            return location
        }


        @SuppressLint("MissingPermission")
        @JvmStatic
        @PrivacyMethodProxy(
            originalClass = LocationManager::class,
            originalMethod = "requestLocationUpdates",
            originalOpcode = MethodInvokeOpcode.INVOKEVIRTUAL
        )
        fun requestLocationUpdates(
            manager: LocationManager, provider: String, minTime: Long, minDistance: Float,
            listener: LocationListener
        ) {
            var key = PrivacySentryConstant.LOCATIONMANAGER_REQUESTLOCATIONUPDATES
            if (PrivacySentry.Privacy.inDangerousState(key)) {
                PrivacyProxyUtil.Util.doFilePrinter(
                    key,
                    methodDocumentDesc = "监视精细行动轨迹",
                    bVisitorModel = true
                )
                return
            }
            PrivacyProxyUtil.Util.doFilePrinter(key, methodDocumentDesc = "监视精细行动轨迹")

            manager.requestLocationUpdates(provider, minTime, minDistance, listener)
        }


        var objectMacLock = Object()
        var objectIpLock = Object()
        var objectHardMacLock = Object()
        var objectSNLock = Object()
        var objectAndroidIdLock = Object()
        var objectBluetoothLock = Object()
        var objectExternalStorageDirectoryLock = Object()


        @PrivacyMethodProxy(
            originalClass = WifiInfo::class,
            originalMethod = "getMacAddress",
            originalOpcode = MethodInvokeOpcode.INVOKEVIRTUAL
        )
        @JvmStatic
        fun getMacAddress(manager: WifiInfo): String? {
            var key = PrivacySentryConstant.WIFIINFO_GETMACADDRESS
            if (PrivacySentry.Privacy.inDangerousState(key)) {
                PrivacyProxyUtil.Util.doFilePrinter(
                    key,
                    methodDocumentDesc = "mac地址-getMacAddress",
                    bVisitorModel = true
                )
                return ""
            }

            synchronized(objectMacLock) {
                return CachePrivacyManager.Manager.loadWithDiskCache(
                    key,
                    "mac地址-getMacAddress",
                    ""
                ) { manager.macAddress }
            }
        }

        @PrivacyMethodProxy(
            originalClass = NetworkInterface::class,
            originalMethod = "getHardwareAddress",
            originalOpcode = MethodInvokeOpcode.INVOKEVIRTUAL
        )
        @JvmStatic
        fun getHardwareAddress(manager: NetworkInterface): ByteArray? {
            var key = PrivacySentryConstant.NETWORKINTERFACE_GETHARDWAREADDRESS
            if (PrivacySentry.Privacy.inDangerousState(key)) {
                PrivacyProxyUtil.Util.doFilePrinter(
                    key,
                    methodDocumentDesc = "mac地址-getHardwareAddress",
                    bVisitorModel = true
                )
                return ByteArray(1)
            }
            synchronized(objectHardMacLock) {
                return CachePrivacyManager.Manager.loadWithDiskCache(
                    key,
                    "mac地址-getHardwareAddress",
                    ""
                ) { manager.hardwareAddress.toString() }.toByteArray()
            }
        }


        @PrivacyMethodProxy(
            originalClass = BluetoothAdapter::class,
            originalMethod = "getAddress",
            originalOpcode = MethodInvokeOpcode.INVOKEVIRTUAL
        )
        @JvmStatic
        fun getAddress(manager: BluetoothAdapter): String? {
            var key = PrivacySentryConstant.BLUETOOTHADAPTER_GETADDRESS
            if (PrivacySentry.Privacy.inDangerousState(key)) {
                PrivacyProxyUtil.Util.doFilePrinter(
                    key,
                    methodDocumentDesc = "蓝牙地址-getAddress",
                    bVisitorModel = true
                )
                return ""
            }
            synchronized(objectBluetoothLock) {
                return CachePrivacyManager.Manager.loadWithMemoryCache(
                    key,
                    "蓝牙地址-getAddress",
                    ""
                ) { manager.address }
            }
        }


        @PrivacyMethodProxy(
            originalClass = Inet4Address::class,
            originalMethod = "getAddress",
            originalOpcode = MethodInvokeOpcode.INVOKEVIRTUAL
        )
        @JvmStatic
        fun getAddress(manager: Inet4Address): ByteArray? {
            var key = PrivacySentryConstant.INET4ADDRESS_GETADDRESS
            if (PrivacySentry.Privacy.inDangerousState(key)) {
                PrivacyProxyUtil.Util.doFilePrinter(key, "ip地址-getAddress", bVisitorModel = true)
                return ByteArray(1)
            }
            var address = manager.address
            PrivacyProxyUtil.Util.doFilePrinter(
                key,
                "ip地址-getAddress-${manager.address ?: ""} , address is ${address ?: ""}"
            )
            return address
        }

        @PrivacyMethodProxy(
            originalClass = InetAddress::class,
            originalMethod = "getAddress",
            originalOpcode = MethodInvokeOpcode.INVOKEVIRTUAL
        )
        @JvmStatic
        fun getAddress(manager: InetAddress): ByteArray? {
            var key = PrivacySentryConstant.INETADDRESS_GETADDRESS
            if (PrivacySentry.Privacy.inDangerousState(key)) {
                PrivacyProxyUtil.Util.doFilePrinter(key, "ip地址-getAddress", bVisitorModel = true)
                return ByteArray(1)
            }
            var address = manager.address
            PrivacyProxyUtil.Util.doFilePrinter(
                key,
                "ip地址-getAddress-${manager.address ?: ""} , address is ${address ?: ""} "
            )
            return address
        }

        @PrivacyMethodProxy(
            originalClass = Inet4Address::class,
            originalMethod = "getHostAddress",
            originalOpcode = MethodInvokeOpcode.INVOKEVIRTUAL
        )
        @JvmStatic
        fun getHostAddress(manager: Inet4Address): String? {
            var key = PrivacySentryConstant.INET4ADDRESS_GETHOSTADDRESS
            if (PrivacySentry.Privacy.inDangerousState(key)) {
                PrivacyProxyUtil.Util.doFilePrinter(
                    key,
                    "ip地址-getHostAddress",
                    bVisitorModel = true
                )
                return ""
            }

            var address = manager.hostAddress
            PrivacyProxyUtil.Util.doFilePrinter(
                key,
                "ip地址-getHostAddress-${manager.hostAddress ?: ""} , address is ${address ?: ""}"
            )
            return address
        }

        @PrivacyMethodProxy(
            originalClass = InetAddress::class,
            originalMethod = "getHostAddress",
            originalOpcode = MethodInvokeOpcode.INVOKEVIRTUAL
        )
        @JvmStatic
        fun getHostAddress(manager: InetAddress): String? {
            var key = PrivacySentryConstant.INETADDRESS_GETHOSTADDRESS
            if (PrivacySentry.Privacy.inDangerousState(key)) {
                PrivacyProxyUtil.Util.doFilePrinter(
                    key,
                    "ip地址-getHostAddress",
                    bVisitorModel = true
                )
                return ""
            }

            var address = manager.hostAddress
            PrivacyProxyUtil.Util.doFilePrinter(
                key,
                "ip地址-getHostAddress-${manager.hostAddress ?: ""} , address is ${address ?: ""}"
            )
            return address
        }

        @PrivacyMethodProxy(
            originalClass = Settings.Secure::class,
            originalMethod = "getString",
            originalOpcode = MethodInvokeOpcode.INVOKESTATIC
        )
        @JvmStatic
        fun getString(contentResolver: ContentResolver?, type: String?): String? {
            var key = "Secure-getString-$type"
            if (!"android_id".equals(type)) {
                return Settings.Secure.getString(
                    contentResolver,
                    type
                )
            }
            if (PrivacySentry.Privacy.inDangerousState(key)) {
                PrivacyProxyUtil.Util.doFilePrinter(
                    key,
                    methodDocumentDesc = "系统信息",
                    args = type,
                    bVisitorModel = true
                )
                return ""
            }
            synchronized(objectAndroidIdLock) {
                return CachePrivacyManager.Manager.loadWithDiskCache(
                    key,
                    "getString-系统信息",
                    ""
                ) {
                    Settings.Secure.getString(
                        contentResolver,
                        type
                    )
                }
            }
        }


        @PrivacyMethodProxy(
            originalClass = Settings.System::class,
            originalMethod = "getString",
            originalOpcode = MethodInvokeOpcode.INVOKESTATIC
        )
        @JvmStatic
        fun getStringSystem(contentResolver: ContentResolver?, type: String?): String? {
            return getString(contentResolver, type)
        }

        @PrivacyMethodProxy(
            originalClass = android.os.Build::class,
            originalMethod = "getSerial",
            originalOpcode = MethodInvokeOpcode.INVOKESTATIC
        )
        @JvmStatic
        fun getSerial(): String? {
            var key = PrivacySentryConstant.BUILD_GETSERIAL
            if (PrivacySentry.Privacy.inDangerousState(key)) {
                PrivacyProxyUtil.Util.doFilePrinter(
                    key,
                    methodDocumentDesc = "Serial",
                    bVisitorModel = true
                )
                return ""
            }
            synchronized(objectSNLock) {
                return CachePrivacyManager.Manager.loadWithDiskCache(
                    key,
                    "getSerial",
                    ""
                ) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        Build.getSerial()
                    } else {
                        Build.SERIAL
                    }
                }
            }
        }

        @PrivacyMethodProxy(
            originalClass = android.os.Environment::class,
            originalMethod = "getExternalStorageDirectory",
            originalOpcode = MethodInvokeOpcode.INVOKESTATIC
        )
        @JvmStatic
        fun getExternalStorageDirectory(): File? {
            var result: File? = null
            var key = PrivacySentryConstant.ENVIRONMENT_GETEXTERNALSTORAGEDIRECTORY
            if (PrivacySentry.Privacy.inDangerousState(key)) {
                PrivacyProxyUtil.Util.doFilePrinter(
                    key,
                    methodDocumentDesc = "getExternalStorageDirectory",
                    bVisitorModel = true
                )
                return result
            }
            synchronized(objectExternalStorageDirectoryLock) {
                result = CachePrivacyManager.Manager.loadWithMemoryCache<File>(
                    key,
                    "getExternalStorageDirectory",
                    File("")
                ) {
                    Environment.getExternalStorageDirectory()
                }
            }
            return result
        }

        // 拦截获取系统设备，简直离谱，这个也不能重复获取
        @JvmStatic
        fun getBrand(): String? {
            var key = PrivacySentryConstant.BUILD_GETBRAND
            if (PrivacySentry.Privacy.inDangerousState(key)) {
                PrivacyProxyUtil.Util.doFilePrinter(
                    key,
                    methodDocumentDesc = "getBrand",
                    bVisitorModel = true
                )
                return ""
            }
            return CachePrivacyManager.Manager.loadWithMemoryCache(
                key,
                "getBrand",
                ""
            ) {
                Build.BRAND
            }
        }
    }
}
