stage('Build') {
    node('maven-docker') {
        ansiColor('xterm') {
            def jdk = tool 'jdk11'
            sh """
            docker info
            """
            git 'https://github.com/kamkie/demo-spring-jsf.git'
            try {
                sh "JAVA_HOME=$jdk/jdk-11.0.2+9 JAVA_TOOL_OPTIONS='' HOST_FOR_SELENIUM=172.17.0.1 ./gradlew clean build"
                //sh "touch build/test-results/test/*.xml"
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
