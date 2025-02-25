package br.com.pointel.archius;

public class ConfigNamerField extends ConfigNamer {

    private final String prefix;
    private final String suffix;

    public ConfigNamerField(String name, Boolean required, String prefix, String suffix) {
        super(ConfigNamerType.FIELD, name, required);
        this.prefix = prefix;
        this.suffix = suffix;
    }

    public String getPrefix() {
        return this.prefix;
    }

    public String getSuffix() {
        return this.suffix;
    }

}
