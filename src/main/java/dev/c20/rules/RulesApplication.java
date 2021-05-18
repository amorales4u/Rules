package dev.c20.rules;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.*;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;


@SpringBootApplication
public class RulesApplication {


    public static void main(String[] args) {

        SpringApplication.run(RulesApplication.class, args);
    }



}
