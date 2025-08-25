# Use a multi-stage build to create a smaller final image
FROM eclipse-temurin:21-jre-alpine
COPY ./target/*.jar app.jar
# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
# Expose the port the app runs on
EXPOSE 8083
# Health check to ensure the service is running
HEALTHCHECK --interval=30s --timeout=10s --start-period=5s --retries=3 \
  CMD curl -f http://localhost:8083/actuator/health || exit 1
# Set the default command to run the application
CMD ["java", "-jar", "app.jar"]