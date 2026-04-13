package common.model;

import java.io.Serializable;

public class QuestionCell implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String category;
    private final int value;
    private final String question;
    private final String answer;

    private boolean used;

    public QuestionCell(String category, int value, String question, String answer) {
        this.category = category;
        this.value = value;
        this.question = question;
        this.answer = answer;
        this.used = false;
    }

    public String getCategory() {
        return category;
    }

    public int getValue() {
        return value;
    }

    public String getQuestion() {
        return question;
    }

    public String getAnswer() {
        return answer;
    }

    public boolean isUsed() {
        return used;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }
}