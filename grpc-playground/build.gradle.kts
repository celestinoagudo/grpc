import com.google.protobuf.gradle.id

plugins {
    id("java")
    id("com.google.protobuf") version "0.9.4"
}

group = "grind.twofourseven"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.google.protobuf:protobuf-java:4.33.0")
    implementation("io.grpc:grpc-netty-shaded:1.76.0")
    implementation("io.grpc:grpc-protobuf:1.76.0")
    implementation("io.grpc:grpc-stub:1.76.0")
    implementation("jakarta.annotation:jakarta.annotation-api:3.0.0")
    implementation("ch.qos.logback:logback-classic:1.5.19")
    implementation("tools.jackson.core:jackson-databind:3.0.0")
    implementation("org.xolstice.maven.plugins:protobuf-maven-plugin:0.6.1")
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")

}

sourceSets {
//    main {
//        proto {
//            srcDir("src/main/proto")
//        }
//        //other configuration
//    }
//    test {
//        proto {
//            srcDir 'src/sample_protofiles'
//        }
//    }
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:4.33.0"
    }
    plugins {
        id("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:1.76.0"
        }
    }
    generateProtoTasks {
        all().forEach {
            it.plugins {
                id("grpc") {
                    option("@generated=omit")
                }
            }
        }
    }
}

tasks.test {
    useJUnitPlatform()
}