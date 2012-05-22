package com.obtuse.ui;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.io.File;
import java.io.FilenameFilter;

/*
 * Copyright © 2012 Daniel Boulet
 */

/**
 * Some utility methods that make using Swing and AWT file selection dialogs a bit easier.
 * <p/>
 * Possibly of more interest, these also provide examples of how to use the file selection dialogs.
 */

@SuppressWarnings("UnusedDeclaration")
public class FileSelectors {

    private FileSelectors() {
        super();

    }

    /**
     * Use the Swing {@link javax.swing.JFileChooser} to select a file.
     * @param parent the component that is requesting this dialog (ignored if null).
     * @param title the title for the dialog window and the approve button text.
     * @param startingDirectory where the game should begin (defaults to the user's home directory if null).
     * @param dialogType the type of dialog.
     * See {@link javax.swing.JFileChooser#setDialogType} for more info.
     * @param fileFilter the optional file filter which selects which files should appear in the dialog.
     * @return the selected file if the "approve" button was clicked; null otherwise.
     */

    public static File swingSelectFile( Component parent, String title, File startingDirectory, int dialogType, FileFilter fileFilter ) {

        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle( title );
        chooser.setApproveButtonText( title );
        chooser.setDialogType( dialogType );

        if ( fileFilter != null ) {

            chooser.setFileFilter( fileFilter );

        }

//        chooser.setFileFilter(
//                new javax.swing.filechooser.FileFilter() {
//                    @SuppressWarnings({ "ParameterNameDiffersFromOverriddenParameter" })
//                    @Override
//                    public boolean accept( File file ) {
//
//                        try {
//
//                            File canonicalFile = file.getCanonicalFile();
//
//                            return file.isDirectory() || canonicalFile.getName().toLowerCase().endsWith( ".xml" );
//
//                        } catch ( IOException e ) {
//
//                            return false;
//
//                        }
//
//                    }
//
//                    @Override
//                    public String getDescription() {
//
//                        return ".xml files";
//                    }
//                }
//        );

        chooser.setFileSelectionMode( JFileChooser.FILES_ONLY );
        int rval = chooser.showSaveDialog( parent );
        if ( rval == JFileChooser.APPROVE_OPTION ) {

            return chooser.getSelectedFile();

        } else {

            return null;

        }

    }

    /**
     * Use the AWT {@link java.awt.FileDialog} to select a file.
     * @param parent the {@link java.awt.Frame} that is requesting this dialog (ignored if null).
     * @param title the title for the dialog window.
     * @param startingDirectory where the game should begin (defaults to the user's home directory if null).
     * @param mode the mode of the dialog.
     * See {@link java.awt.FileDialog#setMode} for more info.
     * @param filenameFilter the optional file filter which selects which files should appear in the dialog.
     * @return the selected file if the "approve" button was clicked; null otherwise.
     */

    public static File awtSelectFile( Frame parent, String title, File startingDirectory, int mode, FilenameFilter filenameFilter ) {

        return FileSelectors.awtSelectFile( new FileDialog( parent, title, mode ), startingDirectory, filenameFilter );

    }

    /**
     * Use the AWT {@link java.awt.FileDialog} to select a file.
     * @param parent the {@link java.awt.Dialog} that is requesting this dialog (ignored if null).
     * @param title the title for the dialog window.
     * @param startingDirectory where the game should begin (defaults to the user's home directory if null).
     * @param mode the mode of the dialog.
     * See {@link java.awt.FileDialog#setMode} for more info.
     * @param filenameFilter the optional file filter which selects which files should appear in the dialog.
     * @return the selected file if the "approve" button was clicked; null otherwise.
     */

    public static File awtSelectFile( Dialog parent, String title, File startingDirectory, int mode, FilenameFilter filenameFilter ) {

        return FileSelectors.awtSelectFile( new FileDialog( parent, title, mode ), startingDirectory, filenameFilter );

    }

    /**
     * Use the AWT {@link java.awt.FileDialog} to select a file.
     * <p/>
     * This is a utility method used by the other two awtSelectFile methods to do the real work of selecting a file
     * once the {@link java.awt.FileDialog} instance has been created.  It is probably not very useful to anyone else but there
     * does not seem to be much harm in making it public.
     * @param dialog the FileDialog which is to be used to select a file.
     * @param startingDirectory where the game should begin (defaults to the user's home directory if null).
     * @param filenameFilter the optional file filter which selects which files should appear in the dialog.
     * @return the selected file if the "approve" button was clicked; null otherwise.
     */

    public static File awtSelectFile( FileDialog dialog, File startingDirectory, FilenameFilter filenameFilter ) {

        if ( filenameFilter != null ) {

            dialog.setDirectory( startingDirectory.getAbsolutePath() );

        }

//        dialog.setFilenameFilter(
//                new FilenameFilter() {
//
//                    public boolean accept( File dir, String fileName ) {
//
//                        File file = new File( dir, fileName );
//                        try {
//
//                            File canonicalFile = file.getCanonicalFile();
//
//                            return file.isDirectory() || canonicalFile.getName().toLowerCase().endsWith( ".xml" );
//
//                        } catch ( IOException e ) {
//
//                            return false;
//
//                        }
//
//                    }
//
//                }
//        );

        dialog.setVisible( true );

        String fileName = dialog.getFile();
        if ( fileName == null ) {

            return null;

        } else {

            String directory = dialog.getDirectory();

            return new File( new File( directory ), fileName );

        }

    }

}
