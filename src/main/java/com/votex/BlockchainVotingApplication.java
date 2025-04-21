package com.votex;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan("com.votex.model")
@EnableJpaRepositories("com.votex.repository")
public class BlockchainVotingApplication {

    public static void main(String[] args) {
        SpringApplication.run(BlockchainVotingApplication.class, args);
    }
}