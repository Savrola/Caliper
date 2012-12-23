package com.obtuse.util;

/*
 * Copyright © 2012 Daniel Boulet
 */

/**
 * An entity with an id number which is unique within some id-space.
 * >p/>
 * Instance's of this class are immutable.
 * Obviously, instances of derivations of this class might not be immutable!
 * <p/>
 * This class implements a {@link #hashCode} method which satisfies the hashCode contract as specified by
 * the {@link Object#hashCode} method.
 * This class also implements a {@link #equals} method which satisfies the equals contract as specified by
 * the {@link Object#equals} method.
 * Of course, derivations of this class could implement hashCode and/or equals methods in ways which violate these contracts.
 */

public class UniqueEntity {

    private final long _id;

    private final int _hashCode;

    private static final UniqueIdGenerator _defaultIdGenerator = new SimpleUniqueIdGenerator();

    /**
     * A utility class which implements a very simple {@link UniqueIdGenerator}.
     * <p/>
     * Each instance of this class returns values from {@link #getUniqueId}
     * which are the next value in the sequence 0, 1, 2, 3 ...
     */

    public static class SimpleUniqueIdGenerator implements UniqueIdGenerator {

        private long _nextId = 0;

        /**
         * Get the next value in the sequence 0, 1, 2, 3 ... from the perspective of this instance.
         * <p/>
         * For example, the following code sequence:
         * <pre>
         * SimpleUniqueIdGenerator outerGenerator = new SimpleUniqueIdGenerator();
         * for ( int i = 0; i < 10; i += 1 ) {
         *
         *     System.out.print( outerGenerator.getUniqueId() + ": " );
         *
         *     SimpleUniqueIdGenerator innerGenerator = new SimpleUniqueIdGenerator();
         *     for ( int j = 0; j < 5; j += 1 ) {
         *
         *         System.out.print( " " + innerGenerator.getUniqueId() );
         *
         *     }
         *
         *     System.out.println();
         *
         * }
         * </pre>
         * produces the following output:
         * <pre>
         *
         * 0:  0 1 2 3 4
         * 1:  0 1 2 3 4
         * 2:  0 1 2 3 4
         * 3:  0 1 2 3 4
         * 4:  0 1 2 3 4
         * 5:  0 1 2 3 4
         * 6:  0 1 2 3 4
         * 7:  0 1 2 3 4
         * 8:  0 1 2 3 4
         * 9:  0 1 2 3 4
         * </pre>
         * @return the next value in the sequence 0, 1, 2, 3 ... from the perspective of this instance.
         */

        public synchronized long getUniqueId() {

            long uid = _nextId;
            _nextId += 1;

            return uid;

        }

        @SuppressWarnings("UseOfSystemOutOrSystemErr")
        public static void main( String[] args ) {

            SimpleUniqueIdGenerator outerGenerator = new SimpleUniqueIdGenerator();
            for ( int ii = 0; ii < 10; ii += 1 ) {

                System.out.print( outerGenerator.getUniqueId() + ": " );

                SimpleUniqueIdGenerator innerGenerator = new SimpleUniqueIdGenerator();
                for ( int i = 0; i < 5; i += 1 ) {

                    System.out.print( " " + innerGenerator.getUniqueId() );

                }

                System.out.println();

            }

        }

    }

    /**
     * Create an instance using the default {@link UniqueIdGenerator}.
     */

    public UniqueEntity() {
        this( UniqueEntity.getDefaultIdGenerator() );
    }

    /**
     * Create an instance using the specified {@link UniqueIdGenerator}.
     * @param uniqueIdGenerator the unique id generator to be used to compute this newly created instance's id value.
     */

    public UniqueEntity( UniqueIdGenerator uniqueIdGenerator ) {
        super();

        _id = uniqueIdGenerator.getUniqueId();
        _hashCode = new Long( _id ).hashCode();

    }

    /**
     * Get this instance's id value.
     * @return this instance's id value.
     */

    public long getId() {

        return _id;

    }

    /**
     * Get a 'default' {@link UniqueIdGenerator} suitable for use when invoking {@link #UniqueEntity(UniqueIdGenerator)}.
     * <p/>
     * This method returns a JVM-unique instance of {@link SimpleUniqueIdGenerator}.
     * @return a JVM-unique instance of {@link SimpleUniqueIdGenerator}.
     */

    public static UniqueIdGenerator getDefaultIdGenerator() {

        return UniqueEntity._defaultIdGenerator;

    }

    /**
     * Returns a hash code for the object.
     * <p/>
     * This method returns precisely what <tt>new Long( this.getId() ).hashCode()</tt> would return.
     * This method satisfies the hashCode contract specified by the {@link Object#hashCode} method.
     * <p/>
     * Note that the hash code value for each newly created instance is pre-computed when the instance is created.
     * This costs a few bytes of space per instance but makes calls to this method very fast.
     * @return a hash code for this instance.
     */

    public int hashCode() {

        return _hashCode;

    }

    /**
     * Indicates whether some other object is "equal to" this one.
     * <p/>
     * This implementation defines "equal to" to require that the other object be an instance of this class
     * and that both this instance and the other object return the same value when their {@link #getId} method
     * is invoked.  This method satisfies the equality contract specified by the {@link Object#equals} method.
     * @param rhs the other object.
     * @return true if the other object is "equal to" this one.
     */

    public boolean equals( Object rhs ) {

        return rhs instanceof UniqueEntity && ((UniqueEntity)rhs).getId() == getId();

    }

}
