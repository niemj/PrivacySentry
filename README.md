# PrivacySentry
    android隐私合规检测
    
## 如何使用
    
```
    1. 在根目录的build.gralde下添加
	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```

    

```
    2. 在项目中的build.gralde下添加
    implementation 'com.github.allenymt:PrivacySentry:0.0.2'
```


```
    简易版初始化
    在代码中调用，越早越好，建议在application中调用
    kotlin:PrivacySentry.Privacy.init(this)
    java:PrivacySentry.Privacy.INSTANCE.init(this);
```


```
    完成功能的初始化
     // 完整版配置
        var builder = PrivacySentryBuilder()
            // 自定义文件结果的输出名
            .configResultFileName("demo_test")
            //自定义检测时间，也支持主动停止检测 PrivacySentry.Privacy.stopWatch()
            .configWatchTime(5 * 60 * 1000)
            // 文件输出后的回调
            .configResultCallBack(object : PrivacyResultCallBack {
                override fun onResultCallBack(filePath: String) {
                    PrivacyLog.i("result file patch is $filePath")
                }
            })
        // 添加默认结果输出，包含log输出和文件输出
        builder.addPrinter(PrivacySentry.Privacy.defaultPrinter(this, builder))
        PrivacySentry.Privacy.init(this, PrivacySentry.Privacy.defaultConfigHookBuilder(builder))
        
        
        java
         // 完整版配置
        PrivacySentryBuilder builder = new PrivacySentryBuilder()
                // 自定义文件结果的输出名
                .configResultFileName("buyer_privacy")
                //自定义检测时间，也支持主动停止检测 PrivacySentry.Privacy.stopWatch()
                .configWatchTime(30 * 1000)
                // 文件输出后的回调
                .configResultCallBack(new PrivacyResultCallBack() {

                    @Override
                    public void onResultCallBack(@NonNull String s) {

                    }
                });
        // 添加默认结果输出，包含log输出和文件输出
        builder.addPrinter(PrivacySentry.Privacy.INSTANCE.defaultPrinter(this, builder));
        PrivacySentry.Privacy.INSTANCE.init(this, PrivacySentry.Privacy.INSTANCE.defaultConfigHookBuilder(builder));
```


```
    在隐私协议确认的时候调用
    kotlin:PrivacySentry.Privacy.updatePrivacyShow()
    java:PrivacySentry.Privacy.INSTANCE.updatePrivacyShow();
```



## 隐私方法调用结果产出
-     默认拦截隐私方法时间为1分钟，支持自定义设置时间。
-     排查结果可参考目录下的demo_result.xls，排查结果支持两个维度查看，第一是结合隐私协议的展示时机和敏感方法的调用时机，第二是统计所有敏感函数的调用次数
-     排查结果可观察日志，结果文件会在 /storage/emulated/0/Android/data/yourPackgeName/cache/xx.xls，需要手动执行下adb pull

## 基本原理
-     一期是运行期基于动态代理hook系统关键函数实现，二期计划是编译期代码插桩实现
-     为什么不用xposed等框架？ 因为想做本地自动化定期排查，第三方hook框架外部依赖性太大
-     为什么不搞基于lint的排查方式？ 工信部对于运行期 敏感函数的调用时机和次数都有限制，代码扫描解决不了这些问题


## 结语
    整体代码很简单，有问题可以直接提~