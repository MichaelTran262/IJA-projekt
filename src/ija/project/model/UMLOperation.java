package ija.project.model;

import java.util.Arrays;
import java.util.List;


/**
 * Třída reprezentuje operaci, která má své jméno, návratový typ a seznam argumentů.
 * @author Thanh Q. Tran    xtrant02
 * @version 1.0
 */
public class UMLOperation extends UMLAttribute {
    private UMLAttribute[] args;

    public UMLOperation(String name) {
        super(name);
    }

    public UMLOperation(String name, String type) {
        super(name, type);
    }

    public static UMLOperation create(String name, String type, UMLAttribute... args) {
        UMLOperation newOperation = new UMLOperation(name, type);
        newOperation.args = args;
        return newOperation;
    }

    public boolean addArgument(UMLAttribute arg) {
        args = Arrays.copyOf(args, args.length + 1);
        args[args.length - 1] = arg;
        return true;
    }

    public List<UMLAttribute> getArguments() {
        return List.of(args);
    }

}
