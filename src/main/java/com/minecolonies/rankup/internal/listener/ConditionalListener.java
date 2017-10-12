package com.minecolonies.luckrankup2.internal.listener;

import com.minecolonies.luckrankup2.Luckrankup2;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.function.Predicate;

/**
 * Indicates that the listener may need turning off.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ConditionalListener
{

    /**
     * The {@link Predicate} that contains the {@link Luckrankup2} plugin. <strong>Must have a NO-ARGS constructor.</strong>
     *
     * @return The class with the predicate in.
     */
    Class<? extends Predicate<Luckrankup2>> value();
}
