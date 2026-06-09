# ---------- Build stage ----------
    FROM eclipse-temurin:21-jdk AS build
    WORKDIR /app
    
    # Copy wrapper + pom first to cache deps
    COPY .mvn/ .mvn
    COPY mvnw pom.xml ./
    RUN chmod +x mvnw && ./mvnw -q -DskipTests dependency:go-offline
    
    # Now add sources and build
    COPY src ./src
    RUN ./mvnw -q -DskipTests package
    
    # ---------- Runtime stage ----------
    FROM eclipse-temurin:21-jre-alpine
    WORKDIR /app
    
    # Copy the shaded Spring Boot jar from the build stage
    COPY --from=build /app/target/*.jar /app/app.jar
    
    # Render will pass PORT; Spring will bind to it via -Dserver.port
    ENV SPRING_PROFILES_ACTIVE=prod
    ENV JAVA_OPTS=""
    ENV PORT=8080
    EXPOSE 8080
    
    # Start the app
    ENTRYPOINT ["sh","-c","java $JAVA_OPTS -Dserver.port=${PORT} -jar /app/app.jar"]