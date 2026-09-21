# ---------- Stage 1: build ----------
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /build

# Copy pom dulu supaya layer dependency ter-cache selama pom.xml tidak berubah
COPY pom.xml .
RUN mvn -B -q dependency:go-offline

COPY src ./src
RUN mvn -B -q package -DskipTests

# ---------- Stage 2: runtime ----------
FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=build /build/target/*.jar /app/app.jar

# Folder upload gambar (app.storage.image-dir=uploads/images), di-mount dari host lewat compose
RUN mkdir -p /app/uploads/images && chmod 777 /app/uploads /app/uploads/images

EXPOSE 8080

# Semua konfigurasi datang dari environment (.env lewat docker compose).
# JAVA_OPTS bisa diatur dari .env, misalnya batas heap.
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar /app/app.jar"]
