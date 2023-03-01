package br.com.provensi.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND)
public class MyFileNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public MyFileNotFoundException(String ex) {
		super(ex);
	}

	public MyFileNotFoundException(String ex, Throwable cause) {
		super(ex, cause);
	}
}
