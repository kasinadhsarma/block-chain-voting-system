# Deployment Guide for Blockchain Voting System

This document provides detailed instructions for deploying the VoteX blockchain voting system in various environments.

## Table of Contents

- [Local Development Deployment](#local-development-deployment)
- [Docker Deployment](#docker-deployment)
- [Cloud Deployment](#cloud-deployment)
  - [AWS Deployment](#aws-deployment)
  - [Azure Deployment](#azure-deployment)
  - [Google Cloud Platform](#google-cloud-platform)
- [Production Considerations](#production-considerations)
- [Monitoring and Maintenance](#monitoring-and-maintenance)
- [Backup and Recovery](#backup-and-recovery)
- [Troubleshooting](#troubleshooting)

## Local Development Deployment

### Prerequisites

- Java JDK 11 or higher
- Maven 3.6+
- Git

### Steps

1. Clone the repository:
   ```bash
   git clone https://github.com/yourusername/block-chain-voting-system.git
   cd block-chain-voting-system
   ```

2. Configure application properties (optional):
   - Edit `src/main/resources/application.yml` for any custom settings

3. Build the application:
   ```bash
   mvn clean install
   ```

4. Run the application:
   ```bash
   mvn spring-boot:run
   ```

5. Access the application:
   - Web interface: `http://localhost:8081`
   - H2 Console: `http://localhost:8081/h2-console`

## Docker Deployment

### Prerequisites

- Docker installed on your machine
- Docker Compose (optional, for multi-container setup)

### Single Container Deployment

1. Create a `Dockerfile` in the root directory (if not present):
   ```dockerfile
   FROM adoptopenjdk:11-jre-hotspot
   WORKDIR /app
   COPY target/blockchain-voting-system-0.0.1-SNAPSHOT.jar app.jar
   EXPOSE 8081
   ENTRYPOINT ["java", "-jar", "app.jar"]
   ```

2. Build the Docker image:
   ```bash
   mvn clean package
   docker build -t blockchain-voting-system:latest .
   ```

3. Run the Docker container:
   ```bash
   docker run -d -p 8081:8081 --name votex blockchain-voting-system:latest
   ```

### Multi-Container Setup with Docker Compose

1. Create a `docker-compose.yml` file:
   ```yaml
   version: '3'
   services:
     app:
       build: .
       ports:
         - "8081:8081"
       environment:
         SPRING_PROFILES_ACTIVE: docker
       depends_on:
         - db
     
     db:
       image: postgres:13
       environment:
         POSTGRES_DB: votingdb
         POSTGRES_USER: votexuser
         POSTGRES_PASSWORD: votexpass
       volumes:
         - postgres-data:/var/lib/postgresql/data
   
   volumes:
     postgres-data:
   ```

2. Create Docker profile settings in `application-docker.yml`:
   ```yaml
   spring:
     datasource:
       url: jdbc:postgresql://db:5432/votingdb
       username: votexuser
       password: votexpass
       driver-class-name: org.postgresql.Driver
     jpa:
       database-platform: org.hibernate.dialect.PostgreSQLDialect
   ```

3. Run with Docker Compose:
   ```bash
   docker-compose up -d
   ```

## Cloud Deployment

### AWS Deployment

#### Using Elastic Beanstalk

1. Install AWS EB CLI:
   ```bash
   pip install awsebcli
   ```

2. Initialize Elastic Beanstalk:
   ```bash
   eb init blockchain-voting-system --platform java
   ```

3. Create an environment and deploy:
   ```bash
   eb create blockchain-voting-env
   ```

4. Configure environment variables:
   ```bash
   eb setenv SPRING_PROFILES_ACTIVE=aws
   ```

5. For database integration, create an RDS instance and configure connection properties in a `application-aws.yml` file.

#### Using EC2

1. Launch an EC2 instance with Amazon Linux 2
2. Install Java:
   ```bash
   sudo amazon-linux-extras install java-openjdk11
   ```

3. Install Git and clone the repository:
   ```bash
   sudo yum install git
   git clone https://github.com/yourusername/block-chain-voting-system.git
   ```

4. Build and run the application:
   ```bash
   cd block-chain-voting-system
   ./mvnw clean package
   java -jar target/blockchain-voting-system-0.0.1-SNAPSHOT.jar
   ```

5. Set up as a systemd service:
   ```bash
   sudo nano /etc/systemd/system/votex.service
   ```
   
   Add the following content:
   ```
   [Unit]
   Description=Blockchain Voting System
   After=network.target
   
   [Service]
   User=ec2-user
   WorkingDirectory=/home/ec2-user/block-chain-voting-system
   ExecStart=/usr/bin/java -jar target/blockchain-voting-system-0.0.1-SNAPSHOT.jar
   SuccessExitStatus=143
   TimeoutStopSec=10
   Restart=on-failure
   RestartSec=5
   
   [Install]
   WantedBy=multi-user.target
   ```
   
   Enable and start the service:
   ```bash
   sudo systemctl enable votex
   sudo systemctl start votex
   ```

### Azure Deployment

#### Azure App Service

1. Install Azure CLI:
   ```bash
   curl -sL https://aka.ms/InstallAzureCLIDeb | sudo bash
   ```

2. Login to Azure:
   ```bash
   az login
   ```

3. Create a resource group:
   ```bash
   az group create --name votex-resources --location eastus
   ```

4. Create an App Service plan:
   ```bash
   az appservice plan create --name votex-plan --resource-group votex-resources --sku B1 --is-linux
   ```

5. Create a web app:
   ```bash
   az webapp create --name votex-blockchain --resource-group votex-resources --plan votex-plan --runtime "JAVA|11-java11"
   ```

6. Deploy the application:
   ```bash
   mvn clean package
   az webapp deploy --resource-group votex-resources --name votex-blockchain --src-path target/blockchain-voting-system-0.0.1-SNAPSHOT.jar --type jar
   ```

### Google Cloud Platform

#### Google App Engine

1. Install Google Cloud SDK
2. Initialize SDK:
   ```bash
   gcloud init
   ```

3. Create an `app.yaml` file:
   ```yaml
   runtime: java11
   instance_class: F2
   ```

4. Deploy to App Engine:
   ```bash
   gcloud app deploy
   ```

#### Google Kubernetes Engine (GKE)

1. Create a `kubernetes/deployment.yaml`:
   ```yaml
   apiVersion: apps/v1
   kind: Deployment
   metadata:
     name: votex
   spec:
     replicas: 2
     selector:
       matchLabels:
         app: votex
     template:
       metadata:
         labels:
           app: votex
       spec:
         containers:
         - name: votex
           image: gcr.io/[YOUR-PROJECT]/blockchain-voting-system:latest
           ports:
           - containerPort: 8081
   ---
   apiVersion: v1
   kind: Service
   metadata:
     name: votex
   spec:
     type: LoadBalancer
     ports:
     - port: 80
       targetPort: 8081
     selector:
       app: votex
   ```

2. Build and push Docker image:
   ```bash
   docker build -t gcr.io/[YOUR-PROJECT]/blockchain-voting-system:latest .
   docker push gcr.io/[YOUR-PROJECT]/blockchain-voting-system:latest
   ```

3. Deploy to GKE:
   ```bash
   kubectl apply -f kubernetes/deployment.yaml
   ```

## Production Considerations

### Database Configuration

For production environments, configure an external database:

1. PostgreSQL:
   ```yaml
   spring:
     datasource:
       url: jdbc:postgresql://[host]:[port]/votingdb
       username: [username]
       password: [password]
       driver-class-name: org.postgresql.Driver
     jpa:
       database-platform: org.hibernate.dialect.PostgreSQLDialect
       hibernate:
         ddl-auto: validate
   ```

2. MySQL:
   ```yaml
   spring:
     datasource:
       url: jdbc:mysql://[host]:[port]/votingdb?useSSL=true
       username: [username]
       password: [password]
       driver-class-name: com.mysql.cj.jdbc.Driver
     jpa:
       database-platform: org.hibernate.dialect.MySQL8Dialect
       hibernate:
         ddl-auto: validate
   ```

### Security Configuration

1. HTTPS Configuration (application.yml):
   ```yaml
   server:
     port: 8443
     ssl:
       key-store: classpath:keystore.p12
       key-store-password: yourpassword
       key-store-type: PKCS12
       key-alias: tomcat
   ```

2. Generate a self-signed certificate (for testing):
   ```bash
   keytool -genkeypair -alias tomcat -keyalg RSA -keysize 2048 -storetype PKCS12 -keystore keystore.p12 -validity 3650
   ```

3. For production, obtain a proper SSL certificate from a trusted CA.

### Load Balancing and High Availability

1. Set up multiple instances behind a load balancer
2. Use sticky sessions if required
3. Configure health checks

### Blockchain Security

1. Increase proof-of-work difficulty for production:
   ```yaml
   blockchain:
     difficulty: 6  # Higher than development value
   ```

2. Consider implementing additional validation nodes

## Monitoring and Maintenance

### Spring Boot Actuator

Add Spring Boot Actuator for monitoring:

1. Add dependency to pom.xml:
   ```xml
   <dependency>
       <groupId>org.springframework.boot</groupId>
       <artifactId>spring-boot-starter-actuator</artifactId>
   </dependency>
   ```

2. Configure in application.yml:
   ```yaml
   management:
     endpoints:
       web:
         exposure:
           include: health,info,metrics
   ```

### Logging Configuration

Configure proper logging for production:

```yaml
logging:
  file:
    name: /var/log/votex/application.log
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss} - %msg%n"
    file: "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n"
  level:
    root: WARN
    com.votex: INFO
    org.springframework: WARN
```

### Backup Procedures

1. Database Backup:
   - PostgreSQL: `pg_dump -U username votingdb > backup.sql`
   - MySQL: `mysqldump -u username -p votingdb > backup.sql`

2. Blockchain Backup:
   - Implement a scheduled task to export blockchain data periodically

## Backup and Recovery

### Database Backup

Set up regular database backups:

1. Create a backup script:
   ```bash
   #!/bin/bash
   TIMESTAMP=$(date +"%Y%m%d_%H%M%S")
   BACKUP_DIR="/path/to/backups"
   
   # For PostgreSQL
   pg_dump -U [username] -h [host] votingdb > $BACKUP_DIR/votingdb_$TIMESTAMP.sql
   
   # Compress the backup
   gzip $BACKUP_DIR/votingdb_$TIMESTAMP.sql
   
   # Cleanup old backups (keep last 7 days)
   find $BACKUP_DIR -name "votingdb_*.sql.gz" -mtime +7 -delete
   ```

2. Set up a cron job:
   ```
   0 1 * * * /path/to/backup_script.sh
   ```

### Blockchain Data Backup

Implement a blockchain export feature and schedule regular backups.

## Troubleshooting

### Common Issues and Solutions

1. **Connection Issues:**
   - Verify database connectivity
   - Check network firewall settings
   
2. **Performance Issues:**
   - Analyze logs for slow queries
   - Check blockchain mining parameters
   - Monitor system resources
   
3. **Security Issues:**
   - Verify SSL certificate validity
   - Check for proper authentication configuration

### Log Analysis

Example log analysis commands:

```bash
# Find error logs
grep "ERROR" /var/log/votex/application.log

# Monitor logs in real-time
tail -f /var/log/votex/application.log

# Check for failed login attempts
grep "login failed" /var/log/votex/application.log
```

### Support Resources

- Report issues on GitHub: https://github.com/yourusername/block-chain-voting-system/issues
- Community forum: [Your forum URL]
- Documentation: [Your documentation URL]