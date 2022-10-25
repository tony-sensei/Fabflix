/**
 * User (customer in db)
 */
public class User {
    private final String username;
    private final String customerId;
    public User(String username, String customerId) {
        this.username = username;
        this.customerId = customerId;
    }

    public String getUsername() { return this.username; }

    public String getId() { return this.customerId; }
}
