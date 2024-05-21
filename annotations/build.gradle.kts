import com.vanniktech.maven.publish.SonatypeHost

plugins {
    `java-library`
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


mavenPublishing{

    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)
    signAllPublications()

    coordinates("com.thesubgraph.safenet", "annotations", "0.1.0")
    pom {
        name.set("annotations")
        description.set("Annotations for generating Dagger modules")
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
