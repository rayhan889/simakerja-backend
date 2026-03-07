# Dependencies cache
FROM eclipse-temurin:25-jdk-noble AS deps
WORKDIR /build

COPY pom.xml mvnw ./
COPY .mvn .mvn
RUN chmod +x mvnw && ./mvnw dependency:go-offline -B

# Build
FROM deps AS build
WORKDIR /build

COPY src src
RUN ./mvnw clean package -DskipTests -B

# Runtime
FROM eclipse-temurin:25-jre-noble AS runtime

RUN apt-get update && \
    apt-get install -y --no-install-recommends curl && \
    rm -rf /var/lib/apt/lists/*

RUN groupadd --system appgroup && \
    useradd --system --gid appgroup --no-create-home appuser

WORKDIR /app

COPY --from=build /build/target/*.jar app.jar

RUN chown appuser:appgroup app.jar

USER appuser

ENV JAVA_OPTS="-XX:MaxRAMPercentage=75.0 -XX:+UseZGC"

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]