# Task Management System

A comprehensive microservices-based web application designed to help teams manage their projects and tasks efficiently. Built with Spring Boot and JHipster, this system provides a scalable solution for project management, task tracking, and team collaboration.

## 🚀 Features

- **Multi-Project Management**: Create and manage multiple projects simultaneously
- **Task Assignment & Tracking**: Create, assign, and monitor tasks with priorities and deadlines
- **Role-Based Access Control**: Hierarchical user roles with granular permissions
- **Real-time Collaboration**: Task comments, file attachments, and notifications
- **Comprehensive Reporting**: Generate detailed reports on project and individual performance
- **Secure Authentication**: JWT-based authentication and authorization
- **Microservices Architecture**: Scalable and maintainable service-oriented design

### Services Overview

1. **Service Discovery (Eureka Server)**: Facilitates the discovery of services within the microservices architecture.
2. **API Gateway**: Acts as a single entry point for all client requests for route management and load balancing
3. **Authentication Service**: Manages user registration, login, and generates JSON Web Tokens (JWT) for secure communication.
4. **User Service**: Manages all non-authentication-related user data (User profiles, roles, and team management)
5. **Project Service**: Manages projects and tracks their overall progress.
6. **Task Service**: Manages tasks, including creation, assignment, tracking, and collaboration features like comments and attachments.
7. **Notification Service**: A non-database service that sends real-time notifications and email alerts by listening to events from other services.
8. **Reporting Service**: Aggregates data from multiple services to generate reports on task completion, project progress, and individual performance.

## 👥 Role-Based Access Control (RBAC)

The system implements a hierarchical role-based access control system with three primary roles:

### 🔴 Admin
- **Scope**: System-wide administrative privileges
- **Permissions**:
  - Full user management (create, update, delete users)
  - Role assignment and management
  - Team creation and management across all teams
  - Project creation and management across all projects
  - Task creation, assignment, and management across all projects
  - Access to all reports and analytics
  - System configuration and settings

### 🟡 Team Lead
- **Scope**: Manager-level privileges with elevated access across teams
- **Key Feature**: Team Leads have manager-level privileges and are **not restricted to specific teams**
- **Permissions**:
  - Team management across **any team** in the system
  - Project creation and management across **all projects**
  - Task creation, assignment, and management across **all projects**
  - User assignment to any team
  - Access to reports for all teams and projects
  - Can view and manage tasks across all teams

> **Important Note**: Team Leads are not just leads for a specific team - they have elevated privileges across the entire system, functioning as middle management with broad oversight capabilities.

### 🟢 Member (Team Member)
- **Scope**: Limited to assigned teams and projects
- **Permissions**:
  - View team information for assigned teams only
  - View and update tasks assigned to them
  - View projects associated with their teams
  - Add comments and attachments to tasks they're involved in
  - Update their own profile information
  - View reports related to their own performance

### Access Control Matrix

| Feature | Admin | Team Lead | Member |
|---------|-------|-----------|--------|
| User Management | ✅ Full | ❌ | ❌ |
| Role Management | ✅ Full | ❌ | ❌ |
| Team Management | ✅ All Teams | ✅ All Teams | ❌ |
| Project Management | ✅ All Projects | ✅ All Projects | 👁️ Assigned Only |
| Task Management | ✅ All Tasks | ✅ All Tasks | 👁️ Assigned Only |
| Reporting | ✅ All Reports | ✅ All Reports | ❌ |

## 🛠️ Technology Stack

- **Backend**: Java Spring Boot
- **Database**: MySQL/PostgreSQL
- **Authentication**: JWT (JSON Web Tokens)
- **Service Discovery**: Netflix Eureka
- **API Gateway**: Spring Cloud Gateway
- **Build Tool**: Maven
- **Framework**: JHipster

## 🚀 Getting Started

### Prerequisites
* Java 11 or higher
* Maven 3.6+
* PostgreSQL
* Node.js (for frontend dependencies)

### Installation
1.  **Clone the repository:**
    ```bash
    git clone https://github.com/odiasesamuel/Task-Management-System
    cd Task-Management-System
    ```
2.  **Start API Gateway:**
    ```bash
    cd gateway
    mvn spring-boot:run
    ```

3.  **Start Individual Services:**
    ```bash
    # Authentication Service
    cd auth-service
    mvn spring-boot:run
    
    # User Service
    cd user-service
    mvn spring-boot:run
    
    # Project Service
    cd project-service
    mvn spring-boot:run
    
    # Task Service
    cd task-service
    mvn spring-boot:run
    
    # Notification Service
    cd notification-service
    mvn spring-boot:run
    
    # Reporting Service
    cd reporting-service
    mvn spring-boot:run
    ```

### Access the Application
* **API Gateway:** `http://localhost:8765`
* **Service Discovery Dashboard:** `http://localhost:8761`

## 📊 Database Design, API Endpoints

### Authentication Service
#### Database
```sql
CREATE TABLE auth_users (
    user_id BIGINT PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL
);
```
#### API Endpoints
**Base URL** `http://localhost:8765/services/authservice`

- **POST** `/api/auth/register`  
  Register a new user.

- **POST** `/api/auth/login`  
  Log in a user and generate a JWT.

- **POST** `/api/auth/validate-token`  
  Validate a JWT.

### User Service
#### Database
```sql
CREATE TABLE users (
    user_id BIGINT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    phone_number VARCHAR(20),
    profile_picture_url VARCHAR(255)
);

CREATE TABLE roles (
    role_id BIGINT PRIMARY KEY,
    role_name VARCHAR(50) UNIQUE NOT NULL
);

CREATE TABLE user_roles (
    user_id BIGINT,
    role_id BIGINT,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles(role_id) ON DELETE CASCADE
);

CREATE TABLE teams (
    team_id BIGINT PRIMARY KEY,
    team_name VARCHAR(255) NOT NULL,
    admin_id BIGINT,
    FOREIGN KEY (admin_id) REFERENCES users(user_id) ON DELETE SET NULL
);

CREATE TABLE user_teams (
    user_id BIGINT,
    team_id BIGINT,
    PRIMARY KEY (user_id, team_id),
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (team_id) REFERENCES teams(team_id) ON DELETE CASCADE
);
```
#### API Endpoints
**Base URL** `http://localhost:8765/services/userservice`

##### Users
- **POST** `/api/users/internal`  
  Creates a new user entry (used internally after registration).

- **PATCH** `/api/users/email/{email}`  
  Updates a user's profile details *(Admin, User — only their own profile)*.

- **GET** `/api/users`  
  Gets all user profile information *(Admin)*.

- **GET** `/api/users/email/{email}`  
  Gets a user's profile information *(Admin, User — only their own profile)*.

- **POST** `/api/users/upload-profile-picture`  
  Upload profile picture


##### Teams
- **POST** `/api/teams`  
  Creates a new team to assign users *(Admin, Team Lead)*.

- **PUT** `/api/teams/{teamId}`  
  Updates a team to assign/remove users from existing team *(Admin, Team Lead)*.

- **GET** `/api/teams`  
  Gets a list of all teams and members *(Admin, Team Lead)*.

- **GET** `/api/teams/{teamId}`  
  Gets a list of members for a given team *(Admin, Team Lead, Team Member)*.

- **DELETE** `/api/teams/{teamId}`  
  Deletes a team *(Admin, Team Lead)*.


##### Roles
- **POST** `/api/roles`  
  Creates a role *(Admin)*.

- **PUT** `/api/roles/{roleId}`  
  Updates a role to assign/remove user roles *(Admin)*.

- **GET** `/api/roles`  
  Gets all roles *(Admin)*.

- **GET** `/api/roles/{roleId}`  
  Gets a specific role by ID *(Admin)*.

- **DELETE** `/api/roles/{roleId}`  
  Deletes a specific role by ID *(Admin)*.

### Project  Service
#### Database
```sql
CREATE TABLE projects (
    project_id BIGINT PRIMARY KEY,
    project_name VARCHAR(255) NOT NULL,
    description TEXT,
    team_id BIGINT,
    created_by_user_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
);
```
#### API Endpoints
**Base URL** `http://localhost:8765/services/project`

- **POST** `/api/projects`  
  Creates a new project *(Admin, Team Lead)*.

- **PUT** `/api/projects/{projectId}`  
  Updates project information *(Admin, Team Lead)*.

- **GET** `/api/projects`  
  Retrieves all projects and their details *(Admin, Team Lead)*.

- **GET** `/api/projects/{projectId}`  
  Retrieves a specific project's details *(Admin, Team Lead, Team Member)*.

- **GET** `/api/teams/{teamId}/projects`  
  Retrieves all projects associated with a team *(Admin, Team Lead, Team Member)*.

- **GET** `/api/projects/{projectId}/progress`  
  Calculates and returns the overall progress of a project *(Admin, Team Lead, Team Member)*.

- **DELETE** `/api/projects/{projectId}`  
  Deletes a specific project *(Admin, Team Lead)*.

### Task Service
#### Database
```sql
CREATE TABLE tasks (
    task_id BIGINT PRIMARY KEY,
    project_id BIGINT NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    assigned_to_user_id BIGINT,
    due_date TIMESTAMP,
    priority ENUM('Low', 'Medium', 'High') NOT NULL,
    status ENUM('To Do', 'In Progress', 'Completed') NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
);

CREATE TABLE task_comments (
    comment_id BIGINT PRIMARY KEY,
    task_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    comment TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (task_id) REFERENCES tasks(task_id) ON DELETE CASCADE,
);

CREATE TABLE task_attachments (
    attachment_id BIGINT PRIMARY KEY,
    task_id BIGINT NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    file_url VARCHAR(255) NOT NULL,
    uploaded_by_user_id BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (task_id) REFERENCES tasks(task_id) ON DELETE CASCADE,
);
```
#### API Endpoints
**Base URL** `http://localhost:8765/services/taskservice`

##### Tasks
- **POST** `/api/tasks`  
  Creates a new task *(Admin, Team Lead)*.

- **PUT** `/api/tasks/{taskId}`  
  Updates an existing task *(Admin, Team Lead)*.

- **GET** `/api/tasks/{taskId}`  
  Retrieves a specific task *(Admin, Team Lead, Team Member)*.

- **GET** `/api/projects/{projectId}/tasks`  
  Retrieves all tasks for a project *(Admin, Team Lead, Team Member)*.

- **GET** `/api/projects/user`  
  Retrieves all tasks for a specific user *(User — only their own task)*.

##### Task Comments
- **POST** `/api/task-comments/{taskId}`  
  Adds a comment to a task *(Admin, Team Lead, Team Member)*.

- **PUT** `/api/task-comments/{taskId}`  
  Updates a comment *(Admin, Team Lead, Team Member)*.

- **GET** `/api/task-comments/{taskId}`  
  Retrieves all comments for a task *(Admin, Team Lead, Team Member)*.

- **DELETE** `/api/task-comments/{taskId}`  
  Deletes a comment *(Admin, Team Lead)*.

##### Task Attachments
- **POST** `/api/task-comments/{taskId}/attachments`  
  Uploads and attaches a file to a task *(Admin, Team Lead, Team Member)*.

### Reporting Service
#### API Endpoints
**Base URL** `http://localhost:8765/reports//taskservice`

- **GET** `/api/reports/projects`  
  Retrieves all project report *(Admin, Team Lead)*.

- **GET** `/api/reports/projects/{projectId}`  
  Retrieves a specific project report *(Admin, Team Lead)*.

### Notifications Service
#### Task Assignment Notification
The Task Management service sends an event to the Notifications service when a task is created or assigned. The event payload would contain the task_id, assigned_to_user_id, and due_date. The Notifications service would then fetch the user's details from the User Management service to send an email or an in-app alert.

#### Collaboration Notifications
The Task Management service sends an event to the Notifications service whenever a comment is added to a task. The event payload would include the task_id, comment and the user_id of the commenter. The Notifications service would then notify all other users involved in that task.

## 📝 Usage

### Creating Your First Project
1. **Register/Login** as an Admin or Team Lead  
2. **Create a Team** with team members  
3. **Create a Project** and assign it to the team  
4. **Add Tasks** to the project with priorities and deadlines  
5. **Assign Tasks** to team members  
6. **Generate Reports** to track performance  

### Team Member Workflow

1. **Login** to your account  
2. **View Assigned Tasks** on your profile  
3. **Update Task Status** as you work  
4. **Add Comments and Upload Files** for collaboration  
5. **Receive Notifications** for new assignments and deadlines  

## 🤝 Contributing
1.  **Fork the repository**
2.  **Create your feature branch** (`git checkout -b feature/AmazingFeature`)
3.  **Commit your changes** (`git commit -m 'Add some AmazingFeature'`)
4.  **Push to the branch** (`git push origin feature/AmazingFeature`)
5.  **Open a Pull Request**