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
package org.alma.obssm.ui;

import org.alma.obssm.Core;
import org.alma.obssm.Manager;
import org.alma.obssm.net.ElasticSearchImpl;
import org.alma.obssm.net.LineReader;
import org.alma.obssm.parser.Parser;
import org.alma.obssm.sm.StateMachineManager;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.alma.obssm.sm.DefaultEntryListener;
import org.alma.obssm.sm.EntryListener;

/**
 * This class run the interpreter via command line.
 *
 * @author Javier Fuentes j.fuentes.m@icloud.com
 * @version 0.4
 * @see UICoreActions
 * @see Core
 * @see Manager
 */
public class CommandLine {

    private final Manager m;

    public CommandLine(Manager m) {
        this.m = m;
    }

    /**
     * This method run the search using the provided parameters.
     *
     *
     * @param scxml State Machine SCXML document
     * @param log_translate JSON log translate document
     * @param query_filter JSON query filter document
     * @param ELK_server ElasticSearch URL (default LineReader)
     * @param listener EntryListener implementation via jar:class
     * @param linereader LineReader implementation via jar:class
     * @param dfrom TimeStamp from
     * @param dto TimeStamp To
     * @param query query to search.
     */
    public void initialize(String scxml,
            String log_translate,
            String query_filter,
            String ELK_server,
            final String listener,
            final String linereader,
            final String dfrom,
            final String dto, final String query) {

        try {
            if (log_translate == null) {
                m.parser = new Parser(m.getResourceFiles("log_translate.json")
                        .getAbsolutePath());
            } else {
                m.parser = new Parser(log_translate);
            }
            if (scxml == null) {
                m.smm = new StateMachineManager(m.getResourceFiles("model.xml")
                        .getAbsolutePath());
            } else {
                m.smm = new StateMachineManager(scxml);
            }
            if (query_filter == null) {
                m.default_query_base = m.getResourceString("query_base.json");
            } else {
                m.default_query_base = m.getFileToString(query_filter);
            }

            if (ELK_server != null) {
                m.ELKUrl = ELK_server;
            }

        } catch (IOException ex) {
            Logger.getLogger(CommandLine.class.getName())
                    .log(Level.SEVERE, "Problems reading configuration files,"
                            + " check the trace.", ex);
        }
        
        
        Class aux = null;
        if (listener != null) {
            if (listener.contains(":")) {
                String[] jarAndClass = listener.split(":");
                aux = m.getEntryListenerClassFromJar(jarAndClass[0], jarAndClass[1]);
            }
        }
        final Class cl;
        if (aux == null) {
            cl = EntryListener.class;
        } else {
            cl = aux;
        }

        Core.startSearch(new UICoreActions() {
            @Override
            public LineReader initialize() {
                return new ElasticSearchImpl(dfrom.replace(" ", "T"),
                        dto.replace(" ", "T"),
                        query,
                        m.default_query_base, m.ELKUrl);
            }

            @Override
            public Manager getManager() {
                return m;
            }

            @Override
            public void filesNotFound() {
            }

            @Override
            public void beforeStartCommunications() {
            }

            @Override
            public void afterStartCommunications() {
            }

            @Override
            public void actionsPerLine(String line) {
            }

            @Override
            public void loopEnd() {
            }

            @Override
            public void exceptions(Exception ex) {
                Logger.getLogger(CommandLine.class.getName())
                        .log(Level.SEVERE, "Check the trace", ex);
            }

            @Override
            public void cleanUp() {
            }

            /*
            @Override
            public EntryListener getEntryListener() {
                if (listener == null) return new DefaultEntryListener(m);
                if (!listener.contains(":")) return new DefaultEntryListener(m);
                try {
                    String[] jarAndClass = listener.split(":");
                    return m.getEntryListenerFromJar(jarAndClass[0], jarAndClass[1]);
                } catch (MalformedURLException | ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                    Logger.getLogger(CommandLine.class.getName()).
                            log(Level.SEVERE, "Problems reading jar/class, check the logs. Using DefaultListener instead: \n" + ex.getMessage(), ex);
                }
                return new DefaultEntryListener(m);
            }
             */
            @Override
            public EntryListener getEntryListener() {
                try {
                    Constructor c = cl.getConstructor(Manager.class);
                    EntryListener l = (EntryListener) c.newInstance(m);
                    return l;
                } catch (InstantiationException
                        | IllegalAccessException
                        | IllegalArgumentException
                        | InvocationTargetException
                        | NoSuchMethodException| SecurityException ex) {
                    Logger.getLogger(CommandLine.class.getName())
                            .log(Level.SEVERE, null, ex);
                }
                return new DefaultEntryListener(m);
            }
        });
    }

}
