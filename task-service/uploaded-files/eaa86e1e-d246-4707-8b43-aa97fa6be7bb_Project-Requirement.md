# Project Requirement: Collaborative Task Management System

## Services Required

1.           Service Discovery (Eureka Server)
2.  API Gateway
3.  Authentication
4.  User
5.  Project
6.  Task
7.  Notification
8.  Reporting

### Authentication Service

This service is solely responsible for user authentication and authorization. It will handle user registration and login and generate JSON Web Tokens (JWT) for secure communication.

#### Database Design

```bash
CREATE TABLE users (
    user_id UUID PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL
);
```

#### API Endpoints

POST /api/auth/register
POST /api/auth/login
POST /api/auth/validate-token

### User Service

The User Service manages all non-authentication-related user data, including profiles, roles, and team management.

#### Database Design

```bash
CREATE TABLE users (
    user_id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    phone_number VARCHAR(20),
    profile_picture_url VARCHAR(255)
);

CREATE TABLE roles (
    role_id UUID PRIMARY KEY,
    role_name VARCHAR(50) UNIQUE NOT NULL
);

CREATE TABLE user_roles (
    user_id UUID,
    role_id UUID,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles(role_id) ON DELETE CASCADE
);

CREATE TABLE teams (
    team_id UUID PRIMARY KEY,
    team_name VARCHAR(255) NOT NULL,
    admin_id UUID,
    FOREIGN KEY (admin_id) REFERENCES users(user_id) ON DELETE SET NULL
);

CREATE TABLE user_teams (
    user_id UUID,
    team_id UUID,
    PRIMARY KEY (user_id, team_id),
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (team_id) REFERENCES teams(team_id) ON DELETE CASCADE
);
```

#### API Endpoints

POST /api/users: Creates a new user entry (used internally after registration in the Authentication Service).
GET /api/users/{userId}: Retrieves a user's profile information.
PUT /api/users/{userId}/profile: Updates a user's profile details.
POST /api/teams: Allows an Admin user to create a new team.
POST /api/teams/{teamId}/members: Assigns a user to a specific team.
GET /api/teams/{teamId}/members: Retrieves a list of members for a given team.
PUT /api/users/{userId}/roles: Modifies a user's role (Admin only).

### Project Service

This service is responsible for managing projects and tracking their overall progress.

#### Database Design

```bash
CREATE TABLE projects (
    project_id UUID PRIMARY KEY,
    project_name VARCHAR(255) NOT NULL,
    description TEXT,
    team_id UUID,
    created_by_user_id UUID NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
);
```

#### API Endpoints

POST /api/projects: Creates a new project.
GET /api/projects/{projectId}: Retrieves a specific project and its details.
PUT /api/projects/{projectId}: Updates project information.
GET /api/teams/{teamId}/projects: Retrieves all projects associated with a team.
GET /api/projects/{projectId}/progress: Calculates and returns the overall progress of a project based on task completion. This would involve an interservice call to the Task Management service's GET /api/projects/{projectId}/tasks endpoint to get all tasks, then calculating the percentage of completed tasks.

### Task Service

This service focuses on creating, assigning, tracking, and collaborating on tasks.

#### Database Design

```bash
CREATE TABLE tasks (
    task_id UUID PRIMARY KEY,
    project_id UUID NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    assigned_to_user_id UUID,
    due_date TIMESTAMP,
    priority ENUM('Low', 'Medium', 'High') NOT NULL,
    status ENUM('To Do', 'In Progress', 'Completed') NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
);

CREATE TABLE task_comments (
    comment_id UUID PRIMARY KEY,
    task_id UUID NOT NULL,
    user_id UUID NOT NULL,
    comment TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (task_id) REFERENCES tasks(task_id) ON DELETE CASCADE,
);

CREATE TABLE task_attachments (
    attachment_id UUID PRIMARY KEY,
    task_id UUID NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    file_url VARCHAR(255) NOT NULL,
    uploaded_by_user_id UUID,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (task_id) REFERENCES tasks(task_id) ON DELETE CASCADE,
);
```

#### API Endpoints

POST /api/tasks: Creates a new task within a project.
GET /api/tasks/{taskId}: Retrieves a specific task.
PUT /api/tasks/{taskId}: Updates an existing task (e.g., status, priority).
GET /api/projects/{projectId}/tasks: Retrieves all tasks for a given project.
POST /api/tasks/{taskId}/comments: Adds a comment to a task.
GET /api/tasks/{taskId}/comments: Retrieves all comments for a task.
POST /api/tasks/{taskId}/attachments: Uploads and attaches a file to a task.

### Notifications Service

The Notifications service is a non-database service that sends real-time notifications and email alerts. It listens for events from other services.

#### Inter-service Communication (Notifications)

This service would primarily communicate by subscribing to events from other services.

**Task Assignment Notification:** The Task Management service sends an event to the Notifications service when a task is created or assigned. The event payload would contain the task_id, assigned_to_user_id, and due_date. The Notifications service would then fetch the user's details from the User Management service to send an email or an in-app alert.

**Collaboration Notifications:** The Task Management service sends an event to the Notifications service whenever a comment is added to a task. The event payload would include the task_id and the user_id of the commenter. The Notifications service would then notify all other users involved in that task.

### Reporting Service

This service generates reports on task completion, project progress, and individual performance.

#### Inter-service Communication (Reporting)

The Reporting service would be an aggregator, pulling data from multiple other services.

**Task and Project Reports:** To generate a comprehensive report, the Reporting service would make multiple inter-service calls:

1. Call the Project Management service to get a list of all projects.

2. Call the Task Management service to get the tasks for each project.

3. Call the User Management service to get user details for the report.

**Performance Tracking:** For individual performance reports, the Reporting service would:

1. Call the User Management service to get user information.

2. Call the Task Management service to find all tasks completed by a specific user.
