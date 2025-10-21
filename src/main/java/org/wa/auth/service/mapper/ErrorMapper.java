package org.wa.auth.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.http.HttpStatus;
import org.wa.auth.service.model.ErrorResponse;

import java.time.LocalDateTime;

@Mapper(componentModel = "spring", imports = LocalDateTime.class)
public interface ErrorMapper {
    @Mapping(target = "message", source = "message")
    @Mapping(target = "status", expression = "java(status.value())")
    @Mapping(target = "timestamp", expression = "java(LocalDateTime.now())")
    ErrorResponse toErrorResponse(String message, HttpStatus status);
}
