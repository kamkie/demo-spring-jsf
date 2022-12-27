rootProject.name = "demo-spring-jsf"
gradle.startParameter.showStacktrace = ShowStacktrace.ALWAYS

pluginManagement {
    val springBootVersion: String by settings

    plugins {
        id("org.springframework.boot") version springBootVersion
    }
}
