# Spring Boot API

The aim of this mini project was to try to create a back end API that will allow users to:
 - Create accounts
 - Verify their new accounts via a token provided the the email address they supply
 - Login
 - Make further requests using a Json Web Token that is supplied upon successful login


## Set up instructions

The following properties have been partially filled in but you will complete them in the `application-dev.properties` file:

You will need to download MySQL server from https://dev.mysql.com/downloads/mysql/

### DATABASE PROPERTIES
- spring.datasource.url=jdbc:mysql:// `location of DB`
- spring.datasource.username= `MySQL username`
- spring.datasource.password= `MySQL password`
- flyway.baseline-on-migrate=true
- spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
- spring.jpa.database-platform = org.hibernate.dialect.MySQL5Dialect

In order to make use of the email verification you will need to sign up for a free Mailtrap account at: https://mailtrap.io

### MAIL PROPERTIES
- spring.mail.host=smtp.mailtrap.io
- spring.mail.port=2525
- spring.mail.username= `Mailtrap username`
- spring.mail.password= `Mailtrap password`

You will need to create your own secret key below; however it is up to you what form that will take e.g. it could be as
simple as `testkey` or as complicated as `K4M5N7Q8R9TBUCVDXFYGZJ3K4M6P7Q8SATBUDWEXFZH2J3M5N6P8R9SATC`.  There are a
number of great sites which will generate a strong key for you, such as:
- https://randomkeygen.com/
- https://www.allkeysgenerator.com/

### SECRET KEY
- spring.datasource.secretKey= `A secret key of your choice`


## Viewing endpoints in Swagger

Once you have completed the set up above and have started running the app, you can view and try out the available using
swagger at: http://localhost:8080/swagger-ui.html


## Testing with Postman
TBC

