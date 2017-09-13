# Transaction Statistics

This backend service calculates and serves statistics of recent transaction amounts using an in-memory data store. It was built with [Spring Boot](https://projects.spring.io/spring-boot/) and [CQEngine](https://github.com/npgall/cqengine).

## How to Run

Maven is a prerequisite for running this project.
Run the following command start the project:

```
mvn spring-boot:run
```

You should then be able to access index.html at http://localhost:8080. On this page you can submit new transactions that will be saved in-memory and calculated in the statistics.