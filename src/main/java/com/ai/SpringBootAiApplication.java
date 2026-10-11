package com.ai;

import com.tabish.proproject.ProProjectApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * Backward compatibility launcher for IDE run configurations that still reference com.ai.SpringBootAiApplication.
 * Forwards execution to com.tabish.proproject.ProProjectApplication.
 */
@SpringBootApplication
@ComponentScan(basePackages = "com.tabish.proproject")
public class SpringBootAiApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProProjectApplication.class, args);
    }
}

