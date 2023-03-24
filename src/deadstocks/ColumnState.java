package deadstocks;

public class ColumnState {
    private boolean visible;
    private boolean grouped;
    private int span;

    public ColumnState(boolean visible, boolean grouped, int span) {
        this.visible = visible;
        this.grouped = grouped;
        this.span = span;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public boolean isGrouped() {
        return grouped;
    }

    public void setGrouped(boolean grouped) {
        this.grouped = grouped;
    }

    public int getSpan() {
        return span;
    }

    public void setSpan(int span) {
        this.span = span;
    }
}
