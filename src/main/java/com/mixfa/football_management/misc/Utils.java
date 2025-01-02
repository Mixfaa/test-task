package com.mixfa.football_management.misc;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class Utils {
    private Utils() {
    }

    public static Iterator<Throwable> causesToIterator(Exception ex) {
        return new Iterator<>() {
            private Throwable current = ex;

            @Override
            public boolean hasNext() {
                return current != null;
            }

            @Override
            public Throwable next() {
                if (current == null)
                    throw new NoSuchElementException();

                Throwable result = current;
                current = current.getCause();
                return result;
            }
        };
    }

    public static Iterable<Throwable> iterateCauses(Exception ex) {
        return () -> causesToIterator(ex);
    }
}
