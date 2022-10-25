/**
 * Item (movie at this point)
 */
public class Item {
    private final String movieTitle;
    private final String movieId;
    private final int price;
    private int quantity;

    public Item(String id, String name) {
        this.price = 15;
        this.movieId = id;
        this.movieTitle = name;
        this.quantity = 0;
    }

    public String getName() { return this.movieTitle; }
    public String getId() { return this.movieId; }
    public int getQuantity() { return this.quantity; }
    public int getPrice() { return this.price; }

    public void setQuantity(int n) { if (--this.quantity <= 0) this.quantity++; }

    public void addQuantity() { this.quantity++; }

    public void subQuantity() { System.out.println(--quantity); }
}
