package me.partlysunny.worldedit;


import com.sk89q.worldedit.extent.clipboard.Clipboard;

public class ClipboardWrapper {

    private final String id;
    private final Clipboard clipboard;

    public ClipboardWrapper(String id, Clipboard clipboard) {
        this.id = id;
        this.clipboard = clipboard;
    }

    public String id() {
        return id;
    }

    public Clipboard clipboard() {
        return clipboard;
    }
}
