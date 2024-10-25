# Collaborative Code Editor

#### A web-based collaborative code editor that allows multiple users to edit code in real-time. The application supports syntax highlighting, version history, user authentication via GitHub OAuth, and file management features.
### Table of Contents

    Features
    Prerequisites
    Installation
    Configuration
    Running the Application
    Usage
    Technologies Used
    Contributing
    License
    Acknowledgements

### Features

    Real-time Collaboration: Multiple users can edit the same code file simultaneously with live updates.
    Syntax Highlighting: CodeMirror is used for code editing with syntax highlighting for various programming languages.
    Version History: Tracks changes and allows users to revert to previous versions.
    User Authentication: Secure login using GitHub OAuth 2.0.
    File Management: Create, edit, delete files and folders within the application.
    Code Execution: Run code snippets and view output directly in the browser.
    Responsive UI: User-friendly interface designed with Bootstrap for responsiveness.

### Prerequisites

    Java Development Kit (JDK) 21 or higher
    Maven build tool
    PostgreSQL database
    Git (for version control)
    An Internet Connection (for external libraries and OAuth)

### Installation
1. Clone the Repository

bash

git clone https://github.com/yourusername/collaborative-code-editor.git
cd collaborative-code-editor

2. Set Up the Database

   Create a PostgreSQL database named collaborative_code_editor.
   Configure the database username and password in the application's properties.

3. Install Dependencies

Ensure you have Maven installed, then run:

bash

mvn clean install

Configuration
1. Application Properties

Edit the src/main/resources/application.properties file to configure the database and OAuth settings:

properties

# Database configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/collaborative_code_editor
spring.datasource.username=your_db_username
spring.datasource.password=your_db_password

# OAuth2 configuration for GitHub
spring.security.oauth2.client.registration.github.client-id=your_github_client_id
spring.security.oauth2.client.registration.github.client-secret=your_github_client_secret

# WebSocket configuration
server.port=8080

2. GitHub OAuth Setup

   Go to GitHub Developer Settings and create a new OAuth application.
   Set the Authorization Callback URL to http://localhost:8080/login/oauth2/code/github.
   Obtain the Client ID and Client Secret and update them in the application.properties file.

Running the Application

bash

mvn spring-boot:run

The application will start on http://localhost:8080.
Usage
1. Access the Application

Navigate to http://localhost:8080 in your web browser.
2. Login with GitHub

   Click on the Login with GitHub button.
   Authorize the application to access your GitHub account.

3. Dashboard

After logging in, you will be redirected to the dashboard, where you can:

    View your profile information.
    Navigate to the code editor.

4. Collaborative Code Editing

   Use the File Manager on the left to create or select files.
   Open a file to start editing.
   Any changes you make will be reflected in real-time to other users editing the same file.
   Use the Save Code button to save your changes.
   Use the Run Code button to execute the code and see the output.

5. Version History

   Click on Load Version History to view previous versions.
   You can revert to a previous version by clicking the Revert button next to it.

6. File Management

   List Files: View all files and folders in the specified path.
   Create New File/Folder: Enter a name, specify if it's a directory, and click Create.
   Edit Files: Open files for editing by clicking the Edit button.
   Delete Files/Folders: Remove files or folders using the Delete button (confirmation required).

Technologies Used

    Backend:
        Java 21
        Spring Boot
        Spring Data JPA
        Spring Security with OAuth2
        WebSocket with STOMP
        PostgreSQL
    Frontend:
        HTML5, CSS3, JavaScript
        Bootstrap 5
        Bootstrap Icons
        CodeMirror (for code editing)
        SockJS and STOMP.js (for WebSocket communication)
    Version Control:
        Git
        JGit (for Git operations within the application)

Contributing

Contributions are welcome! Please follow these steps:

    Fork the Repository: Click the Fork button at the top right of the repository page.

    Clone Your Fork:

    bash

git clone https://github.com/yourusername/collaborative-code-editor.git

Create a Feature Branch:

bash

git checkout -b feature/YourFeature

Make Your Changes: Implement your feature or fix bugs.

Commit Your Changes:

bash

git commit -am 'Add new feature'

Push to Your Fork:

bash

    git push origin feature/YourFeature

    Create a Pull Request: Go to the original repository and click Pull Requests to submit your changes for review.

License

This project is licensed under the MIT License - see the LICENSE file for details.
Acknowledgements

    Thanks to all contributors and open-source projects that made this application possible.
    Special thanks to the creators of CodeMirror and Bootstrap.