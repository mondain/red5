require 'java'

module RedFive
	include_class 'org.red5.server.api.IConnection'
	include_class 'org.red5.server.api.IScope'
	include_class 'org.red5.server.api.stream.IPlayItem'
	include_class 'org.red5.server.api.stream.IServerStream'
	include_class 'org.red5.server.api.stream.IStreamCapableConnection'
	include_class 'org.red5.server.api.stream.support.SimpleBandwidthConfigure'
	include_class 'org.red5.server.api.stream.support.SimplePlayItem'
	include_class 'org.red5.server.api.stream.support.StreamUtils'
end

include_class 'org.red5.server.adapter.ApplicationAdapter'

#
# application.js - a translation into JavaScript of the olfa demo application, a red5 example.
#
# @author Paul Gregoire
#
class Application < ApplicationAdapter

	attr_reader :appScope, :serverStream
	attr_writer :appScope, :serverStream
	 
	def initialize
		super
		puts "Initializing ruby application"
	end

	def appStart(app)
		puts "Ruby appStart"
		@appScope = app
		return true
	end

	def appConnect(conn, params) 
		puts "Ruby appConnect"
		super.measureBandwidth(conn)
		if conn.kind_of?(RedFive::IStreamCapableConnection)
			streamConn = conn
			sbc = RedFive::SimpleBandwidthConfigure.new
			sbc.setMaxBurst(8*1024*1024)
			sbc.setBurst(8*1024*1024)
			sbc.setOverallBandwidth(2*1024*1024)
			streamConn.setBandwidthConfigure(sbc)
		end
		return super.appConnect(conn, params)
	end

	def appDisconnect(conn) 
		puts "Ruby appDisconnect"
		if appScope == conn.getScope && @serverStream != nil 
			@serverStream.close
		end
		super.appDisconnect(conn)
	end

	def toString
		return "Ruby toString"
	end

end