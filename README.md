# Forum System
 
## Project Description
Design and implement a Forum System, where users can create posts, add comments, and like posts (later comments too). The forum will be focused on automotive fanbase.
 
## Functional Requirements
 
### Entities
 
#### Users
- Each user must have a first and last name, email, username, and password.
    - First name and last name must be between 4 and 32 symbols.
    - Email must be a valid email and unique in the system.
 
#### Admins
- Each admin must have a first and last name, email, and may have a phone number.
    - First name and last name must be between 4 and 32 symbols.
    - Email must be a valid email and unique in the system.
 
#### Posts
- Each post must have a user who created it, a title, content, comments, and how many likes it has received.
    - The title must be between 16 and 64 symbols.
    - The content must be between 32 symbols and 8192 symbols.
    - The post must have a user who created it.
    - Other users must be able to post replies.
 
## Public Part
- Accessible without authentication.
- On the home page, anonymous users must be presented with the core features of the platform, the number of users, and the number of posts created so far.
- Anonymous users must be able to register and log in.
- Anonymous users should be able to see a list of the top 10 most commented posts and a list of the 10 most recently created posts.
 
## Private Part
- Accessible only if the user is authenticated.
- Users must be able to log in and log out.
- Users must be able to browse posts created by other users with an option to sort and filter them.
- Users must be able to view a single post, including its title, content, comments, likes, etc. All details and available actions (comment/like/edit) should be presented on the same page.
- Users must be able to update their profile information, but not their username once registered. Users can upload a profile photo.
- Users must be able to create a new post with at least a title and content.
- Each user must be able to edit only their own posts or comments.
- Each user must be able to view all their or any other user's posts and comments (with options to filter and sort them).
- Each user must be able to remove one or more of their own posts, either from the details view or the post list.
- Each user must be able to comment/reply to any other forum post.
 
## Administrative Part
- Accessible to users with administrative privileges.
- Admins must be able to search for a user by their username, email, or first name.
- Admins must be able to block or unblock individual users. Blocked users cannot create posts or comments.
- Admins must be able to delete any post.
- Admins must be able to view a list of all posts with options to filter and sort them.
 
## Optional Features
 
### Post Tags
- Users can add tags to posts for easier navigation and searching.
- Tags can be added when editing a post.
- If a tag doesn't exist, it must be created in the database; if it exists, it should be reused.
- All tags should be in lowercase.
 
## REST API
- CRUD operations for users and posts.
- Search users by username, email, or first name.
- Filter and sort posts by tags.
- Admin operations for making other users admins, deleting posts, and blocking/unblocking users. 
## Technical Requirements
- Follow OOP, KISS principles.
- Use tiered project structure.
- Achieve at least 80% unit test code coverage in the service layer.
- Implement proper exception handling.
- Normalize the database to avoid data duplication and empty data.
 
## Database
- Store data in a relational database.
- Provide scripts to create and populate the database.
 
## Git
- Provide a complete GitHub repository with the project source code and database scripts.
- Ensure commits reflect the project development process and contributions from all team members.
 
 
### Steps to Install
1. Clone the repository:
    ```bash
    git clone https://github.com/forum-RAI/forum-aplication.git
    cd forum-aplication
    ```
 
2. Set up the database:
    - Update `application.properties`
 
#### Example `application.properties`
```
spring.application.name=ForumProject
server.error.include-stacktrace=never
server.error.include-binding-errors=always
server.error.include-message=always
database.url=jdbc:mariadb://localhost:3306/forumsystem
database.username=root
database.password=stefaneqk
```
 
## Link to Swagger Documentation
(we still don't have)
 
## Database Relations
![databaseDiagram](https://github.com/user-attachments/assets/7612f1d0-05a4-44fe-9b2a-71016d53d8a0)

 
## Contributors
https://github.com/Stefcho1227 [Stefan Ivanov]
https://github.com/TodorKst [Todor Kostadinov]
