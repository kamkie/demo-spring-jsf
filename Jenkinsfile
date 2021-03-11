stage('Build') {
    node('maven-docker') {
        ansiColor('xterm') {
            git 'https://github.com/kamkie/demo-spring-jsf.git'
            try {
                def jdk = tool 'jdk11'
                nodejs(nodeJSInstallationName: 'node14') {
                    withEnv(["JAVA_HOME=$jdk", "PATH=$jdk/bin:${env.PATH}", "HOST_FOR_SELENIUM=172.17.0.1"]) {
                        sh """
                        npm --version
                        node --version
                        docker version
                        java -version
                        docker info
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
