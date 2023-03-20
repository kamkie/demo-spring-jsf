import com.github.gradle.node.task.NodeTask
import com.github.spotbugs.snom.SpotBugsTask
import org.asciidoctor.gradle.base.process.ProcessMode
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import java.util.regex.Pattern

plugins {
    java
    jacoco
    idea
    pmd
    id("com.palantir.git-version") version "2.0.0"
    id("org.sonarqube") version "4.0.0.2929"
    id("com.gorylenko.gradle-git-properties") version "2.4.1"
    id("com.diffplug.spotless") version "6.17.0"
    id("com.github.ben-manes.versions") version "0.46.0"
    id("com.github.spotbugs") version "5.0.13"
    id("org.springframework.boot")
    id("org.liquibase.gradle") version "2.2.0"
    id("org.asciidoctor.jvm.convert") version "3.3.2"
    id("com.github.node-gradle.node") version "3.5.1"
    id("com.ofg.uptodate") version "1.6.3"
    id("com.adarshr.test-logger") version "3.2.0"
}

val gitVersion: groovy.lang.Closure<String> by extra
version = gitVersion()
group = "demo"

val javaVersion = JavaVersion.VERSION_17
val nodeVersion = "16.19.0"
val springBootVersion = properties["springBootVersion"]
val joinFacesVersion = "5.0.4"
val spotbugsToolVersion = "4.7.3"
val jacocoToolVersion = "0.8.8"
val pmdToolVersion = "6.52.0"
val testContainersVersion = "1.17.6"

repositories {
    mavenCentral()
    maven { url = uri("https://repository.primefaces.org") }
}

val asciidoctor = configurations.create("asciidoctor")

dependencies {
    asciidoctor(enforcedPlatform("org.springframework.boot:spring-boot-dependencies:$springBootVersion"))
    implementation(enforcedPlatform("org.springframework.boot:spring-boot-dependencies:$springBootVersion"))
    annotationProcessor(enforcedPlatform("org.springframework.boot:spring-boot-dependencies:$springBootVersion"))
    testAnnotationProcessor(enforcedPlatform("org.springframework.boot:spring-boot-dependencies:$springBootVersion"))
    liquibaseRuntime(enforcedPlatform("org.springframework.boot:spring-boot-dependencies:$springBootVersion"))

    annotationProcessor("org.projectlombok:lombok")
    testAnnotationProcessor("org.projectlombok:lombok")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    //annotationProcessor("org.springframework:spring-context-indexer")

    asciidoctor("org.springframework.restdocs:spring-restdocs-asciidoctor")

    liquibaseRuntime("org.postgresql:postgresql")
    liquibaseRuntime("org.liquibase:liquibase-core")
    liquibaseRuntime("info.picocli:picocli:4.7.1")

    // https://mvnrepository.com/artifact/com.github.spotbugs/spotbugs-annotations
    implementation("com.github.spotbugs:spotbugs-annotations:4.7.3")
    implementation("org.projectlombok:lombok")

    implementation("org.springframework.boot:spring-boot-devtools")
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

    implementation("org.joinfaces:primefaces-spring-boot-starter:$joinFacesVersion")
    implementation("org.primefaces:primefaces:12.0.0:jakarta")
    implementation("org.primefaces.extensions:primefaces-extensions:12.0.5:jakarta")
    implementation("org.primefaces.themes:bootstrap:1.0.10")
    implementation("com.google.code.gson:gson")
    implementation("de.ruedigermoeller:fst:3.0.4-jdk17")
    implementation("org.postgresql:postgresql")
    // https://mvnrepository.com/artifact/de.appelgriepsch.logback/logback-gelf-appender
    implementation("de.appelgriepsch.logback:logback-gelf-appender:1.5")
    // https://mvnrepository.com/artifact/com.squareup.okhttp3/okhttp
    implementation("com.squareup.okhttp3:okhttp")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("org.springframework.restdocs:spring-restdocs-mockmvc")
    testImplementation("org.seleniumhq.selenium:selenium-java")
    testImplementation("org.testcontainers:junit-jupiter:$testContainersVersion")
    testImplementation("org.testcontainers:postgresql:$testContainersVersion")
    testImplementation("org.testcontainers:selenium:$testContainersVersion")
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
        outputDir = file("${buildDir}/resources/main/")
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

sonarqube {
    properties {
        property("sonar.host.url", System.getenv("SONAR_URL") ?: "http://127.0.0.1:9000")
        property("sonar.projectName", "spring jsf project")
        property("sonar.projectKey", "${project.group}:${project.name}")
        property("sonar.jacoco.reportPaths", "${buildDir}/jacoco/test.exec")
        property("sonar.exclusions", "")
    }
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

tasks.compileJava {
    options.encoding = "UTF-8"
    options.compilerArgs.addAll(listOf("-Xlint:unchecked", "-Xlint:deprecation", "-parameters"))
}

tasks.compileTestJava {
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
    executionData.setFrom(fileTree("${buildDir}/jacoco").include("*.exec"))
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
    inputs.dir("${buildDir}/generated-snippets")
    setOutputDir(file("${buildDir}/asciidoc/static/docs"))
    inProcess = ProcessMode.JAVA_EXEC
    forkOptions {
        jvmArgs(
                "--add-opens=java.base/sun.nio.ch=ALL-UNNAMED",
                "--add-opens=java.base/java.io=ALL-UNNAMED"
        )
    }
    attributes(mapOf(
            "springbootversion" to springBootVersion,
            "projectdir" to "$projectDir"
    ))
    doLast {
        copy {
            from("${buildDir}/asciidoc")
            into("${buildDir}/resources/main")
        }
    }
}

tasks.withType<Test> {
    outputs.dir("${buildDir}/generated-snippets")
    useJUnitPlatform()
    jvmArgs = listOf(
            "--add-opens=java.base/java.lang=ALL-UNNAMED",
            "--add-opens=java.base/java.math=ALL-UNNAMED",
            "--add-opens=java.base/java.util=ALL-UNNAMED",
            "--add-opens=java.base/java.util.concurrent=ALL-UNNAMED",
            "--add-opens=java.base/java.net=ALL-UNNAMED",
            "--add-opens=java.base/java.text=ALL-UNNAMED",
            "--add-opens=java.sql/java.sql=ALL-UNNAMED"
    )
    testLogging {
        events("passed", "skipped", "failed", /*"standardOut",*/ "standardError")
        showExceptions = true
        exceptionFormat = TestExceptionFormat.FULL
        showCauses = true
        showStackTraces = true
    }
}

val webpack = tasks.register<NodeTask>("webpack") {
    dependsOn(tasks.npmInstall)
    inputs.files("src/main/resources/static/javascript")
    outputs.dir("${buildDir}/resources/main/static/javascript")
    script.set(project.file("node_modules/webpack/bin/webpack.js"))
}

val webpackWatch = tasks.register<NodeTask>("webpackWatch") {
    dependsOn(tasks.npmInstall)
    script.set(project.file("node_modules/webpack/bin/webpack.js"))
    args.set(listOf("--watch", "--display-error-details"))
}

tasks.wrapper {
    gradleVersion = "8.0.2"
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
    sonarqube.get().setDependsOn(listOf<Task>())
}
