package com.exist.app;

import com.exist.service.FileService;

public final class AdvancedJava {

    public static void main(String[] args) {
        try {

            FileService fileService = new FileService();
            String fileName = (args.length == 0) ? FileService.DEFAULT_RESOURCE : fileService.getFileName(args);

            MenuManager menu = new MenuManager();
            menu.startApplication(fileName);
            menu.displayMenu();

        } catch (Exception e) {
            System.err.println("System Error: " + e.getMessage());
        }
    }
}