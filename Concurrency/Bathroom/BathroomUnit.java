package Concurrency.Bathroom;

public class BathroomUnit {
    private String group = null;
    private int inside = 0;

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public int getInside() {
        return inside;
    }

    public void setInside(int inside) {
        this.inside = inside;
    }

    public void addInside() {
        this.inside = this.inside + 1;
    }

    public void removeInside() {
        this.inside = this.inside - 1;
    }
}