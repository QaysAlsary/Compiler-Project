package Classes;

public class Text extends Widget {
    public String text;

    public Text(String text){
        this.text = text;
    }public Text(){
    }

    @Override
    public String toString() {
        return "Text{"+text+"}";
    }

}
