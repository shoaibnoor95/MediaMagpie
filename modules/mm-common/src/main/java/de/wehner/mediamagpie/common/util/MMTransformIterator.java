package de.wehner.mediamagpie.common.util;

import java.util.Iterator;

import org.apache.commons.collections.Transformer;

/**
 * An iterator wrapper that uses a transformer when accessing the next element using method {@linkplain #next()}. This implementation is
 * based on apache's {@linkplain Transformer} implementation but supports Generics.
 * 
 * @author ralf wehner
 * 
 * @param <I>
 *            the input type
 * @param <O>
 *            the output (transformed) type
 */
public class MMTransformIterator<I, O> implements Iterator<O> {

    /** The iterator being used */
    private final Iterator<I> iterator;

    /** The transformer being used */
    private final MMTransformer<I, O> transformer;

    /**
     * Constructs a new <code>TransformIterator</code> that will use the given iterator and transformer.
     * 
     * @param iterator
     *            the iterator to use
     * @param transformer
     *            the transformer to use
     */
    public MMTransformIterator(Iterator<I> iterator, MMTransformer<I, O> transformer) {
        super();
        this.iterator = iterator;
        this.transformer = transformer;
    }

    // -----------------------------------------------------------------------
    public boolean hasNext() {
        return iterator.hasNext();
    }

    /**
     * Gets the next object from the iteration, transforming it using the current transformer. If the transformer is null, no transformation
     * occurs and the object from the iterator is returned directly.
     * 
     * @return the next object
     * @throws java.util.NoSuchElementException
     *             if there are no more elements
     */
    public O next() {
        return transform(iterator.next());
    }

    public void remove() {
        iterator.remove();
    }

    /**
     * Transforms the given object using the transformer.
     * 
     * @param source
     *            the object to transform
     * @return the transformed object
     */
    protected O transform(I source) {
        // FIXME rwe: Hier müsste noch differenziert werden ob es sich um das data oder das metainfo objekt handel...
        return transformer.transform(source);
    }
}
