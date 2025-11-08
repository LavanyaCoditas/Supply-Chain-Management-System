package com.project.supply.chain.management;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class SupplyChainManagementApplication {

	public static void main(String[] args) {
		SpringApplication.run(SupplyChainManagementApplication.class, args);
	}

}
