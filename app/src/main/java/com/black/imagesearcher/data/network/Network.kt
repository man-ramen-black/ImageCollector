package com.black.imagesearcher.data.network

import com.black.imagesearcher.BuildConfig
import com.black.imagesearcher.data.model.NetworkResult
import com.black.imagesearcher.util.JsonUtil
import com.black.imagesearcher.util.Log
import com.black.imagesearcher.util.Util.ifThen
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import okio.Timeout
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Callback
import retrofit2.Converter
import retrofit2.HttpException
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.InterruptedIOException
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.net.UnknownHostException
import java.util.concurrent.CancellationException
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

/**
 * Retrofit 래핑 클래스
 */
class Network<T> private constructor(
    private val retrofit: Retrofit.Builder,
    private var client: OkHttpClient.Builder,
    private val serviceCls: Class<T>
) {
    companion object {
        private const val TIMEOUT_SEC = 30L

        internal const val STATUS_CODE_INVALID_URL = 9999

        // OkHttpClient 기본 설정
        private val defaultClient = OkHttpClient.Builder()
            .callTimeout(TIMEOUT_SEC, TimeUnit.SECONDS)
            .cache(null) // 캐싱하지 않음
            .retryOnConnectionFailure(false)
            .ifThen(BuildConfig.DEBUG) {
                // 로그 출력 설정
                addInterceptor(
                    HttpLoggingInterceptor().apply {
                        level = HttpLoggingInterceptor.Level.BODY
                    }
                )
            }

        /**
         * 네트워크 호출을 위한 Service 객체 반환
         */
        fun <T> service(baseUrl: String, serviceCls: Class<T>) : T {
            return builder(baseUrl, serviceCls).build()
        }

        /**
         * baseUrl을 사용하지 않는 경우 사용
         */
        fun <T> service(serviceCls: Class<T>) : T {
            return builder(serviceCls).build()
        }

        /**
         * Retrofit, OkHttpClient 등 커스텀할 수 있는 Network 객체 반환
         */
        fun <T> builder(baseUrl: String, serviceCls: Class<T>) : Network<T> {
            return createInstance(baseUrl, serviceCls)
        }

        /**
         * baseUrl을 사용하지 않는 경우 사용
         */
        fun <T> builder(serviceCls: Class<T>) : Network<T> {
            return createInstance(null, serviceCls)
        }

        private fun <T> createInstance(baseUrl: String?, serviceCls: Class<T>) : Network<T> {
            val retrofit = Retrofit.Builder()
                .addConverterFactory(
                    JsonConverterFactory(
                        GsonConverterFactory.create(JsonUtil.gson),
                        JsonUtil.kotlinSerialization.asConverterFactory("application/json".toMediaType())
                    )
                )
                // Response를 NetworkResult로 컨버팅해주는 CallAdapter 설정
                .addCallAdapterFactory(ResultCallAdapter.Factory)

            val client = try {
                // baseUrl을 사용하지 않더라도, 설정이 필요하므로 임의의 url 설정
                val nonNullBaseUrl = baseUrl ?: "https://abcd.net/"

                // baseUrl 마지막에 "/"가 누락되는 경우 Exception이 발생하므로 추가
                retrofit.baseUrl("$nonNullBaseUrl/")
                defaultClient
            }
            // Url이 비정상적인 경우 Exception 발생
            // 이 경우 Intercepter를 사용하여 API 호출 시 무조건 에러 response를 반환하도록 설정
            catch(e: Exception) {
                e.printStackTrace()

                // exception이 발생하지 않도록 임의의 url 설정
                retrofit.baseUrl("https://abcd.net/")
                defaultClient
                    .build()
                    .newBuilder()
                    .addInterceptor(object: Interceptor {
                        override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
                            return chain.proceed(chain.request())
                                .newBuilder()
                                .code(STATUS_CODE_INVALID_URL)
                                .message(e.message ?: "Exception")
                                .build()
                        }
                    })
            }

            return Network(retrofit, client, serviceCls)
        }
    }

    /**
     * OkHttpClient 커스텀
     */
    fun client(override: OkHttpClient.Builder.() -> Unit) : Network<T> = also {
        client = client.build().newBuilder()
        override(client)
    }

    /**
     * Retrofit 커스텀
     */
    fun retrofit(override: Retrofit.Builder.() -> Unit) : Network<T> = also {
        override(retrofit)
    }

    /**
     * Service 객체 반환
     */
    fun build() : T {
        return retrofit.client(client.build())
            .build()
            .create(serviceCls)
    }
}

/**
 * https://proandroiddev.com/modeling-retrofit-responses-with-sealed-classes-and-coroutines-9d6302077dfe
 * https://medium.com/shdev/retrofit%EC%97%90-calladapter%EB%A5%BC-%EC%A0%81%EC%9A%A9%ED%95%98%EB%8A%94-%EB%B2%95-853652179b5b
 */
class NetworkResultCall<T>(private val proxy: Call<T>, private val type: Type, private val annotations: Array<out Annotation>) : Call<NetworkResult<T>> {
    /**
     * 참고 : Service에 suspend fun으로 구현된 함수는 기본적으로 enqueue로 동작함
     */
    override fun enqueue(callback: Callback<NetworkResult<T>>) {
        proxy.enqueue(object: Callback<T> {
            override fun onResponse(call: Call<T>, response: Response<T>) {
                callback.onResponse(this@NetworkResultCall, Response.success(onResponse(response, type)))
            }

            override fun onFailure(call: Call<T>, t: Throwable) {
                callback.onResponse(this@NetworkResultCall, Response.success(onFailed(t)))
            }
        })
    }

    override fun execute(): Response<NetworkResult<T>> {
        val result = try {
            onResponse(proxy.execute(), type)
        } catch (t: Throwable) {
            onFailed(t)
        }
        return Response.success(result)
    }

    /**
     * 서버에서 Response를 받은 경우 처리(statusCode와 상관없음)
     */
    private fun onResponse(response: Response<*>?, type: Type) : NetworkResult<T> {
        // statusCode 200 ~ 300을 벗어난 경우 body가 null이고, errorBody에 데이터가 있음
        val result : Result<T?> = response?.body()
            ?.let {
                try {
                    @Suppress("UNCHECKED_CAST")
                    Result.success(it as T)
                } catch (e: ClassCastException) {
                    Result.failure(e)
                }
            }
            ?: parse(response?.errorBody()?.string(), type)

        val data = result.getOrNull()
        val exception = result.exceptionOrNull()
        val raw: okhttp3.Response? = response?.raw()

        return when {
            data != null -> {
                NetworkResult.Success(data, raw!!)
            }

            response?.code() == Network.STATUS_CODE_INVALID_URL -> {
                NetworkResult.Error(IllegalArgumentException("Invalid url : ${raw?.request?.url}"), raw)
            }

            exception != null -> {
                NetworkResult.Error(exception)
            }

            else -> {
                NetworkResult.Error(NoSuchFieldException("Response is null"), raw)
            }
        }
    }

    /**
     * 네트워크 요청 실패 시 처리
     */
    private fun onFailed(t: Throwable) : NetworkResult<T> {
        when(t) {
            // StatusCode가 2xx가 아닌 경우
            is HttpException -> {
                t.printStackTrace()
                return onResponse(t.response(), type)
            }

            // 타임아웃
            is InterruptedIOException  -> {
                t.printStackTrace()
                return NetworkResult.Error(TimeoutException("Timeout"))
            }

            // job.cancel
            is CancellationException -> {
                t.printStackTrace()
                return NetworkResult.Error(CancellationException())
            }

            // 네트워크 연결 끊김
            is UnknownHostException -> {
                t.printStackTrace()
                return NetworkResult.Error(t)
            }
        }

        return try {
            // throw해서 JsonUtil.tryFor로 예외 처리가 된다면 데이터 파싱 에러
            val result = if (KotlinSerialization.isKotlinSerialization(annotations)) {
                JsonUtil.tryForKS { throw t }
            } else {
                JsonUtil.tryForGson { throw t }
            }
            NetworkResult.Error(result.exceptionOrNull()!!)

        } catch (e: Exception) {
            // 그 외 에러는 알 수 없음
            e.printStackTrace()
            NetworkResult.Error(UnknownNetworkException(e))
        }
    }

    /**
     * Gson을 사용하여 String -> T로 변환
     */
    private fun parse(response: String?, type: Type) : Result<T?> {
        if (response == null) {
            Log.w("response is null")
            return Result.success(null)
        }

        return when {
            annotations.any { it.annotationClass == KotlinSerialization::class } -> {
                JsonUtil.tryForKS {
                    JsonUtil.fromNotNull(response, type, true)
                }
            }
            else -> {
                JsonUtil.tryForGson {
                    // https://stackoverflow.com/questions/32444863/google-gson-linkedtreemap-class-cast-to-myclass
                    // 제네릭으로 처리 시 클래스 정보가 손실되므로 type을 직접 전달하여 파싱
                    JsonUtil.from(response, type, false)
                }
            }
        }
    }

    override fun clone(): Call<NetworkResult<T>> = NetworkResultCall(proxy, type, annotations)
    override fun isExecuted(): Boolean = proxy.isExecuted
    override fun cancel() = proxy.cancel()
    override fun isCanceled(): Boolean = proxy.isCanceled
    override fun request(): Request = proxy.request()
    override fun timeout(): Timeout = proxy.timeout()
}

class ResultCallAdapter(private val resultType: Type, private val annotations: Array<out Annotation>): CallAdapter<Type, Call<NetworkResult<Type>>> {
    override fun responseType(): Type {
        return resultType
    }

    override fun adapt(call: Call<Type>): Call<NetworkResult<Type>> {
        return NetworkResultCall(call, resultType, annotations)
    }

    object Factory : CallAdapter.Factory() {
        override fun get(
            returnType: Type,
            annotations: Array<out Annotation>,
            retrofit: Retrofit
        ): CallAdapter<*, *>? {
            // Service의 Return 클래스가 Call인지 확인
            // 참고 : suspend fun인 경우 내부에서 Call로 감싸짐
            if (getRawType(returnType) != Call::class.java) {
                return null
            }

            // Call<#> -> #이 NetworkResult인지 확인
            val callType = getParameterUpperBound(0, returnType as ParameterizedType)
            if (getRawType(callType) != NetworkResult::class.java) {
                return null
            }

            // NetworkResult<#> -> # 클래스 타입 획득하여 CallAdapter 생성
            val resultType = getParameterUpperBound(0, callType as ParameterizedType)
            return ResultCallAdapter(resultType, annotations)
        }
    }
}

annotation class KotlinSerialization {
    companion object {
        fun isKotlinSerialization(annotations: Array<out Annotation>): Boolean {
            return annotations.any { it.annotationClass == KotlinSerialization::class }
        }
    }
}

/**
 * 참고: https://blog.mathpresso.com/%EC%8B%A0%EC%9E%85-%EC%95%88%EB%93%9C%EB%A1%9C%EC%9D%B4%EB%93%9C-%EA%B0%9C%EB%B0%9C%EC%9E%90%EC%9D%98-kotlinx-serialization-%EB%A6%AC%ED%8C%A9%ED%86%A0%EB%A7%81-%EC%84%9C%EC%82%AC%EC%8B%9C-740597911e2e
 */
class JsonConverterFactory(
    private val gsonConverterFactory: GsonConverterFactory,
    private val kotlinSerializationConverterFactory: Converter.Factory
) : Converter.Factory() {
    override fun requestBodyConverter(
        type: Type,
        parameterAnnotations: Array<out Annotation>,
        methodAnnotations: Array<out Annotation>,
        retrofit: Retrofit
    ): Converter<*, RequestBody>? {
        return when {
            KotlinSerialization.isKotlinSerialization(methodAnnotations) -> {
                kotlinSerializationConverterFactory
                    .requestBodyConverter(type, parameterAnnotations, methodAnnotations, retrofit)
            }
            else -> {
                gsonConverterFactory.requestBodyConverter(type, parameterAnnotations, methodAnnotations, retrofit)
            }
        }
    }
    override fun responseBodyConverter(
        type: Type,
        annotations: Array<out Annotation>,
        retrofit: Retrofit
    ): Converter<ResponseBody, *>? {
        return when {
            KotlinSerialization.isKotlinSerialization(annotations) -> {
                kotlinSerializationConverterFactory
                    .responseBodyConverter(type, annotations, retrofit)
            }
            else -> {
                gsonConverterFactory.responseBodyConverter(type, annotations, retrofit)
            }
        }
    }
}

class UnknownNetworkException(cause: Throwable) : Throwable(cause)