package org.red5.demos.nsv;

import org.red5.logging.Red5LoggerFactory;
import org.red5.server.adapter.ApplicationAdapter;
import org.red5.server.api.IConnection;
import org.red5.server.api.IScope;
import org.red5.server.api.stream.IServerStream;
import org.slf4j.Logger;

/**
 * Simple application, used to provide access to Red5 core features.
 * 
 * @author Paul Gregoire
 */
public class Application extends ApplicationAdapter {

	private static Logger log = Red5LoggerFactory.getLogger(Application.class, "nsv");
	
	private IServerStream serverStream;
		
	/** {@inheritDoc} */
    @Override
	public boolean appStart(IScope app) {
	    super.appStart(app);
		log.info("nsv appStart");	
		
		//Get plugin
		//AuthPlugin authPlugin = (AuthPlugin) PluginRegistry.getPlugin("authPlugin");
		//Get the handler (application listener in this case) that you want
		//Red5AuthenticationHandler authHandler = authPlugin.getRed5AuthenticationHandler();
		//Add the handler as a listener for your app (in appStart or where-ever)
		//addListener(authHandler);
		
		return true;
	}

	/** {@inheritDoc} */
    @Override
	public boolean appConnect(IConnection conn, Object[] params) {
		log.info("nsv appConnect");
		
//		if (appScope == conn.getScope()) {
//			serverStream = StreamUtils.createServerStream(appScope, "live0");
//
//			SimplePlayItem item = new SimplePlayItem();
//			item.setStart(0);
//			item.setLength(10000);
//			item.setName("on2_flash8_w_audio");
//			serverStream.addItem(item);
//		
//			item = new SimplePlayItem();
//			item.setStart(20000);
//			item.setLength(10000);
//			item.setName("on2_flash8_w_audio");
//			serverStream.addItem(item);
//		
//			serverStream.start();
//		
//			try {
//				serverStream.saveAs("aaa", false);
//				serverStream.saveAs("bbb", false);
//			} catch (Exception e) {}
//		}
		 
		return super.appConnect(conn, params);
	}

	/** {@inheritDoc} */
    @Override
	public void appDisconnect(IConnection conn) {
		log.info("nsv appDisconnect");
		if (serverStream != null) {
			serverStream.close();
		}
		super.appDisconnect(conn);
	}
}
