package me.partlysunny.blocks.loot;

public class TableEntryWrapper {

    private String entry;
    private int weight;
    private String message;

    public TableEntryWrapper() {
        this("", 0, "");
    }

    public TableEntryWrapper(String entry, int weight, String message) {
        this.entry = entry;
        this.weight = weight;
        this.message = message;
    }

    public String entry() {
        return entry;
    }

    public void setEntry(String entry) {
        this.entry = entry;
    }

    public int weight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public String message() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
