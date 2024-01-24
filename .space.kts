/**
 * JetBrains Space Automation
 * This Kotlin-script file lets you automate build activities
 * For more info, see https://www.jetbrains.com/help/space/automation.html
 */

job("build") {
    container(image = "gradle:8.4-jdk21", displayName = "gradle") {
        env["HOST_FOR_SELENIUM"] = "172.17.0.1"

        kotlinScript { api ->
            api.gradlew("--no-daemon", "build")

            // run publish task for release branches
            if (api.gitBranch().contains("release")) {
                api.gradlew("publish")
            }
        }
    }
}
