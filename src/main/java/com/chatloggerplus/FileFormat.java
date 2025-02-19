package com.chatloggerplus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FileFormat {
    TEXT("Text"),
    JSON("JSON");

    private final String name;

    @Override
    public String toString() {
        return name;
    }
}
