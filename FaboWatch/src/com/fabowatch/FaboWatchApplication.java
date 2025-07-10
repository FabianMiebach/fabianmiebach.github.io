package com.fabowatch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class FaboWatchApplication {

	public static void main(String[] args) {
		SpringApplication.run(FaboWatchApplication.class, args);

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}

		openBrowser("http://localhost:8080");
	}

	private static void openBrowser(String url) {
		try {
			String os = System.getProperty("os.name").toLowerCase();
			if (os.contains("win")) {
				Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + url);
			} else if (os.contains("mac")) {
				Runtime.getRuntime().exec("open " + url);
			} else if (os.contains("nix") || os.contains("nux") || os.contains("aix")) {
				Runtime.getRuntime().exec("xdg-open " + url);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
