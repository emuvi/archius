package br.com.pointel.archius;

public class ArchIndexUnit {
    
    private final String place;
    private final String words;
    private final String likes;
    private final Long indexed;

    public ArchIndexUnit(String place, String words, String likes, Long indexed) {
        this.place = place;
        this.words = words;
        this.likes = likes;
        this.indexed = indexed;
    }

    public String getPlace() {
        return this.place;
    }

    public String getWords() {
        return this.words;
    }

    public String getLikes() {
        return likes;
    }

    public Long getIndexed() {
        return indexed;
    }
    
}
