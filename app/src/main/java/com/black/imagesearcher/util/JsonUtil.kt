package com.black.imagesearcher.util

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonParseException
import com.google.gson.reflect.TypeToken
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import org.json.JSONException
import org.json.JSONObject
import java.lang.reflect.Type

/**
 * Json 처리 유틸
 **/
object JsonUtil {
    /**
     * Kotlin Serialization 객체
     * https://github.com/Kotlin/kotlinx.serialization/blob/master/docs/json.md#json-configuration
     */
    @OptIn(ExperimentalSerializationApi::class)
    val kotlinSerialization : Json by lazy {
        Json {
            // ""이 빠진 값도 String으로 인식하는 등 구문 분석을 너프하게 하도록 설정
            isLenient = true
            // 클래스와 매치되지 않는 key 무시
            ignoreUnknownKeys = true
            // key 값이 없는 경우 변수에 null로 입력되도록 설정
            explicitNulls = false
            // json string 파싱 시 디폴트 값도 반영
            encodeDefaults = true
            // 데이터로 파싱 시 non-null 변수에 null을 설정하려고 하는 경우 default value로 대체
            // 참고 : json value가 타입에 맞지 않는 경우 등엔 동작하지 않음(Exception 발생)
            coerceInputValues = true
            prettyPrint = true
        }
    }

    /**
     * Gson 객체
     */
    val gson : Gson by lazy {
        GsonBuilder()
            .setPrettyPrinting()
            .create()
    }

    /**
     * get data from Json
     */
    fun <T> from(json: String, cls: Class<T>, isKotlinSerialize: Boolean = false): T? {
        return if (isKotlinSerialize) {
            @Suppress("UNCHECKED_CAST")
            tryForKS {
                val serializer = (kotlinSerialization.serializersModule.serializer(cls) as? KSerializer<T>)
                    ?: return@tryForKS null
                kotlinSerialization.decodeFromString(serializer, json)
            }
        } else {
            tryForGson {
                gson.fromJson(json, cls)
            }
        }
    }

    fun <T> from(json: String, type: Type, isKotlinSerialize: Boolean = false): T? {
        return if (isKotlinSerialize) {
            @Suppress("UNCHECKED_CAST")
            tryForKS {
                val serializer = (kotlinSerialization.serializersModule.serializer(type) as? KSerializer<T>)
                    ?: return@tryForKS null
                kotlinSerialization.decodeFromString(serializer, json)
            }
        } else {
            tryForGson {
                gson.fromJson(json, type)
            }
        }
    }

    /**
     * get data from Json
     */
    inline fun <reified T> from(json: String, isKotlinSerialize: Boolean = false): T? {
        return if (isKotlinSerialize) {
            tryForKS {
                kotlinSerialization.decodeFromString(json)
            }
        } else {
            tryForGson {
                gson.fromJson(json, object: TypeToken<T>(){}.type)
            }
        }
    }

    /**
     * parse data to JsonString
     */
    fun <T> to(data: T, type: Type, isKotlinSerialize: Boolean = false): String? {
        return if (isKotlinSerialize) {
            @Suppress("UNCHECKED_CAST")
            tryForKS {
                val serializer = (kotlinSerialization.serializersModule.serializer(type) as? KSerializer<T>)
                    ?: return@tryForKS null
                kotlinSerialization.encodeToString(serializer, data)
            }
        } else {
            tryForGson {
                gson.toJson(data, type)
            }
        }
    }

    /**
     * parse data to JsonString
     */
    inline fun <reified T> to(data: T, isKotlinSerialize: Boolean = false): String? {
        return if (isKotlinSerialize) {
            tryForKS {
                kotlinSerialization.encodeToString(data)
            }
        } else {
            tryForGson {
                gson.toJson(data)
            }
        }
    }

    fun <T> tryForGson(block: () -> T?): T? {
        return try {
            block()
        } catch (e: JsonParseException) {
            e.printStackTrace()
            null
        } catch (e: JsonParseException) {
            e.printStackTrace()
            null
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
            null
        } catch (e: ClassCastException) {
            e.printStackTrace()
            null
        }
    }

    fun <T> tryForKS(block: () -> T?): T? {
        return try {
            block()
        } catch (e: SerializationException) {
            e.printStackTrace()
            null
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
            null
        }
    }

    fun Map<*, *>.toJSONObject(): JSONObject
            = try {
        JSONObject(JsonUtil.to(this)?:"{}")
    } catch (e: JSONException) {
        e.printStackTrace()
        JSONObject()
    }
}