package me.partlysunny.util.classes;

public enum ServerVersion {
    v1_16_R3("1.16.5"),
    v1_17_R1("1.17.1"),
    v1_18_R2("1.18.2"),
    v1_19_R1("1.19.2")
    ;

    private final String name;

    ServerVersion(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
