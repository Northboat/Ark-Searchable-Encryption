package cia.northboat.se;

import cia.northboat.util.HashUtil;
import it.unisa.dia.gas.jpbc.Element;

import java.util.List;

public interface SearchableEncryption {

    void setup();
    void keygen();

    default void trap(String w) {
        throw new UnsupportedOperationException("trap(String w) is not supported");
    }
    default void trap(List<String> W) {
        throw new UnsupportedOperationException("trap(List<String> W) is not supported");
    }

    default void enc(String w) {
        throw new UnsupportedOperationException("enc(String w) is not supported");
    }
    default void enc(List<String> W) {
        throw new UnsupportedOperationException("enc(List<String> W) is not supported");
    }

    boolean search();

    default void updateKey() {
        throw new UnsupportedOperationException("updateKey() is not supported");
    }
    default void updateEnc() {
        throw new UnsupportedOperationException("updateEnc() is not supported");
    }

    default void constTrap(String w) {
        throw new UnsupportedOperationException("constTrap(List<String> W) is not supported");
    }

    default boolean updateSearch() {
        throw new UnsupportedOperationException("updateSearch() is not supported");
    }

}
