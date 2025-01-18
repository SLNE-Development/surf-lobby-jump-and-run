package dev.slne.surf.lobby.jar

import com.google.gson.Gson
import io.papermc.paper.plugin.loader.PluginClasspathBuilder
import io.papermc.paper.plugin.loader.PluginLoader
import io.papermc.paper.plugin.loader.library.impl.MavenLibraryResolver
import org.eclipse.aether.artifact.DefaultArtifact
import org.eclipse.aether.graph.Dependency
import org.eclipse.aether.repository.RemoteRepository
import java.io.IOException
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets
import java.util.stream.Stream

class PluginLibrariesLoader : PluginLoader {
    override fun classloader(classpathBuilder: PluginClasspathBuilder) {
        val resolver = MavenLibraryResolver()
        val pluginLibraries = load()
        pluginLibraries.asDependencies().forEach { dependency: Dependency? ->
            resolver.addDependency(
                dependency!!
            )
        }
        pluginLibraries.asRepositories().forEach { remoteRepository: RemoteRepository? ->
            resolver.addRepository(
                remoteRepository!!
            )
        }
        classpathBuilder.addLibrary(resolver)
    }

    private fun load(): PluginLibraries {
        try {
            javaClass.getResourceAsStream("/paper-libraries.json").use { `in` ->
                return Gson().fromJson(
                    InputStreamReader(`in`, StandardCharsets.UTF_8),
                    PluginLibraries::class.java
                )
            }
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }

    @JvmRecord
    private data class PluginLibraries(
        val repositories: Map<String, String>,
        val dependencies: List<String>
    ) {
        fun asDependencies(): Stream<Dependency> {
            return dependencies.stream()
                .map { d: String? -> Dependency(DefaultArtifact(d), null) }
        }

        fun asRepositories(): Stream<RemoteRepository> {
            return repositories.entries.stream()
                .map { e: Map.Entry<String, String> ->
                    RemoteRepository.Builder(
                        e.key,
                        "default",
                        e.value
                    ).build()
                }
        }
    }
}