package ma.dexter.tools.decompilers.fernflower

import ma.dexter.tools.decompilers.BaseJarDecompiler
import org.jetbrains.java.decompiler.main.decompiler.BaseDecompiler
import org.jetbrains.java.decompiler.main.extern.IBytecodeProvider
import org.jetbrains.java.decompiler.main.extern.IFernflowerLogger
import org.jetbrains.java.decompiler.main.extern.IFernflowerPreferences
import org.jetbrains.java.decompiler.main.extern.IResultSaver
import java.io.File
import java.nio.file.Files
import java.util.jar.Manifest

/*
 * Adapted from [https://github.com/JetBrains/intellij-community/blob/master/plugins/java-decompiler/plugin/src/org/jetbrains/java/decompiler/IdeaDecompiler.kt]
 */
class FernflowerDecompiler: BaseJarDecompiler {
    private val options = defaultOptions()

    /**
     * Decompiles given [classFile] to Java using Fernflower.
     *
     * @return Decompiled Java code
     */
    override fun decompile(
        classFile: File
    ): String {
        val bytecodeProvider = BytecodeProvider(listOf(classFile))

        val resultSaver = ResultSaver()

        val logger = object : IFernflowerLogger() {
            override fun writeMessage(p0: String?, p1: Severity?) {}

            override fun writeMessage(p0: String?, p1: Severity?, p2: Throwable?) {}
        }

        val decompiler = BaseDecompiler(bytecodeProvider, resultSaver, options, logger)
        decompiler.addSource(classFile)
        decompiler.decompileContext()

        return resultSaver.result
    }

    private class BytecodeProvider(jarFiles: List<File>) : IBytecodeProvider {
        private val pathMap = jarFiles.associateBy { File(it.path).absolutePath }

        override fun getBytecode(externalPath: String, internalPath: String?): ByteArray =
            Files.readAllBytes(pathMap[externalPath]?.toPath()) ?: throw AssertionError(externalPath + " not in " + pathMap.keys)
    }

    private class ResultSaver : IResultSaver {
        var result = ""

        override fun saveClassFile(path: String, qualifiedName: String, entryName: String, content: String, mapping: IntArray?) {
            if (result.isEmpty()) {
                result = content
            }
        }

        override fun saveFolder(path: String?) {}

        override fun copyFile(source: String?, path: String?, entryName: String?) {}

        override fun createArchive(path: String?, archiveName: String?, manifest: Manifest?) {}

        override fun saveDirEntry(path: String?, archiveName: String?, entryName: String?) {}

        override fun copyEntry(source: String?, path: String?, archiveName: String?, entry: String?) {}

        override fun saveClassEntry(path: String?, archiveName: String?, qualifiedName: String?, entryName: String?, content: String?) {}

        override fun closeArchive(path: String?, archiveName: String?) {}
    }

    override fun getBanner() = """
            /*
             * Decompiled with Fernflower [e19aab61ce].
             */
        """.trimIndent() + "\n"

    override fun getName() = "Fernflower"

    private fun defaultOptions() = mapOf(
        IFernflowerPreferences.HIDE_DEFAULT_CONSTRUCTOR to "0",
        IFernflowerPreferences.DECOMPILE_GENERIC_SIGNATURES to "1",
        IFernflowerPreferences.REMOVE_SYNTHETIC to "1",
        IFernflowerPreferences.REMOVE_BRIDGE to "1",
        IFernflowerPreferences.LITERALS_AS_IS to "1",
        IFernflowerPreferences.NEW_LINE_SEPARATOR to "1",
        IFernflowerPreferences.BANNER to getBanner(),
        IFernflowerPreferences.MAX_PROCESSING_METHOD to 60,
        IFernflowerPreferences.IGNORE_INVALID_BYTECODE to "1",
    )
}
