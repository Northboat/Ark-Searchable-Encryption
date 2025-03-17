package cia.northboat.se;

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
    default void updateEnc() {
        throw new UnsupportedOperationException("updateEnc() is not supported");
    }

    default void constTrap(String q) {
        throw new UnsupportedOperationException("constTrap(String q) is not supported");
    }
    default void constTrap(List<String> Q) {
        throw new UnsupportedOperationException("constTrap(List<String> Q) is not supported");
    }

    default boolean updateSearch() {
        throw new UnsupportedOperationException("updateSearch() is not supported");
    }

}
