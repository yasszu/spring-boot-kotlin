package com.example.blog

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.web.servlet.function.ServerResponse
import org.springframework.web.servlet.function.router

@SpringBootApplication
@EnableConfigurationProperties(BlogProperties::class)
class BlogApplication {

    @Bean
    fun routes(userRepository: UserRepository, articleRepository: ArticleRepository) = router {
        "/api/user".nest {
            GET("/") { ServerResponse.ok().body(userRepository.findAll()) }
            GET("/{login}") { ServerResponse.ok().body(userRepository.findByLogin(it.pathVariable("login"))) }
        }
        "/api/article".nest {
            GET("/") { ServerResponse.ok().body(articleRepository.findAll()) }
            GET("/{slug}") {
                ServerResponse.ok().body(articleRepository.findBySlug(it.pathVariable("slug"))
                        ?: throw IllegalArgumentException("Wrong article slug provided"))
            }
        }
    }

}


fun main(args: Array<String>) {
    runApplication<BlogApplication>(*args)
}
