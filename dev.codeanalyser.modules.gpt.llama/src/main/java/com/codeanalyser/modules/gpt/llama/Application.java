package com.codeanalyser.modules.gpt.llama;

import org.springframework.ai.ollama.OllamaChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@EnableDiscoveryClient
@SpringBootApplication
@RestController
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Autowired
	private OllamaChatClient chatClient;

	@PostMapping("/generate")
	public String generateData(@RequestBody String prompt) {
		if (prompt == null) {
			throw new IllegalArgumentException("Prompt cannot be null");
		}
		// Your logic to generate GPT data
		return chatClient.call(prompt);
	}

}
