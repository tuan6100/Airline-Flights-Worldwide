buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath("org.liquibase:liquibase-core:5.0.3")
    }
}

plugins {
    java
    id("org.springframework.boot") version "4.1.1"
    id("io.spring.dependency-management") version "1.1.7"
    id("org.liquibase.gradle") version "3.1.0"
}

description = "airline management"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}

repositories {
    mavenCentral()
}

configurations {
    liquibaseRuntime.get().extendsFrom(
        configurations.runtimeClasspath.get()
    )
}

dependencies {
    implementation(project(":core"))
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.projectlombok:lombok")
    implementation("org.springframework.boot:spring-boot-starter-liquibase")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:3.1.1")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-authorization-server")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.boot:spring-boot-starter-data-jpa-test")
    testRuntimeOnly("com.h2database:h2")
    testImplementation("junit:junit")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    annotationProcessor("org.projectlombok:lombok")
    runtimeOnly("org.postgresql:postgresql")
    liquibaseRuntime("org.liquibase:liquibase-core")
    liquibaseRuntime("info.picocli:picocli:4.7.7")
    liquibaseRuntime("org.postgresql:postgresql")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

liquibase {
    activities.register("main") {
        val DB_HOST = System.getenv("DB_HOST") ?: "localhost"
        val DB_PORT = System.getenv("DB_PORT") ?: "5432"
        val DB_NAME = System.getenv("DB_NAME") ?: "postgres"
        val DB_USERNAME = System.getenv("DB_USERNAME") ?: "postgres"
        val DB_PASSWORD = System.getenv("DB_PASSWORD")
        val passwordArguments = DB_PASSWORD?.let { mapOf("password" to it) } ?: emptyMap()
        val changelogFile = "db/changelog/db.changelog-master.json"
        val resourcesDir = sourceSets["main"].resources.srcDirs.first().absolutePath
        arguments = mapOf(
            "changelogFile" to changelogFile,
            "searchPath" to resourcesDir,
            "url" to "jdbc:postgresql://$DB_HOST:$DB_PORT/$DB_NAME",
            "username" to DB_USERNAME,
            "driver" to "org.postgresql.Driver"
        ) + passwordArguments
    }
    runList = "main"
}



tasks.register("prepareKotlinBuildScriptModel")
