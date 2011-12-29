package com.obtuse.ui;

import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 * User: danny
 * Date: 2011/12/25
 * Time: 15:24
 * To change this template use File | Settings | File Templates.
 */
public class JSliderTest extends JFrame {

    private JSlider _slider1;
    private JSlider _slider2;
    private JSlider _slider3;
    private JSlider _slider4;
    private JPanel _panel;

    public JSliderTest() {
        super();
        setContentPane( _panel );

        pack();

    }

    public static void main( String[] args ) {

        JSliderTest test = new JSliderTest();
        test.setVisible( true );

    }
}
