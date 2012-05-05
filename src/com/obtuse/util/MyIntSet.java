package com.obtuse.util;

import java.io.Serializable;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * A compact and fast set of integers.
 * Note that the implementation assumes that the integers are relatively tightly clustered together.
 * Sets with huge gaps in the member values will result in considerably more memory being consumed than one might like.
 * <p/>
 * Copyright © 2009 Obtuse Systems Corporation
 */

public class MyIntSet implements Iterable<Long>, Serializable {

    private long[] _valueBits = null;
    private long   _startValue = 0L;

    public MyIntSet() {

        super();

    }

    public void add( long value ) {

        if ( _valueBits == null ) {

            // Take a wild guess that the first value is in the middle of a modest range of bits.

            //noinspection MagicNumber
            _valueBits = new long[25];
            //noinspection MagicNumber
            _startValue = value - 12 * Long.SIZE;

        }
//        Logger.logMsg( "before adding " + value + ":  " + this );

//        if ( value >= _startValue && value < _startValue + _valueBits.length * Long.SIZE ) {
//
//
//        } else
        if ( value < _startValue ) {

            long newStartValue = _startValue - ( ( _startValue - value ) / Long.SIZE + 10 ) * Long.SIZE;
            long lastValue = _startValue + Long.SIZE * _valueBits.length - 1;
            long[] newArray = new long[( (int)( lastValue + 1 - newStartValue ) / Long.SIZE )];
            System.arraycopy(
                    _valueBits,
                    0,
                    newArray,
                    (int)( _startValue - newStartValue ) / Long.SIZE,
                    _valueBits.length
            );

            _valueBits = newArray;
            _startValue = newStartValue;
//            Logger.logMsg( "after growing left:  " + this );

        } else if ( value >= _startValue + _valueBits.length * Long.SIZE ) {

            long lastValue = _startValue + Long.SIZE * _valueBits.length - 1;
            long newLastValue = lastValue + ( ( value - lastValue ) / Long.SIZE + 10 ) * Long.SIZE;
            long[] newArray = new long[( (int)( newLastValue + 1 - _startValue ) / Long.SIZE )];
            System.arraycopy( _valueBits, 0, newArray, 0, _valueBits.length );

            _valueBits = newArray;
//            Logger.logMsg( "after growing right:  " + this );

        }

        //noinspection UnnecessaryParentheses
        _valueBits[( (int)( value - _startValue ) / Long.SIZE )] |= 1L << ( ( value - _startValue ) & ( Long.SIZE - 1 ) );

//        Logger.logMsg( "after adding " + value + ":  " + this );

    }

    public Iterator<Long> iterator() {

        for ( int i = 0; i < _valueBits.length; i += 1 ) {

            for ( int j = 0; j < Long.SIZE; j += 1 ) {

                //noinspection UnnecessaryParentheses
                if ( ( _valueBits[i] & ( 1L << j ) ) != 0 ) {

                    final int nextI = i;
                    final int nextJ = j;
                    return new Iterator<Long>() {

                        private int _nextI = nextI;
                        private int _nextJ = nextJ;
                        private boolean _done = false;

                        public boolean hasNext() {

                            return !_done;

                        }

                        public Long next() {

                            if ( _done ) {

                                throw new NoSuchElementException( "no more elements" );

                            } else {

                                long rval = _startValue + _nextI * Long.SIZE + _nextJ;
                                if ( _nextJ == Long.SIZE ) {
                                    _nextI += 1;
                                    _nextJ = 0;
                                } else {
                                    _nextJ += 1;
                                }

                                for ( int i = _nextI; i < _valueBits.length; i += 1 ) {

                                    for ( int j = _nextJ; j < Long.SIZE; j += 1 ) {

                                        //noinspection UnnecessaryParentheses
                                        if ( ( _valueBits[i] & ( 1L << j ) ) != 0 ) {

                                            _nextI = i;
                                            _nextJ = j;
                                            return rval;

                                        }

                                    }

                                    _nextJ = 0;

                                }

                                _done = true;
                                return rval;

                            }
                        }

                        public void remove() {

                            throw new UnsupportedOperationException( "remove not supported" );

                        }

                    };

                }

            }
        }

        return new Iterator<Long>() {

            public boolean hasNext() {

                return false;

            }

            public Long next() {

                throw new NoSuchElementException( "set is empty" );

            }

            public void remove() {

                throw new UnsupportedOperationException( "remove not supported" );

            }


        };

    }

//    public String toString() {
//
//        StringBuilder rval = new StringBuilder();
//        String comma = "";
//        for ( int i = 0; i < _valueBits.length; i += 1 ) {
//
//            for ( int j = 0; j < Long.SIZE; j += 1 ) {
//
//                if ( ( _valueBits[i] & ( 1L << j ) ) != 0 ) {
//
//                    rval.append( comma ).append( _startValue + i * Long.SIZE + j );
//                    comma = ", ";
//
//                }
//
//            }
//
//        }
//
//        return "MyIntSet( " + rval + " )";
//
//    }

    public String toString() {

        StringBuilder rval2 = new StringBuilder();
        String comma = "";
        //noinspection ForLoopReplaceableByForEach,ForLoopWithMissingComponent
        for ( Iterator<Long> iter = iterator(); iter.hasNext(); ) {

            rval2.append( comma ).append( iter.next() );
            comma = ", ";

        }

        return "MyIntSet( " + rval2.toString() + " )";

    }

//    public void checkToString() {
//
//        String s1 = toString();
//        String s2 = toString2();
//        if ( !s1.equals( s2 ) ) {
//
//            Logger.logMsg( "iterator is broken (old = \"" + s1 + "\", new = \"" + s2 + "\")" );
//
//        }
//
//    }

    @SuppressWarnings("MagicNumber")
    public static void main( String[] args ) {

        MyIntSet set = new MyIntSet();

        for ( int i = 0; i < 1000; i += 25 ) {

            set.add( i );

        }

        for ( int i = 0; i > -1000; i -= 25 ) {

            set.add( i );

        }

    }

}
