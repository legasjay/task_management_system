package com.olegandreevich.tms.util;

import lombok.AllArgsConstructor;
import lombok.Data;

// Класс для представления ошибок
@Data
@AllArgsConstructor
public class ApiError {
    private int status;
    private String message;
}
