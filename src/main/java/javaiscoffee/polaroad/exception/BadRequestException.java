package javaiscoffee.polaroad.exception;

/**
 * 멤버나 게시글, 리뷰 등 데이터가 존재하지 않을 경우 발생시키는 에러
 */
public class BadRequestException extends RuntimeException{
    public BadRequestException(String message) {
        super(message);
    }
}