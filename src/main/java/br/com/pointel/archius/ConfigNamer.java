package br.com.pointel.archius;

import java.io.Serializable;

public abstract class ConfigNamer implements Serializable {

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

    public Boolean isRequired() {
        return this.required;
    }

    @Override
    public String toString() {
        return name;
    }

}
