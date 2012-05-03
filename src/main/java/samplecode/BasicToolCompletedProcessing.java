/*
 * Copyright 2008-2011 UnboundID Corp. All Rights Reserved.
 */
/*
 * Copyright (C) 2008-2011 UnboundID Corp. This program is free
 * software; you can redistribute it and/or modify it under the terms of
 * the GNU General Public License (GPLv2 only) or the terms of the GNU
 * Lesser General Public License (LGPLv2.1 only) as published by the
 * Free Software Foundation. This program is distributed in the hope
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR
 * PURPOSE. See the GNU General Public License for more details. You
 * should have received a copy of the GNU General Public License along
 * with this program; if not, see <http://www.gnu.org/licenses>.
 */
package samplecode;


import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.util.CommandLineTool;
import com.unboundid.util.LDAPCommandLineTool;
import com.unboundid.util.MinimalLogFormatter;
import com.unboundid.util.Validator;


import java.io.PrintStream;
import java.util.logging.Level;


import samplecode.annotation.Author;
import samplecode.annotation.CodeVersion;
import samplecode.annotation.Since;


/**
 * Provides a generic message for logging when a {@link CommandLineTool}
 * object has completed processing.
 * {@link CommandLineTool#doToolProcessing()} does not throw an
 * exception but provides a result code.
 */
@Author("terry.gardner@unboundid.com")
@Since("Dec 24, 2011")
@CodeVersion("1.0")
public class BasicToolCompletedProcessing
        implements ToolCompletedProcessing
{

  /**
   * {@inheritDoc}
   * <p>
   * precondition: result code, tool, outStream and errStream cannot be
   * {@code null}.
   */
  @Override
  public void displayMessage(final PrintStream outStream,final PrintStream errStream)
  {
    Validator.ensureNotNull(outStream,errStream);
    final LdapLogRecord ldapLogRecord =
            new BasicLdapLogRecord(String.format(
                    "%s has completed processing, the result code was: %s",tool.getToolName(),
                    resultCode));
    if(resultCode.equals(ResultCode.SUCCESS))
    {
      outStream
              .println(new MinimalLogFormatter().format(ldapLogRecord.getLogRecord(Level.INFO)));
    }
    else
    {
      errStream
              .println(new MinimalLogFormatter().format(ldapLogRecord.getLogRecord(Level.INFO)));
    }
  }



  /**
   * Creates a {@code BasicToolCompletedProcessing} with default state.
   * 
   * @param tool
   *          the {@code CommandLineTool} that has completed processing.
   *          {@code tool} is not permitted to be {@code null}.
   * @param resultCode
   *          the result code returned from the tool. @code resultCode}
   *          is not permitted to be {@code null}.
   */
  public BasicToolCompletedProcessing(
          final LDAPCommandLineTool tool,final ResultCode resultCode)
  {
    Validator.ensureNotNull(tool,resultCode);
    this.tool = tool;
    this.resultCode = resultCode;
  }



  // the result code generated by the processing of CommandLineTool
  private final ResultCode resultCode;



  // The tool the completed processing.
  private final LDAPCommandLineTool tool;

}
