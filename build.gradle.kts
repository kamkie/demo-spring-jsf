import com.github.gradle.node.task.NodeTask
import com.github.spotbugs.snom.SpotBugsTask
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.springframework.boot.gradle.plugin.SpringBootPlugin
import org.springframework.boot.gradle.util.VersionExtractor
import java.util.regex.Pattern

plugins {
    java
    jacoco
    idea
    pmd
    id("com.palantir.git-version") version "3.0.0"
    id("com.gorylenko.gradle-git-properties") version "2.4.2"
    id("com.diffplug.spotless") version "6.25.0"
    id("com.github.ben-manes.versions") version "0.51.0"
    id("com.github.spotbugs") version "6.0.14"
    id("org.springframework.boot") version "3.2.5"
    id("org.liquibase.gradle") version "2.2.2"
    id("org.asciidoctor.jvm.convert") version "4.0.2"
    id("com.github.node-gradle.node") version "7.0.2"
    id("com.ofg.uptodate") version "1.6.3"
    id("com.adarshr.test-logger") version "4.0.0"
}

val gitVersion: groovy.lang.Closure<String> by extra
version = gitVersion()
group = "demo"

val javaVersion = JavaVersion.VERSION_21
val nodeVersion = "20.9.0"
val spotbugsToolVersion = "4.8.0"
val jacocoToolVersion = "0.8.9"
val pmdToolVersion = "6.54.0"

repositories {
    mavenCentral()
    maven { url = uri("https://repository.primefaces.org") }
}

val asciidoctor: Configuration = configurations.create("asciidoctor")

dependencies {
    asciidoctor(enforcedPlatform(SpringBootPlugin.BOM_COORDINATES))
    implementation(enforcedPlatform(SpringBootPlugin.BOM_COORDINATES))
    annotationProcessor(enforcedPlatform(SpringBootPlugin.BOM_COORDINATES))
    testAnnotationProcessor(enforcedPlatform(SpringBootPlugin.BOM_COORDINATES))
    liquibaseRuntime(enforcedPlatform(SpringBootPlugin.BOM_COORDINATES))

    annotationProcessor("org.projectlombok:lombok")
    testAnnotationProcessor("org.projectlombok:lombok")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    annotationProcessor("org.springframework:spring-context-indexer")

    asciidoctor("org.springframework.restdocs:spring-restdocs-asciidoctor")

    liquibaseRuntime("org.postgresql:postgresql")
    liquibaseRuntime("org.liquibase:liquibase-core")
    liquibaseRuntime("info.picocli:picocli:4.7.5")

    implementation("com.github.spotbugs:spotbugs-annotations")
    implementation("org.projectlombok:lombok")

    implementation("org.springframework.boot:spring-boot-devtools")
    implementation("org.springframework.boot:spring-boot-configuration-processor")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-aop")
    implementation("org.springframework.boot:spring-boot-starter-cache")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.session:spring-session-jdbc")
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")

    implementation("org.liquibase:liquibase-core")
    implementation("com.github.ben-manes.caffeine:caffeine")
    implementation("io.micrometer:micrometer-registry-prometheus")
    implementation("org.postgresql:postgresql")
    implementation("com.google.code.gson:gson")

    implementation("org.joinfaces:primefaces-spring-boot-starter:5.2.5")
    implementation("org.primefaces:primefaces:13.0.8:jakarta")
    implementation("org.primefaces.extensions:primefaces-extensions:13.0.8:jakarta")
    implementation("org.primefaces.themes:bootstrap:1.0.10")

    implementation("net.logstash.logback:logstash-logback-encoder:7.4")
    implementation("de.ruedigermoeller:fst:3.0.4-jdk17")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("org.springframework.restdocs:spring-restdocs-mockmvc")
    testImplementation("org.springframework.boot:spring-boot-testcontainers")
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:postgresql")
    testImplementation("org.testcontainers:selenium")
    testImplementation("org.seleniumhq.selenium:selenium-java")
}

java {
    sourceCompatibility = javaVersion
    targetCompatibility = javaVersion
}

node {
    version.set(nodeVersion)
    download.set(true)
}

idea {
    module {
        inheritOutputDirs = false
        outputDir = file(layout.buildDirectory.dir("resources/main/"))
    }
}

springBoot {
    buildInfo()
}

liquibase {
    activities {
        register("main") {
            arguments = mapOf(
                    "searchPath" to "$projectDir/src/main/resources",
                    "changeLogFile" to "db/changelog/db.changelog-master.xml",
                    "url" to "jdbc:postgresql://localhost:5432/spring-demo",
                    "username" to "dev",
                    "password" to "dev",
            )
        }
    }
    runList = "main"
}

spotbugs {
    toolVersion.set(spotbugsToolVersion)
    excludeFilter.set(file("spotbugs-exclude.xml"))
}

tasks.withType<SpotBugsTask> {
    reports {
        create("html").required.set(true)
        create("xml").required.set(true)
    }
}

pmd {
    toolVersion = pmdToolVersion
    ruleSetFiles(file("pmd.ruleset.xml"))
    ruleSets = listOf()
}

jacoco {
    toolVersion = jacocoToolVersion
}

spotless {
    java {
        eclipse().configFile("spotless.eclipseformat.xml")    // XML file dumped out by the Eclipse formatter
        indentWithSpaces()
        trimTrailingWhitespace()
        endWithNewline()

        // Eclipse formatter puts excess whitespace after lambda blocks
        //    funcThatTakesLambdas(x -> {} , y -> {} )	// what Eclipse does
        //    funcThatTakesLambdas(x -> {}, y -> {})	// what I wish Eclipse did
        custom("Lambda fix") {
            it.replace("} )", "})").replace("} ,", "},")
        }

        // Eclipse formatter screws up long literals with underscores inside of annotations (see issue #14)
        //    @Max(value = 9_999_999 L) // what Eclipse does
        //    @Max(value = 9_999_999L)  // what I wish Eclipse did
        custom("Long literal fix") {
            Pattern.compile("([0-9_]+) [Ll]").matcher(it).replaceAll("\$1L")
        }
    }
    format("misc") {
        target(fileTree(".") {
            include(".gitignore", "**/.gitignore", "*.kts", "*.md", "src/**/*.md", "infrastructure/**/*.sh", "src/**/*.sh")
            exclude("node_modules/**", "out/**", "build/**")
        })
        indentWithSpaces()
        trimTrailingWhitespace()
        endWithNewline()
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    options.compilerArgs.addAll(listOf("-Xlint:unchecked", "-Xlint:deprecation", "-parameters"))
}

tasks.bootRun {
    systemProperty("spring.output.ansi.enabled", "always")
    jvmArgs = listOf(
            "--add-opens=java.base/java.lang=ALL-UNNAMED",
            "--add-opens=java.base/java.math=ALL-UNNAMED",
            "--add-opens=java.base/java.util=ALL-UNNAMED",
            "--add-opens=java.base/java.util.concurrent=ALL-UNNAMED",
            "--add-opens=java.base/java.net=ALL-UNNAMED",
            "--add-opens=java.base/java.text=ALL-UNNAMED",
            "--add-opens=java.sql/java.sql=ALL-UNNAMED"
    )
    doFirst {
        println(classpath.files.toList())
    }
}

tasks.bootJar {
    archiveClassifier.set("boot")
    layered {
        enabled.set(true)
    }
}

tasks.jacocoTestReport {
    sourceDirectories.setFrom(files("${project.projectDir}/src/main/java"))
    classDirectories.setFrom(sourceSets.main.get().output.asFileTree)
    executionData.setFrom(fileTree(layout.buildDirectory.dir("jacoco")).include("*.exec"))
    dependsOn(tasks.test)
    reports {
        xml.required.set(true)
        html.required.set(true)
    }
}

tasks.asciidoctor {
    mustRunAfter(tasks.test)
    configurations("asciidoctor")
    sourceDir("src/docs/asciidoc")
    inputs.dir(layout.buildDirectory.dir("generated-snippets"))
    setOutputDir(layout.buildDirectory.dir("asciidoc/static/docs"))
    jvm {
        jvmArgs(
                "--add-opens=java.base/sun.nio.ch=ALL-UNNAMED",
                "--add-opens=java.base/java.io=ALL-UNNAMED"
        )
    }
    attributes(mapOf(
            "springbootversion" to VersionExtractor.forClass(SpringBootPlugin::class.java),
            "projectdir" to "$projectDir"
    ))
    doLast {
        copy {
            from(layout.buildDirectory.dir("asciidoc"))
            into(layout.buildDirectory.dir("resources/main"))
        }
    }
}

tasks.withType<Test> {
    outputs.dir(layout.buildDirectory.dir("generated-snippets"))
    useJUnitPlatform()
    jvmArgs = listOf(
            "-XX:+EnableDynamicAgentLoading",
            "--add-opens=java.base/java.lang=ALL-UNNAMED",
            "--add-opens=java.base/java.math=ALL-UNNAMED",
            "--add-opens=java.base/java.util=ALL-UNNAMED",
            "--add-opens=java.base/java.util.concurrent=ALL-UNNAMED",
            "--add-opens=java.base/java.net=ALL-UNNAMED",
            "--add-opens=java.base/java.text=ALL-UNNAMED",
            "--add-opens=java.sql/java.sql=ALL-UNNAMED"
    )
    testLogging {
        events = mutableSetOf(TestLogEvent.PASSED, TestLogEvent.SKIPPED, TestLogEvent.FAILED, /*TestLogEvent.STANDARD_OUT,*/ TestLogEvent.STANDARD_ERROR)
        showExceptions = true
        exceptionFormat = TestExceptionFormat.FULL
        showCauses = true
        showStackTraces = true
    }
}

val webpack = tasks.register<NodeTask>("webpack") {
    dependsOn(tasks.npmInstall)
    inputs.files("src/main/resources/static/javascript")
    outputs.dir("${layout.buildDirectory.get().asFile}/resources/main/static/javascript")
    script.set(project.file("node_modules/webpack/bin/webpack.js"))
    environment.put("NODE_OPTIONS", "--openssl-legacy-provider")
}

val webpackWatch = tasks.register<NodeTask>("webpackWatch") {
    dependsOn(tasks.npmInstall)
    script.set(project.file("node_modules/webpack/bin/webpack.js"))
    args.set(listOf("--watch", "--display-error-details"))
}

tasks.wrapper {
    gradleVersion = "8.7"
    distributionType = Wrapper.DistributionType.ALL
}

tasks {
    getByName("spotlessMisc").dependsOn(npmSetup)
    processResources.get().dependsOn(webpack, generateGitProperties, getByName("bootBuildInfo"))
    compileJava.get().dependsOn(processResources)
    spotbugsMain.get().dependsOn(asciidoctor)
    jar.get().dependsOn(asciidoctor, test)
    bootJar.get().dependsOn(jar, resolveMainClassName)
    test.get().finalizedBy(jacocoTestReport)
}
