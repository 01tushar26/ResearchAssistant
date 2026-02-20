package com.buddy.Code_Buddy.DTO;

import com.buddy.Code_Buddy.DTO.Enums.OperationsEnums;
import lombok.Data;

@Data

public class ResearchRequest {

    private String content;
    private OperationsEnums operation;

}
