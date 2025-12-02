package com.rehancode.chatapp.Exceptions;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {

    private LocalDateTime timestamp;   
    private int status;               
    private boolean success;           
    private String message;           
    private T data;                   

}
