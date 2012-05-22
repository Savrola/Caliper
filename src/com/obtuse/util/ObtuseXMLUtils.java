package com.obtuse.util;

import com.obtuse.util.exceptions.ObtuseXmlNodeException;
import com.obtuse.util.exceptions.ParsingException;
import org.jetbrains.annotations.Nullable;
import org.w3c.dom.*;

import java.io.PrintStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

/*
 * Copyright © 2012 Daniel Boulet
 */

@SuppressWarnings("UnusedDeclaration")
public class ObtuseXMLUtils {

    public static interface CheckNode {

        /**
         * Determine if a candidate node is the target node.
         * @param candidateNode the candidate node.
         * @return true if this is the target node; false otherwise.
         */

        boolean isThisTheNode( Node candidateNode );

        /**
         * Deal with the failure to find the target node.
         * @return null what the find operation should return (presumably <tt>null</tt> but any alternative Node works too).
         * @throws com.obtuse.util.exceptions.ObtuseXmlNodeException if that's how the failure is to be dealt with.
         */

        Node noteFailure() throws ObtuseXmlNodeException;

    }

    private ObtuseXMLUtils() {
        super();

    }

    public static void dumpXmlDocument( PrintStream ps, Document doc ) {

        ps.println( "document name is \"" + doc.getNodeName() + "\"" );
        NodeList contents = doc.getChildNodes();
        for ( int ix = 0; ix < contents.getLength(); ix += 1 ) {

            Node node = contents.item( ix );
            ObtuseXMLUtils.dumpXmlNode( ps, node, 1 );

        }

    }

    public static void dumpXmlNode( PrintStream ps, Node node, int depth ) {

        if ( "#text".equals( node.getNodeName() ) ) {

            if ( node.getTextContent().trim().isEmpty() ) {

//                ps.println( ObtuseUtil5.replicate( "   ", depth ) + "node is \"" + node.getNodeName() + "\" (whitespace)" );

            } else {

                ps.println(
                        ObtuseUtil5.replicate( "   ", depth ) + "node is \"" + node.getNodeName() + "\" (text = \"" + node.getTextContent().trim() + "\")"
                );

            }

            if ( node.getChildNodes().getLength() == 0 && !node.hasAttributes() ) {

                return;

            }

        } else {

            ps.println( ObtuseUtil5.replicate( "   ", depth ) + "node is \"" + node.getNodeName() + "\"" );

        }

        if ( node.hasAttributes() ) {

            NamedNodeMap nodeMap = node.getAttributes();
            if ( nodeMap == null ) {

                Logger.logErr(
                        "we were supposed to find attributes but got nothing in node \"" + node.getNodeName() + "\""
                );

            } else {

                for ( int ix = 0; ix < nodeMap.getLength(); ix += 1 ) {

                    Node item = nodeMap.item( ix );
                    ps.println(
                            ObtuseUtil5.replicate( "   ", depth ) +
                            item.getNodeName() + "=" + item.getNodeValue() // + " (type " + item.getNodeType() + ")"
                    );

                }

            }

        }

        NodeList contents = node.getChildNodes();
        for ( int ix = 0; ix < contents.getLength(); ix += 1 ) {

            Node childNode = contents.item( ix );
            ObtuseXMLUtils.dumpXmlNode( ps, childNode, depth + 1 );

        }

    }

    public static double[] getDoubleArray( Node parentNode, String targetNodeName )
            throws ObtuseXmlNodeException {

        Node arrayNode = ObtuseXMLUtils.findNode( parentNode, targetNodeName );
        NodeList elements = arrayNode.getChildNodes();
        int arrayLength = 0;
        for ( int ix = 0; ix < elements.getLength(); ix += 1 ) {

            Node element = elements.item( ix );
            if ( "item".equals( element.getNodeName() ) ) {

                arrayLength += 1;

            }

        }

        double[] rval = new double[arrayLength];
        int elementIx = 0;
        for ( int ix = 0; ix < elements.getLength(); ix += 1 ) {

            Node element = elements.item( ix );
            if ( "item".equals( element.getNodeName() ) ) {

                String elementString = element.getFirstChild().getNodeValue();
                Logger.logMsg( "got an element \"" + elementString + "\"" );
                try {

                    rval[elementIx] = Double.parseDouble( elementString );
                    elementIx += 1;

                } catch ( NumberFormatException e ) {

                    throw new ObtuseXmlNodeException( "array element " + elementIx + " cannot be parsed as a double", elementIx, e );

                }

            }

        }

        return rval;

    }

    public static double getMandatoryDoubleAttributeValue( Node node, String attributeName )
            throws ObtuseXmlNodeException {

        return ObtuseXMLUtils.getDoubleAttributeValue( node, attributeName, true ).doubleValue();

    }

    public static Double getDoubleAttributeValue( Node node, String attributeName, boolean mandatory )
            throws ObtuseXmlNodeException {

        String attributeValue = ObtuseXMLUtils.getAttributeValue( node, attributeName, mandatory );
        if ( attributeValue == null ) {

            return null;

        }

        try {

            return Double.parseDouble( attributeValue );

        } catch ( NumberFormatException e ) {

            throw new ObtuseXmlNodeException(
                    "attribute " + attributeName + "'s value \"" + attributeValue + "\" in " +
                    node.getNodeName() + " node cannot be parsed as a double",
                    e
            );

        }

    }

    public static int getMandatoryIntegerAttributeValue( Node node, String attributeName )
            throws ObtuseXmlNodeException {

        return ObtuseXMLUtils.getIntegerAttributeValue( node, attributeName, true ).intValue();

    }

    public static Integer getIntegerAttributeValue( Node node, String attributeName, boolean mandatory )
        throws ObtuseXmlNodeException {

        String attributeValue = ObtuseXMLUtils.getAttributeValue( node, attributeName, mandatory );
        if ( attributeValue == null ) {

            return null;
        }

        try {

            return Integer.parseInt( attributeValue );

        } catch ( NumberFormatException e ) {

            throw new ObtuseXmlNodeException(
                    "attribute " + attributeName + "'s value \"" + attributeValue + "\" in " +
                    node.getNodeName() + " node cannot be parsed as an int",
                    e
            );

        }

    }

    public static FormattedImmutableDate getDateTimeAttributeValue( Node node, String attributeName, boolean mandatory )
            throws ObtuseXmlNodeException {

        String attributeValue = ObtuseXMLUtils.getAttributeValue( node, attributeName, mandatory );
        if ( attributeValue == null ) {

            return null;

        }

        try {

            @SuppressWarnings("UnnecessaryLocalVariable")
            ImmutableDate rval = DateUtils.parseYYYY_MM_DD_HH_MM( attributeValue, 0 );
            return new FormattedImmutableDate( rval );

        } catch ( ParsingException e ) {

            throw new ObtuseXmlNodeException(
                    "attribute \"" + attributeName + "\" in node \"" + node.getNodeName() +
                    "\" is not a date time in \"yyyy-mm-dd hh:mm\" format (value=\"" + attributeValue + "\")"
            );

        }

    }

    public static String getAttributeValue( Node node, String attributeName, boolean mandatory )
            throws ObtuseXmlNodeException {

        NamedNodeMap attributes = node.getAttributes();
        Node attribute = attributes.getNamedItem( attributeName );

        if ( attribute == null ) {

            if ( mandatory ) {

                throw new ObtuseXmlNodeException( "attribute " + attributeName + " not found in " + node.getNodeName() + " node." );

            } else {

                return null;

            }

        }

        @SuppressWarnings("UnnecessaryLocalVariable")
        String attributeValue = attribute.getNodeValue();
        return attributeValue;

    }

//    @NotNull
//    public static Node findAttribute( Node node, String attributeName )
//            throws ObtuseXmlNodeException {
//
//        if ( node.hasAttributes() ) {
//
//            NamedNodeMap nodeMap = node.getAttributes();
//            if ( nodeMap == null ) {
//
//                throw new HowDidWeGetHereError(
//                        "Attribute \"" + attributeName + "\" not found in node \"" + node.getNodeName() + "\" " +
//                        "(found no attributes but node was supposed to have attributes)."
//                );
//
//            } else {
//
//                for ( int ix = 0; ix < nodeMap.getLength(); ix += 1 ) {
//
//                    Node candidateItem = nodeMap.item( ix );
//                    if ( attributeName.equals( candidateItem.getNodeName() ) ) {
//
//                        return candidateItem;
//
//                    }
//
//                }
//
//                throw new ObtuseXmlNodeException(
//                        "Attribute \"" + attributeName + "\" not found in node \"" + node.getNodeName() + "\""
//                );
//
//            }
//
//        }
//
//        throw new ObtuseXmlNodeException(
//                "Attribute \"" + attributeName + "\" not found in node \"" + node.getNodeName() + "\" without attributes."
//        );
//
//    }

    public static Collection<InstanceFromXML> getInstancesFromXML(
            MessageProxy messageProxy,
            Node parentNode,
            String targetNodeName,
            String elementNodeName,
            Class<? extends InstanceFromXML> elementClass
    ) throws ObtuseXmlNodeException {

        Collection<InstanceFromXML> rval = new LinkedList<InstanceFromXML>();
        Node arrayNode = ObtuseXMLUtils.findNode( parentNode, targetNodeName );
        NodeList elements = arrayNode.getChildNodes();
        int elementIx = 0;
        for ( int ix = 0; ix < elements.getLength(); ix += 1 ) {

            Node element = elements.item( ix );
            if ( elementNodeName.equals( element.getNodeName() ) ) {

//                String elementString = element.getFirstChild().getNodeValue();
//                Logger.logMsg( "got an element \"" + elementString + "\"" );
                try {

                    rval.add(
                            ObtuseXMLUtils.loadInstanceFromXML(
                                    messageProxy,
                                    null,
                                    element,
                                    elementClass.getPackage(),
                                    elementClass,
                                    "element"
                            )
                    );
                    elementIx += 1;

                } catch ( NumberFormatException e ) {

                    throw new ObtuseXmlNodeException( "array element " + elementIx + " cannot be parsed as a double", elementIx, e );

                }

            }

        }

        return rval;

    }

    public static InstanceFromXML getInstanceFromXML( MessageProxy messageProxy, Node parentNode, String targetNodeName, String name )
            throws ObtuseXmlNodeException {

        Node instance = ObtuseXMLUtils.findNode( parentNode, targetNodeName );
        if ( instance == null ) {

            throw new ObtuseXmlNodeException( targetNodeName + " node not found in " + parentNode.getNodeName() + " " +
                                              "node." );

        }

        return ObtuseXMLUtils.loadInstanceFromXML(
                messageProxy,
                parentNode,
                instance,
                InstanceFromXML.class.getPackage(),
                InstanceFromXML.class,
                name
        );

    }

    public static InstanceFromXML loadInstanceFromXML(
            MessageProxy messageProxy,
            @Nullable
            Node parent,
            Node targetNode,
            @Nullable
            Package optionalExpectedPackage,
            Class<? extends InstanceFromXML> expectedClass,
            String name
    ) {

        return ObtuseXMLUtils.loadInstanceFromXML(
                messageProxy,
                parent,
                targetNode,
                optionalExpectedPackage == null ? null : new Package[] { optionalExpectedPackage },
                expectedClass,
                name
        );

    }
    public static InstanceFromXML loadInstanceFromXML(
            MessageProxy messageProxy,
            @Nullable
            Node parent,
            Node targetNode,
            @Nullable
            Package[] optionalExpectedPackages,
            Class<? extends InstanceFromXML> expectedClass,
            String name
    ) {

        Package[] expectedPackages = optionalExpectedPackages == null ?
                new Package[]{ expectedClass.getPackage() }
                :
                optionalExpectedPackages;

        String targetNodeName = targetNode.getNodeName();

//        targetNodeName = "com.invidi.madison.util." + targetNodeName;

        Class<?> targetClass = null;

        for ( Package targetPackage : expectedPackages ) {

            try {

                targetClass = Class.forName(
                        targetPackage.getName() + '.' + targetNodeName
                );
                break;

            } catch ( ClassNotFoundException e ) {

                // Not there - try somewhere else

            }

        }

        if ( targetClass == null ) {

            String msg;
            if ( expectedPackages.length == 1 ) {

                msg = "The " + name + " class must be in the " + expectedPackages[0].getName() + " package.";

            } else {

                msg = "The " + name + " class must be in one of the following packages:<blockquote>";
                for ( Package targetPackage : expectedPackages ) {

                    msg += targetPackage.getName();

                }
                msg += "</blockquote>";

            }

            messageProxy.error(
                    "Unknown/unsupported " + name + ":  " + targetNodeName,
                    msg,
                    "Ok"
            );

            return null;

        }

        InstanceFromXML configClassInstance = null;
        try {

//            Class<?> targetClass = Class.forName(
//                    expectedPackages.getName() + '.' + targetNodeName
//            );
            Constructor<?> constructor;
            if ( parent == null ) {

                constructor = targetClass.getConstructor( MessageProxy.class, Node.class, Node.class );
                configClassInstance = (InstanceFromXML)constructor.newInstance(
                        messageProxy,
                        parent,
                        targetNode
                );

            } else {

                constructor = targetClass.getConstructor( MessageProxy.class, Node.class );
                configClassInstance = (InstanceFromXML)constructor.newInstance(
                        messageProxy,
                        parent
                );

            }

            if ( !expectedClass.isInstance( configClassInstance ) ) {

                messageProxy.error(
                        "Restoration of " + targetNodeName + " yielded the wrong class of object.",
                        "Expected to get a " + expectedClass.getSimpleName() + " but got a " +
                        configClassInstance.getClass().getSimpleName() + " instead.<br>" +
                        "Please notify Danny (provide him with the .xml file you just tried to load).",
                        "I Promise To Provide Danny With A Copy Of The XML File That I Just Tried To Load"
                );

                return null;

            }

//        } catch ( ClassNotFoundException e ) {
//
//            messageProxy.error(
//                    "Unknown/unsupported " + name + ":  " + targetNodeName,
//                    "The " + name + " classes must be in the com.invidi.madison.util package.",
//                    "Ok"
//            );

        } catch ( NoSuchMethodException e ) {

            messageProxy.error(
                    "The " + name + " class " + targetNodeName + " does not support recovery from XML.",
                    "Please notify Danny (provide him with the .xml file you just tried to load).",
                    "I Promise To Provide Danny With A Copy Of The XML File That I Just Tried To Load"
            );

        } catch ( InvocationTargetException e ) {

            Throwable cause = e.getCause();
            if ( cause instanceof ObtuseXmlNodeException ) {

                messageProxy.error(
                        "Unable to create " + targetNodeName + " instance using provided .xml file.",
                        ObtuseXMLUtils.formatCause( cause ) + "<br>" +
                        "The .xml configuration save file is probably out-of-date or contains a syntax error.<br>" +
                        "Please notify Danny if you conclude that something else is wrong.",
                        "I Promise To Provide Danny With A Copy Of The XML File If I Conclude That Something Else Is Wrong"
                );

            } else {

                messageProxy.error(
                        "Unable to create " + targetNodeName + " instance using provided .xml file.",
                        ObtuseXMLUtils.formatCause( cause ) + "<br>" +
                        "Please notify Danny.",
                        "I Promise To Provide Danny With A Copy Of The XML File That I Just Tried To Load"
                );

            }

        } catch ( InstantiationException e ) {

            messageProxy.error(
                    "Unable to create " + targetNodeName + " instance.",
                    e.getMessage() + "<br>" +
                    "Please notify Danny.",
                    "I Promise To Provide Danny With A Copy Of The XML File That I Just Tried To Load"
            );

        } catch ( IllegalAccessException e ) {

            messageProxy.error(
                    "Unable to create " + targetNodeName + " instance (illegal access exception).",
                    e.getMessage() + "<br>" +
                    "Please notify Danny.",
                    "I Promise To Provide Danny With A Copy Of The XML File That I Just Tried To Load"
            );

        }

        return configClassInstance;

    }

    private static String formatCause( Throwable cause ) {

        return (
                cause.getMessage() == null
                ? "No detail message provided."
                : "Detailed error message was:  " + cause.getMessage()
        ) + "<br>";

    }

    public static Node findNode( Node parentNode, Class<?> targetClass )
            throws ObtuseXmlNodeException {

        return ObtuseXMLUtils.findNode( parentNode, targetClass.getSimpleName() );

//        final String targetNodeName = targetClass.getSimpleName();
//        return ObtuseXMLUtils.findNode(
//                parentNode,
//                new CheckNode() {
//                    public boolean compare( String s1, String s2 ) {
//
//                        return targetNodeName.equals( s2 );
//
//                    }
//
//                    public String getDescription() {
//
//                        return targetNodeName;
//
//                    }
//
//                }
//
//        );

    }

    public static Node findNode( final Node parentNode, final String targetNodeName )
            throws ObtuseXmlNodeException {

        return ObtuseXMLUtils.findNode(
                parentNode,
                new CheckNode() {

                    public boolean isThisTheNode( Node candidateNode ) {

                        return targetNodeName.equals( candidateNode.getNodeName() );

                    }

                    public Node noteFailure()
                            throws ObtuseXmlNodeException {

                        throw new ObtuseXmlNodeException(
                                targetNodeName + " node not found in " + parentNode.getNodeName() + " node."
                        );

                    }

                }
        );

    }

    public static Node findNodeThatEndsWith( final Node parentNode, final String suffix )
            throws ObtuseXmlNodeException {

        return ObtuseXMLUtils.findNode(
                parentNode,
                new CheckNode() {

                    public boolean isThisTheNode( Node candidateNode ) {

                        return candidateNode.getNodeName().endsWith( suffix );

                    }

                    public Node noteFailure()
                            throws ObtuseXmlNodeException {

                        throw new ObtuseXmlNodeException(
                                "No node found with name ending with \"" + suffix + "\" in " +
                                parentNode.getNodeName() + " node."
                        );

                    }

                }
        );

    }

    public static Node findNode( Node parentNode, CheckNode test )
            throws ObtuseXmlNodeException {

        NodeList nodes = parentNode.getChildNodes();
        for ( int ix = 0; ix < nodes.getLength(); ix += 1 ) {

            Node candidateNode = nodes.item( ix );
//            Logger.logMsg( "candidate node is \"" + candidateNode + "\"" );
            // simpleClassName.equals( candidateNode.getNodeName() )
            if ( test.isThisTheNode( candidateNode ) ) {

                return candidateNode;

            }

        }

        return test.noteFailure();

    }

    public static String constructAttributeAssignment( String attributeName, String attributeValue, boolean mandatory )
            throws ObtuseXmlNodeException {

        if ( attributeValue == null ) {

            if ( mandatory ) {

                throw new ObtuseXmlNodeException( "required attribute \"" + attributeName + "\" not provided." );

            }

            return null;

        }

        return attributeName + "=\"" + attributeValue + "\"";

    }

    public static String constructAttributeAssignment( String attributeName, Double attributeValue, boolean mandatory )
            throws ObtuseXmlNodeException {

        if ( attributeValue == null ) {

            if ( mandatory ) {

                throw new ObtuseXmlNodeException( "required attribute \"" + attributeName + "\" not provided." );

            }

            return null;

        }

        return attributeName + "=\"" + attributeValue + "\"";

    }

    public static String constructAttributeAssignment( String attributeName, Integer attributeValue, boolean mandatory )
            throws ObtuseXmlNodeException {

        if ( attributeValue == null ) {

            if ( mandatory ) {

                throw new ObtuseXmlNodeException( "required attribute \"" + attributeName + "\" not provided." );

            }

            return null;

        }

        return attributeName + "=\"" + attributeValue + "\"";

    }

    public static String constructAttributeAssignment( String attributeName, Date attributeValue, boolean mandatory )
            throws ObtuseXmlNodeException {

        if ( attributeValue == null ) {

            if ( mandatory ) {

                throw new ObtuseXmlNodeException( "required attribute \"" + attributeName + "\" not provided." );

            }

            return null;

        }

        return attributeName + "=\"" + DateUtils.formatYYYY_MM_DD_HH_MM( attributeValue ) + "\"";

    }

}
