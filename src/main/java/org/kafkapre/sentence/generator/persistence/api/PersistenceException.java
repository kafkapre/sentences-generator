package org.kafkapre.sentence.generator.persistence.api;

public class PersistenceException extends RuntimeException {

    private static final long serialVersionUID = -391250647648738338L;

    public PersistenceException() {
    }

    public PersistenceException(String s) {
        super(s);
    }

    public PersistenceException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public PersistenceException(Throwable throwable) {
        super(throwable);
    }

    public PersistenceException(String s, Throwable throwable, boolean b, boolean b1) {
        super(s, throwable, b, b1);
    }
}
