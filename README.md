# DIGT_3107_team_3

## Library Management System - Phase 4

A Java console application for managing library management operations.

---

## Project Structure

```
DIGT_3107_team_3/
│
├── src/
│   ├── Main.java              # Entry point with menu system
│   ├── Database.java          # JDBC connection management
│   ├── QueryService.java      # Queries and reports functionality
│   ├── MemberService.java     # Member management operations
│   ├── BookService.java       # (For future phases)
│   └── LoanService.java       # (For future phases)
│
├── create_tables.sql          # Database schema
├── insert_data.sql            # Sample data
├── phase3_sample_data.sql     # Alternative sample data
├── phase3_complex_queries.sql # Complex SQL queries reference
├── .env                       # Database credentials (DO NOT COMMIT)
├── .env.example               # Template for environment variables
├── .gitignore                 # Git ignore rules
└── README.md                  # This file
```

---

## Team Members

- Sienna Markham (220274536)
- Eamon Ryan (219447192)
- Mahjabin Mollah (220416038)

---

## Features Implemented

### Queries and Reports
- List overdue books and fines ($10/day for late returns)
- Find most borrowed books
- List currently borrowed books
- Retrieve a member's borrowing history
- Show books due soon (within 3 days)
- Identify books never borrowed

### Member Management
- Add new members (name, phone, card number)
- View all members
- Search member by ID
- Update member information
- View member loan history
- Delete member
- Track all books borrowed and returned

---

## Prerequisites

1. **Java Development Kit (JDK)**
   - JDK 8 or higher
   - Check version: `java -version`

2. **MySQL Database**
   - MySQL 5.7 or higher
   - Running on `localhost:3306`

3. **MySQL JDBC Connector**
   - Download from: https://dev.mysql.com/downloads/connector/j/ (Recommended to select 'Platform Independent')
   - Or use Maven/Gradle to manage dependencies

---

## Database Setup

1. **Start MySQL Server**
   ```bash
   # Linux/Mac
   sudo systemctl start mysql

   # Windows - use MySQL Workbench or Services
   ```

2. **Create and Populate Database**
   ```bash
   mysql -u root -p < create_tables.sql
   mysql -u root -p < insert_data.sql
   # OR
   mysql -u root -p < phase3_sample_data.sql
   # OR
   Just populate through the MySQL Workbench UI
   ```

   **Note:** The `create_tables.sql` file automatically creates a database user `test_user` with password

3. **Configure Environment Variables**

   Copy the example environment file:
   ```bash
   cp .env.example .env
   ```

   Edit `.env` with your database credentials:
   ```properties
   DB_URL=jdbc:mysql://localhost:3306/Library_Management
   DB_USER=test_user
   DB_PASSWORD=Lb#9mK$2pQxR7wN4 (default - if you use a different one make sure to update create_tables.sql)
   ```

4. **Verify Database**
   ```sql
   mysql -u root -p
   USE Library_Management;
   SHOW TABLES;
   ```

---

## Application Setup

### Configuration

Database credentials are now managed via the `.env` file (see Database Setup step 3 above).

The application automatically loads configuration from `.env` on startup. No code changes needed!

### Compilation and Execution

**Important:** Make sure the `.env` file exists in the project root directory before running the application.

#### Option 1: Using Command Line (with JDBC JAR file)

1. **Download MySQL Connector/J**
   - Place `mysql-connector-java-8.x.x.jar` in the project root directory

2. **Compile**
   ```bash
   # Linux/Mac
   javac -cp .:mysql-connector-java-8.x.x.jar src/*.java

   # Windows
   javac -cp .;mysql-connector-java-8.x.x.jar src/*.java
   ```

3. **Run**
   ```bash
   # Linux/Mac
   java -cp .:mysql-connector-java-8.x.x.jar:src Main

   # Windows
   java -cp .;mysql-connector-java-8.x.x.jar;src Main
   ```

#### Option 2: Using IDE (Eclipse/IntelliJ)

1. **Import Project**
   - File → Open → Select project folder

2. **Add MySQL Connector to Build Path**
   - File → Project Structure → Libraries → + icon
   - Add `mysql-connector-java-8.x.x.jar`

3. **Run Main.java**
   - Right-click `Main.java` → Run As → Java Application

```
---

## Usage

### Main Menu
```
======================================
           MAIN MENU
======================================
1. Queries and Reports
2. Member Management
0. Exit
======================================
```

### Queries and Reports Menu
- Option 1: View overdue books with calculated fines
- Option 2: See top 10 most borrowed books
- Option 3: List all currently borrowed books
- Option 4: View specific member's complete borrowing history
- Option 5: See books due within next 3 days
- Option 6: Find books that have never been borrowed

### Member Management Menu
- Option 1: Register new library members
- Option 2: Display all members
- Option 3: Look up member details by ID
- Option 4: Update member contact info
- Option 5: View member's borrowing history
- Option 6: Remove member from system

---

## Troubleshooting

### "MySQL JDBC Driver not found"
- Ensure `mysql-connector-java.jar` is in classpath
- Verify JAR file path in compile/run commands

### "Access denied for user"
- Verify credentials in `.env` file are correct
- Check MySQL user exists: `SELECT User FROM mysql.user WHERE User='test_user';`
- Ensure user has proper permissions (run `create_tables.sql` as root to create user)

### "Unknown database 'Library_Management'"
- Run `create_tables.sql` to create database
- Verify database name in MySQL: `SHOW DATABASES;`

### "Communications link failure"
- Ensure MySQL server is running
- Check port 3306 is not blocked
- Verify connection URL in `.env` file

### "Could not load .env file"
- Ensure `.env` file exists in project root directory (same level as src/)
- Copy `.env.example` to `.env` if it doesn't exist
- Verify file is named exactly `.env` (not `.env.txt`)

---

## License

Educational project for DIGT 3107
