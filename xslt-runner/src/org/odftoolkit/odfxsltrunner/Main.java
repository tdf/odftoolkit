/************************************************************************
 *
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER
 * 
 * Copyright 2008 Sun Microsystems, Inc. All rights reserved.
 * 
 * Use is subject to license terms.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy
 * of the License at http://www.apache.org/licenses/LICENSE-2.0. You can also
 * obtain a copy of the License at http://odftoolkit.org/docs/license.txt
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * 
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 ************************************************************************/

package org.odftoolkit.odfxsltrunner;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

/**
 * This application applies an XSLT style sheet to an ODF file. To run this application from the command line without Ant, try:
 * java -jar "[path/]odfxsltrunner.jar".
 */
public class Main {

    /** Creates a new instance of Main */
    public Main() {
    }
    
    /**
     * @param aArgs the command line arguments
     */
    public static void main(String[] aArgs) {
        
        String aStyleSheetName = null;
        String aInputName = null;
        String aOutputName = null;
        String aPath = "content.xml";
        String aLogFileName = null;
        int aInputMode = ODFXSLTRunner.INPUT_MODE_PACKAGE;
        int aOutputMode = ODFXSLTRunner.OUTPUT_MODE_COPY_INPUT_PACKAGE;
        Vector<XSLTParameter> aParams = null;
        int nLogLevel = CommandLineLogger.ERROR;
        
        boolean bCommandLineValid = true;
        List<String> aArgList = Arrays.asList(aArgs);
        Iterator<String> aArgIter = aArgList.iterator();
        while( aArgIter.hasNext() && bCommandLineValid )
        {
            String aArg = aArgIter.next();
            if( aArg.equals("-i") )
            {
                aInputMode = ODFXSLTRunner.INPUT_MODE_FILE;
            }
            else if( aArg.equals("-l") )
            {
                if( aArgIter.hasNext() )
                    aLogFileName = aArgIter.next();
                else
                    bCommandLineValid = false;
            }
            else if( aArg.equals("-o") )
            {
                if( aOutputMode == ODFXSLTRunner.OUTPUT_MODE_COPY_INPUT_PACKAGE )
                    aOutputMode = ODFXSLTRunner.OUTPUT_MODE_FILE;
                else
                    bCommandLineValid = false;
            }
            else if( aArg.equals("-p") )
            {
                if( aArgIter.hasNext() )
                    aPath = aArgIter.next();
                else
                    bCommandLineValid = false;
            }
            else if( aArg.equals("-r") )
            {
                if( aOutputMode == ODFXSLTRunner.OUTPUT_MODE_COPY_INPUT_PACKAGE )
                    aOutputMode = ODFXSLTRunner.OUTPUT_MODE_REPLACE_INPUT_PACKAGE;
                else
                    bCommandLineValid = false;
            }
            else if( aArg.equals("-t") )
            {
                if( aOutputMode == ODFXSLTRunner.OUTPUT_MODE_COPY_INPUT_PACKAGE )
                    aOutputMode = ODFXSLTRunner.OUTPUT_MODE_TEMPLATE_PACKAGE;
                else
                    bCommandLineValid = false;
            }
            else if( aArg.equals("-v") )
            {
                nLogLevel = CommandLineLogger.INFO;
            }
            else if( aArg.startsWith("-") )
            {
                System.out.print(aArg);
                System.out.println(": unknown option, use '-' for help");
                bCommandLineValid = false;
                break;
            }
            else if( aStyleSheetName == null )
            {
                aStyleSheetName = aArg;
            }
            else if( aInputName == null )
            {
                aInputName = aArg;
            }
            else if( aOutputMode != ODFXSLTRunner.OUTPUT_MODE_REPLACE_INPUT_PACKAGE && aOutputName == null )
            {
                aOutputName = aArg;
            }
            else if( aArg.indexOf('=') != -1 )
            {
                if( aParams == null )
                    aParams = new Vector<XSLTParameter>();
                aParams.add(new XSLTCommandLineParameter(aArg) );
            }
            else
            {
                bCommandLineValid = false;
                break;
            }
        }
        
        if( aOutputMode == ODFXSLTRunner.OUTPUT_MODE_COPY_INPUT_PACKAGE &&
            aOutputName == null )
            aOutputMode = ODFXSLTRunner.OUTPUT_MODE_STDOUT;

        // check usage
        bCommandLineValid = aInputName != null && aStyleSheetName != null;
        bCommandLineValid = bCommandLineValid &&
                            !(aOutputMode == ODFXSLTRunner.OUTPUT_MODE_REPLACE_INPUT_PACKAGE || aOutputMode == ODFXSLTRunner.OUTPUT_MODE_STDOUT) == (aOutputName != null);
        
        // print help
        if( !bCommandLineValid )
        {
            printUsage();
            return;
        }
        
        PrintStream aLogStream = null;
        if( aLogFileName != null )
        {
            try
            {
                aLogStream = new PrintStream( aLogFileName );
            }
            catch( FileNotFoundException e )
            {
                System.err.println(e.getMessage());
            }
        }

        boolean bError = false;
        try
        {
            ODFXSLTRunner aRunner = new ODFXSLTRunner();
            Logger aLogger = new CommandLineLogger( aLogStream!=null ? aLogStream : System.err, nLogLevel );
            bError = aRunner.runXSLT( aStyleSheetName, aParams, aInputName, aInputMode, aOutputName, aOutputMode, aPath, aLogger  );
        }
        catch( Exception e )
        {
            System.err.println(e.getMessage());
        }
//        if( bError )
//            System.err.println("transformation failed");

        if( aLogStream != null )
            aLogStream.close();
    }
    
    private static void printUsage()
    {
        System.out.println( "usage: odfxsltrunner <style sheet> [-v] [-p <path in package>] [-l log file] [-t] <input package> <output package>");
        System.out.println( "       odfxsltrunner <style sheet> [-v] [-p <path in package>] [-l log file] -r <package>");
        System.out.println( "       odfxsltrunner <style sheet> [-v] [-p <path in package>] [-l log file] -o <input package> <output file>");
        System.out.println( "       odfxsltrunner <style sheet> [-v] [-p <path in package>] [-l log file] -i <input file> <output package>");
        System.out.println();
        System.out.println( "If no option except -p is specified, the transformation <style sheet> is applied to <path in package> of <input package>, <path in package> is replaced with the result of the transformation, and the full package is stored as <output package>." );
        System.out.println( "-p: Apply style sheet to <path in package>; default is content.xml" );
        System.out.println();
        System.out.println( "-r: Don't store result as a new package but replace input package <package>" );
        System.out.println( "-i: Input file is a plain XML file" );
        System.out.println( "-l: Write messages into <log file> instead of stderr" );
        System.out.println( "-o: Store result of tranformation as plain XML file in <output file>" );
        System.out.println( "-t: Store result of transformation in <path in package> of existing <output package>" );
        System.out.println( "-v: Verbose output" );
    }

}
