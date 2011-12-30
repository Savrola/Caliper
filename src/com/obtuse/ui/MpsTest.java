package com.obtuse.ui;

import com.obtuse.util.BasicProgramConfigInfo;
import com.obtuse.util.Logger;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.util.Dictionary;
import java.util.Hashtable;

/**
 * Take the {@link MultiPointSlider} class out for a test drive.
 */

public class MpsTest extends JFrame {

    private JPanel _panel1;
    private JPanel _slider1panel;
    private JPanel _slider2panel;
    private JPanel _slider3panel;
    private JPanel _slider4panel;
    private JPanel _slider5panel;
    private JPanel _slider6panel;
    private JPanel _slider7panel;
    private JPanel _slider8panel;
    private JPanel _slider1;
    private JPanel _slider2;
    private JPanel _slider3;
    private JPanel _slider4;
    private JPanel _slider5;
    private JPanel _slider6;
    private JPanel _slider7;
    private JPanel _slider8;

    public MpsTest() {

        setContentPane( _panel1 );

        pack();

        for ( JComponent slider : new JComponent[] { _slider1, _slider2, _slider3, _slider4, _slider5, _slider6, _slider7, _slider8 } ) {

            MultiPointSlider mps = (MultiPointSlider)slider;
            mps.setMinimumSize( mps.computeMinimumSize() );
            mps.setPreferredSize( mps.computeMinimumSize() );

        }

    }

    public static void main( String[] args ) {

        BasicProgramConfigInfo.init( "Obtuse", "MpsTest", "testing", null );

        MpsTest test = new MpsTest();
        test.setVisible( true );

    }

    private void createUIComponents() {
        Dictionary<Integer,MpsLabel> labels = new Hashtable<Integer, MpsLabel>();
        labels.put( 2, new MpsLabel( "two" ) );
        labels.put( 20, new MpsLabel( "twenty" ) );
        labels.put( 10, new MpsLabel( "<<< ten >>>" ) );

        MultiPointSlider slider = new MultiPointSlider( "s1", 0, 10 );
        slider.setLabelTable( labels );
        slider.setMinorTickSpacing( 1 );
        slider.setMajorTickSpacing( 2 );
        slider.setDrawTickMarks( true );
        slider.setDrawLabels( true );
        slider.getModel().setValue( slider.getModel().getMaximum() );
        slider.setMinimumSize( slider.computeMinimumSize() );
        slider.setPreferredSize( slider.computeMinimumSize() );
        slider.setMaximumSize( slider.computeMinimumSize() );
        _slider1panel.setLayout( new BoxLayout( _slider1panel, BoxLayout.X_AXIS ) );
        _slider1panel.add( slider );

        slider = new MultiPointSlider( "s2", 0, 1000 );
        slider.setLabelTable( labels );
        slider.setPositionOnLine( MultiPointSlider.PositionOnLine.BELOW );
        slider.setMinorTickSpacing( 50 );
        slider.setMajorTickSpacing( 100 );
        slider.setDrawTickMarks( true );
        slider.setDrawLabels( true );
        slider.getModel().setValue( ( slider.getModel().getMinimum() ) );
        slider.setMinimumSize( slider.computeMinimumSize() );
        slider.setPreferredSize( slider.computeMinimumSize() );
        slider.setMaximumSize( slider.computeMinimumSize() );
        _slider2panel.setLayout( new BoxLayout( _slider1panel, BoxLayout.X_AXIS ) );
        _slider2panel.add( slider );

        slider = new MultiPointSlider( "s3", 0, 100 );
        slider.setLabelTable( labels );
        slider.setMinorTickSpacing( 5 );
        slider.setMajorTickSpacing( 10 );
        slider.setDrawTickMarks( true );
        slider.setDrawLabels( false );
        slider.getModel().setValue( slider.getModel().getMaximum() );
        slider.setMinimumSize( slider.computeMinimumSize() );
        slider.setPreferredSize( slider.computeMinimumSize() );
        slider.setMaximumSize( slider.computeMinimumSize() );
        _slider3panel.setLayout( new BoxLayout( _slider1panel, BoxLayout.X_AXIS ) );
        _slider3panel.add( slider );

        slider = new MultiPointSlider( "s4", 0, 1000 );
        slider.setLabelTable( labels );
        slider.setPositionOnLine( MultiPointSlider.PositionOnLine.BELOW );
        slider.setMinorTickSpacing( 50 );
        slider.setMajorTickSpacing( 100 );
        slider.setDrawTickMarks( true );
        slider.setDrawLabels( false );
        slider.getModel().setValue( ( slider.getModel().getMinimum() ) );
        slider.setMinimumSize( slider.computeMinimumSize() );
        slider.setPreferredSize( slider.computeMinimumSize() );
        slider.setMaximumSize( slider.computeMinimumSize() );
        _slider4panel.setLayout( new BoxLayout( _slider1panel, BoxLayout.X_AXIS ) );
        _slider4panel.add( slider );

//        JPanel redPanel = new JPanel();
//        redPanel.setLayout( new BoxLayout( redPanel, BoxLayout.X_AXIS ) );
//        redPanel.setBackground( new Color( 255, 200, 200 ) );
        slider = new MultiPointSlider( "s5", 0, 10 );
        slider.setLabelTable( labels );
        slider.setMinorTickSpacing( 1 );
        slider.setMajorTickSpacing( 2 );
        slider.setDrawTickMarks( true );
        slider.setDrawLabels( true );
        slider.setPositionOnLine( MultiPointSlider.PositionOnLine.LEFT );
        slider.getModel().setValue( slider.getModel().getMaximum() );
        final MultiPointSlider leftSlider = slider;
        slider.addChangeListener(
                new ChangeListener() {

                    public void stateChanged( ChangeEvent changeEvent ) {

                        Logger.logMsg( "left slider changed:  value is " + leftSlider.getModel().getValue() );

                    }

                }
        );
        slider.setMinimumSize( slider.computeMinimumSize() );
        slider.setPreferredSize( slider.computeMinimumSize() );
        slider.setMaximumSize( slider.computeMinimumSize() );
        _slider5panel.setLayout( new BoxLayout( _slider1panel, BoxLayout.X_AXIS ) );
        _slider5panel.add( slider );

        slider = new MultiPointSlider( "s6", 0, 1000 );
        slider.setLabelTable( labels );
        slider.setPositionOnLine( MultiPointSlider.PositionOnLine.RIGHT );
        slider.setMinorTickSpacing( 50 );
        slider.setMajorTickSpacing( 100 );
        slider.setDrawTickMarks( true );
        slider.setDrawLabels( true );
        slider.getModel().setValue( ( slider.getModel().getMinimum() ) );
        slider.setMinimumSize( slider.computeMinimumSize() );
        slider.setPreferredSize( slider.computeMinimumSize() );
        slider.setMaximumSize( slider.computeMinimumSize() );
        _slider6panel.setLayout( new BoxLayout( _slider1panel, BoxLayout.X_AXIS ) );
        _slider6panel.add( slider );

        slider = new MultiPointSlider( "s7", 0, 100 );
        slider.setMinorTickSpacing( 5 );
        slider.setMajorTickSpacing( 10 );
        slider.setDrawTickMarks( true );
        slider.setDrawLabels( false );
        slider.setPositionOnLine( MultiPointSlider.PositionOnLine.LEFT );
        slider.getModel().setValue( slider.getModel().getMaximum() );
        slider.setMinimumSize( slider.computeMinimumSize() );
        slider.setPreferredSize( slider.computeMinimumSize() );
        slider.setMaximumSize( slider.computeMinimumSize() );
        _slider7panel.setLayout( new BoxLayout( _slider1panel, BoxLayout.X_AXIS ) );
        _slider7panel.add( slider );

        slider = new MultiPointSlider( "s8", 0, 1000 );
        slider.setLabelTable( labels );
        slider.setPositionOnLine( MultiPointSlider.PositionOnLine.RIGHT );
        slider.setMinorTickSpacing( 50 );
        slider.setMajorTickSpacing( 100 );
        slider.setDrawTickMarks( true );
        slider.setDrawLabels( false );
        slider.getModel().setValue( ( slider.getModel().getMinimum() ) );
        slider.setMinimumSize( slider.computeMinimumSize() );
        slider.setPreferredSize( slider.computeMinimumSize() );
        slider.setMaximumSize( slider.computeMinimumSize() );
        _slider8panel.setLayout( new BoxLayout( _slider1panel, BoxLayout.X_AXIS ) );
        _slider8panel.add( slider );

    }

}
