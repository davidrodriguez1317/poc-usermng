package com.demo.usermng.utils;

import java.util.Optional;

public class StringHelper {

    public static Optional<Integer> getIndexOfStringInArrayIgnoringCase(String[] elements, String element) {
        for (int i = 0; i < elements.length; i++) {
            if (elements[i].equalsIgnoreCase(element)) {
                return Optional.of(i);
            }
        }
        return Optional.empty();
    }
}
