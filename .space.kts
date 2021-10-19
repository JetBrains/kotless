import runtime.reactive.trigger

job("Kotless / Build") {
    container("openjdk:11") {
        shellScript {
            content = """
              ./gradlew build -x test --console=plain
          """
        }
    }
}

job("Kotless / Test") {
    container("openjdk:11") {
        shellScript {
            content = """
              ./gradlew test --console=plain 
          """
        }
    }
}

job("Kotless / Release") {
    startOn {
        gitPush {
            enabled = false
        }
    }

    container("openjdk:11") {
        shellScript {
            content = """
              ./gradlew publish
          """
        }
    }
}
