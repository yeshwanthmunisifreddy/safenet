plugins {
    id("java-library")
    id("maven-publish")
    alias(libs.plugins.jetbrains.kotlin.jvm)
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}
publishing {
    publications {
        create<MavenPublication>("annotations") {
            groupId = "com.thesubgraph"
            artifactId = "annotations"
            version = "1.0.0"
            from(components["java"])
        }
    }
}