package javaiscoffee.polaroad.response;

public class Status {
    private ResponseStatus responseStatus;

    public Status(ResponseStatus responseStatus) {
        this.responseStatus = responseStatus;
    }

    public String getMessage() {
        return responseStatus.getMessage();
    }
}