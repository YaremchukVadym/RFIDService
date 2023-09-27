# SocialNetAPI

SocialNetAPI is a project that leverages various technologies to develop a RESTful API for a social networking platform. This API enables users to create an account, post content, write comments, like posts, and interact with other users.

## Technologies Used

- **Spring Boot**: This project is built using Spring Boot, simplifying configuration and deployment.

- **Spring Data JPA**: It facilitates easy database access and data management.

- **Spring Data REST**: Automatically generates RESTful endpoints for domain objects.

- **Spring Security**: Ensures the security of the application, including authentication and authorization.

- **Project Lombok**: Simplifies Java code with features like automatic generation of getters, setters, and more.

- **Gson**: Used for handling JSON data, including serialization and deserialization.

- **JSON Web Token (JWT)**: Enables the creation and verification of JWT tokens for user authentication.

- **PostgreSQL**: The chosen database for storing application data.

## API Endpoints

### Authorization

- **Sign In User**
    - `POST /api/auth/signin` - Sign in a user.

- **Register User**
    - `POST /api/auth/signup` - Register a new user.

### User

- **Currently Logged In User**
    - `GET /api/user/` - Retrieve information about the currently logged-in user.

- **User Data**
    - `GET /api/user/:userId` - Retrieve user data by user ID.

- **Update User**
    - `POST /api/auth/update` - Update user information.

- **List of Users**
    - `GET /api/user/search/:username` - Get a list of users by username.

### Post

- **Create New Post**
    - `POST /api/post/create` - Create a new post.

- **Delete Post**
    - `POST /api/post/:postId/delete` - Delete a post by post ID.

- **All Posts**
    - `GET /api/post/all` - Retrieve all posts.

- **User Posts**
    - `GET /api/post/user/posts` - Retrieve all posts by a user.

### Comment

- **Create Comment to Post**
    - `POST /api/comment/:postId/create` - Create a comment for a post.

- **All Comments to Post**
    - `GET /api/comment/:postId/all`
    - Retrieve all comments for a post.

- **Delete Comment**
    - `POST /api/comment/:commentId/delete` - Delete a comment by comment ID.

### Image

- **Upload Image to User**
    - `POST /api/image/upload` - Upload an image to a user's profile.

- **Upload Image to Post**
    - `POST /api/image/:postId/upload` - Upload an image to a post.

- **Profile Image**
    - `GET /api/image/profileImage` - Retrieve the profile image of a user.

- **Image to Post**
    - `GET /api/image/:postId/image` - Retrieve an image associated with a post.

# Project Setup

To successfully run this project, follow these steps:

#### Step 1: Create the Database

1. Open PgAdmin or a PostgreSQL management tool.
2. Create a new database with the name you have defined (usually `yourDataBase`).

#### Step 2: Configure `application.properties`

In the project's root directory, find the `application.properties` file and configure it as follows:

```properties
spring.datasource.driverClassName=org.postgresql.Driver
spring.datasource.url=jdbc:postgresql://localhost:5432/yourDataBase
spring.datasource.username=your_username
spring.datasource.password=your_password

# Create or update tables
spring.jpa.hibernate.ddl-auto=create

spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.properties.hibernate.show_sql=true
```

#### Step 3: Run the Project 
Run the project from your integrated development environment (IDE) or use Maven to run it with the following command:

shell:
mvn spring-boot:run

The project should start successfully, and you can interact with it through the specified API endpoints.

Make sure PostgreSQL is running and listening on port 5432 on your computer if necessary.

Now your project should be ready to work with the configured PostgreSQL database. Enjoy using it!

Access the API at  http://localhost:8080/api