package com.obtuse.ui;

import com.obtuse.util.BasicProgramConfigInfo;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/*
 * Copyright © 2012 Daniel Boulet
 */

/**
 * Try out MPS class again.
 */

public class MpsTest2 extends JFrame {

    private JPanel _panel1;
    private JButton _quitButton;
    private JComponent _slider1;
    private JComponent _slider2;
    private JPanel _slider1panel;
    private JPanel _zzz;
    private JButton _button1;
    private JButton _button2;
    private JButton _button3;
    private JButton _button4;
    private JButton _button5;
    private JPanel _slider2panel;
    private MultiPointSlider _multiPointSlider1;

    public MpsTest2() {
        super();

        setContentPane( _panel1 );

//        BasicProgramConfigInfo.init( "Obtuse", "MultiPointSlider", "MpsTest2", null );

        MultiPointSlider slider;

        slider = new MultiPointSlider( "s3", 0, 10 );
        slider.setPaintLabels( false );
        slider.setPaintTickMarks( true );
        slider.setMinorTickSpacing( 1 );
        slider.setMajorTickSpacing( 2 );
        slider.setMinimumSize( slider.computeMinimumSize() );
        slider.setMaximumSize( slider.computeMinimumSize() );
        slider.setPreferredSize( slider.computeMinimumSize() );
        _slider1 = slider;
        _slider1panel.setLayout( new BoxLayout( _slider1panel, BoxLayout.X_AXIS ) );
        _slider1panel.add( _slider1 );
//        _slider1panel.add( new JButton( "Hello" ) );

        slider = new MultiPointSlider( "s7", 0, 10 );
        slider.setPaintLabels( false );
        slider.setPaintTickMarks( false );
        slider.setMinorTickSpacing( 1 );
        slider.setMajorTickSpacing( 2 );
        slider.setMinimumSize( slider.computeMinimumSize() );
        slider.setMaximumSize( slider.computeMinimumSize() );
        slider.setPreferredSize( slider.computeMinimumSize() );
        slider.setPositionOnLine( MultiPointSlider.PositionOnLine.BELOW );
        _slider2 = slider;
        _slider2panel.setLayout( new BoxLayout( _slider2panel, BoxLayout.X_AXIS ) );
        _slider2panel.add( _slider2 );

        _quitButton.addActionListener(
                new ActionListener() {

                    public void actionPerformed( ActionEvent actionEvent ) {

                        System.exit( 0 );

                    }

                }
        );

        pack();
        setVisible( true );

    }

    public static void main( String[] args ) {

        MpsTest2 mt = new MpsTest2();

    }

    private void createUIComponents() {

        BasicProgramConfigInfo.init( "Obtuse", "MultiPointSlider", "MpsTest2", null );

        _multiPointSlider1 = new MultiPointSlider( "hello", 1, 10 );
        _multiPointSlider1.setMinimumSize( _multiPointSlider1.computeMinimumSize() );
        _multiPointSlider1.setMaximumSize( _multiPointSlider1.computeMinimumSize() );
        _multiPointSlider1.setPreferredSize( _multiPointSlider1.computeMinimumSize() );
        _multiPointSlider1.setKnobSize( MpsKnobSize.SIZE_5x5 );
//        MultiPointSlider slider;
//
//        slider = new MultiPointSlider( "s3", 0, 10 );
//        slider.setDrawLabels( false );
//        slider.setMinimumSize( slider.computeMinimumSize() );
//        slider.setMaximumSize( slider.computeMinimumSize() );
//        slider.setPreferredSize( slider.computeMinimumSize() );
//        slider.setDrawTickMarks( true );
//        slider.setMinorTickSpacing( 1 );
//        slider.setMajorTickSpacing( 2 );
//        _slider1 = slider;
//        _slider1panel.setLayout( new BoxLayout( _slider1panel, BoxLayout.X_AXIS ) );
//        _slider1panel.add( _slider1 );
//        _slider1panel.add( new JButton( "Hello" ) );
//
//        slider = new MultiPointSlider( "s7", 0, 10 );
//        slider.setDrawLabels( false );
//        slider.setMinimumSize( slider.computeMinimumSize() );
//        slider.setMaximumSize( slider.computeMinimumSize() );
//        slider.setPreferredSize( slider.computeMinimumSize() );
//        slider.setPositionOnLine( MultiPointSlider.PositionOnLine.BELOW );
//        slider.setDrawTickMarks( false );
//        slider.setMinorTickSpacing( 1 );
//        slider.setMajorTickSpacing( 2 );
//        _slider2 = slider;
//        _zzz.setLayout( new BoxLayout( _zzz, BoxLayout.X_AXIS ) );
//        _zzz.add( _slider2 );
//
//        pack();

    }

}
