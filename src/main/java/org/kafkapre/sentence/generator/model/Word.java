package org.kafkapre.sentence.generator.model;


import com.fasterxml.jackson.annotation.JsonProperty;

public class Word {

    @JsonProperty("word" )
    private String text;

    @JsonProperty
    private WordCategory category;

    // TODO add href

    Word(){
    }

    public Word(String text, WordCategory category){
        this.text = text;
        this.category = category;
    }


    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public WordCategory getCategory() {
        return category;
    }

//    public void setCategory(WordCategory category) {
//        this.category = category;
//    }

    @Override
    public String toString() {
        return "Word{" +
                "text='" + text + '\'' +
                ", category=" + category +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Word)) {
            return false;
        }

        Word word = (Word) o;

        if (text != null ? !text.equals(word.text) : word.text != null) {
            return false;
        }
        return category == word.category;

    }

    @Override
    public int hashCode() {
        int result = text != null ? text.hashCode() : 0;
        result = 31 * result + (category != null ? category.hashCode() : 0);
        return result;
    }
}
