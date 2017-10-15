package com.minecolonies.rankup.internal.listener;

import com.minecolonies.rankup.Rankup;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.function.Predicate;

/**
 * Indicates that the listeners may need turning off.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ConditionalListener
{

    /**
     * The {@link Predicate} that contains the {@link Rankup} plugin. <strong>Must have a NO-ARGS constructor.</strong>
     *
     * @return The class with the predicate in.
     */
    Class<? extends Predicate<Rankup>> value();
}
