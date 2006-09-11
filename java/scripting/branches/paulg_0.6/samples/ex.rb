require 'java'
module JavaSwing
  include_package "javax.swing"
  include_package "java.awt.event"
end

frame = JavaSwing::JFrame.new("Simple Ruby App")
tree = JavaSwing::JTree.new()
frame.getContentPane().add(tree)
frame.setDefaultCloseOperation(JFrame::EXIT_ON_CLOSE)
frame.pack()
frame.setVisible(true)

---------------

# Ruby Java Bridge - style
#require 'rjb'
#Rjb::load
#module RedFive
#	Rjb::import('org.red5.server.api.IConnection')
#	Rjb::import('org.red5.server.api.IScope')
#	Rjb::import('org.red5.server.api.stream.IPlayItem')
#	Rjb::import('org.red5.server.api.stream.IServerStream')
#	Rjb::import('org.red5.server.api.stream.IStreamCapableConnection')
#	Rjb::import('org.red5.server.api.stream.support.SimpleBandwidthConfigure')
#	Rjb::import('org.red5.server.api.stream.support.SimplePlayItem')
#	Rjb::import('org.red5.server.api.stream.support.StreamUtils')
#end
#ApplicationAdapter = Rjb::import('org.red5.server.adapter.ApplicationAdapter')