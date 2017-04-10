pipeline {
  agent any
  stages {
    stage('clear') {
      steps {
        sh 'git clean -fx'
      }
    }
    stage('build') {
      steps {
        sh './gradlew clean build'
      }
    }
    stage('artefacts') {
      steps {
        archiveArtifacts '/build/lib/*.jar'
      }
    }
  }
}