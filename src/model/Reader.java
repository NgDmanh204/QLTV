package model;

public class Reader {
    private int id;
    private String readerCode;
    private String fullname;
    private String gender;
    private String birthday;
    private String phone;
    private String email;
    private String address;

    public Reader() {}

    public Reader(int id, String readerCode, String fullname, String gender, String birthday, String phone, String email, String address) {
        this.id = id;
        this.readerCode = readerCode;
        this.fullname = fullname;
        this.gender = gender;
        this.birthday = birthday;
        this.phone = phone;
        this.email = email;
        this.address = address;
    }

    public Reader(String readerCode, String fullname, String gender, String birthday, String phone, String email, String address) {
        this.readerCode = readerCode;
        this.fullname = fullname;
        this.gender = gender;
        this.birthday = birthday;
        this.phone = phone;
        this.email = email;
        this.address = address;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getReaderCode() { return readerCode; }
    public void setReaderCode(String readerCode) { this.readerCode = readerCode; }
    public String getFullname() { return fullname; }
    public void setFullname(String fullname) { this.fullname = fullname; }
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    public String getBirthday() { return birthday; }
    public void setBirthday(String birthday) { this.birthday = birthday; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
}