import com.google.auto.service.AutoService
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.TypeSpec
import me.arkadash.example.MyConstant
import java.io.File
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.TypeElement

@AutoService(Processor::class)
class Generator: AbstractProcessor() {

    override fun getSupportedSourceVersion(): SourceVersion {
        return SourceVersion.latestSupported()
    }

    override fun getSupportedAnnotationTypes(): MutableSet<String> {
        return mutableSetOf(MyConstant::class.java.name)
    }

    override fun process(annotations: MutableSet<out TypeElement>?, roundEnv: RoundEnvironment?): Boolean {
        val elementsWithAnnotation = roundEnv?.getElementsAnnotatedWith(MyConstant::class.java)

        val fileName = "GeneratedConstants"
        val objBuilder = TypeSpec.Companion.objectBuilder(fileName)
        createFile(objBuilder, fileName)
        return true
    }

    private fun createFile(objBuilder: TypeSpec.Builder, fileName: String) {
        val packageName = "me.arkadash.sdk.genereted"
        val file = FileSpec.builder(packageName, fileName)
            .addType(objBuilder.build())
            .build() // KotlinPoet
        val generatedDirectory = processingEnv.options[KAPT_GENERATED_NAME]

        file.writeTo(File(generatedDirectory, "$fileName.kt"))
    }

    companion object {
        const val KAPT_GENERATED_NAME = "kapt.kotlin.generated"
    }
}