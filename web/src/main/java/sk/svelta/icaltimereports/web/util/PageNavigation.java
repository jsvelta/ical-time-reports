package sk.svelta.icaltimereports.web.util;

/**
 * Simple enum to centralize strings for common navigation destinations.
 *
 * @author Jaroslav Švelta
 */
public enum PageNavigation {

    CREATE("Create"),
    LIST("List"),
    EDIT("Edit"),
    VIEW("View"),
    INDEX("/index");

    private final String text;

    private PageNavigation(final String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    @Override
    public String toString() {
        return this.text;
    }

}
