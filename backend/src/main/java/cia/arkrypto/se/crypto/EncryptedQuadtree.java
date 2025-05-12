package cia.arkrypto.se.crypto;

import cia.arkrypto.se.ds.Ciphertext;
import cia.arkrypto.se.ds.Point;
import cia.arkrypto.se.ds.QuadtreeNode;
import cia.arkrypto.se.util.HashUtil;
import cia.arkrypto.se.util.IPFEUtil;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Field;
import lombok.Getter;
import org.springframework.security.core.parameters.P;

import java.util.Arrays;
import java.util.Objects;
import java.util.Random;

@Getter
public class EncryptedQuadtree {

    QuadtreeNode root;
    Field G1, Zr;
    int l;
    public EncryptedQuadtree(Field G1, Field Zr, int l){
        this.G1 = G1;
        this.Zr = Zr;
        this.l = l;
        root = new QuadtreeNode(new Point(0, 0));
        height = 1;
        IPFEUtil.setup(G1, Zr, l);
    }


    public String toZCode(int x1, int y1, int x2, int y2){
        if(x1 <= x2){
            if(y1 >= y2){
                return "00";
            } else {
                return "01";
            }
        } else if(y1 >= y2){
            return "10";
        }
        return "11";
    }


    public String toZCode(Point p1, Point p2){
        return toZCode(p1.getX(), p1.getY(), p2.getX(), p2.getY());
    }


    public QuadtreeNode creatNode(String m, Point p){
        Element[] x = HashUtil.hashStr2ZrArr(Zr, m, l);
        return IPFEUtil.keygen(Zr, x, p, m);
    }

    public int getMatchedIndex(String z){
        return switch (z) { // 找下标
            case "00" -> 0;
            case "01" -> 1;
            case "10" -> 2;
            case "11" -> 3;
            default -> -1;
        };
    }

    public int getMatchedIndex(Point p, Point o){
        return getMatchedIndex(toZCode(p, o));
    }

    public boolean insert(String pre, Point p, QuadtreeNode root){
        Point o = root.getP();
        String z = toZCode(p, o); // 增加的前缀
        int i = getMatchedIndex(z);
        QuadtreeNode n = creatNode(pre + z, p); // 根据当前前缀和坐标生成节点
        QuadtreeNode cur = root.getSubtree()[i];
        // 如果这里为空，就直接插入
        if(cur == null){
//            System.out.println(n.getM());
            root.setSubtree(n, i);
            return true;
        }
        // 否则继续向下找
        return insert(pre+z, p, root.getSubtree()[i]);
    }

    public boolean insert(Point p){
        return insert("", p, root);
    }


    public void clean(){
        height = 1;
        root = new QuadtreeNode(new Point(0, 0));
    }

    public void build(Point[] P){
        for(Point p: P){
            if(!insert(p)){
                return;
            }
        }
    }


    public void build(int count){
        build(getPoints(count));
    }


    public Ciphertext getCiphertext(Point p){
        String m = toZCode(p, root.getP());
        Element r = Zr.newRandomElement().getImmutable();
        return IPFEUtil.encrypt(HashUtil.hashStr2ZrArr(Zr, m, l), r);
    }




    public Element innerProduct(Element[] x, Element[] y){
        return IPFEUtil.innerProduct(Zr, x, y);
    }


    // 不知道怎么判断，公式上有点疑问
    public boolean matched(QuadtreeNode node, Ciphertext ciphertext){
        if(node == null){
            return false;
        }

        Element g = IPFEUtil.getBase();
        Element[] x = node.getX();
        Element[] y = ciphertext.getY();
        Element product = innerProduct(x, y);
        Element left = g.powZn(product).getImmutable();

        Element right = IPFEUtil.decrypt(G1, node, ciphertext);

        return left.isEqual(right);
    }

    public QuadtreeNode search(QuadtreeNode node, Ciphertext ciphertext, Point p){
        if(node == null){
            return null;
        }

        if(matched(node, ciphertext)) {
            return node;
        }

        int i = getMatchedIndex(p, node.getP());
        search(node.getSubtree()[i], ciphertext, p);

        return null;
    }


    public QuadtreeNode search(Point p){
        Ciphertext ciphertext = getCiphertext(p);
        int i = getMatchedIndex(p, root.getP());
        return search(root.getSubtree()[i], ciphertext, p);
    }



    public String getTreeStruct(int type) {
        StringBuilder sb = new StringBuilder();
        getTreeStruct(root, "", false, sb, type, 1);
        return sb.toString();
    }


    int height;
    // DFS
    public void getTreeStruct(QuadtreeNode node, String prefix, boolean isTail, StringBuilder sb, int type, int h) {
        if (node == null) return;

        height = Math.max(h, height);

        sb.append(prefix).append(isTail ? "└── " : "├── ");
        switch (type){
            case 1 -> sb.append(formatPoint(node)).append("\n");
            case 2 -> sb.append(formatKeyPair(node)).append("\n");
            case 3 -> sb.append(formatPreStr(node)).append("\n");
            case 4 -> sb.append(formatNode(node)).append("\n");
        }

        QuadtreeNode[] children = node.getSubtree();
        int childCount = (int) Arrays.stream(children).filter(Objects::nonNull).count();

        int printed = 0;
        for (int i = 0; i < 4; i++) {
            QuadtreeNode child = children[i];
            if (child != null) {
                printed++;
                boolean last = printed == childCount;
                getTreeStruct(child, prefix + (isTail ? "    " : "│   "), last, sb, type, h+1);
            }
        }
    }

    public String formatPoint(QuadtreeNode node) {
        // 可以自定义显示内容，比如只显示坐标
        return node.getP() != null ? node.getP().toString() : "(null)";
    }


    public String formatKeyPair(QuadtreeNode node) {
        // 可以自定义显示内容，比如只显示坐标
        return node.getP() != null ? "(s_x = " + node.getS_x() + ", t_x = " + node.getT_x() + ")" : "(null)";
    }

    public String formatPreStr(QuadtreeNode node) {
        // 可以自定义显示内容，比如只显示坐标
        return node.getP() != null ? "("  + node.getM() + ")" : "(null)";
    }

    public String formatNode(QuadtreeNode node) {
        // 可以自定义显示内容，比如只显示坐标
        return node.getP() != null ? "["  + formatPoint(node) + ", " + formatKeyPair(node) + "]" : "(null)";
    }


    public Point[] getPoints(int numPoints) {
        double centerX = 0;
        double centerY = 0;
        double radius = 900;

        Random random = new Random();
        Point[] points = new Point[numPoints];

        for (int i = 0; i < numPoints; i++) {
            double r = radius * Math.sqrt(random.nextDouble()); // 开方保证均匀分布
            double theta = 2 * Math.PI * random.nextDouble();   // 角度从 0 到 2π

            double x = centerX + r * Math.cos(theta);
            double y = centerY + r * Math.sin(theta);

            points[i] = new Point((int) x, (int) y);
        }

        return points;
    }


    public void test(){
        Element g = IPFEUtil.getBase();
        Element[] x = HashUtil.hashStr2ZrArr(Zr, "10", l);
        Element[] y = HashUtil.hashStr2ZrArr(Zr, "10", l);


        // 假设你知道 x 和 y（都是 Element[]）
        Element ip = innerProduct(x, y);

        System.out.println("ip = " + ip);

        Element g_ip = g.powZn(ip); // g^<x, y>

        QuadtreeNode node = IPFEUtil.keygen(Zr, x, null, "10");
        Element r = Zr.newRandomElement().getImmutable();
        Ciphertext ciphertext = IPFEUtil.encrypt(y, r);

        // 再用 C, D, Ei 算 Ex
        Element Ex = IPFEUtil.decrypt(G1, node, ciphertext);

        // 然后看看两者是否一致
        System.out.println("Ex = " + Ex);
        System.out.println("g^<x,y> = " + g_ip);
        System.out.println("是否相等 = " + Ex.isEqual(g_ip));
    }
}
