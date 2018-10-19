dependencies {
    api(kotlin("reflect")) // To override kt version from exposed
    compile("org.jetbrains.exposed:exposed:0.10.5")
    runtimeOnly("org.mariadb.jdbc:mariadb-java-client:2.2.3")
}
