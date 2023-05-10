package com.yl.lib.sentry.hook

/**
 * @author yulun
 * @sinice 2022-01-13 19:57
 */
class PrivacySentryConstant {


    companion object {

        //***********************************仅代理************************************************//
        /**
         * SensorManager-registerListener（仅代理）
         * 已配置游客白名单，仅代理
         */
        val SENSORMANAGER_REGISTERLISTENER = "SensorManager-registerListener"

        /**
         * requestPermissions（游客模式+直接返回）
         * 已配置游客白名单，仅代理
         */
        val REQUESTPERMISSIONS = "requestPermissions"

        /**
         * PackageManager-getPackageInfo（游客模式+直接返回）
         * 游客返回throw NameNotFoundException，已配置游客白名单，仅代理
         */
        val PACKAGEMANAGER_GETPACKAGEINFO = "PackageManager-getPackageInfo"

        /**
         * Inet4Address-getAddress（游客模式+直接返回）
         * 已配置游客白名单，仅代理
         */
        val INET4ADDRESS_GETADDRESS = "Inet4Address-getAddress"

        /**
         * InetAddress-getAddress（游客模式+直接返回）
         * 已配置游客白名单，仅代理
         */
        val INETADDRESS_GETADDRESS = "InetAddress-getAddress"

        /**
         * Inet4Address-getHostAddress（游客模式+直接返回）
         * 已配置游客白名单，仅代理
         */
        val INET4ADDRESS_GETHOSTADDRESS = "Inet4Address-getHostAddress"

        /**
         * InetAddress-getHostAddress（游客模式+直接返回）
         * 已配置游客白名单，仅代理
         */
        val INETADDRESS_GETHOSTADDRESS = "InetAddress-getHostAddress"

        /**
         * ContentResolver-query（游客模式+直接返回）
         * 已配置游客白名单，仅代理
         */
        val CONTENTRESOLVER_QUERY = "ContentResolver-query"

        /**
         * ContentResolver-insert（游客模式+直接返回）
         * 已配置游客白名单，仅代理
         */
        val CONTENTRESOLVER_INSERT = "ContentResolver-insert"

        /**
         * ContentResolver-update（游客模式+直接返回）
         * 已配置游客白名单，仅代理
         */
        val CONTENTRESOLVER_UPDATE = "ContentResolver-update"

        /**
         * ContentResolver-delete（（游客模式+直接返回）
         * 已配置游客白名单，仅代理
         */
        val CONTENTRESOLVER_DELETE = "ContentResolver-delete"

        /**
         * PackageManager-getInstalledPackagesAsUser（游客模式+PackageManager-getInstalledPackages）
         * 游客返回空列表，已配置游客白名单，仅代理
         */
        val PACKAGEMANAGER_GETINSTALLEDPACKAGESASUSER = "PackageManager-getInstalledPackagesAsUser"

        /**
         * PackageManager-getInstalledApplications（游客模式+直接返回）
         * 游客返回空列表，已配置游客白名单，仅代理
         */
        val PACKAGEMANAGER_GETINSTALLEDAPPLICATIONS = "PackageManager-getInstalledApplications"

        /**
         * PackageManager-getInstalledApplicationsAsUser（游客模式+PackageManager-getInstalledApplications）
         * 游客返回空列表，已配置游客白名单，仅代理
         */
        val PACKAGEMANAGER_GETINSTALLEDAPPLICATIONSASUSER =
            "PackageManager-getInstalledApplicationsAsUser"


        //***********************************游客模式+直接返回***************************************//

        /**
         * WifiManager-getDhcpInfo（游客模式+直接返回）
         * 游客返回null,有风险
         */
        val WIFIMANAGER_GETDHCPINFO = "WifiManager-getDhcpInfo"

        /**
         * ActivityManager-getRunningTasks（游客模式+直接返回）
         * 游客返回空列表
         */
        val ACTIVITYMANAGER_GETRUNNINGTASKS = "ActivityManager-getRunningTasks"

        /**
         * ActivityManager-getRecentTasks（游客模式+直接返回）
         * 游客返回空列表
         */
        val ACTIVITYMANAGER_GETRECENTTASKS = "ActivityManager-getRecentTasks"

        /**
         * ActivityManager-getRunningAppProcesses（游客模式+直接返回）
         * 游客返回空列表
         */
        val ACTIVITYMANAGER_GETRUNNINGAPPPROCESSES = "ActivityManager-getRunningAppProcesses"

        /**
         * PackageManager-getInstalledPackages（游客模式+直接返回）
         * 游客返回空列表
         */
        val PACKAGEMANAGER_GETINSTALLEDPACKAGES = "PackageManager-getInstalledPackages"

        /**
         * PackageManager-queryIntentActivities（游客模式+直接返回）
         * 游客返回空列表
         */
        val PACKAGEMANAGER_QUERYINTENTACTIVITIES = "PackageManager-queryIntentActivities"

        /**
         * PackageManager-queryIntentActivityOptions（游客模式+直接返回）
         * 游客返回空列表
         */
        val PACKAGEMANAGER_QUERYINTENTACTIVITYOPTIONS = "PackageManager-queryIntentActivityOptions"

        /**
         * TelephonyManager-getAllCellInfo（游客模式+直接返回）
         * 游客返回空列表
         */
        val TELEPHONYMANAGER_GETALLCELLINFO = "TelephonyManager-getAllCellInfo"

        /**
         * ClipboardManager-getPrimaryClip（游客模式+直接返回）
         * 游客返回ClipData.newPlainText("Label", "")
         */
        val CLIPBOARDMANAGER_GETPRIMARYCLIP = "ClipboardManager-getPrimaryClip"

        /**
         * ClipboardManager-getPrimaryClipDescription（游客模式+直接返回）
         * 游客返回ClipDescription("", arrayOf(MIMETYPE_TEXT_PLAIN))
         */
        val CLIPBOARDMANAGER_GETPRIMARYCLIPDESCRIPTION =
            "ClipboardManager-getPrimaryClipDescription"

        /**
         * ClipboardManager-getText（游客模式+直接返回）
         * 游客返回空字符
         */
        val CLIPBOARDMANAGER_GETTEXT = "ClipboardManager-getText"

        /**
         * ClipboardManager-setPrimaryClip（游客模式+直接返回）
         * 游客方法直接返回
         */
        val CLIPBOARDMANAGER_SETPRIMARYCLIP = "ClipboardManager-setPrimaryClip"

        /**
         * ClipboardManager-setText（游客模式+直接返回）
         * 游客方法直接返回
         */
        val CLIPBOARDMANAGER_SETTEXT = "ClipboardManager-setText"

        /**
         * WifiManager-getConfiguredNetworks（游客模式+直接返回）
         * 游客返回空列表
         */
        val WIFIMANAGER_GETCONFIGUREDNETWORKS = "WifiManager-getConfiguredNetworks"

        /**
         * LocationManager-requestLocationUpdates（游客模式+直接返回）
         * 游客方法直接返回
         */
        val LOCATIONMANAGER_REQUESTLOCATIONUPDATES = "LocationManager-requestLocationUpdates"

        /**
         * ClipboardManager-hasPrimaryClip（游客模式+直接返回）
         * 游客方法直接返回false
         */
        val CLIPBOARDMANAGER_HASPRIMARYCLIP = "ClipboardManager-hasPrimaryClip"


        //***********************************游客模式+缓存处理***************************************//

        /**
         * WifiInfo-getBSSID（游客模式+缓存处理）
         * 游客返回空字符，已配置不使用缓存
         */
        val WIFIINFO_GETBSSID = "WifiInfo-getBSSID"

        /**
         * WifiInfo-getSSID（游客模式+缓存处理）
         * 游客返回空字符，已配置不使用缓存
         */
        val WIFIINFO_GETSSID = "WifiInfo-getSSID"

        /**
         * getIpAddress（游客模式+缓存处理）
         * 游客返回0，已配置不使用缓存
         */
        val WIFIINFO_GETIPADDRESS = "WifiInfo-getIpAddress"

        /**
         * isWifiEnabled（游客模式+缓存处理）
         * 游客返回true，已配置不使用缓存
         */
        val WIFIMANAGER_ISWIFIENABLED = "WifiManager-isWifiEnabled"

        /**
         * Environment-getExternalStorageDirectory（游客模式+缓存处理）
         * 游客返回null,有风险，已配置游客白名单，缓存处理
         */
        val ENVIRONMENT_GETEXTERNALSTORAGEDIRECTORY = "Environment-getExternalStorageDirectory"

        /**
         * WifiManager-getScanResults（游客模式+缓存处理）
         * 游客返回空列表
         */
        val WIFIMANAGER_GETSCANRESULTS = "WifiManager-getScanResults"

        /**
         * LocationManager-getLastKnownLocation_provider（游客模式+缓存处理）
         * 游客返回null,有风险
         */
        val LOCATIONMANAGER_GETLASTKNOWNLOCATION = "LocationManager-getLastKnownLocation"

        /**
         * WifiInfo-getMacAddress（游客模式+缓存处理）
         * 游客返回空字符
         */
        val WIFIINFO_GETMACADDRESS = "WifiInfo-getMacAddress"

        /**
         * NetworkInterface-getHardwareAddress（游客模式+缓存处理）
         * 游客返回ByteArray(1)
         */
        val NETWORKINTERFACE_GETHARDWAREADDRESS = "NetworkInterface-getHardwareAddress"

        /**
         * BluetoothAdapter-getAddress（游客模式+缓存处理）
         * 游客返回空字符
         */
        val BLUETOOTHADAPTER_GETADDRESS = "BluetoothAdapter-getAddress"

        /**
         * Secure-getString_android_id（游客模式+缓存处理）
         * 游客返回空字符
         */
        val SECURE_GETSTRING_ANDROID_ID = "Secure-getString-android_id"

        /**
         * Build-getSerial（游客模式+缓存处理）
         * 游客返回空字符
         */
        val BUILD_GETSERIAL = "Build-getSerial"

        /**
         * Build-getBrand（游客模式+缓存处理）
         * 游客返回空字符
         */
        val BUILD_GETBRAND = "Build-getBrand"

        /**
         * SensorManager-getSensorList（游客模式+缓存处理）
         * 游客返回空列表
         */
        val SENSORMANAGER_GETSENSORLIST = "SensorManager-getSensorList"

        /**
         * TelephonyManager-getMeid（游客模式+缓存处理）
         * 游客返回空字符
         */
        val TELEPHONYMANAGER_GETMEID = "TelephonyManager-getMeid"

        /**
         * TelephonyManager-getDeviceId（游客模式+缓存处理）
         * 游客返回空字符
         */
        val TELEPHONYMANAGER_GETDEVICEID = "TelephonyManager-getDeviceId"

        /**
         * TelephonyManager-getSubscriberId（游客模式+缓存处理）
         * 游客返回空字符
         */
        val TELEPHONYMANAGER_GETSUBSCRIBERID = "TelephonyManager-getSubscriberId"

        /**
         * TelephonyManager-getImei（游客模式+缓存处理）
         * 游客返回空字符
         */
        val TELEPHONYMANAGER_GETIMEI = "TelephonyManager-getImei"

        /**
         * TelephonyManager-getSimSerialNumber（游客模式+缓存处理）
         * 游客返回空字符
         */
        val TELEPHONYMANAGER_GETSIMSERIALNUMBER = "TelephonyManager-getSimSerialNumber"

        /**
         * TelephonyManager-getLine1Number（游客模式+缓存处理）
         * 游客返回空字符
         */
        val TELEPHONYMANAGER_GETLINE1NUMBER = "TelephonyManager-getLine1Number"

        /**
         * TelephonyManager-getSimOperator（游客模式+缓存处理）
         * 游客返回空字符
         */
        val TELEPHONYMANAGER_GETSIMOPERATOR = "TelephonyManager-getSimOperator"

        /**
         * TelephonyManager-getNetworkOperator（游客模式+缓存处理）
         * 游客返回空字符
         */
        val TELEPHONYMANAGER_GETNETWORKOPERATOR = "TelephonyManager-getNetworkOperator"

        /**
         * TelephonyManager-getSimState（游客模式+缓存处理）
         * 游客返回SIM_STATE_UNKNOWN
         */
        val TELEPHONYMANAGER_GETSIMSTATE = "TelephonyManager-getSimState"
    }
}