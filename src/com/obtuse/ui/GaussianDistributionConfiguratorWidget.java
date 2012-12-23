package com.obtuse.ui;

import com.obtuse.util.*;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.util.*;

/**
 * A widget for configurating a gaussian distribution.
 * <p/>
 * Copyright © 2012 Invidi Technologies Corporation
 * Copyright © 2012 Obtuse Systems Corporation
 */

public class GaussianDistributionConfiguratorWidget extends JPanel {

    public static final float DEFAULT_CENTER = .5f;
    public static final float DEFAULT_STANDARD_DEVIATION = .25f;
    public static final float DEFAULT_WEIGHT = 1f;
    public static final double SCALING_FACTOR = 1000.0;
    private JPanel _panel1;
    private MultiPointSlider _weightSlider;
    private MultiPointSlider _centerSlider;
    private MultiPointSlider _standardDeviationSlider;
    private JPanel _previewPanel;
    private JLabel _currentWeightLabel;
    private JLabel _currentCenteredAtLabel;
    private JLabel _currentStdDevLabel;
    private JPanel _weightPanel;
    private JPanel _weightInnerPanel;
    private JPanel _centerPanel;
    private JPanel _centerInnerPanel;
    private JPanel _standardDeviationPanel;
    private JPanel _standardDeviationInnerPanel;
    private JPanel _outerPanel;

    @SuppressWarnings({ "UnusedDeclaration", "MismatchedReadAndWriteOfArray" })
    private JPanel[] _horizontalPanels = {
            _panel1,
            _outerPanel,
            _weightInnerPanel,
            _centerInnerPanel,
            _standardDeviationInnerPanel
    };

    @SuppressWarnings({ "UnusedDeclaration", "MismatchedReadAndWriteOfArray" })
    private JPanel[] _verticalPanels = {
            _weightPanel,
            _centerPanel,
            _standardDeviationPanel
    };

    private final java.util.List<ChangeListener> _changeListeners = new LinkedList<ChangeListener>();

    @SuppressWarnings("StaticVariableNamingConvention")
    private static final Dictionary<Integer,MpsLabel> S_0TO_1BY_QUARTERS_LABELS;
    private GaussianDistributionDrawing _gdd;

    static {

        @SuppressWarnings({ "MagicNumber", "UseOfObsoleteCollectionType" })
        Hashtable<Integer,MpsLabel> ht = MpsLabel.makeLabels( 0, 1000, 250, 3 );
        S_0TO_1BY_QUARTERS_LABELS = ObtuseUtil5.unmodifiableHashtable( ht );

    }

    @SuppressWarnings("UnusedDeclaration")
    public GaussianDistributionConfiguratorWidget() {

        this(
                GaussianDistributionConfiguratorWidget.DEFAULT_WEIGHT,
                GaussianDistributionConfiguratorWidget.DEFAULT_CENTER,
                GaussianDistributionConfiguratorWidget.DEFAULT_STANDARD_DEVIATION
        );

    }

    public GaussianDistributionConfiguratorWidget( float weight, float center, float stdDev ) {

        super();

//        for ( JPanel panel : _verticalPanels ) {
//
//            panel.setLayout( new BoxLayout( panel, BoxLayout.Y_AXIS ) );
//
//        }
//        for ( JPanel panel : _horizontalPanels ) {
//
//            panel.setLayout( new BoxLayout( panel, BoxLayout.X_AXIS ) );
//
//        }

        //noinspection ThisEscapedInObjectConstruction
        setLayout( new BoxLayout( this, BoxLayout.X_AXIS ) );
        add( _panel1 );

        _previewPanel.setLayout( new BoxLayout( _previewPanel, BoxLayout.X_AXIS ) );
        _gdd = new GaussianDistributionDrawing( new GaussianDistribution( center, stdDev ) );
        _previewPanel.add( _gdd );

        //noinspection MagicNumber
        configureSlider( _weightSlider, 0, 1000, (int)( weight * 1000 ), 250, 0, null );
        //noinspection MagicNumber
        configureSlider(
                _centerSlider,
                0,
                1000,
                (int)( center * 1000 ),
                250,
                0,
                GaussianDistributionConfiguratorWidget.S_0TO_1BY_QUARTERS_LABELS
        );
        //noinspection MagicNumber
        configureSlider(
                _standardDeviationSlider,
                0,
                1000,
                (int)( stdDev * 1000 ),
                250,
                0,
                GaussianDistributionConfiguratorWidget.S_0TO_1BY_QUARTERS_LABELS
        );

        _weightSlider.getModel().addChangeListener(
                new ChangeListener() {

                    public void stateChanged( ChangeEvent changeEvent ) {

                        Logger.logMsg(
                                "weight changed:  " +
                                "value = " + _weightSlider.getModel().getValue() + ", " +
                                "min = " + _weightSlider.getModel().getMinimum() + ", " +
                                "max = " + _weightSlider.getModel().getMaximum()
                        );

                        setDistribution();
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

        setDistribution();

    }

    public double getWeight() {

        return _weightSlider.getValue() / GaussianDistributionConfiguratorWidget.SCALING_FACTOR;

    }

    public double getCenter() {

        return _centerSlider.getValue() / GaussianDistributionConfiguratorWidget.SCALING_FACTOR;

    }

    public double getStandardDeviation() {

        return _standardDeviationSlider.getValue() / GaussianDistributionConfiguratorWidget.SCALING_FACTOR;

    }

    public WeightedGaussianDistribution getProportionalGaussianDistribution() {

        return new WeightedGaussianDistribution( getWeight(), getCenter(), getStandardDeviation() );

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

        _gdd.setDistribution(
                new GaussianDistribution(
                        _centerSlider.getValue() / GaussianDistributionConfiguratorWidget.SCALING_FACTOR,
                        _standardDeviationSlider.getValue() / GaussianDistributionConfiguratorWidget.SCALING_FACTOR
                )
        );

        _currentWeightLabel.setText( "" + _weightSlider.getModel().getValue() );
        _currentCenteredAtLabel.setText(
                "" + ObtuseUtil5.lpad0(
                        _centerSlider.getModel().getValue() /
                        GaussianDistributionConfiguratorWidget.SCALING_FACTOR, 6, 3
                )
        );

        _currentStdDevLabel.setText(
                "" + ObtuseUtil5.lpad0(
                        _standardDeviationSlider.getModel().getValue() /
                        GaussianDistributionConfiguratorWidget.SCALING_FACTOR, 6, 3
                )
        );

    }

    @SuppressWarnings("SameParameterValue")
    private void configureSlider(
            MultiPointSlider slider,
            int minimum,
            int maximum,
            int value,
            int majorTicks,
            int minorTicks,
            @Nullable Dictionary<Integer, MpsLabel> labels
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

        _weightSlider = new MultiPointSlider( "weight", 1, 1000 );

        //noinspection MagicNumber
        _centerSlider = new MultiPointSlider( "center", 0, 1000, 25 );

        _standardDeviationSlider = new MultiPointSlider( "standard deviation", 1, 1000 );

    }

    public static void main( String[] args ) {

        BasicProgramConfigInfo.init( "Obtuse", "Shared", "StandardDeviationConfigurationWidget", null );

        JFrame xx = new JFrame();
        JPanel jp = new JPanel();
        jp.setLayout( new BoxLayout( jp, BoxLayout.Y_AXIS ) );
        xx.setContentPane( jp );
        @SuppressWarnings("MagicNumber")
        final GaussianDistributionConfiguratorWidget[] widgets = {
                new GaussianDistributionConfiguratorWidget( .02f, .025f, .025f / 3 ),
                new GaussianDistributionConfiguratorWidget( .2f, .1f, .05f / 2 ),
                new GaussianDistributionConfiguratorWidget( .35f, .2f, .2f / 2 ),
                new GaussianDistributionConfiguratorWidget( .43f, .3f, .15f / 2 )
        };

        final StackedGaussianDistributionsDrawing pgdd = new StackedGaussianDistributionsDrawing();
        pgdd.setMinimumSize( new Dimension( 100, 100 ) );
        pgdd.setPreferredSize( new Dimension( 100, 100 ) );
        pgdd.setDistributions(
                new WeightedGaussianDistribution[] {
                        widgets[0].getProportionalGaussianDistribution(),
                        widgets[1].getProportionalGaussianDistribution(),
                        widgets[2].getProportionalGaussianDistribution(),
                        widgets[3].getProportionalGaussianDistribution()
                }
        );
        jp.add( pgdd );

        for ( GaussianDistributionConfiguratorWidget widget : widgets ) {

            widget.addChangeListener(
                    new ChangeListener() {

                        public void stateChanged( ChangeEvent changeEvent ) {

                            pgdd.setDistributions(
                                    new WeightedGaussianDistribution[] {
                                            widgets[0].getProportionalGaussianDistribution(),
                                            widgets[1].getProportionalGaussianDistribution(),
                                            widgets[2].getProportionalGaussianDistribution(),
                                            widgets[3].getProportionalGaussianDistribution()
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
