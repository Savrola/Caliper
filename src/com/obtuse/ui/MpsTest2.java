package com.obtuse.ui;

import com.obtuse.util.BasicProgramConfigInfo;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Try out MPS class again.
 */

public class MpsTest2 extends JFrame {

    private JPanel _panel1;
    private JButton _quitButton;
    private JPanel _slider1;
    private JPanel _slider2;

    public MpsTest2() {
        super();

        setContentPane( _panel1 );

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

        MultiPointSlider slider;

        slider = new MultiPointSlider( "s3", 0, 10 );
        slider.setDrawLabels( false );
        slider.setMinimumSize( slider.computeMinimumSize() );
        slider.setMaximumSize( slider.computeMinimumSize() );
        slider.setPreferredSize( slider.computeMinimumSize() );
        slider.setDrawTickMarks( true );
        slider.setMinorTickSpacing( 1 );
        slider.setMajorTickSpacing( 2 );
        _slider1 = slider;

        slider = new MultiPointSlider( "s7", 0, 10 );
        slider.setDrawLabels( false );
        slider.setMinimumSize( slider.computeMinimumSize() );
        slider.setMaximumSize( slider.computeMinimumSize() );
        slider.setPreferredSize( slider.computeMinimumSize() );
        slider.setPositionOnLine( MultiPointSlider.PositionOnLine.BELOW );
        slider.setDrawTickMarks( false );
        slider.setMinorTickSpacing( 1 );
        slider.setMajorTickSpacing( 2 );
        _slider2 = slider;

        pack();

    }
}
