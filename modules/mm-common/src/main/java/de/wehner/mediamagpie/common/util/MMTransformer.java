package de.wehner.mediamagpie.common.util;

import org.apache.commons.collections.FunctorException;

/**
 * Defines a functor interface implemented by classes that transform one object into another.
 * <p>
 * This code was taken from apache's commons-collection and enhanced with generics.
 * </p>
 * 
 * @see {@linkplain org.apache.commons.collections.Transformer}
 */
public interface MMTransformer<I, O> {

    /**
     * Transforms the input object (leaving it unchanged) into some output object.
     * 
     * @param input
     *            the object to be transformed, should be left unchanged
     * @return a transformed object
     * @throws ClassCastException
     *             (runtime) if the input is the wrong class
     * @throws IllegalArgumentException
     *             (runtime) if the input is invalid
     * @throws FunctorException
     *             (runtime) if the transform cannot be completed
     */
    public O transform(I input);

}
