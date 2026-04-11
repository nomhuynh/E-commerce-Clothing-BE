package com.clothingstore.backend;

import com.clothingstore.backend.config.LoyaltyProperties;
import com.clothingstore.backend.config.VnpayProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({ VnpayProperties.class, LoyaltyProperties.class })
public class ECommerceClothingBeApplication {

	public static void main(String[] args) {
		SpringApplication.run(ECommerceClothingBeApplication.class, args);
	}

}
