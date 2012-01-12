package com.obtuse.util;

/*
 * Copyright © 2008 Loa Corporation
 * Copyright © 2011 Daniel Boulet
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * Manage the pressed and unpressed versions of an icon/image used as a button.
 * <p/>
 */

@SuppressWarnings( { "UnusedDeclaration" } )
public class ButtonInfo {

    private final JLabel _button;
    private final ImageIcon _pressedIcon;
    private final ImageIcon _unpressedIcon;
    private final Runnable _action;
    private static float _defaultDarkeningFactor = 0.8f;

    private ButtonInfo( JLabel button, ImageIcon pressedIcon, ImageIcon unpressedIcon, Runnable action ) {
        super();

        _button = button;
        _pressedIcon = pressedIcon;
        _unpressedIcon = unpressedIcon;
        _action = action;

    }

    public static void setDefaultDarkeningFactor( float factor ) {

        _defaultDarkeningFactor = factor;

    }

    public static float getDefaultDarkeningFactor() {

        return _defaultDarkeningFactor;

    }

    public Runnable getAction() {

        return _action;

    }

    public ImageIcon getPressedIcon() {

        return _pressedIcon;

    }

    public ImageIcon getUnpressedIcon() {

        return _unpressedIcon;

    }

    public JLabel getButton() {

        return _button;

    }

    public static ButtonInfo makeButtonLabel(
                final ButtonOwner buttonOwner,
                JLabel button,
                Runnable action,
                String buttonName
    ) {

        return makeButtonLabel(
                buttonOwner,
                button,
                action,
                buttonName,
                ImageIconUtils.getDefaultResourceBaseDirectory(),
                _defaultDarkeningFactor
        );

    }

    public static ButtonInfo makeButtonLabel(
            final ButtonOwner buttonOwner,
            JLabel button,
            Runnable action,
            String buttonName,
            String resourceBaseDirectory,
            float darkeningFactor
    ) {

        ImageIcon unpressedIcon = ImageIconUtils.fetchIconImage(
                "button-" + buttonName + ".png",
                0,
                resourceBaseDirectory
        );

        // Create a somewhat darker version of the unpressed icon.

        ImageIcon pressedIcon = new ImageIcon(
                ImageIconUtils.changeImageBrightness( unpressedIcon.getImage(), darkeningFactor )
        );


//        ImageIcon pressedIcon = IconImageUtils.fetchIconImage( "button-" + buttonName + "-pressed.png" );

        return makeButtonLabel( buttonOwner, button, action, unpressedIcon, pressedIcon );

    }

    public static ButtonInfo makeButtonLabel(
            final ButtonOwner buttonOwner,
            JLabel button,
            Runnable action,
            ImageIcon unpressedIcon,
            ImageIcon pressedIcon
    ) {

        int width = Math.max( pressedIcon.getIconWidth(), unpressedIcon.getIconWidth() );
        int height = Math.max( pressedIcon.getIconHeight(), unpressedIcon.getIconHeight() );

        final ButtonInfo bi = new ButtonInfo( button, pressedIcon, unpressedIcon, action );

        button.addMouseListener(
                new MouseListener() {
                    public void mouseClicked( MouseEvent mouseEvent ) {

                        if ( bi.getButton().isEnabled() ) {

                            bi.getAction().run();
                            buttonOwner.setButtonStates();

                        }

                    }

                    public void mousePressed( MouseEvent mouseEvent ) {

                        if ( bi.getPressedIcon() != null && bi.getButton().isEnabled() ) {

                            bi.getButton().setIcon( bi.getPressedIcon() );

                        }

                    }

                    public void mouseReleased( MouseEvent mouseEvent ) {

                        if ( bi.getButton().isEnabled() ) {

                            bi.getButton().setIcon( bi.getUnpressedIcon() );

                        }

                    }

                    public void mouseEntered( MouseEvent mouseEvent ) {

                        if ( bi.getButton().isEnabled() ) {

                            buttonOwner.setCursor( Cursor.getPredefinedCursor( Cursor.HAND_CURSOR ) );

                        }

                    }

                    public void mouseExited( MouseEvent mouseEvent ) {

                        if ( bi.getButton().isEnabled() ) {

                            bi.getButton().setIcon( bi.getUnpressedIcon() );
                            buttonOwner.setCursor( Cursor.getDefaultCursor() );

                        }

                    }

                }
        );

        button.setIcon( bi.getUnpressedIcon() );
        button.setText( null );
        button.setMinimumSize( new Dimension( width, height ) );
        button.setMaximumSize( new Dimension( width, height ) );

        return bi;
    }

}
