package com.frozen.sakura;

public class Main {

    public static void main(String[] args) {
        System.out.println(args[1]);
	    FileHelper fileHelper = new FileHelper();
	    fileHelper.createFile();
    }
}
