dependencies {
    api(kotlin("reflect")) // To override kt version from exposed
    compile("org.jetbrains.exposed:exposed:0.10.5")
    runtimeOnly("mysql:mysql-connector-java:8.0.8-dmr")
}
