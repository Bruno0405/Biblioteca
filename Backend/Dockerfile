####
# Build stage — uses GraalVM with native-image to produce a native binary
####
FROM quay.io/quarkus/ubi9-quarkus-graalvmce-builder-image:jdk-21 AS builder
WORKDIR /build

COPY .mvn/wrapper .mvn/wrapper
COPY mvnw pom.xml ./
RUN ./mvnw dependency:go-offline -B

COPY src src
RUN ./mvnw package -Dnative -DskipTests -B

####
# Runtime stage — minimal UBI image, no JVM needed
####
FROM registry.access.redhat.com/ubi9/ubi-minimal:9.7
WORKDIR /work
RUN chown 1001 /work \
    && chmod "g+rwX" /work \
    && chown 1001:root /work
COPY --from=builder --chown=1001:root --chmod=0755 /build/target/*-runner /work/application

EXPOSE 8080
USER 1001

ENTRYPOINT ["./application", "-Dquarkus.http.host=0.0.0.0"]
