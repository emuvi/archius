package br.com.pointel.archius;

public class ArchIndexUnit {
    
    private final String place;
    private final String words;
    private final Long indexed;

    public ArchIndexUnit(String place, String words, Long indexed) {
        this.place = place;
        this.words = words;
        this.indexed = indexed;
    }

    public String getPlace() {
        return this.place;
    }

    public String getWords() {
        return this.words;
    }

    public Long getIndexed() {
        return indexed;
    }
    
}
