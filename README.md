TedTalks Management App
A Spring Boot application for managing and analyzing TedTalk data. This application processes TedTalk information such as views, likes, and publication dates and provides insights through RESTful APIs.

Features
Add, update, delete, and retrieve TedTalk information.
Analyze the most influential speakers based on likes and views.
Identify the most influential TedTalk per year.
Prerequisites
Docker: Required to build and run the application using containers.
Maven: Required to build the project and generate the JAR file.
Assumptions
Maximum Number of Likes/Views:
The maximum value for likes and views is 9,223,372,036,854,775,807 (Java long).
Non-Numeric Likes/Views:
If non-numeric values are provided for likes or views, they are treated as 0.
Invalid Date Formats:
If the provided date is in an invalid format:
For an invalid month but a valid year, the application uses the current month and the provided year.
For an invalid year but a valid month, the application uses the current year and the provided month.
If both are invalid, the application uses the current date.
Future Enhancements (To Be Implemented If Time Allows)
Swagger Documentation:
Comprehensive API documentation using Swagger UI.
Spring Security:
Secure the APIs with authentication and authorization mechanisms.
Hibernate Caching:
Improve performance by enabling Hibernate second-level caching.
Flyway Database Migrations:
Implement database versioning and migrations using Flyway.
Steps to Run the Application
1. Clone the Repository
Run the following command to clone the project:

bash
Kodu kopyala
git clone https://github.com/tugcehilal/tedtalks-app.git
cd tedtalks-app
2. Build the Project
Generate the JAR file using Maven:

bash
Kodu kopyala
mvn clean install
Note: The generated JAR file will be placed in the target folder.

3. Build and Run Using Docker Compose
Run the following command to build and start the application:

bash
Kodu kopyala
docker-compose -f docker-compose-prod.yml up --build
This will:

Build the application image.
Start two containers:
MySQL Database (mapped to port 3306 inside the container).
Spring Boot Application (mapped to port 8082).
4. Test the Application
Once the containers are running:

Access the APIs at http://localhost:8082/api/tedtalks.
Example endpoints:
GET /api/tedtalks: Retrieve all TedTalks.
POST /api/tedtalks: Add a new TedTalk (pass JSON in the request body).
GET /api/tedtalks/influential-speakers: Retrieve the most influential speakers.
GET /api/tedtalks/most-influential-per-year: Retrieve the most influential TedTalk for each year.
Sample Request and Response
POST /api/tedtalks
Request Body:

json
Kodu kopyala
{
  "title": "How to Code Efficiently",
  "author": "Jane Doe",
  "date": "2022-09",
  "views": 50000,
  "likes": 300,
  "link": "http://example.com/talk"
}
Response:

json
Kodu kopyala
{
  "title": "How to Code Efficiently",
  "author": "Jane Doe",
  "date": "2022-09",
  "views": 50000,
  "likes": 300,
  "link": "http://example.com/talk"
}
Troubleshooting
Common Issues
Port Conflicts:

Ensure ports 8082 (application) and 3306 (database) are not in use by other applications.
To stop conflicting services, use:
bash
Kodu kopyala
sudo kill -9 $(sudo lsof -t -i:3306)
sudo kill -9 $(sudo lsof -t -i:8082)
JAR File Missing:

Ensure the mvn clean install step was successful. The JAR file should exist in the target folder.
Database Not Starting:

Verify that Docker is running and MySQL is configured correctly in the docker-compose-prod.yml file.
Notes
Required Tools:
Ensure Docker and Maven are installed on your system.
Database Configuration:
Default credentials for the MySQL database:
Username: root
Password: rootpassword
Database: tedtalks
The application runs with the prod profile.
