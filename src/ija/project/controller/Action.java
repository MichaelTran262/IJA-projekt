package ija.project.controller;

/**
* Interface pro třídy využité v implementaci undo a redo
 */
public interface Action{
    void run();
    void undo();
    void redo();

}