package net.vpc.kifkif.export;

/**
 * @author vpc
 * Date: 16 janv. 2005
 * Time: 16:21:45
 */
public class ExportException extends Exception {
    public ExportException() {
    }

    public ExportException(String message) {
        super(message);
    }

    public ExportException(String message, Throwable cause) {
        super(message, cause);
    }

    public ExportException(Throwable cause) {
        super(cause);
    }
}
