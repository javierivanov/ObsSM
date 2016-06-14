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
import org.alma.obssm.Run;
import org.alma.obssm.sm.DefaultEntryListener;
import org.alma.obssm.sm.EntryListener;
import org.apache.commons.scxml.model.Transition;
import org.apache.commons.scxml.model.TransitionTarget;

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
     * @param grep Log retrieve only
     * @param dfrom TimeStamp from
     * @param dto TimeStamp To
     * @param query query to search.
     */
    public void initialize(String scxml,
            String log_translate,
            String query_filter,
            String ELK_server,
            final String listener,
            final String grep,
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
                m.ESUrl = ELK_server;
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
            cl = DefaultEntryListener.class;
        } else {
            cl = aux;
        }

        Core.startSearch(new UICoreActions() {
            @Override
            public LineReader initialize() {
                ElasticSearchImpl out = new ElasticSearchImpl(dfrom.replace(" ", "T"),
                        dto.replace(" ", "T"),
                        query,
                        m.default_query_base, m.ESUrl);
                /**
                 * Checks if the grep option is ON. And parse options.
                 */
                if (grep != null) {
                    if (!grep.equals("")) {
                        out.setSpecialOutput(grep.split(":"));
                    } else {
                        out.setSpecialOutput(new String[]{});
                    }
                }
                return out;
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
                if (Run.VERBOSE) {
                    Logger.getLogger(CommandLine.class.getName())
                            .log(Level.INFO, "Before start Com");
                }
            }

            @Override
            public void afterStartCommunications() {
                if (Run.VERBOSE) {
                    Logger.getLogger(CommandLine.class.getName())
                            .log(Level.INFO, "After start Com");
                }
            }

            @Override
            public void actionsPerLine(String line) {
                if (grep != null) {
                    System.out.println(line);
                }
            }

            @Override
            public void loopEnd() {
                if (Run.VERBOSE) {
                    Logger.getLogger(CommandLine.class.getName())
                            .log(Level.INFO, "Loop ended");
                }
            }

            @Override
            public void exceptions(Exception ex) {
                Logger.getLogger(CommandLine.class.getName())
                        .log(Level.SEVERE, "Check the trace", ex);
            }
 
            @Override
            public void cleanUp() {
                if (Run.VERBOSE) {
                    Logger.getLogger(CommandLine.class.getName())
                            .log(Level.INFO, "Running cleanUp");
                }
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
                /**
                 * If grep mode is active Listener option will be silenced.
                 */
                if (grep != null) {
                    return new EntryListener(m) {
                        @Override
                        public void initialize() {
                        }

                        @Override
                        public void onTransition(TransitionTarget from, TransitionTarget to, Transition transition, String array, String timeStamp, String logLine) {
                        }

                        @Override
                        public void onEntry(TransitionTarget tt) {
                        }

                        @Override
                        public void onExit(TransitionTarget tt) {
                        }
                    };
                }
                try {
                    Constructor c = cl.getConstructor(Manager.class);
                    EntryListener l = (EntryListener) c.newInstance(m);
                    return l;
                } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException ex) {
                    Logger.getLogger(CommandLine.class.getName())
                            .log(Level.SEVERE, null, ex);
                }

                return new DefaultEntryListener(m);
            }
        });
    }

}
