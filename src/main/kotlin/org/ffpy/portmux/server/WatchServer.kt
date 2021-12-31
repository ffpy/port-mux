package org.ffpy.portmux.server

import org.ffpy.portmux.config.ConfigManager
import org.ffpy.portmux.util.StreamUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.nio.file.*
import java.util.stream.Stream
import kotlin.concurrent.thread

/**
 * 配置文件监听服务
 */
class WatchServer {

    companion object {
        private val log: Logger = LoggerFactory.getLogger(WatchServer::class.java)
    }

    fun start(configPath: Path) {
        val watcher = FileSystems.getDefault().newWatchService()
        val watchPath = configPath.toAbsolutePath().parent
        watchPath.register(watcher, StandardWatchEventKinds.ENTRY_MODIFY)
        thread {
            Stream.generate(eventGenerator(watcher))
                .filter(StreamUtils.filterByShortTime<List<WatchEvent<*>>>(1000))
                .forEach { events ->
                    events.asSequence()
                        .map { it.context() as Path }
                        .firstOrNull() { it == configPath.fileName }
                        ?.let {
                            try {
                                Thread.sleep(500)
                                log.info("Updating config: $it")
                                ConfigManager.init(configPath)
                                log.info("Update config successful")
                            } catch (e: Exception) {
                                log.error("Update config fail: ${e.message}")
                            }
                        }
                }
        }
    }

    private fun eventGenerator(watcher: WatchService): () -> MutableList<WatchEvent<*>> = {
        val key = watcher.take()
        val events = key.pollEvents()
        if (!key.reset()) throw RuntimeException("Fail to watching configuration file")
        events
    }
}