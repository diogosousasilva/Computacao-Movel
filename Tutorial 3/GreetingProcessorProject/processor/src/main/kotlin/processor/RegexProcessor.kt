package processor

import com.dam.annotations.Extract
import com.google.auto.service.AutoService
import com.squareup.kotlinpoet.*
import java.io.File
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic

@AutoService(Processor::class)
@SupportedSourceVersion(SourceVersion.RELEASE_23)
@SupportedAnnotationTypes("com.dam.annotations.Extract")
class RegexProcessor : AbstractProcessor() {

    override fun process(annotations: MutableSet<out TypeElement>, roundEnv: RoundEnvironment): Boolean {
        val classMethodMap = mutableMapOf<TypeElement, MutableList<ExecutableElement>>()

        for (element in roundEnv.getElementsAnnotatedWith(Extract::class.java)) {
            if (element is ExecutableElement) {
                val enclosingClass = element.enclosingElement as TypeElement
                classMethodMap.computeIfAbsent(enclosingClass) { mutableListOf() }.add(element)
            }
        }

        for ((classElement, methods) in classMethodMap) {
            generateRegexExtractorClass(classElement, methods)
        }

        return true
    }

    private fun generateRegexExtractorClass(classElement: TypeElement, methods: List<ExecutableElement>) {
        val packageName = processingEnv.elementUtils.getPackageOf(classElement).toString()
        val originalClassName = classElement.simpleName.toString()
        val extractorClassName = "${originalClassName}Extractor"

        val classBuilder = TypeSpec.classBuilder(extractorClassName)
            .primaryConstructor(
                FunSpec.constructorBuilder()
                    .addParameter("input", String::class)
                    .build()
            )
            .superclass(ClassName(packageName, originalClassName))
            .addSuperclassConstructorParameter("input")
            .addModifiers(KModifier.PUBLIC)

        for (method in methods) {
            val methodName = method.simpleName.toString()
            val regex = method.getAnnotation(Extract::class.java)?.regex ?: ""

            // Must return String?
            val returnType = String::class.asTypeName().copy(nullable = true)

            val methodBuilder = FunSpec.builder(methodName)
                .addModifiers(KModifier.OVERRIDE)
                .returns(returnType)
                .addStatement("val match = %T(%S).find(input)", Regex::class, regex)
                .addStatement("return match?.groupValues?.get(1)")

            classBuilder.addFunction(methodBuilder.build())
        }

        val file = FileSpec.builder(packageName, extractorClassName)
            .addType(classBuilder.build())
            .build()

        try {
            val kaptKotlinGeneratedDir = processingEnv.options["kapt.kotlin.generated"]
            if (kaptKotlinGeneratedDir != null) {
                file.writeTo(File(kaptKotlinGeneratedDir))
            } else {
                processingEnv.messager.printMessage(Diagnostic.Kind.ERROR, "kapt.kotlin.generated not found")
            }
        } catch (e: Exception) {
            processingEnv.messager.printMessage(Diagnostic.Kind.ERROR, "Error generating Kotlin file: ${e.message}")
        }
    }
}
