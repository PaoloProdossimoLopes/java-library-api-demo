package com.paoloprodossimolopes.libraryapidemo.api.exceptions;

import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InvalidParamsException {
    private List<String> errors;

    public InvalidParamsException(BindingResult result) {
        this.errors = new ArrayList<>();
        for (ObjectError e: result.getAllErrors()) {
            this.errors.add("invalid field");
        }
    }

    public InvalidParamsException(BussinessException exc) {
        this.errors = Arrays.asList(exc.getMessage());
    }

    public List<String> getErrors() {
        return errors;
    }
}
