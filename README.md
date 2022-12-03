

How to deplpy this application with Tomcat:
Step 1: Git clone
Run the following command in terminal:
git clone https://github.com/uci-jherold2-teaching/cs122b-fall-team-13.git

Step 2: IntelliJ Configuration
In IDE(we use IntelliJ), open the cloned project folder and choose Maven.

Step 3: Setup Tomcat Configuration
In IntelliJ, under Edit Configurations, add Tomcat 9.0.67 and add build artifact.
Then click Run, the Tomcat will be successfully connected.

Project 1:
Demo Video URL:
https://youtu.be/n8JRboehhE4

Members' Contribution:
Lai Jiang: Write create table sql file, implement movie-list, do all the aws stuff, and record demo video.
Zimu Qian: Implement single-movie and single-star pages; Beautify all pages.

Project 2:

Demo Video URL:
https://youtu.be/-GxIBRpbkKI


Members' Contribution:
Lai Jiang: MovieList, Single movie, Single star, Main page for browsing and searching.
Zimu Qian: Implement login, shopping cart, and check out functions; beautify pages.

Substring matching design: we use LIKE to do the substring design in searching, which is "%" in the front or end of the substring in sql querys.


Project 3:

Demo Video URL:
https://youtu.be/ErwIKxqU7yY

Members' Contribution:
Lai Jiang: PreparedStatement, HTTPS, XML parsing, query optimization.
Zimu Qian: Employee Dashboard, Recaptcha, PasswordEncrypyion.

The queries with Prepared Statements: MovieListServlet.java, SingleStarServlet.java, SingleMovieServlet.java, CreditCardServlet.java, LoginServlet.java

Parsing time optimization Strategy:
1. Using fulltext index.
2. Using hashset to avoid duplicates when parsing.


Project 4:

Demo Video URL:
https://youtu.be/fqA29_MwoHg

Members' Contribution:
Lai Jiang: Autocomplete, fuzzy search
Zimu Qian: Android

Fuzzy Search Idea:
1. using substring match
2. using Levenshtein Distance Approach, which is: if lev(kitten, sitting) = 3, then we have kitten -> sitten -> sittin -> sitting


Project 5:
- # General
    - #### Team#: Team 13
    
    - #### Names: Lai Jiang, Zimu Qian
    
    - #### Project 5 Video Demo Link:

    - #### Instruction of deployment:

    - #### Collaborations and Work Distribution:
        - Lai Jiang:
        - Zimu Qian: JDBC Connection Pooling and Performance Measurement


- # Connection Pooling
    - #### Include the filename/path of all code/configuration files in GitHub of using JDBC Connection Pooling.
        - All servlet files were written with JDBC Connection Pooling enabled.
        - Config files changed: web/META-INF/context.xml, web/WEB-INF/web.xml
    
    - #### Explain how Connection Pooling is utilized in the Fabflix code.
        - Created a DataSource where each getConnection function can get a connection from it
    
    - #### Explain how Connection Pooling works with two backend SQL.
    

- # Master/Slave
    - #### Include the filename/path of all code/configuration files in GitHub of routing queries to Master/Slave SQL.

    - #### How read/write requests were routed to Master/Slave SQL?
    

- # JMeter TS/TJ Time Logs
    - #### Instructions of how to use the `log_processing.*` script to process the JMeter logs.


- # JMeter TS/TJ Time Measurement Report

| **Single-instance Version Test Plan**          | **Graph Results Screenshot** | **Average Query Time(ms)** | **Average Search Servlet Time(ms)** | **Average JDBC Time(ms)** | **Analysis** |
|------------------------------------------------|------------------------------|----------------------------|-------------------------------------|---------------------------|--------------|
| Case 1: HTTP/1 thread                          | ![](path to image in img/)   | ??                         | ??                                  | ??                        | ??           |
| Case 2: HTTP/10 threads                        | ![](path to image in img/)   | ??                         | ??                                  | ??                        | ??           |
| Case 3: HTTPS/10 threads                       | ![](path to image in img/)   | ??                         | ??                                  | ??                        | ??           |
| Case 4: HTTP/10 threads/No connection pooling  | ![](path to image in img/)   | ??                         | ??                                  | ??                        | ??           |

| **Scaled Version Test Plan**                   | **Graph Results Screenshot** | **Average Query Time(ms)** | **Average Search Servlet Time(ms)** | **Average JDBC Time(ms)** | **Analysis** |
|------------------------------------------------|------------------------------|----------------------------|-------------------------------------|---------------------------|--------------|
| Case 1: HTTP/1 thread                          | ![](path to image in img/)   | ??                         | ??                                  | ??                        | ??           |
| Case 2: HTTP/10 threads                        | ![](path to image in img/)   | ??                         | ??                                  | ??                        | ??           |
| Case 3: HTTP/10 threads/No connection pooling  | ![](path to image in img/)   | ??                         | ??                                  | ??                        | ??           |
