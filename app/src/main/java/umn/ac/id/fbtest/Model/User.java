package umn.ac.id.fbtest.Model;

public class User {
    private String company, identification, image, nama;

    public User()
    {

    }

    public User(String company, String identification, String image, String nama) {
        this.company = company;
        this.identification = identification;
        this.image = image;
        this.nama = nama;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getIdentification() {
        return identification;
    }

    public void setIdentification(String identification) {
        this.identification = identification;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }
}
