package javaiscoffee.polaroad.exception;

/**
 * 서버가 해당 요청을 이해했지만, 권한이 없어 요청이 거부되었음을 의미하는 상태값이다.
 * 즉, 클라이언트가 해당 요청에 대한 권한이 없다고 알려주는 것
 * 로그인은 했지만 해당 요청에 대한 권한이 없는 경우
 */
public class ForbiddenException extends RuntimeException{
    public ForbiddenException(String message) {
        super(message);
    }
}
