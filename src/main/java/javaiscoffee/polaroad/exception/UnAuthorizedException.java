package javaiscoffee.polaroad.exception;

/**
 * 클라이언트가 인증되지 않았거나, 유효한 인증 정보가 부족하여 요청이 거부되었음을 의미하는 상태값이다.
 * 즉, 클라이언트가 인증되지 않았기 때문에 요청을 정상적으로 처리할 수 없다고 알려주는 것
 * 로그인하지 않은 상태에서 로그인 인증이 필요한 서비스를 요청한 경우
 */
public class UnAuthorizedException extends RuntimeException{
    public UnAuthorizedException(String message) {
        super(message);
    }
}
