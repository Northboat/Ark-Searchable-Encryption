package cia.arkrypto.se.service;

import cia.arkrypto.se.crypto.tree.EncryptedQuadtree;
import cia.arkrypto.se.model.bo.Point;
import cia.arkrypto.se.model.bo.QuadtreeNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class IPFETreeService {

    private final EncryptedQuadtree encryptedQuadtree;
    @Autowired
    public IPFETreeService(EncryptedQuadtree encryptedQuadtree){

        this.encryptedQuadtree = encryptedQuadtree;
    }


    public Map<String, Object> buildTree(int count, int type){
        Map<String, Object> data = new HashMap<>();

        long s = System.currentTimeMillis();
        encryptedQuadtree.clean();
        encryptedQuadtree.build(count);
        long e = System.currentTimeMillis();
        data.put("time_cost", e-s);

        String treeStr = encryptedQuadtree.getTreeStruct(type);
        String htmlTreeStr = treeStr.replace("\n", "<br>");
        htmlTreeStr = htmlTreeStr.replace(" ", "&nbsp;");
        data.put("tree", htmlTreeStr);

        System.out.println("Tree Height: " + encryptedQuadtree.getHeight());
        data.put("height", encryptedQuadtree.getHeight());

        return data;
    }



    public Map<String, Object> search(Point p){
        Map<String, Object> data = new HashMap<>();

        if(encryptedQuadtree.getHeight() == 1){
            data.put("Error", "Please build tree first");
            return data;
        }

        long s = System.currentTimeMillis();
        // 匹配
        QuadtreeNode target = encryptedQuadtree.search(p);
        long e = System.currentTimeMillis();

        data.put("time_cost", e-s);
        data.put("target_node", target == null ? "null" : target);

//        data.put("Error", "Search haven't finished yet. Nothing seems to match here. I suspect there's an issue with the search logic. I'll fix it later when I have time. Maybe the matching formula is also problematic");

        return data;
    }
}
