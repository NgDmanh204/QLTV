package model;

public class Book {
    private int id;
    private String title;
    private String category;
    private String author;
    private String publisher;
    private int quantity;
    private double price;

    public Book() {}

    public Book(int id, String title, String category, String author, String publisher, int quantity, double price) {
        this.id = id;
        this.title = title;
        this.category = category;
        this.author = author;
        this.publisher = publisher;
        this.quantity = quantity;
        this.price = price;
    }

    public Book(String title, String category, String author, String publisher, int quantity, double price) {
        this.title = title;
        this.category = category;
        this.author = author;
        this.publisher = publisher;
        this.quantity = quantity;
        this.price = price;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    public String getPublisher() { return publisher; }
    public void setPublisher(String publisher) { this.publisher = publisher; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
}