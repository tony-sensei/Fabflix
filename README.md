

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
