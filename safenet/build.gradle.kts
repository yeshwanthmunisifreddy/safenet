import com.vanniktech.maven.publish.SonatypeHost

plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.hiltAndroid)
    kotlin("kapt")
    alias(libs.plugins.vanniktech.maven.publish)
}

android {
    namespace = "com.thesubgraph.safenet"
    compileSdk = 34

    defaultConfig {
        minSdk = 24
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
        aarMetadata {
            minCompileSdk = 24
        }

    }

    buildTypes {
        release {
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    kapt{
        correctErrorTypes = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.bundles.retrofit.network)
    implementation(libs.safenet.annotations)
    implementation(libs.hilt.android)
    kapt(libs.hilt.android.compiler)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}

mavenPublishing {
    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)
    signAllPublications()
    coordinates("com.thesubgraph.safenet", "safenet", "0.1.0")
    pom {
        name.set("safenet")
        description.set(
            "SafeNet is a library engineered to streamline the" +
                    " configuration of the network layer and dependency " +
                    "injection in your projects. It leverages annotations " +
                    "to automatically furnish essential components such as " +
                    "repositories, API services, and use cases. " +
                    "This eliminates the need for manual setup " +
                    "using @Module and @Provides annotations, thereby " +
                    "simplifying the process of establishing the Network " +
                    "Layer and Dependency Injection for network layer."
        )
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
