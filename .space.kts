/**
* JetBrains Space Automation
* This Kotlin-script file lets you automate build activities
* For more info, see https://www.jetbrains.com/help/space/automation.html
*/

job("build") {
    container(image = "gradle:7.5-jdk17", displayName = "gradle") {
        kotlinScript { api ->
            api.gradlew("build")

            // run publish task for release branches
            if (api.gitBranch().contains("release")) {
                api.gradlew("publish")
            }
        }
    }
}
