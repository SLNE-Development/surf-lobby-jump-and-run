package dev.slne.surf.lobby.jar.util.spring

import dev.slne.surf.lobby.jar.plugin
import net.kyori.adventure.text.logger.slf4j.ComponentLogger
import org.springframework.beans.factory.config.BeanDefinition
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.context.annotation.Role
import org.springframework.jdbc.datasource.DriverManagerDataSource
import java.sql.SQLException
import javax.sql.DataSource

@Configuration
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
class PluginDataConfig {
    private val log = ComponentLogger.logger()

    @Bean
    @Primary
    fun dataSource(): DataSource {
        val config = plugin.config
        val dbType = config.getString("mysql.type", "mariadb")
        val hostname = config.getString("mysql.hostname")
        val port = config.getInt("mysql.port")
        val databaseName = config.getString("mysql.database")
        val username = config.getString("mysql.username")
        val password = config.getString("mysql.password")

        val driverClassName = config.getString("mysql.driver", "org.mariadb.jdbc.Driver")!!
        val jdbcUrl = "jdbc:$dbType://$hostname:$port/$databaseName"

        val dataSource = DriverManagerDataSource()

        with(dataSource) {
            setDriverClassName(driverClassName)
            this.username = username
            this.password = password
            url = jdbcUrl
        }

        return dataSource
    }

    private fun validateDatasource(dataSource: DriverManagerDataSource) {
        try {
            dataSource.connection.use {
                log.info("Database connection established.")
            }
        } catch (e: SQLException) {
            val message = buildString {
                appendLine("Failed to tryEstablishConnection to the database.")
                appendLine("The database connection could not be established using the provided configuration.")
                appendLine("Database URL: ${dataSource.url}")
                appendLine("Username: ${dataSource.username}")
                appendLine("Password set: ${dataSource.password != null}")
                appendLine("Check if the database server is running and accessible.")
                appendLine("Verify that the database URL, username, and password are correct.")
                appendLine("Ensure that the database driver (org.mariadb.jdbc.Driver) is compatible.")
            }

            throw IllegalStateException(message, e)
        }
    }


}