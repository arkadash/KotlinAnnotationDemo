import com.google.auto.service.AutoService
import com.squareup.kotlinpoet.FileSpec
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

        createFile()
        return true
    }

    private fun createFile() {
        val packageName = "me.arkadash.sdk-genereted"
        val fileName = "GeneratedConstants"
        val file = FileSpec.builder(packageName, fileName).build() // KotlinPoet
        val generatedDirectory = processingEnv.options[KAPT_GENERATED_NAME]

        file.writeTo(File(generatedDirectory, "$fileName.kt"))
    }

    companion object {
        const val KAPT_GENERATED_NAME = "kapt.kotlin.generated"
    }
}