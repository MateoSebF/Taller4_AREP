# Web Server

In this laboratory project, you will explore various aspects of network connections. The work provides a solid foundation for understanding URL mechanisms, how sockets enable basic server-client communication, and how to build a web server running on port 35000 by default.

The server supports requests for HTML, CSS, JavaScript, and image files, and it also exposes a REST API that generates objects based on query parameters—all implemented using Java’s networking library.

A key feature of this project introduced in Laboratory 2 is the ability to define custom server logic with lambda functions, enabling flexible and efficient request handling.

Additionally, Laboratory 3 introduces an innovative feature: the implementation of classes that simulate a REST controller using annotations. This functionality effectively mimics the behavior of the Spring Boot framework, providing a familiar structure for those experienced with it.

In Laboratory 4, we introduced how to deploy this service using Docker and AWS. Additionally, we improved concurrency by implementing a thread pool to manage multiple requests efficiently. We also incorporated graceful shutdown mechanisms to terminate the server cleanly, ensuring proper resource management and a controlled shutdown process.

## Getting Started

This project is built in Java using Maven. No additional dependencies are required, except for the JUnit Jupiter dependency to run the project tests.


### Prerequisites

Before running this project, ensure you have the following installed on your system:

* Java Development Kit (JDK) 17 or more
    * Download and install from: [Oracle JDK or OpenJDK](https://www.oracle.com/co/java/technologies/downloads/)
    * Verify installation with:
        ```
        java -version
        ```
* Apache Maven (for dependency management and build automation)
    * Install from: [Maven Downloads](https://maven.apache.org/download.cgi#Installation)
    * Verify installation with:
        ```
        mvn -version
        ```
* Git (to clone the repository)
    * Install from: [Git Downloads](https://git-scm.com/downloads)
    * Verify installation with:
        ```
        git --version
        ```

### Installing

Follow these steps to set up and run the project in your local development environment:

Clone the Repository:

```
git clone https://github.com/MateoSebF/Taller4_AREP

```

Navigate to the Project Directory:

```
cd Taller4_AREP
```

Build the Project with Maven:

```
mvn clean compile
```

Run the Web Server:

```
mvn clean compile exec:java
```
or
```
java -cp "target/classes" co.edu.eci.arep.webserver.WebServer
```

### Architecture
The core architecture of this project revolves around the HTTP protocol. At its center, we have an HttpServer that manages incoming user requests. To ensure scalability and maintainability, the design includes two complementary components: HttpRequest and HttpResponse. These two classes standardize the input and output of the server, considering key aspects such as:

![Http protocol](readimages/http.svg)

To use this framework effectively, you need to understand how requests and responses work, particularly when handling them within lambda functions. This includes extracting query parameters, analyzing the URI, and more.

To ensure proper functionality, your lambda functions must return an HttpResponse, specifying the status code, headers, and body using the appropriate setter methods.

An important aspect to configure is the location from which the server will serve static files. By default, it uses the /src/main/resources/static folder.

Laboratory 2:

A complete example demonstrating the use of HTML, CSS, JavaScript, and images is already set up. You can find and test this example in the WebServer class.

Currently, the server supports the GET, POST, PUT, and UPDATE methods, each requiring the appropriate handler function. Here’s a complete usage example:

![Use example](readimages/example1.png)

Laboratory 3:

In Laboratory 3, the architecture is slightly modified to offer more flexibility. You will implement your methods with three new annotations:

1. @RestController: Indicates that a class serves as a REST API controller. Classes marked with this annotation are executed when you start the web server application, and their endpoints are exposed under /rest.

2. @GetMapping, @PostMapping, @PutMapping, @DeleteMapping: These annotations map methods to their respective HTTP request types. Each annotation accepts a value representing the final endpoint (e.g., /hello), which will be accessible via /rest/hello.

3. @RequestParam: This annotation represents a request parameter, accepting two values: the parameter name and a default value. It automatically maps query parameters from a request, simplifying both implementation and usage.

A complete example demonstrating the use of annotations is already set up. You can find an example in the classes in controller package.

You can return in your mapping methods HttpResponse or a string, if you return a string you will not be able to indicate the headers and the code.
### Test the REST serices
A simple way to test this project is by retrieving an image in different formats. You can achieve this in three ways:

Laboratory 1: Use the directory structure as set up in Laboratory 1.
Laboratory 2: Employ a lambda function that performs the query, as demonstrated in Laboratory 2.
Laboratory 3: Use the REST controller introduced in Laboratory 3 to read the folder, access the image, and return it.
Below are the endpoints to test the functionality:

Laboratory 1:
http://localhost:35000/images/placeholder.png

Laboratory 2:
http://localhost:35000/app/image

Laboratory 3:
http://localhost:35000/rest/image

All three requests should return the following image:

![Placeholder example](src/main/resources/static/images/placeholder.png)


For example, if you want to retrieve the index HTML file, simply navigate to:
http://localhost:35000/rest/index
This endpoint is implemented in the Greeting controller.

Use postman for test:
https://testeci.postman.co/workspace/TestEci-Workspace~1d36ca68-dbcc-4fe9-8928-836eb052ae5b/collection/33334270-3bd50790-7c1c-41fe-96f6-4a7a9b062d86?action=share&creator=33334270


Or import the json in the root directory:
AREP_FRAMEWORK.postman_collection.json

## Running the tests

To run the automated tests for this system, you can use Maven, which integrates well with JUnit for running tests. These tests help ensure that the application is working as expected and validate the behavior of the system.

You can run all tests in the system using the following Maven command:

```
mvn test
```

### Break down into end to end tests
The following tests verify the server's functionality by simulating HTTP requests for various file types—HTML, CSS, JavaScript, and images—and validating the server's responses. These tests ensure that the server correctly serves content and returns the appropriate HTTP status codes.

HTML Test Example:
For instance, the testMakeConnectionHTML() test confirms that a request for index.html responds with a 200 OK status. This verifies that the server is running and can handle HTML page requests as expected.

Laboratory 2

In Laboratory 2, an additional test class for HttpRequest was introduced. This test class ensures that the HttpRequest component correctly reads and processes HTTP requests, particularly verifying that the request body is accurately read as a String.

Laboratory 3

Laboratory 3 includes the HttpControllerTest, which automatically validates the responses for specific endpoints. This test verifies the status code, content type, and content for connections to endpoints such as /rest/index and /rest/image, ensuring that the REST API behaves as intended.

### And coding style tests

These tests ensure that the code follows standard Java coding conventions and best practices, including naming conventions, documentation, and formatting. These tests help maintain consistency, readability, and clarity across the project.

For example, the **testMethodNamingConvention()** test verifies that all method names follow the camelCase convention, a widely accepted naming style in Java. This helps maintain a consistent coding style across the projec

## Deployment  

To deploy this project, we provide a Docker-based solution. We create an image using a `Dockerfile` to configure the environment. This file specifies important aspects, such as the port configuration. This is crucial because, when deploying the application, you need to specify both the internal port used by the app and the external port to expose.  

Additionally, we include a `docker-compose.yml` file that automates the image creation process. You have two options for deployment:  

1. **Build the image locally** and push it to your own Docker repository.  
2. **Use the pre-built image** available in the following repository:  
   - [Docker Hub - mateosebf/webserver](https://hub.docker.com/repository/docker/mateosebf/webserver).  

Once you have access to the image, deployment can be done using cloud services like **Azure** or **AWS**. In this case, we focus on **AWS deployment**.  

### AWS Deployment  

To deploy the application on **AWS**, follow these steps:  

1. **Create an EC2 instance** according to your requirements.  
2. **Configure security rules** for the instance:  
   - Allow inbound connections to the port you want to expose (e.g., `42000`).  
3. **Connect to the EC2 instance** via SSH.  
4. **Install and configure Docker** on the instance.  
5. **Run the container** using the following command:  

   ```sh
   docker run -d -p 42000:6000 --name second mateosebf/webserver 
   ```
    ### Container Configuration  

- `6000` is the **internal port** used by the server (defined in the `Dockerfile` as an environment variable).  
- `42000` is the **exposed port** that will be accessible from the internet.  

Once the container is running, you can access the application via the **public DNS** provided by AWS.  

### Testing the System  

You can test the deployment using the `/index.html` endpoint, which provides an example of a CRUD system. Additionally, before creating the image, you can define your own `RestController` classes. When deployed, all the defined `RestController` endpoints will be automatically available.  

### Endpoint Structure  

- **REST API endpoints:** `/rest/{endpoint}`  
- **Lambda server endpoints:** `/app/{endpoint}`  

### Example Deployment  

![Deployment Example](readimages/deploy.png)  

### Link to Deployment Demo  

[Watch the deployment in action](https://pruebacorreoescuelaingeduco.sharepoint.com/sites/Reco842/Shared%20Documents/General/Recordings/Reuni%C3%B3n%20en%20_General_-20250226_204708-Grabaci%C3%B3n%20de%20la%20reuni%C3%B3n.mp4?web=1&referrer=Teams.TEAMS-WEB&referrerScenario=MeetingChicletGetLink.view)  


## Built With

* [Java](https://www.oracle.com/co/java/technologies/downloads/) - The programming language used
* [Maven](https://maven.apache.org/) - Dependency Management
* [JUnit](https://junit.org/junit5/) - Testing Framework for unit tests

## Versioning

We use [Conventional Commits](https://www.conventionalcommits.org/en/v1.0.0/) for versioning.  

## Authors

**Mateo Forero** - *Initial work* - [MateoSebF](https://github.com/MateoSebF)

## Acknowledgments

* Inspiration from various resources and tutorials on building simple HTTP servers.