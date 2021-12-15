import io.kotless.buildsrc.Versions

group = rootProject.group
version = rootProject.version

dependencies {
    implementation(gradleApi())
    implementation(gradleKotlinDsl())
    implementation("org.jetbrains.kotlin", "kotlin-gradle-plugin", Versions.kotlin)

    //Parsing
    implementation(project(":dsl:kotless:kotless-lang-parser"))
    implementation(project(":dsl:ktor:ktor-lang-parser"))
    implementation(project(":dsl:spring:spring-lang-parser"))
    implementation(project(":engine"))

    //Bundled plugins
    implementation("com.github.jengelman.gradle.plugins", "shadow", "6.0.0")
    implementation("io.kcdk", "io.kcdk.gradle.plugin", "0.1.1")

    implementation("org.apache.logging.log4j", "log4j-core", "2.16.0")

    //local start
    implementation("org.testcontainers", "localstack", "1.16.2")

    //utils
    implementation("org.codehaus.plexus", "plexus-utils", "3.3.0")
    implementation("org.codehaus.plexus", "plexus-archiver", "4.2.1")
    implementation("org.codehaus.plexus", "plexus-container-default", "2.1.0")

    //terraform DSL
    implementation("io.terraformkt.providers:aws:3.14.1-0.1.4")
    implementation("io.terraformkt:entities:0.1.4")

    api("com.fasterxml.jackson.core", "jackson-databind", "2.10.3")
    api("com.fasterxml.jackson.dataformat", "jackson-dataformat-cbor", "2.10.3")

    testImplementation(gradleTestKit())
    testImplementation("org.junit.jupiter", "junit-jupiter-api", "5.6.0")
    testImplementation("org.junit.jupiter", "junit-jupiter-params", "5.6.0")
    testRuntimeOnly("org.junit.jupiter", "junit-jupiter-engine", "5.6.0")
}

tasks.withType<Test> {
    useJUnitPlatform {
        includeTags = setOf("unit")
        excludeTags = setOf("integration-ci", "integration-local")
    }

    testLogging {
        events("passed", "skipped", "failed")
    }
}

tasks.create("integration-ci", Test::class.java) {
    group = "verification"

    useJUnitPlatform {
        excludeTags = setOf("unit", "integration-local")
        includeTags = setOf("integration-ci")
    }

    testLogging {
        events("passed", "skipped", "failed")
    }
}

tasks.create("integration-local", Test::class.java) {
    group = "verification"

    useJUnitPlatform {
        excludeTags = setOf("unit", "integration-ci")
        includeTags = setOf("integration-local")
    }

    testLogging {
        events("passed", "skipped", "failed")
    }
}
