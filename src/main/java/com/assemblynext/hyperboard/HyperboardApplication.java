package com.assemblynext.hyperboard;

import java.io.File;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class HyperboardApplication {

	public static void main(String[] args) {
		SpringApplication.run(HyperboardApplication.class, args);
	}

		// Adding Hotel Ratings Document to our MongoDB Collection - HotelRating 
		@Bean
		public CommandLineRunner createUploadFolders() {
		  return (args) -> {
			//create public folders on classpath for thumbnails and uploads
			String[] PATHS = {
				"./public",
				"./public/uploads",
				"./public/thumbs"
			};
			for (String path : PATHS) {
				File directory = new File(path);
				if (! directory.exists()){
					directory.mkdir();
				}
			}
		  };
	} 

}
