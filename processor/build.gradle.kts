import com.vanniktech.maven.publish.SonatypeHost

plugins {
    `java-library`
    kotlin("kapt")
    alias(libs.plugins.jetbrains.kotlin.jvm)
    alias(libs.plugins.vanniktech.maven.publish)

}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    implementation(libs.safenet.annotations)
    implementation(libs.dagger)
    implementation(libs.javapoet) {
        because("A Java API for generating .java source files")
    }
    compileOnly(libs.google.auto.service) {
        because("A configuration/metadata generator for java.util.ServiceLoader-style service providers")
    }
    kapt(libs.google.auto.service)
    compileOnly(libs.incap.core) {
        because("Helper library and annotation processor for building incremental annotation processors")
    }
    kapt(libs.incap.processor)
}
mavenPublishing{

    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)
    signAllPublications()

    coordinates("com.thesubgraph.safenet", "processor", "0.1.0")
    pom {
        name.set("processor")
        description.set("A processor for generating Dagger modules from annotated interfaces and classes")
        inceptionYear.set("2024")
        url.set("https://github.com/yeshwanthmunisifreddy/SafeNet")
        licenses {
            license {
                name.set("The Apache License, Version 2.0")
                url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                distribution.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
            }
        }
        developers {
            developer {
                id.set("yeshwanthmunisifreddy")
                name.set("Yeshwanth Munisifreddy")
                email.set("yeshwanth@thesubgraph.com")
            }
        }
        scm {
            connection.set("scm:git:git://github.com/yeshwanthmunisifreddy/SafeNet.git")
            developerConnection.set("scm:git:ssh://github.com:yeshwanthmunisifreddy/SafeNet.git")
            url.set("https://github.com/yeshwanthmunisifreddy/SafeNet")
        }
    }

}


