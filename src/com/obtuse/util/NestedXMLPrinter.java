package com.obtuse.util;

import java.io.Closeable;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Collection;
import java.util.Stack;

/*
 * Copyright © 2012 Daniel Boulet
 */

@SuppressWarnings("UnusedDeclaration")
public class NestedXMLPrinter implements Closeable {

    private int _nestingLevel = 0;
    private String _currentIndentString = "";
    private boolean _needIndent = true;

    private Stack<String> _tagStack = new Stack<String>();

    private final int _indentPerLevel;
    private final PrintStream _ps;
    private final String _perIndentString;

    public NestedXMLPrinter( int indentPerLevel, PrintStream ps ) {
        super();

        _indentPerLevel = indentPerLevel;
        _perIndentString = ObtuseUtil.replicate( " ", indentPerLevel );
        _ps = ps;

    }

    public void flush() {

        _ps.flush();

    }

    public void emitOpenTag( String tagName ) {

        println( "<" + tagName + ">" );
        nest( tagName );

    }

    public void emitOpenTag( String tagName, String[] attributes ) {

        emitTag( tagName, attributes, true );

    }

    public void emitOpenTag( Class<?> tagClass, String[] attributes ) {

        emitTag( tagClass.getSimpleName(), attributes, true );

    }

    public void emitOpenTag( Class<?> tagClass ) {

        emitOpenTag( tagClass.getSimpleName() );

    }

    public void emitTag( String tagName, String[] attributes, boolean leaveOpen ) {

        print( "<" + tagName );

        if ( attributes != null ) {

            for ( String attribute : attributes ) {

                if ( attribute != null ) {

                    print( " " + attribute );

                }

            }

        }

        if ( leaveOpen ) {

            println( ">" );
            nest( tagName );

        } else {

            println( "/>" );

        }

    }

    public void emitTag( String tagName, String[] attributes ) {

        emitTag( tagName, attributes, false );

    }

    public void emitTag( String tagName, Collection<String> attributes, boolean leaveOpen ) {

        emitTag( tagName, attributes == null ? null : attributes.toArray( new String[attributes.size()] ), leaveOpen );

    }

    public void emitTag( String tagName, Collection<String> attributes ) {

        emitTag( tagName, attributes, false );

    }

    public void emitTag( String tagName, String content ) {

        println( "<" + tagName + ">" + content + "</" + tagName + ">" );

    }

    public void emitTag( String tagName ) {

        emitTag( tagName, (String[])null );

    }

    public void emitTag( Class<?> tagClass ) {

        emitTag( tagClass.getSimpleName() );

    }

    public void emitCloseTag( String tagName ) {

        unNest( tagName );
        println( "</" + tagName + ">" );

    }

    public void emitCloseTag( Class<?> tagClass ) {

        emitCloseTag( tagClass.getSimpleName() );

    }

    public void emitArray( String arrayName, double[] values, int precision ) {

        emitOpenTag( arrayName );
        for ( double v : values ) {

            emitTag( "item", ObtuseUtil.lpad( v, 0, precision ) );

        }
        emitCloseTag( arrayName );

    }

    public void emitArray( String arrayName, double[] values ) {

        emitOpenTag( arrayName );
        for ( double v : values ) {

            emitTag( "item", "" + v );

        }
        emitCloseTag( arrayName );

    }

    public void emitArray( String arrayName, long[] values ) {

        emitOpenTag( arrayName );
        for ( long v : values ) {

            emitTag( "item", "" + v );

        }
        emitCloseTag( arrayName );

    }

    public void emitArray( String arrayName, int[] values ) {

        emitOpenTag( arrayName );
        for ( int v : values ) {

            emitTag( "item", "" + v );

        }
        emitCloseTag( arrayName );

    }

    public void emitArray( String arrayName, boolean[] values ) {

        emitOpenTag( arrayName );
        for ( boolean v : values ) {

            emitTag( "item", v ? "T" : "F" );

        }
        emitCloseTag( arrayName );

    }

    public void emitArray( String arrayName, Object[] values ) {

        emitOpenTag( arrayName );
        for ( Object v : values ) {

            emitTag( "item", v.toString() );

        }
        emitCloseTag( arrayName );

    }

    public void nest( String tag ) {

        _nestingLevel += 1;
        _tagStack.push( tag );
        _currentIndentString = ObtuseUtil.replicate( _perIndentString, _nestingLevel );

    }

    public void nest() {

        nest( "" );

    }

    public void unNest( String tagName ) {

        String expectedTag = _tagStack.pop();
        if ( expectedTag.equals( tagName ) ) {

            _nestingLevel -= 1;
            _currentIndentString = ObtuseUtil.replicate( "   ", _nestingLevel );

        } else {

            throw new IllegalArgumentException( "attempt to un-nest tag \"" + tagName + "\" when next tag should be \"" + expectedTag + "\"" );

        }

    }

    public void unNest() {

        unNest( "" );

    }

    public void print( String text ) {

        doIndent();
        _ps.print( text );

    }

    public void println( String text ) {

        if ( !text.isEmpty() ) {

            doIndent();

        }

        _ps.println( text );
        _needIndent = true;

    }

    public void doIndent() {

        if ( _needIndent ) {

            _ps.print( _currentIndentString );
            _needIndent = false;

        }

    }

    public int getIndentPerLevel() {

        return _indentPerLevel;

    }

    public void close()
            throws IOException {

        _ps.close();

    }

}
