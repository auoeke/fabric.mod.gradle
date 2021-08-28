package net.auoeke.fabricmodgradle.extension

import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.Opcodes
import java.io.File
import java.io.InputStream
import java.util.*

class ClassInfo(input: InputStream) : ClassVisitor(Opcodes.ASM9) {
    lateinit var name: String private set
    var superclass: String? = null
        private set
    var interfaces: HashSet<String> = HashSet()
    val binaryName: String by lazy {this.name.replace('/', '.')}

    init {
        ClassReader(input).accept(this, ClassReader.SKIP_CODE)
    }

    override fun visit(version: Int, access: Int, name: String, signature: String?, superName: String, interfaces: Array<String>?) {
        if (superName != "java/lang/Object") {
            this.superclass = superName
        }

        this.name = name
        interfaces?.also {this.interfaces += interfaces}
    }
}
