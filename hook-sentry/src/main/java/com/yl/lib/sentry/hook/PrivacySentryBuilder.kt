package com.yl.lib.sentry.hook

import com.yl.lib.sentry.hook.printer.BasePrinter
import com.yl.lib.sentry.hook.printer.DefaultLogPrint
import com.yl.lib.sentry.hook.util.MainProcessUtil

/**
 * @author yulun
 * @sinice 2021-09-24 15:07
 */
class PrivacySentryBuilder {

    // 默认需要关闭
    @Volatile
    var debug: Boolean = false

    //日志输出 和 文件输出
    private var mPrinterList: ArrayList<BasePrinter>? = null

    // 默认的监听时间
    private var watchTime: Long = 3 * 60 * 1000

    // 游客模式回调
    private var visitorModelCallBack: VisitorModelCallBack? = null

    // 结束回调
    private var privacyResultCallBack: PrivacyResultCallBack? = null

    // 输出的文件名
    private var resultFileName: String? = null

    // 是否激活输入日志到文件
    private var enableFileResult: Boolean = true

    // 游客模式，拦截所有敏感方法，默认关闭
    @Volatile
    private var visitorModel: Boolean = false

    // 可以拦截读取系统剪贴板
    @Volatile
    private var enableReadClipBoard: Boolean = true

    // 白名单，不受游客模式限制
    private var mWhiteMethodsList: ArrayList<String>? = null

    // 敏感函数列表
    private var mDangerousList: ArrayList<String>? = null

    // 可以取缓存值的函数列表
    private var mLoadCacheList: ArrayList<Pair<String, Boolean>>? = null

    constructor() {
        mDangerousList = ArrayList()
        mLoadCacheList = ArrayList()
        mWhiteMethodsList = ArrayList()
        addPrinter(DefaultLogPrint())
    }

    fun getPrinterList(): ArrayList<BasePrinter>? {
        return mPrinterList
    }

    fun getWatchTime(): Long? {
        return watchTime
    }

    fun getWhiteMethodsList(): List<String>? {
        return mWhiteMethodsList
    }

    fun getDangerousList(): List<String>? {
        return mDangerousList
    }

    fun getLoadCacheList(): List<Pair<String, Boolean>>? {
        return mLoadCacheList
    }

    fun getResultCallBack(): PrivacyResultCallBack? {
        return privacyResultCallBack
    }

    fun getVisitorCallBack(): VisitorModelCallBack? {
        return visitorModelCallBack
    }

    fun getResultFileName(): String? {
        // 这里可能是多进程
        return if (MainProcessUtil.MainProcessChecker.isMainProcess(PrivacySentry.Privacy.getContext())) {
            resultFileName
        } else {
            var processName = PrivacySentry.Privacy.getContext()?.let {
                MainProcessUtil.MainProcessChecker.getProcessName(PrivacySentry.Privacy.getContext()!!)
            } ?: ""
            "${processName}_$resultFileName"
        }
    }

    fun addPrinter(basePrinter: BasePrinter): PrivacySentryBuilder {
        if (mPrinterList == null) {
            mPrinterList = ArrayList()
        }
        mPrinterList?.add(basePrinter)
        return this
    }

    fun addPrinter(basePrinter: List<BasePrinter>): PrivacySentryBuilder {
        if (mPrinterList == null) {
            mPrinterList = ArrayList()
        }
        mPrinterList?.addAll(basePrinter)
        return this
    }

    fun addLoadCacheList(pair: Pair<String, Boolean>): PrivacySentryBuilder {
        if (mLoadCacheList == null) {
            mLoadCacheList = ArrayList()
        }
        mLoadCacheList?.add(pair)
        return this
    }

    fun addLoadCacheList(
        pairs: List<Pair<String, Boolean>>,
        isClear: Boolean
    ): PrivacySentryBuilder {
        if (mLoadCacheList == null) {
            mLoadCacheList = ArrayList()
        }
        if (isClear) {
            mLoadCacheList?.clear()
        }
        mLoadCacheList?.addAll(pairs)
        return this
    }

    fun addWhiteMethods(method: String): PrivacySentryBuilder {
        if (mWhiteMethodsList == null) {
            mWhiteMethodsList = ArrayList()
        }
        mWhiteMethodsList?.add(method)
        return this
    }

    fun addWhiteMethods(methods: List<String>, isClear: Boolean): PrivacySentryBuilder {
        if (mWhiteMethodsList == null) {
            mWhiteMethodsList = ArrayList()
        }
        if (isClear) {
            mWhiteMethodsList?.clear()
        }
        mWhiteMethodsList?.addAll(methods)
        return this
    }

    fun addDangerous(dangerous: String): PrivacySentryBuilder {
        if (mDangerousList == null) {
            mDangerousList = ArrayList()
        }
        mDangerousList?.add(dangerous)
        return this
    }

    fun addDangerous(dangerous: List<String>): PrivacySentryBuilder {
        if (mDangerousList == null) {
            mDangerousList = ArrayList()
        }
        mDangerousList?.addAll(dangerous)
        return this
    }

    fun syncDebug(debug: Boolean): PrivacySentryBuilder {
        this.debug = debug
        return this
    }

    fun configWatchTime(watchTime: Long): PrivacySentryBuilder {
        this.watchTime = watchTime
        return this
    }

    fun configVisitorCallBack(visitorModelCallBack: VisitorModelCallBack?): PrivacySentryBuilder {
        this.visitorModelCallBack = visitorModelCallBack
        return this
    }

    fun configResultCallBack(privacyResultCallBack: PrivacyResultCallBack?): PrivacySentryBuilder {
        this.privacyResultCallBack = privacyResultCallBack
        return this
    }

    fun configResultFileName(resultFileName: String): PrivacySentryBuilder {
        this.resultFileName = resultFileName
        return this
    }

    fun configVisitorModel(visitorModel: Boolean): PrivacySentryBuilder {
        this.visitorModel = visitorModel
        return this
    }

    fun isVisitorModel(): Boolean {
        return visitorModel
    }

    fun enableFileResult(enableFileResult: Boolean): PrivacySentryBuilder {
        this.enableFileResult = enableFileResult
        return this
    }

    fun isEnableFileResult(): Boolean {
        return enableFileResult
    }

    fun enableReadClipBoard(enableReadClipBoard: Boolean): PrivacySentryBuilder {
        this.enableReadClipBoard = enableReadClipBoard
        return this
    }

    fun isEnableReadClipBoard(): Boolean {
        return enableReadClipBoard
    }

}
