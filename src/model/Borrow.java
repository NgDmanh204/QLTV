package model;

public class Borrow {
    private int id;
    private String borrowerName;
    private String phoneNumber;
    private String borrowDate;
    private String returnDate;
    private String bookCode;
    private int readerId;
    private double fine;

    public Borrow() {}

    public Borrow(int id, String borrowerName, String phoneNumber, String borrowDate, String returnDate, String bookCode) {
        this.id = id;
        this.borrowerName = borrowerName;
        this.phoneNumber = phoneNumber;
        this.borrowDate = borrowDate;
        this.returnDate = returnDate;
        this.bookCode = bookCode;
    }

    public Borrow(String borrowerName, String phoneNumber, String borrowDate, String returnDate, String bookCode) {
        this.borrowerName = borrowerName;
        this.phoneNumber = phoneNumber;
        this.borrowDate = borrowDate;
        this.returnDate = returnDate;
        this.bookCode = bookCode;
    }

    public Borrow(int id, String borrowerName, String phoneNumber, String borrowDate, String returnDate, String bookCode, int readerId, double fine) {
        this.id = id;
        this.borrowerName = borrowerName;
        this.phoneNumber = phoneNumber;
        this.borrowDate = borrowDate;
        this.returnDate = returnDate;
        this.bookCode = bookCode;
        this.readerId = readerId;
        this.fine = fine;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getBorrowerName() { return borrowerName; }
    public void setBorrowerName(String borrowerName) { this.borrowerName = borrowerName; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public String getBorrowDate() { return borrowDate; }
    public void setBorrowDate(String borrowDate) { this.borrowDate = borrowDate; }
    public String getReturnDate() { return returnDate; }
    public void setReturnDate(String returnDate) { this.returnDate = returnDate; }
    public String getBookCode() { return bookCode; }
    public void setBookCode(String bookCode) { this.bookCode = bookCode; }
    public int getReaderId() { return readerId; }
    public void setReaderId(int readerId) { this.readerId = readerId; }
    public double getFine() { return fine; }
    public void setFine(double fine) { this.fine = fine; }
}