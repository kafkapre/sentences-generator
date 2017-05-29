package org.kafkapre.sentence.generator.model;

import org.apache.commons.lang3.Validate;

import java.util.Optional;


public class Words {

    private final String noun;
    private final String verb;
    private final String adjective;

    public Words(String noun, String verb, String adjective) {
        this.noun = noun;
        this.verb = verb;
        this.adjective = adjective;
    }

    public Words(Optional<Word> noun, Optional<Word> verb, Optional<Word> adjective) {
        this(getWordText(noun), getWordText(verb), getWordText(adjective));
    }

    private static String getWordText(Optional<Word> word) {
        if (word.isPresent() && word.get().getText().trim().length() > 0) {
            return word.get().getText();
        } else {
            return null;
        }
    }

    public void validate() {
        Validate.notBlank(noun, "Noun is missing.");
        Validate.notBlank(verb, "Verb is missing.");
        Validate.notBlank(adjective, "Adjective is missing.");
    }

    public String getNoun() {
        return noun;
    }

    public String getVerb() {
        return verb;
    }

    public String getAdjective() {
        return adjective;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Words)) {
            return false;
        }

        Words words = (Words) o;

        if (noun != null ? !noun.equals(words.noun) : words.noun != null) {
            return false;
        }
        if (verb != null ? !verb.equals(words.verb) : words.verb != null) {
            return false;
        }
        return adjective != null ? adjective.equals(words.adjective) : words.adjective == null;

    }

    @Override
    public int hashCode() {
        int result = noun != null ? noun.hashCode() : 0;
        result = 31 * result + (verb != null ? verb.hashCode() : 0);
        result = 31 * result + (adjective != null ? adjective.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Words{" +
                "noun='" + noun + '\'' +
                ", verb='" + verb + '\'' +
                ", adjective='" + adjective + '\'' +
                '}';
    }
}
