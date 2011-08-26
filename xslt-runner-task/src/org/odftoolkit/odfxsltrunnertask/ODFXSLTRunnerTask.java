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

package org.odftoolkit.odfxsltrunnertask;

// IMPORTANT! You need to compile this class against ant.jar.
// The easiest way to do this is to add ${ant.core.lib} to your project's classpath.
// For example, for a plain Java project with no other dependencies, set in project.properties:
// javac.classpath=${ant.core.lib}

import java.io.File;
import java.util.Iterator;
import java.util.Vector;
import org.odftoolkit.odfxsltrunner.Logger;
import org.odftoolkit.odfxsltrunner.ODFXSLTRunner;
import org.odftoolkit.odfxsltrunner.XSLTParameter;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

import org.apache.tools.ant.taskdefs.Mkdir;

/**
 * Ant Taks for applying XSLT style sheets to ODF packages.
 */
public class ODFXSLTRunnerTask extends Task {

    private File m_aStyleSheet = null;
    private File m_aInput = null;
    private File m_aInputFile = null;
    private File m_aOutput = null;
    private File m_aOutputFile = null;
    private String m_aPath = "content.xml";
    private String m_aProcessor = null;
    private Vector<XSLTParameter> m_aParams = null;
    boolean m_bForce = false;
    boolean m_bTemplate = false;
    
    /**
     * Set input package. The specified path within the package is the source of 
     * the transformation.
     * 
     * @param aIn Input package
     */
    public void setIn( File aIn )
    {
        m_aInput = aIn;
    }

    /**
     * Set input file. This (XML) file is used directly as source of the 
     * transformation.
     * 
     * @param aInFile Input File
     */
    public void setInFile( File aInFile )
    {
        m_aInputFile = aInFile;
    }

    /**
     * Set output package. The source of the transformation is stored in a 
     * package with this name.
     * 
     * @param aOut
     */
    public void setOut( File aOut )
    {
        m_aOutput = aOut;
    }

    /**
     * Set output file. The source of the transformation is stored under this
     * name as plain (XML) file.
     * 
     * @param aOutFile
     */
    public void setOutFile( File aOutFile )
    {
        m_aOutputFile = aOutFile;
    }

    /**
     * Set the style sheet to apply.
     * 
     * @param aStyle
     */
    public void setStyle( File aStyle )
    {
        m_aStyleSheet = aStyle;
    }

    /**
     * Sets template mode. In template mode, the output package must exist,
     * and the specified path within the output file is replaced with the result
     * of the transformation
     * 
     * @param bTemplate Template mode
     */
    public void setTemplate( boolean bTemplate )
    {
        m_bTemplate = bTemplate;
    }
    
    /**
     * Set force mode. In this mode, the style sheet is applied even if the
     * output file or package is newer than the source and the style sheet.
     * 
     * @param bForce
     */
    public void setForce( boolean bForce )
    {
        m_bForce = bForce;
    }

    /**
     * Set processor.
     *
     * @param bForce
     */
    public void setProcessor( String aProcessor )
    {
        m_aProcessor = aProcessor;
    }


    /**
     * Paramter class for nested <param> elements.
     */
    public static class Param implements XSLTParameter
    {
        private String m_aName = null;
        private String m_aValue = null;

        Param()
        {
        }

        /**
         * Set parameter name.
         * @param aName
         */
        public void setName( String aName )
        {
            m_aName = aName;
        }

        /**
         * Set parameter expression.
         * 
         * @param aValue
         */
        public void setExpression( String aValue )
        {
            m_aValue = aValue;
        }

        /**
         * Get paramter name.
         * 
         * @return paramter name
         */
        public String getName()
        {
            return m_aName;
        }

        /** 
         * Get paramter value.
         * 
         * @return paramter value.
         */
        public String getValue()
        {
            return m_aValue;
        }
    }
    
    /**
     * Create a new paramter for a nested <param> element.
     * 
     * @return new paramter.
     */
    public Param createParam()
    {
        Param aParam = new Param();
        if( m_aParams == null )
            m_aParams = new Vector<XSLTParameter>();
        m_aParams.add( aParam );
        return aParam;
    }
    

    public @Override void execute() throws BuildException {
    
        if( m_aStyleSheet == null )
            throw new BuildException( "style attribute must be set", getLocation() );
        if( !m_aStyleSheet.exists() )
            throw new BuildException( "style sheet " + m_aStyleSheet.getAbsolutePath() + " does not exist", getLocation() );

        int m_aInputMode = ODFXSLTRunner.INPUT_MODE_PACKAGE;
        if( m_aInput == null && m_aInputFile == null )
            throw new BuildException( "in or infile attribute must be set", getLocation() );
        else if( m_aInput != null && m_aInputFile != null )
            throw new BuildException( "in and infile attributes must not be set simultaneously", getLocation() );
        if( m_aInputFile != null )
        {
            m_aInput = m_aInputFile;
            m_aInputMode = ODFXSLTRunner.INPUT_MODE_FILE;
        }
        if( !m_aInput.exists() )
            throw new BuildException( "input file " + m_aInput.getAbsolutePath() + " does not exist", getLocation() );
        
        int m_aOutputMode = ODFXSLTRunner.OUTPUT_MODE_COPY_INPUT_PACKAGE;
        if( m_aOutput == null && m_aOutputFile == null )
            throw new BuildException( "out or outfile attribute must be set", getLocation() );
        if( m_aOutput != null && m_aOutputFile != null )
            throw new BuildException( "out and outfile attribute must not be set simultaneously", getLocation() );
        if( m_bTemplate && m_aOutput == null )
            throw new BuildException( "out attribute must be set if template attribute is set", getLocation() );

        if( m_aOutputFile != null )
        {
            m_aOutput = m_aOutputFile;
            m_aOutputMode = ODFXSLTRunner.OUTPUT_MODE_FILE;
        }
        else if( m_bTemplate )
        {
            m_aOutputMode = ODFXSLTRunner.OUTPUT_MODE_TEMPLATE_PACKAGE;
            if( !m_aOutput.exists() )
                throw new BuildException( "output file " + m_aStyleSheet.getAbsolutePath() + " does not exist", getLocation() );
        }
        
        if( m_aParams != null )
        {
            Iterator<XSLTParameter> aIter = m_aParams.iterator();
            while( aIter.hasNext() )
            {
                XSLTParameter aParam = aIter.next();
                if( aParam.getName() == null )
                    throw new BuildException( "parameter name attribute must be set", getLocation() );

                if( aParam.getValue() == null )
                    throw new BuildException( "parameter expression attribute must be set", getLocation() );
            }
        }
        
        if( !m_bForce && m_aOutput.exists() && 
            m_aOutput.lastModified() > m_aInput.lastModified() &&
            m_aOutput.lastModified() > m_aStyleSheet.lastModified())
            return;

        if( !m_aOutput.exists() )
        {
            File aParentFile = m_aOutput.getParentFile();
            if( aParentFile != null )
            {
                Mkdir aMKDir = (Mkdir)getProject().createTask("mkdir");
                aMKDir.setDir(aParentFile);
                aMKDir.init();
                aMKDir.setLocation(getLocation());
                aMKDir.execute();
            }
        }
        
        boolean bError = false;
        try
        {
            ODFXSLTRunner aRunner = new ODFXSLTRunner();
            Logger aLogger = new AntLogger( getProject() );
            bError = aRunner.runXSLT( m_aStyleSheet, m_aParams, m_aInput, m_aInputMode, m_aOutput, m_aOutputMode, m_aPath, m_aProcessor, aLogger  );
        }
        catch( Exception e )
        {
            throw new BuildException( e, getLocation() );
        }
        if( bError )
            throw new BuildException( "transformation failed", getLocation() );
    }

}