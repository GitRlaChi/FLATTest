package flat.exep;

public class FlatException extends Exception {
    final String cause;

    public FlatException(String c) {
        cause = c;
    }

    public FlatException() {
        this("오류 발생");
    }

    public String getMessage() {
        return cause;
    }
}