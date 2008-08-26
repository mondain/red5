package org.red5.webapps.bwcheck;


import org.red5.server.adapter.MultiThreadedApplicationAdapter;
import org.red5.server.api.IConnection;
import org.red5.server.api.IScope;
import org.red5.webapps.bwcheck.BandwidthDetection;

import org.red5.webapps.bwcheck.security.PlaybackSecurity;
import org.red5.webapps.bwcheck.security.PublishSecurity;
import org.red5.webapps.bwcheck.security.SharedObjectSecurity;

/**
 * Bandwidth Detection demo for the Red5 Server.
 *
 * @author The Red5 Project (red5@osflash.org)
 * @author Dan Rossi
 */
public class BWApplication extends MultiThreadedApplicationAdapter
{
	@Override
	public boolean appStart(IScope app) {
		registerStreamPublishSecurity(new PublishSecurity());
		registerSharedObjectSecurity(new SharedObjectSecurity());
		registerStreamPlaybackSecurity(new PlaybackSecurity());
		
		return super.appStart(app);
	}
	
	@Override
	public boolean appConnect(IConnection conn, Object[] params) {

		return super.appConnect(conn, params);
	}
}
