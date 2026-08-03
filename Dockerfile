# ---------- Stage 1: build the React frontend ----------
FROM node:22-alpine AS frontend-build
WORKDIR /app/frontend
COPY frontend/package*.json ./
RUN npm install
COPY frontend/ ./
RUN npm run build

# ---------- Stage 2: build the Spring Boot backend ----------
FROM maven:3.9-eclipse-temurin-17 AS backend-build
WORKDIR /app/backend
COPY backend/pom.xml ./
RUN mvn -B -q dependency:go-offline
COPY backend/src ./src
# Bundle the built frontend into the jar's static resources so the
# backend serves the UI and the API from one process.
COPY --from=frontend-build /app/frontend/dist ./src/main/resources/static
RUN mvn -B -q package -DskipTests

# ---------- Stage 3: runtime image ----------
FROM eclipse-temurin:17-jre
WORKDIR /app
ENV APP_DB_PATH=/app/data/mission-control.db
COPY --from=backend-build /app/backend/target/mission-control-backend-1.0.0.jar app.jar
EXPOSE 8080
VOLUME ["/app/data"]
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
