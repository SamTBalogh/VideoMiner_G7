package aiss.videominer.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code= HttpStatus.FORBIDDEN, reason="VideoMiner doesn't allow calls without a token")
public class TokenRequiredException extends Exception {
}
