import com.diffplug.gradle.spotless.SpotlessExtension

val mvnGroupId = "io.github.wcarmon"
val mvnArtifactId = "property-utils-jvm" // see settings.gradle.kts
val mvnVersion = "1.0.0"

repositories {
    mavenCentral()
}

plugins {
    java
    id("com.diffplug.spotless") version "6.23.3"

    `java-library`
    `maven-publish`
    signing
}

group = mvnGroupId
version = mvnVersion

configure<JavaPluginExtension> {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

dependencies {
    implementation("org.jetbrains:annotations:24.1.0")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.1")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.10.1")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.10.1")

    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.1")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher:1.10.1")
}

java {
    withJavadocJar()
    withSourcesJar()
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            groupId = mvnGroupId
            artifactId = mvnArtifactId
            version = mvnVersion

            from(components["java"])

            suppressPomMetadataWarningsFor("runtimeElements")

            versionMapping {

            }

            pom {
                name = "property-utils-jvm"
                description = "Utilities for using Property instances"
                url = "https://github.com/wcarmon/property-utils-jvm"

                licenses {
                    license {
                        name = "MIT License"
                        url =
                            "https://raw.githubusercontent.com/wcarmon/property-utils-jvm/main/LICENSE"
                    }
                }

                developers {
                    developer {
                        id = "wcarmon"
                        name = "Wil Carmon"
                        email = "github@wcarmon.com"
                    }
                }


                scm {
                    connection =
                        "scm:git:git@github.com:wcarmon/property-utils-jvm.git"
                    developerConnection =
                        "scm:git:ssh://github.com:wcarmon/property-utils-jvm.git"
                    url = "https://github.com/wcarmon/property-utils-jvm"
                }
            }
        }
    }

    repositories {
        maven {
            // change URLs to point to your repos, e.g. http://my.org/repo
            val releasesRepoUrl = uri(layout.buildDirectory.dir("repos/releases")) // TODO: fix
            val snapshotsRepoUrl = uri(layout.buildDirectory.dir("repos/snapshots")) // TODO: fix

            url = if (version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl
            else releasesRepoUrl // TODO: fix

//            url = uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")

            // See ~/.gradle/gradle.properties
            credentials {
//                username = properties.getProperty('mavenCentralUsername')
//                password = properties.getProperty('mavenCentralPassword')
            }

//            metadataSources {
//                gradleMetadata()
//            }
        }
    }
}

signing {
    sign(publishing.publications["mavenJava"])
}

tasks.javadoc {
    if (JavaVersion.current().isJava9Compatible) {
        (options as StandardJavadocDocletOptions).addBooleanOption("html5", true)
    }
}

configure<SpotlessExtension> {
    java {
        googleJavaFormat("1.18.1").aosp().reflowLongStrings()
        importOrder()
        removeUnusedImports()

        target(
            "src/*/java/**/*.java"
        )

        targetExclude(
            "src/gen/**",
            "src/genTest/**"
        )
    }
}

