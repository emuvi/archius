package br.com.pointel.archius;

public class ArchBaseUnit {
    
    private final String place;
    private final String verifier;
    private final Long modified;

    public ArchBaseUnit(String place, String verifier, Long modified) {
        this.place = place;
        this.verifier = verifier;
        this.modified = modified;
    }

    public String getPlace() {
        return this.place;
    }

    public String getVerifier() {
        return this.verifier;
    }

    public Long getModified() {
        return this.modified;
    }
    
}
