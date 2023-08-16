

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
    
    - #### Project 5 Video Demo Link: https://youtu.be/YtLTvV12jl4

    - #### Instruction of deployment:
        Step 1: Git clone
        Run the following command in terminal:
        git clone https://github.com/uci-jherold2-teaching/cs122b-fall-team-13.git

        Step 2: IntelliJ Configuration
        In IDE(we use IntelliJ), open the cloned project folder and choose Maven.

        Step 3: Setup Tomcat Configuration
        In IntelliJ, under Edit Configurations, add Tomcat 9.0.67 and add build artifact.
        Then click Run, the Tomcat will be successfully connected.

    - #### Collaborations and Work Distribution:
        - Lai Jiang: Master-Slave Replication, Load Balancer, and Sessions Stickyness
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
        - Original: http://35.90.237.219:8080/cs122b-fall22-project1/
        - Load Balancer: http://35.90.237.219/cs122b-fall22-project1/
        - Master: http://18.236.230.142:8080/cs122b-fall22-project1/
        - Slave: http://35.89.38.109:8080/cs122b-fall22-project1/

    - #### How read/write requests were routed to Master/Slave SQL?
        - Can read from both master and slave, but only write master.
    

- # JMeter TS/TJ Time Logs
    - #### Instructions of how to use the `log_processing.*` script to process the JMeter logs.
        - Put log_processing.py into the same folder as the log.txt file
        - Input "python3 log_processing.py" in the terminal and hit enter


- # JMeter TS/TJ Time Measurement Report

| **Single-instance Version Test Plan**          | **Graph Results Screenshot** | **Average Query Time(ms)** | **Average Search Servlet Time(ms)** | **Average JDBC Time(ms)** | **Analysis** |
|------------------------------------------------|------------------------------|----------------------------|-------------------------------------|---------------------------|--------------|
| Case 1: HTTP/1 thread                          | ![path to image in img/](https://drive.google.com/file/d/1SKEfaoHmc5QUY0psM-L3fncMR5NzLvyS/view?usp=share_link)   | 288                        | 190.53                              | 190.08                    | Normal situation           |
| Case 2: HTTP/10 threads                        | ![path to image in img/](https://drive.google.com/file/d/1RKIlbiH2wNcJJ8am8av84VO6NuSsNDZm/view?usp=sharing)   | 1046                       | 998.19                              | 998.06                    | More threads than case above           |
| Case 3: HTTPS/10 threads                       | ![path to image in img/](https://drive.google.com/file/d/1prvmSKJQEKNl6QM7GTmltQnBVUmgBAjG/view?usp=sharing)   | 1058                       | 1018.80                             | 1018.49                   | Https encrytion took more time           |
| Case 4: HTTP/10 threads/No connection pooling  | ![path to image in img/](https://drive.google.com/file/d/1ejJ_WfHEHm_ap4VgjyA7etm8Wde9FEQO/view?usp=sharing)   | 1048                       | 1028.10                             | 1027.89                   | More jdbc and servlet time because of no connection pooling           |

| **Scaled Version Test Plan**                   | **Graph Results Screenshot** | **Average Query Time(ms)** | **Average Search Servlet Time(ms)** | **Average JDBC Time(ms)** | **Analysis** |
|------------------------------------------------|------------------------------|----------------------------|-------------------------------------|---------------------------|--------------|
| Case 1: HTTP/1 thread                          | ![path to image in img/](https://drive.google.com/file/d/1Im-7uFlpP0Sw5BIVI0WA8oCPkn7uj3H2/view?usp=sharing)   | 210                        | 170.18                              | 169.93                    | Load Balancer made it faster           |
| Case 2: HTTP/10 threads                        | ![path to image in img/](https://drive.google.com/file/d/1CUYZ_ZdKqaYXyQCIvIy5QBY47ij4RHgU/view?usp=sharing)   | 518                        | 480.23                              | 480.04                    | More threads than case above           |
| Case 3: HTTP/10 threads/No connection pooling  | ![path to image in img/](https://drive.google.com/file/d/1G1WjXo-HG-m09hwEGSUdZcg-uaZmdpHN/view?usp=sharing)   | 532                        | 457.89                              | 456.83                    | Maybe some flaw data           |
