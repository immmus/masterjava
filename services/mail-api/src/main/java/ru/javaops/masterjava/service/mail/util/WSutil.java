package ru.javaops.masterjava.service.mail.util;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import ru.javaops.masterjava.service.mail.Addressee;

import java.util.Set;

public class WSutil {

    public static Set<Addressee> split(String addressees) {
        Iterable<String> split = Splitter.on(',').trimResults().omitEmptyStrings().split(addressees);
        return ImmutableSet.copyOf(Iterables.transform(split, Addressee::new));
    }
}
