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

import org.apache.commons.cli.*;

/**
 * Main class, initialize the State Machines and Parsers and runs the
 * interpreter only.
 *
 *
 * @version 1.0
 * @author Javier Fuentes j.fuentes.m@icloud.com
 *
 * @see Manager
 */
public class Run {

    /**
     * Flag to run with a simulation input.
     */
    public static boolean SIMUL = false;
    /**
     * Main function.
     *
     * @param args
     */
    public static void main(String args[]) {

        
        /**
         * 
         * Options set. This is for command Line only.
         * If the args array length is zero, runs with Graphical Interface.
         */
        CommandLineParser parser = new DefaultParser();
        Options options = new Options();
        options.addOption("c", "cmd", false, "use a Command Line Interface");
        options.addOption("h", "help", false, "show this message");
        options.addOption( Option.builder().desc("use a given scxml file to parse a SM").argName("scxml file").numberOfArgs(1).longOpt("scxml").build());
        options.addOption( Option.builder().desc("use a given json file to translate log").argName("json file").numberOfArgs(1).longOpt("log_translate").build());
        options.addOption( Option.builder().desc("use a given query filter (json) file to search through ElasticSearch").argName("json file").numberOfArgs(1).longOpt("query_filter").build());
        options.addOption( Option.builder().desc("TimeStamp from").argName("time stamp from").numberOfArgs(1).longOpt("date_from").build());
        options.addOption( Option.builder().desc("TimeStamp to").argName("time stamp to").numberOfArgs(1).longOpt("date_to").build());
        options.addOption( Option.builder().desc("Query DSL").argName("query").numberOfArgs(1).longOpt("query").build());
        options.addOption( Option.builder().desc("Transition listener (Default: DefaultEntryistener)").argName("ListenerJarFile:ListenerClass").numberOfArgs(1).longOpt("listener").build());
        options.addOption( Option.builder().desc("Linereader implementation (Default: ElasticSearch)").argName("LineReaderJarFile:LineReaderClass").numberOfArgs(1).longOpt("linereader").build());
        options.addOption( Option.builder().desc("Elastic Search Server").argName("elk_server").numberOfArgs(1).longOpt("elk_server").build());



        /**
         * Parsing of options.
         */
        try {
            CommandLine line = parser.parse(options, args);
            if (line.hasOption("help")) {
                printUsage(options);
            }

            if (line.hasOption("cmd")) {
                String dfrom = null, dto = null, query = null;
                String scxml = null;
                String log_translate = null;
                String query_filter = null;
                String elk = null;
                String listener = null;
                String linereader = null;
                /**
                 * This vars are required.
                 */
                if (!line.hasOption("date_from") 
                        || !line.hasOption("date_to") 
                        || !line.hasOption("query")) {
                    printUsage(options, "date_from, date_to and query are required!!");
                } else {
                    dfrom = line.getOptionValue("date_from");
                    dto = line.getOptionValue("date_to");
                    query = line.getOptionValue("query");
                }
                /**
                 * This vars are optional.
                 */
                if (line.hasOption("scxml")) 
                    scxml = line.getOptionValue("scxml");
                if (line.hasOption("logtranslate"))
                    log_translate = line.getOptionValue("logtranslate");
                if (line.hasOption("queryfilter"))
                    query_filter = line.getOptionValue("queryfilter");
                if (line.hasOption("elk_server"))
                    elk = line.getOptionValue("elk_server");
                if (line.hasOption("listener"))
                    listener = line.getOptionValue("listener");
                if (line.hasOption("linereader"))
                    linereader = line.getOptionValue("linereader");
                
                new Manager().
                        launchCommandLine().
                        initialize(scxml,
                                log_translate,
                                query_filter,
                                elk,
                                listener,
                                linereader,
                                dfrom,
                                dto,
                                query);
            } else {
                new Manager().launchPanel().setVisible(true);
            }
        } catch (ParseException ex) {
            printUsage(options, ex.getMessage());
        }
    }

    /**
     * Show help in console.
     * @param options
     */
    public static void printUsage(Options options) {
        printUsage(options, null);
    }

    /**
     * Show help in console.
     * @param options
     * @param message 
     */
    public static void printUsage(Options options, String message) {
        HelpFormatter formatter = new HelpFormatter();
        if (message != null) {
            System.out.println(message);
        }
        formatter.printHelp("obssm", options);
        System.exit(0);
    }

}
