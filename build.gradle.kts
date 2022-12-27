import com.github.gradle.node.task.NodeTask
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import java.util.regex.Pattern

plugins {
    java
    jacoco
    idea
    pmd
    id("com.palantir.git-version") version "0.15.0"
    id("org.sonarqube") version "3.5.0.2730"
    id("com.gorylenko.gradle-git-properties") version "2.4.1"
    id("com.diffplug.spotless") version "6.12.0"
    id("com.github.ben-manes.versions") version "0.44.0"
    id("com.github.spotbugs") version "5.0.13"
    id("org.springframework.boot") version "3.0.1"
    id("org.liquibase.gradle") version "2.1.1"
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
val springBootVersion = "3.0.1"
val joinFacesVersion = "5.0.0"
val spotbugsToolVersion = "4.7.3"
val jacocoToolVersion = "0.8.8"
val pmdToolVersion = "6.52.0"
val snippetsDir = "build/generated-snippets"

repositories {
    mavenCentral()
    maven { url = uri("https://repository.primefaces.org") }
}

val asciidoctor = configurations.create("asciidoctor")

dependencies {
    asciidoctor(platform("org.springframework.boot:spring-boot-dependencies:$springBootVersion"))
    implementation(platform("org.springframework.boot:spring-boot-dependencies:$springBootVersion"))
    annotationProcessor(platform("org.springframework.boot:spring-boot-dependencies:$springBootVersion"))
    testAnnotationProcessor(platform("org.springframework.boot:spring-boot-dependencies:$springBootVersion"))
    liquibaseRuntime(platform("org.springframework.boot:spring-boot-dependencies:$springBootVersion"))

    annotationProcessor("org.projectlombok:lombok")
    testAnnotationProcessor("org.projectlombok:lombok")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
//    annotationProcessor("org.springframework:spring-context-indexer")

    asciidoctor("org.springframework.restdocs:spring-restdocs-asciidoctor")

    liquibaseRuntime("org.postgresql:postgresql")
    liquibaseRuntime("org.liquibase:liquibase-core")

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
    implementation("org.primefaces.extensions:primefaces-extensions:12.0.2:jakarta")
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
    testImplementation("io.github.bonigarcia:selenium-jupiter:4.3.2") {
        exclude(group = "org.slf4j", module = "slf4j-api")
    }
    testImplementation("org.seleniumhq.selenium:selenium-java:4.7.2")
    testImplementation("org.testcontainers:postgresql:1.17.6") {
        exclude(group = "log4j", module = "log4j")
        exclude(group = "org.slf4j", module = "slf4j-api")
    }
}

sourceSets {
    main {
        resources.srcDir("${buildDir}/generated/")
    }
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
        activities.register("main") {
            arguments = mapOf(
                    "changeLogFile" to "src/main/resources/db/changelog/db.changelog-master.xml",
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
    excludeFilter.set(layout.projectDirectory.file("spotbugs-exclude.xml"))
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
        property("sonar.jacoco.reportPaths", "${project.buildDir}/jacoco/test.exec")
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
            include(".gitignore", "**/.gitignore", "build.gradle.kts", "settings.gradle.kts", "*.md", "src/**/*.md", "infrastructure/**/*.sh", "src/**/*.sh")
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
    classpath = project.files(sourceSets.main.get().resources.srcDirs, classpath)
}

tasks.bootJar {
    archiveClassifier.set("boot")
    dependsOn(tasks.asciidoctor)
    layered {
        enabled.set(true)
    }
}

tasks.jacocoTestReport {
    reports {
        xml.required.set(true)
        html.required.set(true)
    }
}

tasks.asciidoctor {
    configurations("asciidoctor")
    inputs.dir(snippetsDir)
    dependsOn(tasks.test)
    sourceDir("src/docs/asciidoc")
    attributes(mapOf(
            "stylesheet" to "amies.css",
            "stylesdir" to "styles",
            "springbootversion" to springBootVersion,
            "projectdir" to "$projectDir"
    ))
    outputs.dir("build/resources/main")
    doLast {
        copy {
            from("build/docs/asciidoc/")
            into("build/resources/main/static/docs")
            include("index.html")
        }
    }
}

tasks.withType<Test>() {
    outputs.dir(snippetsDir)
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
        // set options for log level LIFECYCLE
        events("passed", "skipped", "failed", /*"standardOut",*/ "standardError")
        showExceptions = true
        exceptionFormat = TestExceptionFormat.FULL
        showCauses = true
        showStackTraces = true
    }
}

val springConfiguration = tasks.register<Copy>("springConfiguration") {
    inputs.files("$buildDir/classes/java/main")
    outputs.dir("$buildDir/generated")
    from(file("$buildDir/classes/java/main/META-INF/"))
    into(file("$buildDir/generated/META-INF/"))
    doLast {
        delete("$buildDir/classes/java/main/META-INF")
    }
}

val h2Tcp = tasks.register<JavaExec>("h2Tcp") {
    classpath(sourceSets.main.get().runtimeClasspath)
    mainClass.set("org.h2.tools.Console")
    args("-tcp")
    classpath = sourceSets["main"].runtimeClasspath
    setDependsOn(listOf<Task>())
}

val webpack = tasks.register<NodeTask>("webpack") {
    dependsOn(tasks.npmInstall)
    inputs.files("src/main/resources/static/javascript")
    outputs.dir("build/resources/main/static/javascript")
    script.set(project.file("node_modules/webpack/bin/webpack.js"))
}

val webpackWatch = tasks.register<NodeTask>("webpackWatch") {
    dependsOn(tasks.npmInstall)
    script.set(project.file("node_modules/webpack/bin/webpack.js"))
    args.set(listOf("--watch", "--display-error-details"))
}

tasks.wrapper {
    gradleVersion = "7.6"
    distributionType = Wrapper.DistributionType.ALL
}

tasks {
    getByName("spotlessMisc").dependsOn(npmSetup)
    processResources.get().dependsOn(webpack)
    generateGitProperties.get().mustRunAfter(processResources)
    getByName("bootBuildInfo").mustRunAfter(processResources)
    compileJava.get().dependsOn(processResources)
    springConfiguration.get().dependsOn(compileJava)
    spotbugsMain.get().dependsOn(compileTestJava)
    classes.get().dependsOn(springConfiguration)
    jar.get().dependsOn(spotlessCheck, spotbugsMain, spotbugsTest, pmdMain, pmdTest)
    test.get().finalizedBy(jacocoTestReport)
    sonarqube.get().setDependsOn(listOf<Task>())
}
