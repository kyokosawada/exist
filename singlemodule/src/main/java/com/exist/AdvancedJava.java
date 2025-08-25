package com.exist;

public final class AdvancedJava {

    public static void main(String[] args) {
        try {

            FileService fileService = new FileService();
            String fileName = (args.length == 0) ? FileService.DEFAULT_RESOURCE : fileService.getFileName(args);

            MenuService menu = new MenuService();
            menu.startApplication(fileName);
            menu.displayMenu();

        } catch (Exception e) {
            System.err.println("System Error: " + e.getMessage());
        }
    }
}