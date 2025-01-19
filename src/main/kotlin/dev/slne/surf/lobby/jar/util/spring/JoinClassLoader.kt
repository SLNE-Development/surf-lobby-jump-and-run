package dev.slne.surf.lobby.jar.util.spring

import java.io.IOException
import java.net.URL
import java.nio.ByteBuffer
import java.util.*
import kotlin.math.max
import kotlin.math.min


class JoinClassLoader(
    private val parent: ClassLoader?,
    private vararg val delegateClassLoaders: ClassLoader
) : ClassLoader(parent) {

    constructor(parent: ClassLoader?, delegateClassLoaders: Collection<ClassLoader>) : this(
        parent,
        *delegateClassLoaders.toTypedArray()
    )

    @Throws(ClassNotFoundException::class)
    override fun findClass(name: String): Class<*> {
        val path = name.replace('.', '/') + ".class"
        val url = findResource(path)
        val byteCode: ByteBuffer

        if (url == null) {
            throw ClassNotFoundException(name)
        }

        try {
            byteCode = loadResource(url)
        } catch (exception: IOException) {
            throw ClassNotFoundException(name, exception)
        }

        return defineClass(name, byteCode, null)
    }

    override fun findResource(name: String): URL? {
        for (delegateClassLoader in delegateClassLoaders) {
            val resource: URL? = delegateClassLoader.getResource(name)

            if (resource != null) {
                return resource
            }
        }

        return null
    }

    @Throws(IOException::class)
    override fun findResources(name: String): Enumeration<URL> {
        val vector: Vector<URL> = Vector()

        for (delegateClassLoader in delegateClassLoaders) {
            val resources: Enumeration<URL> = delegateClassLoader.getResources(name)

            while (resources.hasMoreElements()) {
                vector.add(resources.nextElement())
            }
        }

        return vector.elements()
    }

    @Throws(IOException::class)
    private fun loadResource(url: URL): ByteBuffer {
        url.openStream().use { stream ->
            var initialBufferCapacity = min(0x40000, stream.available() + 1)

            initialBufferCapacity =
                if (initialBufferCapacity <= 2) 0x10000 else max(initialBufferCapacity, 0x200)

            var buffer = ByteBuffer.allocate(initialBufferCapacity)

            while (true) {
                if (!buffer.hasRemaining()) {
                    val newBuffer = ByteBuffer.allocate(buffer.capacity() * 2)

                    buffer.flip()
                    newBuffer.put(buffer)
                    buffer = newBuffer
                }

                val length = stream.read(buffer.array(), buffer.position(), buffer.remaining())

                if (length <= 0) {
                    break
                }

                buffer.position(buffer.position() + length)
            }

            buffer.flip()

            return buffer
        }
    }
}
