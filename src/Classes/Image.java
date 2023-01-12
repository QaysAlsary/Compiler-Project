package Classes;

public class Image extends Widget {
    public String path;

    public Image(String imagePath) {
        this.path = imagePath;
    }

    @Override
    public String toString() {
        return "\n            image path{"+path+"}";
    }
}
