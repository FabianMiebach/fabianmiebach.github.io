package com.fabowatch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.event.EventListener;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan("DB_Tables")
public class FaboWatchApplication {

	public static void main(String[] args) {
		SpringApplication.run(FaboWatchApplication.class, args);
	}

	@EventListener(ApplicationReadyEvent.class)
	public void openBrowser() {
		String url = "http://localhost:8080";

		try {
			String os = System.getProperty("os.name").toLowerCase();
			if (os.contains("win")) {
				Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + url);
			} else if (os.contains("mac")) {
				Runtime.getRuntime().exec("open " + url);
			} else if (os.contains("nix") || os.contains("nux") || os.contains("aix")) {
				Runtime.getRuntime().exec("xdg-open " + url);
			} else {
				System.out.println("Unsupported OS, please open browser manually at " + url);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}