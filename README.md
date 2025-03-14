# Bug Tracking System - Java Swing & Spring Boot

## Project Overview
The Bug Tracking System is a Java-based application designed to streamline bug reporting, assignment, and resolution within software teams. The system features role-based dashboards for Admins, Project Managers, Developers, and Testers, ensuring efficient workflow, bug tracking, and real-time collaboration.

---

## Key Features & Functionality

- **Role-Based Access Control**: Secure login with separate dashboards for Admins, Project Managers, Developers, and Testers.
- **Bug Management**: Report, assign, update, and track bug statuses efficiently across different roles.
- **Real-Time Chat**: WebSocket-based communication system for seamless collaboration between team members.
- **Performance Dashboard**: Visual analytics using JFreeChart for tracking team performance and bug resolution metrics.
- **REST API Integration**: Backend API replaced direct database queries for improved scalability and efficiency.
- **Secure Authentication**: Implemented secure user authentication and password reset modules.

---

## Technology Stack

- **Frontend**: Java Swing (GUI)
- **Backend**: Spring Boot, REST API
- **Database**: MySQL
- **Libraries & Tools**:
  - Unirest (for HTTP Requests)
  - JFreeChart (for performance visualization)
  - WebSockets (for chat system)
- **Build & Dependency Management**: Maven

---

## User Roles & Functionalities

### **LOGIN PAGE**
src/main/resources/Screenshot 2025-03-14 161608.png

### 👨‍💻 **Developer**
- **View Bugs**: See all assigned bugs.
- **Finish Bug**: Mark bugs as resolved once fixed.
- **Join Chat Room**: Communicate with testers and other developers.
- **Search**: Find relevant bugs based on various criteria.
- **Log Out**: Exit the system.

### 🕵️ **Tester**
- **Add Bug**: Report new bugs to the system.
- **Assign Bug**: Assign reported bugs to developers for resolution.
- **View Bugs**: See all bugs reported, including their statuses.
- **View Developers**: Check which developer has been assigned to each bug.
- **Host Chat Room**: Start a chat for discussion with developers and project managers.
- **Log Out**: Exit the system.

### 🤵 **Project Manager**
- **Monitor Bugs**: Track the status of all bugs.
- **Monitor Testers**: Review the activity and performance of testers.
- **Monitor Developers**: Track developer performance, including the number of resolved bugs.
- **Check Performance**: Analyze bug resolution efficiency and overall project progress.
- **Log Out**: Exit the system.

### 🦸 **Admin**
- **View Users**: Manage user accounts (view details of all users).
- **Add User**: Create new user accounts (Admin, Developer, Tester, Project Manager).
- **Update User**: Modify user details (e.g., role, personal information).
- **Delete User**: Remove users from the system.
- **Sign Up**: Register new Admins for system access.
- **Log Out**: Exit the system.

---

## Workflow Example

1. **Tester reports a bug**: The Tester adds a new bug to the system, detailing the issue.
2. **Project Manager monitors the bug status**: The Project Manager oversees the progress of the reported bugs.
3. **Tester assigns the bug to a Developer**: The Tester assigns the bug to an available Developer.
4. **Developer resolves the bug**: The Developer fixes the bug and updates its status to "resolved."
5. **Tester verifies and closes the bug**: The Tester verifies the resolution and closes the bug.
6. **Admin manages users if needed**: Admins can add, update, or delete user accounts.

---

## Achievements & Impact

- **Reduced Manual Effort**: Automation reduced bug tracking efforts by 70%.
- **Enhanced Team Collaboration**: Real-time updates and chat integration improved team communication.
- **Scalable & Secure**: Migration to RESTful APIs ensured improved security and scalability.

---

## How to Run the Project

### Prerequisites

- Java 11 or higher
- MySQL (with a configured database)
- Maven
- WebSocket server for real-time chat functionality

### Steps

1. Clone this repository to your local machine:
    ```bash
    git clone https://github.com/your-username/BugTrackingSystem.git
    ```
2. Navigate to the project directory:
    ```bash
    cd BugTrackingSystem
    ```
3. Build the project using Maven:
    ```bash
    mvn clean install
    ```
4. Configure the MySQL database and update the `application.properties` file in the `src/main/resources` directory.
5. Run the Spring Boot application:
    ```bash
    mvn spring-boot:run
    ```

6. Launch the Java Swing GUI and access the system.

---

## Contributing

1. Fork this repository.
2. Create a new branch (`git checkout -b feature-name`).
3. Make your changes and commit them (`git commit -am 'Add new feature'`).
4. Push to the branch (`git push origin feature-name`).
5. Create a new Pull Request.

---

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

## Acknowledgments

- Spring Boot: For building the backend with REST API.
- Java Swing: For creating the frontend GUI.
- MySQL: For the relational database system.
- WebSockets: For real-time communication.
- JFreeChart: For performance analytics and data visualization.

