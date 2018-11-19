dependencies {
    implementation("net.dv8tion:JDA:${prop("jda_version")}") {
        exclude(module = "opus-java")
    }
    implementation(project(":database"))
}