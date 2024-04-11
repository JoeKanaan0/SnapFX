package ar.midtermproject.model;

public record Chat(long id, String text, User sender, User receiver) {

    public Chat(String text, User sender, User receiver) {
        this(-1, text, sender, receiver);
    }

}