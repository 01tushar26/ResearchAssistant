package com.buddy.Code_Buddy.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OlamaModelRequest {
    private String model;
    private String prompt;
    private boolean stream;
}
