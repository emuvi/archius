package br.com.pointel.archius;

public abstract class ConfigNamer {

    private final ConfigNamerType type;
    private final String name;
    private final Boolean required;

    public ConfigNamer(ConfigNamerType type, String name, Boolean required) {
        this.type = type;
        this.name = name;
        this.required = required;
    }

    public ConfigNamerType getKind() {
        return type;
    }

    public String getName() {
        return this.name;
    }

    public Boolean getRequired() {
        return this.required;
    }

}
