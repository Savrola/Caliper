package com.obtuse.ui;

import com.obtuse.util.ButtonInfo;
import com.obtuse.util.ButtonManager;
import com.obtuse.util.Logger;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/*
 * Copyright © 2012 Daniel Boulet
 */

/**
 * Provide a (relatively) easy to use way to have a scrollable JList with (individually optional)
 * associated add, delete, up, down and duplicate buttons.
 */

@SuppressWarnings("UnusedDeclaration")
public abstract class JListManager implements ButtonManager {

    public interface ListElementFactory {

        Object createListElement( int row );

        Object duplicateListElement( int row, Object original );

    }

    public abstract static class UndoManagedObjects extends UndoableBenoitEdit {

        private final Object[] _objects;

        protected UndoManagedObjects( String presentationName, Object[] objects ) {
            super( presentationName );

            _objects = new Object[objects.length];
            System.arraycopy( objects, 0, _objects, 0, objects.length );

        }

        protected UndoManagedObjects( String presentationName, Collection<Object> objects ) {
            super( presentationName );

            _objects = new Object[objects.size()];
            int i = 0;
            for ( Object object : objects ) {

                _objects[i] = object;
                i += 1;

            }

        }

        public Object[] getObjects() {

            return _objects;

        }

        public int getObjectCount() {

            return _objects.length;

        }

        public String toString() {

            return "UndoManagedObjects( " + Arrays.deepToString( _objects ) + ", " + super.toString() + " )";

        }

    }

    public interface UndoableListElementFactory {

        /**
         * Create a new list element and optionally create an {@link UndoableBenoitEdit} instance to manage side-effects.
         * <p/>This method is called by {@link JListManager#doAddButton} in preparation for performing
         * an undoable addition of a new list element.
         * Note that this method call must only create the new list element and optionally provide an
         * {@link UndoableBenoitEdit} instance that manages the side-effects of inserting and removing
         * the element from the list.  The caller of this method (i.e. {@link JListManager#doAddButton})
         * is responsible for actually inserting and removing the new list element from the {@link javax.swing.JList} as needed.
         * <p/>
         * Assuming that this method returns a {@link UndoableBenoitEdit}, the returned {@link UndoableBenoitEdit} instance's
         * {@link UndoableBenoitEdit#undo}
         * and
         * {@link UndoableBenoitEdit#redo}
         * methods will be called as follows:
         * <ul>
         *     <li>
         *         the returned {@link UndoableBenoitEdit} instance's {@link UndoableBenoitEdit#redo} method
         *         will be called almost immediately to deal with the side effects
         *         of adding the new list elements for the first time.
         *         This call will occur just before the newly created list element is inserted
         *         into the {@link javax.swing.JList}.
         *     </ul>
         *     <li>
         *         if an <i>undo</i> event occurs which requires the removal of the new list
         *         element from the {@link javax.swing.JList} then
         *         the returned {@link UndoableBenoitEdit} instance's {@link UndoableBenoitEdit#canUndo}
         *         method is called to verify that the <i>undo</i> is still possible.
         *         If this call returns false then a {@link javax.swing.undo.CannotUndoException} is thrown
         *         (the {@link javax.swing.JList} will still contain the new list element).
         *         <br/><br/>
         *         Otherwise, the returned {@link UndoableBenoitEdit} instance's
         *         {@link UndoableBenoitEdit#undo} method will be called immediately after
         *         the new list element has been removed from the {@link javax.swing.JList}.
         *         If this call to the returned {@link UndoableBenoitEdit} instance's
         *         {@link UndoableBenoitEdit#undo} method throws a {@link javax.swing.undo.CannotUndoException} then the new list element
         *         is inserted back into the {@link javax.swing.JList} before the exception is re-thrown.
         *         <br/><br/>
         *         VERY IMPORTANT:  the returned {@link UndoableBenoitEdit} instance's {@link UndoableBenoitEdit#undo} method
         *         *** MUST *** leave the new list element in a state where it can be re-inserted into
         *         the {@link javax.swing.JList} should a subsequent <i>redo</i> event occur.
         *     </li>
         *     <li>
         *         if a subsequent <i>redo</i> event occurs which requires the re-insertion of the
         *         new list element into the {@link javax.swing.JList} then the returned {@link UndoableBenoitEdit} instance's
         *         {@link UndoableBenoitEdit#redo} method will be called just before the
         *         new list element is re-inserted into the {@link javax.swing.JList}.
         *     </li>
         * </ul>
         * @param row where the newly created list element is about to be inserted into the {@link javax.swing.JList}.
         * @return a wrapper containing the new list element and optionally the {@link UndoableBenoitEdit}
         * instance responsible for managing the side-effects of inserting and removing the new list element
         * from the {@link javax.swing.JList}.
         */

        UndoManagedObjects prepareToAddListElement( int row );

        /**
         * Optionally create an {@link UndoableBenoitEdit} instance to manage the side-effects associated with undoing and
         * redoing the deletion of one or more list elements.
         * <p/>This method is called by {@link JListManager#doDeleteButton} in preparation for performing
         * an undoable deletion of one or more list elements.
         * Note that this method call is only responsible for (optionally) creating the {@link UndoableBenoitEdit}
         * instance to deal with the side-effects of deleting and re-inserting the list element(s) from the {@link javax.swing.JList}.
         * The caller of this method (i.e. {@link JListManager#doDeleteButton} is responsible for actually
         * deleting and re-inserting the list element from the {@link javax.swing.JList} as needed.
         * <p/>
         * Assuming that this method returns a {@link UndoableBenoitEdit}, the returned {@link UndoableBenoitEdit} instance's
         * {@link UndoableBenoitEdit#undo}
         * and
         * {@link UndoableBenoitEdit#redo}
         * methods will be called as follows:
         * <ul>
         *     <li>
         *         the returned {@link UndoableBenoitEdit} instance's {@link UndoableBenoitEdit#redo} method
         *         will be called almost immediately to deal with the side effects
         *         of deleting the list element(s) for the first time.
         *         This call will occur immediately after the list element(s) are actually removed from the {@link javax.swing.JList}.
         *         If this call to the returned {@link UndoableBenoitEdit} instance's
         *         {@link UndoableBenoitEdit#redo} method throws a {@link javax.swing.undo.CannotRedoException} then the list elements
         *         are inserted back into the {@link javax.swing.JList} before the exception is re-thrown.
         *         <br/><br/>
         *         VERY IMPORTANT:  the returned {@link UndoableBenoitEdit} instance's {@link UndoableBenoitEdit#undo} method
         *         *** MUST *** leave the removed list elements in a state where they can be re-inserted into
         *         the {@link javax.swing.JList} should a subsequent <i>undo</i> event occur.
         *     </ul>
         *     <li>
         *         if an <i>undo</i> event occurs which requires the re-insertion of the list
         *         element(s) into the {@link javax.swing.JList} then the returned {@link UndoableBenoitEdit} instance's
         *         {@link UndoableBenoitEdit#undo} method will be called before
         *         the list element(s) are re-inserted into the {@link javax.swing.JList}.
         *     </li>
         *     <li>
         *         if a subsequent <i>redo</i> event occurs which requires the removal of the
         *         list element(s) from the {@link javax.swing.JList} then the returned {@link UndoableBenoitEdit} instance's
         *         {@link UndoableBenoitEdit#redo} method will be called immediately after the
         *         list element(s) are removed from the {@link javax.swing.JList}.
         *         If this call to the returned {@link UndoableBenoitEdit} instance's
         *         {@link UndoableBenoitEdit#redo} method throws an unchecked exception then the list elements
         *         are inserted back into the {@link javax.swing.JList} before the exception is re-thrown.
         *     </li>
         * </ul>
         * Note that any call to the returned {@link UndoableBenoitEdit} instance's {@link UndoableBenoitEdit#redo}
         * method is preceded by a call to the  returned {@link UndoableBenoitEdit} instance's
         * {@link UndoableBenoitEdit#canRedo} method to verify that the <i>redo</i> or <i>first-redo</i> is
         * still possible. If this call returns false then a {@link javax.swing.undo.CannotUndoException} is thrown
         * (the {@link javax.swing.JList} will still contain the list element(s)).
         * @param listElements the list elements which are about to be deleted.  This array will contain at
         *                     least one list element.
         * @return the UBE instance responsible for dealing with the side-effects of deleting and re-inserting
         * the list element(s) from the list; null if there are no side-effects.
         */

        UndoableBenoitEdit prepareToDeleteListElements( ListElement[] listElements );

        /**
         * Duplicate one or more list elements and optionally create an {@link UndoableBenoitEdit} instance
         * to manage side-effects.
         * <p/>This method is called by {@link JListManager#doDeleteButton} in preparation for performing
         * an undoable duplication of one or more new list elements.
         * Note that this method call must only create the new list element(s) and optionally provide an
         * {@link UndoableBenoitEdit} instance that manages the side-effects of inserting and removing
         * the element from the list.
         * The caller of this method (i.e. {@link JListManager#doDuplicateButton})
         * is responsible for actually inserting and removing the new list element(s) from the {@link javax.swing.JList} as needed.
         * <p/>
         * Assuming that this method returns a {@link UndoableBenoitEdit}, the returned {@link UndoableBenoitEdit}
         * instance's {@link UndoableBenoitEdit#undo}
         * and
         * {@link UndoableBenoitEdit#redo}
         * methods will be called as follows:
         * <ul>
         *     <li>
         *         the returned {@link UndoableBenoitEdit} instance's {@link UndoableBenoitEdit#redo} method
         *         will be called almost immediately to deal with the side effects
         *         of duplicating the new list element(s) for the first time.
         *         This call will occur just before the newly created list element(s) are inserted
         *         into the {@link javax.swing.JList}.
         *     </ul>
         *     <li>
         *         if an <i>undo</i> event occurs which requires the removal of the duplicate list
         *         element(s) from the {@link javax.swing.JList} then
         *         the returned {@link UndoableBenoitEdit} instance's {@link UndoableBenoitEdit#canUndo}
         *         method is called to verify that the <i>undo</i> is still possible.
         *         If this call returns false then a {@link javax.swing.undo.CannotUndoException} is thrown
         *         (the {@link javax.swing.JList} will still contain the duplicate list elements).
         *         <br/><br/>
         *         Otherwise, the returned {@link UndoableBenoitEdit} instance's
         *         {@link UndoableBenoitEdit#undo} method will be called immediately after
         *         the duplicate list element(s) have been removed from the {@link javax.swing.JList}.
         *         If this call to the returned {@link UndoableBenoitEdit} instance's
         *         {@link UndoableBenoitEdit#undo} method throws a {@link javax.swing.undo.CannotUndoException} then the duplicate
         *         list element(s) are inserted back into the {@link javax.swing.JList} before the exception is re-thrown.
         *         <br/><br/>
         *         VERY IMPORTANT:  the returned {@link UndoableBenoitEdit} instance's {@link UndoableBenoitEdit#undo} method
         *         *** MUST *** leave the duplicate list element(s) in a state where they can be re-inserted into
         *         the {@link javax.swing.JList} should a subsequent <i>redo</i> event occur.
         *     </li>
         *     <li>
         *         if a subsequent <i>redo</i> event occurs which requires the re-insertion of the
         *         duplicate list elements into the {@link javax.swing.JList} then the returned {@link UndoableBenoitEdit} instance's
         *         {@link UndoableBenoitEdit#redo} method will be called just before the
         *         duplicate list element(s) are re-inserted into the {@link javax.swing.JList}.
         *     </li>
         * </ul>
         * @param listElements the list elements which are about to be deleted.  This array will contain at
         *                     least one list element.
         * @return the UBE instance responsible for dealing with the side-effects of duplicating and un-duplicating
         * (is that a word?) the list element(s) from the list; null if there are no side-effects.
         */

        UndoManagedObjects prepareToDuplicateListElement( ListElement[] listElements );

        /**
         * Optionally create an {@link UndoableBenoitEdit} instance
         * to manage side-effects of moving one or more contiguous list elements up or down in the {@link javax.swing.JList}.
         * <p/>This method is called by {@link JListManager#doMoveUpButton} and
         * {@link JListManager#doMoveDownButton} in preparation for performing an undoable move of one or more
         * contiguous list elements.
         * Note that this method call is only responsible for (optionally) creating an
         * {@link UndoableBenoitEdit} instance that manages the side-effects of moving the list elements.
         * The caller of this method (i.e. {@link JListManager#doMoveUpButton} or {@link JListManager#doMoveDownButton})
         * is responsible for actually moving list element(s) in the {@link javax.swing.JList} as needed.
         * <p/>
         * Assuming that this method returns a {@link UndoableBenoitEdit}, the returned {@link UndoableBenoitEdit}
         * instance's {@link UndoableBenoitEdit#undo}
         * and
         * {@link UndoableBenoitEdit#redo}
         * methods will be called as follows:
         * <ul>
         *     <li>
         *         the returned {@link UndoableBenoitEdit} instance's {@link UndoableBenoitEdit#redo} method
         *         will be called almost immediately to deal with the side effects
         *         of moving new list element(s) for the first time.
         *         This call will occur just <u>after</u> the list element(s) are actually moved.
         *     </ul>
         *     <li>
         *         if an <i>undo</i> event occurs which requires the reversal of the earlier movement of the list
         *         element(s) then the returned {@link UndoableBenoitEdit} instance's {@link UndoableBenoitEdit#canUndo}
         *         method will be called to deal with the side-effects.
         *         This call will occur just after the list element(s) are actually moved back to their original
         *         location(s) in the list.
         *     </li>
         *     <li>
         *         if a subsequent <i>redo</i> event occurs which requires the re-movement of the
         *         list elements then the returned {@link UndoableBenoitEdit} instance's
         *         {@link UndoableBenoitEdit#redo} method will be called to deal with the side-effects.
         *         This call will occur just after the list element(s) are actually moved.
         *     </li>
         * </ul>
         * @param moveUp true if the elements are to be moved up the {@link javax.swing.JList}; false if they are to be moved
         *               down the {@link javax.swing.JList}.
         * @param firstElementRow the row number of the first or only list element to be moved.
         * @param contiguousListElements the one or more list elements which are to be moved.  List elements are
         *                               only moved as a contiguous set.
         * @return the UBE instance responsible for dealing with the side-effects of moving or un-moving the
         * list elements.
         */

        UndoableBenoitEdit prepareToMoveListElements(
                boolean moveUp,
                int firstElementRow,
                List<Object> contiguousListElements
        );

    }

    /**
     * An internal class used to carry around JList elements.
     * This class is declared as public on the off chance that it might prove useful to someone.
     * <p/>Instances of this class are immutable.
     */

    public static class ListElement {

        private final int _row;
        private final Object _listElement;

        public ListElement( int row, Object listElement ) {

            super();

            _row = row;
            _listElement = listElement;

        }

        public int getRow() {

            return _row;

        }

        public Object getElement() {

            return _listElement;

        }

        public String toString() {

            return "ListElement( " + _row + ", \"" + _listElement + "\" )";

        }

    }

    /**
     * Manage a JPanel of buttons associated with a particular {@link JListManager} instance.
     */

    public class ButtonPanel extends JPanel {

        public static final String DEFAULT_ADD_BUTTON_NAME = "add-hollow-medium";
        public static final String DEFAULT_DELETE_BUTTON_NAME = "delete-hollow-medium";
        public static final String DEFAULT_MOVE_UP_BUTTON_NAME = "up-hollow-medium";
        public static final String DEFAULT_MOVE_DOWN_BUTTON_NAME = "down-hollow-medium";
        public static final String DEFAULT_DUPLICATE_BUTTON_NAME = "duplicate-grey-text";

        public static final int INTER_BUTTON_GAP = 3;

        private boolean _addButtonIncluded = true;
        private JLabel     _addButton = null;
        private String     _addButtonName = null;
        private ButtonInfo _addButtonInfo = null;

        private boolean _deleteButtonIncluded = true;
        private JLabel     _deleteButton = null;
        private String     _deleteButtonName = null;
        private ButtonInfo _deleteButtonInfo = null;

        private boolean _moveUpButtonIncluded = true;
        private JLabel _moveUpButton = null;
        private String _moveUpButtonName = null;
        private ButtonInfo _moveUpButtonInfo = null;

        private boolean _moveDownButtonIncluded = true;
        private JLabel _moveDownButton = null;
        private String _moveDownButtonName = null;
        private ButtonInfo _moveDownButtonInfo = null;

        private boolean _duplicateButtonIncluded = true;
        private JLabel     _duplicateButton = null;
        private String     _duplicateButtonName = null;
        private ButtonInfo _duplicateButtonInfo = null;

        private boolean _multipleDeleteAllowed    = false;
        private boolean _multipleDuplicateAllowed = false;

        private boolean _panelReady = false;

        public ButtonPanel() {

            super();

        }

        /**
         * Configure this instance.
         * <p/>This method creates the JPanel and populates it with whichever buttons have been included in
         * this instance's configuration (see the various set methods in this class for more information).
         * @throws IllegalArgumentException if either or both of the 'add' or the 'duplicate' buttons are
         * included in the configuration and no {@link ListElementFactory} was specified using
         * {@link JListManager#setListElementFactory}.
         * <p/>This method may only be called (successfully) once for any given ButtonPanel instance.
         * Once this method has been called successfully, all of the set methods in this class will throw
         * an IllegalArgumentException if they are called.
         */

        public void configure() {

            setLayout( new BoxLayout( this, BoxLayout.X_AXIS ) );

            if ( _listElementFactory != null && _undoableListElementFactory != null ) {

                throw new IllegalArgumentException(
                        "both a regular and an undoable list element factory have been configured (this is a bug as this should have been caught in the list element factory setters)"
                );

            }

            boolean firstButton = true;
            if ( _addButtonIncluded ) {

                if ( _listElementFactory == null && _undoableListElementFactory == null ) {

                    throw new IllegalArgumentException(
                            "add button is configured but no list element factory has not set"
                    );

                }

                if ( _addButton == null ) {

                    if ( _addButtonName == null ) {

                        _addButtonName = ButtonPanel.DEFAULT_ADD_BUTTON_NAME;

                    }

                    _addButton = new JLabel();
                    _addButtonInfo = ButtonInfo.makeButtonLabel(
                            JListManager.this,
                            _addButton,
                            new Runnable() {

                                public void run() {

                                    doAddButton();

                                }

                            },
                            _addButtonName,
                            _resourceBaseDirectory,
                            ButtonInfo.getDefaultDarkeningFactor()
                    );

                }

                add( Box.createHorizontalStrut( ButtonPanel.INTER_BUTTON_GAP ) );

                add( _addButton );
                firstButton = false;

            }

            if ( _deleteButtonIncluded ) {

                if ( _deleteButton == null ) {

                    if ( _deleteButtonName == null ) {

                        _deleteButtonName = ButtonPanel.DEFAULT_DELETE_BUTTON_NAME;

                    }

                    _deleteButton = new JLabel();
                    _deleteButtonInfo = ButtonInfo.makeButtonLabel(
                            JListManager.this,
                            _deleteButton,
                            new Runnable() {

                                public void run() {

                                    doDeleteButton();

                                }

                            },
                            _deleteButtonName,
                            _resourceBaseDirectory,
                            ButtonInfo.getDefaultDarkeningFactor()
                    );

                }

                if ( !firstButton ) {

                    add( Box.createHorizontalStrut( ButtonPanel.INTER_BUTTON_GAP ) );
                    firstButton = false;

                }

                add( _deleteButton );

            }

            if ( _moveUpButtonIncluded ) {

                if ( _moveUpButton == null ) {

                    if ( _moveUpButtonName == null ) {

                        _moveUpButtonName = ButtonPanel.DEFAULT_MOVE_UP_BUTTON_NAME;

                    }

                    _moveUpButton = new JLabel();
                    _moveUpButtonInfo = ButtonInfo.makeButtonLabel(
                            JListManager.this,
                            _moveUpButton,
                            new Runnable() {

                                public void run() {

                                    doMoveUpButton();

                                }

                            },
                            _moveUpButtonName,
                            _resourceBaseDirectory,
                            ButtonInfo.getDefaultDarkeningFactor()
                    );

                }

                if ( !firstButton ) {

                    add( Box.createHorizontalStrut( ButtonPanel.INTER_BUTTON_GAP ) );
                    firstButton = false;

                }

                add( _moveUpButton );

            }

            if ( _moveDownButtonIncluded ) {

                if ( _moveDownButton == null ) {

                    if ( _moveDownButtonName == null ) {

                        _moveDownButtonName = ButtonPanel.DEFAULT_MOVE_DOWN_BUTTON_NAME;

                    }

                    _moveDownButton = new JLabel();
                    _moveDownButtonInfo = ButtonInfo.makeButtonLabel(
                            JListManager.this,
                            _moveDownButton,
                            new Runnable() {

                                public void run() {

                                    doMoveDownButton();

                                }

                            },
                            _moveDownButtonName,
                            _resourceBaseDirectory,
                            ButtonInfo.getDefaultDarkeningFactor()
                    );

                }

                if ( !firstButton ) {

                    add( Box.createHorizontalStrut( ButtonPanel.INTER_BUTTON_GAP ) );
                    firstButton = false;

                }

                add( _moveDownButton );

            }

            if ( _duplicateButtonIncluded ) {

                if ( _listElementFactory == null ) {

                    throw new IllegalArgumentException(
                            "duplicate button is configured but list element factory is not set"
                    );

                }

                if ( _duplicateButton == null ) {

                    if ( _duplicateButtonName == null ) {

                        _duplicateButtonName = ButtonPanel.DEFAULT_DUPLICATE_BUTTON_NAME;

                    }

                    _duplicateButton = new JLabel();
                    _duplicateButtonInfo = ButtonInfo.makeButtonLabel(
                            JListManager.this,
                            _duplicateButton,
                            new Runnable() {

                                public void run() {

                                    doDuplicateButton();

                                }

                            },
                            _duplicateButtonName,
                            _resourceBaseDirectory,
                            ButtonInfo.getDefaultDarkeningFactor()
                    );

                }

                if ( !firstButton ) {

                    add( Box.createHorizontalStrut( ButtonPanel.INTER_BUTTON_GAP ) );
//                    firstButton = false;

                }

                add( _duplicateButton );

            }

            _panelReady = true;

        }

        /**
         * Determine if the 'add' button is included in or excluded from this instance's configuration.
         * @return true if it is included; false if it is excluded.
         */

        public boolean isAddButtonIncluded() {

            return _addButtonIncluded;

        }

        /**
         * Specify whether the 'add' button should be included in or excluded from this instance's
         * configuration.
         * @param addButtonIncluded true if the add button is to be included; false if it is to be excluded.
         * @throws IllegalArgumentException if called after a successful call to {@link #configure}.
         */

        public void setAddButtonIncluded( boolean addButtonIncluded ) {

            checkIfTooLate( "setAddButtonIncluded" );
            _addButtonIncluded = addButtonIncluded;

        }

        /**
         * Determine if the 'delete' button is included in or excluded from this instance's configuration.
         * @return true if it is included; false if it is excluded.
         */

        public boolean isDeleteButtonIncluded() {

            return _deleteButtonIncluded;

        }

        /**
         * Specify whether the 'delete' button should be included in or excluded from this instance's
         * configuration.
         * @param deleteButtonIncluded true if the delete button is to be included; false if it is to be excluded.
         * @throws IllegalArgumentException if called after a successful call to {@link #configure}.
         */

        public void setDeleteButtonIncluded( boolean deleteButtonIncluded ) {

            checkIfTooLate( "setDeleteButtonIncluded" );
            _deleteButtonIncluded = deleteButtonIncluded;

        }

        /**
         * Determine if the 'up' button is included in or excluded from this instance's configuration.
         * @return true if it is included; false if it is excluded.
         */

        public boolean isMoveUpButtonIncluded() {

            return _moveUpButtonIncluded;

        }

        /**
         * Specify whether the 'move up' button should be included in or excluded from this instance's
         * configuration.
         * @param moveUpButtonIncluded true if the up button is to be included; false if it is to be excluded.
         * @throws IllegalArgumentException if called after a successful call to {@link #configure}.
         */

        public void setMoveUpButtonIncluded( boolean moveUpButtonIncluded ) {

            checkIfTooLate( "setMoveUpButtonIncluded" );
            _moveUpButtonIncluded = moveUpButtonIncluded;

        }

        /**
         * Determine if the 'down' button is included in or excluded from this instance's configuration.
         * @return true if it is included; false if it is excluded.
         */

        public boolean isMoveDownButtonIncluded() {

            return _moveDownButtonIncluded;

        }

        /**
         * Specify whether the 'move down' button should be included in or excluded from this instance's
         * configuration.
         * @param moveDownButtonIncluded true if the down button is to be included; false if it is to be excluded.
         * @throws IllegalArgumentException if called after a successful call to {@link #configure}.
         */

        public void setMoveDownButtonIncluded( boolean moveDownButtonIncluded ) {

            checkIfTooLate( "setMoveDownButtonIncluded" );
            _moveDownButtonIncluded = moveDownButtonIncluded;

        }

        /**
         * Determine if the 'duplicate' button is included in or excluded from this instance's configuration.
         * @return true if it is included; false if it is excluded.
         */

        public boolean isDuplicateButtonIncluded() {

            return _duplicateButtonIncluded;

        }

        /**
         * Specify whether the 'duplicate' button should be included in or excluded from this instance's
         * configuration.
         * @param duplicateButtonIncluded true if the duplicate button is to be included; false if it is to be excluded.
         * @throws IllegalArgumentException if called after a successful call to {@link #configure}.
         */

        public void setDuplicateButtonIncluded( boolean duplicateButtonIncluded ) {

            checkIfTooLate( "setDuplicateButtonIncluded" );
            _duplicateButtonIncluded = duplicateButtonIncluded;

        }

        /**
         * Determine if this instance has been configured.
         * @return true if {@link #configure} has been (successfully) called; false otherwise;
         */

        public boolean isPanelReady() {

            return _panelReady;

        }

        /**
         * Get this instance's 'add' button.
         * @return this instance's 'add' button or null if the button has been excluded
         * (see {@link #setAddButtonIncluded} for more info) or has not yet been
         * defined (see {@link #setAddButton} and {@link #configure} for more info).
         */

        public JLabel getAddButton() {

            return _addButton;

        }

        /**
         * Set this instance's 'add' button instance and/or its button name.
         * @param addButton this instance's 'add' button instance.
         *                        Leave null if you want
         *                        to let this ButtonPanel instance create the 'add' button instance
         *                        if it is included in the panel's configuration (see {@link #setAddButtonIncluded}
         *                        for more info).
         * @param buttonName the 'add' button's name.
         *                   Leave null if you want to use the default name {@link #DEFAULT_ADD_BUTTON_NAME}
         *                   (see {@link #getAddButtonName} for more info).
         */

        public void setAddButton( JLabel addButton, String buttonName ) {

            checkIfTooLate( "setAddButton" );
            _addButton = addButton;
            _addButtonName = buttonName;
            setAddButtonIncluded( true );

        }

        /**
         * Get this instance's 'delete' button.
         * @return this instance's 'delete' button or null if the button has been excluded
         * (see {@link #setDeleteButtonIncluded} for more info) or has not yet been
         * defined (see {@link #setDeleteButton} and {@link #configure} for more info).
         */

        public JLabel getDeleteButton() {

            return _deleteButton;

        }

        /**
         * Set this instance's 'delete' button instance and/or its button name.
         * @param deleteButton this instance's 'delete' button instance.
         *                        Leave null if you want
         *                        to let this ButtonPanel instance create the 'delete' button instance
         *                        if it is included in the panel's configuration (see {@link #setDeleteButtonIncluded}
         *                        for more info).
         * @param buttonName the 'delete' button's name.
         *                   Leave null if you want to use the default name {@link #DEFAULT_DELETE_BUTTON_NAME}
         *                   (see {@link #getDeleteButtonName} for more info).
         */

        public void setDeleteButton( JLabel deleteButton, String buttonName ) {

            checkIfTooLate( "setDeleteButton" );
            _deleteButton = deleteButton;
            _deleteButtonName = buttonName;
            setDeleteButtonIncluded( true );

        }

        /**
         * Get this instance's 'up' button.
         * @return this instance's 'up' button or null if the button has been excluded
         * (see {@link #setMoveUpButtonIncluded} for more info) or has not yet been
         * defined (see {@link #setMoveUpButton} and {@link #configure} for more info).
         */

        public JLabel getMoveUpButton() {

            return _moveUpButton;

        }

        /**
         * Set this instance's 'up' button instance and/or its button name.
         * @param moveUpButton this instance's 'up' button instance.
         *                        Leave null if you want
         *                        to let this ButtonPanel instance create the 'up' button instance
         *                        if it is included in the panel's configuration (see {@link #setMoveUpButtonIncluded}
         *                        for more info).
         * @param buttonName the 'up' button's name.
         *                   Leave null if you want to use the default name {@link #DEFAULT_MOVE_UP_BUTTON_NAME}
         *                   (see {@link #getMoveUpButtonName} for more info).
         */

        public void setMoveUpButton( JLabel moveUpButton, String buttonName ) {

            checkIfTooLate( "setMoveUpButton" );
            _moveUpButton = moveUpButton;
            _moveUpButtonName = buttonName;
            setMoveUpButtonIncluded( true );

        }

        /**
         * Get this instance's 'down' button.
         * @return this instance's 'down' button or null if the button has been excluded
         * (see {@link #setMoveDownButtonIncluded} for more info) or has not yet been
         * defined (see {@link #setMoveDownButton} and {@link #configure} for more info).
         */

        public JLabel getMoveDownButton() {

            return _moveDownButton;

        }

        /**
         * Set this instance's 'down' button instance and/or its button name.
         * @param moveDownButton this instance's 'down' button instance.
         *                        Leave null if you want
         *                        to let this ButtonPanel instance create the 'down' button instance
         *                        if it is included in the panel's configuration (see {@link #setMoveDownButtonIncluded}
         *                        for more info).
         * @param buttonName the 'down' button's name.
         *                   Leave null if you want to use the default name {@link #DEFAULT_MOVE_DOWN_BUTTON_NAME}
         *                   (see {@link #getMoveDownButtonName} for more info).
         */

        public void setMoveDownButton( JLabel moveDownButton, String buttonName ) {

            checkIfTooLate( "setMoveDownButton" );
            _moveDownButton = moveDownButton;
            _moveDownButtonName = buttonName;
            setMoveDownButtonIncluded( true );

        }

        /**
         * Get this instance's 'duplicate' button.
         * @return this instance's 'duplicate' button or null if the button has been excluded
         * (see {@link #setDuplicateButtonIncluded} for more info) or has not yet been
         * defined (see {@link #setDuplicateButton} and {@link #configure} for more info).
         */

        public JLabel getDuplicateButton() {

            return _duplicateButton;

        }

        /**
         * Set this instance's 'duplicate' button instance and/or its button name.
         * @param duplicateButton this instance's 'duplicate' button instance.
         *                        Leave null if you want
         *                        to let this ButtonPanel instance create the 'duplicate' button instance
         *                        if it is included in the panel's configuration (see {@link #setDuplicateButtonIncluded}
         *                        for more info).
         * @param buttonName the 'duplicate' button's name.
         *                   Leave null if you want to use the default name {@link #DEFAULT_DUPLICATE_BUTTON_NAME}
         *                   (see {@link #getDuplicateButtonName} for more info).
         */

        public void setDuplicateButton( JLabel duplicateButton, String buttonName ) {

            checkIfTooLate( "setDuplicateButton" );
            _duplicateButton = duplicateButton;
            _duplicateButtonName = buttonName;
            setDuplicateButtonIncluded( true );

        }

        /**
         * Get the part of the image file name that uniquely specifies which image is to be used for the 'add' button
         * (defaults to {@link #DEFAULT_ADD_BUTTON_NAME}).
         * <p/>The path to the 'add' button's image file is
         * <blockquote>
         *     <i>resourceBaseDirectory</i><tt>/button-</tt><i>addButtonName</i><tt>.png</tt>
         * </blockquote>
         * which could be constructed using
         * <blockquote>
         *     <tt>getResourceBaseDirectory() + "/button-" + getAddButtonName() + ".png"
         * </blockquote>
         * @return the part of the image file name that uniquely specifies which image is to be used for the 'add' button.
         */

        public String getAddButtonName() {

            return _addButtonName;

        }

        /**
         * Get the 'add' button's {@link com.obtuse.util.ButtonInfo} instance.
         * @return the 'add' button's {@link com.obtuse.util.ButtonInfo} instance.
         */

        public ButtonInfo getAddButtonInfo() {

            return _addButtonInfo;

        }

        /**
         * Get the part of the image file name that uniquely specifies which image is to be used for the 'delete' button
         * (defaults to <tt>delete-hollow</tt>).
         * <p/>The path to the 'delete' button's image file is specified by
         * <blockquote>
         *     <i>resourceBaseDirectory</i><tt>/button-</tt><i>deleteButtonName</i><tt>.png</tt>
         * </blockquote>
         * which could be constructed using
         * <blockquote>
         *     <tt>getResourceBaseDirectory() + "/button-" + getDeleteButtonName() + ".png"
         * </blockquote>
         * @return the part of the image file name that uniquely specifies which image is to be used for the 'delete' button.
         */

        public String getDeleteButtonName() {

            return _deleteButtonName;

        }

        /**
         * Get the 'delete' button's {@link com.obtuse.util.ButtonInfo} instance.
         * @return the 'delete' button's {@link com.obtuse.util.ButtonInfo} instance.
         */

        public ButtonInfo getDeleteButtonInfo() {

            return _deleteButtonInfo;

        }

        /**
         * Get the part of the image file name that uniquely specifies which image is to be used for the 'move up' button
         * (defaults to <tt>up-hollow</tt>).
         * <p/>The path to the 'move up' button's image file is specified by
         * <blockquote>
         *     <i>resourceBaseDirectory</i><tt>/button-</tt><i>moveUpButtonName</i><tt>.png</tt>
         * </blockquote>
         * which could be constructed using
         * <blockquote>
         *     <tt>getResourceBaseDirectory() + "/button-" + getMoveUpButtonName() + ".png"
         * </blockquote>
         * @return the part of the image file name that uniquely specifies which image is to be used for the 'move up' button.
         */

        public String getMoveUpButtonName() {

            return _moveUpButtonName;

        }

        /**
         * Get the 'move up' button's {@link com.obtuse.util.ButtonInfo} instance.
         * @return the 'move up' button's {@link com.obtuse.util.ButtonInfo} instance.
         */

        public ButtonInfo getMoveUpButtonInfo() {

            return _moveUpButtonInfo;

        }

        /**
         * Get the part of the image file name that uniquely specifies which image is to be used for the 'move down' button
         * (defaults to <tt>down-hollow</tt>).
         * <p/>The path to the 'move down' button's image file is specified by
         * <blockquote>
         *     <i>resourceBaseDirectory</i><tt>/button-</tt><i>moveDownButtonName</i><tt>.png</tt>
         * </blockquote>
         * which could be constructed using
         * <blockquote>
         *     <tt>getResourceBaseDirectory() + "/button-" + getMoveDownButtonName() + ".png"
         * </blockquote>
         * @return the part of the image file name that uniquely specifies which image is to be used for the 'move down' button.
         */

        public String getMoveDownButtonName() {

            return _moveDownButtonName;

        }

        /**
         * Get the 'move down' button's {@link com.obtuse.util.ButtonInfo} instance.
         * @return the 'move down' button's {@link com.obtuse.util.ButtonInfo} instance.
         */

        public ButtonInfo getMoveDownButtonInfo() {

            return _moveDownButtonInfo;

        }

        /**
         * Get the part of the image file name that uniquely specifies which image is to be used for the 'duplicate' button
         * (defaults to <tt>duplicate-grey-text</tt>).
         * <p/>The path to the 'duplicate' button's image file is specified by
         * <blockquote>
         *     <i>resourceBaseDirectory</i><tt>/button-</tt><i>duplicateButtonName</i><tt>.png</tt>
         * </blockquote>
         * which could be constructed using
         * <blockquote>
         *     <tt>getResourceBaseDirectory() + "/button-" + getDuplicateButtonName() + ".png"
         * </blockquote>
         * @return the part of the image file name that uniquely specifies which image is to be used for the 'duplicate' button.
         */

        public String getDuplicateButtonName() {

            return _duplicateButtonName;

        }

        /**
         * Get the 'duplicate' button's {@link com.obtuse.util.ButtonInfo} instance.
         * @return the 'duplicate' button's {@link com.obtuse.util.ButtonInfo} instance.
         */

        public ButtonInfo getDuplicateButtonInfo() {

            return _duplicateButtonInfo;

        }

        /**
         * Internal method used by {@link JListManager#setButtonStates()} to set a button's enabled state.
         * @param button the button to be enabled or disabled (this method is a no-op if this parameter is null).
         * @param enabled true if the button is to be enabled; false otherwise.
         */
        private void setButtonEnabledState( JLabel button, boolean enabled ) {

            if ( button != null ) {

                button.setEnabled( enabled );

            }

        }

        /**
         * Determine if multiple rows can be deleted with a single click of the 'delete' button.
         * @return true if multiple rows may be deleted by a single click of the 'delete' button; false otherwise.
         */

        public boolean isMultipleDeleteAllowed() {

            return _multipleDeleteAllowed;

        }

        /**
         * Indicate whether or not multiple rows of the JList may be deleted using a single
         * click of the 'delete' button.
         * <p/>If set to true and the 'delete' button is clicked when more than one row is
         * selected then every selected row is deleted.
         * <p/>The value of this setting is used by {@link JListManager#setButtonStates} when deciding
         * whether or not the 'delete' button should be enabled.
         * <p/>Needless to say, the 'delete' button is never enabled if it was not set to be configured
         * when this {@link ButtonPanel} instance was configured.
         * <p/>This setting defaults to false.
         * @param multipleDeleteAllowed true if multiple rows may be deleted using a single
         *                                 click of the 'delete' button; false otherwise.
         */

        public void setMultipleDeleteAllowed( boolean multipleDeleteAllowed ) {

            checkIfTooLate( "setMultipleDeleteAllowed" );

            _multipleDeleteAllowed = multipleDeleteAllowed;

        }

        /**
         * Determine if multiple rows can be duplicated with a single click of the 'duplicate' button.
         * @return true if multiple rows may be duplicated by a single click of the 'duplicate' button; false otherwise.
         */

        public boolean isMultipleDuplicateAllowed() {

            return _multipleDuplicateAllowed;

        }

        /**
         * Indicate whether or not multiple rows of the JList may be duplicated using a single
         * click of the 'duplicate' button.
         * <p/>If set to true and the 'duplicate' button is clicked when more than one row is
         * selected then every selected row is duplicate.
         * <p/>The value of this setting is used by {@link JListManager#setButtonStates} when deciding
         * whether or not the 'duplicate' button should be enabled.
         * <p/>Needless to say, the 'duplicate' button is never enabled if it was not set to be configured
         * when this {@link ButtonPanel} instance was configured.
         * <p/>This setting defaults to false.
         * @param multipleDuplicateAllowed true if multiple rows may be duplicated using a single
         *                                 click of the 'duplicate' button; false otherwise.
         */

        public void setMultipleDuplicateAllowed( boolean multipleDuplicateAllowed ) {

            checkIfTooLate( "setMultipleDuplicateAllowed" );

            _multipleDuplicateAllowed = multipleDuplicateAllowed;

        }

    }

    private JList _jList;
    private DefaultListModel _listModel;
    @SuppressWarnings("FieldCanBeLocal")
    private ListSelectionModel _listSelectionModel;
    private JScrollPane _scrollPane;
    private ButtonPanel _buttonPanel;
    private UndoManager _undoManager = null;
    @SuppressWarnings("FieldCanBeLocal")
    private final String _name;
    private ListElementFactory _listElementFactory = null;
    private UndoableListElementFactory _undoableListElementFactory = null;
    private String _resourceBaseDirectory;

    /**
     * Create a {@link JListManager}.
     * @param name the name of this JListManager.
     *             This name might appear in certain error messages depending on which version
     *             of this class you are using.
     * @param resourceBaseDirectory the relative or absolute path to the directory containing
     *                              the button images used by this instance's {@link ButtonPanel}.
     */

    protected JListManager( String name, String resourceBaseDirectory ) {

        super();

        _name = name;
        _resourceBaseDirectory = resourceBaseDirectory;

        _listModel = new DefaultListModel();
        _jList = new JList( _listModel );
        _listSelectionModel = _jList.getSelectionModel();
        _jList.addListSelectionListener(
                new ListSelectionListener() {

                    public void valueChanged( ListSelectionEvent listSelectionEvent ) {

                        setButtonStates();

                    }

                }
        );
        _scrollPane = new JScrollPane( _jList );
        _buttonPanel = new ButtonPanel();

    }

    public String getResourceBaseDirectory() {

        return _resourceBaseDirectory;

    }

    /**
     * Get this instance's {@link javax.swing.JList}.
     * Note that Java's Swing facility does not allow a Swing component to appear in more than one place in a Swing
     * GUI.  In other words, either put the result of calling this method into your GUI somewhere or
     * put the result of calling {@link #getScrollPane} into your GUI somewhere.  Do not do both or Swing will
     * either silently remove one or the other from your GUI or possibly do something even more unpleasant.
     * @return this instance's {@link javax.swing.JList}.
     */

    public JList getJList() {

        return _jList;

    }

    /**
     * Get this instance's {@link javax.swing.JList} wrapped inside a {@link javax.swing.JScrollPane}.
     * Note that Java's Swing facility does not allow a Swing component to appear in more than one place in a Swing
     * GUI.  In other words, either put the result of calling this method into your GUI somewhere or
     * put the result of calling {@link #getJList} into your GUI somewhere.  Do not do both or Swing will
     * either silently remove one or the other from your GUI or possibly do something even more unpleasant.
     * @return a {@link javax.swing.JScrollPane} containing this instance's {@link javax.swing.JList} wrapped within it.
     */

    public JScrollPane getScrollPane() {

        return _scrollPane;

    }

    /**
     * Get this instance's {@link javax.swing.DefaultListModel}.
     * @return this instance's DefaultListModel.
     */
    public DefaultListModel getListModel() {

        return _listModel;

    }

    /**
     * Get this instance's {@link ButtonPanel}.
     * @return this instance's ButtonPanel.
     */
    public ButtonPanel getButtonPanel() {

        return _buttonPanel;

    }

    /**
     * Enable or disable the buttons which were configured for this instance.
     * <p/>Whether or not a button is enabled is determined by how many and/or which elements in the list have
     * been selected.  The rules are as follows:
     * <blockquote>
     *     The add button is enabled if no rows or exactly one row is selected.
     *     <br/><br/>
     *     The delete button is enabled in either of two situations:
     *     <ul>
     *         <li>multiple deletes are allowed and at least one row is selected.</li>
     *         <li>multiple deletes are not allowed and exactly one row is selected.</li>
     *     </ul>
     *     The move up button is enabled if any single row other than the first row is selected.
     *     <br/><br/>
     *     The move down button is enabled if any single row other than the last row is selected.
     *     <br/><br/>
     *     The duplicate button is enabled in either of two situations:
     *     <ul>
     *         <li>multiple duplicates are allowed and at least one row is selected.</li>
     *         <li>multiple duplicates are not allowed and exactly one row is selected.</li>
     *     </ul>
     * </blockquote>
     * <p/>This method <b><u>*** MUST ***</u></b> be called if something has happened would <b><u>*** could ***</u></b>
     * require one or more of the button's to be enabled or disabled.
     * <p/>If this method is overridden then the overidding method <b><u>*** MUST ***</u></b> invoke
     * <blockquote>
     *     <tt>super.setButtonStates();</tt>
     * </blockquote>
     * The overidding method <b><u>*** MUST NOT ***</u></b> enable any of the buttons in this instance's ButtonPanel
     * after it has called <tt>super.setButtonStates()</tt>.
     * It may be appropriate for the overridding method to override this method's decision to enable one or more
     * of the buttons in this instance's ButtonPanel although there is almost certainly a better way to deal with
     * whatever situation seems to necessitate this action.
     */

    public void setButtonStates() {

        _buttonPanel.setButtonEnabledState( _buttonPanel.getAddButton(), _jList.getSelectedIndices().length <= 1 );

        _buttonPanel.setButtonEnabledState(
                _buttonPanel.getDeleteButton(),
                _buttonPanel.isMultipleDeleteAllowed()
                ?
                _jList.getSelectedIndices().length > 0
                :
                _jList.getSelectedIndices().length == 1
        );

        _buttonPanel.setButtonEnabledState(
                _buttonPanel.getMoveUpButton(),
                _jList.getSelectedIndices().length == 1 && _jList.getSelectedIndex() != 0
        );

        _buttonPanel.setButtonEnabledState(
                _buttonPanel.getMoveDownButton(),
                _jList.getSelectedIndices().length == 1 && _jList.getSelectedIndex() != _listModel.getSize() - 1
        );

        _buttonPanel.setButtonEnabledState(
                _buttonPanel.getDuplicateButton(),
                _buttonPanel.isMultipleDuplicateAllowed()
                ?
                _jList.getSelectedIndices().length > 0
                :
                _jList.getSelectedIndices().length == 1
        );

    }

    /**
     * Respond to a click of this instance's ButtonPanel's 'add' button.
     * <p/>An appropriate {@link UndoableBenoitEdit} record will be created and recorded if this
     * instance has a {@link javax.swing.undo.UndoManager} set.
     */

    private void doAddButton() {

        Logger.logMsg( "add button clicked" );

        if ( _jList.getSelectedIndices().length > 1 ) {

            throw new IllegalArgumentException( "unable to add a new row if more than one row is selected" );

        }

        UndoableBenoitEdit undo;
        if ( _jList.isSelectionEmpty() ) {

            final int addedRow = _listModel.getSize();
            final UndoManagedObjects userUndo;
            final Object newElement;
            if ( _listElementFactory == null ) {

                userUndo = _undoableListElementFactory.prepareToAddListElement( addedRow );
                if ( userUndo.getObjectCount() != 1 ) {

                    throw new IllegalArgumentException(
                            "" + _undoableListElementFactory +
                            ".prepareToAddListElement did not return exactly one object"
                    );

                }
                newElement = userUndo.getObjects()[0];

            } else {

                userUndo = null;
                newElement = _listElementFactory.createListElement( addedRow );

            }

            undo = new UndoableBenoitEdit( "add button with no selected rows" ) {

                public void doUndo() {

                    if ( userUndo != null && !userUndo.canUndo() ) {

                        Logger.logErr(
                                "undo of add operation cannot be performed because userUndo.getUndoableEdit().canUndo" +
                                "() said so"
                        );
                        throw new CannotUndoException();

                    }

                    if ( !newElement.equals( _listModel.getElementAt( addedRow ) ) ) {

                        Logger.logErr(
                                "undo of add operation cannot be performed " +
                                "because one or more pre-conditions failed " +
                                "(the list has changed since the last undo/redo operation):  " +
                                "unable to undo add of \"" + newElement + "\" because it is not " +
                                "where it is expected to be (row " + addedRow + ")"
                        );

                        throw new CannotUndoException();

                    }

                    _listModel.remove( addedRow );

                    if ( userUndo != null ) {

                        userUndo.doRedo();

                    }

                    _jList.setSelectedIndices( new int[0] );

                }

                public void doRedo() {

                    _listModel.addElement( newElement );
                    _jList.setSelectedIndices( new int[0] );
                    _jList.ensureIndexIsVisible( _listModel.getSize() - 1 );

                }

            };

        } else {

            final int addedRow = _jList.getSelectedIndex();
            final Object newElement = _listElementFactory.createListElement( addedRow );

            undo = new UndoableBenoitEdit( "add button with selected row(s)" ) {

                public void doUndo() {

                    if ( !newElement.equals( _listModel.getElementAt( addedRow ) ) ) {

                        Logger.logErr(
                                "undo of add operation cannot be performed " +
                                "because one or more pre-conditions failed " +
                                "(the list has changed since the last undo/redo operation):  " +
                                "unable to undo add of \"" + newElement + "\" because it is not " +
                                "where it is expected to be (row " + addedRow + ")"
                        );

                        throw new CannotUndoException();

                    }

                    _listModel.remove( addedRow );
                    _jList.setSelectedIndex( addedRow );

                }

                public void doRedo() {

                    _listModel.add( addedRow, newElement );
                    _jList.setSelectedIndex( addedRow + 1 );
                    _jList.ensureIndexIsVisible( addedRow + 1 );

                }

            };

        }

        // Actually add the element by 'redoing' the operation.

        undo.redo();

        if ( _undoManager != null ) {

            _undoManager.addEdit( undo );

        }

        setButtonStates();

    }

    /**
     * Respond to a click of this instance's ButtonPanel's 'delete' button.
     * <p/>An appropriate {@link UndoableBenoitEdit} record will be created and recorded if this
     * instance has a {@link javax.swing.undo.UndoManager} set.
     */

    private void doDeleteButton() {

        Logger.logMsg( "delete button clicked" );

        // Build an array of the elements to be deleted and where they are to be deleted from.
        // This list is used to restore deleted elements when the time comes to undo the deletion.
        // This list is also used to verify that the correct elements are being deleted when the
        // time come to redo the deletion.

        final ListElement[] listElements = new ListElement[_jList.getSelectedIndices().length];
        for ( int ix = 0; ix < listElements.length; ix += 1 ) {

            int row = _jList.getSelectedIndices()[ix];
            listElements[ix] = new ListElement( row, _listModel.getElementAt( row ) );

        }

        UndoableBenoitEdit undo = new UndoableBenoitEdit( "delete button" ) {

                    public void doUndo() {

                        for ( ListElement deletedElement : listElements ) {

                            _listModel.add(
                                    deletedElement.getRow(),
                                    deletedElement.getElement()
                            );

                        }

                    }

                    public void doRedo() {

                        // Verify that the elements which we are to delete are where they are supposed to be in the
                        // JList.

                        boolean ok = true;
                        for ( ListElement deletedElement : listElements ) {

                            Object toBeDeletedElement = _listModel.get( deletedElement.getRow() );
                            if ( !deletedElement.getElement().equals( toBeDeletedElement ) ) {

                                Logger.logMsg(
                                        "redo or first-do of delete operation cannot be performed " +
                                        "because one or more pre-conditions failed " +
                                        "(the list has changed since the last undo/redo operation):  " +
                                        "unable to redo delete since row " + deletedElement.getRow() +
                                        " contains \"" + toBeDeletedElement + "\"" +
                                        " but should contain \"" + deletedElement.getElement() + "\""
                                );

                                ok = false;

                            }

                        }

                        if ( !ok ) {

                            throw new CannotRedoException();

                        }

                        // Delete the elements.  Note that we must delete them in reverse order since deleting
                        // then in forward order would cause the row numbers of later elements to change before
                        // we get around to deleting them (i.e. we would either delete the wrong elements or
                        // suffer an array bounds error).

                        int adjustment = 0;
                        for ( ListElement listElement : listElements ) {

                            int row = listElement.getRow() - adjustment;
                            Object toBeDeletedElement = _listModel.get( row );
                            if ( listElement.getElement().equals( toBeDeletedElement ) ) {

                                _listModel.remove( row );

                            } else {

                                Logger.logMsg(
                                        "redo or first-do of delete operation failed after pre-conditions passed " +
                                        "(this is a bug in JListManager):  " +
                                        "planned to delete element \"" + listElement.getElement() + "\"" +
                                        " on row " + row +
                                        " but it contains element \"" + toBeDeletedElement + "\""
                                );

                                throw new CannotRedoException();

                            }

                            adjustment += 1;

                        }

                    }

                };

        // Actually delete the element(s) by 'redoing' the operation.

        undo.redo();

        if ( _undoManager != null ) {

            _undoManager.addEdit( undo );

        }

        setButtonStates();

    }

    /**
     * Respond to a click of this instance's ButtonPanel's 'move up' button.
     * <p/>An appropriate {@link UndoableBenoitEdit} record will be created and recorded if this
     * instance has a {@link javax.swing.undo.UndoManager} set.
     */

    private void doMoveUpButton() {

        Logger.logMsg( "up button clicked" );

        final int row = _jList.getSelectedIndex();
        final Object toBeMoved = _listModel.get( row );

        UndoableBenoitEdit undo = new UndoableBenoitEdit( "move up button" ) {

                    public void doUndo() {

                        Object found = _listModel.get( row - 1 );
                        if ( toBeMoved.equals( found ) ) {

                            Object entry = _listModel.remove( row - 1 );
                            _listModel.add( row, entry );
                            _jList.setSelectedIndex( row );

                        } else {

                            Logger.logErr(
                                    "undo of move up operation cannot be performed " +
                                    "because one or more pre-conditions failed " +
                                    "(the list has changed since the last undo/redo operation):  " +
                                    "unable to undo move of \"" + toBeMoved + "\" " +
                                    "because row " + ( row - 1 ) + " contains " + "\"" + found + "\" instead"
                            );
                            throw new CannotUndoException();

                        }

                    }

                    public void doRedo() {

                        Object found = _listModel.get( row );
                        if ( toBeMoved.equals( found ) ) {

                            Object entry = _listModel.remove( row );
                            _listModel.add( row - 1, entry );
                            _jList.setSelectedIndex( row - 1 );

                        } else {

                            Logger.logErr(
                                    "redo or first-do of move up operation cannot be performed " +
                                    "because one or more pre-conditions failed " +
                                    "(the list has changed since the last undo/redo operation):  " +
                                    "unable to redo move of \"" + toBeMoved + "\" " +
                                    "because row " + row + " contains \"" + found + "\" instead"
                            );
                            throw new CannotRedoException();

                        }

                    }

                };

        // Actually move the row upwards by 'redoing' the operation.

        undo.redo();

        if ( _undoManager != null ) {

            _undoManager.addEdit( undo );

        }

        setButtonStates();

    }

    /**
     * Respond to a click of this instance's ButtonPanel's 'move down' button.
     * <p/>An appropriate {@link UndoableBenoitEdit} record will be created and recorded if this
     * instance has a {@link javax.swing.undo.UndoManager} set.
     */

    private void doMoveDownButton() {

        Logger.logMsg( "down button clicked" );

        final int row = _jList.getSelectedIndex();
        final Object toBeMoved = _listModel.get( row );

        UndoableBenoitEdit undo = new UndoableBenoitEdit( "move down button" ) {

                    public void doUndo() {

                        Object found = _listModel.get( row + 1 );
                        if ( toBeMoved.equals( found ) ) {

                            Object entry = _listModel.remove( row + 1 );
                            _listModel.add( row, entry );
                            _jList.setSelectedIndex( row );

                        } else {

                            Logger.logErr(
                                    "undo of move down operation cannot be performed " +
                                    "because one or more pre-conditions failed " +
                                    "(the list has changed since the last undo/redo operation):  " +
                                    "unable to undo move of \"" + toBeMoved + "\" " +
                                    "because row " + ( row + 1 ) + " contains " + "\"" + found + "\" instead"
                            );
                            throw new CannotUndoException();

                        }

                    }

                    public void doRedo() {

                        Object found = _listModel.get( row );
                        if ( toBeMoved.equals( found ) ) {

                            Object entry = _listModel.remove( row );
                            _listModel.add( row + 1, entry );
                            _jList.setSelectedIndex( row + 1 );

                        } else {

                            Logger.logErr(
                                    "redo or first-do of move down operation cannot be performed " +
                                    "because one or more pre-conditions failed " +
                                    "(the list has changed since the last undo/redo operation):  " +
                                    "unable to redo/first-do move of \"" + toBeMoved + "\" " +
                                    "because row " + row + " contains \"" + found + "\" instead"
                            );
                            throw new CannotRedoException();

                        }

                    }

                };

        // Actually move the row downwards by 'redoing' the operation.

        undo.redo();

        if ( _undoManager != null ) {

            _undoManager.addEdit( undo );

        }

        setButtonStates();

    }

    /**
     * Respond to a click of this instance's ButtonPanel's 'duplicate' button.
     * <p/>An appropriate {@link UndoableBenoitEdit} record will be created and recorded if this
     * instance has a {@link javax.swing.undo.UndoManager} set.
     */

    private void doDuplicateButton() {

        // Build a list of the selected elements as we will need to know these when we undo or redo this operation.

        final ListElement[] listElements = new ListElement[_jList.getSelectedIndices().length];
        for ( int ix = 0; ix < listElements.length; ix += 1 ) {

            int row = _jList.getSelectedIndices()[ix];
            listElements[ix] = new ListElement( row, _listModel.getElementAt( row ) );

        }

        // Build a parallel list of the duplicate elements that we are going to delete/insert as we undo/redo
        // this operation.

        final Object[] duplicates = new Object[listElements.length];
        for ( int ix = 0; ix < duplicates.length; ix += 1 ) {

            int row = _jList.getSelectedIndices()[ix];
            duplicates[ix] = _listElementFactory.duplicateListElement( row, _listModel.getElementAt( row ) );

        }

        UndoableBenoitEdit undo = new UndoableBenoitEdit( "duplicate button" ) {

                    public void doUndo() {

                        // Verify that the duplicated elements that we are about to remove are all
                        // where we expect them to be.

                        int adjustment = 0;
                        boolean ok = true;
                        for ( int ix = 0; ix < listElements.length; ix++ ) {

                            ListElement element = listElements[ix];

                            int row = element.getRow() + adjustment;
                            Object duplicateElement = duplicates[ix];
                            Object actualElement = _listModel.getElementAt( row + 1 );
                            if ( !actualElement.equals( duplicateElement ) ) {

                                Logger.logMsg(
                                        "undo of duplicate operation cannot be performed because one or more pre-conditions failed " +
                                        "(the list has changed since the last undo/redo operation):  " +
                                        "unable to undo duplication since row " + ( row + 1 ) +
                                        " contains \"" + actualElement + "\"" +
                                        " but should contain \"" + duplicateElement + "\""
                                );

                                ok = false;

                            }

                            adjustment += 1;

                        }

                        if ( !ok ) {

                            throw new CannotUndoException();

                        }

                        // Delete the duplicate elements.

                        for ( int ix = 0, listElementsLength = listElements.length; ix < listElementsLength; ix++ ) {

                            ListElement originalElement = listElements[ix];

                            int row = originalElement.getRow();
                            Object duplicateElement = duplicates[ix];
                            Object actualElement = _listModel.getElementAt( row + 1 );
                            if ( actualElement.equals( duplicateElement ) ) {

                                _listModel.remove( row + 1 );

                            } else {

                                Logger.logMsg(
                                        "undo of duplicate operation failed after pre-conditions passed " +
                                        "(this is a bug in JListManager):  " +
                                        "planned to delete duplicate element \"" + duplicateElement + "\"" +
                                        " on row " + row +
                                        " but it contains element \"" + actualElement + "\""
                                );

                                throw new CannotRedoException();
                            }

                        }

                    }

                    public void doRedo() {

                        // Verify that the elements that we are about to duplicate are all
                        // where we expect them to be.

                        boolean ok = true;
                        for ( ListElement element : listElements ) {

                            int row = element.getRow();
                            Object originalElement = element.getElement();
                            Object foundElement = _listModel.getElementAt( row );
                            if ( !foundElement.equals( originalElement ) ) {

                                Logger.logMsg(
                                        "redo or first-do of duplicate operation cannot be performed because one or " +
                                        "more pre-conditions failed " +
                                        "(the list has changed since the last undo/redo operation):  " +
                                        "unable to redo/first-do duplication since row " + row +
                                        " contains \"" + foundElement + "\"" +
                                        " but should contain \"" + originalElement + "\""
                                );

                                ok = false;

                            }

                        }

                        if ( !ok ) {

                            throw new CannotRedoException();

                        }

                        // Insert the duplicates.

                        int adjustment = 0;
                        for ( int ix = 0; ix < listElements.length; ix += 1 ) {

                            ListElement element = listElements[ix];
                            int row = element.getRow() + adjustment;
                            Object duplicate = duplicates[ix];
                            _listModel.add( row + 1, duplicate );

                            adjustment += 1;

                        }

                    }

                };

        // Actually insert the duplicates.

        undo.redo();

        if ( _undoManager != null ) {

            _undoManager.addEdit( undo );

        }

        setButtonStates();

    }

    /**
     * Get this instance's {@link javax.swing.undo.UndoManager}.
     * @return this instance's {@link javax.swing.undo.UndoManager} or null if it has not yet been set.
     */

    public UndoManager getUndoManager() {

        return _undoManager;

    }

    /**
     * Set this instance's {@link javax.swing.undo.UndoManager}.
     * <p/>If this instance's ButtonPanel is configured when this instance's {@link javax.swing.undo.UndoManager} is null then
     * no undo management will be performed as the various buttons are pressed.
     * @param undoManager the new value for this instance's {@link javax.swing.undo.UndoManager}.
     * @throws IllegalArgumentException if this method is called after this instance's ButtonPanel
     * has been configured.
     */

    public void setUndoManager( UndoManager undoManager ) {

        checkIfTooLate( "setUndoManager" );
        _undoManager = undoManager;

    }

    /**
     * Get this instance's {@link ListElementFactory}.
     * @return this instance's {@link ListElementFactory} or null if it has not yet been set.
     */

    public ListElementFactory getListElementFactory() {

        return _listElementFactory;
    }

    /**
     * Set this instance's {@link ListElementFactory}.
     * <p/>This instance's ButttonPanel cannot be (successfully) configured if this instance's
     * {@link ListElementFactory} is null.
     * @param listElementFactory the new value for this instance's {@link ListElementFactory}.
     * @throws IllegalArgumentException if this method is called after this instance's ButtonPanel
     * has been configured or if this instance's undoableListElementFactory is already set.
     */

    public void setListElementFactory( ListElementFactory listElementFactory ) {

        checkIfTooLate( "setListElementFactory" );
        if ( _undoableListElementFactory != null ) {

            throw new IllegalArgumentException( "attempt to set regular listElementFactory when undoableListElementFactory is already set" );

        }

        _listElementFactory = listElementFactory;

    }

    /**
     * Get this instance's {@link UndoableListElementFactory}.
     * @return this instance's {@link UndoableListElementFactory} or null if it has not yet been set.
     */

    public UndoableListElementFactory getUndoableListElementFactory() {

        return _undoableListElementFactory;

    }

    /**
     * Set this instance's {@link ListElementFactory}.
     * <p/>This instance's ButttonPanel cannot be (successfully) configured if this instance's
     * {@link ListElementFactory} is null.
     * @param undoableListElementFactory the new value for this instance's {@link ListElementFactory}.
     * @throws IllegalArgumentException if this method is called after this instance's ButtonPanel
     * has been configured or if this instance's regular listElementFactory is already set.
     */

    public void setUndoableListElementFactory( UndoableListElementFactory undoableListElementFactory ) {

        checkIfTooLate( "setListElementFactory" );
        if ( _listElementFactory != null ) {

            throw new IllegalArgumentException( "attempt to set undoableListElementFactory when regular listElementFactory is already set" );

        }

        _undoableListElementFactory = undoableListElementFactory;

    }

    /**
     * Verify that a change request is not occurring too late.
     * Most of a JListManager's state becomes frozen once
     * the JListManager's ButtonPanel is configured.
     * Attempts to change these parts of the JListManager's state after the ButtonPanel is configured are verbotten.
     * @param methodName which setting is being called to make a change which might be too late.
     */

    private void checkIfTooLate( String methodName ) {

        if ( _buttonPanel.isPanelReady() ) {

            throw new IllegalArgumentException( methodName + " called after JlistManager's button panel is ready" );

        }

    }

}
