package com.github.core.interceptor.impl;

import com.github.core.interceptor.FileInter;

import java.io.File;

public class FilePrintInte implements FileInter {
    @Override
    public void apply(File file) {
        System.out.println(file.getPath());
    }
}
