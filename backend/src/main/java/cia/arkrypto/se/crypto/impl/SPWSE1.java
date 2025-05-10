package cia.arkrypto.se.crypto.impl;

import cia.arkrypto.se.crypto.CipherSystem;
import cia.arkrypto.se.util.HashUtil;
import it.unisa.dia.gas.jpbc.*;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;
import it.unisa.dia.gas.plaf.jpbc.pairing.a.TypeACurveGenerator;

import java.security.SecureRandom;


public class SPWSE1 extends CipherSystem {


    Field G2;
    Element g1, g2, h, v, r, m;
    public SPWSE1(Field G1, Field GT, Field Zr, Pairing bp, int n, Field G2){
        super(G1, GT, Zr, bp, n);
        this.G2 = G2;
    }
    public Element[] H, S, T, R;


    // 初始化主公钥、主私钥
    @Override
    public void setup(){ // 一些参与计算的随机数
        g1 = randomG();
        g2 = G2.newRandomElement().getImmutable();
        v = this.getZr().newRandomElement().getImmutable();
        r = this.getZr().newRandomElement().getImmutable();
        m = this.getZr().newRandomElement().getImmutable();
    }

    @Override
    public void keygen(){
        h = g1.powZn(v).getImmutable();

        S = new Element[2*this.getN()];
        T = new Element[2*this.getN()];
        H = new Element[2*this.getN()];

        for(int i = 0; i < 2*this.getN(); i++){
            S[i] = this.getZr().newRandomElement().getImmutable();
            T[i] = this.getZr().newRandomElement().getImmutable();
            H[i] = g1.powZn(S[i]).mul(h.powZn(T[i])).getImmutable();
        }

        R = new Element[this.getN()];
        for(int i = 0; i < this.getN(); i++){
            // 取随机数填充 R
            R[i] = this.getZr().newRandomElement().getImmutable();
        }
    }


    // 加密后的密文
    public Element C1, C2;
    public Element[] E;
    @Override
    public void enc(String word){

//        System.out.println("加密关键词 " + word + "\n=====================");

        // 将字符串各个字符映射到整数群 W，长度为 n
        Element[] W = HashUtil.hashStr2ZrArr(this.getZr(), word, this.getN());

//        System.out.print("关键词的 ASCII 码映射 W: ( ");
//        for(Element e: W){
//            System.out.print(e + " ");
//        }
//        System.out.println(")");



        Element[] X = new Element[2*this.getN()];
        // 通过 W 构造向量 X
        for(int i = 0; i < this.getN(); i++){
            // 实际上文档里给的是 x[2i-1] = r*ri*wi，但我从0开始存，所以偶数用这个
            // 即用 x[0] 表示 x1
            X[2*i] = r.mul(R[i]).mul(W[i]).getImmutable();
            // 而奇数用 -r*ri
            X[2*i+1] = r.negate().mul(R[i]).getImmutable();
        }

//        System.out.print("关键词的加密的中间态 X: ( ");
//        for(Element e: X){
//            System.out.print(e + " ");
//        }
//        System.out.println(")");



        // 计算关键词 word 的密文
        C1 = g1.powZn(r).getImmutable();
        C2 = h.powZn(r).getImmutable();
        // 将向量 X 扩展为二维的密文 E
        E = new Element[2*this.getN()];
        for(int i = 0; i < 2*this.getN(); i++){
            E[i] = g1.powZn(X[i]).mul(H[i].powZn(r)).getImmutable();
        }

//        System.out.println("关键词密文 C1: " + C1 + "\n关键词密文 C2: " + C2);
//        System.out.println("关键词密文 E: ");
//        for(Element e: E){
//            System.out.println(e);
//        }
//        System.out.println("=====================\n");

    }


    // 计算要查找的关键词的陷门（加密信息）
    public Element T1, T2;
    public Element[] K;
    @Override
    public void trap(String word){
//        System.out.println("计算陷门 " + word + "\n=====================");

        // 将关键词字符串映射为整数群向量，这里的处理和上面一样，用 0 填充多余的位置
        Element[] W = HashUtil.hashStr2ZrArr(this.getZr(), word, this.getN());

//        System.out.print("陷门的 ASCII 码映射 W: ( ");
//        for(Element e: W){
//            System.out.print(e + " ");
//        }
//        System.out.println(")");

        // 通过 W 构造关键词对应的向量 Y
        Element[] Y = new Element[2*this.getN()];
        for(int i = 0; i < this.getN(); i++){
            if(i < word.length() && word.charAt(i) != '*'){
                Y[2*i] = this.getZr().newOneElement().getImmutable();
                Y[2*i+1] = W[i];
            } else {
                Y[2*i] = this.getZr().newZeroElement().getImmutable();
                Y[2*i+1] = this.getZr().newZeroElement().getImmutable();
            }
        }

//        System.out.print("陷门加密的中间态 Y: ( ");
//        for(Element e: Y){
//            System.out.print(e + " ");
//        }
//        System.out.println(")");


        // 将 Y 扩展为二维矩阵 K
        Element s1 = this.getZr().newZeroElement(), s2 = this.getZr().newZeroElement();
        K = new Element[2*this.getN()];
        for(int i = 0; i < 2*this.getN(); i++){
            s1.add(S[i].mul(Y[i]));
            s2.add(T[i].mul(Y[i]));
//            System.out.println(S[i].mul(Y[i]));
//            System.out.println("s1: " + s1);
//            System.out.println("s2: " + s2 + "\n");
            K[i] = g2.powZn(m.mul(Y[i])).getImmutable();
        }
        T1 = g2.powZn(m.mul(s1)).getImmutable();
        T2 = g2.powZn(m.mul(s2)).getImmutable();


//        System.out.println("陷门 T1: " + T1 + "\n陷门 T2: " + T2 + "\n陷门 K:");
//        for(Element e: K){
//            System.out.println(e);
//        }
//        System.out.println("=====================\n");
    }


    @Override
    public boolean search(){
//        System.out.println("开始匹配\n=====================");
        Element acc = this.getGT().newOneElement();
        for(int i = 0; i < 2*this.getN(); i++){
            acc.mul(this.getBp().pairing(E[i], K[i]));
        }
//        Element m = acc.getImmutable();
        Element d = this.getBp().pairing(C1, T1).mul(this.getBp().pairing(C2, T2)).getImmutable();

        Element ans = acc.div(d).getImmutable();

        System.out.println("left: " + ans);
        System.out.println("right: " + this.getGT().newOneElement());
//        System.out.println("=====================\n\n");

        return ans.isEqual(this.getGT().newOneElement());
    }


    public static void main(String[] args) {
        genParams();
    }

    public static void genParams(){
        // 1. 创建固定种子的 SecureRandom 实例
        SecureRandom fixedRandom = new SecureRandom();
        fixedRandom.setSeed(12345L);  // 固定随机种子
        // 初始化 type a 类型曲线
        PairingParametersGenerator pg = new TypeACurveGenerator(160, 512);
        // 生成参数
        PairingParameters params = pg.generate();
        // 打印参数
        System.out.println(params.toString());
    }

    // 简单的双线性验证
    public static void testPairing(){

        Pairing bp = PairingFactory.getPairing("a8.properties");

        // 二、选择群上的元素
        Field G1 = bp.getG1();
        Field G2 = bp.getG2();
        Field Zr = bp.getZr();
        Element u = G1.newRandomElement().getImmutable();
        Element v = G2.newRandomElement().getImmutable();
        Element a = Zr.newRandomElement().getImmutable();
        Element b = Zr.newRandomElement().getImmutable();
        System.out.println("G1 上元素 u: " + u);
        System.out.println("Zr 上元素 a: " + a);


        // 三、计算等式左半部分
        Element ua = u.powZn(a);
        Element vb = v.powZn(b);
        Element left = bp.pairing(ua,vb);

        // 四、计算等式右半部分
        Element euv = bp.pairing(u,v).getImmutable();
        Element ab = a.mul(b);
        Element right = euv.powZn(ab);

        if (left.isEqual(right)) {
            System.out.println("Yes");
        } else {
            System.out.println("No");
        }

    }
}