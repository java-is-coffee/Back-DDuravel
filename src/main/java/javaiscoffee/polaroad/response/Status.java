package javaiscoffee.polaroad.response;

public class Status {
    private ResponseMessages responseMessages;

    public Status(ResponseMessages responseMessages) {
        this.responseMessages = responseMessages;
    }

    public String getMessage() {
        return responseMessages.getMessage();
    }
}