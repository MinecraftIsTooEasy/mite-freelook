package com.github.jeffyjamzhd.freelook.config;

public enum ZoomEnum {
    REALISTIC("freelook.zoom_type.realistic"),
    INTERPOLATED("freelook.zoom_type.interpolated"),
    CLASSIC("freelook.zoom_type.classic");

    public String name;
    ZoomEnum(String name) {
        this.name = name;
    }
}
