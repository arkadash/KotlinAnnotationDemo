import com.google.auto.service.AutoService
import com.squareup.kotlinpoet.*
import me.arkadash.example.MyConstant
import me.arkadash.example.MyFaker
import java.io.File
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind
import javax.lang.model.element.TypeElement
import javax.lang.model.type.TypeMirror
import kotlin.random.Random

@AutoService(Processor::class)
class FakerGenerator: AbstractProcessor() {
    private val packageName = "me.arkadash.sdk.genereted"

    override fun getSupportedSourceVersion(): SourceVersion {
        return SourceVersion.latestSupported()
    }

    override fun getSupportedAnnotationTypes(): MutableSet<String> {
        return mutableSetOf(MyFaker::class.java.name)
    }

    override fun process(annotations: MutableSet<out TypeElement>?, roundEnv: RoundEnvironment?): Boolean {
        val elementsWithAnnotation = roundEnv?.getElementsAnnotatedWith(MyFaker::class.java)
        if( elementsWithAnnotation?.isEmpty() == true) {
            return true
        }

        elementsWithAnnotation?.forEach { element ->
            val myConstantAnnotation = element.getAnnotation(MyFaker::class.java)
            val fileName = getFileName(myConstantAnnotation, element)
            val typeBuilder = TypeSpec.classBuilder(fileName)

            // Create constructor
            val constructorBuilder = FunSpec.constructorBuilder()
            val myList = mutableListOf<PropertySpec>()
            element.enclosedElements.filter {
                it.kind == ElementKind.FIELD
            }.forEach {
                val name = it.simpleName.toString()
                val type = getTypeName(it.asType())

                val defaultValue = getFakeMethod(type)
                constructorBuilder.addParameter(
                    ParameterSpec.builder(
                        name = name,
                        type = type
                    ).defaultValue(defaultValue).build()
                )
                myList.add(
                    PropertySpec.builder(
                        name, type).initializer(name).build())
            }

            myList.forEach { typeBuilder.addProperty(it) }
            typeBuilder.primaryConstructor(constructorBuilder.build())

            val fakerClass = ClassName(packageName, fileName)
            val file = FileSpec.builder(packageName, fileName)
                .addImport("kotlin.random", "Random")
                .addType(
                    typeBuilder
                    .addFunction(buildFunction(fileName))
                    .build())
                .addFunction(FunSpec.builder("${fileName}StaticInit")
                    .addStatement("%T(\"blue\").fake()", fakerClass)
                    .build())
                .build()

            createFile(file, fileName)
        }
        return true
    }

    private fun getFakeMethod(type: ClassName): String {
        when (type) {
            Int::class.asClassName() -> {
                return "Random.nextInt(0, 100)"
            }
            Double::class.asClassName() -> {
                return "Random.nextDouble(0, 100)"
            }
        }
        return "\"${getRandomString(12)}\""
    }

    private fun getRandomString(length: Int) : String {
        val charset = "ABCDEFGHIJKLMNOPQRSTUVWXTZabcdefghiklmnopqrstuvwxyz0123456789"
        return (1..length)
            .map { charset.random() }
            .joinToString("")
    }

    private fun buildFunction(fileName: String) = FunSpec.builder("fake")
        .addStatement("println(%P)", fileName).build()

    private fun getTypeName(type: TypeMirror): ClassName {
        return if (type.toString() == "java.lang.String") {
            ClassName("kotlin", "String")
        } else {
            ClassName("kotlin", "Int")
        }
    }

    private fun getFileName(
        myConstantAnnotation: MyFaker,
        element: Element
    ) = if (myConstantAnnotation.filename != "null") myConstantAnnotation.filename else "${element.simpleName}Faker"

    private fun createFile(file: FileSpec, fileName: String) {
        val generatedDirectory = processingEnv.options[KAPT_GENERATED_NAME]
        file.writeTo(File(generatedDirectory, "$fileName.kt"))
    }

    companion object {
        const val KAPT_GENERATED_NAME = "kapt.kotlin.generated"
    }
}