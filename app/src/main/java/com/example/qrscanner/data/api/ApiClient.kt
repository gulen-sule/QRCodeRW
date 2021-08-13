package com.example.qrscanner.data.api

import android.annotation.SuppressLint
import android.util.Log
import com.example.qrscanner.BuildConfig
import com.example.qrscanner.Const
import com.example.qrscanner.applications.QrScannerApplication
import com.google.gson.GsonBuilder
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

@Suppress("UNCHECKED_CAST")
class ApiClient {

    private var mInstance: ApiService? = null
    private var retrofit: Retrofit? = null
    private var okHttpClient: OkHttpClient? = null
    private var baseUrl: String? = null

    private fun initConfiguration() {
        baseUrl = Const.BASE_URL
    }

    @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    private fun <T> createRetrofitService(): T? {
        if (retrofit == null) {
            try {
                //Log.d("tokenTAG",QrScannerApplication.getInstance().getToken().toString())
                val interceptor = Interceptor { chain: Interceptor.Chain ->
                    val requestBuilder = chain.request().newBuilder()
                    requestBuilder.addHeader("Content-Type", "application/json")
                        .addHeader("Authorization", "Bearer " + QrScannerApplication.getInstance().getToken())
                        .addHeader("Accept", "*")
                        .addHeader("Expect", "application/json")
                        .addHeader("X-Requested-With", "XMLHttpRequest")
                        //.addHeader("User-agent", Const.USER_AGENT)
                    val request = requestBuilder.build()
                    chain.proceed(request)
                }

                val gson = GsonBuilder()
                    .setLenient() //.registerTypeHierarchyAdapter(Collection.class, new CollectionAdapter())
                    .create()

                okHttpClient = OkHttpClient()
                val builder = OkHttpClient.Builder()
                builder.addInterceptor(interceptor)

                try {
                    if (BuildConfig.DEBUG || !BuildConfig.DEBUG) {
                        val loggingInterceptor = HttpLoggingInterceptor()
                        loggingInterceptor.apply { loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY }
                        builder.addInterceptor(loggingInterceptor)
                    }
                    if (!BuildConfig.DEBUG || BuildConfig.DEBUG) {
                        val trustAllCerts = arrayOf<TrustManager>(@SuppressLint("CustomX509TrustManager")
                        object : X509TrustManager {
                            @SuppressLint("TrustAllX509TrustManager")
                            @Throws(CertificateException::class)
                            override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {
                            }

                            @SuppressLint("TrustAllX509TrustManager")
                            @Throws(CertificateException::class)
                            override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {
                            }

                            override fun getAcceptedIssuers(): Array<X509Certificate> {
                                return arrayOf()
                            }
                        })

                        val sslContext = SSLContext.getInstance("SSL")
                        sslContext.init(null, trustAllCerts, SecureRandom())
                        val sslSocketFactory = sslContext.socketFactory
                        builder.sslSocketFactory(sslSocketFactory, (trustAllCerts[0] as X509TrustManager))
                        builder.hostnameVerifier { _, _ ->
                            true
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                builder.connectTimeout(20, TimeUnit.SECONDS)
                builder.readTimeout(20, TimeUnit.SECONDS)

                okHttpClient = builder.build()

                retrofit = Retrofit.Builder()
                    .baseUrl(baseUrl)  //baseUrl
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .client(okHttpClient)
                    .build()
            } catch (e: Exception) {
                println(e)
            }
        }

        mInstance = retrofit!!.create(ApiService::class.java)

        return mInstance as T?
    }

    fun <T : Any> fetch(
        request: Observable<Response<T?>>,
        success: (response: T, code: Int, message: String) -> Unit,
        failure: () -> Unit,
        loading: (status: Boolean) -> Unit,
    ) {

        request.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .safeSubscribe(object : Observer<Response<T?>> {

                override fun onSubscribe(d: Disposable) {}

                override fun onNext(response: Response<T?>) {
                    when (response.code()) {
                        in 200..301 -> {
                            success(response.body() as T, response.code(), response.message().toString())
                            loading(false)
                            Log.d("fetchTAG","code: 200")
                        }
                        401 -> {// token expire oldugunda bu code doner databasei guncelle
                            response.errorBody()?.let {
                                failure()
                            }
                            Log.d("fetchTAG","code: 401")
                            loading(false)
                            //unAuthenticated()
                        }
                        400, 404, 500 -> {
                            response.errorBody()?.let {
                                failure()
                            }
                            Log.d("fetchTAG","code: 400 ... 500 ")
                            loading(false)
                        }
                        else -> {
                            Log.d("fetchTAG","code: 500 sonrasi")
                            response.errorBody()?.let {
                                failure()
                            }
                            loading(false)
                        }
                    }
                }

                override fun onError(e: Throwable) {
                    try {
                        failure()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    } finally {
                        loading(false)
                    }
                }

                override fun onComplete() {
                    //finished()
                }
            })
    }

    val service: ApiService
        get() {
            initConfiguration()
            if (mInstance == null) {
                mInstance = setInstance()
            }
            return mInstance!!
        }

    private fun setInstance(): ApiService? {
        mInstance = null
        mInstance = createRetrofitService<ApiService>()
        return mInstance
    }


    companion object {
        private var instance: ApiClient? = null

        fun instance(): ApiClient {
            if (instance == null)
                return ApiClient()
            return instance as ApiClient
        }
    }
}
