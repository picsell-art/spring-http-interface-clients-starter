package art.picsell.gradle.httpinterface

import org.gradle.api.DefaultTask
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.objectweb.asm.ClassReader
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Type
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.MethodNode
import java.nio.file.Files

abstract class GenerateHttpInterfaceClientStubsTask : DefaultTask() {

    companion object {
        private const val HTTP_INTERFACE_CLIENT_DESC =
            "Lart/picsell/starter/client/annotation/HttpInterfaceClient;"
    }

    @get:InputFiles
    abstract val inputClassesDirs: ConfigurableFileCollection

    @get:OutputDirectory
    abstract val outputDirectory: DirectoryProperty

    abstract val stubClassSuffix: Property<String>

    abstract val componentAnnotation: Property<String>

    abstract val componentBeanNamePrefix: Property<String>

    @TaskAction
    fun generate() {
        val outputDir = outputDirectory.get().asFile
        if (outputDir.exists()) {
            outputDir.deleteRecursively()
        }
        outputDir.mkdirs()

        var generated = 0
        inputClassesDirs.files
            .filter { it.exists() }
            .forEach { dir ->
                dir.walkTopDown()
                    .filter { it.isFile && it.extension == "class" }
                    .forEach { classFile ->
                        val classNode = ClassNode()
                        ClassReader(classFile.readBytes()).accept(classNode, 0)
                        if (!classNode.isHttpInterfaceClient()) {
                            return@forEach
                        }
                        val stubContent = buildStub(classNode)
                        val packagePath = classNode.packageName().replace('.', '/')
                        val outputPath = outputDir.toPath()
                            .resolve(packagePath)
                            .also { Files.createDirectories(it) }
                            .resolve("${classNode.simpleName()}${stubClassSuffix.get()}.java")
                        Files.writeString(outputPath, stubContent)
                        generated++
                    }
            }

        if (generated == 0) {
            outputDir.deleteRecursively()
        }
    }

    private fun ClassNode.isHttpInterfaceClient(): Boolean {
        val isInterface = (access and Opcodes.ACC_INTERFACE) == Opcodes.ACC_INTERFACE
        if (!isInterface) return false
        val annotations = visibleAnnotations.orEmpty()
        return annotations.any { it.desc == HTTP_INTERFACE_CLIENT_DESC }
    }

    private fun ClassNode.packageName(): String =
        name.substringBeforeLast('/', "").replace('/', '.')

    private fun ClassNode.simpleName(): String =
        name.substringAfterLast('/')

    private fun buildStub(classNode: ClassNode): String {
        val packageDeclaration = classNode.packageName()
            .takeIf { it.isNotEmpty() }
            ?.let { "package $it;\n\n" }
            ?: ""

        val stubClassName = classNode.simpleName() + stubClassSuffix.get()
        val componentAnnotationClass = componentAnnotation.get()
        val beanName = componentBeanNamePrefix.get() + classNode.getHttpInterfaceClientName()

        val builder = StringBuilder()
        builder.append(packageDeclaration)
        builder.append("import ").append(componentAnnotationClass).append(";\n\n")
        builder.append("@").append(componentAnnotationClass.substringAfterLast('.'))
            .append("(\"").append(beanName).append("\")\n")
        builder.append("public final class ").append(stubClassName)
            .append(" implements ").append(classNode.name.replace('/', '.')).append(" {\n")

        classNode.methods
            .filter { it.isAbstractContract() }
            .forEach { method ->
                builder.append('\n')
                builder.append("    @Override\n")
                builder.append("    public ").append(method.returnType()).append(' ')
                    .append(method.name).append('(')
                    .append(method.parameters())
                    .append(") {\n")
                builder.append(method.body())
                builder.append("    }\n")
            }

        builder.append("}\n")
        return builder.toString()
    }

    private fun ClassNode.getHttpInterfaceClientName(): String {
        val annotation = visibleAnnotations.orEmpty()
            .firstOrNull { it.desc == HTTP_INTERFACE_CLIENT_DESC } ?: return simpleName()
        val attributes = annotation.values ?: return simpleName()
        val nameIndex = attributes.indexOfFirst { it == "name" }
        if (nameIndex == -1 || nameIndex + 1 >= attributes.size) {
            return simpleName()
        }
        return attributes[nameIndex + 1] as? String ?: simpleName()
    }

    private fun MethodNode.isAbstractContract(): Boolean {
        if (name == "<init>" || name == "<clinit>") return false
        val isStatic = (access and Opcodes.ACC_STATIC) == Opcodes.ACC_STATIC
        if (isStatic) return false
        val isPrivate = (access and Opcodes.ACC_PRIVATE) == Opcodes.ACC_PRIVATE
        if (isPrivate) return false
        val isBridge = (access and Opcodes.ACC_BRIDGE) == Opcodes.ACC_BRIDGE
        if (isBridge) return false
        val isAbstract = (access and Opcodes.ACC_ABSTRACT) == Opcodes.ACC_ABSTRACT
        return isAbstract
    }

    private fun MethodNode.returnType(): String =
        Type.getReturnType(desc).asJavaType()

    private fun MethodNode.parameters(): String {
        val parameterTypes = Type.getArgumentTypes(desc)
        return parameterTypes
            .mapIndexed { index, type -> "${type.asJavaType()} param$index" }
            .joinToString(", ")
    }

    private fun MethodNode.body(): String {
        val returnType = Type.getReturnType(desc)
        return when (returnType.sort) {
            Type.VOID -> "        throw new UnsupportedOperationException(\"IDE-only stub\");\n"
            Type.BOOLEAN -> "        return false;\n"
            Type.CHAR -> "        return '\\0';\n"
            Type.BYTE, Type.SHORT, Type.INT -> "        return 0;\n"
            Type.LONG -> "        return 0L;\n"
            Type.FLOAT -> "        return 0f;\n"
            Type.DOUBLE -> "        return 0d;\n"
            else -> "        throw new UnsupportedOperationException(\"IDE-only stub\");\n"
        }
    }

    private fun Type.asJavaType(): String = when (sort) {
        Type.VOID -> "void"
        Type.BOOLEAN -> "boolean"
        Type.CHAR -> "char"
        Type.BYTE -> "byte"
        Type.SHORT -> "short"
        Type.INT -> "int"
        Type.FLOAT -> "float"
        Type.LONG -> "long"
        Type.DOUBLE -> "double"
        Type.ARRAY -> buildString {
            append(elementType.asJavaType())
            append("[]".repeat(dimensions))
        }
        Type.OBJECT -> className
        else -> className
    }
}
