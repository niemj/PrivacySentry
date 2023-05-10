package com.yl.lib.sentry.hook

import android.app.Application
import android.content.Context
import android.os.Handler
import android.os.Looper
import com.yl.lib.sentry.hook.cache.DiskCache
import com.yl.lib.sentry.hook.printer.BaseFilePrinter
import com.yl.lib.sentry.hook.printer.BasePrinter
import com.yl.lib.sentry.hook.printer.DefaultFilePrint
import com.yl.lib.sentry.hook.printer.PrintCallBack
import com.yl.lib.sentry.hook.util.PrivacyLog
import com.yl.lib.sentry.hook.util.PrivacyUtil
import com.yl.lib.sentry.hook.util.PrivacyUtil.Util.getApplicationByReflect
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.util.concurrent.atomic.AtomicBoolean

/**
 * @author yulun
 * @sinice 2021-09-24 14:33
 */
class PrivacySentry {
    object Privacy {
        @Volatile
        private var mBuilder: PrivacySentryBuilder? = PrivacySentryBuilder()
        private val bInit = AtomicBoolean(false)
        private val bFilePrintFinish = AtomicBoolean(false)
        private var bShowPrivacy: AtomicBoolean? = null
        private var ctx: Application? = null
        private val diskCache: DiskCache by lazy {
            DiskCache()
        }

        /**
         *  transform简单初始化，需要搭配插件使用
         */
        fun initTransform(ctx: Application) {
            var builder = PrivacySentryBuilder()
            init(ctx, builder)
        }

        /**
         *  完整版初始化
         */
        fun init(
            ctx: Application, builder: PrivacySentryBuilder?
        ) {
            if (bInit.compareAndSet(false, true)) {
                mBuilder = builder
                initInner(ctx)
            }
        }

        private fun initInner(ctx: Application) {
            PrivacyLog.i("call initInner")
            this.ctx = ctx
            //添加默认缓存状态函数列表
            mBuilder?.addLoadCacheList(defaultLoadCacheState(), false)
            //添加默认白名单敏感函数列表
            mBuilder?.addWhiteMethods(defaultWhiteMethodsList(), false)
            if (mBuilder?.isEnableFileResult() == true || mBuilder?.debug == true) {
                if (mBuilder?.isEnableFileResult() == true) {
                    mBuilder?.getWatchTime()?.let {
                        PrivacyLog.i("delay stop watch $it")
                        var handler = Handler(Looper.getMainLooper())
                        handler.postDelayed({
                            stop()
                        }, it)
                    }
                }
                mBuilder?.addPrinter(defaultFilePrinter(ctx, mBuilder))
            }
        }

        fun hasInit(): Boolean {
            return bInit.get()
        }

        /**
         * 停止文件写入
         */
        fun stop() {
            if (!isFilePrintFinish()) {
                PrivacyLog.i("call stopWatch")
                var filePrinterList =
                    mBuilder?.getPrinterList()?.filterIsInstance<BaseFilePrinter>()
                var printerSize = filePrinterList?.size ?: 0
                mBuilder?.getPrinterList()?.filterIsInstance<BaseFilePrinter>()?.forEach {
                    // 强制写入文件
                    GlobalScope.launch(Dispatchers.IO) {
                        it.flushToFile()
                        printerSize--
                        if (printerSize == 0) {
                            bFilePrintFinish.set(true)
                        }
                    }
                    // 结果回调
                    mBuilder?.getResultCallBack()?.onResultCallBack(it.resultFileName)
                }
            }

        }

        /**
         * 记录展示隐私协议，调用时机一般为 隐私协议点击确定的时候，必须调用
         */
        fun updatePrivacyShow() {
            if (bShowPrivacy?.get() == true) {
                return
            }
            PrivacyLog.i("call updatePrivacyShow")
            if (bShowPrivacy == null) {
                bShowPrivacy = AtomicBoolean(true)
            } else {
                bShowPrivacy?.compareAndSet(false, true)
            }
            diskCache.put("show_privacy_dialog", "true")
            mBuilder?.getPrinterList()?.filterIsInstance<BaseFilePrinter>()
                ?.forEach { it.appendData("点击隐私协议确认", "点击隐私协议确认", "点击隐私协议确认") }
        }

        fun hasShowPrivacy(): Boolean {
            if (bShowPrivacy == null) {
                // getContext() = null 代表什么？
                // privacy还未初始化，而且是在attachBaseContext里调用
                if (getContext() == null) {
                    // 这里返回ture，是尽量不影响三方SDK的使用
                    return true
                } else {
                    bShowPrivacy =
                        AtomicBoolean(
                            diskCache.get(
                                "show_privacy_dialog",
                                "false"
                            ).second == "true"
                        )

                }
            }
            return bShowPrivacy?.get() ?: false
        }

        fun isDebug(): Boolean {
            return mBuilder?.debug ?: true
        }

        fun getContext(): Application? {
            return ctx ?: getApplicationByReflect() ?: null
        }

        fun getBuilder(): PrivacySentryBuilder? {
            return mBuilder ?: null
        }

        fun inDangerousState(key: String): Boolean {
            // 游客模式白名单
            if (isWhiteMethod(key)) {
                return false
            }
            // 未同意隐私协议
            if (!hasShowPrivacy()) {
                return true
            }
            // 游客模式
            if (mBuilder?.isVisitorModel() == true) {
                return true
            }
            // 危险函数
            if (isDangerousMethod(key)) {
                return true
            }
            return false
        }

        fun isLoadFromCache(method: String): Boolean {
            for (item in mBuilder?.getLoadCacheList()!!) {
                if (method.contains(item.first)) {
                    return item.second
                }
            }
            return true
        }

        /**
         * 当前写入文件任务是否结束
         * @return Boolean
         */
        fun isFilePrintFinish(): Boolean {
            return bFilePrintFinish.get()
        }

        /**
         * 关闭游客模式
         */
        fun closeVisitorModel() {
            PrivacyLog.i("closeVisitorModel")
            mBuilder?.configVisitorModel(false)
            mBuilder?.getPrinterList()?.forEach {
                it.filePrint(
                    "closeVisitorModel",
                    "关闭游客模式",
                    "关闭游客模式"
                )
            }
        }

        /**
         * 打开游客模式
         */
        fun openVisitorModel() {
            PrivacyLog.i("openVisitorModel")
            mBuilder?.configVisitorModel(true)
            mBuilder?.getPrinterList()?.forEach {
                it.filePrint(
                    "openVisitorModel",
                    "打开游客模式",
                    "打开游客模式"
                )
            }
        }

        /**
         * 是否用缓存返回的函数列表(不使用缓存返回)
         */
        private fun defaultLoadCacheState(): List<Pair<String, Boolean>> {
            val list = ArrayList<Pair<String, Boolean>>()
            //放开bssid ,ssdid的缓存，这个会导致腾讯定位出问题
            list.add(Pair(PrivacySentryConstant.WIFIINFO_GETSSID, false))
            list.add(Pair(PrivacySentryConstant.WIFIINFO_GETBSSID, false))
            list.add(Pair(PrivacySentryConstant.WIFIINFO_GETIPADDRESS, false))
            //放开wifiEnable缓存，不再走缓存判断
            list.add(Pair(PrivacySentryConstant.WIFIMANAGER_ISWIFIENABLED, false))
            return list
        }

        /**
         * 游客模式白名单（仅代理）
         */
        private fun defaultWhiteMethodsList(): List<String> {
            val list = ArrayList<String>()
            //权限请求
            list.add(PrivacySentryConstant.REQUESTPERMISSIONS)
            //游客返回null,有风险
            list.add(PrivacySentryConstant.WIFIMANAGER_GETDHCPINFO)
            //ip地址只做代理，不再拦截
            list.add(PrivacySentryConstant.INET4ADDRESS_GETADDRESS)
            list.add(PrivacySentryConstant.INETADDRESS_GETADDRESS)
            list.add(PrivacySentryConstant.INET4ADDRESS_GETHOSTADDRESS)
            list.add(PrivacySentryConstant.INETADDRESS_GETHOSTADDRESS)
            //contentResolver的方法，只做代理，不再拦截
            list.add(PrivacySentryConstant.CONTENTRESOLVER_QUERY)
            list.add(PrivacySentryConstant.CONTENTRESOLVER_INSERT)
            list.add(PrivacySentryConstant.CONTENTRESOLVER_UPDATE)
            list.add(PrivacySentryConstant.CONTENTRESOLVER_DELETE)
            //Location游客返回null,有风险
            list.add(PrivacySentryConstant.LOCATIONMANAGER_GETLASTKNOWNLOCATION)
            //sdk根目录游客返回null,有风险
            list.add(PrivacySentryConstant.ENVIRONMENT_GETEXTERNALSTORAGEDIRECTORY)
            //放开package信息读取,只做代理，不再拦截
            list.add(PrivacySentryConstant.PACKAGEMANAGER_GETPACKAGEINFO)
            list.add(PrivacySentryConstant.PACKAGEMANAGER_GETINSTALLEDPACKAGESASUSER)
            list.add(PrivacySentryConstant.PACKAGEMANAGER_GETINSTALLEDAPPLICATIONS)
            list.add(PrivacySentryConstant.PACKAGEMANAGER_GETINSTALLEDAPPLICATIONSASUSER)
            return list
        }

        private fun defaultFilePrinter(
            ctx: Context,
            builder: PrivacySentryBuilder?
        ): List<BasePrinter> {
            var fileName = builder?.getResultFileName() ?: "privacy_result_${
                PrivacyUtil.Util.formatTime(
                    System.currentTimeMillis()
                )
            }"
            PrivacyLog.i("print fileName is $fileName")
            return listOf(
                DefaultFilePrint(
                    "${ctx.getExternalFilesDir(null)}${File.separator}privacy${File.separator}$fileName.xls",
                    printCallBack = object : PrintCallBack {
                        override fun checkPrivacyShow(): Boolean {
                            return hasShowPrivacy()
                        }

                        override fun stopWatch() {
                            PrivacyLog.i("stopWatch")
                            Privacy.stop()
                        }
                    }, watchTime = builder?.getWatchTime()
                )
            )
        }

        private fun isWhiteMethod(method: String): Boolean {
            for (item in mBuilder?.getWhiteMethodsList()!!) {
                if (method.contains(item)) {
                    return true
                }
            }
            return false
        }

        private fun isDangerousMethod(method: String): Boolean {
            for (item in mBuilder?.getDangerousList()!!) {
                if (method.contains(item)) {
                    return true
                }
            }
            return false
        }

    }
}

/**
 * 检测结果回调，业务方自行处理
 */
public interface PrivacyResultCallBack {
    fun onResultCallBack(filePath: String)
}

/**
 * 游客模式回调，业务方自行处理
 */
public interface VisitorModelCallBack {
    fun onVisitorCallBack(
        funName: String,
        methodDocumentDesc: String = "", msg: String = ""
    )
}
