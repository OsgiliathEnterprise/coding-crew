plugins {
    // Define plugin versions for subprojects (not applied at root)
    id("org.springframework.boot") version "3.4.2" apply false
    id("io.spring.dependency-management") version "1.1.6" apply false
    id("org.jetbrains.gradle.plugin.idea-ext") version "1.1.9"
    id("idea")
    wrapper
}

group = "net.osgiliath.ai"
version = "1.0-SNAPSHOT"

tasks.wrapper {
    gradleVersion = "9.3.1"
    distributionType = Wrapper.DistributionType.BIN
}

subprojects {
    pluginManager.apply("java")

    // Configure Java toolchain explicitly via extension to avoid ordering issues
    extensions.configure<JavaPluginExtension> {
        toolchain.languageVersion.set(JavaLanguageVersion.of(21))
    }

    dependencies {
        "testImplementation"(platform("org.junit:junit-bom:5.14.2"))
        "testImplementation"("org.junit.jupiter:junit-jupiter")
        "testRuntimeOnly"("org.junit.platform:junit-platform-launcher")
        "implementation"(platform("io.cucumber:cucumber-bom:7.34.2"))
        "implementation"(platform("org.bsc.langgraph4j:langgraph4j-bom:1.8.3"))
        "implementation"(platform("dev.langchain4j:langchain4j-bom:1.11.0"))
    }

    tasks.withType<Test>().configureEach {
        useJUnitPlatform()

        // Add detailed test logging for debugging
        testLogging {
            events("passed", "skipped", "failed", "standardOut", "standardError")
            exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
            showExceptions = true
            showCauses = true
            showStackTraces = true
        }

        // Enable debug output
        systemProperty("java.util.logging.config.file", "")
    }
}
