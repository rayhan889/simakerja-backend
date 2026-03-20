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
    apt-get install -y --no-install-recommends \
        curl \
        tesseract-ocr \
        tesseract-ocr-ind \
        libtesseract-dev \
        libleptonica-dev \
        libgomp1 && \
    rm -rf /var/lib/apt/lists/*

ENV LD_LIBRARY_PATH=/usr/lib/x86_64-linux-gnu:/usr/local/lib

ENV TESSDATA_PATH=/usr/share/tesseract-ocr/5/tessdata

RUN echo "=== Tessdata verification ===" && \
    ls -la ${TESSDATA_PATH}/ && \
    test -f ${TESSDATA_PATH}/ind.traineddata && \
    echo "ind.traineddata OK" || \
    (echo "ERROR: ind.traineddata not found!" && exit 1)

RUN groupadd --system appgroup && \
    useradd --system --gid appgroup --no-create-home appuser

WORKDIR /app

COPY --from=build /build/target/*.jar app.jar

RUN chown appuser:appgroup app.jar

USER appuser

ENV JAVA_OPTS="-XX:MaxRAMPercentage=75.0 -XX:+UseZGC"

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]