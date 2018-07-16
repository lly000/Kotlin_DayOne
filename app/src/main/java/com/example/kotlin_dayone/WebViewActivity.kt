package com.example.kotlin_dayone

import android.view.KeyEvent
import android.view.KeyEvent.KEYCODE_BACK
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.RelativeLayout
import com.example.kotlin_dayone.base.BaseActivity
import org.json.JSONObject

class WebViewActivity : BaseActivity() {

    private lateinit var mWebView: WebView

    private lateinit var mLayout: RelativeLayout

    var intsteps: Int = 0

    override fun setContentView() {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        setContentView(R.layout.activity_webview)
    }

    override fun initView() {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        mWebView = findViewById(R.id.webView)
        mLayout = findViewById(R.id.layout)
    }

    override fun initData() {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        //激活webview为活跃状态，能正常执行网页的响应
        mWebView.onResume()

        /**
         * 当页面被失去焦点被切换到后台后不可见状态，需要执行onPause()
         * 通过onPause()动作通知内核暂停所有动作，比如DOM的解析、plugin的执行、JavaScript执行。
         */
        mWebView.onPause()

        /**
         * 当应用程序（存在webview）被切换到后台时，这个方法不仅仅针对当前的webView
         * 而是全局的全应用程序的webView，他会在哪听所有webView的layout,parsing,javascripttimer,
         * 降低cpu功耗
         */
        mWebView.pauseTimers()
        //恢复pauseTimers状态
        mWebView.resumeTimers()
        /**
         * 销毁webView，在关闭activity时，如果webView的音乐和视频，还在播放，就必须销毁wenView
         * 但是注意：webView调用destory时，webView仍绑定在activity上
         * 这是由于自定义webView构建时传入了该activity的context对象
         * 因此需要先从父容器中移除webView，然后再销毁webView
         */
        //再父容器中移除webView
        mLayout.removeView(mWebView)
        //销毁webView
        mWebView.destroy()

        //判断webView是否可以后退返回值为boolean
        mWebView.canGoBack()
        //后退网页
        mWebView.goBack()
        //判断webView是否可以前进返回值为boolean
        mWebView.canGoForward()
        //前进网页
        mWebView.goForward()

        /**
         * 以当前的index为起点，前进或后退到历史
         * 记录中指定的intsteps，如果intsteps为负数则为后退
         * 正数则为前进
         */
        mWebView.goBackOrForward(intsteps)

        /**
         * 清除网页访问留下的缓存
         * 优于内核缓存是全局的因此这个方法不仅仅正对webView而是针对整个应用程序
         */
        mWebView.clearCache(true)
        /**
         * 清除当前webView访问的历史纪录
         * 只会webView访问历史纪录里的所有记录除了当前访问记录
         */
        mWebView.clearHistory()
        /**
         * 这个api仅仅清除自动完成填充的表单数据，并不会清除webView存储到本地的数据
         */
        mWebView.clearFormData()

        //声明websettings子类
        var webSettings: WebSettings = mWebView.settings
        //如果访问的页面中要与javascript交互，则webView必须设置支持javascript
        webSettings.javaScriptEnabled = true

        //设置自适应屏幕，两者合用
        webSettings.useWideViewPort = true//将图片调整到适合webView的大小
        webSettings.loadWithOverviewMode = true//缩放至屏幕的大小

        //缩放操作
        webSettings.setSupportZoom(true)//支持缩放，默认为true，是下面哪个的前提
        webSettings.allowFileAccess=true//设置内置的缩放控件，若为fase则webView不可缩放
        webSettings.displayZoomControls=false//隐藏原生的缩放控件

        //其他细节操作
        webSettings.cacheMode=WebSettings.LOAD_CACHE_ELSE_NETWORK//关闭webView中的缓存
        webSettings.allowFileAccess=true//设置可以访问文件
        webSettings.javaScriptCanOpenWindowsAutomatically=true//支持通过js打开窗口
        webSettings.defaultTextEncodingName="utf-8"//设置编码格式

        //优先使用缓存
        mWebView.settings.cacheMode=WebSettings.LOAD_CACHE_ELSE_NETWORK
        /**
         * 缓存模式如下
         * LOAD_CACHE_ONLY:不使用网络，只读取本地缓存数据
         * LOAD_DEFAULT:(默认)根据cache-control决定是否从网络上去数据
         * LOAD_NO_CACHE:不使用缓存，只从网络获取数据
         * LOAD_CACHE_ELSE_NETWORK:只要本地有，无论是否过期，或者one-cache，都使用缓存中的数据
         */
        mWebView.webViewClient=WebViewClient()

        mWebView.addJavascriptInterface(JSONObject(),"myobj")
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if ((keyCode == KEYCODE_BACK) && mWebView.canGoBack()) {
            mWebView.goBack()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

}