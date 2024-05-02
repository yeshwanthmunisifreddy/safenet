package com.thesubgraph.processor

import com.google.auto.service.AutoService
import com.squareup.javapoet.AnnotationSpec
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.ParameterSpec
import com.squareup.javapoet.TypeName
import com.squareup.javapoet.TypeSpec
import com.thesubgraph.annotations.UseModule
import dagger.Module
import dagger.Provides
import java.io.IOException
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.annotation.processing.SupportedAnnotationTypes
import javax.annotation.processing.SupportedSourceVersion
import javax.lang.model.SourceVersion
import javax.lang.model.element.ElementKind
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.Modifier
import javax.lang.model.element.TypeElement
import javax.lang.model.element.VariableElement
import javax.tools.Diagnostic

@SupportedAnnotationTypes("com.thesubgraph.annotation.UsecaseModule")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@AutoService(Processor::class)
class UseProviderProcessor : AbstractProcessor() {
    override fun getSupportedAnnotationTypes(): Set<String> {
        return setOf(UseModule::class.java.canonicalName)
    }

    override fun process(annotations: Set<TypeElement>, roundEnv: RoundEnvironment): Boolean {
        roundEnv.getElementsAnnotatedWith(UseModule::class.java).filterIsInstance<TypeElement>()
            .forEach { element ->
                if (element.kind != ElementKind.CLASS) {
                    processingEnv.messager.printMessage(
                        Diagnostic.Kind.ERROR, "Only Class can be annotated with @UseModule"
                    )
                    return true
                }


                val className = element.simpleName.toString() + "Module"
                val functionName = "provide" + element.simpleName.toString()
                val constructorParameters =
                    extractConstructorParameters(element)

                val methodSpec = functionBuilder(
                    functionName = functionName,
                    constructorParameters = constructorParameters,
                    element = element
                )
                val classSpec = objectBuilder(className, methodSpec, element)
                val javaFile = JavaFile.builder(
                    ClassName.bestGuess(element.asType().toString()).packageName(), classSpec
                ).build()
                try {
                    javaFile.writeTo(processingEnv.filer)

                } catch (e: IOException) {
                    processingEnv.messager.printMessage(Diagnostic.Kind.ERROR, e.message)
                }
            }
        return true
    }

    private fun objectBuilder(
        className: String,
        methodSpec: MethodSpec.Builder,
        element: TypeElement,
    ): TypeSpec? = TypeSpec.classBuilder(className).addModifiers(Modifier.PUBLIC)
        .addAnnotation(Module::class.java).addAnnotation(
            AnnotationSpec.builder(ClassName.get("dagger.hilt", "InstallIn")).addMember(
                "value",
                "\$T.class",
                ClassName.get("dagger.hilt.android.components", "ViewModelComponent")
            ).build()
        ).addMethod(methodSpec.build()).addOriginatingElement(element).build()

    private fun functionBuilder(
        functionName: String,
        constructorParameters: List<VariableElement>,
        element: TypeElement,
    ): MethodSpec.Builder {

        val parameterSpecs = constructorParameters.map { parameter ->
            ParameterSpec.builder(TypeName.get(parameter.asType()), parameter.simpleName.toString()).build()
        }
         val methodSpec = MethodSpec.methodBuilder(functionName).addModifiers(Modifier.PUBLIC)
            .addAnnotation(ClassName.get("dagger.hilt.android.scopes", "ViewModelScoped"))
             .addAnnotation(Provides::class.java)
             .addStatement(
                "return new \$T(\$N)",
                ClassName.bestGuess(element.asType().toString()),
                "repository"
            ).returns(ClassName.bestGuess(element.asType().toString()))
        parameterSpecs.forEach { parameter ->
            methodSpec.addParameter(parameter)
        }
        return methodSpec

    }

    private fun extractConstructorParameters(element: TypeElement): List<VariableElement> {
        val constructorParameters = mutableListOf<VariableElement>()
        val constructors = element.enclosedElements.filterIsInstance<ExecutableElement>()
            .filter { it.kind == ElementKind.CONSTRUCTOR }
        if (constructors.isNotEmpty()) {
            val constructor = constructors.firstOrNull()
            constructor?.parameters?.forEach { parameter ->
                constructorParameters.add(parameter)
            }
        }
        return constructorParameters
    }
}