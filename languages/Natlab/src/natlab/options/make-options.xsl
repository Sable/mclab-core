<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:output method="text" indent="no"/>
  <xsl:strip-space elements="*"/>

<!--*************************************************************************-->
<!--* ROOT TEMPLATE *********************************************************-->
<!--*************************************************************************-->

  <xsl:template match="/options">
// =========================================================================== //
//                                                                             //
// Copyright 2008-2012 Andrew Casey, Jun Li, Jesse Doherty,                    //
//   Maxime Chevalier-Boisvert, Toheed Aslam, Anton Dubrau, Nurudeen Lameed,   //
//   Amina Aslam, Rahul Garg, Soroush Radpour, Olivier Savary Belanger,        //
//   Laurie Hendren, Clark Verbrugge and McGill University.                    //
//                                                                             //
//   Licensed under the Apache License, Version 2.0 (the "License");           //
//   you may not use this file except in compliance with the License.          //
//   You may obtain a copy of the License at                                   //
//                                                                             //
//       http://www.apache.org/licenses/LICENSE-2.0                            //
//                                                                             //
//   Unless required by applicable law or agreed to in writing, software       //
//   distributed under the License is distributed on an "AS IS" BASIS,         //
//   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  //
//   See the License for the specific language governing permissions and       //
//   limitations under the License.                                            //
//                                                                             //
// =========================================================================== //
 
/* THIS FILE IS AUTO-GENERATED FROM natlab_options.xml. DO NOT MODIFY. */

package natlab.options;
import java.util.*;

/** Natlab command-line options parser.
 */
public class Options extends OptionsBase {
    public Options() { }

<xsl:apply-templates mode="constants" select="/options/section"/>

    public boolean parse( String[] argv ) {
        for( int i = argv.length; i > 0; i-- ) {
            pushOptions( argv[i-1] );
        }
        while( hasMoreOptions() ) {
            String option = nextOption();
            if( option.charAt(0) != '-' ) {
                files.add( option );
                continue;
            }
            while( option.charAt(0) == '-' ) {
                option = option.substring(1);
            }
            if( false );
<xsl:apply-templates mode="parse" select="/options/section"/>
            else {
                System.out.println( "Invalid option -"+option );
                return false;
            }
        }
    
        return true;
    }

<xsl:apply-templates mode="vars" select="/options/section"/>

    public String getUsage() {
        return ""
<xsl:apply-templates mode="usage" select="/options/section"/>;
    }

}
  </xsl:template>

<!--*************************************************************************-->
<!--* PARSE TEMPLATES *******************************************************-->
<!--*************************************************************************-->

  <xsl:template mode="parse" match="section">
      <xsl:apply-templates mode="parse" select="boolopt|multiopt|listopt|stropt|macroopt"/>
  </xsl:template>

<!--* BOOLEAN_OPTION *******************************************************-->
  <xsl:template mode="parse" match="boolopt">
            else if( false <xsl:text/>
    <xsl:for-each select="alias">
            || option.equals( "<xsl:value-of select="."/>" )<xsl:text/>
    </xsl:for-each>
            )
                <xsl:value-of select="translate(alias[last()],'-. ','___')"/> = true;
  </xsl:template>

<!--* MULTI_OPTION *******************************************************-->
  <xsl:template mode="parse" match="multiopt">
            else if( false<xsl:text/>
    <xsl:for-each select="alias">
            || option.equals( "<xsl:value-of select="."/>" )<xsl:text/>
    </xsl:for-each>
            ) {
                if( !hasMoreOptions() ) {
                    System.out.println( "No value given for option -"+option );
                    return false;
                }
                String value = nextOption();
    <xsl:variable name="name" select="translate(alias[last()],'-. ','___')"/>
                if( false );
    <xsl:for-each select="value">
                else if( false<xsl:text/>
      <xsl:for-each select="alias">
                || value.equals( "<xsl:value-of select="."/>" )<xsl:text/>
      </xsl:for-each>
                ) {
                    if( <xsl:copy-of select="$name"/> != 0
                    &#38;&#38; <xsl:copy-of select="$name"/> != <xsl:copy-of select="$name"/>_<xsl:value-of select="translate(alias[last()],'-. ','___')"/> ) {
                        System.out.println( "Multiple values given for option "+option );
                        return false;
                    }
                    <xsl:copy-of select="$name"/> = <xsl:copy-of select="$name"/>_<xsl:value-of select="translate(alias[last()],'-. ','___')"/>;
                }
    </xsl:for-each>
                else {
                    System.out.println( "Invalid value "+value+" given for option -"+option );
                    return false;
                }
           }
  </xsl:template>

<!--* PATH_OPTION *******************************************************-->
  <xsl:template mode="parse" match="listopt">
            else if( false<xsl:text/>
    <xsl:for-each select="alias">
            || option.equals( "<xsl:value-of select="."/>" )<xsl:text/>
    </xsl:for-each>
            ) {
                if( !hasMoreOptions() ) {
                    System.out.println( "No value given for option -"+option );
                    return false;
                }
                String value = nextOption();
    <xsl:variable name="name" select="translate(alias[last()],'-. ','___')"/>
                if( <xsl:copy-of select="$name"/> == null )
                    <xsl:copy-of select="$name"/> = new LinkedList();

                <xsl:copy-of select="$name"/>.add( value );
            }
  </xsl:template>

<!--* STRING_OPTION *******************************************************-->
  <xsl:template mode="parse" match="stropt">
            else if( false<xsl:text/>
    <xsl:for-each select="alias">
            || option.equals( "<xsl:value-of select="."/>" )<xsl:text/>
    </xsl:for-each>
            ) {
                if( !hasMoreOptions() ) {
                    System.out.println( "No value given for option -"+option );
                    return false;
                }
                String value = nextOption();
    <xsl:variable name="name" select="translate(alias[last()],'-. ','___')"/>
                if( <xsl:copy-of select="$name"/>.length() == 0 )
                    <xsl:copy-of select="$name"/> = value;
                else {
                    System.out.println( "Duplicate values "+<xsl:copy-of select="$name"/>+" and "+value+" for option -"+option );
                    return false;
                }
            }
  </xsl:template>

<!--* MACRO_OPTION *******************************************************-->
  <xsl:template mode="parse" match="macroopt">
            else if( false<xsl:text/>
    <xsl:for-each select="alias">
            || option.equals( "<xsl:value-of select="."/>" )<xsl:text/>
    </xsl:for-each>
            ) {
                <xsl:for-each select="expansion">
                <xsl:sort select="position()" data-type="number" order="descending"/><xsl:text/>
                pushOptions( "<xsl:value-of select="."/>" );<xsl:text/>
                </xsl:for-each>
            }
  </xsl:template>

<!--*************************************************************************-->
<!--* VARS TEMPLATES ********************************************************-->
<!--*************************************************************************-->

  <xsl:template mode="vars" match="section">
      <xsl:apply-templates mode="vars" select="boolopt|multiopt|listopt|stropt|macroopt"/>
  </xsl:template>

<!--* BOOLEAN_OPTION *******************************************************-->
  <xsl:template mode="vars" match="boolopt">
    public boolean <xsl:value-of select="translate(alias[last()],'-. ','___')"/>() { return <xsl:value-of select="translate(alias[last()],'-. ','___')"/>; }
    private boolean <xsl:value-of select="translate(alias[last()],'-. ','___')"/> = false;<xsl:text/>
    public void set_<xsl:value-of select="translate(alias[last()],'-. ','___')"/>( boolean setting ) { <xsl:value-of select="translate(alias[last()],'-. ','___')"/> = setting; }
  </xsl:template>

<!--* MULTI_OPTION *******************************************************-->
  <xsl:template mode="vars" match="multiopt">
    public int <xsl:value-of select="translate(alias[last()],'-. ','___')"/>() {<xsl:text/>
    <xsl:variable name="name" select="translate(alias[last()],'-. ','___')"/><xsl:text/>
    <xsl:for-each select="value">
      <xsl:if test="default"><xsl:text/>
        if( <xsl:value-of select="$name"/> == 0 ) return <xsl:value-of select="$name"/>_<xsl:value-of select="translate(alias[last()],'-. ','___')"/>;<xsl:text/>
      </xsl:if>
    </xsl:for-each>
        return <xsl:value-of select="translate(alias[last()],'-. ','___')"/>; 
    }
    public void set_<xsl:value-of select="translate(alias[last()],'-. ','___')"/>( int setting ) { <xsl:value-of select="translate(alias[last()],'-. ','___')"/> = setting; }
    private int <xsl:value-of select="translate(alias[last()],'-. ','___')"/> = 0;<xsl:text/>
  </xsl:template>

<!--* PATH_OPTION *******************************************************-->
  <xsl:template mode="vars" match="listopt">
    public List <xsl:value-of select="translate(alias[last()],'-. ','___')"/>() { 
        if( <xsl:value-of select="translate(alias[last()],'-. ','___')"/> == null )
            return java.util.Collections.EMPTY_LIST;
        else
            return <xsl:value-of select="translate(alias[last()],'-. ','___')"/>;
    }
    public void set_<xsl:value-of select="translate(alias[last()],'-. ','___')"/>( List setting ) { <xsl:value-of select="translate(alias[last()],'-. ','___')"/> = setting; }
    private List <xsl:value-of select="translate(alias[last()],'-. ','___')"/> = null;<xsl:text/>
  </xsl:template>

<!--* STRING_OPTION *******************************************************-->
  <xsl:template mode="vars" match="stropt">
    public String <xsl:value-of select="translate(alias[last()],'-. ','___')"/>() { return <xsl:value-of select="translate(alias[last()],'-. ','___')"/>; }
    public void set_<xsl:value-of select="translate(alias[last()],'-. ','___')"/>( String setting ) { <xsl:value-of select="translate(alias[last()],'-. ','___')"/> = setting; }
    private String <xsl:value-of select="translate(alias[last()],'-. ','___')"/> = "";<xsl:text/>
  </xsl:template>

<!--* MACRO_OPTION *******************************************************-->
  <xsl:template mode="vars" match="macroopt">
  </xsl:template>

<!--*************************************************************************-->
<!--* CONSTANTS TEMPLATES ***************************************************-->
<!--*************************************************************************-->

  <xsl:template mode="constants" match="section">
      <xsl:apply-templates mode="constants" select="multiopt"/>
  </xsl:template>

<!--* MULTI_OPTION *******************************************************-->
  <xsl:template mode="constants" match="multiopt">
    <xsl:variable name="name" select="translate(alias[last()],'-. ','___')"/>
    <xsl:for-each select="value">
      <xsl:variable name="number"><xsl:number/></xsl:variable>
      <xsl:for-each select="alias">
    public static final int <xsl:copy-of select="$name"/>_<xsl:value-of select="translate(.,'-. ','___')"/> = <xsl:value-of select="$number"/>;<xsl:text/>
      </xsl:for-each>
    </xsl:for-each>
  </xsl:template>

<!--*************************************************************************-->
<!--* USAGE TEMPLATES *******************************************************-->
<!--*************************************************************************-->

  <xsl:template mode="usage" match="section">
+"\n<xsl:value-of select="name"/>:\n"
      <xsl:apply-templates mode="usage" select="boolopt|multiopt|listopt|stropt|macroopt"/>
  </xsl:template>

<!--* BOOLEAN_OPTION *******************************************************-->
  <xsl:template mode="usage" match="boolopt">
+padOpt("<xsl:for-each select="alias"> -<xsl:value-of select="."/></xsl:for-each>", "<xsl:apply-templates select="short_desc"/>" )<xsl:text/>
  </xsl:template>

<!--* MULTI_OPTION *******************************************************-->
  <xsl:template mode="usage" match="multiopt">
+padOpt("<xsl:for-each select="alias"> -<xsl:value-of select="."/><xsl:text> </xsl:text><xsl:call-template name="arg-label"/></xsl:for-each>", "<xsl:apply-templates select="short_desc"/>" )<xsl:text/>
    <xsl:for-each select="value">
+padVal("<xsl:for-each select="alias"><xsl:value-of select="string(' ')"/><xsl:value-of select="."/></xsl:for-each><xsl:if test="default"> (default)</xsl:if>", "<xsl:value-of select="translate(short_desc,'&#10;',' ')"/>" )<xsl:text/>
      </xsl:for-each>
  </xsl:template>

<!--* PATH_OPTION *******************************************************-->
  <xsl:template mode="usage" match="listopt">
+padOpt("<xsl:for-each select="alias"> -<xsl:value-of select="."/><xsl:text> </xsl:text><xsl:call-template name="arg-label"/></xsl:for-each>", "<xsl:apply-templates select="short_desc"/>" )<xsl:text/>
  </xsl:template>

<!--* STRING_OPTION *******************************************************-->
  <xsl:template mode="usage" match="stropt">
+padOpt("<xsl:for-each select="alias"> -<xsl:value-of select="."/><xsl:text> </xsl:text><xsl:call-template name="arg-label"/></xsl:for-each>", "<xsl:apply-templates select="short_desc"/>" )<xsl:text/>
  </xsl:template>

<!--* MACRO_OPTION *******************************************************-->
  <xsl:template mode="usage" match="macroopt">
+padOpt("<xsl:for-each select="alias"> -<xsl:value-of select="."/></xsl:for-each>", "<xsl:apply-templates select="short_desc"/>" )<xsl:text/>
  </xsl:template>

  <xsl:template match="use_arg_label">
    <xsl:call-template name="arg-label"/>
  </xsl:template>

  <!-- Factored out so it can be used to print the argument labels in
       the option summary, e.g. "-src-prec format", 
       as well as argument labels in short_desc and long_desc. -->
  <xsl:template name="arg-label">
  <xsl:choose>
    <xsl:when test="ancestor::*/set_arg_label">
      <xsl:value-of select="translate(string(ancestor::*/set_arg_label),
                            'abcdefghijklmnopqrstuvwxyz',
                            'ABCDEFGHIJKLMNOPQRSTUVWXYZ')"/>
    </xsl:when>
    <xsl:otherwise>ARG</xsl:otherwise>
  </xsl:choose>
  </xsl:template>

  <xsl:template match="var">
  <xsl:value-of select="translate(string(),
                            'abcdefghijklmnopqrstuvwxyz',
                            'ABCDEFGHIJKLMNOPQRSTUVWXYZ')"/>
  </xsl:template>
</xsl:stylesheet>
