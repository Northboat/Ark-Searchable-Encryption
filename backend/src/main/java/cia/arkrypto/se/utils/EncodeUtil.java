package cia.arkrypto.se.utils;

import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Field;

import java.math.BigInteger;
import java.util.Base64;

public class EncodeUtil {


    public static String parseBigInteger2HexStr(BigInteger bi){
        return bi.toString(16);
    }

    public static BigInteger parseHexStr2BigInteger(String str){
        return new BigInteger(str, 16);
    }


    public static String parseElement2Base64Str(Element element){
        byte[] bytes = element.toBytes(); // 转为 byte[]
        return Base64.getEncoder().encodeToString(bytes);
    }

    public static Element parseBase64Str2Element(String base64, Field field){
        byte[] bytes = Base64.getDecoder().decode(base64);
        return field.newElementFromBytes(bytes).getImmutable();
    }
}
