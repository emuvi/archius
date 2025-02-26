package br.com.pointel.archius;

public class ConfigNamerCombo extends ConfigNamer {

    private final String[] options;
    private final String prefix;
    private final String suffix;

    public ConfigNamerCombo(String name, Boolean required, String[] options, String prefix, String suffix) {
        super(ConfigNamerType.FIELD, name, required);
        this.options = options;
        this.prefix = prefix;
        this.suffix = suffix;
    }

    public String[] getOptions() {
        return this.options;
    }

    public String getPrefix() {
        return this.prefix;
    }

    public String getSuffix() {
        return this.suffix;
    }

}
