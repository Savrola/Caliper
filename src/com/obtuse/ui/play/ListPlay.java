package com.obtuse.ui.play;

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
 * %%% something clever goes here.
 */
public class ListPlay extends JFrame {

    private JPanel _panel1;
    private JScrollPane _scrollPane;
    private JPanel _panelOfPanels;
    private DefaultListModel _listModel;
    private ListCellRenderer _cellRenderer;

    public class MyCellRenderer implements ListCellRenderer {

        public Component getListCellRendererComponent( JList jList, Object o, int i, boolean b, boolean b1 ) {

            return (Component)o;

        }

    }

    public ListPlay() {

        setContentPane( _panel1 );

        _panelOfPanels.setLayout( new BoxLayout( _panelOfPanels, BoxLayout.Y_AXIS ) );
        _panelOfPanels.add( makeListElement( "Hello" ) );
        _panelOfPanels.add( makeListElement( "World" ) );
        _panelOfPanels.add( makeListElement( "There" ), 1 );

        pack();
        setVisible( true );

    }

    private JComponent makeListElement( final String label ) {

        JPanel panel = new JPanel();
        panel.setLayout( new BoxLayout( panel, BoxLayout.X_AXIS ) );
        JButton button = new JButton( label );
        button.addActionListener(
                new ActionListener() {

                    public void actionPerformed( ActionEvent actionEvent ) {

                        Logger.logMsg( "Someone clicked the \"" + label + "\" button" );

                    }

                }
        );

        panel.add( button );
        panel.add( new JLabel( label) );

        return panel;

    }

    public static void main( String[] args ) {

        BasicProgramConfigInfo.init( "Obtuse", "Caliper", "ListPlay", null );
        ListPlay lp = new ListPlay();

    }

}
