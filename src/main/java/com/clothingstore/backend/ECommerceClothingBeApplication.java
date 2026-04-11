package com.clothingstore.backend;

import com.clothingstore.backend.config.LoyaltyProperties;
import com.clothingstore.backend.config.GeminiProperties;
import com.clothingstore.backend.config.OllamaProperties;
import com.clothingstore.backend.config.OpenRouterProperties;
import com.clothingstore.backend.config.VnpayProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({ VnpayProperties.class, LoyaltyProperties.class, GeminiProperties.class, OllamaProperties.class, OpenRouterProperties.class })
public class ECommerceClothingBeApplication {

	public static void main(String[] args) {
		SpringApplication.run(ECommerceClothingBeApplication.class, args);
	}

}
