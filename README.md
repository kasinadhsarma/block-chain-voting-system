# Blockchain Voting System (VoteX)

A secure, transparent, and tamper-resistant electronic voting system built using blockchain technology and Spring Boot.

## Table of Contents
- [Overview](#overview)
- [Features](#features)
- [Architecture](#architecture)
- [Technologies Used](#technologies-used)
- [Getting Started](#getting-started)
  - [Prerequisites](#prerequisites)
  - [Installation](#installation)
  - [Running the Application](#running-the-application)
- [Development](#development)
- [Deployment](#deployment)
- [Security Considerations](#security-considerations)
- [Contributing](#contributing)
- [License](#license)

## Overview

VoteX is a blockchain-based voting platform that ensures election integrity through immutable records, cryptographic security, and decentralized validation. The system provides a reliable mechanism for conducting elections with transparency while maintaining voter privacy and preventing tampering.

## Features

- **Blockchain-based Vote Storage**: All votes are recorded in a blockchain, ensuring immutability and auditability
- **Cryptographic Security**: Uses SHA-256 hashing and RSA asymmetric encryption
- **Proof-of-Work Consensus**: Ensures blockchain integrity through mining with adjustable difficulty
- **Election Management**: Create, configure, and manage multiple elections
- **Candidate Registration**: Add and manage candidates for each election
- **Voter Registration and Verification**: Secure voter registration with verification
- **Vote Casting**: Secure and private vote casting process
- **Real-time Results**: View election results in real-time once voting has concluded
- **Auditability**: Full transparency of the voting process while maintaining voter privacy

## Architecture

The system is built on a layered architecture:

### Core Components:

1. **Blockchain Layer**:
   - `Block`: Individual blocks containing batches of votes
   - `Blockchain`: The chain of blocks with proof-of-work consensus

2. **Cryptography Services**:
   - `HashUtil`: SHA-256 hash generation
   - `RSAUtil`: Asymmetric key generation and signature verification

3. **Data Models**:
   - `Election`: Represents an election with start/end dates and candidates
   - `Candidate`: Represents a candidate in an election
   - `Voter`: Registered voter with verification status
   - `Vote`: A vote cast by a voter for a specific candidate in an election

4. **Services**:
   - `ElectionService`: Manages election operations
   - `VotingService`: Handles vote casting and verification

5. **Controllers**:
   - `ElectionController`: API endpoints for election management
   - `VoteController`: API endpoints for voting operations

### Data Flow:

1. Admin creates an election and adds candidates
2. Voters register and are verified
3. Voters cast votes which are signed with their private keys
4. Votes are verified and added to the transaction pool
5. Blockchain mines blocks containing batches of vote transactions
6. Results can be viewed while maintaining voter privacy

## Technologies Used

- **Backend**: Java 11, Spring Boot 2.7.14
- **Database**: H2 Database (embedded)
- **Security**: Spring Security
- **Blockchain**: Custom implementation with proof-of-work consensus
- **Cryptography**: SHA-256 hashing, RSA asymmetric encryption
- **Build Tool**: Maven

## Getting Started

### Prerequisites

- Java JDK 11 or higher
- Maven 3.6+

### Installation

1. Clone the repository:
   ```bash
   git clone https://github.com/yourusername/block-chain-voting-system.git
   cd block-chain-voting-system
   ```

2. Build the application:
   ```bash
   mvn clean install
   ```

### Running the Application

1. Start the application:
   ```bash
   mvn spring-boot:run
   ```

2. The application will be available at:
   ```
   http://localhost:8081
   ```

3. Access the H2 database console:
   ```
   http://localhost:8081/h2-console
   ```
   - JDBC URL: `jdbc:h2:mem:votingdb`
   - Username: `sa`
   - Password: [leave empty]

## Development

### Project Structure

```
/src
  /main
    /java/com/votex
      /blockchain       # Blockchain implementation
      /config           # Application configuration
      /controller       # REST API controllers
      /crypto           # Cryptographic utilities
      /model            # Data models
      /repository       # Data access layer
      /service          # Business logic services
    /resources
      application.yml   # Application configuration
```

### Key Files and Their Responsibilities

- `Block.java`: Defines blockchain block structure with transactions, hashing
- `Blockchain.java`: Manages the chain, provides proof-of-work mining
- `Vote.java`: Represents an individual vote with signatures
- `Election.java`: Defines an election with candidates and time period
- `ElectionService.java`: Business logic for election management
- `VotingService.java`: Handles vote casting and verification logic

## Deployment

### Docker Deployment

1. Build the Docker image:
   ```bash
   docker build -t blockchain-voting-system .
   ```

2. Run the container:
   ```bash
   docker run -p 8081:8081 blockchain-voting-system
   ```

### Cloud Deployment (AWS)

1. **EC2 Instance**:
   - Launch EC2 instance with Java 11
   - Clone repository and build with Maven
   - Run as a service using systemd

2. **AWS Elastic Beanstalk**:
   ```bash
   # Install EB CLI
   pip install awsebcli
   
   # Initialize EB application
   eb init blockchain-voting-system --platform java
   
   # Deploy
   eb create blockchain-voting-env
   ```

### Production Considerations

1. **Database**: Replace H2 with PostgreSQL or MySQL:
   ```yaml
   # Production application.yml
   spring:
     datasource:
       url: jdbc:postgresql://[host]:[port]/votingdb
       username: [username]
       password: [password]
       driver-class-name: org.postgresql.Driver
   ```

2. **Security**:
   - Enable HTTPS with proper SSL certificates
   - Implement rate limiting
   - Add IP whitelisting for admin functions

3. **Scaling**:
   - Use load balancing for multiple instances
   - Consider distributed blockchain network for full decentralization

## Security Considerations

- **Vote Privacy**: Votes are linked to voter IDs in an encrypted manner
- **Immutability**: Blockchain ensures votes cannot be altered once cast
- **Verification**: RSA signatures ensure vote authenticity
- **Double-voting Prevention**: System tracks voter status to prevent multiple votes
- **DDoS Protection**: Implement rate limiting and challenge-response systems

## Contributing

1. Fork the repository
2. Create a feature branch: `git checkout -b feature-name`
3. Commit changes: `git commit -m 'Add feature'`
4. Push to branch: `git push origin feature-name`
5. Submit a pull request

## License

This project is licensed under the MIT License - see the LICENSE file for details.