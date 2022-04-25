package ija.project.controller;

import javafx.stage.FileChooser;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;
import java.nio.file.Files;
import java.nio.file.Paths;
import com.fasterxml.jackson.databind.ObjectMapper;

import ija.project.model.UMLClass;


public class FileHandler {

    public class FileClass {
        String name;
        String[] attributes;
    }

    private File file;

    public FileHandler(File file) {
        this.file = file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public void getFile() {
        System.out.println(file.getAbsolutePath());
    }


    /**
     * Funkce zparsuje soubor a vrátí třídy
     */
    public void parseFile() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            // TODO: Dostat ze souboru ./tests/test1.json data a převést na manipulovatelný objekt
            FileClass[] classes = mapper.readValue(file, FileClass.class);
            for (FileClass cl : classes){
                System.out.println(cl);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
