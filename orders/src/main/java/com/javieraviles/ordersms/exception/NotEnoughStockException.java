package com.javieraviles.ordersms.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Not enough stock")
public class NotEnoughStockException extends RuntimeException {

}