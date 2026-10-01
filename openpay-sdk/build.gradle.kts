plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    `maven-publish`
    signing
}

android {
    namespace = "mx.dev1.openpay.sdk"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        minSdk = 26

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        debug {
            enableUnitTestCoverage = true
        }
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    buildFeatures {
        compose = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }

    publishing {
        singleVariant("release") {
            withSourcesJar()
            withJavadocJar()
        }
    }
}

publishing {
    publications {
        register<MavenPublication>("release") {
            groupId = project.property("GROUP").toString()
            artifactId = "openpay-sdk"
            version = project.property("VERSION_NAME").toString()

            afterEvaluate {
                from(components["release"])
            }

            pom {
                name.set("Openpay Android SDK")
                description.set(
                    "Kotlin-first Openpay SDK for Android: card tokenization, validation, " +
                        "antifraud device sessions and Compose UI components."
                )
                url.set("https://github.com/DEV1-Softworks/openpay-android")
                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }
                developers {
                    developer {
                        id.set("dev1-softworks")
                        name.set("DEV1 Softworks Labs")
                        url.set("https://labs.dev1.mx")
                    }
                }
                scm {
                    connection.set("scm:git:git://github.com/DEV1-Softworks/openpay-android.git")
                    developerConnection.set("scm:git:ssh://github.com/DEV1-Softworks/openpay-android.git")
                    url.set("https://github.com/DEV1-Softworks/openpay-android")
                }
            }
        }
    }

    repositories {
        // Remote repository configured entirely through environment variables so
        // credentials never live in the build. Skipped when the URL is absent
        // (publishToMavenLocal keeps working without any setup).
        val remoteRepositoryUrl = System.getenv("MAVEN_REPOSITORY_URL")
        if (remoteRepositoryUrl != null) {
            maven {
                name = "remote"
                url = uri(remoteRepositoryUrl)
                credentials {
                    username = System.getenv("MAVEN_REPOSITORY_USERNAME")
                    password = System.getenv("MAVEN_REPOSITORY_PASSWORD")
                }
            }
        }
    }
}

signing {
    // In-memory PGP signing, enabled only when the key material is provided
    // (for example on the release CI). Local builds stay unsigned.
    val signingKey = System.getenv("SIGNING_KEY")
    val signingPassword = System.getenv("SIGNING_PASSWORD")
    if (signingKey != null) {
        useInMemoryPgpKeys(signingKey, signingPassword)
        sign(publishing.publications)
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.serialization.json)

    // HTTP client
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.cio)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.client.logging)
    implementation(libs.ktor.serialization.kotlinx.json)

    // Dependency injection
    implementation(platform(libs.koin.bom))
    implementation(libs.koin.android)
    implementation(libs.koin.androidx.compose)

    // Compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    testImplementation(libs.junit)
    testImplementation(libs.androidx.junit)
    testImplementation(libs.mockito.core)
    testImplementation(libs.mockito.kotlin)
    testImplementation(libs.robolectric)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.ktor.client.mock)
    testImplementation(platform(libs.koin.bom))
    testImplementation(libs.koin.test)
    testImplementation(libs.koin.test.junit4)
    testImplementation(platform(libs.androidx.compose.bom))
    testImplementation(libs.androidx.compose.ui.test.junit4)

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
}
