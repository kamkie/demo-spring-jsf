import com.diffplug.spotless.FormatterFunc
import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import com.github.gradle.node.task.NodeTask
import com.github.spotbugs.snom.SpotBugsTask
import com.palantir.gradle.gitversion.CommonGitOperations
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.springframework.boot.gradle.plugin.SpringBootPlugin
import org.springframework.boot.gradle.util.VersionExtractor
import java.io.Serializable
import java.nio.charset.StandardCharsets
import java.util.regex.Pattern

buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath("org.liquibase:liquibase-core:5.0.3")
    }
}

plugins {
    java
    jacoco
    idea
    pmd
    id("com.palantir.git-version") version "5.0.0"
    id("com.gorylenko.gradle-git-properties") version "4.0.1"
    id("com.diffplug.spotless") version "8.8.0"
    id("com.github.ben-manes.versions") version "0.54.0"
    id("com.github.spotbugs") version "6.5.9"
    id("org.springframework.boot") version "4.1.0"
    id("org.liquibase.gradle") version "3.1.0"
    id("com.github.node-gradle.node") version "7.1.0"
    id("com.adarshr.test-logger") version "4.0.0"
}

// Use palantir git-version's configuration-cache-safe provider API instead of the legacy
// `gitVersion()` extra-closure. version() is the Provider<String> equivalent of
// `git describe --tags --always --first-parent` (+ ".dirty").
val commonGitOperations = objects.newInstance(CommonGitOperations.Default::class.java)
fun getProjectVersion(): String = try {
    commonGitOperations.version().get()
} catch (e: Exception) {
    "0.0.0-SNAPSHOT"
}

version = getProjectVersion()
group = "demo"

val javaVersion = JavaVersion.VERSION_25
val nodeVersion = file(".node-version").readText().trim()
// CI provisioners opt out explicitly; local builds keep the managed Node fallback by default.
val downloadNode = providers.gradleProperty("nodeDownload").map(String::toBoolean).orElse(true)
val spotbugsToolVersion = "4.9.8"
val jacocoToolVersion = "0.8.14"
val pmdToolVersion = "7.23.0"
val primefacesVersion = "15.0.17"
val gsonVersion = "2.14.0"
val jnaVersion = "5.19.1"
val seleniumVersion = "4.46.0"
val asciiDoctorJVersion = "3.0.1"
val picocliVersion = "4.7.7"
val spotbugsAnnotationsVersion = "4.10.2"
val logstashLogbackEncoderVersion = "9.0"
val joinfacesVersion = "6.1.0"
val primefacesThemesVersion = "1.1.0"
val rerunnerJupiterVersion = "2.1.6"
val caffeineVersion = "3.2.4"
val postgresqlVersion = "42.7.13"
val gradleWrapperVersion = "9.6.1"
val generatedSnippetsDir = layout.buildDirectory.dir("generated-snippets")
val generatedFrontendResourcesDir = layout.buildDirectory.dir("generated-resources/webpack")


repositories {
    mavenCentral()
    maven { url = uri("https://repository.primefaces.org") }
}

val asciidoctorRuntime: Configuration = configurations.create("asciidoctorRuntime")

dependencies {
    asciidoctorRuntime(platform(SpringBootPlugin.BOM_COORDINATES))
    developmentOnly(platform(SpringBootPlugin.BOM_COORDINATES))
    implementation(platform(SpringBootPlugin.BOM_COORDINATES))
    annotationProcessor(platform(SpringBootPlugin.BOM_COORDINATES))
    testAnnotationProcessor(platform(SpringBootPlugin.BOM_COORDINATES))
    liquibaseRuntime(platform(SpringBootPlugin.BOM_COORDINATES))

    annotationProcessor("org.projectlombok:lombok")
    testAnnotationProcessor("org.projectlombok:lombok")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    annotationProcessor("org.springframework:spring-context-indexer")

    asciidoctorRuntime("org.asciidoctor:asciidoctorj:$asciiDoctorJVersion")
    asciidoctorRuntime("org.asciidoctor:asciidoctorj-cli:$asciiDoctorJVersion")
    asciidoctorRuntime("org.springframework.restdocs:spring-restdocs-asciidoctor")

    liquibaseRuntime("org.postgresql:postgresql:$postgresqlVersion")
    liquibaseRuntime("org.liquibase:liquibase-core")
    liquibaseRuntime("info.picocli:picocli:$picocliVersion")

    developmentOnly("org.springframework.boot:spring-boot-devtools")

    implementation("com.github.spotbugs:spotbugs-annotations:$spotbugsAnnotationsVersion")
    implementation("org.projectlombok:lombok")

    implementation("org.springframework.boot:spring-boot-configuration-processor")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-aspectj")
    implementation("org.springframework.boot:spring-boot-starter-cache")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-restclient")
    implementation("org.springframework.boot:spring-boot-session")
    implementation("org.springframework.session:spring-session-jdbc")
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")

    implementation("org.springframework.boot:spring-boot-starter-liquibase")
    implementation("com.github.ben-manes.caffeine:caffeine:$caffeineVersion")
    implementation("io.micrometer:micrometer-registry-prometheus")
    implementation("org.postgresql:postgresql:$postgresqlVersion")
    implementation("com.google.code.gson:gson:$gsonVersion")
    implementation("net.logstash.logback:logstash-logback-encoder:$logstashLogbackEncoderVersion")

    implementation("org.joinfaces:primefaces-spring-boot-starter:$joinfacesVersion")
    implementation("org.primefaces:primefaces:$primefacesVersion:jakarta")
    implementation("org.primefaces.extensions:primefaces-extensions:$primefacesVersion:jakarta")
    implementation("org.primefaces.themes:bootstrap:$primefacesThemesVersion")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
    testImplementation("org.springframework.boot:spring-boot-resttestclient")
    testImplementation("org.springframework.boot:spring-boot-starter-data-jpa-test")
    testImplementation("org.springframework.boot:spring-boot-starter-session-jdbc-test")
    testImplementation("org.springframework.boot:spring-boot-starter-liquibase-test")
    testImplementation("org.springframework.boot:spring-boot-testcontainers")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("org.springframework.restdocs:spring-restdocs-mockmvc")
    testImplementation("org.testcontainers:testcontainers-junit-jupiter")
    testImplementation("org.testcontainers:testcontainers-postgresql")
    testImplementation("org.testcontainers:testcontainers-selenium")
    testImplementation("org.seleniumhq.selenium:selenium-java:$seleniumVersion")
    testImplementation("net.java.dev.jna:jna-platform:$jnaVersion")
    testImplementation("io.github.artsok:rerunner-jupiter:$rerunnerJupiterVersion")
}

java {
    sourceCompatibility = javaVersion
    targetCompatibility = javaVersion
}

node {
    version.set(nodeVersion)
    download.set(downloadNode)
}

idea {
    module {
        inheritOutputDirs = false
        outputDir = file(layout.buildDirectory.dir("resources/main/"))
    }
}

springBoot {
    buildInfo {
        // On local builds, exclude the volatile build timestamp so build-info.properties is
        // byte-identical across runs of the same commit. Otherwise bootBuildInfo's
        // `timeIfNotExcluded` input changes every invocation, invalidating processResources and
        // cascading a full test + asciidoctor + jar rebuild on every `build`/`test` even when
        // nothing changed. (Setting `time = null` is not enough — the plugin falls back to "now".)
        // On CI (fresh clean builds, nothing to cache) keep a real build.time so the published
        // artifact and actuator /info carry an accurate timestamp.
        if (!providers.environmentVariable("CI").isPresent) {
            excludes.add("time")
        }
    }
}

liquibase {
    activities {
        register("main") {
            arguments = mapOf(
                    "searchPath" to "$projectDir/src/main/resources",
                    "changelogFile" to "db/changelog/db.changelog-master.xml",
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
        leadingTabsToSpaces()
        trimTrailingWhitespace()
        endWithNewline()

        // Eclipse formatter puts excess whitespace after lambda blocks
        //    funcThatTakesLambdas(x -> {} , y -> {} )	// what Eclipse does
        //    funcThatTakesLambdas(x -> {}, y -> {})	// what I wish Eclipse did
        custom("Lambda fix", object : Serializable, FormatterFunc {
            override fun apply(text: String): String {
                return text.replace("} )", "})").replace("} ,", "},")
            }
        })


        // Eclipse formatter screws up long literals with underscores inside annotations (see issue #14)
        //    @Max(value = 9_999_999 L) // what Eclipse does
        //    @Max(value = 9_999_999L) // what I wish Eclipse did
        custom("Long literal fix", object : Serializable, FormatterFunc {
            override fun apply(text: String): String {
                return Pattern.compile("([0-9_]+) [Ll]").matcher(text).replaceAll($$"$1L")
            }
        })
    }
    format("misc") {
        target(fileTree(".") {
            include(".gitignore", "**/.gitignore", "*.kts", "*.md", "src/**/*.md", "infrastructure/**/*.sh", "src/**/*.sh")
            exclude(".gradle/**", "node_modules/**", "out/**", "build/**")
        })
        leadingTabsToSpaces()
        trimTrailingWhitespace()
        endWithNewline()
    }
}

tasks.withType<JavaCompile> {
    options.encoding = StandardCharsets.UTF_8.name()
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
            "--add-opens=java.base/java.time=ALL-UNNAMED",
            "--add-opens=java.sql/java.sql=ALL-UNNAMED"
    )
    doFirst {
        println(classpath.files.toList())
    }
}

val asciidoctorTask = tasks.register<JavaExec>("asciidoctor") {
    group = "documentation"
    description = "Generates the Spring REST Docs reference documentation."
    mustRunAfter(tasks.test)

    val sourceDirectory = layout.projectDirectory.dir("src/docs/asciidoc")
    val outputDirectory = layout.buildDirectory.dir("asciidoc/static/docs")
    inputs.dir(sourceDirectory)
    inputs.dir(generatedSnippetsDir)
    outputs.dir(outputDirectory)
    outputs.cacheIf("Asciidoctor output is fully declared") { true }

    classpath = asciidoctorRuntime
    mainClass.set("org.asciidoctor.cli.jruby.AsciidoctorInvoker")
    jvmArgs = listOf(
            "--sun-misc-unsafe-memory-access=allow",
            "--add-opens=java.base/sun.nio.ch=ALL-UNNAMED",
            "--add-opens=java.base/java.io=ALL-UNNAMED",
            "--enable-native-access=ALL-UNNAMED"
    )
    args(
            "--backend", "html5",
            "--destination-dir", outputDirectory.get().asFile.absolutePath,
            "--attribute", "lang=",
            "--attribute", "revnumber=unspecified",
            "--attribute", "snippets=${generatedSnippetsDir.get().asFile.absolutePath}",
            "--attribute", "springbootversion=${VersionExtractor.forClass(SpringBootPlugin::class.java)}",
            "--attribute", "projectdir=$projectDir",
            sourceDirectory.file("index.adoc").asFile.absolutePath
    )
}

tasks.bootJar {
    archiveClassifier.set("boot")
    from(asciidoctorTask) {
        into("BOOT-INF/classes/static/docs")
    }
    layered {
        enabled.set(true)
    }
}

tasks.jacocoTestReport {
    sourceDirectories.setFrom(files("${projectDir}/src/main/java"))
    classDirectories.setFrom(sourceSets.main.get().output.asFileTree)
    executionData.setFrom(fileTree(layout.buildDirectory.dir("jacoco")).include("*.exec"))
    dependsOn(tasks.test)
    reports {
        xml.required.set(true)
        html.required.set(true)
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
    providers.gradleProperty("selenium.mode")
        .orElse(providers.systemProperty("selenium.mode"))
        .orElse(providers.environmentVariable("SELENIUM_MODE"))
        .orNull
        ?.let { systemProperty("selenium.mode", it) }
    jvmArgs = listOf(
            "-XX:+EnableDynamicAgentLoading",
            "--enable-native-access=ALL-UNNAMED",
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

val unitTest = tasks.register<Test>("unitTest") {
    group = LifecycleBasePlugin.VERIFICATION_GROUP
    description = "Runs unit tests without integration or Selenium tests."
    testClassesDirs = sourceSets.test.get().output.classesDirs
    classpath = sourceSets.test.get().runtimeClasspath
    useJUnitPlatform {
        excludeTags("integration", "selenium")
    }
    extensions.configure<JacocoTaskExtension> {
        isEnabled = false
    }
}

tasks.withType<DependencyUpdatesTask> {
    rejectVersionIf { (candidate.group == "org.jacoco") && (candidate.version != currentVersion) }
    rejectVersionIf { isNonStable(candidate.version) && !isNonStable(currentVersion) }
}

fun isNonStable(version: String): Boolean {
    val stableKeyword = listOf("RELEASE", "FINAL", "GA").any { version.uppercase().contains(it) }
    val regex = "^[0-9,.v-]+(-r)?$".toRegex()
    val isStable = stableKeyword || regex.matches(version)
    return isStable.not()
}

val webpack = tasks.register<NodeTask>("webpack") {
    dependsOn(tasks.npmInstall)
    inputs.files(
            "package.json",
            "package-lock.json",
            "scripts/build.mjs"
    )
    inputs.files(fileTree("src/main/resources/static") {
        include("javascript/**", "css/**")
    })
    outputs.dir(generatedFrontendResourcesDir)
    script.set(File("$projectDir/scripts/build.mjs"))
    environment.put("FRONTEND_RESOURCES_DIR", "build/generated-resources/webpack")
    environment.put("NODE_ENV", "production")
}

val webpackWatch = tasks.register<NodeTask>("webpackWatch") {
    dependsOn(tasks.npmInstall)
    inputs.files(
            "package.json",
            "package-lock.json",
            "scripts/build.mjs"
    )
    inputs.files(fileTree("src/main/resources/static") {
        include("javascript/**", "css/**")
    })
    // bootRun serves this runtime resource directory, so watch rebuilds are visible immediately.
    outputs.dir(layout.buildDirectory.dir("resources/main/static/javascript"))
    script.set(File("$projectDir/scripts/build.mjs"))
    args.set(listOf("--watch"))
    environment.put("FRONTEND_RESOURCES_DIR", "build/resources/main")
    environment.put("NODE_ENV", "development")
}

tasks.wrapper {
    gradleVersion = gradleWrapperVersion
    distributionType = Wrapper.DistributionType.BIN
    distributionSha256Sum = "9c0f7faeeb306cb14e4279a3e084ca6b596894089a0638e68a07c945a32c9e14"
}

tasks {
    getByName("spotlessMisc").dependsOn(npmSetup)
    processResources {
        dependsOn(generateGitProperties, getByName("bootBuildInfo"))
        from(webpack)
    }
    compileJava.get().dependsOn(processResources)
    jar {
        dependsOn(asciidoctorTask, test)
        from(asciidoctorTask) {
            into("static/docs")
        }
    }
    bootJar.get().dependsOn(jar, resolveMainClassName)
    test {
        outputs.dir(generatedSnippetsDir)
    }
    check.get().dependsOn(jacocoTestReport)
}
