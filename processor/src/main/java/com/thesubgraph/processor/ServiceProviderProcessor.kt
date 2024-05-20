package com.thesubgraph.processor

import com.google.auto.service.AutoService
import com.squareup.javapoet.AnnotationSpec
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.TypeSpec
import com.thesubgraph.annotations.BaseUrl
import com.thesubgraph.annotations.ServiceModule
import dagger.Module
import dagger.Provides
import java.io.IOException
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.annotation.processing.SupportedAnnotationTypes
import javax.annotation.processing.SupportedSourceVersion
import javax.inject.Singleton
import javax.lang.model.SourceVersion
import javax.lang.model.element.ElementKind
import javax.lang.model.element.Modifier
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic

@SupportedAnnotationTypes("com.thesubgraph.annotation.ServiceModule")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@AutoService(Processor::class)
class ServiceProviderProcessor : AbstractProcessor() {
    override fun getSupportedAnnotationTypes(): Set<String> {
        return setOf(ServiceModule::class.java.canonicalName)
    }

    override fun process(annotations: Set<TypeElement>, roundEnv: RoundEnvironment): Boolean {
        roundEnv.getElementsAnnotatedWith(ServiceModule::class.java)
            .filterIsInstance<TypeElement>()
            .forEach { element ->
                if (generateServiceModule(element)) return true
            }
        return true
    }

    private fun generateServiceModule(element: TypeElement): Boolean {
        if (checkElementType(element)) return true
        val className = element.simpleName.toString() + "Module"
        val functionName = "provide" + element.simpleName.toString()
        val baseUrlAnnotation = element.getAnnotation(BaseUrl::class.java)
        val baseUrl = baseUrlAnnotation?.value ?: throw IllegalArgumentException(
            "Base URL not set for ${
                ClassName.bestGuess(
                    element.asType().toString()
                ).packageName()
            }"
        )
        val methodSpec = functionBuilder(
            functionName = functionName,
            baseUrl = baseUrl,
            element = element
        )
        val classSpec = objectBuilder(
            className = className,
            methodSpec = methodSpec,
            element = element
        )
        val javaFile = JavaFile.builder(
            ClassName.bestGuess(element.asType().toString()).packageName(),
            classSpec
        ).build()

        try {
            javaFile.writeTo(processingEnv.filer)
        } catch (e: IOException) {
            processingEnv.messager.printMessage(Diagnostic.Kind.ERROR, "Failed to write file: ${e.message}")
        }
        return false
    }

    private fun objectBuilder(
        className: String,
        methodSpec: MethodSpec.Builder,
        element: TypeElement,
    ): TypeSpec? = TypeSpec.classBuilder(className)
        .addModifiers(Modifier.PUBLIC)
        .addAnnotation(Module::class.java)
        .addAnnotation(
            AnnotationSpec.builder(ClassName.get("dagger.hilt", "InstallIn"))
                .addMember(
                    "value",
                    "\$T.class",
                    ClassName.get("dagger.hilt.components", "SingletonComponent")
                )
                .build()
        )
        .addMethod(methodSpec.build())
        .addOriginatingElement(element)
        .build()

    private fun functionBuilder(
        functionName: String,
        baseUrl: String,
        element: TypeElement,
    ): MethodSpec.Builder = MethodSpec.methodBuilder(functionName)
        .addModifiers(Modifier.PUBLIC)
        .addAnnotation(Singleton::class.java)
        .addAnnotation(Provides::class.java)
        .addParameter(ClassName.bestGuess("okhttp3.OkHttpClient"), "okHttpClient")
        .addParameter(
            ClassName.bestGuess("retrofit2.converter.gson.GsonConverterFactory"),
            "gsonConverterFactory"
        )
        .addStatement(
            "return com.thesubgraph.safenet.di.RetrofitModule.INSTANCE.provideRetrofit(okHttpClient, gsonConverterFactory,\$S, \$T.class)",
            baseUrl,
            ClassName.bestGuess(
                element.asType().toString()
            )
        )
        .returns(ClassName.bestGuess(element.asType().toString()))

    private fun checkElementType(element: TypeElement): Boolean {
        if (element.kind != ElementKind.INTERFACE) {
            processingEnv.messager.printMessage(
                Diagnostic.Kind.ERROR,
                "Only interfaces can be annotated with @ServiceModule"
            )
            return true
        }
        return false
    }
}