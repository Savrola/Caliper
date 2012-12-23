package com.obtuse.ui;

/*
 * Copyright © 2012 Daniel Boulet
 */

import com.obtuse.util.BasicProgramConfigInfo;
import com.obtuse.util.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Manage a JPanel containing {@link SortableJComponent}s.
 */

public class SortedJPanel extends JPanel {

    @SuppressWarnings("FieldCanBeLocal")
    private final BoxLayout _layoutManager;

    @SuppressWarnings("SameParameterValue")
    public SortedJPanel( int axis ) {
        super();

        //noinspection MagicConstant,ThisEscapedInObjectConstruction
        _layoutManager = new BoxLayout( this, axis );
        super.setLayout( _layoutManager );

    }

    public void setLayout( LayoutManager layoutManager ) {

        if ( getLayout() == null ) {

            super.setLayout( layoutManager );

        } else {

            throw new IllegalArgumentException( "SortedJPanel:  attempt to change layout manager once sorted JPanel has been created" );

        }

    }

    public Component add( Component component ) {

        if ( !( component instanceof SortableJComponent ) ) {

            throw new IllegalArgumentException( "SortedJPanel.add:  components must implement SortableJComponent interface" );

        }

        SortableJComponent sortableJComponent = (SortableJComponent)component;
        for ( int ix = 0; ix < getComponentCount(); ix += 1 ) {

            SortableJComponent existingComponent = (SortableJComponent)getComponent( ix );
            if ( sortableJComponent.compareTo( existingComponent ) <= 0 ) {

                super.add( component, ix );

                return component;

            }

        }

        super.add( component );

        return component;

    }

    public Component add( Component component, int ix ) {

        throw new IllegalArgumentException(
                "SortedJPanel.add:  attempt to use add( Component comp, int index ), must use add( Component comp )"
        );

    }

    public void add( Component component, Object constraints ) {

        throw new IllegalArgumentException(
                "SortedJPanel.add:  attempt to use add( Component comp, Object contstraints ), must use add( Component comp )"
        );

    }

    public void add( Component component, Object constraints, int ix ) {

        throw new IllegalArgumentException(
                "SortedJPanel.add:  attempt to use add( Component comp, Object constraints, int index ), must use add( Component comp )"
        );

    }

    public Component add( String name, Component component ) {

        throw new IllegalArgumentException(
                "SortedJPanel.add:  attempt to use add( String name, Component comp ), must use add( Component comp )"
        );

    }

    private static class MyJPanel extends JPanel implements SortableJComponent {

        private final String _label;

        private MyJPanel( String label ) {
            super();

            _label = label;

        }

        public int compareTo( SortableJComponent rhs ) {

            return _label.compareTo( ((MyJPanel)rhs).getLabel() );

        }

        public String getLabel() {

            return _label;

        }

    }

    private static JPanel makeJPanel( final String label ) {

        JPanel panel = new MyJPanel( label );
        panel.setLayout( new BoxLayout( panel, BoxLayout.X_AXIS ) );
        JButton button = new JButton( label );
        button.addActionListener(
                new ActionListener() {

                    public void actionPerformed( ActionEvent actionEvent ) {

                        Logger.logMsg( "someone clicked the \"" + label + "\" button" );

                    }

                }
        );

        panel.add( new JLabel( label ) );
        panel.add( button );

        return panel;

    }

    @SuppressWarnings("UnqualifiedStaticUsage")
    public static void main( String[] args ) {

        BasicProgramConfigInfo.init( "Obtuse", "Caliper", "SortedJPanel", null );

        JFrame topFrame = new JFrame();

        SortedJPanel sjp = new SortedJPanel( BoxLayout.Y_AXIS );
        sjp.add( makeJPanel( "There" ) );
        sjp.add( makeJPanel( "World" ) );
        sjp.add( makeJPanel( "Hello" ) );
        sjp.add( makeJPanel( " starts with space" ) );
        sjp.add( makeJPanel( "Zee Last One" ) );

        topFrame.setContentPane( sjp );
        topFrame.pack();
        topFrame.setVisible( true );

    }

}
