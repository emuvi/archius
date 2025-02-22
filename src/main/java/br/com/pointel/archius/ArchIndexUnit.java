package br.com.pointel.archius;

public class ArchIndexUnit {
    
    private final String place;
    private final String words;

    public ArchIndexUnit(String place, String words) {
        this.place = place;
        this.words = words;
    }

    public String getPlace() {
        return this.place;
    }

    public String getWords() {
        return this.words;
    }
    
}
