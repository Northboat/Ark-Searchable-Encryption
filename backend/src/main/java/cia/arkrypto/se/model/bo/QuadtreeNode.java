package cia.arkrypto.se.model.bo;

import it.unisa.dia.gas.jpbc.Element;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class QuadtreeNode {

    private String m;
    private Point p;
    private Element[] x;
    private Element s_x, t_x;
    private QuadtreeNode[] subtree;

    public QuadtreeNode(){
        subtree = new QuadtreeNode[4];
    }

    public QuadtreeNode(Point o){
        this.p = o;
        subtree = new QuadtreeNode[4];
    }

    public QuadtreeNode(Point p, String m, Element[] x, Element s_x, Element t_x){
        this.p = p;
        this.m = m;
        this.x = x;
        this.s_x = s_x;
        this.t_x = t_x;
        subtree = new QuadtreeNode[4];
    }


    public void setSubtree(QuadtreeNode t, int i){
        this.subtree[i] = t;
    }


}
