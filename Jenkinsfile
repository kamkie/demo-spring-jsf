node('maven-docker') {
    def gitProps = checkout scm
    def jdk = tool 'jdk17'
    stage("show tool versions") {
        echo "GIT_BRANCH=${gitProps.GIT_BRANCH}, GIT_COMMIT=${gitProps.GIT_COMMIT}"
        nodejs(nodeJSInstallationName: 'node16') {
            withEnv(["JAVA_HOME=$jdk", "PATH=$jdk/bin:${env.PATH}"]) {
                sh """
                npm --version
                node --version
                java -version
                docker version
                docker info
                """
            }
        }
    }
    stage("clean") {
        sh "./gradlew clean"
    }
    stage('Build jar') {
        ansiColor('xterm') {
            try {
                nodejs(nodeJSInstallationName: 'node16') {
                    withEnv(["JAVA_HOME=$jdk", "PATH=$jdk/bin:${env.PATH}", "HOST_FOR_SELENIUM=172.17.0.1"]) {
                        sh "./gradlew build"
                    }
                }
            } finally {
                junit 'build/test-results/*/*.xml'
                step([$class: 'JacocoPublisher'])
                archiveArtifacts(artifacts: 'build/screenshot/*')

                recordIssues enabledForFailure: true, tools: [spotBugs(pattern: 'build/reports/spotbugs/*.xml'),
                                                              pmdParser(pattern: 'build/reports/pmd/*.xml'),
                                                              mavenConsole(), java(), javaDoc()]
            }

        }
    }
    stage("build docker image") {
        sh """
        docker build -t demo-spring-jsf .
        """
    }
    stage("push docker image") {
        sh """
        docker tag demo-spring-jsf image-registry.openshift-image-registry.svc:5000/jenkins-dev/demo-spring-jsf:latest
        docker push image-registry.openshift-image-registry.svc:5000/jenkins-dev/demo-spring-jsf:latest
        """
    }
    stage("rollout") {
        sh """
        oc login --help https://\${OPENSHIFT_API_URL} --token=\$(cat /run/secrets/kubernetes.io/serviceaccount/token)
        oc project jenkins-dev
        oc rollout status dc/demo-spring-jsf -w
        """
    }
}
