public class Wordval {
    String word;
    Double value;
    Wordval[] children; //how many children the wordval has - 0 means leaf, break

    public Wordval() {
        this.word = null;
        this.value = null;
        this.children = null;
    }

    public boolean isLeaf(){
        return children.length==0;
    }

    public Wordval(String word, double value, Wordval[] children){
        this.word = word;
        this.value = value;
        this.children = children;
    }

    public void setValue(double value){
        this.value=value;
    }
}
