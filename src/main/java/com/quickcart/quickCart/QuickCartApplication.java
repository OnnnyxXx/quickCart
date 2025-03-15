package com.quickcart.quickCart;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching    //подключение Spring Cache
public class QuickCartApplication {

	public static void main(String[] args) {
		SpringApplication.run(QuickCartApplication.class, args);
	}

}
