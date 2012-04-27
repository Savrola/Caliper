package com.obtuse.ui;

import javax.swing.undo.*;

/*
 * Copyright © 2012 Daniel Boulet
 */

/**
 * Describe something which can be undone and/or redone.
 */

public abstract class UndoableBenoitEdit implements UndoableEdit {

    private boolean _alive = true;
    private boolean _hasBeenDone = false;
    private final String _presentationName;
//    private final Runnable _undo;
//    private final Runnable _redo;

    public UndoableBenoitEdit(
            String presentationName //,
//            Runnable undo,
//            Runnable redo
    ) {
        super();

        _presentationName = presentationName;
//        _undo = undo;
//        _redo = redo;
//
//        if ( _undo == null ) {
//
//            throw new IllegalArgumentException( "undo is null (maybe you should just provide a Runnable that doesn't do anything)" );
//
//        }
//
//        if ( _redo == null ) {
//
//            throw new IllegalArg
//        }

    }

    public final void undo() {

        if ( !canUndo() ) {

            throw new CannotUndoException();

        }

        doUndo();

        _hasBeenDone = false;

    }

    public abstract void doUndo();


    public boolean canUndo() {

        return _alive && _hasBeenDone;

    }

    public final void redo() {

        if ( !canRedo() ) {

            throw new CannotRedoException();

        }

        doRedo();

        _hasBeenDone = true;

    }

    public abstract void doRedo();

    public boolean canRedo() {

        return _alive && !_hasBeenDone;

    }

    public void die() {

        _alive = false;

    }

    public boolean addEdit( UndoableEdit undoableEdit ) {

        return false;

    }

    public boolean replaceEdit( UndoableEdit undoableEdit ) {

        return false;

    }

    public boolean isSignificant() {

        return true;

    }

    public String getPresentationName() {

        return _presentationName;

    }

    public String getUndoPresentationName() {

        return "undo " + _presentationName;

    }

    public String getRedoPresentationName() {

        return "redo " + _presentationName;

    }

    public String toString() {

        return "UndoableBenoitEdit( \"" + getPresentationName() + "\" )";
    }

}
