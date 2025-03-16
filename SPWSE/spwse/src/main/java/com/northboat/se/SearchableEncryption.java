package com.northboat.se;

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
}
