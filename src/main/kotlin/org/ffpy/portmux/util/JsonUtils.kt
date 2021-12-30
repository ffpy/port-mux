package org.ffpy.portmux.util

import blue.endless.jankson.Jankson
import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import java.io.IOException
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
        val filteredJson = Jankson.builder()
            .build()
            .load(path.toFile())
            .toJson()
        return createGson().fromJson(filteredJson, type)
    }

    private fun createGson() = GsonBuilder()
        .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
        .create()
}