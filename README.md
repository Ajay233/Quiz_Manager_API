# Spring Boot API

[![CircleCI](https://circleci.com/gh/Ajay233/stand_alone_api.svg?style=svg)](https://circleci.com/gh/Ajay233/stand_alone_api)

The aim of this mini project was to try to create a back end API that will allow users to:
 - Create and manage their accounts
 - Verify their new accounts via a token sent to the email address they supply during sign up
 - Login
 - Create and manage quizzes

 The app serves a React front end which is live and usable at: https://quiz-hosting-and-management.herokuapp.com/
 The repo for the front end can be viewed at: https://github.com/Ajay233/react_redux_front_end

## Set up instructions

The app makes use of of development and test databases which are built and run in Docker containers so you won't need to
download and set up MySQL and MySQL server.  You will however need to download Docker from: https://www.docker.com/products/docker-desktop

You will also need maven installed in order to run the app later on.  To check if you have maven installed, try running
`mvn -version` in the command line.  If you have maven installed you will see the version details returned.  If not you can
install maven by running: `brew install maven` or if you are using windows you can download and install Maven from https://maven.apache.org/download.cgi

### 1. Fork this repository
First you will need to fork this repository so that you can clone the forked repo to your own machine.


### 2. Setting up the Docker compose file
The `docker-compose.yml` file has been mostly completed but you will have to create your own credentials.

```
    environment:
      - MYSQL_ROOT_PASSWORD=
      - MYSQL_DATABASE=
      - MYSQL_USER=
      - MYSQL_PASSWORD=
```
You will need to set:
1. The root password
2. The database
3. The username
4. The password

These details need to be repeated in the environment section for `practice-test-db`.

### 3. Setting up the `dev` properties file

Next you will need to update a number of properties files.  The following properties have been partially filled in but
you will complete them in the `application-dev.properties` file:

#### DATABASE PROPERTIES
- spring.datasource.url=jdbc:mysql://127.0.0.1:32793/`Database specified in docker-compose file`
- spring.datasource.username= `Username specified in docker-compose file`
- spring.datasource.password= `Password specified in docker-compose file`
- flyway.baseline-on-migrate=true
- spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
- spring.jpa.database-platform = org.hibernate.dialect.MySQL5Dialect

In order to make use of the email verification you will need to sign up for a free Mailtrap account at: https://mailtrap.io

#### GMAIL PROPERTIES
- spring.mail.host=smtp.gmail.io
- spring.mail.port=587
- spring.mail.username= `Your Gmail username`
- spring.mail.password= `Your Gmail password`

Unfortunately gmail won't initially let outside services, such as this, send emails from your account as it is set up to block
them.  You will need to:
1. Go into `manage your google account`

2. Click on the `security` tab on the left hand menu

3. Scroll down to the section `Less secure app access` and turn on access

#### SECRET KEY for JWT creation

You will need to create your own secret key below; however it is up to you what form that will take e.g. it could be as
simple as `testkey` or as complicated as `K4M5N7Q8R9TBUCVDXFYGZJ3K4M6P7Q8SATBUDWEXFZH2J3M5N6P8R9SATC`.  There are a
number of great sites which will generate a strong key for you, such as:
- https://randomkeygen.com/
- https://www.allkeysgenerator.com/

Once you have done this, you can set the key for the property below
- spring.datasource.secretKey= `A secret key of your choice`


### 4. Setting up the `test` properties

In order to finish off the set up, you will need to apply the same details as above to the `application-test.properties`
file in: `src/test/resources/application-test.properties`

The only difference is under the **DATABASE** properties, the first line will be slightly different from the application-dev properties.
Make sure it reads as follows:
`spring.datasource.url=jdbc:mysql://127.0.0.1:32795/resttest`

Once you have done this, update the `application.properties` file (line 1), by changing `spring.profiles.active=ci` to
`spring.profiles.active=test`.  This will tell spring to use the application-test.properties file when running tests and
setting up the test database.  Due to the CircleCI continuous integration set up, any work that's pushed to github has to use
the `application-ci.properties` file so that CircleCI can set up its own version of the test database and run all of the test.

## Running the app

### 1. Docker

In the command line and enter the following command:

`docker-compose up -d`

This will fire up the containers for the dev and test databases.  If it's the first time running this, it will create the
databases.  by using the `-d` flag the command is run in a detached state, leaving the command line free for further commands
to be entered.

### 2. Maven

Next, enter the following command:

`mvn spring-boot: run`

This will build and run the app.  If you see the following lines, the app will be up and running:

```
2020-04-21 21:38:49.424  INFO 31889 --- [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat started on port(s): 8080 (http) with context path ''
2020-04-21 21:38:49.426  INFO 31889 --- [           main] com.apiTest.ApiTestApplication           : Started ApiTestApplication in 19.429 seconds (JVM running for 19.713)
```

### Demo credentials

Use the credentials below to try out the app if you do not want to sign up for an account.  If you are worried about signing up for an account, please note that passwords are encrypted using Bcrypt so they are securely encrypted before they are saved in the database and no one will be able to see what the password is.  You also have the option to delete your account which will permanently delete it from the database.  No information will be held in relation to the account after it's deletion.

| Username | Password | Permission level |
|----------|----------|------------------|
| test1@test.com | demoUser123 | USER |
| test3@test.com | readOnly123 | READ-ONLY |
| test5@test.com | admin123 | ADMIN |

## Viewing endpoints in Swagger

Once you have completed the set up above and have started running the app, you can view and try out the available using
swagger at: http://localhost:8080/swagger-ui.html

Swagger shows all of the available endpoints and the classes used.

When testing endpoints other than login and sign up, you will need to ensure you have set the JWT so all headers for subsequent requests will include
it otherwise access is denied.  To set the JWT you need to:

1. Login using the login endpoint.
2. Copy the JWT
3. Click the Authorize button
4. Then enter Bearer, followed by the token e.g. `Bearer abcdefg1234`
5. Then click authorize and this will now set up you up to be able to use the endpoints


## Testing with Postman
The endpoints can all be tested via postman as you can set up a header within the postman app.  For any endpoints other than the
`auth` endpoints, you will need to include `Authorization: Bearer + a valid JWT token` in the header.  Simply sign up and then log
in to get a JWT in the response body which you can use to access the other endpoints.

The base url of the app is `https://quiz-manager-api.herokuapp.com/`


