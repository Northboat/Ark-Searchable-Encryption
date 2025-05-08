package cia.arkrypto.se.crypto;

import java.util.Arrays;
import java.util.List;

public interface SearchableEncryption {
    void setup();

    void keygen();

    default void enc(String w) {
        throw new UnsupportedOperationException("enc(String w) is not supported");
    }
    default void enc(List<String> W) {
        throw new UnsupportedOperationException("enc(List<String> W) is not supported");
    }

    default void trap(String q) {
        throw new UnsupportedOperationException("trap(String q) is not supported");
    }
    default void trap(List<String> Q) {
        throw new UnsupportedOperationException("trap(List<String> Q) is not supported");
    }

    boolean search();



    default void updateKey() {
        throw new UnsupportedOperationException("updateKey() is not supported");
    }
    default void reEnc() {
        throw new UnsupportedOperationException("updateEnc() is not supported");
    }

    default void constTrap(String q) {
        trap(q);
    }
    default void constTrap(List<String> Q) {
        trap(Q);
    }

    default boolean updateSearch() {
        return search();
    }

    default List<Long> test(List<String> words, int round){
        long t1 = 0, t2 = 0, t3 = 0;
        for(int i = 0; i < round; i++){
            setup();
            keygen();

            long s1 = System.currentTimeMillis();
            try{
                enc(words);
            }catch (UnsupportedOperationException e){
                for(String word: words){
                    enc(word);
                }
            }
            long e1 = System.currentTimeMillis();
            t1 += e1-s1;


            long s2 = System.currentTimeMillis();
            try{
                trap(words);
            }catch (UnsupportedOperationException e){
                for(String word: words){
                    trap(word);
                }
            }
            long e2 = System.currentTimeMillis();
            t2 += e2-s2;

            long s3 = System.currentTimeMillis();
            for(int j = 0; j < words.size(); j++)
                search();
            long e3 = System.currentTimeMillis();
            t3 += e3-s3;
        }
        return Arrays.asList(t1, t2, t3);
    }

    default List<Long> test(List<String> words, int sender, int receiver, int round) {
        throw new UnsupportedOperationException("test(List<String> words, int sender, int receiver, int round) is not supported");
    }

}
