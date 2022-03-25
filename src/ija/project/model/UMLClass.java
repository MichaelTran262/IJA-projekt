package ija.project.model;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class UMLClass extends UMLClassifier {
    private boolean isAbstract;
    private final List<UMLAttribute> attributeList;

    public UMLClass(String name) {
        super(name);
        attributeList = new LinkedList<UMLAttribute>();
    }

    public boolean isAbstract() {
        return isAbstract;
    }

    public void setAbstract(boolean isAbstract) {
        this.isAbstract = isAbstract;
    }

    public boolean addAttribute(UMLAttribute attr) {
        if (attributeList.contains(attr)) {
            return false;
        } else {
            attributeList.add(attr);
            return true;
        }
    }

    public int getAttrPosition(UMLAttribute attr) {
        return attributeList.indexOf(attr);
    }

    public int moveAttrAtPosition(UMLAttribute attr, int pos) {
        if (attributeList.contains(attr)) {
            attributeList.remove(getAttrPosition(attr));
            attributeList.add(pos, attr);
            return 0;
        } else {
            return -1;
        }
    }

    public List<UMLAttribute> getAttributes() {
        return Collections.unmodifiableList(attributeList);
    }
}
