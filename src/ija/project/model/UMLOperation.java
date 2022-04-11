package ija.project.model;

import java.util.Arrays;
import java.util.List;

public class UMLOperation extends UMLAttribute {
    private UMLAttribute[] args;

    public UMLOperation(String name, String type) {
        super(name, type);
    }

    public static UMLOperation create(String name, String type, UMLAttribute... args) {
        UMLOperation newOperation = new UMLOperation(name, type);
        newOperation.args = args;
        return newOperation;
    }

    public boolean addArgument(UMLAttribute arg) {
        args = Arrays.copyOf(args, args.length + 1); //create new array from old array and allocate one more element
        args[args.length - 1] = arg;
        return true;
    }

    public List<UMLAttribute> getArguments() {
        return List.of(args);
    }
}
