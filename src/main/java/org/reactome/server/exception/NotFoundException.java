package org.reactome.server.exception;

/**
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 */
public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}
