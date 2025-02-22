package br.com.pointel.archius;

public class ArchBaseUnit {
    
    private final String place;
    private final Long modified;
    private final String verifier;

    public ArchBaseUnit(String place, Long modified, String verifier) {
        this.place = place;
        this.modified = modified;
        this.verifier = verifier;
    }

    public String getPlace() {
        return this.place;
    }

    public Long getModified() {
        return this.modified;
    }

    public String getVerifier() {
        return this.verifier;
    }
    
}
