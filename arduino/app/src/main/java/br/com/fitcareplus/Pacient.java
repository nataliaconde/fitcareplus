package br.com.fitcareplus;

public class Pacient {
    private String username;
    private int image;
    private String description;

    public Pacient(String username, String description, int image){
        this.username = username;
        this.description = description;
        this.image = image;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
