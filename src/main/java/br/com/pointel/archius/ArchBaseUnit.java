package br.com.pointel.archius;

public class ArchBaseUnit {
    
    public final String place;
    public final Long modified;
    public final String verifier;

    public ArchBaseUnit(String place, Long modified, String verifier) {
        this.place = place;
        this.modified = modified;
        this.verifier = verifier;
    }
    
}
