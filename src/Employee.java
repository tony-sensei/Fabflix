/**
 * Employee (employee in db)
 */
public class Employee {
    private final String fullname;
    private final String email;
    public Employee(String email, String fullname) {
        this.fullname = fullname;
        this.email = email;
    }

    public String getFullName() { return this.fullname; }

    public String getEmail() { return this.email; }
}
