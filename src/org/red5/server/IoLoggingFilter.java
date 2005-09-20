package org.red5.server;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.mina.common.ByteBuffer;
import org.apache.mina.common.IdleStatus;
import org.apache.mina.io.IoFilter;
import org.apache.mina.io.IoSession;
import org.apache.mina.util.SessionLog;

/**
 * Logs all MINA I/O events to {@link Logger}.
 * 
 * @author The Apache Directory Project (dev@directory.apache.org)
 * @author Trustin Lee (trustin@apache.org)
 * @version $Rev: 264677 $, $Date: 2005-08-30 11:44:35 +0900 $
 * 
 * @see SessionLog
 */
public class IoLoggingFilter implements IoFilter
{
    /**
     * Session attribute key: prefix string
     */
    public static final String PREFIX = SessionLog.PREFIX;

    /**
     * Session attribute key: {@link Logger}
     */
    public static final String LOGGER = SessionLog.LOGGER;
    
    private Level defaultLevel = Level.INFO;
    
    /**
     * Creates a new instance.
     */
    public IoLoggingFilter()
    {
    }
    
    /**
     * Returns the default level of log entry this filter logs. 
     */
    public Level getDefaultLevel() {
        return defaultLevel;
    }
    
    /**
     * Sets the default level of log entry this filter logs. 
     */
    public void setDefaultLevel(Level defaultLevel) {
        if( defaultLevel == null )
        {
            defaultLevel = Level.INFO;
        }
        this.defaultLevel = defaultLevel;
    }
    
    public void sessionOpened( NextFilter nextFilter, IoSession session )
    {
        SessionLog.log( defaultLevel, session, "OPENED" );
        nextFilter.sessionOpened( session );
    }

    public void sessionClosed( NextFilter nextFilter, IoSession session )
    {
        SessionLog.log( defaultLevel, session, "CLOSED" );
        nextFilter.sessionClosed( session );
    }

    public void sessionIdle( NextFilter nextFilter, IoSession session, IdleStatus status )
    {
        SessionLog.log( defaultLevel, session, "IDLE: " + status );
        nextFilter.sessionIdle( session, status );
    }

    public void exceptionCaught( NextFilter nextFilter, IoSession session, Throwable cause )
    {
        SessionLog.log( defaultLevel, session, "EXCEPTION:", cause );
        nextFilter.exceptionCaught( session, cause );
    }

    public void dataRead( NextFilter nextFilter, IoSession session, ByteBuffer buf)
    {
        SessionLog.log( defaultLevel, session, "READ: \n" + formatHexDump(buf.getHexDump()) );
        nextFilter.dataRead( session, buf );
    }

    public void dataWritten( NextFilter nextFilter, IoSession session, Object marker)
    {
        //SessionLog.log( defaultLevel, session, "WRITTEN: " + marker );
        nextFilter.dataWritten( session, marker );
    }

    public void filterWrite( NextFilter nextFilter, IoSession session, ByteBuffer buf, Object marker)
    {
        SessionLog.log( defaultLevel, session, "WRITE: \n" + marker + ", " + formatHexDump(buf.getHexDump()) );
        nextFilter.filterWrite( session, buf, marker );
    }
    
   public static String formatHexDump(String in){
	   int chunk = 60;
	   StringBuffer out = new StringBuffer();
	   int from = 0;
	   int to = 0;
	   int size = in.length();
	   while(from < size){
		   if(size < from + chunk)
			   to = size;
		   else to = from + chunk;
		   out.append(in.substring(from,to));
		   out.append("\n");
		   from = to;
	   }
	   return out.toString();
   }
    
}
