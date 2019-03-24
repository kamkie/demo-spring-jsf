stage('Build') {
    node('maven-docker') {
        ansiColor('xterm') {
            def jdk = tool 'jdk11'
            def jdkHome = "$jdk/jdk-11.0.2+9"
            sh """
            docker info
            """
            git 'https://github.com/kamkie/demo-spring-jsf.git'
            try {
                nodejs(nodeJSInstallationName: 'node11') {
                    withEnv(["JAVA_HOME=$jdkHome", "PATH=$jdkHome/bin:${env.PATH}", "HOST_FOR_SELENIUM=172.17.0.1"]) {
                        sh """
                        npm --version
                        node --version
                        docker version
                        java -version
                        """
                        sh "./gradlew clean build"
                    }
                }
            } finally {
                junit 'build/test-results/*/*.xml'
                step([$class: 'JacocoPublisher'])
                archiveArtifacts(artifacts: 'build/screenshot/*')

                recordIssues enabledForFailure: true, tool: spotBugs(pattern: 'build/reports/spotbugs/*.xml')
                recordIssues enabledForFailure: true, tool: pmdParser(pattern: 'build/reports/pmd/*.xml')
                recordIssues enabledForFailure: true, tools: [mavenConsole(), java(), javaDoc()]
            }
        }
    }
}
