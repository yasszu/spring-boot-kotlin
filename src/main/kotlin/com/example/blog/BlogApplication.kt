package com.example.blog

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.context.support.beans
import org.springframework.web.servlet.function.ServerRequest
import org.springframework.web.servlet.function.ServerResponse
import org.springframework.web.servlet.function.router

@SpringBootApplication
@EnableConfigurationProperties(BlogProperties::class)
class BlogApplication

fun main(args: Array<String>) {
    runApplication<BlogApplication>(*args) {
        addInitializers(beans())
    }
}

fun beans() = beans {
    bean<UserHandler>()
    bean<ArticleHandler>()
    bean { initializeData(ref(), ref()) }
    bean {
        router {
            "/api/user".nest {
                val handler = ref<UserHandler>()
                GET("/", handler::findAll)
                GET("/{login}", handler::find)
            }
            "/api/article".nest {
                val handler = ref<ArticleHandler>()
                GET("/", handler::findAll)
                GET("/{slug}", handler::find)
            }
        }
    }
}

fun initializeData(userRepository: UserRepository, articleRepository: ArticleRepository) {
    val smaldini = userRepository.save(User("smaldini", "Stéphane", "Maldini"))
    articleRepository.save(Article(
            title = "Reactor Bismuth is out",
            headline = "Lorem ipsum",
            content = "dolor sit amet",
            author = smaldini
    ))
    articleRepository.save(Article(
            title = "Reactor Aluminium has landed",
            headline = "Lorem ipsum",
            content = "dolor sit amet",
            author = smaldini
    ))
}

class UserHandler(private val repository: UserRepository) {

    fun findAll(req: ServerRequest) = ServerResponse.ok().body(repository.findAll())

    fun find(req: ServerRequest) = ServerResponse.ok().body(repository.findByLogin(req.pathVariable("login")))

}

class ArticleHandler(private val repository: ArticleRepository) {

    fun findAll(req: ServerRequest) = ServerResponse.ok().body(repository.findAll())

    fun find(req: ServerRequest) = ServerResponse.ok()
            .body(repository.findBySlug(req.pathVariable("slug"))
                    ?: throw IllegalArgumentException("Wrong article slug provided"))

}