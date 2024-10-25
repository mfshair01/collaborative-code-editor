package com.example.collaborative_code_editor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class CollaborativeCodeEditorApplication {
	public static void main(String[] args) {
		SpringApplication.run(CollaborativeCodeEditorApplication.class, args);
	}
}
