package fr.maxlego08.zsupport.faq;

public class Faq {
    private final String name;
    private final String title;
    private final String answer;
    private long id;


    public Faq(String name, String title, String answer) {
        this.name = name;
        this.title = title;
        this.answer = answer;
    }

    public Faq(long id, String name, String title, String answer) {
        this.name = name;
        this.title = title;
        this.answer = answer;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getTitle() {
        return title;
    }

    public String getAnswer() {
        return answer;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}