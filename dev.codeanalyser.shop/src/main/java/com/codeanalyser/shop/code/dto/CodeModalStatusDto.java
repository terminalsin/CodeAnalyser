package com.codeanalyser.shop.code.dto;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CodeModalStatusDto {
    @SerializedName("id")
    private UUID id;

    @SerializedName("status")
    private String status;

    public CodeModalStatusDto(UUID id, String status) {
        this.id = id;
        this.status = status;
    }

    public CodeModalStatusDto() {
    }
}
