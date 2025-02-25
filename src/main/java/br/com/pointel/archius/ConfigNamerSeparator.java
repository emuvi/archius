package br.com.pointel.archius;

public class ConfigNamerSeparator extends ConfigNamer {

    private final String chars;

    public ConfigNamerSeparator(String name, Boolean required, String chars) {
        super(ConfigNamerType.SEPARATOR, name, required);
        this.chars = chars;
    }

    public String getChars() {
        return this.chars;
    }

}
