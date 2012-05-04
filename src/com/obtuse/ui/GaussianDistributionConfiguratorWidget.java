package com.obtuse.ui;

import com.obtuse.util.*;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.util.*;

/*
 * Copyright © 2012 Daniel Boulet
 */

public class GaussianDistributionConfiguratorWidget extends JPanel {

    private JPanel _panel1;
    private MultiPointSlider _weightSlider;
    private MultiPointSlider _centerSlider;
    private MultiPointSlider _standardDeviationSlider;
    private JPanel _previewPanel;

    private java.util.List<ChangeListener> _changeListeners = new LinkedList<ChangeListener>();

    private static Hashtable<Integer,MpsLabel> _0to1byQuartersLabels;
    private GaussianDistributionDrawing _gdd;

    static {

        _0to1byQuartersLabels = new Hashtable<Integer,MpsLabel>();
        _0to1byQuartersLabels.put( 0, new MpsLabel( "0.0" ) );
        _0to1byQuartersLabels.put( 25, new MpsLabel( "0.25" ) );
        _0to1byQuartersLabels.put( 50, new MpsLabel( "0.5" ) );
        _0to1byQuartersLabels.put( 75, new MpsLabel( "0.75" ) );
        _0to1byQuartersLabels.put( 100, new MpsLabel( "1.0" ) );

    }

    public GaussianDistributionConfiguratorWidget() {
        super();

        setLayout( new BoxLayout( this, BoxLayout.X_AXIS ) );
        add( _panel1 );

        _previewPanel.setLayout( new BoxLayout( _previewPanel, BoxLayout.X_AXIS ) );
        _gdd = new GaussianDistributionDrawing( new GaussianDistribution( 0.5, 0.5 ) );
        _previewPanel.add( _gdd );

        configureSlider( _weightSlider, 0, 100, 1, 25, 0, _0to1byQuartersLabels );
        configureSlider( _centerSlider, 0, 100, 50, 25, 0, _0to1byQuartersLabels );
        configureSlider( _standardDeviationSlider, 0, 100, 25, 25, 0, _0to1byQuartersLabels );
        setDistribution();

        _weightSlider.getModel().addChangeListener(
                new ChangeListener() {
                    public void stateChanged( ChangeEvent changeEvent ) {

                        Logger.logMsg(
                                "weight changed:  " +
                                "value = " + _weightSlider.getModel().getValue() + ", " +
                                "min = " + _weightSlider.getModel().getMinimum() + ", " +
                                "max = " + _weightSlider.getModel().getMaximum()
                        );

                        fireChangeListeners( changeEvent );

                    }

                }
        );

        _centerSlider.getModel().addChangeListener(
                new ChangeListener() {
                    public void stateChanged( ChangeEvent changeEvent ) {

//                        Logger.logMsg(
//                                "center changed:  " +
//                                "value = " + _centerSlider.getModel().getValue() + ", " +
//                                "min = " + _centerSlider.getModel().getMinimum() + ", " +
//                                "max = " + _centerSlider.getModel().getMaximum()
//                        );

                        setDistribution();
                        fireChangeListeners( changeEvent );

                    }

                }
        );

        _standardDeviationSlider.getModel().addChangeListener(
                new ChangeListener() {
                    public void stateChanged( ChangeEvent changeEvent ) {

//                        Logger.logMsg(
//                                "std dev changed:  " +
//                                "value = " + _standardDeviationSlider.getModel().getValue() + ", " +
//                                "min = " + _standardDeviationSlider.getModel().getMinimum() + ", " +
//                                "max = " + _standardDeviationSlider.getModel().getMaximum()
//                        );

                        if ( _standardDeviationSlider.getValue() == 0 ) {

                            _standardDeviationSlider.setValue( 1 );

                        } else {

                            Logger.logMsg( "std dev is " + _standardDeviationSlider.getValue() );
                            setDistribution();
                            fireChangeListeners( changeEvent );

                        }

                    }

                }
        );

    }

    public double getWeight() {

        return _weightSlider.getValue() / 100.0;

    }

    public double getCenter() {

        return _centerSlider.getValue() / 100.0;

    }

    public double getStandardDeviation() {

        return _standardDeviationSlider.getValue() / 100.0;

    }

    public ProportionalGaussianDistribution getProportionalGaussianDistribution() {

        return new ProportionalGaussianDistribution( getWeight(), getCenter(), getStandardDeviation() );

    }

    public void addChangeListener( ChangeListener changeListener ) {

        removeChangeListener( changeListener );
        _changeListeners.add( changeListener );

    }

    public void removeChangeListener( ChangeListener changeListener ) {

        _changeListeners.remove( changeListener );

    }

    private void fireChangeListeners( ChangeEvent changeEvent ) {

        for ( ChangeListener changeListener : _changeListeners ) {

            changeListener.stateChanged( changeEvent );

        }

    }

    private void setDistribution() {

        _gdd.setDistribution( new GaussianDistribution( _centerSlider.getValue() / 100.0, _standardDeviationSlider.getValue() / 100.0 ) );

    }

    private void configureSlider(
            MultiPointSlider slider,
            int minimum,
            int maximum,
            int value,
            int majorTicks,
            int minorTicks,
            Dictionary<Integer, MpsLabel> labels
    ) {

        slider.setMinimum( minimum );
        slider.setMaximum( maximum );
        slider.setValue( value );
        slider.setMajorTickSpacing( majorTicks );
        slider.setMinorTickSpacing( minorTicks );
        slider.setPaintLabels( true );
        slider.setPaintTicks( majorTicks > 0 || minorTicks > 0 );
//        slider.setPaintTrack( true );
        slider.setLabelTable( labels );

    }

    private void createUIComponents() {

        _weightSlider = new MultiPointSlider( "weight", 1, 100 );

        _centerSlider = new MultiPointSlider( "center", 0, 100, 25 );

        _standardDeviationSlider = new MultiPointSlider( "standard deviation", 1, 100 );

    }

    public static void main( String[] args ) {

        BasicProgramConfigInfo.init( "Obtuse", "Shared", "StandardDeviationConfigurationWidget", null );

        JFrame xx = new JFrame();
        JPanel jp = new JPanel();
        jp.setLayout( new BoxLayout( jp, BoxLayout.Y_AXIS ) );
        xx.setContentPane( jp );
        final GaussianDistributionConfiguratorWidget[] widgets = new GaussianDistributionConfiguratorWidget[]
                {
                        new GaussianDistributionConfiguratorWidget(),
                        new GaussianDistributionConfiguratorWidget(),
                        new GaussianDistributionConfiguratorWidget()
                };

        final ProportionalGaussianDistributionsDrawing pgdd = new ProportionalGaussianDistributionsDrawing();
        pgdd.setMinimumSize( new Dimension( 100, 100 ) );
        pgdd.setPreferredSize( new Dimension( 100, 100 ) );

        jp.add( pgdd );

        for ( GaussianDistributionConfiguratorWidget widget : widgets ) {

            widget.addChangeListener(
                    new ChangeListener() {
                        public void stateChanged( ChangeEvent changeEvent ) {

                            pgdd.setDistributions(
                                    new ProportionalGaussianDistribution[] {
                                            widgets[0].getProportionalGaussianDistribution(),
                                            widgets[1].getProportionalGaussianDistribution(),
                                            widgets[2].getProportionalGaussianDistribution()
                                    }
                            );
                        }

                    }
            );
            jp.add( widget );

        }

        xx.pack();
        xx.setVisible( true );

    }

}
