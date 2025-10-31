package org.wa.auth.service.mapper;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.http.HttpStatus;
import org.wa.auth.service.model.ErrorResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;


public class ErrorMapperTest {

    private final ErrorMapper errorMapper = Mappers.getMapper(ErrorMapper.class);

    @Test
    void toErrorResponseTest() {
        String message = "message";
        HttpStatus status = HttpStatus.BAD_REQUEST;

        ErrorResponse errorResponse = errorMapper.toErrorResponse(message, status);

        assertNotNull(errorResponse);
        assertEquals(message, errorResponse.getMessage());
        assertEquals(status.value(), errorResponse.getStatus());
        assertNotNull(errorResponse.getTimestamp());
    }

    @Test
    void toErrorResponseNullTest() {
        ErrorResponse errorResponse = errorMapper.toErrorResponse(null, null);

        assertNull(errorResponse);
    }
}
