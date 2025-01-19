package dev.slne.surf.lobby.jar.util.spring

import org.springframework.boot.Banner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.core.io.DefaultResourceLoader
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.transaction.annotation.EnableTransactionManagement

private lateinit var _context: ConfigurableApplicationContext

fun runApplication(
    source: Class<*>,
    primaryClassLoader: ClassLoader,
    vararg classLoaders: ClassLoader
) {
    if (::_context.isInitialized) {
        return
    }

    val originalClassLoader = Thread.currentThread().contextClassLoader
    Thread.currentThread().contextClassLoader = primaryClassLoader

    val loaders = mutableListOf<ClassLoader>()
    loaders.add(primaryClassLoader)
    loaders.addAll(classLoaders)

    val applicationBuilder = SpringApplicationBuilder(source)
        .resourceLoader(
            DefaultResourceLoader(
                JoinClassLoader(
                    primaryClassLoader,
                    *loaders.toTypedArray()
                )
            )
        )

    applicationBuilder.bannerMode(Banner.Mode.OFF)
    applicationBuilder.logStartupInfo(false)

    _context = applicationBuilder.run()
    Thread.currentThread().contextClassLoader = originalClassLoader

    _context.registerShutdownHook()
}

val context: ConfigurableApplicationContext
    get() = _context

@SpringBootApplication(exclude = [DataSourceAutoConfiguration::class])
@EnableJpaAuditing
@EnableJpaRepositories
@EnableTransactionManagement
class PluginApplication