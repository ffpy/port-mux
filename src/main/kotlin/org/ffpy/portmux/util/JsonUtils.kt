package org.ffpy.portmux.util

import a2u.tn.utils.json.TnJson
import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.NoSuchFileException
import java.nio.file.Path

/**
 * JSON工具类
 */
object JsonUtils {

    /**
     * JSON文件转对象
     *
     * @param path JSON文件路径
     * @param type 要转换的对象类型
     * @param T 对象类型
     * @return 转换后的对象
     * @throws NoSuchFileException 如果找不到文件
     * @throws IOException 如果读取文件出错
     */
    @Throws(NoSuchFileException::class, IOException::class)
    fun <T> parse(path: Path, type: Class<T>): T {
        return parse(Files.readString(path, StandardCharsets.UTF_8), type)
    }

    /**
     * JSON字符串转对象
     *
     * @param data JSON字符串
     * @param type 要转换的对象类型
     * @param T 对象类型
     * @return 转换后的对象
     */
    fun <T> parse(data: String, type: Class<T>): T {
        val gson = createGson()
        return gson.fromJson(gson.toJson(TnJson.parse(data)), type)
    }

    private fun createGson() = GsonBuilder()
        .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
        .create()
}