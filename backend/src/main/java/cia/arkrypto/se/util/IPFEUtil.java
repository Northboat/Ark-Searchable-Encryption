package cia.arkrypto.se.util;


import cia.arkrypto.se.ds.Ciphertext;
import cia.arkrypto.se.ds.Point;
import cia.arkrypto.se.ds.QuadtreeNode;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Field;


public class IPFEUtil {

    // 公钥
    public static Element g, h;
    private static Element[] s, t, h_i;


    public static Element getBase(){
        return g;
    }

    public static void setup(Field G1, Field Zr, int l){

        g = G1.newRandomElement().getImmutable();
        h = G1.newRandomElement().getImmutable();

        s = new Element[l];
        t = new Element[l];
        h_i = new Element[l];
        for(int i = 0; i < l; i++){
            s[i] = Zr.newRandomElement().getImmutable();
            t[i] = Zr.newRandomElement().getImmutable();
            h_i[i] = g.powZn(s[i]).mul(g.powZn(t[i])).getImmutable();
        }

    }


    public static QuadtreeNode keygen(Field Zr, Element[] x, Point p, String m){
//        System.out.println("====== KeyGen ======");
        int n = x.length;
        Element s1 = Zr.newZeroElement();
        Element s2 = Zr.newZeroElement();

        for(int i = 0; i < n; i++){
            s1 = s1.add(s[i].mul(x[i]));
            s2 = s2.add(t[i].mul(x[i]));
        }
        Element s_x = s1.getImmutable();
        Element t_x = s2.getImmutable();

        return new QuadtreeNode(p, m, x, s_x, t_x);
    }


    public static Ciphertext encrypt(Element[] y, Element r){
        int n = y.length;
        Element C = g.powZn(r).getImmutable();
        Element D = h.powZn(r).getImmutable();
        Element[] E = new Element[n];
        for(int i = 0; i < n; i++){
            E[i] = g.powZn(y[i]).mul(h_i[i].powZn(r)).getImmutable();
        }

        return new Ciphertext(y, C, D, E);
    }

    public static Element decrypt(Field G1, QuadtreeNode t, Ciphertext c){
        Element[] x = t.getX();
        Element s_x = t.getS_x();
        Element t_x = t.getT_x();
        int n = x.length;

        Element e = G1.newOneElement();
        for(int i = 0; i < n; i++){
            e = e.mul(c.getE()[i].powZn(x[i]));
        }

        Element p1 = e.getImmutable();
        Element p2 = c.getC().powZn(s_x).mul(c.getD().powZn(t_x)).getImmutable();

        return p1.div(p2).getImmutable(); // Ex
    }


    public static Element innerProduct(Field Zr, Element[] x, Element[] y){
        Element product = Zr.newZeroElement();
        int n = x.length;
        for(int i = 0; i < n; i++){
            product = product.add(x[i].mul(y[i]));
        }
        return product.getImmutable();
    }
}
