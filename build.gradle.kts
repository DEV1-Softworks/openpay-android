// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.kotlin.serialization) apply false
}

// JaCoCo coverage convention shared by every Android module. Each module gets:
//  - jacocoTestReport: XML + HTML coverage report from the debug unit tests
//  - jacocoCoverageVerification: fails the build when line coverage drops below 80%
val jacocoVersion = libs.versions.jacoco.get()

val coverageExclusions = listOf(
    "**/R.class",
    "**/R\$*.class",
    "**/BuildConfig.*",
    "**/Manifest*.*",
    "**/*Test*.*",
    "android/**/*.*",
    "**/*ComposableSingletons*.*",
    "**/*Preview*Kt*.*",
)

subprojects {
    apply(plugin = "jacoco")

    configure<JacocoPluginExtension> {
        toolVersion = jacocoVersion
    }

    tasks.withType<Test>().configureEach {
        configure<JacocoTaskExtension> {
            // Required so Robolectric-instrumented classes are measured correctly.
            isIncludeNoLocationClasses = true
            excludes = listOf("jdk.internal.*")
        }
    }

    plugins.withId("com.android.base") {
        val moduleBuildDir = layout.buildDirectory

        // AGP 9 built-in Kotlin compiles into built_in_kotlinc; javac output stays in javac.
        val coverageClassDirectories = files(
            fileTree(moduleBuildDir.dir("intermediates/built_in_kotlinc/debug/compileDebugKotlin/classes")) {
                exclude(coverageExclusions)
            },
            fileTree(moduleBuildDir.dir("intermediates/javac/debug/classes")) {
                exclude(coverageExclusions)
            },
        )
        val coverageSourceDirectories = files("src/main/java", "src/main/kotlin")
        val coverageExecutionData =
            moduleBuildDir.file("outputs/unit_test_code_coverage/debugUnitTest/testDebugUnitTest.exec")

        tasks.register<JacocoReport>("jacocoTestReport") {
            group = "verification"
            description = "Generates the JaCoCo coverage report for debug unit tests."
            dependsOn("testDebugUnitTest")
            reports {
                xml.required.set(true)
                html.required.set(true)
            }
            classDirectories.setFrom(coverageClassDirectories)
            sourceDirectories.setFrom(coverageSourceDirectories)
            executionData.setFrom(coverageExecutionData)
        }

        tasks.register<JacocoCoverageVerification>("jacocoCoverageVerification") {
            group = "verification"
            description = "Fails when debug unit test line coverage is below 80%."
            dependsOn("testDebugUnitTest")
            mustRunAfter("jacocoTestReport")
            violationRules {
                rule {
                    limit {
                        counter = "LINE"
                        value = "COVEREDRATIO"
                        minimum = "0.80".toBigDecimal()
                    }
                }
            }
            classDirectories.setFrom(coverageClassDirectories)
            sourceDirectories.setFrom(coverageSourceDirectories)
            executionData.setFrom(coverageExecutionData)
        }
    }
}
