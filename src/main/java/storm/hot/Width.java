package storm.hot;
public class Width {
    int window;
    int slide;
    Width() {
        this.window = Integer.getInteger("width.window", 3600);
        this.slide = Integer.getInteger("width.slide", 60);
    }
}
