package aiss.videominer.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code= HttpStatus.BAD_REQUEST, reason = "Just one search parameter for filtering must be provided.")
public class BadRequestParameterField extends Exception{
}
