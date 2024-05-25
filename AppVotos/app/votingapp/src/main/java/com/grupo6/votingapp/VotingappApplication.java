package com.grupo6.votingapp;

import java.util.TimeZone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import jakarta.annotation.PostConstruct;

@SpringBootApplication
public class VotingappApplication {

	@PostConstruct
    void started() {
        // Set the default time zone to Portugal
        TimeZone.setDefault(TimeZone.getTimeZone("Europe/Lisbon"));
    }
	public static void main(String[] args) {
		SpringApplication.run(VotingappApplication.class, args);
	}

}
