package br.com.pointel.archius;

public class ArchBaseUnit {
    
    private final String place;
    private final String verifier;
    private final Long modified;
    private final Long indexed;

    public ArchBaseUnit(String place, String verifier, Long modified, Long indexed) {
        this.place = place;
        this.verifier = verifier;
        this.modified = modified;
        this.indexed = indexed;
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

    public Long getIndexed() {
        return this.indexed;
    }
    
}
