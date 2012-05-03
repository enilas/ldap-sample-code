/*
 * Copyright 2008-2011 UnboundID Corp. All Rights Reserved.
 */
/*
 * Copyright (C) 2008-2011 UnboundID Corp. This program is free
 * software; you can redistribute it and/or modify it under the terms of
 * the GNU General Public License (GPLv2 only) or the terms of the GNU
 * Lesser General Public License (LGPLv2.1 only) as published by the
 * Free Software Foundation. This program is distributed in the hope
 * that it will be useful,but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR
 * PURPOSE. See the GNU General Public License for more details. You
 * should have received a copy of the GNU General Public License along
 * with this program; if not,see <http://www.gnu.org/licenses>.
 */
package samplecode;

import com.unboundid.ldap.sdk.*;
import com.unboundid.util.*;
import com.unboundid.util.args.*;
import java.io.*;
import java.util.*;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.Logger;
import samplecode.listener.*;
import samplecode.password.*;
import samplecode.tools.*;

/**
 * Demonstrates the use of the {@code PasswordModifyExtendedRequest} by changing
 * the existing password specified by the {@code --bindPassword} command line
 * argument to the password specified by the {@code --newPassword} command line
 * argument or if {@code --newPassword} is not specified,has the server
 * generate a new password (which is returned in the extended response).
 * <p>
 * Sample invocation:<blockquote>
 * 
 * <pre>
 * java -cp CLASSPATH \
 *  samplecode.PasswordModifyExtendedOperationDemo \
 *  --hostname hostname \
 *  --port port \
 *  --bindDn DN \
 *  --bindPassword existingPassword \
 *  --newPassword newPassword
 * </pre>
 * 
 * </blockquote> Alternatively,to have the server generate a
 * password:<blockquote>
 * 
 * <pre>
 * java -cp CLASSPATH \
 *  samplecode.PasswordModifyExtendedOperationDemo \
 *  --hostname hostname \
 *  --port port \
 *  --bindDn DN \
 *  --bindPassword existingPassword
 * </pre>
 * 
 * </blockquote>
 */
@Author("terry.gardner@unboundid.com")
@Since("12-Nov-2011")
@CodeVersion("1.26")
public final class PasswordModifyExtendedOperationDemo extends AbstractTool
  implements ObservedByLdapExceptionListener,LdapExceptionListener

{
  /**
   * {@inheritDoc}
   */
  @Override
  public String getToolDescription() {
    return PasswordModifyExtendedOperationDemo.TOOL_DESCRIPTION;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getToolName() {
    return PasswordModifyExtendedOperationDemo.TOOL_NAME;
  }

  @Override
  public void ldapRequestFailed(final LdapExceptionEvent ldapExceptionEvent) {
    logger.log(Level.SEVERE,ldapExceptionEvent.getLdapException().getExceptionMessage());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public synchronized void removeLdapExceptionListener(
                                                       final LdapExceptionListener ldapExceptionListener) {
    if (ldapExceptionListener != null) {
      ldapExceptionListeners.remove(ldapExceptionListener);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Logger getLogger() {
    return Logger.getLogger(getClass().getName());
  }

  /**
   * The long identifier of the command line argument whose parameter is taken
   * as the value to which the user's password should be set. If the
   * {@code --newPassword} command line option is present,the parameter must
   * not be of zero-length. If the {@code --newPassword} command line option
   * is not present,then a {@code null} value for {@code newPassword} is
   * used,and the server is expected to generate a new password and include
   * it in the response.
   */
  public static final String ARG_NAME_NEW_PASSWORD;

  /**
   * The description of this tool. This value is used in help messages and for
   * other purposes.
   */
  public static final String TOOL_DESCRIPTION;

  /**
   * The name of this tool. This value is used in help messages and for other
   * purposes.
   */
  public static final String TOOL_NAME;

  /**
   * Provides a demonstration of the password modify extended operation.
   * <blockquote>
   * 
   * <pre>
   * Demonstrates the use of the PasswordModifyExtendedRequest by changing the
   * existing password specified by the --bindPassword command line argument to the
   * password specified by the --newPassword command line argument.
   * 
   * Usage:  PasswordModifyExtendedOperationDemo {options}
   * 
   * Available options include:
   * -h,--hostname {host}
   *     The IP address or resolvable name to use to connect to the directory
   *     server.  If this is not provided,then a default value of 'localhost' will
   *     be used.
   * -p,--port {port}
   *     The port to use to connect to the directory server.  If this is not
   *     provided,then a default value of 389 will be used.
   * -D,--bindDN {dn}
   *     The DN to use to bind to the directory server when performing simple
   *     authentication.
   * -w,--bindPassword {password}
   *     The password to use to bind to the directory server when performing simple
   *     authentication or a password-based SASL mechanism.
   * -j,--bindPasswordFile {path}
   *     The path to the file containing the password to use to bind to the
   *     directory server when performing simple authentication or a password-based
   *     SASL mechanism.
   * -Z,--useSSL
   *     Use SSL when communicating with the directory server.
   * -q,--useStartTLS
   *     Use StartTLS when communicating with the directory server.
   * -X,--trustAll
   *     Trust any certificate presented by the directory server.
   * -K,--keyStorePath {path}
   *     The path to the file to use as the key store for obtaining client
   *     certificates when communicating securely with the directory server.
   * -W,--keyStorePassword {password}
   *     The password to use to access the key store contents.
   * -u,--keyStorePasswordFile {path}
   *     The path to the file containing the password to use to access the key store
   *     contents.
   * --keyStoreFormat {format}
   *     The format (e.g.,jks,jceks,pkcs12,etc.) for the key store file.
   * -P,--trustStorePath {path}
   *     The path to the file to use as trust store when determining whether to
   *     trust a certificate presented by the directory server.
   * -T,--trustStorePassword {password}
   *     The password to use to access the trust store contents.
   * -U,--trustStorePasswordFile {path}
   *     The path to the file containing the password to use to access the trust
   *     store contents.
   * --trustStoreFormat {format}
   *     The format (e.g.,jks,jceks,pkcs12,etc.) for the trust store file.
   * -N,--certNickname {nickname}
   *     The nickname (alias) of the client certificate in the key store to present
   *     to the directory server for SSL client authentication.
   * -o,--saslOption {name=value}
   *     A name-value pair providing information to use when performing SASL
   *     authentication.
   * --abandonOnTimeout
   *     Whether the LDAP SDK should abandon an operation that has timed out.
   * --autoReconnect
   *     Whether the LDAP SDK should automatically reconnect when a connection is
   *     lost.
   * --connectTimeoutMillis {connect-timeout-millis-integer}
   *     Specifies the maximum length of time in milliseconds that a connection
   *     attempt should be allowed to continue before giving up. A value of zero
   *     indicates that there should be no connect timeout.
   * -b,--baseObject {distinguishedName}
   *     The base object used in the search request.
   * --maxResponseTimeMillis {max-response-time-in-milliseconds}
   *     The maximum length of time in milliseconds that an operation should be
   *     allowed to block,with 0 or less meaning no timeout is enforced. This
   *     command line argument is optional and has a default value of zero.
   * --reportInterval {positive-integer}
   *     The report interval in milliseconds.
   * --reportCount {positive-integer}
   *     Specifies the maximum number of reports. This command line argument is
   *     applicable to tools that display repeated reports. The time between
   *     repeated reports is specified by the --reportInterval command line
   *     argument.
   * -a,--attribute {attribute name or type}
   *     The attribute used in the search request or other request. This command
   *     line argument is not required,and can be specified multiple times. If this
   *     command line argument is not specified,the value '*' is used.
   * -f,--filter {filter}
   *     The search filter used in the search request.
   * -i,--initialConnections {positiveInteger}
   *     The number of initial connections to establish to directory server when
   *     creating the connection pool.
   * -m,--maxConnections {positiveInteger}
   *     The maximum number of connections to establish to directory server when
   *     creating the connection pool.
   * -s,--scope {searchScope}
   *     The scope of the search request; allowed values are BASE,ONE,and SUB
   * --sizeLimit {positiveInteger}
   *     The client-request maximum number of results which are returned to the
   *     client. If the number of entries which match the search parameter is
   *     greater than the client-requested size limit or the server-imposed size
   *     limit a SIZE_LIMIT_EXCEEDED code is returned in the result code in the
   *     search response.
   * --timeLimit {positiveInteger}
   *     The client-request maximum time to search used by the server. If the time
   *     of the search is greater than the client-requested time limit or the
   *     server-imposed time limit a TIME_LIMIT_EXCEEDED code is returned in the
   *     result code in the search response.
   * --pageSize {positiveInteger}
   *     The search page size
   * -n,--newPassword {new-password}
   *     The parameter of this argument is used as the new password. If this command
   *     line argument is not present the server must generate a new password and
   *     return the new password in the rspponse.
   * -H,-?,--help
   *     Display usage information for this program.
   * </pre>
   * 
   * </blockquote>
   * 
   * @param args
   *            The command-line arguments less the JVM specific arguments.
   */
  public static void main(final String... args) {
    final PrintStream outStream = System.out;
    final PrintStream errStream = System.err;
    final PasswordModifyExtendedOperationDemo passwordModifyExtendedOperationDemo =
      new PasswordModifyExtendedOperationDemo(outStream,errStream);
    passwordModifyExtendedOperationDemo
      .addLdapExceptionListener(passwordModifyExtendedOperationDemo);
    final ResultCode resultCode =
      passwordModifyExtendedOperationDemo.runTool(args);
    final ToolCompletedProcessing completedProcessing =
      new BasicToolCompletedProcessing(
                                       passwordModifyExtendedOperationDemo,resultCode);
    completedProcessing.displayMessage(outStream,errStream);
  }

  static {
    ARG_NAME_NEW_PASSWORD = "newPassword";
    TOOL_NAME = "PasswordModifyExtendedOperationDemo";
    TOOL_DESCRIPTION =
      "Demonstrates the use of the PasswordModifyExtendedRequest "
      + "by changing the existing password specified by the "
      + "--bindPassword command line argument to the password "
      + "specified by the --newPassword command line argument.";
  }

  /**
   * The command line options processor which handles basic command line
   * options.
   */
  private CommandLineOptions commandLineOptions;

  /**
   * Formats {@code LogRecord} objects in a standardized way.
   */
  private final Formatter formatter = new MinimalLogFormatter();

  /**
   * interested parties to {@code LdapExceptionEvents}
   */
  private volatile Vector<LdapExceptionListener> ldapExceptionListeners =
    new Vector<LdapExceptionListener>();

  /**
   * Prepares {@code PasswordModifyExtendedOperationDemo} for use by a client
   * - the {@code System.out} and {@code System.err OutputStreams} are used.
   */
  public PasswordModifyExtendedOperationDemo() {
    this(System.out,System.err);
  }

  /**
   * Prepares {@code PasswordModifyExtendedOperationDemo} for use by a client
   * - use the specified {@code outStream} and {@code errStream}.
   * 
   * @param outStream
   *            the stream to which non-error messages are transmitted.
   * @param errStream
   *            the stream to which error messages are transmitted.
   */
  public PasswordModifyExtendedOperationDemo(final OutputStream outStream,
                                             final OutputStream errStream) {
    super(outStream,errStream);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public synchronized void addLdapExceptionListener(
                                                    final LdapExceptionListener ldapExceptionListener) {
    if (ldapExceptionListener != null) {
      ldapExceptionListeners.add(ldapExceptionListener);
    }
  }

  /**
   * {@inheritDoc}
   * <p>
   * Installs the standard command line arguments processor and then adds the
   * {@code --newPassword} command line argument.
   */
  @Override
  public void addArguments(final ArgumentParser argumentParser)
    throws ArgumentException {
    Validator.ensureNotNull(argumentParser);

    /*
     * Create the command line options processor.
     */
    commandLineOptions =
      CommandLineOptions.newCommandLineOptions(argumentParser);

    /*
     * Create and add to the argumentParser the argument whose parameter is
     * the password to which the user's password will be set. This command
     * line option is not required and can be specified one time.
     */
    final Character shortIdentifier = Character.valueOf('n');
    final String longIdentifier =
      PasswordModifyExtendedOperationDemo.ARG_NAME_NEW_PASSWORD;
    final boolean isRequired = false;
    final int maxOccurrences = 1;
    final String valuePlaceholder = "{new-password}";
    final String description =
      "The parameter of this argument is used as the new password. "
      + "If this command line argument is not present "
      + "the server must generate a new password "
      + "and return the new password in the rspponse.";
    final StringArgument stringArgument =
      new StringArgument(shortIdentifier,longIdentifier,isRequired,
                         maxOccurrences,valuePlaceholder,description);
    argumentParser.addArgument(stringArgument);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ResultCode executeToolTasks() {
    introduction();

    /*
     * Retrieve the user-specified bind DN from the command line argument
     * processor. The {@code LDAPCommandLineTool} --bindDn command line
     * argument is optional,therefore,it is necessary to check before
     * proceeding.
     */
    final DN distinguishedName = commandLineOptions.getBindDn();
    final StringBuilder builder = new StringBuilder();
    builder.append("No bindDn was specified on the command line. ");
    builder.append(String.format(" %s requires a valid bindDn.",
                                 getToolName()));
    builder.append(" Use '--bindDn DN' to specify the bind DN or use '--help'.");
    Validator.ensureNotNullWithMessage(distinguishedName,
                                       builder.toString());

    LDAPConnection ldapConnection;
    try {
      ldapConnection = getConnection();
    } catch (final LDAPException ldapException) {
      fireLdapExceptionListener(null,ldapException);
      return ldapException.getResultCode();
    }

    /*
     * Create the object which provides password changing services.
     */
    final ChangePassword changer =
      ChangePassword.newChangePassword(ldapConnection);

    /*
     * Attempt to change the password using the
     * PasswordModifyExtended.Request
     */
    final String oldPassword = commandLineOptions.getBindPassword();
    final String newPassword =
      (String) commandLineOptions
      .get(PasswordModifyExtendedOperationDemo.ARG_NAME_NEW_PASSWORD);
    ResultCode resultCode;
    final int responseTimeMillis =
      commandLineOptions.getMaxResponseTimeMillis();
    try {
      changer.changePassword(distinguishedName,oldPassword,newPassword,
                             responseTimeMillis);
      resultCode = ResultCode.SUCCESS;
    } catch (final LDAPException ldapException) {
      fireLdapExceptionListener(ldapConnection,ldapException);
      resultCode = ldapException.getResultCode();
      builder.delete(0,builder.capacity());
      builder.append(getToolName());
      final String hostname = commandLineOptions.getHostname();
      final Integer port = Integer.valueOf(commandLineOptions.getPort());
      builder.append(String.format(
                                   " failed to change password at server ldap://%s:%d",
                                   hostname,port));
      builder.append(String.format("; result from server was %s.",
                                   resultCode));
      err(builder.toString());
    } catch (final SupportedFeatureException e) {
      resultCode = ResultCode.UNWILLING_TO_PERFORM;
    } catch (final PasswordModifyExtendedOperationFailedException passwordModifyExtendedOperationFailedException) {
      resultCode =
        passwordModifyExtendedOperationFailedException
        .getResultCode();
    }
    return resultCode;
  }

  /**
   * {@inheritDoc}
   */
  @SuppressWarnings("unchecked")
  @Override
  public void fireLdapExceptionListener(final LDAPConnection ldapConnection,
                                        final LDAPException ldapException) {
    Validator.ensureNotNull(ldapException);
    Vector<LdapExceptionListener> copy;
    synchronized (this) {
      copy =
        (Vector<LdapExceptionListener>) ldapExceptionListeners
        .clone();
    }
    if (copy.size() == 0) {
      return;
    }
    final LdapExceptionEvent ev =
      new LdapExceptionEvent(this,ldapConnection,ldapException);
    for (final LdapExceptionListener l : copy) {
      l.ldapRequestFailed(ev);
    }
  }

}