/**
 * *****************************************************************************
 * ALMA - Atacama Large Millimeter Array
 * Copyright (c) AUI - Associated Universities Inc., 2016
 * (in the framework of the ALMA collaboration).
 * All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307  USA
 ******************************************************************************
 */
package org.alma.obssm;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import org.alma.obssm.net.ElasticSearchImpl;

import org.alma.obssm.ui.CommandLine;
import org.alma.obssm.ui.ObsSMPanel;
import org.alma.obssm.net.LineReader;
import org.alma.obssm.parser.Parser;
import org.alma.obssm.sm.DefaultEntryListener;
import org.alma.obssm.sm.EntryListener;
import org.alma.obssm.sm.StateMachineManager;

/**
 * This class manages all components, with the purpose of interconnecting
 * objects between them.
 *
 * This class must be instanced once, not more.
 *
 * TO DO: Allow to launch a console mode operation.
 *
 * @author Javier Fuentes Munoz j.fuentes.m@icloud.com
 * @version 1.0
 * @see StateMachineManager
 * @see LineReader
 * @see ObsSMPanel
 * @see Parser
 *
 */
public class Manager {

    /**
     * Global StateMachine Manager, provides interaction with all the
     * StateMachines.
     */
    public StateMachineManager smm;

    /**
     * Global LineReader implementation. It's required to read data.
     */
    public LineReader lr;

    /**
     * Global default GUI
     */
    public ObsSMPanel osmPanel;

    /**
     * Global parser, reads constraints and logs to match transitions.
     */
    public Parser parser;

    /**
     * Global command line UI
     */
    public CommandLine cmdLine;

    /**
     * Default global query base
     *
     * @see ElasticSearchImpl
     */
    public String default_query_base;

    /**
     * Default ElasticSearch Url
     *
     * @see ElasticSearchImpl
     */
    public String ESUrl;

    /**
     * This constructor does not do nothing.
     */
    public Manager() {
        this.ESUrl = "http://elk-master.osf.alma.cl:9200";
        this.default_query_base = "";
    }

    /**
     * Shortcut to create an instance.
     *
     * @return CommandLine instance.
     */
    public ObsSMPanel launchPanel() {
        //Remove customs styles for java swing. Metal!!
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
        } catch (UnsupportedLookAndFeelException | ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
            if (Run.VERBOSE)
                Logger.getLogger(Manager.class.getName()).log(Level.SEVERE, null, ex);
        }
        osmPanel = new ObsSMPanel(this);
        return osmPanel;
    }

    /**
     * Shortcut to create an instance.
     *
     * @return CommandLine instance
     */
    public CommandLine launchCommandLine() {
        cmdLine = new CommandLine(this);
        return cmdLine;
    }

    /**
     *
     * Returns a temporal file for a internal resource.
     *
     * @param file
     * @return temporal file.
     * @throws IOException
     */
    public File getResourceFiles(String file) throws IOException {
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(file);
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        File f = File.createTempFile("temp-file", ".temp");

        try (FileWriter fw = new FileWriter(f)) {
            String tempLine;

            while (true) {
                tempLine = br.readLine();
                if (tempLine == null) {
                    break;
                }
                fw.append(tempLine);
            }
        }

        return f;
    }

    /**
     *
     * Return a String with the contents of the resource file.
     *
     * @param file
     * @return
     * @throws IOException
     */
    public String getResourceString(String file) throws IOException {
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(file);
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String tempLine;
        StringBuilder sb = new StringBuilder();
        while (true) {
            tempLine = br.readLine();
            if (tempLine == null) {
                break;
            }
            sb.append(tempLine);
        }
        return sb.toString();
    }

    /**
     * Returns the contents of a File as String.
     *
     * @param file to be read.
     * @return String with the contents of the file
     * @throws IOException
     */
    public String getFileToString(String file) throws IOException {
        File f = new File(file);
        BufferedReader br = new BufferedReader(new FileReader(f));
        String tempLine;
        StringBuilder sb = new StringBuilder();
        while (true) {
            tempLine = br.readLine();
            if (tempLine == null) {
                break;
            }
            sb.append(tempLine);
        }
        return sb.toString();
    }

    /**
     * Returns a Custom EntryListener from an external Jar
     *
     * @param jarUrl
     * @param className
     * @return Custom EntryListener
     * @throws MalformedURLException
     * @throws ClassNotFoundException
     * @throws NoSuchMethodException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     */
    public EntryListener getEntryListenerFromJar(String jarUrl, String className)
            throws MalformedURLException,
            ClassNotFoundException,
            NoSuchMethodException,
            InstantiationException,
            IllegalAccessException,
            IllegalArgumentException,
            InvocationTargetException {

        EntryListener l;
        URLClassLoader loader = new URLClassLoader(
                new URL[]{new File(jarUrl).toURI().toURL()},
                getClass().getClassLoader());

        Constructor c = loader.loadClass(className).getConstructor(Manager.class);
        l = (EntryListener) c.newInstance(this);

        return l;
    }

    /**
     * Returns a Class object, given a jar url and the class name.
     *
     * <i>it does not check the constructor or types.</i>
     *
     * @param jarUrl
     * @param className
     * @return
     */
    public Class getEntryListenerClassFromJar(String jarUrl, String className) {
        try {
            URLClassLoader loader = new URLClassLoader(
                    new URL[]{new File(jarUrl).toURI().toURL()},
                    getClass().getClassLoader());

            Class cl = loader.loadClass(className);
            return cl;
        } catch (ClassNotFoundException | MalformedURLException ex) {
            Logger.getLogger(Manager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return DefaultEntryListener.class;
    }

    /**
     * Returns a custom LineReader from an external Jar
     *
     * @param jarUrl
     * @param className
     * @return
     * @throws MalformedURLException
     * @throws ClassNotFoundException
     * @throws NoSuchMethodException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     */
    public LineReader getLineReaderFromJar(String jarUrl, String className)
            throws MalformedURLException,
            ClassNotFoundException,
            NoSuchMethodException,
            InstantiationException,
            IllegalAccessException,
            IllegalArgumentException,
            InvocationTargetException {

        LineReader r;
        URLClassLoader loader = new URLClassLoader(
                new URL[]{new File(jarUrl).toURI().toURL()},
                getClass().getClassLoader());

        Constructor c = loader.loadClass(className).getConstructor(Manager.class);
        r = (LineReader) c.newInstance(this);

        return r;
    }
}
