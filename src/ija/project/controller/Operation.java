package ija.project.controller;

import javafx.scene.shape.Line;
import javafx.scene.text.Text;

public class Operation extends Line {
    private int from;
    private int to;
    private int type;
    private int order;
    private Line up = new Line();
    private Line down = new Line();
    private Text name;

    public Operation(int odkud, int kam, int typ, int poradi, String jmeno){
        super();
        from=odkud;
        to=kam;
        type=typ;
        order=poradi;
        setStartX(70+from*100);
        setStartY(135+order*60);
        setEndX(this.getStartX()+(to-from)*100);
        setEndY(getStartY());
        name = new Text(jmeno);
        setUpDownText();
        if(type == 2){
            getStrokeDashArray().addAll(5d,4d);
        }
    }

    public Line getUp(){return up;}
    public Line getDown(){return down;}
    public Text getName(){return name;}
    private void setUpDownText(){
        if(from < to){
            up.setStartX(getEndX());
            up.setStartY(getEndY());
            up.setEndX(getEndX()-10);
            up.setEndY(getEndY()-10);
            down.setStartX(getEndX());
            down.setStartY(getEndY());
            down.setEndX(getEndX()-10);
            down.setEndY(getEndY()+10);
            name.setX(getStartX()+10);
            name.setY(getStartY()-5);
        }
        else{
            up.setStartX(getEndX());
            up.setStartY(getEndY());
            up.setEndX(getEndX()+10);
            up.setEndY(getEndY()-10);
            down.setStartX(getEndX());
            down.setStartY(getEndY());
            down.setEndX(getEndX()+10);
            down.setEndY(getEndY()+10);
            name.setX((getStartX()+getEndX())/2-10);
            name.setY(getStartY()-5);
        }
    }
}
